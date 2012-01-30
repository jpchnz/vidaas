package uk.ac.ox.oucs.iam.postsecurely;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


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
	private String webAppName = "http://localhost:8080/iam/ReceivePost";
	
	
	@Test
	public void test1() throws Exception {
		System.out.println("test1");
		SendViaPost post = new SendViaPost();
		post.sendPost(webAppName, "name=freddy&password=bibble");
		List<SecurePostData> securePostDataList = null;
		int counter = 10;
		while ( (securePostDataList == null) && (counter != 0) ) {
			Thread.sleep(1000);
			securePostDataList = ReceivePostedData.getPendingMessageData(webAppName);
			counter--;
		}
		assertTrue(counter > 0); // Timeout waiting for data
		assertFalse(securePostDataList == null);
		assertTrue(securePostDataList.size()==1);
		assertTrue(securePostDataList.get(0).isMessageHasBeenVerified());
		assertFalse(securePostDataList.get(0).isBadSig());
		assertFalse(securePostDataList.get(0).isMessageTimeout());
		assertFalse(securePostDataList.get(0).isNoPrivateKey());
		assertTrue(securePostDataList.get(0).getPostParms().size() == 2);
		assertTrue(securePostDataList.get(0).getPostParms().get(0).compareTo("password=bibble") == 0);
		assertTrue(securePostDataList.get(0).getPostParms().get(1).compareTo("name=freddy") == 0);
	}
	
	
	
	@Test
	public void test2() throws Exception {
		System.out.println("test2");
		SendViaPost post = new SendViaPost();
		post.sendPost(webAppName, "name=Another name&password=This Is My Password123");
		post.sendPost(webAppName, "name=Another name&password=This Is My Password123");
		List<SecurePostData> securePostDataList = null;
		int counter = 10;
		while ( (securePostDataList == null) && (counter != 0) ) {
			Thread.sleep(1000);
			securePostDataList = ReceivePostedData.getPendingMessageData(webAppName);
			counter--;
		}
		assertTrue(counter > 0); // Timeout waiting for data
		assertFalse(securePostDataList == null);
		assertTrue(securePostDataList.size()==2);
		assertTrue(securePostDataList.get(0).isMessageHasBeenVerified());
		assertFalse(securePostDataList.get(0).isBadSig());
		assertFalse(securePostDataList.get(0).isMessageTimeout());
		assertFalse(securePostDataList.get(0).isNoPrivateKey());
		assertTrue(securePostDataList.get(0).getPostParms().size() == 2);
		assertTrue(securePostDataList.get(0).getPostParms().get(0).compareTo("password=This Is My Password123") == 0);
		assertTrue(securePostDataList.get(0).getPostParms().get(1).compareTo("name=Another name") == 0);
	}
}
