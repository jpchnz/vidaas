package uk.ac.ox.oucs.iam.security.utilities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

import sun.misc.BASE64Encoder;


/**
 * Signature Generator generates a signature for an input string. The signature
 * generation requires the private-key part of an asymmetric key pair.
 * 
 * @author Ants
 */
public class SignatureGenerator {
	private boolean useMessageExpiry = true;
	private long timeStampOfMessage;
	static boolean useSun = false;

	/**
	 * Create a signature generator, using the specified private key
	 * 
	 * @param privateKeyBytes
	 *            the private key to use in the signature generation
	 * @throws GeneralSecurityException
	 *             on any errors initialising the private key instance
	 */
	public SignatureGenerator(byte[] privateKeyBytes)
			throws GeneralSecurityException {
		super();
		initPrivateKey(privateKeyBytes);
	}

	/**
	 * Create a signature generator, using the private key in the given file
	 * 
	 * @param filePath
	 *            the path to a file containing the private key to use in the
	 *            signature generation
	 * @throws GeneralSecurityException
	 *             on any errors initialising the private key instance
	 * @throws IOException
	 *             on any errors while reading the private key from disk
	 */
	public SignatureGenerator(String filePath) throws IOException,
			GeneralSecurityException {
		FileInputStream input = new FileInputStream(filePath);
		byte[] keyBytes = new byte[input.available()];
		input.read(keyBytes);
		input.close();
		initPrivateKey(keyBytes);
	}

	// Create the private key instance, using the key bytes passed in.
	private void initPrivateKey(byte[] privateKeyBytes)
			throws GeneralSecurityException {
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("DSA");
		this.privateKey = keyFactory.generatePrivate(keySpec);
	}

	// The private key used to sign input data.
	private PrivateKey privateKey;

	/**
	 * Sign the given message using the SHA-1 with DSA algorithm.
	 * 
	 * @param message
	 *            the message to sign
	 * @return the signature bytes - note that these should be base 64 encoded
	 *         if these are required to be viewed by users
	 * @throws InvalidKeyException
	 *             if the private key provided is invalid
	 * @throws NoSuchAlgorithmException
	 *             if the signature algorithm is not available
	 * @throws SignatureException
	 *             on any errors during the signature generation
	 */
	public byte[] signMessage(String message) throws NoSuchAlgorithmException,
			SignatureException, InvalidKeyException {
		Signature sig = Signature.getInstance("SHA1withDSA");
		sig.initSign(privateKey);
		sig.update(message.getBytes());
		return sig.sign();
	}
	
	
	
	/**
	 * Create a digital signature of a message and then encode this as a 64 bit binary
	 * 
	 * @param messageToSend the message whose digital signature is required
	 * @return the digital signature of the message, including a formation time
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 */
	public VidaasSignature signMessageAndEncode(String messageToSend) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException  {
		byte[] signature;
		if (useMessageExpiry) {
			timeStampOfMessage = new Date().getTime();
			signature = signMessage(messageToSend + "_" + timeStampOfMessage);
		}
		else {
			signature = signMessage(messageToSend);
			timeStampOfMessage = 0;
		}
		String encodedSignature;

		if (useSun) {
			encodedSignature = new BASE64Encoder().encode(signature);
		}
		else {
			encodedSignature = javax.xml.bind.DatatypeConverter.printBase64Binary(signature);
		}
		
		return new VidaasSignature(encodedSignature, timeStampOfMessage);
	}

	/**
	 * 
	 * @param useMessageExpiry true if the message will get timed out, else false
	 */
	public void setUseMessageExpiry(boolean useMessageExpiry) {
		this.useMessageExpiry = useMessageExpiry;
	}
	
	
	
	
	private static void printCmdParameters() {
	    System.out.println( "\n\nUsage: sig-gen.bat [message]" +
	        "\n\nThis requires private-key.bin. The message is " +
	        "base-64 encoded and written to the file signature.base64\n\n");
	  }
	 
	  public static void main( String[] args) {
	    if ( args.length != 1) {
	      printCmdParameters();
	      System.exit( 1);
	    }
	    try {
	      SignatureGenerator sigGen = new SignatureGenerator( "private-key.bin");
	      byte[] message = sigGen.signMessage( args[0]);
	      BASE64Encoder encoder = new BASE64Encoder();
	      String signature = encoder.encode( message);
	      System.out.println("\nSignature = [" + signature + "]\n\n");
	      FileOutputStream output = new FileOutputStream( "signature.base64");
	      output.write( signature.getBytes());
	      output.close();
	    }
	    catch ( Exception e) {
	      e.printStackTrace();
	      System.exit( 1);
	    }
	  }
}