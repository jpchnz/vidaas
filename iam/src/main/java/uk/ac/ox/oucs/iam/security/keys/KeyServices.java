package uk.ac.ox.oucs.iam.security.keys;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

import uk.ac.ox.oucs.iam.security.utilities.GeneralUtils;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.KeyNotFoundException;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.NoEncodingException;

/**
 * Provides services based around a generic (e.g. HMAC or Symmetric) key. The
 * class is able to create a key, write it to disk and ensure its permissions
 * are set appropriately.
 * 
 * @author oucs0153
 * 
 */
public class KeyServices implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7176143589838004264L;
	protected SecretKey secretKey;
	private String keyFileName;
	private final boolean useJna = true;
	private static CLibrary libc;
	interface CLibrary extends Library {
		public int chmod(String path, int mode);
	}

	public enum Impls {
		Mac, KeyGenerator
	};

	protected String algorithm;
	protected boolean newKeyCreated;

	/**
	 * Constructor.
	 * 
	 * @param keyFileName
	 *            the name of the key file to use. Will be created if doesn't
	 *            exist.
	 * @param createIfNotThere
	 *            if set, the class will create the key if it cannot find it.
	 *            Unset this for key (i.e. message) consumers
	 * @param algorithm
	 *            the encryption to be used, e.g. "Blowfish", "HmacSHA512"
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws KeyNotFoundException
	 * @throws NoEncodingException
	 */
	public KeyServices(String keyFileName, boolean createIfNotThere,
			String algorithm) throws NoSuchAlgorithmException, IOException,
			KeyNotFoundException, NoEncodingException {
		if (Platform.isLinux()) {
			libc = (CLibrary) Native.loadLibrary("c", CLibrary.class);
		}
		this.keyFileName = keyFileName;
		this.algorithm = algorithm;
		newKeyCreated = !prepareKey(createIfNotThere);
	}

	/**
	 * Read existing HMAC key from file.
	 * 
	 * @throws IOException
	 */
	private void readKeyFromFile() throws IOException {
		secretKey = (SecretKey) GeneralUtils.readObjectFromFile(keyFileName);

		/*
		 * It shouldn't be necessary to reset file permissions on the key, since
		 * that should have been done on creation, but just for safety let's do
		 * so anyway since this operation should only be called once at startup.
		 */
		alterKeyFileModeToUserReadOnly();
	}

	/**
	 * Generate HMAC key and write this to file
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws NoEncodingException
	 */
	private void writeKeyToFile() throws NoSuchAlgorithmException, IOException,
			NoEncodingException {
		generateKey();

		GeneralUtils.writeObject(keyFileName, secretKey);

		alterKeyFileModeToUserReadOnly();
	}

	/**
	 * Generate Key and store this in local variable as String
	 * 
	 * @return a byte array of the key
	 * @throws NoSuchAlgorithmException
	 * @throws NoEncodingException
	 */
	private byte[] generateKey() throws NoSuchAlgorithmException,
			NoEncodingException {
		KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
		secretKey = kgen.generateKey();
		byte b[] = secretKey.getEncoded();
		if (b == null) {
			/*
			 * This algorithm doesn't support encoding
			 * 
			 * We could use b = secretKey.toString().getBytes(); and return
			 * that, but this might be unsafe if we then try to decode a byte
			 * array generated this way, so for now let's just abort
			 */
			throw new NoEncodingException();
		}
		return b;
	}

	/**
	 * Check if a key exists in file (and should thus be used). If not, create
	 * one.
	 * 
	 * @param cannotCreate
	 *            if true then this module is a consumer that must use a key
	 *            from elsewhere
	 * @return true if a key file previously existed and is thus used
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws KeyNotFoundException
	 * @throws NoEncodingException
	 */
	private boolean prepareKey(boolean createIfNotThere)
			throws NoSuchAlgorithmException, IOException, KeyNotFoundException,
			NoEncodingException {
		boolean keyExists = new File(keyFileName).exists();
		if (keyExists) {
			// We have a key - use it
			readKeyFromFile();
		} else {
			if (createIfNotThere) {
				// We don't currently have a key - create one
				writeKeyToFile();
			} else {
				// Consumer - we are not allowed to create this key!
				throw new KeyNotFoundException();
			}
		}
		return keyExists;
	}

	/**
	 * Set the key file to be just readable by the local user and nobody else.
	 * This is done using JNA - there is an option to use the File methods but I
	 * have found this to have problems (i.e. not work).
	 */
	private void alterKeyFileModeToUserReadOnly() {
		if (useJna && Platform.isLinux()) {
			libc.chmod(keyFileName, 0400);
		} else {
			File f = new File(keyFileName);
			f.setExecutable(false);
			f.setReadable(true, true);
			f.setWritable(false);
		}
	}

	/**
	 * List all encryption methods supported on this system. Mainly for debug
	 * and development
	 * 
	 * @param impls
	 *            the type of service required, e.g. mac or symmetric
	 */
	public void listImpls(Impls impls) {
		String[] names = getCryptoImpls(impls.toString());
		for (String n : names) {
			System.out.println(n);
		}
	}

	/**
	 * List all implementations supported on this sytem. Mainly for debug and
	 * development
	 * 
	 * @param serviceType
	 *            the type of service required, e.g. mac or symmetric. These are
	 *            enumerated under Impls
	 * @return an array of possible encryption methods
	 */
	private String[] getCryptoImpls(String serviceType) {
		Set<String> result = new HashSet<String>();

		// All all providers
		Provider[] providers = Security.getProviders();
		for (int i = 0; i < providers.length; i++) {
			// Get services provided by each provider
			Set<?> keys = providers[i].keySet();
			for (Iterator<?> it = keys.iterator(); it.hasNext();) {
				String key = (String) it.next();
				key = key.split(" ")[0];

				if (key.startsWith(serviceType + ".")) {
					result.add(key.substring(serviceType.length() + 1));
				} else if (key.startsWith("Alg.Alias." + serviceType + ".")) {
					// This is an alias
					result.add(key.substring(serviceType.length() + 11));
				}
			}
		}
		return (String[]) result.toArray(new String[result.size()]);
	}
}
