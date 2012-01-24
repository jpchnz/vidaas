package uk.ac.ox.oucs.iam;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ox.oucs.iam.security.utilities.SignatureVerifier;
import uk.ac.ox.oucs.iam.security.utilities.VidaasSignature;

public class AccessServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5369078740269568204L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		out.println("AccessServlet Executed");
		out.println("The name passed in is : " + request.getParameter("name"));
		out.println("The signature is : " + request.getParameter("sig"));
		out.println("The timestamp is : " + request.getParameter("ts"));
		checkSigs(request.getParameter("sig"), request.getParameter("name"), request.getParameter("ts"), out);
		
		out.flush();
		out.close();
	}

	/*
	 * Best use post since there aren't the memory restrictions that you get
	 * with a "get" request. Also, using post means that sensitive data (like
	 * passwords) are not passed in via the URL
	 */
	boolean verbose = false;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		if (verbose) {
			out.println("The name passed in is : " + request.getParameter("name"));
			out.println("The signature is : " + request.getParameter("sig"));
			out.println("The timestamp is : " + request.getParameter("ts"));
		}

		/*
		 * The question is - should we acknowledge this - or log it as an
		 * attempted hack? Let's assume "key1.pub" is the public key.
		 */
		checkSigs(request.getParameter("sig"), request.getParameter("name"), request.getParameter("ts"), out);

		if (!verbose) {
			return;
		}
		out.println(request.getAttribute("targeted-id"));
		out.println(request.getHeader("targeted-id"));
		out.println(request.getAttribute("AJP_targeted-id"));
		out.println(request.getHeader("AJP_targeted-id"));

		out.println(request.getAttribute("persistent-id"));
		out.println(request.getHeader("persistent-id"));
		out.println(request.getAttribute("AJP_persistent-id"));
		out.println(request.getHeader("AJP_persistent-id"));

		out.println("The remote user is : " + request.getHeader("REMOTE_USER")
				+ " or maybe " + System.getenv("REMOTE_USER"));
		out.println("The remote user is : "
				+ request.getAttribute("REMOTE_USER") + " or maybe "
				+ request.getParameter("REMOTE_USER"));
		// String variable = System.getenv("targeted-id");
		out.println("The targetted id in the header is : "
				+ request.getHeader("targeted-id"));

		Map<String, String> result;
		result = System.getenv();
		out.println("We have a targetted id of " + System.getenv("targeted-id"));
		// for (String res : result.values()) {
		// out.println(res);
		// }

		try {
			for (String envName : result.keySet()) {
				out.println(String.format("%s=%s%n", envName,
						result.get(envName)));
			}
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		out.flush();
		out.close();
	}

	
	private boolean checkSigs(String sig, String name, String ts, PrintWriter out) {
		boolean ret = false;
		try {
			SignatureVerifier sigVerify = new SignatureVerifier("/opt/key1.pub");
			VidaasSignature vidaasSignature = new VidaasSignature(sig, Long.parseLong(ts));
			if (sigVerify.verifyDigitalSignature(vidaasSignature, name)) {
				out.println("\nSignature verification successful - this is what we expect!!!!\n");
				ret = true;
			} else {
				if (sigVerify.isMessageTooOld()) {
					out.println("Message is too old");
				}
				else {
					out.println("\nSignature verification failed - not an expected result - bad!\n" + ts);
				}
				ret = false;
			}
			

			sigVerify = new SignatureVerifier("/opt/key2.pub");
			if (sigVerify.verifyDigitalSignature(vidaasSignature, name)) {
				out.println("\nSignature verification successful - not an expected result - bad!\n");
				ret = false;
			} else {
				out.println("\nSignature verification failed when using wrong key - this is what we expect!!!!\n");
			}
		} catch (Exception e1) {
			ret = false;
			e1.printStackTrace();
		}
		return ret;
	}
}
