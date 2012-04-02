package uk.ac.ox.oucs.iam.roles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

@Deprecated
public class RolePoster {
	private OutputStreamWriter out;
	private String postData;
	private URLConnection connection = null;
	private URL url;
	
	
	public RolePoster(URL url) {
		this.url = url;
	}
	
	
	protected String sendPost(String postData) throws IOException {
		this.postData = postData;
		sendPost();
		return getResult();
	}
	
	
	/**
	 * Post the data set in variable:postData to the web service defined in the constructor
	 * @throws IOException
	 */
	private void sendPost() throws IOException {
		connection = url.openConnection();
		connection.setDoOutput(true);
		out = new OutputStreamWriter(connection.getOutputStream());
		out.write(postData);
		out.flush();
		out.close();
	}
	
	
	/**
	 * Add the result from the servlet to a String with newlines
	 * @return the output from the web servlet as a String
	 * @throws IOException
	 */
	private String getResult() throws IOException {
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
}
