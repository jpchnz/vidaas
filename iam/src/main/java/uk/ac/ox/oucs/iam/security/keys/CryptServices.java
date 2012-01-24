package uk.ac.ox.oucs.iam.security.keys;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import uk.ac.ox.oucs.iam.security.utilities.exceptions.KeyNotFoundException;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.NoEncodingException;

/**
 * Encrypt or decrypt a message for sending. If not specified, a default
 * "Blowfish" algorithm is used.
 * 
 * 
 * @author oucs0153
 * 
 */
public class CryptServices extends KeyServices implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1545748550612655203L;
	private Cipher ecipher;
	private Cipher dcipher;

	/**
	 * Constructor
	 * 
	 * @param createIfNotThere
	 *            if set, the class will create the key if it cannot find it.
	 *            Unset this for key (i.e. message) consumers
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IOException
	 * @throws KeyNotFoundException
	 * @throws NoEncodingException
	 */
	public CryptServices(boolean createIfNotThere)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IOException, KeyNotFoundException,
			NoEncodingException {
		super("cryptKey", createIfNotThere, "Blowfish");
		init();
	}

	/**
	 * Constructor - for testing only
	 * 
	 * @param keyName
	 *            - name of the key to use
	 * @param createIfNotThere
	 *            if set, the class will create the key if it cannot find it.
	 *            Unset this for key (i.e. message) consumers
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IOException
	 * @throws KeyNotFoundException
	 * @throws NoEncodingException
	 */
	protected CryptServices(String keyName, boolean createIfNotThere)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IOException, KeyNotFoundException,
			NoEncodingException {
		super(keyName, createIfNotThere, "Blowfish");
		init();
	}

	/**
	 * Constructor
	 * 
	 * @param createIfNotThere
	 *            if set, the class will create the key if it cannot find it.
	 *            Unset this for key (i.e. message) consumers
	 * 
	 * @param algorithm
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IOException
	 * @throws KeyNotFoundException
	 * @throws NoEncodingException
	 */
	public CryptServices(boolean createIfNotThere, String algorithm)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IOException, KeyNotFoundException,
			NoEncodingException {
		super("cryptKey", createIfNotThere, algorithm);
		init();
	}

	/**
	 * Initialise variables
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 */
	private void init() throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException {
		ecipher = Cipher.getInstance(algorithm);
		dcipher = Cipher.getInstance(algorithm);
		ecipher.init(Cipher.ENCRYPT_MODE, secretKey);
		dcipher.init(Cipher.DECRYPT_MODE, secretKey);
	}

	/**
	 * Encrypt a message
	 * 
	 * @param str
	 *            the String to encrypt
	 * @return the encrypted value of the string
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 */
	public String encrypt(String str) throws IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException {
		// Encode the string into bytes using utf-8
		byte[] utf8 = str.getBytes("UTF8");

		// Encrypt
		byte[] enc = ecipher.doFinal(utf8);

		// Encode bytes to base64 to get a string
		return new sun.misc.BASE64Encoder().encode(enc);
	}

	/**
	 * Decrypt a string
	 * 
	 * @param str
	 *            the String to decrypt
	 * @return the decrypted value of the string
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws IOException
	 */
	public String decrypt(String str) throws IllegalBlockSizeException,
			BadPaddingException, IOException {
		// Decode base64 to get bytes
		byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

		// Decrypt
		byte[] utf8 = dcipher.doFinal(dec);

		// Decode using utf-8
		return new String(utf8, "UTF8");
	}
}
