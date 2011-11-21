package uk.ac.ox.oucs.vidaas.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import uk.ac.ox.oucs.vidaas.shared.OvfCatalogException;

import com.vmware.vcloud.api.rest.schema.ErrorType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.TaskStatusType;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;

public class VcloudSession {

	/**
	 * authenticated vCloud client session
	 */
	private static VcloudClient client;
	
	/**
	 * Virtual Data Centre
	 */
	private static Vdc vdc = null;
	
	/**
	 * Static initialisation
	 */
	static {
		staticInit();
	}
	
	/**
	 * Authenticate to vcloud API
	 */
	private static boolean staticInit() {
		InputStream in = VcloudSession.class.getResourceAsStream("vmware.properties");
		Properties props = new Properties();

		try {
			//load properties
			props.load(in);
			String password = props.getProperty("vidaas.vmware.password");
			String URL = props.getProperty("vidaas.vmware.URL");
			String username = props.getProperty("vidaas.vmware.username");
			String version = props.getProperty("vidaas.vmware.version");
			String vdcName = props.getProperty("vidaas.vmware.vdcName");
			String checkcert = props.getProperty("vidaas.vmware.checkcert");
			
			// Trust all certificates?
			if("false".equalsIgnoreCase(checkcert)) {
				// Setting up for SSL access. Do not use it production environment
				Protocol https = new Protocol("https",
					(ProtocolSocketFactory) new FakeSSLSocketFactory(), 443);
				Protocol.registerProtocol("https", https);
			}
	
			// Login
			client = new VcloudClient(URL);
			client.login(username, password, client.getSupportedVersions().get(version));
			vdc = getVDC(vdcName);
			
			return true;
			
		} catch (Exception e) {
			client = null;
			return false;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				//ignore
			}
		}
	}
	
	/**
	 * 
	 * @return authenticated vCloud client session
	 */
	public static VcloudClient getClient() {
		return client;
	}
	
	/**
	 * Gets VDCRefs
	 * @throws VCloudException on vcloud API errors
	 * @throws OvfCatalogException if initialisation failed
	 * @deprecated
	 */
	Collection<ReferenceType> getVdcRefs() throws OvfCatalogException, VCloudException {
		
		try {
			if(client==null) {
				if(!staticInit()) {
					throw new OvfCatalogException("Initialisation failed");
				}	
			}
	
			HashMap<String, ReferenceType> orgsList = client.getOrgRefsByName();
			Collection<ReferenceType> vdcRefs=null;
			
			//iterate vOrgs
			for (String vOrg : orgsList.keySet()) {
				ReferenceType orgRef = orgsList.get(vOrg);
				Organization org = Organization.getOrganizationByReference(client, orgRef);
				
				if(vdcRefs==null) {
					vdcRefs = org.getVdcRefs();
				} else {
					vdcRefs.addAll(org.getVdcRefs());
				}
			}
			return vdcRefs;
		} catch (VCloudException e) {
			ErrorType vCloudError = e.getVcloudError();
			if(vCloudError.getMajorErrorCode()==403) {
				//possible session timeout, reauthenticate
				client = null;
				return getVdcRefs();
			} else {
				throw e;
			}
		}

	}
	
	/**
	 * 
	 * @param vdcName name of vdc
	 * @return
	 * @throws VCloudException
	 */
	private static Vdc getVDC(String vdcName) throws OvfCatalogException {
		try {
	
			HashMap<String, ReferenceType> orgsList = client.getOrgRefsByName();
			
			//iterate vOrgs
			for (String vOrg : orgsList.keySet()) {
				ReferenceType orgRef = orgsList.get(vOrg);
				Organization org = Organization.getOrganizationByReference(client, orgRef);
				
				ReferenceType vdcRef;
				if(vdcName != null) {
					vdcRef = org.getVdcRefByName(vdcName);
				} else {
					vdcRef = org.getVdcRefs().iterator().next();
				}
				return Vdc.getVdcByReference(client, vdcRef);
			}
			
			throw new OvfCatalogException("No VDC available.");
			
		} catch (VCloudException e) {
			ErrorType vCloudError = e.getVcloudError();
			if(vCloudError.getMajorErrorCode()==403) {
				//possible session timeout, reauthenticate
				reset();
				return getVDC(vdcName);
			} else {
				throw new OvfCatalogException("Could not get VDC.");
			}
		}
	}
	
	/**
	 * 
	 * @return a cached Vdc object
	 */
	public static Vdc getVDC() {
		return vdc;
	}
	
	/**
	 * Waiting for a successful task
	 *
	 * @throws VCloudException
	 * 
	 */
	public static boolean waitForTaskCompletion(Task task, int maxMillis)
			throws VCloudException {
		int time=0;
		TaskStatusType taskStatus = Task.getTaskByReference(client,
				task.getReference()).getResource().getStatus();
		while (!taskStatus.equals(TaskStatusType.SUCCESS)) {
			taskStatus = Task.getTaskByReference(client,
					task.getReference()).getResource().getStatus();
			if (taskStatus.equals(TaskStatusType.ERROR)
					|| taskStatus.equals(TaskStatusType.CANCELED)) {
				return false;
			}
			if(time>maxMillis) {
				return false;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
			time+=50;
		}
		return true;
	}

	/**
	 * Create a new session.
	 */
	public static void reset() {
		client = null;
		vdc = null;
		staticInit();
	}

}
