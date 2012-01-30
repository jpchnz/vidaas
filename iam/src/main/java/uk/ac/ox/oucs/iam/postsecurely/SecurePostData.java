package uk.ac.ox.oucs.iam.postsecurely;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * Helper class contains a list of a POST message that has been sent to the web server 
 * and the status of its verification
 * 
 * @author oucs0153
 *
 */
public class SecurePostData {
	private boolean messageHasBeenVerified = false;
	private boolean messageTimedOut = false;
	private boolean badSig = false;
	private boolean noPrivateKey = false;
	private String originatorHost = null;
	private List<String> postParms = new ArrayList<String>();
	private static String token1 = "Printing data for SecurePostData";
	private static String token2 = "Verified:";
	private static String token3 = "Timeout:";
	private static String token4 = "Bad sig:";
	private static String token5 = "No key:";
	
		
	public void printData(PrintWriter out) {
		out.println(token1);
		out.println(token2 + messageHasBeenVerified);
		out.println(token3 + messageTimedOut);
		out.println(token4 + badSig);
		out.println(token5 + noPrivateKey);
		for (String s : postParms) {
			out.println(s);
		}
	}
	
	public static List<SecurePostData> getObjectFromString(String dataString) {
		if ( (dataString == null) || (dataString.length() == 0) ) {
			return null;
		}
		
		SecurePostData spd = null;
		List<SecurePostData> spdList = new ArrayList<SecurePostData>();

		for (String s : dataString.split("\n")) {
			if (s.equals(token1)) {
				if (spd != null) {
					spdList.add(spd);
				}
				spd = new SecurePostData();
			}
			else if (s.startsWith(token2)) {
				spd.setMessageHasBeenVerified(s.substring(token2.length()).equals("true"));
			}
			else if (s.startsWith(token3)) {
				spd.setMessageTimedOut(s.substring(token3.length()).equals("true"));
			}
			else if (s.startsWith(token4)) {
				spd.setBadSig(s.substring(token4.length()).equals("true"));
			}
			else if (s.startsWith(token5)) {
				spd.setNoPrivateKey(s.substring(token5.length()).equals("true"));
			}
			else {
				spd.addPostParm(s);
			}
		}
		
		if (spd != null) {
			spdList.add(spd);
		}
		
		return spdList;
	}
	
	
	public void setMessageHasBeenVerified(boolean messageHasBeenVerified) {
		this.messageHasBeenVerified = messageHasBeenVerified;
	}
	public void addPostParm(String postParm) {
		postParms.add(postParm);
	}
	public void setMessageTimedOut(boolean messageTimedOut) {
		this.messageTimedOut = messageTimedOut;
	}
	public void setBadSig(boolean badSig) {
		this.badSig = badSig;
	}
	public void setNoPrivateKey(boolean noPrivateKey) {
		this.noPrivateKey = noPrivateKey;
	}


	public boolean isMessageHasBeenVerified() {
		return messageHasBeenVerified;
	}


	public boolean isMessageTimedOut() {
		return messageTimedOut;
	}


	public boolean isBadSig() {
		return badSig;
	}


	public boolean isNoPrivateKey() {
		return noPrivateKey;
	}


	public void setPostParms(List<String> postParms) {
		this.postParms = postParms;
	}
	
	public List<String> getPostParms() {
		return postParms;
	}

	public String getOriginatorHost() {
		return originatorHost;
	}

	public void setOriginatorHost(String originatorHost) {
		this.originatorHost = originatorHost;
	}
}
