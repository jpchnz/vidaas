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
	
	/**
	 * Retrieves a list of Vapps
	 * @return
	 */
	public VmValue[] getVMs() throws OvfCatalogException {
		try {

			Vdc vdc = VcloudSession.getVDC();
			
			//copy to value class
			HashMap<String, ResourceReferenceType> vmRefs = vdc.getVappRefsByName();
			VmValue[] vmValue = new VmValue[vmRefs.size()];
			int i = 0;
			for(String vmName : vmRefs.keySet()) {
				Vapp vApp = Vapp.getVappByReference(VcloudSession.getClient(), vmRefs.get(vmName));
				
				List<Vapp> vms = vApp.getChildrenVms();
				HashSet<String> ips = new HashSet<String>();
				int busy = 0;
				for (Vapp vm : vms) {
					ips.addAll(vm.getIpAddresses());
					busy = vm.getTasks().size();
				}
				
				vmValue[i++] = new VmValue(vmName, vApp.getVappStatus(), ips, busy);
			}
			return vmValue;

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
				
			Vdc vdc = VcloudSession.getVDC();
			
			//copy to value class
			Collection<ResourceReferenceType> vappTemplRefs = vdc.getVappTemplateRefs();
			VmValue vmValue[] = new VmValue[vappTemplRefs.size()];
			int i=0;
			for(ResourceReferenceType vappTemplRef : vappTemplRefs) {
				String catName = vappTemplRef.getName();
				
				VappTemplate vappTemplate = VappTemplate.getVappTemplateByReference(VcloudSession.getClient(), vappTemplRef);
				Task task = vappTemplate.enableDownload();
				VmValue vm;
				if(VcloudSession.waitForTaskCompletion(task, 100)) {
					vm = new VmValue(catName, vappTemplRef.getStatus(), null, vappTemplate.getTasks().size());
				} else {
					vm = new VmValue(catName, vappTemplRef.getStatus(), null, vappTemplate.getTasks().size());
				}
				vm.setCreateable(true);
				vmValue[i++] = vm;
			}
			return vmValue;

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
		
		Vdc vdc = VcloudSession.getVDC();
		
		//find VM by name
		HashMap<String, ResourceReferenceType> vmRefs = vdc.getVappRefsByName();
		for(String vmName : vmRefs.keySet()) {
			if(vmName.equals(name)) {
				Vapp vApp = Vapp.getVappByReference(VcloudSession.getClient(), vmRefs.get(vmName));
				if(start==true) {
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
							networkConnection.setIsConnected(true);
						}
						Task t = childVm.updateSection(networkConnectionSectionType);
						VcloudSession.waitForTaskCompletion(t, 10000);
					}
					Task t = vApp.powerOn();
					VcloudSession.waitForTaskCompletion(t, 100);
				} else {
					vApp.shutdown();
				}
				break;
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
		Vdc vdc = VcloudSession.getVDC();

		for(ReferenceType vappTemplRef : vdc.getVappTemplateRefs()) {
			if(vappTemplRef.getName().equals(templateName)) {
				vAppTemplateReference = vappTemplRef;
				break;
			}
		}

		if(vAppTemplateReference==null) {
			throw new OvfCatalogException("template with name \"" + templateName + "\" not found");
		}

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

}
