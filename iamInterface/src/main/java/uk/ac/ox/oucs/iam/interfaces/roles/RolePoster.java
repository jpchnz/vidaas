package uk.ac.ox.oucs.iam.interfaces.roles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.apache.log4j.Logger;

import uk.ac.ox.oucs.iam.interfaces.security.ReceivePostedData;
import uk.ac.ox.oucs.iam.interfaces.utilities.SystemVars;

/**
 * The main class to help determine which roles are authorised to perform which
 * actions.
 * 
 * @author dave
 * 
 */
public class RolePoster {
	private static Logger log = Logger.getLogger(RolePoster.class);

	private OutputStreamWriter out;
	private String postData;
	private URLConnection connection = null;
	private URL url;
	private Date lastTry = null;
	private static RolePoster instance = null;

	/**
	 * Provides an instance of the RolePoster class, instantiating it if it has
	 * not yet been created.
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	protected static RolePoster getInstance() throws MalformedURLException {
		if (instance == null) {
			instance = new RolePoster(new URL(SystemVars.ADDRESS_OF_IAM_WEBAPP));
		}

		return instance;
	}

	/**
	 * Private constructor. Callers need to use the getInstance method
	 * @param url
	 */
	private RolePoster(URL url) {
		this.url = url;
	}

	

	/**
	 * General input into the http post functions of role authorisation. If the (usually remote)
	 * role service is not available this function will notice this and then, on the next attempt, not
	 * try to contact it for 10 minutes to save on resources. In that instance, it will return "true".
	 * 
	 * TODO
	 * It will probably be desirable to return another value in that instance, notifying the user
	 * that authorisation functions are not available.
	 * 
	 * @param postData
	 * @return
	 */
	protected String sendPost(String postData) {
		if (lastTry != null) {
			// We cannot currently access the remote server.
			Date now = new Date();
			if ((now.getTime() - lastTry.getTime()) > (10 * 60 * 1000)) {
				/*
				 * It has been 10 mins since we last tried to contact the server
				 * - try again
				 */
				lastTry = null;
			} else {
				// Just say yes for now
				log.error("Still unable to access the auth server!");
				return "true";
			}
		}

		this.postData = postData;
		try {
			System.out.println("Able to try to post to remote server");
			log.debug("About to post to the auth server");
			sendPost();
			return getResult();
		} catch (Exception e) {
			System.out.println("Auth server not contactable!");
			lastTry = new Date();
			log.error("Unable to access the auth server!");
			return "true";
		}
	}

	/**
	 * Post the data set in variable "postData" to the web service defined in the
	 * constructor
	 * 
	 * @throws IOException
	 */
	private void sendPost() throws Exception {
		log.debug("sendPost");
		connection = url.openConnection();
		connection.setDoOutput(true);
		out = new OutputStreamWriter(connection.getOutputStream());
		out.write(postData);
		out.flush();
		out.close();
		log.debug("sendPost:return");
	}
	
	
	

	/**
	 * Add the result from the servlet to a String with newlines
	 * 
	 * @return the output from the web servlet as a String
	 * @throws IOException
	 */
	private String getResult() throws Exception {
		BufferedReader in = null;
		String result = "";

		in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

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
				// String.format("name=freddy%d&password=bibble%d&anotherField=oh no larry%d",
				// new Random().nextInt(99999), new Random().nextInt(99999), new
				// Random().nextInt(99999))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
