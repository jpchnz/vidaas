package uk.ac.ox.oucs.iam.interfaces.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;

import uk.ac.ox.oucs.iam.interfaces.security.utilities.VidaasSignature;
import uk.ac.ox.oucs.iam.interfaces.utilities.GeneralUtils;

/**
 * Signature Generator generates a signature for an input string. The signature
 * generation requires the private-key part of an asymmetric key pair.
 * 
 * @author Ants
 */
public class SignatureGenerator {
	private boolean useMessageExpiry = true;
	private long timeStampOfMessage;
	static boolean useApache = true;
	static boolean sanityCheck = true;
	static final String encodingInstance = "SHA1withDSA";
	private String keyFilePath;
	public final static String TIMESTAMP_POST_ATTRIBUTE = "ts";
	public final static String KEYFILE_POST_ATTRIBUTE = "key";
	public final static String SIGNATURE_POST_ATTRIBUTE = "sig";

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
	public SignatureGenerator(String filePath) throws IOException, GeneralSecurityException {
		FileInputStream input = new FileInputStream(filePath + ".priv");
		keyFilePath = filePath;
		byte[] keyBytes = new byte[input.available()];
		input.read(keyBytes);
		input.close();
		initPrivateKey(keyBytes);
	}


	// Create the private key instance, using the key bytes passed in.
	private void initPrivateKey(byte[] privateKeyBytes) throws GeneralSecurityException, IOException {
		boolean temp = true;
		if (temp) {
			this.privateKey = (PrivateKey) GeneralUtils.readObjectFromFile(keyFilePath + ".priv");
			return;
		}
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("DSA");
		this.privateKey = keyFactory.generatePrivate(keySpec);
	}

	// The private key used to sign input data.
	private PrivateKey privateKey;

	/**
	 * Sign the given message using the SHA-1 with DSA algorithm. Note that using this method will
	 * not provide timestamps meaning messages cannot be aged out. If this is required, then
	 * signMessageAndEncode should be used instead.
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
	private byte[] signMessage(String message) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
		Signature sig = Signature.getInstance(encodingInstance);
		sig.initSign(privateKey);
		sig.update(message.getBytes());
		return sig.sign();
	}

	/**
	 * Create a digital signature of a message and then encode this
	 * 
	 * @param messageToSend
	 *            the message whose digital signature is required
	 * @return the digital signature of the message, including a formation time
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException 
	 */
	public VidaasSignature signMessageAndEncode(String messageToSend) throws InvalidKeyException,
			NoSuchAlgorithmException, SignatureException, UnsupportedEncodingException {
		byte[] signature;
		if (useMessageExpiry) {
			timeStampOfMessage = new Date().getTime();
			signature = signMessage(messageToSend + "_" + timeStampOfMessage);
		} else {
			signature = signMessage(messageToSend);
			timeStampOfMessage = 0;
		}
		
		VidaasSignature vSig = encodeMessage(signature);
		vSig.setOriginalMessage(messageToSend);
		return vSig;
	}
		
		
	/**
	 * Encode a byte array so that it may be dealt with as a string (for http posting)
	 * @param signature the byte array to encode
	 * @return a VidaasSignature object that contains the encoded byte array
	 * @throws UnsupportedEncodingException
	 */
	private VidaasSignature encodeMessage(byte[] signature) throws UnsupportedEncodingException {
		String encodedSignature;
		
		encodedSignature = new String(Base64.encodeBase64(signature));

		return new VidaasSignature(URLEncoder.encode(encodedSignature, "UTF-8"), timeStampOfMessage);
	}

	/**
	 * 
	 * @param useMessageExpiry
	 *            true if the message will get timed out, else false
	 */
	public void setUseMessageExpiry(boolean useMessageExpiry) {
		this.useMessageExpiry = useMessageExpiry;
	}

	private static void printCmdParameters() {
		System.out.println("\n\nUsage: sig-gen.bat [message]" + "\n\nThis requires /tmp/key.priv. The message is "
				+ "base-64 encoded and written to the file signature.base64\n\n");
	}

	public static boolean originalCode = false;
	public static void main(String[] args) {
		if (args.length != 1) {
			printCmdParameters();
			System.exit(1);
		}
		try {
			if (originalCode) {
				SignatureGenerator sigGen = new SignatureGenerator("/tmp/key");
				sigGen.setUseMessageExpiry(false);
				byte[] message = sigGen.signMessage(args[0]);
				String signature = new String(Base64.encodeBase64(message));
				GeneralUtils.writeObject("signature.base64", signature);
			}
			else {
				SignatureGenerator signature = new SignatureGenerator("/tmp/key");
				signature.setUseMessageExpiry(false); // TODO turn off for ease of development - should normally be on
				VidaasSignature vSig = signature.signMessageAndEncode("freddy");
				System.out.println("Sig in is " + vSig.getSignature());
				GeneralUtils.writeObject("signature.base64.new", vSig.getSignature());
			}
			System.out.println("done");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}