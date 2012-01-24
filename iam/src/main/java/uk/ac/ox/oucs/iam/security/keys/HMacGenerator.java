package uk.ac.ox.oucs.iam.security.keys;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;

import uk.ac.ox.oucs.iam.security.utilities.GeneralUtils;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.KeyNotFoundException;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.NoEncodingException;

/**
 * Encrypt an HMAC. If not specified, a default "HmacSHA512" algorithm is used.
 * The key is generated.
 * 
 * @author oucs0153
 * 
 */
public class HMacGenerator extends KeyServices implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6005370272212675152L;

	private String hMacString;

	/**
	 * General constructor. Tries to set up the privateKey variable with the
	 * private key, either by reading a previous key or generating a new one
	 * using a default HmacSHA512 algorithm.
	 * 
	 * @param createIfNotThere
	 *            if false then this module is a consumer that must use an HMAC
	 *            from elsewhere
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws KeyNotFoundException
	 * @throws InvalidKeyException
	 * @throws NoEncodingException
	 */
	public HMacGenerator(String inputMessage) throws NoSuchAlgorithmException,
			IOException, KeyNotFoundException, InvalidKeyException,
			NoEncodingException {
		super("key", true, "HmacSHA512");
		generateHmacMD5(inputMessage);
	}

	/**
	 * General constructor. Tries to set up the privateKey variable with the
	 * private key, either by reading a previous key or generating a new one.
	 * 
	 * @param createIfNotThere
	 *            if false then this module is a consumer that must use an HMAC
	 *            from elsewhere
	 * @param algorithm
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws KeyNotFoundException
	 * @throws InvalidKeyException
	 * @throws NoEncodingException
	 */
	public HMacGenerator(String key, String algorithm, String inputMessage)
			throws NoSuchAlgorithmException, IOException, KeyNotFoundException,
			InvalidKeyException, NoEncodingException {
		super(key, true, algorithm);
		generateHmacMD5(inputMessage);
	}

	@Override
	public boolean equals(Object obj) {
		return ((HMacGenerator) obj).gethMacString().equals(this.hMacString);
	}

	/**
	 * General constructor used for testing purposes only. Tries to set up the
	 * privateKey variable with the private key, either by reading a previous
	 * key or generating a new one.
	 * 
	 * @param key
	 *            the name of the key file to use
	 * @param createIfNotThere
	 *            if false then this module is a consumer that must use an HMAC
	 *            from elsewhere
	 * @param algorithm
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws KeyNotFoundException
	 * @throws InvalidKeyException
	 * @throws NoEncodingException
	 */
	protected HMacGenerator(String key, String inputMessage)
			throws NoSuchAlgorithmException, IOException, KeyNotFoundException,
			InvalidKeyException, NoEncodingException {
		super(key, true, "HmacSHA512");
		generateHmacMD5(inputMessage);
	}

	/**
	 * Generate encrypted message based on the local HMAC
	 * 
	 * @param input
	 *            the message that needs to be encrypted
	 * @return null if no key usable, else a String containing the message
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	private void generateHmacMD5(String inputMessage)
			throws UnsupportedEncodingException, NoSuchAlgorithmException,
			InvalidKeyException {
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		byte[] utf8 = inputMessage.getBytes("UTF8");
		byte[] digest = mac.doFinal(utf8);
		// GeneralUtils.writeObject("digest.source", digest);

		// If desired, convert the digest into a string
		hMacString = new sun.misc.BASE64Encoder().encode(digest);
		GeneralUtils.writeObject("hMacString.source", hMacString);
	}

	public String gethMacString() {
		return hMacString;
	}
}
