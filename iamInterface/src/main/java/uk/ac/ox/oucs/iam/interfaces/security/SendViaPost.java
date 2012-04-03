package uk.ac.ox.oucs.iam.interfaces.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import uk.ac.ox.oucs.iam.interfaces.security.audit.IamAudit;
import uk.ac.ox.oucs.iam.interfaces.security.keys.KeyServices;
import uk.ac.ox.oucs.iam.interfaces.security.utilities.VidaasSignature;
import uk.ac.ox.oucs.iam.interfaces.utilities.GeneralUtils;
import uk.ac.ox.oucs.iam.interfaces.utilities.SystemVars;
import uk.ac.ox.oucs.iam.interfaces.utilities.exceptions.DuplicateKeyException;
import uk.ac.ox.oucs.iam.interfaces.utilities.exceptions.KeyNotFoundException;
import uk.ac.ox.oucs.iam.interfaces.utilities.exceptions.NewKeyException;


/**
 * Class designed to http post data to a remote server. This class
 * will wrapper the data with a security layer thus adding authorisation, authentication and
 * message timeout.
 * 
 * @author dave
 *
 */
public class SendViaPost {
	Logger log = Logger.getLogger(SendViaPost.class);
	private URL urlOfReceiveService;
	private String destinationIP;
	private URLConnection connection = null;
	private OutputStreamWriter out;
	private boolean encrypt = true;
	public String keyFile;
	private boolean messagePosted = false;
	private final String algorithm = "HmacSHA512";
	private IamAudit auditer = new IamAudit();

	/**
	 * Send post data securely to webapp. This function will wrap the requested
	 * post data with a timestamp and signature, generated from the data, the
	 * timestamp and the local private key (that will be generated if it doesn't
	 * exist).
	 * 
	 * The receiver should be able to validate the data using the public key of
	 * the sender.
	 * 
	 * @param destinationIP
	 *            the address where the POST data is to be sent
	 * @param postData
	 *            HTTP POST data
	 * @return a full String representation of the data transmitted if the
	 *         message was successfully sent, otherwise null
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws KeyNotFoundException
	 *             The private key for the located public key has not been
	 *             found.
	 * @throws NewKeyException
	 *             A new key pair has been created. This needs to be sent to the
	 *             host specified in the url before the request is re-run.
	 * @throws DuplicateKeyException
	 *             More than one private key found in the local key store. Only
	 *             one private key may be present.
	 */
	public String sendSecurePost(String destinationIP, String postData) throws IOException, NewKeyException,
			KeyNotFoundException, DuplicateKeyException {
		this.destinationIP = destinationIP;
		keyFile = GeneralUtils.provideBaseKeyPairName();
		return sendSecurePost(postData);
	}

	// Testing only
	public String sendSecurePost(String destinationIP, String postData, boolean encrypt) throws IOException,
			NewKeyException, KeyNotFoundException, DuplicateKeyException {
		this.destinationIP = destinationIP;
		this.encrypt = encrypt;
		keyFile = GeneralUtils.provideBaseKeyPairName();
		return sendSecurePost(postData);
	}

