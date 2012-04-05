package uk.ac.ox.oucs.iam.interfaces.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import uk.ac.ox.oucs.iam.interfaces.security.keys.KeyServices;
import uk.ac.ox.oucs.iam.interfaces.utilities.exceptions.DuplicateKeyException;
import uk.ac.ox.oucs.iam.interfaces.utilities.exceptions.KeyNotFoundException;

/**
 * A class containing general static utilities
 * 
 * @author dave
 *
 */
public class GeneralUtils {
	/**
	 * Send a POST command using the HttpPost helper class from Apache with a single parameter value (a=b)
	 * @param destination Destination IP
	 * @param key single key to be sent as parameter (key=data)
	 * @param data single data to be sent as parameter (key=data)
	 * @return the response from the destination
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static String sendStandardHttpPost(String destination, String key, String data) throws IllegalStateException, IOException {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
		nameValuePair.add(new BasicNameValuePair(key, data));
		return sendStandardHttpPost(destination, nameValuePair);
	}
	
	
	/**
	 * Send a POST command using the HttpPost helper class from Apache  with multiple parameter values (a=b&c=d)
	 * @param destination destination Destination IP
	 * @param nameValuePairs a list of name/value pairs to be sent as parameters
	 * @return the response from the destination
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static String sendStandardHttpPost(String destination, List<NameValuePair> nameValuePairs) throws IllegalStateException, IOException {
		String result = "";
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(destination);
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response = client.execute(post);
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent()));
		String decodedString;

		boolean firstLine = true;
		while ((decodedString = rd.readLine()) != null) {
			if (firstLine) {
				firstLine = false;
				continue;
			}
			result += decodedString + "\n";
		}
		
		return result;
	}
	
	
	/**
	 * Get a key pair from the keystore
	 * TODO - in dev
	 * 
	 * @param keystore
	 * @param alias
	 * @param password
	 * @return
	 */
	public static KeyPair getPrivateKey(KeyStore keystore, String alias, char[] password) {
	    try {
	        // Get private key
	        Key key = keystore.getKey(alias, password);
	        if (key instanceof PrivateKey) {
	            // Get certificate of public key
	            java.security.cert.Certificate cert = keystore.getCertificate(alias);

	            // Get public key
	            PublicKey publicKey = cert.getPublicKey();

	            // Return a key pair
	            return new KeyPair(publicKey, (PrivateKey)key);
	        }
	    } catch (UnrecoverableKeyException e) {
	    } catch (NoSuchAlgorithmException e) {
	    } catch (KeyStoreException e) {
	    }
	    return null;
	}
	
	/**
	 * Load the key stor
	 * TODO - in dev
	 * 
	 * @return
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 */
	public static KeyStore loadKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(SystemVars.keyStoreFileName);
//		log.debug(inputStream == null ? "NULL" : "NOT NULL");
		InputStream inputStream2 = classLoader.getResourceAsStream("/"+SystemVars.keyStoreFileName);
		inputStream = GeneralUtils.class.getClassLoader().getResourceAsStream(SystemVars.keyStoreFileName);
	      
		//File f = new File(new String(u));
		if (inputStream == null) {
			// We have not found the keystore - let's create it anew
			return prepareNewKeyStore();
		}
	    KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
	    keystore.load(inputStream, SystemVars.temporaryKeystorePassword.toCharArray());
	    
