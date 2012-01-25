package uk.ac.ox.oucs.iam.postsecurely;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class SendViaPost {
	private URL url;
	private URLConnection connection = null;
	private OutputStreamWriter out;
	private String postData;
	private boolean encrypt = false;
	
	
	public SendViaPost() {
	}
	
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
		connection = url.openConnection();
		connection.setDoOutput(true);
		out = new OutputStreamWriter(connection.getOutputStream());
		out.write(postData);
		out.flush();
		out.close();
	}
	
	
	public String getResult() throws IOException {
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
			post.sendPost("http://localhost:8081/iam/ReceivePost", "name=freddy&password=bibble");
			System.out.println(post.getResult());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
