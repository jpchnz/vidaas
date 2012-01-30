package uk.ac.ox.oucs.iam.postsecurely;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;


public class ReceivePostedData {
	public static List<SecurePostData> getPendingMessageData(String urlString) throws IOException {
		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
		
		out.write(ReceivePost.REQUEST_DATA_CODE);

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
			List<SecurePostData> securePostDataList = ReceivePostedData.getPendingMessageData("http://localhost:8080/iam/ReceivePost");
			if (securePostDataList == null) {
				System.out.println("No data returned");
			}
			else {
				System.out.println(String.format("We have %s piece%s of data", securePostDataList.size(), securePostDataList.size() > 1 ? "s" : ""));
				System.out.println("Originator for data #1 = " + securePostDataList.get(0).getOriginatorHost());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
