package uk.ac.ox.oucs.iam.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import uk.ac.ox.oucs.iam.audit.IamAudit;
import uk.ac.ox.oucs.iam.interfaces.security.ReceivePostedData;
import uk.ac.ox.oucs.iam.interfaces.security.SecurePostData;
import uk.ac.ox.oucs.iam.interfaces.security.SignatureGenerator;
import uk.ac.ox.oucs.iam.interfaces.security.SignatureVerifier;
import uk.ac.ox.oucs.iam.interfaces.security.keys.KeyServices;
import uk.ac.ox.oucs.iam.interfaces.utilities.SystemVars;
import uk.ac.ox.oucs.iam.security.utilities.GeneralUtils;

/**
 * Receive a secure post from a client, check whether the message is authorised
 * and authentic and then ping the required recipient with a message enabling
 * them to obtain details of the message.
 * 
 * @author dave
 * 
 */
@SuppressWarnings("serial")
public class ReceivePost extends HttpServlet implements Serializable {
	private static Logger log = Logger.getLogger(ReceivePost.class);
	private PrintWriter out;
	public String keyFileName = "";
	public String keyDir;
	private SecurePostData securePostData;
	private List<SecurePostData> securePostDataList = new ArrayList<SecurePostData>();
	private boolean clearStack;
	private IamAudit auditer = new IamAudit();
	private boolean dontAcceptGet = false; // Set this to true if HTTP GET
											// requests are not allowed on this
											// server

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("doGet");
		if (dontAcceptGet) {
			return;
		}

		out = response.getWriter();

		newCheckRequest(request);

		// out.flush();
		out.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("doPost");
		out = response.getWriter();

		newCheckRequest(request);

