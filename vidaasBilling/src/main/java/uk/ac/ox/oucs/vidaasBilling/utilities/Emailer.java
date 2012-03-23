package uk.ac.ox.oucs.vidaasBilling.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class Emailer {
	private static Logger log = Logger.getLogger(Emailer.class);

	private static Properties fMailServerConfig = new Properties();

	static {
		fetchConfig();
	}

	/**
	 * Send a single email.
	 */
	public String sendEmail(String aFromEmailAddr, String aToEmailAddr,
			String aSubject, String aBody) {
		log.debug("Send email ...");
		// Here, no Authenticator argument is used (it is null).
		// Authenticators are used to prompt the user for user
		// name and password.
		String ret2 = "";
		// Session session = Session.getDefaultInstance(fMailServerConfig,
		// null);
		Session session = Session.getInstance(
				fMailServerConfig,
				new GMailAuthenticator(fMailServerConfig
						.getProperty("mail.from"), fMailServerConfig
						.getProperty("mail.password")));
		MimeMessage message = new MimeMessage(session);
		try {
			// the "from" address may be set in code, or set in the
			// config file under "mail.from" ; here, the latter style is used
			// message.setFrom( new InternetAddress(aFromEmailAddr) );
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					aToEmailAddr));
			message.setSubject(aSubject);
			message.setText(aBody);
			Transport.send(message);
		} catch (MessagingException ex) {
			log.error("Cannot send email. " + ex);
			ret2 = ex.toString();
		}
		
		log.debug("Returning ...");

		return ret2;
	}

	/**
	 * Allows the config to be refreshed at runtime, instead of requiring a
	 * restart.
	 */
	public static void refreshConfig() {
		fMailServerConfig.clear();
		fetchConfig();
	}

	/**
	 * Open a specific text file containing mail server parameters, and populate
	 * a corresponding Properties object.
	 */
	private static void fetchConfig() {
		InputStream input = null;
		try {
			// If possible, one should try to avoid hard-coding a path in this
			// manner; in a web application, one should place such a file in
			// WEB-INF, and access it using ServletContext.getResourceAsStream.
			// Another alternative is Class.getResourceAsStream.
			// This file contains the javax.mail config properties mentioned
			// above.inputStream =
			// Protection.class.getClassLoader().getResourceAsStream("pwFile");
			input = Emailer.class.getClassLoader().getResourceAsStream(
					"Mail.Properties");
			fMailServerConfig.load(input);
		} catch (IOException ex) {
			System.err
					.println("Cannot open and load mail server properties file.");
		} finally {
			try {
				if (input != null)
					input.close();
			} catch (IOException ex) {
				System.err.println("Cannot close mail server properties file.");
			}
		}
	}
}
