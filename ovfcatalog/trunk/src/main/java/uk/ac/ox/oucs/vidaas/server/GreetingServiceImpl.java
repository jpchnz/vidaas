package uk.ac.ox.oucs.vidaas.server;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import uk.ac.ox.oucs.vidaas.client.GreetingService;
import uk.ac.ox.oucs.vidaas.shared.FieldVerifier;
import uk.ac.ox.oucs.vidaas.shared.OvfCatalogException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.VCloudException;
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
		String URL = "https://vcloud2.nsms.ox.ac.uk/api/versions";
		String username = "chris@VIDaaS";
		String password = "ch37*27aB5(";
		String version = "1.0";

		try {
			// Setting up for SSL access. Do not use it production environment
			Protocol https = new Protocol("https",
					(ProtocolSocketFactory) new FakeSSLSocketFactory(), 443);
			Protocol.registerProtocol("https", https);
	
			client = new VcloudClient(URL);
			// Client login

			HashMap<String, ReferenceType> orgsList = client.login(username,
					password, client.getSupportedVersions().get(version));
		} catch (Exception e) {
			client = null;
		}
	}
	
	/**
	 * Retrieves a list of Vapps
	 * @return
	 */
	public Set<String> getVMs() throws OvfCatalogException {
		try {
			HashMap<String, ReferenceType> orgsList = client.getOrgRefsByName();
			
			//iterate vOrgs
			for (String vOrg : orgsList.keySet()) {
				ReferenceType orgRef = orgsList.get(vOrg);
				Organization org = Organization.getOrganizationByReference(client, orgRef);
				
				//iterate VDCs
				for(ReferenceType vdcRef : org.getVdcRefs()) {
					Vdc vdc = Vdc.getVdcByReference(client, vdcRef);
					
					return vdc.getVappRefsByName().keySet();
				}
			}
			return null;
		} catch (VCloudException e) {
			throw new OvfCatalogException(e.getMessage());
		}
	}

  public String greetServer() throws OvfCatalogException {

    String response = "";
    for ( String vm : getVMs() ) {
    	response = response + escapeHtml(vm) + "<br/>";
    }
    return response;
  }

  /**
   * Escape an html string. Escaping data received from the client helps to
   * prevent cross-site script vulnerabilities.
   *
   * @param html the html string to escape
   * @return the escaped string
   */
  private String escapeHtml(String html) {
    if (html == null) {
      return null;
    }
    return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(
        ">", "&gt;");
  }

}
