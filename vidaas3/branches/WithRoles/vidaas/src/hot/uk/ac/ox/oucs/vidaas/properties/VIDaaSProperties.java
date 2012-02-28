package uk.ac.ox.oucs.vidaas.properties;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("vidaasPriperties")
@Scope(ScopeType.APPLICATION)
public class VIDaaSProperties {

	// Utility variable to flag whether properties are loaded or not
	private boolean propertiesLoaded = false;

	// The local absolute path to Seam Directory
	private String seamLocation;

	// The local absolute path to the server where Application will be deployed
	private String serverLocation;

	// URL for the server to access deployed application
	private String serverURL;

	// The local absolute path to the directory for VIDaaS generated files
	private String vidaasDataLocation;

	// The local absolute path to the jdbc driver for the database used by Seam
	private String jdbcDriverLocation;

	public boolean isPropertiesLoaded() {
		return propertiesLoaded;
	}

	public void setPropertiesLoaded(boolean propertiesLoaded) {
		this.propertiesLoaded = propertiesLoaded;
	}

	public String getSeamLocation() {
		return seamLocation;
	}

	public void setSeamLocation(String seamLocation) {
		this.seamLocation = seamLocation;
	}

	public String getServerLocation() {
		return serverLocation;
	}

	public void setServerLocation(String serverLocation) {
		this.serverLocation = serverLocation;
	}

	public String getServerURL() {
		return serverURL;
	}

	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

	public String getVidaasDataLocation() {
		return vidaasDataLocation;
	}

	public void setVidaasDataLocation(String vidaasDataLocation) {
		this.vidaasDataLocation = vidaasDataLocation;
	}

	public String getJdbcDriverLocation() {
		return jdbcDriverLocation;
	}

	public void setJdbcDriverLocation(String jdbcDriverLocation) {
		this.jdbcDriverLocation = jdbcDriverLocation;
	}

}
