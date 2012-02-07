package uk.ac.ox.oucs.iam.security.keys;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uk.ac.ox.oucs.iam.security.utilities.GeneralUtils;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.KeyNotFoundException;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.NoEncodingException;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

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
	protected PrivateKey privateKey;
	private String genericKeyFileName;
	public static String privateKeyNameExtension = ".priv", publicKeyNameExtension = ".pub";
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
	 *            the name of the key file to use. Might be created if doesn't
	 *            exist depending on other variable in this call
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
		this.genericKeyFileName = keyFileName;
		this.algorithm = algorithm;
		newKeyCreated = !prepareKey(createIfNotThere);
	}
	
	
	private void generateKeyPair() {
		try {
			String algorithm = "DSA"; // or RSA, DH, etc.

			// Generate a 1024-bit Digital Signature Algorithm (RSA) key pair
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
			keyPairGenerator.initialize(1024);
			KeyPair keyPair = keyPairGenerator.genKeyPair();
			PrivateKey privateKey = keyPair.getPrivate();
			PublicKey publicKey = keyPair.getPublic();

			// Get the formats of the encoded bytes
			String privateKeyFormat = privateKey.getFormat();
			System.out.println("PrivateKey format : " + privateKeyFormat);
			String publicKeyFormat = publicKey.getFormat();
			System.out.println("PublicKey format : " + publicKeyFormat);

			// Get bytes of the public and private keys
			byte[] privateKeyBytes = privateKey.getEncoded();
			byte[] publicKeyBytes = publicKey.getEncoded();

			// Get key pair Objects from their respective byte arrays
			// We initialize encoded key specifications based on the encoding
			// formats
			EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
			EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

			KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
			PrivateKey newPrivateKey = keyFactory.generatePrivate(privateKeySpec);
			PublicKey newPublicKey = keyFactory.generatePublic(publicKeySpec);

			GeneralUtils.writeObject(genericKeyFileName+privateKeyNameExtension, newPrivateKey);
			GeneralUtils.writeObject(genericKeyFileName+publicKeyNameExtension, newPublicKey);
			alterKeyFileModeToUserReadOnly(genericKeyFileName+privateKeyNameExtension);
			alterKeyFileModeToUserReadOnly(genericKeyFileName+publicKeyNameExtension);

			System.out.println("Is transformation valid ? "
					+ (privateKey.equals(newPrivateKey) && publicKey.equals(newPublicKey)));
		} catch (InvalidKeySpecException e) {
		} catch (NoSuchAlgorithmException e) {
		}
	}

	/**
	 * Read existing HMAC key from file.
	 * 
	 * @throws IOException
	 */
	private void readKeyFromFile() throws IOException {
		privateKey = (PrivateKey) GeneralUtils.readObjectFromFile(genericKeyFileName+privateKeyNameExtension);

		/*
		 * It shouldn't be necessary to reset file permissions on the key, since
		 * that should have been done on creation, but just for safety let's do
		 * so anyway since this operation should only be called once at startup.
		 */
		alterKeyFileModeToUserReadOnly();
	}

	

//	/**
//	 * Generate Key and store this in local variable as String
//	 * 
//	 * @return a byte array of the key
//	 * @throws NoSuchAlgorithmException
//	 * @throws NoEncodingException
//	 */
//	private byte[] generateKey() throws NoSuchAlgorithmException,
//			NoEncodingException {
////		byte b[] = generateKeyPair();
////		KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
////		secretKey = kgen.generateKey();
////		b = secretKey.getEncoded();
////		if (b == null) {
////			/*
////			 * This algorithm doesn't support encoding
////			 * 
////			 * We could use b = secretKey.toString().getBytes(); and return
////			 * that, but this might be unsafe if we then try to decode a byte
////			 * array generated this way, so for now let's just abort
////			 */
////			throw new NoEncodingException();
////		}
////		return b;
//	}

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
		boolean keyExists = new File(genericKeyFileName+privateKeyNameExtension).exists();
		if (keyExists) {
			// We have a key - use it
			readKeyFromFile();
		} else {
			if (createIfNotThere) {
				// We don't currently have a key - create one
				generateKeyPair();
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
		alterKeyFileModeToUserReadOnly(genericKeyFileName);
	}
	private void alterKeyFileModeToUserReadOnly(String fileName) {
		if (false) {
			return;
		}
		if (useJna && Platform.isLinux()) {
			libc.chmod(fileName, 0400);
		} else {
			File f = new File(fileName);
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
