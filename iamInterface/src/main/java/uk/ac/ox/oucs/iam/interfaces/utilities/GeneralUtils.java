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
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
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

public class GeneralUtils {
	public static String sendStandardHttpPost(String destination, String key, String data) throws IllegalStateException, IOException {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
		nameValuePair.add(new BasicNameValuePair(key, data));
		return sendStandardHttpPost(destination, nameValuePair);
	}
	
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
				throw new IOException();
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

	public static String readPublicKeyFromFileAndEncode(String filename) throws IOException {
		String encodedPublicKey = "";

		PublicKey publicKey = (PublicKey) readObjectFromFile(filename);

		encodedPublicKey = new String(Base64.encodeBase64(publicKey.getEncoded()));

		return URLEncoder.encode(encodedPublicKey, "UTF-8");
	}

	public static void decodePublicKeyAndWriteToFile(String encodedPublicKey, String filename)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		String algorithm = "DSA";
		byte[] decodedKey = Base64.decodeBase64(encodedPublicKey);
		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(decodedKey);
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		PublicKey newPublicKey = keyFactory.generatePublic(publicKeySpec);
		GeneralUtils.writeObject(filename, newPublicKey);
	}

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
	
	public static String reconstructSortedData(String[] dataToSort) {
		String postData = "";
		for (String s : dataToSort) {
			postData += s;
			if (s.compareTo(dataToSort[dataToSort.length-1]) != 0) {
				postData += "&";
			}
		}
		return postData;
	}
}
