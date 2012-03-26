package uk.ac.ox.oucs.iam.interfaces.security;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;


/**
 * Helper class contains a list of a POST message that has been sent to the web server 
 * and the status of the message's verification
 * 
 * @author oucs0153
 *
 */
public class SecurePostData {
	private static Logger log = Logger.getLogger(SecurePostData.class);
	
	private boolean messageHasBeenVerified = false;
	private boolean messageTimedOut = false;
	private boolean badSig = false;
	private boolean noPrivateKey = false;
	private String intendedDestination = "";
	private String originatorHost = null;
//	private List<String> postParms = new ArrayList<String>();
	private Map<String, String> postParms = new ConcurrentHashMap<String, String>();
	private static String token_title_message = "Printing data for SecurePostData";
	private static String token_verified = "Verified:";
	private static String token_timeout = "Timeout:";
	private static String token_bad_sig = "Bad sig:";
	private static String token_no_key = "No key:";
	private static String token_originator_host = "Originator Host:";
	private static String token_destination_host = "Intended Destination Host:";
	
		
	public void printData(PrintWriter out) {
		if (log.isDebugEnabled()) {
			log.debug("printData");
			log.debug(token_title_message);
			log.debug(token_verified + messageHasBeenVerified);
			log.debug(token_timeout + messageTimedOut);
			log.debug(token_bad_sig + badSig);
			log.debug(token_no_key + noPrivateKey);
			log.debug(token_originator_host + originatorHost);
			log.debug(token_destination_host + intendedDestination);
			for (String s : postParms.values()) {
				log.debug("\t"+s);
			}
		}
		
		out.println(token_title_message);
		out.println(token_verified + messageHasBeenVerified);
		out.println(token_timeout + messageTimedOut);
		out.println(token_bad_sig + badSig);
		out.println(token_no_key + noPrivateKey);
		out.println(token_originator_host + originatorHost);
		out.println(token_destination_host + intendedDestination);
		for (String s : postParms.values()) {
			out.println(s);
		}
	}
	
	
	
	/**
	 * This method will look at the supplied data string and parse it into a list of SecurePostData objects that
	 * can then be easily queried. 
	 * @param dataString The new line separated data stream that required parsing
	 * @return A list of SecurePostData objects
	 */
	public static List<SecurePostData> getObjectFromString(String dataString) {
		if ( (dataString == null) || (dataString.length() == 0) ) {
			return null;
		}
		
		SecurePostData spd = null;
		List<SecurePostData> spdList = new ArrayList<SecurePostData>();

		for (String s : dataString.split("\n")) {
			if (s.equals(token_title_message)) {
				if (spd != null) {
					spdList.add(spd);
				}
				spd = new SecurePostData();
			}
			else {
				if (spd == null) {
					spd = new SecurePostData();
				}
				if (s.startsWith(token_verified)) {
					spd.setMessageHasBeenVerified(s.substring(token_verified.length()).equals("true"));
				}
				else if (s.startsWith(token_timeout)) {
					spd.setMessageTimedOut(s.substring(token_timeout.length()).equals("true"));
				}
				else if (s.startsWith(token_bad_sig)) {
					spd.setBadSig(s.substring(token_bad_sig.length()).equals("true"));
				}
				else if (s.startsWith(token_no_key)) {
					spd.setNoPrivateKey(s.substring(token_no_key.length()).equals("true"));
				}
				else if (s.startsWith(token_originator_host)) {
					spd.setOriginatorHost(s.substring(token_originator_host.length()));
				}
				else if (s.startsWith(token_destination_host)) {
					spd.setIntendedDestination(s.substring(token_destination_host.length()));
				}
				else {
					if (spd != null) {
						String[] components = s.split("=");
						spd.addPostParm(components[0], components[1]);
					}
				}
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
	public void addPostParm(String parm1, String parm2) {
		postParms.put(parm1, parm2);
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


	public void setPostParms(Map<String, String> postParms) {
		this.postParms = postParms;
	}
	
	public Map<String, String> getPostParms() {
		return postParms;
	}

	public String getOriginatorHost() {
		return originatorHost;
	}

	public void setOriginatorHost(String originatorHost) {
		this.originatorHost = originatorHost;
	}



	public String getIntendedDestination() {
		return intendedDestination;
	}



	public void setIntendedDestination(String intendedDestination) {
		this.intendedDestination = intendedDestination;
	}
}
