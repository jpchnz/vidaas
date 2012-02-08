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

import uk.ac.ox.oucs.iam.postsecurely.SendViaPost;
import uk.ac.ox.oucs.iam.security.keys.KeyServices;
import uk.ac.ox.oucs.iam.security.utilities.GeneralUtils;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.DuplicateKeyException;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.KeyNotFoundException;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.NewKeyException;

public class ApplicationServletContextListener implements ServletContextListener {
	public void contextInitialized(ServletContextEvent event) {
		/*
		 * TODO
		 * Send a ping to the manager node so that it knows where to send public keys to.
		 */
		//prepareAndSendPublicKey(true); // Set to false to actually send the text
		
		
		/*
		 * Now to do servlet stuff
		 */
		ServletContext ctx = event.getServletContext();

		String prefix = ctx.getRealPath("/");
		String file = "WEB-INF" + System.getProperty("file.separator") + "classes"
				+ System.getProperty("file.separator") + "log4j.xml";

		DOMConfigurator.configure(prefix + file);
	}
	
	private String prepareAndSendPublicKey(boolean prepareStringOnly) {
		String publicKey;
		String hostName = GeneralUtils.getLocalHostname();
		String ip = GeneralUtils.getLocalIPAddress();
		String ipAddressOfManager = "localhost"; // TODO This should be via a configuration file
		String dataToPost = null;
		
		try {
			publicKey = GeneralUtils.provideBaseKeyPairName() + KeyServices.publicKeyNameExtension;
			dataToPost = String.format("%s=%s&%s=%s&%s=%s&%s=%s",
					KeyUtilitiesServlet.HELLO_WORLD_ATTRIBUTE, ip,
					KeyUtilitiesServlet.HELLO_WORLD_HOSTNAME_ATTRIBUTE, hostName,
					KeyUtilitiesServlet.HELLO_WORLD_PUBLICKEYNAME_ATTRIBUTE, new File(GeneralUtils.provideBaseKeyPairName()).getName()+"xx",
					KeyUtilitiesServlet.HELLO_WORLD_PUBLICKEY_ATTRIBUTE, GeneralUtils.readPublicKeyFromFileAndEncode(publicKey)); // TODO - needs encoding
			System.out.println(dataToPost);
			if (prepareStringOnly) {
				return dataToPost;
			}
			URL url = new URL("http://" + ipAddressOfManager + ":8081/iam/KeyUtilitiesServlet");//http://localhost:8081/iam
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
		return dataToPost;
	}

	public void contextDestroyed(ServletContextEvent event) {
	}

	public static void main(String[] args) {
		ApplicationServletContextListener thisObject = new ApplicationServletContextListener();
		String postData = thisObject.prepareAndSendPublicKey(true);
		SendViaPost post = new SendViaPost();
		try {
			post.sendPost("http://129.67.103.124:8081/iam/KeyUtilitiesServlet", postData, false);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NewKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (KeyNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (DuplicateKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		 * Example output
		 * helloWorld=129.67.103.124&hostName=ethics-gradient&publicKeyName=eba95ef3-9fcd-44c2-88ba-d9f1f3172446&publicKey=MIIBuDCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYUAAoGBAJxjLg0EDP5u3fPbv/Y5dP4Dw5al6ig+FJbF5k++zaXT1RebFIR9zbUZG/Lt1Hh4ILL+PTqXlV7qnEZ9vzTltENEJvWZtuH3s3jMJ7CFgZyvv1L+1b64OGX7OUCvOPdB0wrQaKJG8b5Y50HClqiWDWily9RH7KDwn1GkyktlFWE7
		 */
	}
}