package uk.ac.ox.oucs.vidaas.create;

import java.io.*;
import java.util.*;

import uk.ac.ox.oucs.vidaas.utility.XMLDatabaseEnvironment;
import uk.ac.ox.oucs.vidaas.utility.mdConst;

import com.sleepycat.db.*;
import com.sleepycat.dbxml.*;

public class CreateBerkeleyXMLDatabase {
	
	private XmlManager theMgr = null;
	private XmlContainer openedContainer = null;
	private Environment env = null;
	private XmlTransaction txn = null;
	
	
	private  XmlContainer xmlDatabaseContainer(String containerPath,
			String databaseName) {		

		File path2DbEnv = new File(containerPath);
		try {
			env = XMLDatabaseEnvironment.createEnvironment(path2DbEnv);

			theMgr = new XmlManager(env, new XmlManagerConfig());						

		} catch (Exception e) {
			e.printStackTrace();
		}

		return openedContainer;
	}

	public void loadXMLFileInContainer(String containerPath,
			String databaseName, String pathValue, String fileValue, BufferedWriter outValue) {

		try {
			outValue.write("Retrieving XML Manager" + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(theMgr == null){
			xmlDatabaseContainer(containerPath, databaseName);
		}
		// create a transactional container
		XmlContainerConfig config = new XmlContainerConfig();
		//config.setTransactional(true);
		try {
			outValue.write("Creating / Opening XML Container" + "\n");
		if (theMgr.existsContainer(databaseName) == 0) {
			openedContainer = theMgr.createContainer(databaseName, config);
		}

		openedContainer = theMgr.openContainer(databaseName, config);
		
		//Transaction dbtxn = env.beginTransaction(null, null);
		//txn = theMgr.createTransaction(dbtxn);

		outValue.write("Loading File" + "\n");
		XmlInputStream inputSteam = 
            theMgr.createLocalFileInputStream(pathValue + fileValue);

		openedContainer.putDocument(fileValue, inputSteam, XmlDocumentConfig.DEFAULT);
		outValue.write("Added " + fileValue + " to container" +
				databaseName + "\n");
		} catch (Exception exp){
			exp.printStackTrace();
		}
	}

}
