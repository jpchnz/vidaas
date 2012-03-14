package uk.ac.ox.oucs.iam.interfaces.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import uk.ac.ox.oucs.iam.interfaces.utilities.SystemVars;


public class ReceivePostedData {
	public static final String REQUEST_DATA_CODE = "requestCurrent=data";
	public static final String REQUEST_DATA_CODE_DONT_CLEAR_STACK = "requestCurrent=dataNoClear";
	
	public static List<SecurePostData> getPendingMessageData() throws IOException {
		URL url = new URL(SystemVars.ADDRESS_OF_IAM_WEBAPP_RECEIVER);
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
		
		out.write(REQUEST_DATA_CODE);

		out.flush();
		out.close();
		
		BufferedReader in = null;
		String result = "";

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
		
		List<SecurePostData> securePostDataList = null;
		if (result.length() != 0) {
			securePostDataList = SecurePostData.getObjectFromString(result);
		}
		
		return securePostDataList;
	}
	
	public static void main(String[] args) {
		try {
			List<SecurePostData> securePostDataList = ReceivePostedData.getPendingMessageData();
			if ( (securePostDataList == null) || (securePostDataList.size() == 0) ) {
				System.out.println("No data returned");
			}
			else {
				System.out.println(String.format("We have %s piece%s of data", securePostDataList.size(), securePostDataList.size() > 1 ? "s" : ""));
				int counter = 0;
				for (SecurePostData spd : securePostDataList) {
					System.out.println("Item " + (counter+1));
					System.out.println("Originator for data " + (counter+1) + " = " + spd.getOriginatorHost());
					for (String s : spd.getPostParms()) {
						System.out.println(s);
					}
					counter++;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
