package uk.ac.ox.oucs.vidaas.create;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import uk.ac.ox.oucs.vidaas.data.holder.DataHolder;
import uk.ac.ox.oucs.vidaas.utility.SystemCommandExecutor;

public class CreateWebApplication {
	private DataHolder dataHolder = null;
	
	public void createWebApplication(String webApplicationName, String webApplicationLocation, 
			String databaseName, String userName, String password, DataHolder dataHolderValue){
		
		dataHolder = dataHolderValue;
		
		String seamLocaionTemp = System.getProperty("seamLocaion");
		String serverLocationTemp = System.getProperty("serverLocation");
		String jdbcDriverJarTemp = System.getProperty("jdbcDriverJar");	
		
		//System.out.println(seamLocaionTemp + "  " + serverLocationTemp +  "  " +  jdbcDriverJarTemp);
		
		dataHolder.setCurrentStatus("Building Environment");
		copySeamDir(webApplicationName, webApplicationLocation, seamLocaionTemp);
		
		// Deploy Directory is webApplicationLocation + /seamProject
		dataHolder.setCurrentStatus("Creating Web Application Direcotry");
		createDeployDirectory(webApplicationLocation);
		
		String tempDirectoryName = webApplicationLocation + webApplicationName;
		Properties properties = new Properties();

		dataHolder.setCurrentStatus("Reading Default Settings");
        readPropertiesFile(properties, tempDirectoryName);

        dataHolder.setCurrentStatus("Updating Default Settings");
        
        updatePropertiesFile(properties, "project.name", webApplicationName);
        updatePropertiesFile(properties, "workspace.home", webApplicationLocation + "seamProject");
        
        updatePropertiesFile(properties, "driver.jar", jdbcDriverJarTemp);
        updatePropertiesFile(properties, "jboss.home", serverLocationTemp);
        
        updatePropertiesFile(properties, "hibernate.connection.username", userName);
        updatePropertiesFile(properties, "hibernate.connection.password", password);
        updatePropertiesFile(properties, "hibernate.connection.url", "jdbc:postgresql://localhost/"+ databaseName);
		
        writePropertiesFile(properties, tempDirectoryName);
        
        dataHolder.setCurrentStatus("Creating Web Application. Be patient it may take few minutes.");
        runSeamCommandsSingleStep(tempDirectoryName);
        
        /*
        runSeamCommandCreateProject(tempDirectoryName);
        
        try {
        	Thread.sleep(10000);
        } catch (Exception ex){
        	
        }
        
        // Hack to remove 
        removeMenuPage(webApplicationName, webApplicationLocation);
        try {
        	Thread.sleep(5000);
        } catch (Exception ex){
        	
        }
        dataHolder.setCurrentStatus("Reverse Engineering From Database");
        runSeamCommandGenerateEntities(tempDirectoryName);
        
        try {
        	Thread.sleep(5000);
        } catch (Exception ex){
        	
        }
        dataHolder.setCurrentStatus("Deploying Web Application");
        runSeamCommandExplodeProject(tempDirectoryName);
        
        try {
        	Thread.sleep(500);
        } catch (Exception ex){
        	
        }
        */
        dataHolder.setCurrentStatus("Removing Temporary Files");
        removeSeamDir(webApplicationName, webApplicationLocation);
        removeProjectDir(webApplicationLocation);
        
        //dataHolder.setOkButton(false);
	}
	
	// Content of jboss-seam-2.2.2.Final dir will be copied to
	// /opt/VIDaaSData/project_1/Rivers/main/romanrivers
	// romanriver is webApplicationName
	private int copySeamDir(String webApplicationName, String webApplicationLocation, String seamLocation) {
        int result = -99;
        /*
         * FIXME
         * webApplicationName, webApplicationLocation, seamLocation
         * all obtained from environment vars and may be null
         * need to check for this
         */
        List<String> command = new ArrayList<String>();
        command.add("cp");
        command.add("-r");
        command.add(seamLocation);
        command.add(webApplicationLocation + webApplicationName);

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(command);
        try {
            result = commandExecutor.executeCommand();
            StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
            StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

            // print the output from the command
            /*
            System.out.println("STDOUT");
            System.out.println(stdout);
            System.out.println("STDERR");
            System.out.println(stderr);*/
        } catch (Exception e) {
        	dataHolder.setCurrentStatus("Failed to Build Environment");
        	e.printStackTrace();
        	//dataHolder.setOkButton(false);
        }
        return result;
    }
	
	private boolean createDeployDirectory(String webApplicationLocation){
		boolean status;
	    status = new File(webApplicationLocation + "seamProject").mkdir();
	    
	    return status;
	}
	
	private int runSeamCommandsSingleStep(String tempDirectoryNameWithPath){
		int result = -99;
		
		List<String> command = new ArrayList<String>();
        command.add(tempDirectoryNameWithPath + "/VIDaaS-SeamScript.sh");
        command.add(tempDirectoryNameWithPath);
        
        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(command);
        try {
        	result = commandExecutor.executeCommand();
            StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
            StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

            // print the output from the command
            
            System.out.println("STDOUT");
            System.out.println(stdout);
            System.out.println("STDERR");
            System.out.println(stderr);
        } catch (Exception e){
        	dataHolder.setCurrentStatus("Failed to Gerate Web Application");
        	e.printStackTrace();
        }
        
        System.out.println("Process Result: " + result);
		return result;
	}
	
