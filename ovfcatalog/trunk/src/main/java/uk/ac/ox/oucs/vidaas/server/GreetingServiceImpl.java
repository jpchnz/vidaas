package uk.ac.ox.oucs.vidaas.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import uk.ac.ox.oucs.vidaas.client.GreetingService;
import uk.ac.ox.oucs.vidaas.shared.OvfCatalogException;
import uk.ac.ox.oucs.vidaas.shared.VmValue;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.ResourceReferenceType;
import com.vmware.vcloud.api.rest.schema.TaskStatusType;
import com.vmware.vcloud.api.rest.schema.ovf.FileType;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VappTemplate;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
    GreetingService {
	
	static VcloudClient client;
	
	/**
	 * Static initialisation
	 * 
	 * 1) Authenticate to vcloud API
	 */
	{
		InputStream in = this.getClass().getResourceAsStream("vmware.properties");
		Properties props = new Properties();

		try {
			//load properties
			props.load(in);
			String password = props.getProperty("vidaas.vmware.password");
			String URL = props.getProperty("vidaas.vmware.URL");
			String username = props.getProperty("vidaas.vmware.username");
			String version = props.getProperty("vidaas.vmware.version");
			
			// Setting up for SSL access. Do not use it production environment
			Protocol https = new Protocol("https",
					(ProtocolSocketFactory) new FakeSSLSocketFactory(), 443);
			Protocol.registerProtocol("https", https);
	
			client = new VcloudClient(URL);
			// Client login

			client.login(username, password, client.getSupportedVersions().get(version));
		} catch (Exception e) {
			client = null;
		}
	}
	
	/**
	 * Retrieves a list of Vapps
	 * @return
	 */
	public VmValue[] getVMs() throws OvfCatalogException {
		try {
			HashMap<String, ReferenceType> orgsList = client.getOrgRefsByName();
			
			//iterate vOrgs
			for (String vOrg : orgsList.keySet()) {
				ReferenceType orgRef = orgsList.get(vOrg);
				Organization org = Organization.getOrganizationByReference(client, orgRef);
				
				//iterate VDCs
				for(ReferenceType vdcRef : org.getVdcRefs()) {
					Vdc vdc = Vdc.getVdcByReference(client, vdcRef);
					
					//copy to value class
					HashMap<String, ResourceReferenceType> vmRefs = vdc.getVappRefsByName();
					VmValue[] vmValue = new VmValue[vmRefs.size()];
					int i = 0;
					for(String vmName : vmRefs.keySet()) {
						Vapp vApp = Vapp.getVappByReference(client, vmRefs.get(vmName));
						
						List<Vapp> vms = vApp.getChildrenVms();
						HashSet<String> ips = new HashSet<String>();
						for (Vapp vm : vms) {
							ips.addAll(vm.getIpAddresses());
						}
						
						vmValue[i++] = new VmValue(vmName, vApp.getVappStatus(), ips, null);
					}
					return vmValue;
				}
			}
			return null;
		} catch (VCloudException e) {
			throw new OvfCatalogException(e.getMessage());
		}
	}
	
	/**
	 * Retrieves a list of Vapps
	 * @return
	 */
	public VmValue[] getTemplates() throws OvfCatalogException {
		try {
			HashMap<String, ReferenceType> orgsList = client.getOrgRefsByName();
			
			//iterate vOrgs
			for (String vOrg : orgsList.keySet()) {
				ReferenceType orgRef = orgsList.get(vOrg);
				Organization org = Organization.getOrganizationByReference(client, orgRef);
				
				//iterate VDCs
				for(ReferenceType vdcRef : org.getVdcRefs()) {
					Vdc vdc = Vdc.getVdcByReference(client, vdcRef);
					
					//copy to value class
					Collection<ResourceReferenceType> vappTemplRefs = vdc.getVappTemplateRefs();
					VmValue vmValue[] = new VmValue[vappTemplRefs.size()];
					int i=0;
					for(ResourceReferenceType vappTemplRef : vappTemplRefs) {
						String catName = vappTemplRef.getName();
						
						VappTemplate vappTemplate = VappTemplate.getVappTemplateByReference(client, vappTemplRef);
						Task task = vappTemplate.enableDownload();
						VmValue vm;
						if(waitForTaskCompletion(client, task, 200)) {
							HashMap<String, FileType> downloadFiles = vappTemplate.getDownloadFileNames();
							vm = new VmValue(catName, vappTemplRef.getStatus(), null, downloadFiles.keySet());
						} else {
							vm = new VmValue(catName, vappTemplRef.getStatus(), null, null);
						}
						vm.setCreateable(true);
						vmValue[i++] = vm;
					}
					return vmValue;
				}
			}
			return null;
		} catch (VCloudException e) {
			throw new OvfCatalogException(e.getMessage());
		}
	}

  /*
   * Escape an html string. Escaping data received from the client helps to
   * prevent cross-site script vulnerabilities.
   *
   * @param html the html string to escape
   * @return the escaped string
   */
  /*private String escapeHtml(String html) {
    if (html == null) {
      return null;
    }
    return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(
        ">", "&gt;");
  }*/
  
	/**
	 * Waiting for a successful task
	 *
	 * @throws VCloudException
	 */
	public static boolean waitForTaskCompletion(VcloudClient client, Task task, int maxMillis)
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

}
