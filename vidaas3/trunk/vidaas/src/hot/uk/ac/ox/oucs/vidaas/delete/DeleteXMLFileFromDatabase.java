package uk.ac.ox.oucs.vidaas.delete;

import java.io.File;

import uk.ac.ox.oucs.vidaas.utility.XMLDatabaseEnvironment;

import com.sleepycat.db.Environment;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlContainerConfig;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlManagerConfig;
import com.sleepycat.dbxml.XmlTransaction;

public class DeleteXMLFileFromDatabase {

	private XmlManager theMgr = null;
	private XmlContainer openedContainer = null;
	private Environment env = null;
	//private XmlTransaction txn = null;
	
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
	
	public void deleteXMLFileInContainer(String containerPath,
			String databaseName, String fileValue){
		System.out.println("Before deleteXMLFileInContainer");
		if(theMgr == null){
			xmlDatabaseContainer(containerPath, databaseName);
		}
		
		XmlContainerConfig config = new XmlContainerConfig();
		
		try {
			openedContainer = theMgr.openContainer(databaseName, config);
			
			openedContainer.deleteDocument(fileValue);
			
			openedContainer.close();
			theMgr.close();
			System.out.println("After deleteXMLFileInContainer");
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
