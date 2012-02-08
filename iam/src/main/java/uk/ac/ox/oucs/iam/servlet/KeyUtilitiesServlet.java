package uk.ac.ox.oucs.iam.servlet;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ox.oucs.iam.security.keys.KeyServices;
import uk.ac.ox.oucs.iam.security.utilities.GeneralUtils;

/**
 * This is a servlet that provides key store type services for VIDaaS. The idea
 * is that when VIDaaS starts up, each node POSTs a message to a central service
 * with their host information (IP address mainly) and if that address is a
 * change from previously (and if so, what the old address was) for DHCP
 * environments. Upon first registration, they should also send their public
 * key. The central service collates these along with the keys and is able to
 * push all public keys to all other nodes, again via POST, so that nodes may
 * securely interact.
 * 
 * This is a work in progress.
 * 
 * @author oucs0153
 * 
 */
@SuppressWarnings("serial")
public class KeyUtilitiesServlet extends HttpServlet {
	private PrintWriter out;
	private List<VIDaaSHosts> vidaasHostList;

	class KeyFilter implements FilenameFilter {
		String keyExtName;

		public boolean accept(File dir, String name) {
			return (name.endsWith(keyExtName));
		}

		public KeyFilter(String keyExtName) {
			this.keyExtName = keyExtName;
		}
	}

	class VIDaaSHosts {
		// This has been defined as a separate class so it can be extended
		public String ip;
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
					KeyServices ks = new KeyServices(keys[0].getAbsolutePath().substring(0,
							keys[0].getAbsolutePath().lastIndexOf(".")), false, "HmacSHA512");
					details = ks.getPrivateKeyData();
				}
				catch (Exception e) {
					details = "Unable to get details - " + keys[0].getAbsolutePath();
				}
				response.getWriter().write(keys[0].getAbsolutePath() + " (encoding=" + details + ")");
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

		keys = getPublicKeyList();
		if (request.getParameter("publicKeyNumber") != null) {
			response.getWriter().write(String.format("%s", keys.length));
		}
		if (request.getParameter("publicKeyNames") != null) {
			String result;
			if (keys.length == 0) {
				result = "No public keys found in " + GeneralUtils.provideKeyPairDirectory();
			}
			else {
				result = "";
				for (int index = 0; index < keys.length; index++) {
					result += keys[index].getAbsolutePath() + "\n";
				}
			}
			response.getWriter().write(result);
		}

		if (request.getParameter("remoteHosts") != null) {
			/*
			 * User has asked for a list of all remote hosts, so that we can
			 * potentially ship the local public key to them all. The hosts
			 * should be read from a file into memory here.
			 */
			readHosts();
			if (vidaasHostList.size() == 0) {
				response.getWriter().write("No hosts defined");
			}
			else {
				response.getWriter().write("Number of hosts:"+vidaasHostList.size()+"\n");
				for (VIDaaSHosts vh : vidaasHostList) {
					response.getWriter().write(vh.ip + "\n");
				}
			}
		}
		if (request.getParameter("shipToHosts") != null) {
			/*
			 * User has asked for a list of all remote hosts, so that we can
			 * potentially ship the local public key to them all.
			 * 
			 * TODO Send post commands to each of these with all public keys
			 */
			if (vidaasHostList.size() == 0) {
				response.getWriter().write("No hosts defined - cannot ship");
			}
			else {
				if (true) {
					response.getWriter().write("Code to be written");
				}
				else {
					/*
					 * TODO
					 * We need proper IP addresses before this will work
					 */
					keys = getPublicKeyList();
					String dataToPost;
					for (VIDaaSHosts vh : vidaasHostList) {
						System.out.println("host: http://" + vh.ip + ":8081/iam/KeyUtilitiesServlet");
						URL url = new URL("http://" + vh.ip + ":8081/iam/KeyUtilitiesServlet");
						URLConnection connection = url.openConnection();
						connection.setDoOutput(true);
						OutputStreamWriter outputSW = new OutputStreamWriter(connection.getOutputStream());
						for (File f : keys) {
							dataToPost = "request=publicKeyIssue&publicKey="
									+ GeneralUtils.readFileAsString(f.getAbsolutePath());
							System.out.println(dataToPost);
							outputSW.write(dataToPost);
						}

						out.flush();
						out.close();

						response.getWriter().write(vh.ip + ":shipped");
					}
				}
			}
		}
	}

	private File[] getPublicKeyList() throws IOException {
		File dir = new File(GeneralUtils.provideKeyPairDirectory());
		FilenameFilter filter = new KeyFilter(KeyServices.publicKeyNameExtension);
		return dir.listFiles(filter);
	}

	private void readHosts() {
		vidaasHostList = new ArrayList<VIDaaSHosts>();
		try {
			String hostString = (String) GeneralUtils.readFileAsString("/tmp/hostfile");
			System.out.println(hostString);
			System.out.println("Number:" + hostString.split("\n").length);
			for (String s : hostString.split("\n")) {
				VIDaaSHosts vh = new VIDaaSHosts();
				vh.ip = s;
				vidaasHostList.add(vh);
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
