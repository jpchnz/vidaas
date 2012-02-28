package uk.ac.ox.oucs.vidaas.concurrency;

import uk.ac.ox.oucs.vidaas.properties.VIDaaSProperties;

public class VIDaaSPropertiesReaderThread implements Runnable {
	
	private VIDaaSProperties vidaasProperties;
	
	public VIDaaSPropertiesReaderThread(VIDaaSProperties vidaasPropertiesValue){
		this.vidaasProperties = vidaasPropertiesValue;
	}
	
	public void run(){ 
		
		String seamLocaionTemp = System.getProperty("seamLocaion");
		String serverLocationTemp = System.getProperty("serverLocation");
		String serverURLTemp = System.getProperty("serverURL");
		String vidaasDataLocationTemp = System.getProperty("VIDaaSDataLocation");
		String jdbcDriverJarTemp = System.getProperty("jdbcDriverJar");		
		
		vidaasProperties.setSeamLocation(seamLocaionTemp);
		vidaasProperties.setServerLocation(serverLocationTemp);
		vidaasProperties.setServerURL(serverURLTemp);
		vidaasProperties.setVidaasDataLocation(vidaasDataLocationTemp);
		vidaasProperties.setJdbcDriverLocation(jdbcDriverJarTemp);
		
		vidaasProperties.setPropertiesLoaded(true);
	}

}
