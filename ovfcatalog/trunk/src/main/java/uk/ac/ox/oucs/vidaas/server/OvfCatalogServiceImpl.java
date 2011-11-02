package uk.ac.ox.oucs.vidaas.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBElement;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import uk.ac.ox.oucs.vidaas.client.OvfCatalogService;
import uk.ac.ox.oucs.vidaas.shared.OvfCatalogException;
import uk.ac.ox.oucs.vidaas.shared.VmValue;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.vmware.vcloud.api.rest.schema.ErrorType;
import com.vmware.vcloud.api.rest.schema.FenceModeValuesType;
import com.vmware.vcloud.api.rest.schema.InstantiateVAppTemplateParamsType;
import com.vmware.vcloud.api.rest.schema.InstantiationParamsType;
import com.vmware.vcloud.api.rest.schema.IpAddressAllocationModeType;
import com.vmware.vcloud.api.rest.schema.NetworkConfigSectionType;
import com.vmware.vcloud.api.rest.schema.NetworkConfigurationType;
import com.vmware.vcloud.api.rest.schema.NetworkConnectionSectionType;
import com.vmware.vcloud.api.rest.schema.NetworkConnectionType;
import com.vmware.vcloud.api.rest.schema.ObjectFactory;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.ResourceReferenceType;
import com.vmware.vcloud.api.rest.schema.TaskStatusType;
import com.vmware.vcloud.api.rest.schema.VAppNetworkConfigurationType;
import com.vmware.vcloud.api.rest.schema.ovf.MsgType;
import com.vmware.vcloud.api.rest.schema.ovf.SectionType;
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
public class OvfCatalogServiceImpl extends RemoteServiceServlet implements
    OvfCatalogService {
	
	static VcloudClient client;
	
	/**
	 * Static initialisation
	 */
	{
		staticInit();
	}
	
	/**
	 * Authenticate to vcloud API
	 */
	private boolean staticInit() {
		InputStream in = getClass().getResourceAsStream("vmware.properties");
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
	 * Retrieves a list of Vapps
	 * @return
	 */
	public VmValue[] getVMs() throws OvfCatalogException {
		try {

			//iterate VDCs
			for(ReferenceType vdcRef : getVdcRefs()) {
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
					
					vmValue[i++] = new VmValue(vmName, vApp.getVappStatus(), ips);
				}
				return vmValue;
			}
			return null;
		} catch (VCloudException e) {
			ErrorType vCloudError = e.getVcloudError();
			if(vCloudError.getMajorErrorCode()==403) {
				//possible session timeout, reauthenticate
				client = null;
				return getVMs();
			}
			throw new OvfCatalogException(e.getMessage());
		}
	}
	
	/**
	 * Retrieves a list of Vapps
	 * @return
	 */
	public VmValue[] getTemplates() throws OvfCatalogException {
		try {
				
			//iterate VDCs
			for(ReferenceType vdcRef : getVdcRefs()) {
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
					if(waitForTaskCompletion(client, task, 100)) {
						vm = new VmValue(catName, vappTemplRef.getStatus(), null);
					} else {
						vm = new VmValue(catName, vappTemplRef.getStatus(), null);
					}
					vm.setCreateable(true);
					vmValue[i++] = vm;
				}
				return vmValue;
			}
			return null;
		} catch (VCloudException e) {
			throw new OvfCatalogException(e.getMessage());
		}
	}
	
	/**
	 * Stops VM by name
	 * @param name name of VM to stop
	 */
	public void stopVM(String name) throws OvfCatalogException {
		try {
			startStopVM(name, false);
		} catch (VCloudException e) {
			throw new OvfCatalogException(e.getMessage());
		}
	}
	
	/**
	 * Starts VM by name
	 * @param name name of VM to start
	 */
	public void startVM(String name) throws OvfCatalogException {
		try {
			startStopVM(name, true);
		} catch (VCloudException e) {
			throw new OvfCatalogException(e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param name
	 * @param start
	 * @throws OvfCatalogException 
	 */
	private void startStopVM(String name, boolean start) throws VCloudException, OvfCatalogException {
		
		//iterate VDCs
		for(ReferenceType vdcRef : getVdcRefs()) {
			Vdc vdc = Vdc.getVdcByReference(client, vdcRef);
			
			//find VM by name
			HashMap<String, ResourceReferenceType> vmRefs = vdc.getVappRefsByName();
			for(String vmName : vmRefs.keySet()) {
				if(vmName.equals(name)) {
					Vapp vApp = Vapp.getVappByReference(client, vmRefs.get(vmName));
					if(start==true) {
						System.out.println("	Configuring VM Ip Addressing Mode");
						List<Vapp> childVms = vApp.getChildrenVms();
						for (Vapp childVm : childVms) {
							NetworkConnectionSectionType networkConnectionSectionType = childVm
									.getNetworkConnectionSection();
							List<NetworkConnectionType> networkConnections = networkConnectionSectionType
									.getNetworkConnection();
							for (NetworkConnectionType networkConnection : networkConnections) {
								networkConnection
										.setIpAddressAllocationMode(IpAddressAllocationModeType.POOL);
								ReferenceType parentNetwork = vdc.getAvailableNetworkRefs().iterator().next();
								String parentNetworkName = parentNetwork.getName();
								networkConnection.setNetwork(parentNetworkName);
							}
							Task t = childVm.updateSection(networkConnectionSectionType);
							waitForTaskCompletion(client, t, 10000);
							for (String ip : Vapp.getVappByReference(client,
									childVm.getReference()).getIpAddresses()) {
								System.out.println("		" + ip);
							}
						}
						Task t = vApp.powerOn();
						waitForTaskCompletion(client, t, 100);
					} else {
						vApp.shutdown();
					}
					break;
				}	
			}
		}


	}
	
	/**
	 * Gets VDCRefs
	 * @throws VCloudException on vcloud API errors
	 * @throws OvfCatalogException if initialisation failed
	 */
	private Collection<ReferenceType> getVdcRefs() throws OvfCatalogException, VCloudException {
		
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
	
	public void createVM(String templateName, String vmName) throws OvfCatalogException {
		try {
			newVmFromTemplate(templateName, vmName);
		} catch (VCloudException e) {
			throw new OvfCatalogException(e.getMessage());
		}
	}
	
	public Vapp newVmFromTemplate(String templateName, String vmName) throws VCloudException, OvfCatalogException {
		
		ReferenceType vAppTemplateReference = null;
		Vdc vdc = null;
		
		for(ReferenceType vdcRef : getVdcRefs()) {
			Vdc curVdc = Vdc.getVdcByReference(client, vdcRef);
			for(ReferenceType vappTemplRef : curVdc.getVappTemplateRefs()) {
				if(vappTemplRef.getName().equals(templateName)) {
					if(vAppTemplateReference == null) {
						vAppTemplateReference = vappTemplRef;
						vdc = curVdc;
					} else {
						throw new OvfCatalogException("Duplicate vApp with name \"" + vappTemplRef + "\"");
					}
				}
			}
		}
		if(vAppTemplateReference==null || vdc==null) {
			throw new OvfCatalogException("template with name \"" + templateName + "\" not found");
		}
		
		System.out.println("Instantiating " + vAppTemplateReference.getName());
		System.out.println("-----------------------------");

		NetworkConfigurationType networkConfigurationType = new NetworkConfigurationType();
		if (vdc.getAvailableNetworkRefs().size() == 0) {
			throw new OvfCatalogException("No Networks in vdc to instantiate the vapp");
		}

		ReferenceType parentNetwork = vdc.getAvailableNetworkRefs().iterator().next();
		networkConfigurationType.setParentNetwork(parentNetwork);
		networkConfigurationType.setFenceMode(FenceModeValuesType.BRIDGED);

		VAppNetworkConfigurationType vAppNetworkConfigurationType = new VAppNetworkConfigurationType();
		vAppNetworkConfigurationType.setConfiguration(networkConfigurationType);
		String parentNetworkName = parentNetwork.getName();
		vAppNetworkConfigurationType.setNetworkName(parentNetworkName);

		NetworkConfigSectionType networkConfigSectionType = new NetworkConfigSectionType();
		MsgType networkInfo = new MsgType();
		networkConfigSectionType.setInfo(networkInfo);
		List<VAppNetworkConfigurationType> vAppNetworkConfigs = networkConfigSectionType
				.getNetworkConfig();
		vAppNetworkConfigs.add(vAppNetworkConfigurationType);

		InstantiationParamsType instantiationParamsType = new InstantiationParamsType();
		List<JAXBElement<? extends SectionType>> sections = instantiationParamsType
				.getSection();
		sections.add(new ObjectFactory()
				.createNetworkConfigSection(networkConfigSectionType));

		InstantiateVAppTemplateParamsType instVappTemplParamsType = new InstantiateVAppTemplateParamsType();
		instVappTemplParamsType.setName(vmName);
		instVappTemplParamsType.setSource(vAppTemplateReference);
		instVappTemplParamsType.setInstantiationParams(instantiationParamsType);

		Vapp vapp = vdc.instantiateVappTemplate(instVappTemplParamsType);
		
		return vapp;
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
