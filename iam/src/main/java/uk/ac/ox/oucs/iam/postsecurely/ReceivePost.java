package uk.ac.ox.oucs.iam.postsecurely;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ox.oucs.iam.security.utilities.GeneralUtils;
import uk.ac.ox.oucs.iam.security.utilities.SignatureVerifier;
import uk.ac.ox.oucs.iam.security.utilities.VidaasSignature;

public class ReceivePost extends HttpServlet {
	private PrintWriter out;
	public String keyFile = "/tmp/key";

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
		String[] messages = new String[10];
		int counter = 0;
		out.println("checkRequest");
		Enumeration<?> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String data = (String) e.nextElement();

			if (data != null) {
				out.println(data + " - ");
				out.println(request.getParameter(data));
				if (data.compareTo("sig") == 0) {
					out.println("This is encrypted");
					// Encrypted sig - need to verify
					File privateKey = new File(keyFile+".pub");
					if (!privateKey.exists()) {
						out.println("Unable to get at the private key");
					}
					else {
						try {
							SignatureVerifier sigVer = new SignatureVerifier(keyFile);
							
							for (int index = counter-1; index > -1; index--) {
								messageToVerify += messages[index];
								if (index != 0) {
									messageToVerify += "&";
								}
							}
//							messageToVerify = messageToVerify + "sig=" + request.getParameter(data);
							out.println("About to verify message...<" + messageToVerify + ">");
							String signature = request.getParameter(data);
							out.println(signature);
							
							SignatureVerifier sigVerifier = new SignatureVerifier(keyFile);
							byte[] decodedBytes = sigVerifier.decodeAsByteArray(signature);
							if (sigVerifier.verifyDigitalSignature(decodedBytes, messageToVerify)) {
								out.println("Message is for real");
							}
							else {
								out.println("Bad message!!!" + decodedBytes.length);
							}
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							out.println("Exception trying to verify the data:" + e1.getMessage());  
						}
					}
				}
				else {
					out.println("Not encrypted");
					messages[counter++] = data + "=" + request.getParameter(data);
				}
			}
		}
	}
}
