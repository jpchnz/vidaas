package uk.ac.ox.oucs.iam.postsecurely;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import uk.ac.ox.oucs.iam.audit.IamAudit;
import uk.ac.ox.oucs.iam.security.utilities.SignatureGenerator;
import uk.ac.ox.oucs.iam.security.utilities.SignatureVerifier;

@SuppressWarnings("serial")
public class ReceivePost extends HttpServlet {
	private PrintWriter out;
	public String keyFile = "/tmp/key";
	private SecurePostData securePostData;
	private List<SecurePostData> securePostDataList = new ArrayList<SecurePostData>();
	public static final String REQUEST_DATA_CODE = "requestCurrent=data";
	private IamAudit auditer = new IamAudit();
	private boolean dontAcceptGet = true;
	private Logger log = Logger.getLogger(ReceivePost.class);
	

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
	 * Method that looks at the POST data sent to the application server. It breaks down all the
	 * parameters, validates the encrypted data contained within and builds a SecurePostData
	 * object with that data, adding it to an internal list for local query.
	 * 
	 * Important. The &sig parameter should be the very last parameter sent. So an example post could be
	 * ?name=fred&ts=1323857454692&sig=MCwCFGKn7ucmYGTiIti5%2B3QNOnjXSGbMAhRxAvm%2BelrlIvqrCm6LObd%2B5yC%2BSA%3D%3D
	 * 
	 * 
	 * @param request
	 */
	private void checkRequest(HttpServletRequest request) {
		String messageToVerify = "";
		String timestamp = "";
		int counter = 0;
		securePostData = new SecurePostData();

		auditer.auditSuccess("Post request received from " + request.getRemoteAddr());
		log.debug("QQQQQQQQQQQQQQQQQQQQQQQ" + request.getRemoteAddr());
		securePostData.setOriginatorHost(request.getRemoteAddr());

		Enumeration<?> e = request.getParameterNames();
		String[] messages = new String[request.getParameterMap().size()];
		/*
		 * Understand and process each parameter 
		 */
		while (e.hasMoreElements()) {
			String data = (String) e.nextElement();

			if (data != null) {
				out.println(data + " = " + request.getParameter(data));
				if (data.compareTo("sig") == 0) {
					/*
					 * A digital signature has been sent, which is expected. Verify this. 
					 */
					File privateKey = new File(keyFile + ".pub");
					if (!privateKey.exists()) {
						out.println("Unable to get at the private key");
						securePostData.setNoPrivateKey(true);
					}
					else {
						try {
							/*
							 * When the message is POSTed here, the signature is generated from
							 * that message. Thus we need to build the message and check that
							 * the expected signature is present.
							 * 
							 *  Remember, the sender generates the signature using their private key.
							 *  The receiver (here) generates the signature using the sender's
							 *  public key. If the sigs are the same, we know the message has not been 
							 *  tampered with (since the sig is generated using the message) and,
							 *  since we have used the sender's public key to generate the sig, we
							 *  can guarantee that the message did indeed come from the sender. 
							 */
							for (int index = counter - 1; index > -1; index--) {
								messageToVerify += messages[index];
								if (index != 0) {
									messageToVerify += "&";
								}
							}

							out.println("About to verify message...<" + messageToVerify + ">");
							String signature = request.getParameter(data);

							SignatureVerifier sigVerifier = new SignatureVerifier(keyFile);
							byte[] decodedBytes = sigVerifier.decodeAsByteArray(signature);
							if (timestamp == "") {
								securePostData.setMessageHasBeenVerified(sigVerifier.verifyDigitalSignature(decodedBytes, messageToVerify));
							}
							else {
								securePostData.setMessageHasBeenVerified(sigVerifier.verifyDigitalSignature(decodedBytes, messageToVerify + "_"
										+ timestamp));
								securePostData.setMessageTimedOut(!sigVerifier.verifyTimestamp(timestamp));
							}
							if (securePostData.isMessageHasBeenVerified() && !securePostData.isMessageTimedOut()) {
								securePostData.setMessageHasBeenVerified(true);
							}
							else {
								auditer.auditFailure("Message has not been verified");
								if (securePostData.isMessageTimedOut()) {
									auditer.auditFailure("The message is considered too old:"
											+ getAllCallerDetails(request));
									securePostData.setMessageTimedOut(true);
								}
								if (!securePostData.isMessageHasBeenVerified()) {
									auditer.auditFailure("Bad verification:" + getAllCallerDetails(request));
									securePostData.setBadSig(true);
								}
							}
						}
						catch (Exception e1) {
							out.println("Exception trying to verify the data:" + e1.getMessage());
							auditer.auditFailure("Bad signature:" + getAllCallerDetails(request));
							securePostData.setBadSig(true);
						}
					}
					manipulateSecurePostDataList(securePostData);
				}
				else {
					/*
					 * This is not a digital signature parameter so we should look
					 * at the data. If the data == REQUEST_DATA_CODE then a client is asking for the
					 * data collected so far - send and reset
					 */
					String dataRequestor = data + "=" + request.getParameter(data);
					if (dataRequestor.compareTo(REQUEST_DATA_CODE) == 0) {
						manipulateSecurePostDataList(null);
						return;
					}
					else {
						/*
						 * This message is not a signature. We should therefore
						 * add it to the data we used to generate the signature,
						 * unless it is a timestamp In which case we need to
						 * extract it
						 */
						if (data.compareTo(SignatureGenerator.TIMESTAMP_POST_ATTRIBUTE) == 0) {
							timestamp = request.getParameter(data);
						}
						else {
							messages[counter] = data + "=" + request.getParameter(data);
							securePostData.addPostParm(messages[counter]);
							counter++;
						}
					}
				}
			}
		}
	}

	private String getAllCallerDetails(HttpServletRequest request) {
		return String.format("Remote host:%s, Referer:%s, remoteHost:%s, user agent:%s", request.getRemoteAddr(),
				request.getHeader("referer"), request.getHeader("host"), request.getHeader("user-agent"));
	}

	private synchronized void manipulateSecurePostDataList(SecurePostData dataItem) {
		if (dataItem == null) {
			/*
			 * The user has requested all current data - let's give it to them
			 * and then clear the list
			 */
			for (SecurePostData spd : securePostDataList) {
				spd.printData(out);
			}
			securePostDataList.clear();
		}
		else {
			securePostDataList.add(dataItem);
		}
	}
}
