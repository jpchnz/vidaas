package uk.ac.ox.oucs.iam.postsecurely;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import uk.ac.ox.oucs.iam.GlobalTestVars;


/**
 * Testing the sending and receiving of encrypted data. Note that for this to work, the relevant
 * web server must be running the iam.war code.
 * 
 * TODO
 * Add more tests to check timeouts, no key issues, etc
 * @author dave
 *
 */
public class SendAndReceiveTest {
	private String webAppName = "http://localhost:8081/iam/ReceivePost";
	private boolean webServerNotRunning = false;
	
	
	@Test
	public void test1() throws Exception {
		if (!GlobalTestVars.runTests) {
			return;
		}
		if (webServerNotRunning) {
			return;
		}
		System.out.println("test1");
		
		System.out.println("\tcheck send data ...");
		SendViaPost post = new SendViaPost();
		
		
		/*
		 * First we send some data to the web server ...
		 */
		String postedData = post.sendPost(webAppName, "name=freddy&password=bibble");
		assertFalse(postedData == null);
		System.out.println("\tData posted <" + postedData + ">");
		
		
		/*
		 * Now try to receive it
		 */
		List<SecurePostData> securePostDataList = null;
		System.out.println("\tcheck receive data ...");
		int counter = 10;
		while ( (securePostDataList == null) && (counter != 0) ) {
			Thread.sleep(1000); // We need to wait a second or so to ensure the data has been received and processed ...
			securePostDataList = ReceivePostedData.getPendingMessageData(webAppName);
			counter--;
		}
		assertTrue(counter > 0); // Timeout waiting for data
		System.out.println("\tTotal extra wait was " + (9-counter) + " seconds");
		assertFalse(securePostDataList == null);
		assertTrue(securePostDataList.size()==1);
		assertTrue(securePostDataList.get(0).isMessageHasBeenVerified());
		assertFalse(securePostDataList.get(0).isBadSig());
		assertFalse(securePostDataList.get(0).isMessageTimedOut());
		assertFalse(securePostDataList.get(0).isNoPrivateKey());
		assertTrue(securePostDataList.get(0).getPostParms().size() == 2);
		assertTrue(securePostDataList.get(0).getPostParms().get(0).compareTo("password=bibble") == 0);
		assertTrue(securePostDataList.get(0).getPostParms().get(1).compareTo("name=freddy") == 0);
		System.out.println("\tData returned and understood successfully");
	}
	
	
	
	@Test
	public void test2() throws Exception {
		if (!GlobalTestVars.runTests) {
			return;
		}
		if (webServerNotRunning) {
			return;
		}
		System.out.println("test2");
		
		System.out.println("\tcheck send data ...");
		SendViaPost post = new SendViaPost();
		String postedData = post.sendPost(webAppName, "name=Another name is here&password=My Secret Passwordzzz123");
		assertFalse(postedData == null);
		System.out.println("\tData posted <" + postedData + ">");
		postedData = post.sendPost(webAppName, "name=Another name&password=This Is My Password123");
		assertFalse(postedData == null);
		System.out.println("\tData posted <" + postedData + ">");
		List<SecurePostData> securePostDataList = null;
		System.out.println("\tcheck receive data ...");
		int counter = 10;
		while ( (securePostDataList == null) && (counter != 0) ) {
			Thread.sleep(1000);
			securePostDataList = ReceivePostedData.getPendingMessageData(webAppName);
			counter--;
		}
		assertTrue(counter > 0); // Timeout waiting for data
		System.out.println("\tTotal extra wait was " + (9-counter) + " seconds");
		assertFalse(securePostDataList == null);
		assertTrue(securePostDataList.size()==2);
		assertTrue(securePostDataList.get(0).isMessageHasBeenVerified());
		assertFalse(securePostDataList.get(0).isBadSig());
		assertFalse(securePostDataList.get(0).isMessageTimedOut());
		assertFalse(securePostDataList.get(0).isNoPrivateKey());
		assertTrue(securePostDataList.get(0).getPostParms().size() == 2);
		assertTrue(securePostDataList.get(0).getPostParms().get(0).compareTo("password=My Secret Passwordzzz123") == 0);
		assertTrue(securePostDataList.get(0).getPostParms().get(1).compareTo("name=Another name is here") == 0);
		assertTrue(securePostDataList.get(1).getPostParms().get(0).compareTo("password=This Is My Password123") == 0);
		assertTrue(securePostDataList.get(1).getPostParms().get(1).compareTo("name=Another name") == 0);
		System.out.println("\tData returned and understood successfully");
	}
}
