package uk.ac.ox.oucs.vidaas.concurrency;

import uk.ac.ox.oucs.vidaas.create.CreateWebApplication;
import uk.ac.ox.oucs.vidaas.data.holder.DataHolder;

public class CreateWebApplicationThread  implements Runnable {
	
	private DataHolder dataHolder = null;
	
	private String webApplicationName;
	private String databaseName;
	private String userName;
	private String password;
	private String webApplicationLocation;
	
	public CreateWebApplicationThread(String webApplicationNameValue, String webApplicationLocationValue, 
			String databaseNameValue, String userNameValue, String passwordValue, DataHolder dataHolderValue){
		
		this.webApplicationName = webApplicationNameValue;
		this.webApplicationLocation = webApplicationLocationValue;
		this.databaseName = databaseNameValue;
		this.userName = userNameValue;
		this.password = passwordValue;
		
		dataHolder = dataHolderValue;
	}

	public void run(){
		
		boolean processOutcome = false;
		String serverURLTemp = System.getProperty("serverURL");
		
		dataHolder.setOkButton(true);
		
		CreateWebApplication createWebApplication = new CreateWebApplication();
		processOutcome = createWebApplication.createWebApplication(webApplicationName,
				webApplicationLocation, databaseName, userName, password, dataHolder);
		
		if (processOutcome == true){
			dataHolder.setCurrentStatus("Web Application will be available at: '" + serverURLTemp + webApplicationName + "' after few minutes");
		}
		dataHolder.setOkButton(false);
	}
}
