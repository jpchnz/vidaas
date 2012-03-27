package uk.ac.ox.oucs.iam.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import uk.ac.ox.oucs.iam.audit.IamAudit;
import uk.ac.ox.oucs.iam.interfaces.security.ReceivePostedData;
import uk.ac.ox.oucs.iam.interfaces.security.SecurePostData;
import uk.ac.ox.oucs.iam.interfaces.security.SignatureGenerator;
import uk.ac.ox.oucs.iam.interfaces.security.SignatureVerifier;
import uk.ac.ox.oucs.iam.interfaces.security.keys.KeyServices;
import uk.ac.ox.oucs.iam.interfaces.utilities.SystemVars;
import uk.ac.ox.oucs.iam.security.utilities.GeneralUtils;

@SuppressWarnings("serial")
public class ReceivePost extends HttpServlet {
	private static Logger log = Logger.getLogger(ReceivePost.class);
	private PrintWriter out;
	public String keyFile = "";
	public String keyDir;
	private SecurePostData securePostData;
	private List<SecurePostData> securePostDataList = new ArrayList<SecurePostData>();
	private boolean clearStack;
	private IamAudit auditer = new IamAudit();
	private boolean dontAcceptGet = false; // Set this to true if HTTP GET
											// requests are not allowed on this
											// server

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (dontAcceptGet) {
			return;
		}

		out = response.getWriter();

		checkRequest(request);

		out.flush();
		out.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		out = response.getWriter();

		checkRequest(request);