	    return keystore;
	}
	
	/**
	 * Prepare a new key store
	 * TODO - in dev
	 * 
	 * @return
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 */
	public static KeyStore prepareNewKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//		InputStream inputStream = classLoader.getResourceAsStream(SystemVars.keyStoreFileName);
	    FileOutputStream fos = new FileOutputStream("/tmp/tt");

		// Load the keystore
	    ks.load(null, null);

		
		ks.store(fos, SystemVars.temporaryKeystorePassword.toCharArray() );  
	    
		
		fos.close();
	    return ks;
	}
	
	
	/**
	 * Utility to provide the local key pair name. I wrote this because it may
	 * be that the initial naming convention for keys is changed, and this
	 * provides a centralised means of changing their names. Key names will need
	 * to be unique in the VIDaaS system since several (public) keys will need
	 * to reside on the web server.
	 * 
	 * For now, the key name shall be a UUID in a specific local folder.
	 * 
	 * @return The fully qualified base name of the keypair (e.g. /tmp/key, that
	 *         might refer to /tmp/key.pub and /tmp/key.priv). This will be
	 *         generated if no key currently exists.
	 * @throws DuplicateKeyException
	 *             more than one private keys were found in the key directory
	 * @throws KeyNotFoundException
	 *             the corresponding public key to the located private key was
	 *             not found
	 * @throws IOException
	 */
	public static String provideBaseKeyPairName() throws DuplicateKeyException, KeyNotFoundException, IOException {
		String keyPairName = "";
		String keyPairDirectory = provideKeyPairDirectory();

		/*
		 * Look for a public and private key
		 */

		String fileName;
		File folder = new File(keyPairDirectory);
		File[] listOfFiles = folder.listFiles();

		int counter = 0;
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				fileName = listOfFiles[i].getName();
				if (fileName.endsWith(KeyServices.privateKeyNameExtension)) {
					counter++;
					keyPairName = fileName.substring(0,
							fileName.length() - KeyServices.privateKeyNameExtension.length());
				}
			}
		}
		if (counter > 1) {
			throw new DuplicateKeyException(counter);
		}
		else if (counter == 1) {
			if (!new File(keyPairDirectory + File.separator + keyPairName + KeyServices.publicKeyNameExtension)
					.exists()) {
				throw new KeyNotFoundException();
			}
		}
		else {
			/*
			 * We have no keys - first time through. Generate a new key name
			 * here.
			 */
			UUID id = UUID.randomUUID();
			keyPairName = id.toString();
		}

		return keyPairDirectory + keyPairName;
	}

	/**
	 * Read the contents of the system file "keyData.txt" and from that extract
	 * the location folder where keys are stored.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String provideKeyPairDirectory() throws IOException {
		String keyPairDirectory = null;// /tmp/keyStore
		String keyDataText = null;
		try {
			keyDataText = (String) readObjectFromFile("keyData.txt");
			if (keyDataText != null) {
				String[] keyDataTextPair = keyDataText.split("=");
				if ((keyDataTextPair != null) && (keyDataTextPair.length > 1)) {
					keyPairDirectory = keyDataTextPair[1];
				}
			}
		}
		catch (IOException e) {
			keyPairDirectory = SystemVars.locationOfKeyStore;
		}
		if (!new File(keyPairDirectory).exists()) {
			if (!new File(keyPairDirectory).mkdirs()) {
				// Cannot create folder to hold keys
				throw new IOException(String.format("Cannot create folder to hold keys - permissions problem with folder %s, or maybe the folder doesn't exist?",
						keyPairDirectory));
			}
		}

		if (keyPairDirectory == null) {
			// Problem here - badly formatted data file
			throw new IOException();
		}

		return keyPairDirectory;
	}
	
	
	

	/**
	 * Copy a file
	 * 
	 * @param src
	 *            the source file to be copied
	 * @param dst
	 *            the destination file
	 * @throws IOException
	 */
	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	/**
	 * Read the contents of a file and return as a generic object that can be
	 * cast to something more useful
	 * 
	 * @param fileName
	 *            the file to read
	 * @return a generic object representing the file contents
	 * @throws IOException
	 */
	public static Object readObjectFromFile(String fileName) throws IOException {
		Object o = null;

		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			o = in.readObject();
			in.close();
			fis.close();
		}
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		return o;
	}

	
	
	/**
	 * Create an URL encoded PublicKey object 
	 * @param filename the file containing the key
	 * @return The URL encoded key
	 * @throws IOException
	 */
	public static String readPublicKeyFromFileAndEncode(String filename) throws IOException {
		String encodedPublicKey = "";

		PublicKey publicKey = (PublicKey) readObjectFromFile(filename);

		encodedPublicKey = new String(Base64.encodeBase64(publicKey.getEncoded()));

		return URLEncoder.encode(encodedPublicKey, "UTF-8");
	}

	
	
	/**
	 * Decode an encoded key and write to file
	 * 
	 * @param encodedPublicKey the encoded key to process
	 * @param filename the name of the file to write the unencoded key to
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static void decodePublicKeyAndWriteToFile(String encodedPublicKey, String filename)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		String algorithm = "DSA";
		byte[] decodedKey = Base64.decodeBase64(encodedPublicKey);
		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(decodedKey);
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		PublicKey newPublicKey = keyFactory.generatePublic(publicKeySpec);
		GeneralUtils.writeObject(filename, newPublicKey);
	}

	
	/**
	 * Read a file to a string
	 * 
	 * @param filePath the file to read
	 * @return the contents of a file as a String
	 * @throws java.io.IOException
	 */
	public static String readFileAsString(String filePath) throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

	/**
	 * Create a file with specific contents (such as a key)
	 * 
	 * @param fileName
	 *            the file to create
	 * @param o
	 *            the contents for the file
	 */
	public static void writeObject(String fileName, Object o) {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(fileName);
			out = new ObjectOutputStream(fos);
			out.writeObject(o);
			out.close();
			fos.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	
	/**
	 * Append data to a file, creating the file if it doesn't exist
	 * @param fileName the file to append data to
	 * @param data the data to append
	 */
	public static void appendStringToFile(String fileName, String data) {
		try {
			// ask user for file name to write to

			FileWriter out = new FileWriter(fileName);
			BufferedWriter writer = new BufferedWriter(out);
			writer.write(data);
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the local hostname
	 * 
	 * @return the hostname of the local machine
	 */
	public static String getLocalHostname() {
		String hostname = null;
		try {
			InetAddress addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return hostname;
	}

	
	
	/**
	 * Get the local host IP address
	 * 
	 * TODO investigate the response with multi NICs
	 * 
	 * @return the ip address of the local host
	 */
	public static String getLocalIPAddress() {
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
		return ip.getHostAddress();
	}
	
	
	/**
	 * Concatenate data into a String as a POST parameter. This this function will add the appropriate
	 * '&' characters, so that a=b,c=d will be concatenated to a=b&c=d
	 * @param dataToConcatenate the resultant concatenation that can be used as a POST parameter
	 * @return
	 */
	public static String reconstructSortedData(String[] dataToConcatenate) {
		String postData = "";
		for (String s : dataToConcatenate) {
			postData += s;
			if (s.compareTo(dataToConcatenate[dataToConcatenate.length-1]) != 0) {
				postData += "&";
			}
		}
		return postData;
	}
	
	
	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore ks = loadKeyStore();
		
		// List the aliases
	    Enumeration e = ks.aliases();
	    for (; e.hasMoreElements(); ) {
	        String alias = (String)e.nextElement();

	        // Does alias refer to a private key?
	        boolean b = ks.isKeyEntry(alias);

	        // Does alias refer to a trusted certificate?
	        b = ks.isCertificateEntry(alias);
	    }
	}
}
