package uk.ac.ox.oucs.iam.servlet;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import uk.ac.ox.oucs.iam.audit.IamAudit;
import uk.ac.ox.oucs.iam.interfaces.security.keys.KeyServices;
import uk.ac.ox.oucs.iam.interfaces.utilities.exceptions.DuplicateKeyException;
import uk.ac.ox.oucs.iam.interfaces.utilities.exceptions.KeyNotFoundException;
import uk.ac.ox.oucs.iam.interfaces.utilities.exceptions.NewKeyException;
import uk.ac.ox.oucs.iam.postsecurely.SendViaPost;
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
	private String remoteHostFile = "/tmp/hostfile";
	private List<VIDaaSHosts> vidaasHostList;
	public static final String HELLO_WORLD_ATTRIBUTE = "helloWorld";
	public static final String HELLO_WORLD_HOSTNAME_ATTRIBUTE = "hostName";
	public static final String HELLO_WORLD_PUBLICKEYNAME_ATTRIBUTE = "publicKeyName";
	public static final String HELLO_WORLD_PUBLICKEY_ATTRIBUTE = "publicKey";
	public static final String HAVE_A_PUBLIC_KEY_ATTRIBUTE = "haveAPublicKey";
	private IamAudit auditer = new IamAudit();
	private Logger log = Logger.getLogger(KeyUtilitiesServlet.class);
	
	

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
		public String hostName;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("doGet");
		
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-store, no-cache");

		if (request.getParameter(HAVE_A_PUBLIC_KEY_ATTRIBUTE) != null) {
			log.debug("Public key attribute received");
			/*
			 * We have been sent a public key - simply add it to our key store.
			 * Note that this should only be received on non-manager machines
			 */
			String originatorIp = request.getParameter(HAVE_A_PUBLIC_KEY_ATTRIBUTE);
			String hostName = request.getParameter(HELLO_WORLD_HOSTNAME_ATTRIBUTE);
			String publicKey = request.getParameter(HELLO_WORLD_PUBLICKEY_ATTRIBUTE);
			String publicKeyName = request.getParameter(HELLO_WORLD_PUBLICKEYNAME_ATTRIBUTE) + "yy";

			auditer.auditAlways("Have a key message from " + hostName + " (" + originatorIp + ")");

			log.debug(originatorIp);
			log.debug(hostName);
			log.debug(publicKey);

			try {
				GeneralUtils.decodePublicKeyAndWriteToFile(publicKey, GeneralUtils.provideKeyPairDirectory()
						+ File.separatorChar + publicKeyName + KeyServices.publicKeyNameExtension);
			}
			catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (request.getParameter(HELLO_WORLD_ATTRIBUTE) != null) {
			log.debug("Hello world attribute received");
			/*
			 * Note that this should only be received on the manager machine
			 */
			if (vidaasHostList.size() == 0) {
				readHosts();
			}
			String originatorIp = request.getParameter(HELLO_WORLD_ATTRIBUTE);
			String hostName = request.getParameter(HELLO_WORLD_HOSTNAME_ATTRIBUTE);
			String publicKey = request.getParameter(HELLO_WORLD_PUBLICKEY_ATTRIBUTE);
			String publicKeyName = request.getParameter(HELLO_WORLD_PUBLICKEYNAME_ATTRIBUTE);
			auditer.auditAlways("Hello world message from " + hostName + " (" + originatorIp + ")");
			log.debug(originatorIp);
			log.debug(hostName);
			log.debug(publicKey);
			boolean foundEntry = false;
			for (VIDaaSHosts vh : vidaasHostList) {
				if (vh.hostName.equalsIgnoreCase(hostName)) {
					foundEntry = true;
					break;
				}
			}
			if (!foundEntry) {
				auditer.auditAlways(hostName + " (" + originatorIp + ") - first message - write public key file "
						+ publicKeyName);
				GeneralUtils.appendStringToFile(remoteHostFile, hostName);
				try {
					GeneralUtils.decodePublicKeyAndWriteToFile(publicKey, GeneralUtils.provideKeyPairDirectory()
							+ File.separatorChar + publicKeyName + KeyServices.publicKeyNameExtension);
				}
				catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
				catch (InvalidKeySpecException e) {
					e.printStackTrace();
				}
			}
			return;
		}

		File dir = new File(GeneralUtils.provideKeyPairDirectory());
		FilenameFilter filter = new KeyFilter(KeyServices.privateKeyNameExtension);
		File[] keys = dir.listFiles(filter);

		if (request.getParameter("privateKey") != null) {
			log.debug("Private key parameter recevied");
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
			log.debug("Private key number parameter recevied");
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
			log.debug("Public key number parameter recevied");
			response.getWriter().write(String.format("%s", keys.length));
		}
		if (request.getParameter("publicKeyNames") != null) {
			log.debug("Public key names parameter recevied");
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
			log.debug("Remote hosts command recevied");
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
				response.getWriter().write("Number of hosts:" + vidaasHostList.size() + "\n");
				for (VIDaaSHosts vh : vidaasHostList) {
					response.getWriter().write(vh.hostName + "\n");
				}
			}
		}
		if (request.getParameter("shipToHosts") != null) {
			log.debug("Ship to hosts command recevied");
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
				/*
				 * TODO We need proper IP addresses before this will work
				 */
				keys = getPublicKeyList();
				for (VIDaaSHosts vh : vidaasHostList) {
					String destURL = "http://" + vh.hostName + ":8081/iam/KeyUtilitiesServlet";
					System.out.println("Sending to " + destURL);
					String publicKey;
					for (File f : keys) {
						try {
							publicKey = GeneralUtils.provideBaseKeyPairName();

							String postData = String.format("%s=%s&%s=%s&%s=%s&%s=%s", HAVE_A_PUBLIC_KEY_ATTRIBUTE,
									GeneralUtils.getLocalIPAddress(), HELLO_WORLD_HOSTNAME_ATTRIBUTE,
									GeneralUtils.getLocalHostname(), HELLO_WORLD_PUBLICKEYNAME_ATTRIBUTE, f.getName(),
									HELLO_WORLD_PUBLICKEY_ATTRIBUTE,
									GeneralUtils.readPublicKeyFromFileAndEncode(publicKey));
							SendViaPost post = new SendViaPost();// HAVE_A_PUBLIC_KEY_ATTRIBUTE
							post.sendPost(destURL, postData, false);
						}
						catch (IOException e) {
							e.printStackTrace();
						}
						catch (NewKeyException e) {
							e.printStackTrace();
						}
						catch (KeyNotFoundException e) {
							e.printStackTrace();
						}
						catch (DuplicateKeyException e) {
							e.printStackTrace();
						}
					}
					response.getWriter().write(vh.hostName + ":shipped");

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
			String hostString = (String) GeneralUtils.readFileAsString(remoteHostFile);
			System.out.println(hostString);
			System.out.println("Number:" + hostString.split("\n").length);
			for (String s : hostString.split("\n")) {
				VIDaaSHosts vh = new VIDaaSHosts();
				vh.hostName = s;
				vidaasHostList.add(vh);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