		out.flush();
		out.close();
	}

	/**
	 * Method that looks at the POST data sent to the application server. It
	 * breaks down all the parameters, validates the encrypted data contained
	 * within and builds a SecurePostData object with that data, adding it to an
	 * internal list for local query.
	 * 
	 * Important. The &sig parameter should be the very last parameter sent. So
	 * an example post could be
	 * ?name=fred&ts=1323857454692&sig=MCwCFGKn7ucmYGTiIti5
	 * %2B3QNOnjXSGbMAhRxAvm%2BelrlIvqrCm6LObd%2B5yC%2BSA%3D%3D
	 * 
	 * If the post command consists of REQUEST_DATA_CODE then data held is
	 * provided to the caller and then cleared from memory If the post command
	 * consists of REQUEST_DATA_CODE_DONT_CLEAR_STACK then data held is provided
	 * to the caller but NOT cleared from memory
	 * 
	 * @param request
	 */
	private void checkRequest(HttpServletRequest request) {
		log.debug("checkRequest");
		
		String messageToVerify = "";
		String timestamp = "";
		int counter = 0;
		securePostData = new SecurePostData();

		String hostId = request.getRemoteAddr();
		securePostData.setOriginatorHost(hostId);
		auditer.auditSometimes("Post request received from " + hostId);

		Enumeration<?> e = request.getParameterNames();
		String[] messages = new String[request.getParameterMap().size()];
		/*
		 * Understand and process each parameter
		 */
		while (e.hasMoreElements()) {
			String data = (String) e.nextElement();
			if (data != null) {
				log.debug(data + "=" + request.getParameter(data));

				if (data.compareTo(SignatureGenerator.SIGNATURE_POST_ATTRIBUTE) == 0) {
					/*
					 * A digital signature has been sent, which is expected.
					 * Verify this.
					 */
					try {
						keyDir = GeneralUtils.provideKeyPairDirectory();

						File publicKey = new File(keyDir + File.separator + keyFile
								+ KeyServices.publicKeyNameExtension);
						if (!publicKey.exists()) {
							log.debug("Unable to get at the public key");
							securePostData.setNoPrivateKey(true);
						}
						else {
							try {
								/*
								 * When the message is POSTed here, the
								 * signature is generated from that message.
								 * Thus we need to build the message and check
								 * that the expected signature is present.
								 * 
								 * Remember, the sender generates the signature
								 * using their private key. The receiver (here)
								 * generates the signature using the sender's
								 * public key. If the sigs are the same, we know
								 * the message has not been tampered with (since
								 * the sig is generated using the message) and,
								 * since we have used the sender's public key to
								 * generate the sig, we can guarantee that the
								 * message did indeed come from the sender.
								 */
//								for (int index = counter - 1; index > -1; index--) {
								for (int index = 0; index < counter; index++) {
									messageToVerify += messages[index];
//									if (index != 0) {
									if (index != (counter-1)) {
										messageToVerify += "&";
									}
								}

								log.debug("About to verify message...<" + messageToVerify + ">");
								String signature = request.getParameter(data);

								SignatureVerifier sigVerifier = new SignatureVerifier(keyDir + File.separator + keyFile);
								log.debug(keyDir + File.separator + keyFile);
								byte[] decodedBytes = sigVerifier.decodeAsByteArray(signature);
								//out.println(decodedBytes[0] +" "+ decodedBytes[10]+" "+ decodedBytes[20]);
								log.debug("Verify:" + messageToVerify);
								if (timestamp == "") {
									out.println("Verify with no timestamp");
									securePostData.setMessageHasBeenVerified(sigVerifier.verifyDigitalSignature(
											decodedBytes, messageToVerify));
								}
								else {
									log.debug("Verify with timestamp");
									securePostData.setMessageHasBeenVerified(sigVerifier.verifyDigitalSignature(
											decodedBytes, messageToVerify + "_" + timestamp));
									boolean verified = !sigVerifier.verifyTimestamp(timestamp);
									log.debug("Time stamp too old = " + verified);
									securePostData.setMessageTimedOut(verified);
									if (log.isDebugEnabled()) {
									log.debug("Let's check this:");
										Date now = new Date();
										log.debug("Calc: " + (now.getTime() - Long.parseLong(timestamp)) + " div by 1000 = " + (now.getTime() - Long.parseLong(timestamp))/1000);
										log.debug("greater than 60?");
										if (((now.getTime() - Long.parseLong(timestamp))/1000 > 60)
												&& (Long.parseLong(timestamp) != 0)) {
											// Message is too old
											log.debug("old:" + (((now.getTime() - Long.parseLong(timestamp))/1000)));
										} else {
											// Message has not yet expired
											log.debug("not old:" + ((now.getTime() - Long.parseLong(timestamp)/1000)));
										}
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
										auditer.auditAlways("The message is considered too old:"
												+ getAllCallerDetails(request));
										securePostData.setMessageTimedOut(true);
										log.debug("Message too old");
									}
									if (!securePostData.isMessageHasBeenVerified()) {
										auditer.auditAlways("Bad verification:" + getAllCallerDetails(request));
										securePostData.setBadSig(true);
										log.debug("Bad verification");
									}
								}
							}
							catch (Exception e1) {
								log.debug("Exception trying to verify the data:" + e1.getMessage());
								auditer.auditAlways("Bad signature:" + getAllCallerDetails(request));
								securePostData.setBadSig(true);
							}
						}
					}
					catch (IOException e2) {
						e2.printStackTrace();
						log.debug("Unable to get at the public key");
						securePostData.setNoPrivateKey(true);
					}
					manipulateSecurePostDataList(securePostData);
					if (log.isDebugEnabled()) {
						log.debug("Sig:"+securePostData.isBadSig());
						log.debug("Verified:"+securePostData.isMessageHasBeenVerified());
						log.debug("Timeout:"+securePostData.isMessageTimedOut());
						log.debug("Priv key:"+securePostData.isNoPrivateKey());
					}
					
					/*
					 * We can now send a little tickle to the intended recipient telling them that
					 * there is data waiting for them 
					 */
					log.debug("About to send notification");
					try {
//						SendViaPost svp = new SendViaPost();
//						String result = svp.sendSecurePost(securePostData.getIntendedDestination(),
//								String.format("%s=%s", SystemVars.POST_COMMAND_COMMAND_TOKEN, SystemVars.POST_COMMAND_NEW_DATA_AVAILABLE),
//								false);
//						if (log.isDebugEnabled()) {
//							log.debug(String.format("got result from post: %s", result));
//						}
						
						URL url = new URL(securePostData.getIntendedDestination());
						URLConnection connection = url.openConnection();
						if (log.isDebugEnabled()) {
							log.debug(String.format("About to post to %s", securePostData.getIntendedDestination()));
							log.debug(String.format("Will use the following parms: %s=%s", SystemVars.POST_COMMAND_COMMAND_TOKEN, SystemVars.POST_COMMAND_NEW_DATA_AVAILABLE));
						}
						connection.setDoOutput(true);
						OutputStreamWriter outsw = new OutputStreamWriter(connection.getOutputStream());
						outsw.write(String.format("%s=%s", SystemVars.POST_COMMAND_COMMAND_TOKEN, SystemVars.POST_COMMAND_NEW_DATA_AVAILABLE));
						outsw.flush();
						outsw.close();
						log.debug("Message posted");
					}
					catch (Exception ex) {
						log.error("Exception trying to notify " + securePostData.getIntendedDestination());
						log.error(ex);
					}
				}
				else {
					/*
					 * This is not a digital signature parameter so we should
					 * look at the data. If the data == REQUEST_DATA_CODE then a
					 * client is asking for the data collected so far - send and
					 * reset
					 */
					String dataRequestor = data + "=" + request.getParameter(data);
					if (dataRequestor.compareTo(ReceivePostedData.REQUEST_DATA_CODE) == 0) {
						auditer.auditAlways("Request to gather all data and then clear the stack");
						clearStack = true;
						manipulateSecurePostDataList(null);
						return;
					}
					if (dataRequestor.compareTo(ReceivePostedData.REQUEST_DATA_CODE_DONT_CLEAR_STACK) == 0) {
						auditer.auditAlways("Request to gather all data but not clear the stack");
						clearStack = false;
						manipulateSecurePostDataList(null);
						return;
					}

					/*
					 * This message is not a signature. We should therefore add
					 * it to the data we used to generate the signature, unless
					 * it is a timestamp In which case we need to extract it
					 */
					if (data.compareTo(SignatureGenerator.TIMESTAMP_POST_ATTRIBUTE) == 0) {
						timestamp = request.getParameter(data);
					}
					else if (data.compareTo(SignatureGenerator.KEYFILE_POST_ATTRIBUTE) == 0) {
						keyFile = request.getParameter(data);
						auditer.auditSometimes(String.format("Using key %s for host %s", keyFile, hostId));
					}
					else if (data.compareTo(SignatureGenerator.DEST_IP) == 0) {
						String destIp = request.getParameter(data);
						securePostData.setIntendedDestination(destIp);
						auditer.auditSometimes(String.format("Destination IP is %s", destIp));
					}
					else {
						messages[counter] = data + "=" + request.getParameter(data);
						securePostData.addPostParm(data, messages[counter]);
						log.debug("Adding:" + messages[counter]);
						counter++;
					}
				}
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
			 * and then clear the list
			 */
			for (SecurePostData spd : securePostDataList) {
				spd.printData(out);
			}
			if (clearStack) {
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
