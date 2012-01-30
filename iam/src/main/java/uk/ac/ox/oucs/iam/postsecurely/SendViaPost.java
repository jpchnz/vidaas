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

import uk.ac.ox.oucs.iam.audit.IamAudit;
import uk.ac.ox.oucs.iam.security.keys.KeyServices;
import uk.ac.ox.oucs.iam.security.utilities.SignatureGenerator;
import uk.ac.ox.oucs.iam.security.utilities.SignatureVerifier;
import uk.ac.ox.oucs.iam.security.utilities.VidaasSignature;

public class SendViaPost {
	private URL url;
	private URLConnection connection = null;
	private OutputStreamWriter out;
	private String postData;
	private boolean encrypt = true;
	public String keyFile = "/tmp/key";
	private boolean messagePosted = false;
	private final String keyType = "HmacSHA512";
	

	public void sendPost(String url, String postData) throws IOException, MalformedURLException {
		this.url = new URL(url);
		this.postData = postData;
		sendPost();
	}

	public void sendPost(URL url, String postData) throws IOException {
		this.url = url;
		this.postData = postData;
		sendPost();
	}

	private void sendPost() throws IOException {
		IamAudit auditer = new IamAudit();
		messagePosted = false;
		VidaasSignature vSig = null;
		
		if (encrypt) {
			/*
			 * The first thing we need to do is generate a signature. This should be
			 * a specific file in a specific, read-only location.
			 */
			File privateKey = new File(keyFile+".priv");
			if (!privateKey.exists()) {
				// Create the private key
				try {
					new KeyServices(keyFile, true, keyType);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (!privateKey.exists()) {
				System.out.println("This is bad - sorry it didn't work out");
				return;
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
			} catch (GeneralSecurityException e) {
				System.out.println("Unable to generate signature - sorry it didn't work out");
				return;
			}
		}

		connection = url.openConnection();
		connection.setDoOutput(true);
		out = new OutputStreamWriter(connection.getOutputStream());

		/*
		 * An example post with timestamp is
		 * ?name=fred&ts=1323857454692&sig=MCwCFGKn7ucmYGTiIti5%2B3QNOnjXSGbMAhRxAvm%2BelrlIvqrCm6LObd%2B5yC%2BSA%3D%3D
		 * An example post without timestamp is
		 * ?name=fred&sig=MCwCFGKn7ucmYGTiIti5%2B3QNOnjXSGbMAhRxAvm%2BelrlIvqrCm6LObd%2B5yC%2BSA%3D%3D
		 */
		System.out.println("Posting " + postData + "&sig=" + vSig.getSignature());
		if (vSig.isTimeStampInUse()) {
			out.write(postData + "&" + SignatureGenerator.TIMESTAMP_POST_ATTRIBUTE + "=" + vSig.getTimestamp() + "&sig=" + vSig.getSignature());
		}
		else {
			out.write(postData + "&sig=" + vSig.getSignature());
		}
		auditer.auditSuccess(String.format("Sent post <%s> to host %s with timestamp %s", postData, url.toString(), vSig.isTimeStampInUse()));
		out.flush();
		out.close();
		messagePosted = true;
		getResult();
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
			post.sendPost("http://localhost:8080/iam/ReceivePost", "name=freddy&password=bibble");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
