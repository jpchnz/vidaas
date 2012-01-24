package uk.ac.ox.oucs.iam.security.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

/**
 * Signature Verifier verifies a digital signature. The signature verification
 * requires the public-key part of an asymmetric key pair.
 * 
 * @author Ants
 */
public class SignatureVerifier {
	// The public key used to verify the digital signature.
	private PublicKey publicKey;
	private int maxMessageAgeSeconds = 60;
	private boolean messageTooOld = false;
	

	/**
	 * Create a signature generator, using the specified public key
	 * 
	 * @param publicKeyBytes
	 *            the public key to use in the signature generation
	 * @throws GeneralSecurityException
	 *             on any errors initialising the public key instance
	 */
	public SignatureVerifier(byte[] publicKeyBytes)
			throws GeneralSecurityException {
		super();
		initPublicKey(publicKeyBytes);
	}

	/**
	 * Create a signature verifier, using the public key in the given file
	 * 
	 * @param filePath
	 *            the path to a file containing the public key to use in the
	 *            signature generation
	 * @throws GeneralSecurityException
	 *             on any errors initialising the public key instance
	 * @throws IOException
	 *             on any errors while reading the public key from disk
	 */
	public SignatureVerifier(String filePath) throws IOException,
			GeneralSecurityException {
		FileInputStream input = new FileInputStream(filePath);
		byte[] keyBytes = new byte[input.available()];
		input.read(keyBytes);
		input.close();
		initPublicKey(keyBytes);
	}

	
	/**
	 * Create the public key instance, using the key bytes passed in
	 * 
	 * @param publicKeyBytes bytes to use for the public key
	 * @throws GeneralSecurityException
	 */
	private void initPublicKey(byte[] publicKeyBytes)
			throws GeneralSecurityException {
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("DSA");
		this.publicKey = keyFactory.generatePublic(keySpec);
	}

	/**
	 * Verify the signature for a message.
	 * 
	 * @param signatureBytes
	 *            the digital signature to verify
	 * @param message
	 *            the message that the signature is for
	 * @return true if the verification succeeds, otherwise false
	 * @throws java.security.GeneralSecurityException
	 *             on any security API errors during the verification
	 */
	public boolean verifyDigitalSignature(byte[] signatureBytes, String message)
			throws GeneralSecurityException {
		Signature sig = Signature.getInstance("SHA1withDSA");
		sig.initVerify(publicKey);
		sig.update(message.getBytes());
		return sig.verify(signatureBytes);
	}

	
	
	/**
	 * Decodes the signature before calling verifyDigitalSignature above
	 * 
	 * @param vidaasSignature the signature as a string (contains the signature but a creation stamp)
	 * @param message the message embedded in the digital signature
	 * @return true if the verification succeeds, otherwise false
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public boolean verifyDigitalSignature(VidaasSignature vidaasSignature, String message) throws GeneralSecurityException, IOException  {
		byte[] sigBytes;
		Date now = new Date();
		if (((now.getTime() - vidaasSignature.getTimestamp()) > (maxMessageAgeSeconds*1000))
				&& (vidaasSignature.getTimestamp() != 0)) {
			// Message is too old
			messageTooOld = true;
		} else {
			// Message has not yet expired
			messageTooOld = false;
			if (SignatureGenerator.useSun) {
				sigBytes = new sun.misc.BASE64Decoder().decodeBuffer(vidaasSignature.getSignature());
			}
			else {
				sigBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(vidaasSignature.getSignature());
			}
			return verifyDigitalSignature(sigBytes, vidaasSignature.getTimestampedMessage(message));
		}
		return false;
	}
	
		

	
	/**
	 * Set the maximum age that a digital certificate is allowed to be before considered
	 * too old
	 * 
	 * @param maxMessageAgeSeconds the new age of the certificate. Must be >0 (if the check is required)
	 * or zero to turn off checking.
	 */
	public void setMaxMessageAgeSeconds(int maxMessageAgeSeconds) {
		if (maxMessageAgeSeconds >= 0) {
			this.maxMessageAgeSeconds = maxMessageAgeSeconds;
		}
	}

	

	/**
	 * 
	 * @return true of the message has been calculated as being too old (i.e. was
	 * generated more than maxMessageAgeSeconds seconds previously), else false
	 */
	public boolean isMessageTooOld() {
		return messageTooOld;
	}
	
	
	
	
	private static void printCmdParameters() {
	    System.out.println( "\n\nUsage: sig-verify.bat [message]" +
	        "\n\nThis requires the files public-key.bin and " +
	        "signature.base64.\n\n");
	  }
	 
	  public static void main( String[] args) {
	    if ( args.length != 1) {
	      printCmdParameters();
	      System.exit( 1);
	    }
	    try {
	      // Read the signature in and decode it.
	      FileInputStream input = new FileInputStream( "signature.base64");
	      sun.misc.BASE64Decoder decoder = new  sun.misc.BASE64Decoder();
	      byte[] sigBytes = decoder.decodeBuffer( input);
	      input.close();
	 
	      // Verify the digital signature.
	      SignatureVerifier sigVerify = new SignatureVerifier( "public-key.bin");
	      if ( sigVerify.verifyDigitalSignature( sigBytes, args[0])) {
	        System.out.println( "\nSignature verification successful!\n");
	      }
	      else {
	        System.out.println( "\nSignature verification failed!\n");
	        System.exit( 1);
	      }
	    }
	    catch ( Exception e) {
	      e.printStackTrace();
	      System.exit( 1);
	    }
	  }
}