		// out.flush();
		out.close();
	}

	/**
	 * Method that looks at the POST data sent to the application server. It
	 * breaks down all the parameters, validates the encrypted data contained
	 * within and builds a SecurePostData object with that data, adding it to an
	 * internal list for local query.
	 * 
	 * If the post command consists of REQUEST_DATA_CODE then data held is
	 * provided to the caller and then cleared from memory If the post command
	 * consists of REQUEST_DATA_CODE_DONT_CLEAR_STACK then data held is provided
	 * to the caller but NOT cleared from memory
	 * 
	 * @param request
	 */
	private void newCheckRequest(HttpServletRequest request) {
		log.debug("newCheckRequest is here ...");
		

		String hostId = request.getRemoteAddr();
		securePostData = new SecurePostData();
		String messageToVerify = "";
		String timestamp = "";
		securePostData.setOriginatorHost(hostId);
		auditer.auditSometimes("Post request received from " + hostId);

		log.debug("Checking parms");
		if (SystemVars.useMysql) {
			log.debug("MySql in use");
			
			String pubKeyUuid = request.getParameter(SystemVars.POST_COMMAND_PROVIDE_UUID_OF_PUBLIC_KEY);
			if ((pubKeyUuid != null) && (pubKeyUuid.length() != 0)) {
				log.info("Message to provide public key for remote host");
//				keyFileName = request.getParameter(SignatureGenerator.KEYFILE_POST_ATTRIBUTE);
//				log.debug("1");
//				if ((keyFileName == null) || (keyFileName.length() == 0)) {
//					log.error("Bad input - no key file name");
//					return;
//				}log.debug("1");
				if ((hostId == null) || (hostId.length() == 0)) {
					log.error("Bad input - no sending ip");
					return;
				}
				String publicKey = request.getParameter(SystemVars.POST_COMMAND_PROVIDE_PUBLIC_KEY);
				if ((publicKey == null) || (publicKey.length() == 0)) {
					log.error("Bad input - no public key");
					return;
				}
				
				uk.ac.ox.oucs.iam.servlet.model.IamPublicKey keytest = new uk.ac.ox.oucs.iam.servlet.model.IamPublicKey();
				keytest.setActualKey("WW");
				keytest.setOwnerIp("WWW");
				keytest.setUuid("QQQ");

				log.debug("Adding test key to database");
				AccessDB.create(keytest); // Add the key to the database
				log.debug("Test key added");

				// We have a new public key. Add to the db.
				uk.ac.ox.oucs.iam.servlet.model.IamPublicKey key = new uk.ac.ox.oucs.iam.servlet.model.IamPublicKey();
				key.setActualKey(publicKey);
				key.setOwnerIp(hostId);
				key.setUuid(pubKeyUuid);

				log.debug("Adding key to database");
				AccessDB.create(key); // Add the key to the database
				log.debug("Key added");
				return;
			}
		}

		Map params = request.getParameterMap();
		Iterator i = params.keySet().iterator();
		String[] messages = new String[request.getParameterMap().size()];
		int counter = 0;

		if (log.isDebugEnabled()) {
			log.debug("Total number of parameters passed in:" + request.getParameterMap().size());
		}

		// Loop through all parameters
		while (i.hasNext()) {
			String key = (String) i.next();
			String value = ((String[]) params.get(key))[0];
			log.debug(key + "=" + value);

			String dataRequestor = key + "=" + value;
			if (dataRequestor.compareTo(ReceivePostedData.REQUEST_DATA_CODE) == 0) {
				auditer.auditAlways("Request to gather all data and then clear the stack");
				clearStack = true;
				manipulateSecurePostDataList(null);
				return;
			}
			else if (dataRequestor.compareTo(ReceivePostedData.REQUEST_DATA_CODE_DONT_CLEAR_STACK) == 0) {
				auditer.auditAlways("Request to gather all data but not clear the stack");
				clearStack = false;
				manipulateSecurePostDataList(null);
				return;
			}

			/*
			 * This message is not a signature. We should therefore add it to
			 * the data we used to generate the signature, unless it is a
			 * timestamp In which case we need to extract it
			 */
			if (key.compareTo(SignatureGenerator.TIMESTAMP_POST_ATTRIBUTE) == 0) {
				/*
				 * A timestamp has been passed with the message. We can use this
				 * to check the message is not too old.
				 */
				timestamp = value;
			}
			else if (key.compareTo(SignatureGenerator.KEYFILE_POST_ATTRIBUTE) == 0) {
				/*
				 * This is the uuid name of the private key used to produce the
				 * digital signature of the message. The corresponding public
				 * key should be local to this service, and use of that should
				 * verify the validity of the signature.
				 */
				keyFileName = value;
				auditer.auditSometimes(String.format("Using key %s for host %s", keyFileName, hostId));
			}
			else if (key.compareTo(SignatureGenerator.DEST_IP) == 0) {
				/*
				 * The IP of the machine which this message is intended for
				 */
				String destIp = value;
				securePostData.setIntendedDestination(destIp);
				auditer.auditSometimes(String.format("Destination IP is %s", destIp));
			}
			else if (key.compareTo(SignatureGenerator.SIGNATURE_POST_ATTRIBUTE) != 0) {
				/*
				 * Actual post data for the intended recipient
				 */
				messages[counter] = key + "=" + value;
				securePostData.addPostParm(key, messages[counter]);
				if (log.isDebugEnabled()) {
					log.debug("Adding message:" + messages[counter]);
				}
				counter++;
			}
		}

		/*
		 * Now we have built up a list of messages intended for the recipient.
		 * We can look at the digital signature associated with the message and
		 * validate it. Since we cannot decrypt the digital signature (we only
		 * have the public key here, not the private key), all we can do is
		 * encrypt the key again and check that the signature is ok.
		 */
		log.debug("Initial data parse finished. Now look at the signature ...");
		i = params.keySet().iterator();
		while (i.hasNext()) {
			/*
			 * We loop through each parameter until we get the signature.
			 */
			String key = (String) i.next();
			String value = ((String[]) params.get(key))[0];
			if (key.compareTo(SignatureGenerator.SIGNATURE_POST_ATTRIBUTE) == 0) {
				/*
				 * A digital signature has been sent, which is expected. Verify
				 * this. How we do so depends on if the key is located in a file
				 * in the file system or database
				 */

				/*
				 * When the message is POSTed here, the signature is generated
				 * from that message. Thus we need to build the message and
				 * check that the expected signature is present.
				 * 
				 * Remember, the sender generates the signature using their
				 * private key. The receiver (here) generates the signature
				 * using the sender's public key. If the sigs are the same, we
				 * know the message has not been tampered with (since the sig is
				 * generated using the message) and, since we have used the
				 * sender's public key to generate the sig, we can guarantee
				 * that the message did indeed come from the sender.
				 */
				securePostData.setNoPrivateKey(true);
				SignatureVerifier sigVerifier = null;
				if (SystemVars.useMysql) {
					uk.ac.ox.oucs.iam.servlet.model.IamPublicKey localKey = AccessDB.getKey(keyFileName);
					if (localKey != null) {
						/*
						 * Now let's extract the key from the database. The key
						 * itself will be URL encoded as it was for transit, so
						 * we need to decode it.
						 * 
						 * When the key is sent, we 
						 * 1. encode base64 
						 * 2. url encode
						 * 
						 * Thus to get the key back we
						 * 
						 * 2. url decode 
						 * 2. decode base 64
						 */
						String urlDecodedKey;
						try {
							urlDecodedKey = URLDecoder.decode(localKey.getActualKey(), "UTF-8");
							byte[] keyArray = Base64.decodeBase64(urlDecodedKey);
							sigVerifier = new SignatureVerifier(keyArray);
							securePostData.setNoPrivateKey(false);
						} catch (Exception e) {
							log.debug("Exception trying to verify the data:" + e.getMessage());
							auditer.auditAlways("Bad signature:" + getAllCallerDetails(request));
							securePostData.setBadSig(true);
						}
					}
					else {
						log.error("No public key available");
					}
				}
				else {
					try {
						keyDir = GeneralUtils.provideKeyPairDirectory();
						File publicKey = new File(keyDir + keyFileName + KeyServices.publicKeyNameExtension);
						if (!publicKey.exists()) {
							log.error("Unable to get at the public key:" + publicKey.getAbsolutePath());
						}
						else {
							if (log.isDebugEnabled()) {
								log.debug("Using public key:" + publicKey.getAbsolutePath());
							}
							securePostData.setNoPrivateKey(false);
							sigVerifier = new SignatureVerifier(keyDir + keyFileName);
						}
					} catch (Exception e) {
						log.debug("Exception trying to verify the data:" + e.getMessage());
						auditer.auditAlways("Bad signature:" + getAllCallerDetails(request));
						securePostData.setBadSig(true);
					}
				}
				if (sigVerifier != null) {
					log.debug("About to process data with public key");
					for (int index = counter - 1; index > -1; index--) {
						messageToVerify += messages[index];
						if (index != 0) {
							messageToVerify += "&";
						}
					}
					/*
					 * In order to verify the signature is correct, we need to
					 * generate it in exactly the same way as it was generated
					 * by the originator, and thus clearly the order of
					 * parameters is crucial. By convention, We sort the
					 * parameters alphabetically.
					 */
					log.debug("About to split " + messageToVerify);
					String[] dataToSort = messageToVerify.split("&");
					Arrays.sort(dataToSort);

					if (log.isDebugEnabled()) {
						log.debug(String.format("Split into %d items", dataToSort.length));
						for (String s : dataToSort) {
							log.debug(s);
						}
					}

					log.debug("Data has been sorted");
					messageToVerify = uk.ac.ox.oucs.iam.interfaces.utilities.GeneralUtils
							.reconstructSortedData(dataToSort);
					log.debug("Data has been reconstructed");
					log.debug(messageToVerify);
					String signature = value;

					if (log.isDebugEnabled()) {
						log.debug(String.format("About to verify message <%s> using signature <%s>", messageToVerify,
								signature));
						log.debug(String.format("Keyfile to be used is " + keyDir + keyFileName));
						log.debug(String.format("Sig is " + signature));
					}
					byte[] decodedBytes = sigVerifier.decodeAsByteArray(signature);

					if (log.isDebugEnabled()) {
						log.debug(String.format("Decoded length:%s", decodedBytes.length));
						log.debug(String.format("Decoded bytes length:%d (%d,%d,%d)", decodedBytes.length,
								decodedBytes[4], decodedBytes[27], decodedBytes[43]));
						log.debug("Verify:" + messageToVerify);
					}

					if ((timestamp == null) || (timestamp.length() == 0)) {
						log.warn("Verify with no timestamp");
						try {
							securePostData.setMessageHasBeenVerified(sigVerifier.verifyDigitalSignature(decodedBytes,
									messageToVerify));
						} catch (GeneralSecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else {
						log.debug("Verify with timestamp");
						try {
							securePostData.setMessageHasBeenVerified(sigVerifier.verifyDigitalSignature(decodedBytes,
									messageToVerify + "_" + timestamp));
						} catch (GeneralSecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						boolean verified = !sigVerifier.verifyTimestamp(timestamp);
						if (log.isDebugEnabled()) {
							log.debug(String.format("Timestamp is %stoo old", verified ? "" : "not "));
						}

						securePostData.setMessageTimedOut(verified);
					}
				}

				if (securePostData.isMessageHasBeenVerified() && !securePostData.isMessageTimedOut()) {
					securePostData.setMessageHasBeenVerified(true);
					String validatedMessage = String.format(
							"Post request from %s validated with the following parameters:\n", hostId);
					for (String s : securePostData.getPostParms().values()) {
						validatedMessage += "\t" + s + "\n";
					}
					auditer.auditSometimes(validatedMessage);
					log.debug(validatedMessage);
				}
				else {
					auditer.auditAlways("Message has not been verified");
					if (securePostData.isMessageTimedOut()) {
						auditer.auditAlways("The message is considered too old:" + getAllCallerDetails(request));
						securePostData.setMessageTimedOut(true);
						log.debug("Message too old");
					}
					if (!securePostData.isMessageHasBeenVerified()) {
						auditer.auditAlways("Bad verification:" + getAllCallerDetails(request));
						securePostData.setBadSig(true);
						log.debug("Bad verification");
					}
				}

				manipulateSecurePostDataList(securePostData);
				if (log.isDebugEnabled()) {
					if (securePostData.isMessageHasBeenVerified()) {
						log.debug("Message has been verified");
					}
					else {
						log.debug("Message has NOT been verified");
						log.debug("Bad signature:" + securePostData.isBadSig());
						log.debug("Timeout:" + securePostData.isMessageTimedOut());
						log.debug("Priv key:" + securePostData.isNoPrivateKey());
					}

					log.debug("About to send notification");
				}

				/*
				 * We can now send a little tickle to the intended recipient
				 * telling them that there is data waiting for them
				 */

				try {
					boolean useApacheHttpPost = true;
					String result = "";
					if (useApacheHttpPost) {
						log.debug("About to send post request to " + securePostData.getIntendedDestination());
						result = uk.ac.ox.oucs.iam.interfaces.utilities.GeneralUtils.sendStandardHttpPost(
								securePostData.getIntendedDestination(), SystemVars.POST_COMMAND_COMMAND_TOKEN,
								SystemVars.POST_COMMAND_NEW_DATA_AVAILABLE);
						if (log.isDebugEnabled()) {
							log.debug("Got result: " + result);
						}
					}
					else {
						URL url = new URL(securePostData.getIntendedDestination());
						URLConnection connection = url.openConnection();
						if (log.isDebugEnabled()) {
							log.debug(String.format("About to post to %s", securePostData.getIntendedDestination()));
							log.debug(String.format("Will use the following parms: %s=%s",
									SystemVars.POST_COMMAND_COMMAND_TOKEN, SystemVars.POST_COMMAND_NEW_DATA_AVAILABLE));
						}
						connection.setDoOutput(true);
						String dataToSend = String.format("%s=%s", SystemVars.POST_COMMAND_COMMAND_TOKEN,
								SystemVars.POST_COMMAND_NEW_DATA_AVAILABLE);
						OutputStreamWriter outsw = new OutputStreamWriter(connection.getOutputStream());
						outsw.write(dataToSend);
						outsw.flush();
						outsw.close();

						BufferedReader in = null;

						in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

						String decodedString;

						while ((decodedString = in.readLine()) != null) {
							result += decodedString + "\n";
							log.debug(result);
						}
						in.close();
					}

					log.debug("Message posted");
				} catch (Exception ex) {
					log.error("Exception trying to notify " + securePostData.getIntendedDestination());
					log.error(ex);
				}

				/*
				 * We don't need to stay in the loop any longer since we have
				 * processed all data now.
				 */
				break;
			}
		}
	}

	private String getAllCallerDetails(HttpServletRequest request) {
		return String.format("Remote host:%s, Referer:%s, remoteHost:%s, user agent:%s", request.getRemoteAddr(),
				request.getHeader("referer"), request.getHeader("host"), request.getHeader("user-agent"));
	}

	/**
	 * Do some work on the list of secure data items.
	 * 
	 * @param dataItem
	 *            If null, print the data to the PrintWriter (so that it may be
	 *            collected by the caller). If not null then add the item to the
	 *            list of data items for later collection.
	 */
	private synchronized void manipulateSecurePostDataList(SecurePostData dataItem) {
		log.debug("manipulateSecurePostDataList");

		if (dataItem == null) {
			if (log.isDebugEnabled()) {
				log.debug("manipulateSecurePostDataList - collect items");
				log.debug(String.format("We currently have %d items", securePostDataList.size()));
			}
			/*
			 * The user has requested all current data - let's give it to them
			 * and then potentially clear the list
			 */
			for (SecurePostData spd : securePostDataList) {
				spd.printData(out);
			}
			if (clearStack) {
				log.info("List being cleared");
				securePostDataList.clear();
			}
		}
		else {
			securePostDataList.add(dataItem);
			if (log.isDebugEnabled()) {
				log.debug("manipulateSecurePostDataList - add item");
				log.debug(String.format("We now have %d items", securePostDataList.size()));
			}
		}
	}

	public static void main(String[] args) throws IOException {
		URL url = new URL("http://129.67.103.124:8081/iam/ProjectRoleServlet");
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		OutputStreamWriter outsw = new OutputStreamWriter(connection.getOutputStream());
		outsw.write(String.format("getRoles=true"));
		outsw.flush();
		BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			System.out.println(line);
		}
		rd.close();
		outsw.close();
		System.out.println("Done");
	}
}
