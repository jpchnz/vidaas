package uk.ac.ox.oucs.iam.postsecurely;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.Random;

import uk.ac.ox.oucs.iam.audit.IamAudit;
import uk.ac.ox.oucs.iam.security.keys.KeyServices;
import uk.ac.ox.oucs.iam.security.utilities.GeneralUtils;
import uk.ac.ox.oucs.iam.security.utilities.SignatureGenerator;
import uk.ac.ox.oucs.iam.security.utilities.SignatureVerifier;
import uk.ac.ox.oucs.iam.security.utilities.VidaasSignature;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.DuplicateKeyException;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.KeyNotFoundException;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.NewKeyException;

public class SendViaPost {
	private URL url;
	private URLConnection connection = null;
	private OutputStreamWriter out;
	private String postData;
	private boolean encrypt = true;
	public String keyFile;
	private boolean messagePosted = false;
	private final String keyType = "HmacSHA512";
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
	 * @param url
	 *            the address where the POST data is to be sent
	 * @param postData
	 *            HTTP POST data
	 * @return true if the message was successfully sent, otherwise false
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
	public boolean sendPost(String url, String postData) throws IOException, NewKeyException, KeyNotFoundException,
			DuplicateKeyException {
		this.url = new URL(url);
		keyFile = GeneralUtils.provideBaseKeyPairName();
		this.postData = postData;
		return sendPost();
	}

	private boolean sendPost() throws IOException, NewKeyException, KeyNotFoundException {
		IamAudit auditer = new IamAudit();
		messagePosted = false;
		VidaasSignature vSig = null;

		if (encrypt) {
			/*
			 * The first thing we need to do is generate a signature. This
			 * should be a specific file in a specific, read-only location.
			 */
			File privateKey = new File(keyFile + KeyServices.privateKeyNameExtension);
			if (!privateKey.exists()) {
				// Create the private and public keys
				try {
					auditer.auditAlways("Local keys do not exist so will be created");
					new KeyServices(keyFile, true, keyType);
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
				System.out.println("This is bad - sorry it didn't work out");
				return false;
			}

			// Now we generate a signature object
			try {
				SignatureGenerator signature = new SignatureGenerator(keyFile);
				signature.setUseMessageExpiry(true);
				vSig = signature.signMessageAndEncode(postData);

				boolean debug = true;
				if (debug) {
					// Try to decode ...
					SignatureVerifier sigVerifier = new SignatureVerifier(keyFile);
					byte[] decodedBytes = sigVerifier.decodeAsByteArrayWithoutPosting(vSig.getSignature());
					if (sigVerifier.verifyDigitalSignature(decodedBytes, vSig.getOriginalMessage())) {
						System.out.println("All good so far");
					}
					else {
						System.out.println("All bad so far");
					}
				}
			}
			catch (GeneralSecurityException e) {
				System.out.println("Unable to generate signature - sorry it didn't work out");
				return false;
			}
		}

		connection = url.openConnection();
		connection.setDoOutput(true);
		out = new OutputStreamWriter(connection.getOutputStream());

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
		if (vSig.isTimeStampInUse()) {
			dataToPost = String.format("%s&%s=%s&%s=%s&%s=%s", postData, SignatureGenerator.KEYFILE_POST_ATTRIBUTE,
					keyBaseName, SignatureGenerator.TIMESTAMP_POST_ATTRIBUTE, vSig.getTimestamp(),
					SignatureGenerator.SIGNATURE_POST_ATTRIBUTE, vSig.getSignature());
		}
		else {
			dataToPost = String.format("%s&%s=%s&%s=%s", postData, SignatureGenerator.KEYFILE_POST_ATTRIBUTE,
					keyBaseName, SignatureGenerator.SIGNATURE_POST_ATTRIBUTE, vSig.getSignature());
		}// SignatureGenerator
		System.out.println("Posting " + dataToPost);
		out.write(dataToPost);
		auditer.auditSometimes(String.format("Sent post <%s> to host %s with timestamp %s", postData, url.toString(),
				vSig.isTimeStampInUse()));
		out.flush();
		out.close();
		messagePosted = true;
		getResult();

		return true;
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
			System.out.println("Send via post");
			SendViaPost post = new SendViaPost();
			for (int i = 0; i < 10; i++) {
				post.sendPost(
						"http://localhost:8081/iam/ReceivePost",
						String.format("name=freddy%d&password=bibble%d&anotherField=oh no larry%d", new Random().nextInt(99999),
								new Random().nextInt(99999), new Random().nextInt(99999)));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