	private String sendSecurePost(String postData) throws IOException, NewKeyException, KeyNotFoundException {
		if (log.isDebugEnabled()) {
			log.debug(String.format("sendSecurePost(%s)", postData));
			log.debug(String.format("Encrypt is %s and the dest ip is %s", encrypt, destinationIP));
		}

		String dataToPost = "";

		/*
		 * Here is the rub. We have post data here that we use to generate a
		 * digital signature. However, the data is sent over the wire and
		 * reconstructed at the servlet, but the servlet can't guarantee the
		 * order in which it processes the parameters. If it gets the order
		 * wrong, it won't be able to generate the correct message to validate
		 * the signature. Thus we should send the data in alphabetical order so
		 * that it can be reset to alphabetical order at the servlet.
		 */
		String[] dataToSort = postData.split("&");
		Arrays.sort(dataToSort);
		// Now reconstruct
		postData = GeneralUtils.reconstructSortedData(dataToSort);

		urlOfReceiveService = new URL(SystemVars.ADDRESS_OF_IAM_WEBAPP_RECEIVER);
		messagePosted = false;
		VidaasSignature vSig = null;

		if (encrypt) {
			/*
			 * The first thing we need to do is generate a signature. This
			 * should be a specific file in a specific, read-only location.
			 */
			File privateKey = new File(keyFile + KeyServices.privateKeyNameExtension);
			if (!privateKey.exists()) {
				log.info("Private key does not exist - creating this now.");
				try {
					auditer.auditAlways("Local keys do not exist so will be created");
					new KeyServices(keyFile, true, algorithm);
				}
				catch (Exception e) {
					e.printStackTrace();
					/*
					 * This is really bad. Without the ability to create keys we
					 * can't do anything.
					 */
					throw new KeyNotFoundException();
				}
				
				if (SystemVars.useMysql) {
					log.info("We have needed to generate a new key. Before we continue" +
							"processing, we should send details of the public key to the remote" +
							"server");
					List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
					log.info("Have keyfile " + keyFile);
					nameValuePair.add(new BasicNameValuePair(SystemVars.POST_COMMAND_PROVIDE_UUID_OF_PUBLIC_KEY, keyFile));
					nameValuePair.add(new BasicNameValuePair(SystemVars.POST_COMMAND_PROVIDE_PUBLIC_KEY, keyFile));
//					String result = uk.ac.ox.oucs.iam.interfaces.utilities.GeneralUtils.sendStandardHttpPost(
//							securePostData.getIntendedDestination(), SystemVars.POST_COMMAND_COMMAND_TOKEN,
//							SystemVars.POST_COMMAND_NEW_DATA_AVAILABLE);
				}
				
				throw new NewKeyException(keyFile);
			}
			if (!privateKey.exists()) {
				log.debug("Can't find or create private key. This is bad - sorry it didn't work out");
				return null;
			}

			// Now we generate a signature object
			try {
				SignatureGenerator signature = new SignatureGenerator(keyFile);
				signature.setUseMessageExpiry(true);
				vSig = signature.signMessageAndEncode(postData);

				boolean debug = true;
				if (debug) {
					log.debug("Debug on - will check data before sending");
					log.debug(String.format("Keyfile:%s", keyFile));
					// Try to decode ...
					SignatureVerifier sigVerifier = new SignatureVerifier(keyFile);

					log.debug(String.format("Sig to verify:%s", vSig.getSignature()));

					byte[] decodedBytes = sigVerifier.decodeAsByteArrayWithoutPosting(vSig.getSignature());
					int a1 = decodedBytes[4];
					int a2 = decodedBytes[27];
					int a3 = decodedBytes[43];
					log.debug(String.format("Decoded bytes length:%d (%d,%d,%d)", decodedBytes.length, decodedBytes[4],
							decodedBytes[27], decodedBytes[43]));

					if (sigVerifier.verifyDigitalSignature(decodedBytes, vSig.getOriginalMessage())) {
						log.debug("All good so far");
					}
					else {
						log.error("All bad so far - unable to verify sig");
						System.exit(0);
					}
				}
			}
			catch (GeneralSecurityException e) {
				log.error("Unable to generate signature - sorry it didn't work out");
				return null;
			}
		}

		if (connection == null) {
			connection = urlOfReceiveService.openConnection();
			connection.setDoOutput(true);
			log.debug("Connection opened");
			
		}
		out = new OutputStreamWriter(connection.getOutputStream());
		log.debug("Stream writer prepared");
		/*
		 * An example post with timestamp is
		 * ?name=fred&ts=1323857454692&key=...&
		 * sig=MCwCFGKn7ucmYGTiIti5%2B3QNOnjXSGbMAhRxAvm
		 * %2BelrlIvqrCm6LObd%2B5yC%2BSA%3D%3D An example post without timestamp
		 * is ?name=fred&key=...&sig=MCwCFGKn7ucmYGTiIti5%2B3QNOnjXSGbMAhRxAvm%2
		 * BelrlIvqrCm6LObd%2B5yC%2BSA%3D%3D
		 */
		String keyBaseName = new File(keyFile).getName();

		if (encrypt) {
			if (vSig.isTimeStampInUse()) {
				dataToPost = String.format("%s&%s=%s&%s=%s&%s=%s&%s=%s", postData,
						SignatureGenerator.KEYFILE_POST_ATTRIBUTE, keyBaseName,
						SignatureGenerator.TIMESTAMP_POST_ATTRIBUTE, vSig.getTimestamp(), SignatureGenerator.DEST_IP,
						URLEncoder.encode(destinationIP, "UTF-8"),// destinationIP,
						SignatureGenerator.SIGNATURE_POST_ATTRIBUTE, vSig.getSignature());
			}
			else {
				dataToPost = String.format("%s&%s=%s&%s=%s&%s=%s", postData, SignatureGenerator.KEYFILE_POST_ATTRIBUTE,
						keyBaseName, SignatureGenerator.DEST_IP, URLEncoder.encode(destinationIP, "UTF-8"),// destinationIP,
						SignatureGenerator.SIGNATURE_POST_ATTRIBUTE, vSig.getSignature());
			}
		}
		else {
			dataToPost = postData;
		}

		log.debug("writing data ...");

		// String hostname = "hostname.com";
		// int port = 8081;
		// InetAddress addr = InetAddress.getByName("localhost");
		// Socket socket = new Socket(addr, port);
		// BufferedWriter wr = new BufferedWriter(new
		// OutputStreamWriter(socket.getOutputStream(), "UTF8"));
		// wr.write("POST /vidaasBilling/BillingServlet HTTP/1.0\r\n");
		// wr.write("Content-Length: " + dataToPost.length() + "\r\n");
		// wr.write("Content-Type: application/x-www-form-urlencoded\r\n");
		// wr.write("\r\n");
		//
		// // Send data
		// wr.write(URLEncoder.encode(dataToPost, "UTF-8"));
		// wr.flush();
		// BufferedReader rd = new BufferedReader(new
		// InputStreamReader(socket.getInputStream()));
		// String line;
		// while ((line = rd.readLine()) != null) {
		// String s = line;
		// // Process line...
		// }
		// wr.close();
		// rd.close();

//		out.write(dataToPost);
////		if (encrypt) {
////			auditer.auditSometimes(String.format("Sent post <%s> to host %s with timestamp %s", postData,
////					destinationIP, vSig.isTimeStampInUse()));
////		}
//		out.flush();
//		
//		messagePosted = true;
//		log.debug("Message posted");
//		//getResult();
//		out.close();
		sendAllData(dataToPost);

		return dataToPost;
	}
	
	
	private void sendAllData(String data) {
		try {
		    // Send data
		    URL url = new URL(SystemVars.ADDRESS_OF_IAM_WEBAPP_RECEIVER);
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();

		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    while ((line = rd.readLine()) != null) {
		        // Process line...
		    }
		    wr.close();
		    rd.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private String getResult() throws IOException {
		if (!messagePosted) {
			return "No data";
		}
		messagePosted = false;

		BufferedReader in = null;
		String result = "";

		in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		String decodedString;

		while ((decodedString = in.readLine()) != null) {
			result += decodedString + "\n";
		}
		in.close();

		return result;
	}

	public static void main(String[] args) {
		try {
			// System.out.println("Send via post");
			// Date orig_stamp = new Date();
			// // System.out.println(orig_stamp.getTime());
			// Thread.sleep(500);
			// Date now = new Date();
			//
			// System.out.println((now.getTime() - orig_stamp.getTime()));
			// if ( (now.getTime() - orig_stamp.getTime()) > (60 * 1000) ) {
			// System.out.println("old");
			// }
			// else {
			// System.out.println("Fine");
			// }
			// if (true) return;
			SendViaPost post = new SendViaPost();
			for (int i = 0; i < 1; i++) {
				String r = post.sendSecurePost(
						// "http://129.67.241.38/iam/ReceivePost",
						// "http://localhost:8081/iam/ReceivePost",
						"http://129.67.241.38/vidaasBilling/BillingServlet",
						String.format("name=freddy%d&password=bibble%d&anotherField=oh no larry%d",
								new Random().nextInt(99999), new Random().nextInt(99999), new Random().nextInt(99999)));
				System.out.println("Result:" + r);
			}
			List<SecurePostData> securePostDataList = ReceivePostedData.getPendingMessageDataAndClear();
			int counter = 0;
			for (SecurePostData spd : securePostDataList) {
				System.out.println("Item " + (counter + 1));
				System.out.println("Originator for data " + (counter + 1) + " = " + spd.getOriginatorHost());
				System.out.println("Timeout = " + spd.isMessageTimedOut());
				System.out.println("Verified = " + spd.isMessageHasBeenVerified());
				for (String s : spd.getPostParms().values()) {
					System.out.println(s);
				}
				counter++;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