	private int runSeamCommandCreateProject(String tempDirectoryNameWithPath) {
        int result = -99;
        List<String> command = new ArrayList<String>();
        command.add(tempDirectoryNameWithPath + "/seam");
        command.add("create-project");

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(command);
        try {
            result = commandExecutor.executeCommand();
            StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
            StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

            // print the output from the command
            
            System.out.println("STDOUT");
            System.out.println(stdout);
            System.out.println("STDERR");
            System.out.println(stderr);
        } catch (Exception e) {
        	dataHolder.setCurrentStatus("Failed to create Project");
        	e.printStackTrace();
        	//dataHolder.setOkButton(false);
        }
        return result;
    }
	
	private int runSeamCommandGenerateEntities(String tempDirectoryNameWithPath) {
        int result = -99;
        List<String> command = new ArrayList<String>();
        command.add(tempDirectoryNameWithPath + "/seam");
        command.add("generate-entities");

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(command);
        try {
            result = commandExecutor.executeCommand();
            StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
            StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

            // print the output from the command
            
            System.out.println("STDOUT");
            System.out.println(stdout);
            System.out.println("STDERR");
            System.out.println(stderr);
        } catch (Exception e) {
        	dataHolder.setCurrentStatus("Failed to do Reverse Engineering");
        	e.printStackTrace();
        	//dataHolder.setOkButton(false);
        }
        return result;
    }

    private int runSeamCommandExplodeProject(String tempDirectoryNameWithPath) {
        int result = -99;
        List<String> command = new ArrayList<String>();
        command.add(tempDirectoryNameWithPath + "/seam");
        command.add("explode");

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(command);
        try {
            result = commandExecutor.executeCommand();
            StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
            StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

            // print the output from the command
            /*
            System.out.println("STDOUT");
            System.out.println(stdout);
            System.out.println("STDERR");
            System.out.println(stderr);*/
        } catch (Exception e) {
        	dataHolder.setCurrentStatus("Failed to Deploy Project");
        	e.printStackTrace();
        	//dataHolder.setOkButton(false);
        }
        return result;
    }
    
    private int removeSeamDir(String webApplicationName, String webApplicationLocation) {
        int result = -99;
        List<String> command = new ArrayList<String>();
        command.add("rm");
        command.add("-r");
        command.add(webApplicationLocation + webApplicationName);

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(command);
        try {
            result = commandExecutor.executeCommand();
            StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
            StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

            // print the output from the command
            System.out.println("STDOUT");
            System.out.println(stdout);
            System.out.println("STDERR");
            System.out.println(stderr);
        } catch (Exception e) {
        	dataHolder.setCurrentStatus("Failed to remove Temportay Directory and Files");
        	e.printStackTrace();
        	//dataHolder.setOkButton(false);
        }
        return result;
    }
    private int removeMenuPage(String webApplicationName, String webApplicationLocation){
    	int result = -99;
    	 List<String> command = new ArrayList<String>();
         command.add("rm");
         //command.add("*");
         System.out.println(webApplicationLocation + "seamProject/" + webApplicationName + "/exploded-archives/" +webApplicationName + ".war/layout/*");
         
         dataHolder.setCurrentStatus(webApplicationLocation + "seamProject/" + webApplicationName + "/exploded-archives/" +webApplicationName + ".war/layout/*");
         
         command.add(webApplicationLocation + webApplicationName + "/exploded-archives/" +webApplicationName + ".war/layout");
         SystemCommandExecutor commandExecutor = new SystemCommandExecutor(command);
         try {
             result = commandExecutor.executeCommand();
             StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
             StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

             // print the output from the command
             System.out.println("STDOUT");
             System.out.println(stdout);
             System.out.println("STDERR");
             System.out.println(stderr);
         } catch (Exception e) {
        	 e.printStackTrace();
         	//dataHolder.setCurrentStatus("Failed to remove Temportay Directory and Files");
         	//dataHolder.setOkButton(false);
         }
         
    	return result;
    }
    private int removeProjectDir(String webApplicationLocation) {
        int result = -99;
        List<String> command = new ArrayList<String>();
        command.add("rm");
        command.add("-r");
        command.add(webApplicationLocation + "seamProject");

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(command);
        try {
            result = commandExecutor.executeCommand();
            StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
            StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

            // print the output from the command
            System.out.println("STDOUT");
            System.out.println(stdout);
            System.out.println("STDERR");
            System.out.println(stderr);
        } catch (Exception e) {
        	dataHolder.setCurrentStatus("Failed to remove Temporary Directory and Files");
        	e.printStackTrace();
        	//dataHolder.setOkButton(false);
        }
        return result;
    }
	
	private void updatePropertiesFile(Properties properties, String key, String value) {
		/*
		 * NOTE
		 * 
		 * Be very careful to not log these details - sometimes a password field may be passed it
		 */
        properties.getProperty(key);
        properties.setProperty(key, value);
    }

    private void writePropertiesFile(Properties properties, String tempDirectoryNameWithPath) {
        try {
            properties.store(new FileOutputStream(tempDirectoryNameWithPath + "/seam-gen/build.properties"), null);
        } catch (IOException e) {
        	dataHolder.setCurrentStatus("Failed to update Default Configuration");
        	//dataHolder.setOkButton(false);
            e.printStackTrace();
        }
    }
    
    private void readPropertiesFile(Properties properties, String tempDirectoryNameWithPath) {
        try {
            properties.load(new FileInputStream(tempDirectoryNameWithPath + "/seam-gen/build.properties"));
        } catch (IOException e) {
        	dataHolder.setCurrentStatus("Failed to read Default Configuration");
        	e.printStackTrace();
        	//dataHolder.setOkButton(false);
        }

        Set<String> propertiesEntryList = properties.stringPropertyNames();
        Iterator<String> propertiesEntryListIterator = propertiesEntryList.iterator();
        while (propertiesEntryListIterator.hasNext()) {
            String entryString = propertiesEntryListIterator.next();
            System.out.println(entryString + ": " + properties.getProperty(entryString));
        }
    }

}
