package uk.ac.ox.oucs.iam.postsecurely;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ox.oucs.iam.audit.IamAudit;
import uk.ac.ox.oucs.iam.security.utilities.GeneralUtils;
import uk.ac.ox.oucs.iam.security.utilities.SignatureGenerator;
import uk.ac.ox.oucs.iam.security.utilities.SignatureVerifier;
import uk.ac.ox.oucs.iam.security.utilities.VidaasSignature;

public class ReceivePost extends HttpServlet {
	private PrintWriter out;
	public String keyFile = "/tmp/key";
	private SecurePostData securePostData;
	private List<SecurePostData> securePostDataList = new ArrayList<SecurePostData>();
	public static final String REQUEST_DATA_CODE = "requestCurrent=data";
	private IamAudit auditer = new IamAudit();

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

	private void checkRequest(HttpServletRequest request) {
		String messageToVerify = "";
		String timestamp = "";
		int counter = 0;
		securePostData = new SecurePostData();

		auditer.auditSuccess("Post request received from " + request.getRemoteAddr());

		
		Enumeration<?> e = request.getParameterNames();
		String[] messages = new String[request.getParameterMap().size()];
		while (e.hasMoreElements()) {
			String data = (String) e.nextElement();

			if (data != null) {
				out.println(data + " = " + request.getParameter(data));
				if (data.compareTo("sig") == 0) {
					out.println("This is encrypted");
					// Encrypted sig - need to verify
					File privateKey = new File(keyFile + ".pub");
					if (!privateKey.exists()) {
						out.println("Unable to get at the private key");
						securePostData.setNoPrivateKey(true);
					}
					else {
						try {
							for (int index = counter - 1; index > -1; index--) {
								messageToVerify += messages[index];
								if (index != 0) {
									messageToVerify += "&";
								}
							}

							out.println("About to verify message...<" + messageToVerify + ">");
							String signature = request.getParameter(data);
							out.println(signature);

							SignatureVerifier sigVerifier = new SignatureVerifier(keyFile);
							byte[] decodedBytes = sigVerifier.decodeAsByteArray(signature);
							boolean verified = false;
							boolean messageIsTooOld = false;
							if (timestamp == "") {
								verified = sigVerifier.verifyDigitalSignature(decodedBytes, messageToVerify);
							}
							else {
								verified = sigVerifier.verifyDigitalSignature(decodedBytes, messageToVerify + "_"
										+ timestamp);
								messageIsTooOld = !sigVerifier.verifyTimestamp(timestamp);
							}
							if (verified && !messageIsTooOld) {
								auditer.auditSuccess("Message has been verified");
							}
							else {
								auditer.auditFailure("Message has not been verified");
								securePostData.setMessageHasBeenVerified(false);
								if (messageIsTooOld) {
									auditer.auditFailure("The message is considered too old:"
											+ getAllCallerDetails(request));
									securePostData.setMessageTimeout(true);
								}
								if (!verified) {
									auditer.auditFailure("Bad verification:" + getAllCallerDetails(request));
									securePostData.setBadSig(true);
								}
							}
						}
						catch (Exception e1) {
							// TODO Auto-generated catch block
							out.println("Exception trying to verify the data:" + e1.getMessage());
							auditer.auditFailure("Bad signature:" + getAllCallerDetails(request));
							securePostData.setBadSig(true);
						}
					}
					manipulateSecurePostDataList(securePostData);
				}
				else {
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
							securePostData.addPostParms(messages[counter]);
							counter++;
						}
					}
				}
			}
		}
	}

	private String getAllCallerDetails(HttpServletRequest request) {
		return String.format("Remote host:%s, Referer:%s, remoteHost:%s, user agent:%s", request.getRemoteAddr(), request.getHeader("referer"),
				request.getHeader("host"), request.getHeader("user-agent"));
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
