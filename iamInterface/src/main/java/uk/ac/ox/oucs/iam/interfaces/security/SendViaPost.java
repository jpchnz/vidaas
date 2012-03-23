package uk.ac.ox.oucs.iam.interfaces.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import uk.ac.ox.oucs.iam.interfaces.security.audit.IamAudit;
import uk.ac.ox.oucs.iam.interfaces.security.keys.KeyServices;
import uk.ac.ox.oucs.iam.interfaces.security.utilities.VidaasSignature;
import uk.ac.ox.oucs.iam.interfaces.utilities.GeneralUtils;
import uk.ac.ox.oucs.iam.interfaces.utilities.SystemVars;
import uk.ac.ox.oucs.iam.interfaces.utilities.exceptions.DuplicateKeyException;
import uk.ac.ox.oucs.iam.interfaces.utilities.exceptions.KeyNotFoundException;
import uk.ac.ox.oucs.iam.interfaces.utilities.exceptions.NewKeyException;

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
	public String sendSecurePost(String destinationIP, String postData) throws IOException, NewKeyException, KeyNotFoundException,
			DuplicateKeyException {
		this.destinationIP = destinationIP;
		keyFile = GeneralUtils.provideBaseKeyPairName();
		return sendSecurePost(postData);
	}

	// Testing only
	public String sendSecurePost(String destinationIP, String postData, boolean encrypt) throws IOException, NewKeyException,
			KeyNotFoundException, DuplicateKeyException {
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
					// Try to decode ...
					SignatureVerifier sigVerifier = new SignatureVerifier(keyFile);
					byte[] decodedBytes = sigVerifier.decodeAsByteArrayWithoutPosting(vSig.getSignature());
					if (sigVerifier.verifyDigitalSignature(decodedBytes, vSig.getOriginalMessage())) {
						log.debug("All good so far");
					}
					else {
						log.error("All bad so far - unable to verify sig");
					}
				}
			}
			catch (GeneralSecurityException e) {
				log.error("Unable to generate signature - sorry it didn't work out");
				return null;
			}
		}

		connection = urlOfReceiveService.openConnection();
		connection.setDoOutput(true);
		log.debug("Connection opened");
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
		String dataToPost;
		if (encrypt) {
			if (vSig.isTimeStampInUse()) {
				dataToPost = String.format("%s&%s=%s&%s=%s&%s=%s&%s=%s", postData, SignatureGenerator.KEYFILE_POST_ATTRIBUTE,
						keyBaseName, SignatureGenerator.TIMESTAMP_POST_ATTRIBUTE, vSig.getTimestamp(), SignatureGenerator.DEST_IP, destinationIP,
						SignatureGenerator.SIGNATURE_POST_ATTRIBUTE, vSig.getSignature());
			}
			else {
				dataToPost = String.format("%s&%s=%s&%s=%s&%s=%s", postData, SignatureGenerator.KEYFILE_POST_ATTRIBUTE,
						keyBaseName, SignatureGenerator.DEST_IP, destinationIP, SignatureGenerator.SIGNATURE_POST_ATTRIBUTE, vSig.getSignature());
			}
		}
		else {
			dataToPost = postData;
		}

		log.debug("writing data ...");
		out.write(dataToPost);
		if (encrypt) {
			auditer.auditSometimes(String.format("Sent post <%s> to host %s with timestamp %s", postData, destinationIP,
					vSig.isTimeStampInUse()));
		}
		out.flush();
		out.close();
		messagePosted = true;
		log.debug("Message posted");
		getResult();

		return dataToPost;
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
//			System.out.println("Send via post");
//			Date orig_stamp = new Date();
////			System.out.println(orig_stamp.getTime());
//			Thread.sleep(500);
//			Date now = new Date();
//			
//			System.out.println((now.getTime() - orig_stamp.getTime()));
//			if ( (now.getTime() - orig_stamp.getTime()) > (60 * 1000) ) {
//				System.out.println("old");
//			}
//			else {
//				System.out.println("Fine");
//			}
//			if (true) return;
			SendViaPost post = new SendViaPost();
			for (int i = 0; i < 1; i++) {
				String r = post.sendSecurePost(
						//"http://129.67.241.38/iam/ReceivePost",
						"http://localhost:8081/iam/ReceivePost",
						String.format("name=freddy%d&password=bibble%d&anotherField=oh no larry%d",
								new Random().nextInt(99999), new Random().nextInt(99999), new Random().nextInt(99999)));
				System.out.println("Result:"+r);
			}
			List<SecurePostData> securePostDataList = ReceivePostedData.getPendingMessageDataAndClear();
			int counter = 0;
			for (SecurePostData spd : securePostDataList) {
				System.out.println("Item " + (counter+1));
				System.out.println("Originator for data " + (counter+1) + " = " + spd.getOriginatorHost());
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
