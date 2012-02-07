package uk.ac.ox.oucs.iam.servlet;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ox.oucs.iam.security.keys.KeyServices;
import uk.ac.ox.oucs.iam.security.utilities.GeneralUtils;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.KeyNotFoundException;
import uk.ac.ox.oucs.iam.security.utilities.exceptions.NoEncodingException;

@SuppressWarnings("serial")
public class KeyUtilitiesServlet extends HttpServlet {
	private PrintWriter out;
	class KeyFilter implements FilenameFilter {
		String keyExtName;
	    public boolean accept(File dir, String name) {
	        return (name.endsWith(keyExtName));
	    }
	    public KeyFilter(String keyExtName) {
	    	this.keyExtName = keyExtName;
	    }
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-store, no-cache");
		

		File dir = new File(GeneralUtils.provideKeyPairDirectory());
		FilenameFilter filter = new KeyFilter(KeyServices.privateKeyNameExtension);
		File[] keys = dir.listFiles(filter);
		

		
		if (request.getParameter("privateKey") != null) {
			if (keys == null) {
				response.getWriter().write("Error trying to get your information");
				return;
			}
			if (keys.length == 0) {
				response.getWriter().write("None");
			}
			else {
				String details = "";
				try {
					KeyServices ks = new KeyServices(keys[0].getAbsolutePath().substring(0, keys[0].getAbsolutePath().lastIndexOf(".")), false, "HmacSHA512");
					details = ks.getPrivateKeyData();
				}
				catch (Exception e) {
					details = "Unable to get details - " + keys[0].getAbsolutePath();
				}
				response.getWriter().write(keys[0].getAbsolutePath() + " (encoding=" + details+")");
			}
		}
		if (request.getParameter("privateKeyNumber") != null) {
			if (keys == null) {
				response.getWriter().write("Error trying to get your information");
				return;
			}
			if (keys.length == 1) {
				response.getWriter().write("1");
			}
			else {
				response.getWriter().write(String.format("%s : this is bad - there should be only 1", keys.length));
			}
		}
		
		
		dir = new File(GeneralUtils.provideKeyPairDirectory());
		filter = new KeyFilter(KeyServices.publicKeyNameExtension);
		keys = dir.listFiles(filter);
		if (request.getParameter("publicKeyNumber") != null) {
			response.getWriter().write(String.format("%s", keys.length));
		}
		if (request.getParameter("publicKeyNames") != null) {
			String result = "";
			for (int index = 0; index < keys.length; index++) {
				result += keys[index].getAbsolutePath() + "\n";
			}
			response.getWriter().write(result);
		}
		
		if (request.getParameter("remoteHosts") != null) {
			/*
			 * User has asked for a list of all remote hosts, so that we can potentially ship 
			 * the local public key to them all.
			 */
			response.getWriter().write("Here you are");
		}
		if (request.getParameter("shipToHosts") != null) {
			/*
			 * User has asked for a list of all remote hosts, so that we can potentially ship 
			 * the local public key to them all.
			 */
			response.getWriter().write("Shipped");
		}
	}

		
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
