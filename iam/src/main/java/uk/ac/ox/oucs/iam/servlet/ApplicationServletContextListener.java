package uk.ac.ox.oucs.iam.servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.xml.DOMConfigurator;

import uk.ac.ox.oucs.iam.security.keys.KeyServices;
import uk.ac.ox.oucs.iam.security.utilities.GeneralUtils;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.DuplicateKeyException;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.KeyNotFoundException;

public class ApplicationServletContextListener implements ServletContextListener {
	public void contextInitialized(ServletContextEvent event) {
		/*
		 * TODO
		 * Send a ping to the manager node so that it knows where to send public keys to.
		 */
		prepareAndSendPublicKey();
		
		
		/*
		 * Now to do servlet stuff
		 */
		ServletContext ctx = event.getServletContext();

		String prefix = ctx.getRealPath("/");
		String file = "WEB-INF" + System.getProperty("file.separator") + "classes"
				+ System.getProperty("file.separator") + "log4j.xml";

		DOMConfigurator.configure(prefix + file);
	}
	
	private void prepareAndSendPublicKey() {
		String publicKey;
		String hostName = GeneralUtils.getLocalHostname();
		String ip = GeneralUtils.getLocalIPAddress();
		String ipAddressOfManager = "manager"; // TODO This should be via a configuration file
		String dataToPost = null;
		
		try {
			publicKey = GeneralUtils.provideBaseKeyPairName() + KeyServices.publicKeyNameExtension;
			dataToPost = String.format("%s=%s&%s=%s&%s=%s&%s=%s",
					KeyUtilitiesServlet.HELLO_WORLD_ATTRIBUTE, ip,
					KeyUtilitiesServlet.HELLO_WORLD_HOSTNAME_ATTRIBUTE, hostName,
					KeyUtilitiesServlet.HELLO_WORLD_PUBLICKEYNAME_ATTRIBUTE, new File(GeneralUtils.provideBaseKeyPairName()).getName(),
					KeyUtilitiesServlet.HELLO_WORLD_PUBLICKEY_ATTRIBUTE, GeneralUtils.readFileAsString(publicKey)); // TODO - needs encoding
			System.out.println(publicKey);
			URL url = new URL("http://" + ipAddressOfManager + ":8081/iam/KeyUtilitiesServlet");
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			OutputStreamWriter outputSW = new OutputStreamWriter(connection.getOutputStream());
			System.out.println(dataToPost);
			outputSW.write(dataToPost);
			outputSW.flush();
			outputSW.close();
		}
		catch (DuplicateKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (KeyNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnknownHostException e) {
			System.out.println("It is likely this part of the code has not yet been written!");
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(dataToPost);
	}

	public void contextDestroyed(ServletContextEvent event) {
	}

}