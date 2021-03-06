package uk.ac.ox.oucs.iam.interfaces.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import uk.ac.ox.oucs.iam.interfaces.security.utilities.VidaasSignature;
import uk.ac.ox.oucs.iam.interfaces.utilities.GeneralUtils;

/**
 * Signature Verifier verifies a digital signature. The signature verification
 * requires the public-key part of an asymmetric key pair.
 * 
 * @author Ants
 */
public class SignatureVerifier {
	Logger log = Logger.getLogger(SignatureVerifier.class);
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
	public SignatureVerifier(byte[] publicKeyBytes) throws GeneralSecurityException {
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
	public SignatureVerifier(String filePath) throws IOException, GeneralSecurityException {
		if (log.isDebugEnabled()) {
			log.debug("SignatureVerifier:" + filePath);
		}
		this.publicKey = (PublicKey) GeneralUtils.readObjectFromFile(filePath + ".pub");
	}

	/**
	 * Create the public key instance, using the key bytes passed in
	 * 
	 * @param publicKeyBytes
	 *            bytes to use for the public key
	 * @throws GeneralSecurityException
	 */
	private void initPublicKey(byte[] publicKeyBytes) throws GeneralSecurityException {
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("DSA");
		this.publicKey = keyFactory.generatePublic(keySpec);
	}

	/**
	 * Verify the signature for a message, without worrying about timestamps
	 * 
	 * @param signatureBytes
	 *            the digital signature to verify
	 * @param message
	 *            the message that the signature is for
	 * @return true if the verification succeeds, otherwise false
	 * @throws java.security.GeneralSecurityException
	 *             on any security API errors during the verification
	 */
	public boolean verifyDigitalSignature(byte[] signatureBytes, String message) throws GeneralSecurityException {
		Signature sig = Signature.getInstance(SignatureGenerator.encodingInstance);
		sig.initVerify(publicKey);
		sig.update(message.getBytes());
		return sig.verify(signatureBytes);
	}

	/**
	 * Decodes the signature before calling verifyDigitalSignature above
	 * 
	 * @param vidaasSignature
	 *            the signature as a string (contains the signature but a
	 *            creation stamp)
	 * @param message
	 *            the message embedded in the digital signature
	 * @return true if the verification succeeds, otherwise false
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public boolean verifyDigitalSignature(VidaasSignature vidaasSignature, String message)
			throws GeneralSecurityException, IOException {
		byte[] sigBytes;
		System.out.println("verifyDigitalSignature");
		
		Date now = new Date();
		if (((now.getTime() - vidaasSignature.getTimestamp()) > (maxMessageAgeSeconds * 1000))
				&& (vidaasSignature.getTimestamp() != 0)) {
			// Message is too old
			messageTooOld = true;
		} else {
			// Message has not yet expired
			messageTooOld = false;
			String decodedUrl = decodeSignature(vidaasSignature.getSignature());
			System.out.println("Decoded:" + decodedUrl);
			sigBytes = decodedUrl.getBytes();
			return verifyDigitalSignature(sigBytes, vidaasSignature.getTimestampedMessage(message));
		}
		
		return false;
	}
	
	
	/**
	 * Determine if a message is too old based on its timestamp. A message is deemed to be
	 * too old if it was generated more than maxMessageAgeSeconds seconds ago.
	 * maxMessageAgeSeconds is currently 60.
	 * @param timestamp A string representation of the number of milliseconds elapsed between message creation and 1970
	 * @return true if the timestamp suggests the message was generated within the last (60) seconds,
	 * 		i.e. the timestamp has been verified, else false
	 */
	public boolean verifyTimestamp(String timestamp) {
		log.debug("Verifying string based timestamp");
		return (verifyTimestamp(Long.parseLong(timestamp)));
	}
	
	/**
	 * Determine if a message is too old based on its timestamp. A message is deemed to be
	 * too old if it was generated more than maxMessageAgeSeconds seconds ago.
	 * maxMessageAgeSeconds is currently 60.
	 * @param lTimestamp the number of milliseconds elapsed between message creation and 1970
	 * @return true if the timestamp suggests the message was generated within the last (60) seconds,
	 * 		i.e. the timestamp has been verified, else false
	 */
	public boolean verifyTimestamp(long lTimestamp) {
		Date now = new Date();
		if (log.isDebugEnabled()) {
			log.debug(String.format("Check if input timestamp (%d, args) is much less than current timestamp (%d) that would suggest the message is too old",
					lTimestamp, now.getTime()));
			log.debug(String.format("Max age of a message is %d seconds", maxMessageAgeSeconds));
		}
		if (((now.getTime() - lTimestamp) > maxMessageAgeSeconds*1000)
				&& (lTimestamp != 0)) {
			log.debug("Message is too old");
			// Message is too old
			messageTooOld = true;
		} else {
			// Message has not yet expired
			log.debug("Message is not too old");
			messageTooOld = false;
		}
		return !messageTooOld;
	}
	
	public byte[] decodeAsByteArrayWithoutPosting(String signature) throws UnsupportedEncodingException {
		return Base64.decodeBase64(URLDecoder.decode(signature, "UTF-8"));
	}
	
	public byte[] decodeAsByteArray(String signature) {
		return Base64.decodeBase64(signature);
	}
	
	public String decodeSignature(String signature) throws IOException {
		return new String(Base64.decodeBase64(signature));
	}

	/**
	 * Set the maximum age that a digital certificate is allowed to be before
	 * considered too old
	 * 
	 * @param maxMessageAgeSeconds
	 *            the new age of the certificate. Must be >0 (if the check is
	 *            required) or zero to turn off checking.
	 */
	public void setMaxMessageAgeSeconds(int maxMessageAgeSeconds) {
		if (maxMessageAgeSeconds >= 0) {
			this.maxMessageAgeSeconds = maxMessageAgeSeconds;
		}
	}

	/**
	 * 
	 * @return true of the message has been calculated as being too old (i.e.
	 *         was generated more than maxMessageAgeSeconds seconds previously),
	 *         else false
	 */
	public boolean isMessageTooOld() {
		return messageTooOld;
	}

	private static void printCmdParameters() {
		System.out.println("\n\nUsage: sig-verify.bat [message]" + "\n\nThis requires the files public-key.bin and "
				+ "signature.base64.\n\n");
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			printCmdParameters();
			System.exit(1);
		}
		try {
			if (SignatureGenerator.originalCode) {
				// Read the signature in and decode it.
				String sig = (String) GeneralUtils.readObjectFromFile("signature.base64");
				byte[] sigBytes = Base64.decodeBase64(sig);

				// Verify the digital signature.
				SignatureVerifier sigVerify = new SignatureVerifier("/tmp/key");
				if (sigVerify.verifyDigitalSignature(sigBytes, args[0])) {
					System.out.println("\nSignature verification successful!\n");
				} else {
					System.out.println("\nSignature verification failed!\n");
					System.exit(1);
				}
			} else {
				SignatureVerifier sigVer = new SignatureVerifier("/tmp/key");
				String sig = (String) GeneralUtils.readObjectFromFile("signature.base64.new");
				System.out.println("Sig is " + sig);
				VidaasSignature vidaasSignature = new VidaasSignature(sig, 0);
				boolean b = sigVer.verifyDigitalSignature(vidaasSignature, "freddy");
				if (b) {
					System.out.println("\nSignature verification successful!\n");
				} else {
					System.out.println("\nSignature verification failed!\n");
					System.exit(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}