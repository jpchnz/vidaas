package uk.ac.ox.oucs.iam.interfaces.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import uk.ac.ox.oucs.iam.interfaces.utilities.GeneralUtils;
import uk.ac.ox.oucs.iam.interfaces.utilities.SystemVars;

public class ReceivePostedData {
	private static Logger log = Logger.getLogger(ReceivePostedData.class);

	public static final String REQUEST_DATA_CODE = "requestCurrent=data";
	public static final String REQUEST_DATA_CODE_DONT_CLEAR_STACK = "requestCurrent=dataNoClear";

	/**
	 * Receive pending messages from the IAM secure message receiver, clearing
	 * the stack once processed.
	 * 
	 * @return a list of SecurePostData objects defining the messages destined
	 *         for the service
	 * @throws IOException
	 */
	public static List<SecurePostData> getPendingMessageDataAndClear() throws IOException {
		log.debug("getPendingMessageDataAndClear");
		return getPendingMessageData(true);
	}

	/**
	 * Receive pending messages from the IAM secure message receiver but do not
	 * clear the stack once processed.
	 * 
	 * @return a list of SecurePostData objects defining the messages destined
	 *         for the service
	 * @throws IOException
	 */
	public static List<SecurePostData> getPendingMessageDataAndKeep() throws IOException {
		log.debug("getPendingMessageDataAndKeep");
		return getPendingMessageData(false);
	}

	private static List<SecurePostData> getPendingMessageData(boolean clear) throws IOException {
		String result = "";

		if (log.isDebugEnabled()) {
			log.debug(String.format("About to post command to %s", SystemVars.ADDRESS_OF_IAM_WEBAPP_RECEIVER));
		}
		boolean test = true;

		if (test) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("requestCurrent",
					"data"));
			result = GeneralUtils.sendStandardHttpPost(SystemVars.ADDRESS_OF_IAM_WEBAPP_RECEIVER, nameValuePairs);
			if (log.isDebugEnabled()) {
				log.debug("Got result: " + result);
			}
		}
		else {
			URL url = new URL(SystemVars.ADDRESS_OF_IAM_WEBAPP_RECEIVER);
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());

			if (clear) {
				out.write(REQUEST_DATA_CODE);
			}
			else {
				out.write(REQUEST_DATA_CODE_DONT_CLEAR_STACK);
			}

			out.flush();
			out.close();

			BufferedReader in = null;

			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String decodedString;

			boolean firstLine = true;
			while ((decodedString = in.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}
				result += decodedString + "\n";
			}
			in.close();
		}

		List<SecurePostData> securePostDataList = new ArrayList<SecurePostData>();
		if (result.length() != 0) {
			securePostDataList = SecurePostData.getObjectFromString(result);
		}

		return securePostDataList;
	}

	public static void main(String[] args) {
		try {
			List<SecurePostData> securePostDataList = ReceivePostedData.getPendingMessageDataAndClear();
			if ((securePostDataList == null) || (securePostDataList.size() == 0)) {
				System.out.println("No data returned");
			}
			else {
				System.out.println(String.format("We have %s piece%s of data", securePostDataList.size(),
						securePostDataList.size() > 1 ? "s" : ""));
				int counter = 0;
				for (SecurePostData spd : securePostDataList) {
					System.out.println("Item " + (counter + 1));
					System.out.println("Originator for data " + (counter + 1) + " = " + spd.getOriginatorHost());
					for (String s : spd.getPostParms().values()) {
						System.out.println(s);
					}
					counter++;
				}
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
