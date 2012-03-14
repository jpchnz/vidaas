package uk.ac.ox.oucs.iam.interfaces.roles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Random;

import uk.ac.ox.oucs.iam.interfaces.security.SendViaPost;
import uk.ac.ox.oucs.iam.interfaces.utilities.SystemVars;

public class RolePoster {
	private OutputStreamWriter out;
	private String postData;
	private URLConnection connection = null;
	private URL url;
	private Date lastTry = null;
	private static RolePoster instance = null;
	
	
	protected static RolePoster getInstance() throws MalformedURLException {
		if (instance == null) {
			instance = new RolePoster(new URL(SystemVars.ADDRESS_OF_IAM_WEBAPP));
		}
		
		return instance;
	}
	
	
	
	private RolePoster(URL url) {
		this.url = url;
	}
	
	
	protected String sendPost(String postData) {
		if (lastTry != null) {
			// We cannot currently access the remote server. 
			Date now = new Date();
			if ((now.getTime()-lastTry.getTime()) > (10*60*1000) ) {
				/*
				 * It has been 10 mins since we last tried to contact the server - try again
				 */
				lastTry = null;
			}
			else {
				// Just say yes for now
				return "true";
			}
		}
		
		this.postData = postData;
		try {
			System.out.println("Able to try to post to remote server");
			sendPost();
			return getResult();
		}
		catch (Exception e) {
			System.out.println("Auth server not contactable!");
			lastTry = new Date();
			return "true";
		}
		
//		if (remoteServerUp) {
//			sendPost();
//		}
//		
//		if (remoteServerUp) {
//			return getResult();
//		}
//		else {
//			System.out.println("Auth server not up!");
//			return "true";
//		}
	}
	
	/**
	 * Post the data set in variable:postData to the web service defined in the constructor
	 * @throws IOException
	 */
	private void sendPost() throws Exception {
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
	private String getResult() throws Exception {
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
			RolePoster post = RolePoster.getInstance();
			for (int i = 0; i < 10; i++) {
				System.out.println(post.sendPost("isOwner=owner"));
//						String.format("name=freddy%d&password=bibble%d&anotherField=oh no larry%d",
//								new Random().nextInt(99999), new Random().nextInt(99999), new Random().nextInt(99999))));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
