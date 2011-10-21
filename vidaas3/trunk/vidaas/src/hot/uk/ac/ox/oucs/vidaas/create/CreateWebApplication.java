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

import uk.ac.ox.oucs.vidaas.utility.SystemCommandExecutor;

public class CreateWebApplication {
	
	public void createWebApplication(String webApplicationName, String webApplicationLocation, 
			String databaseName, String userName, String password){
		copySeamDir(webApplicationName, webApplicationLocation);
		createDeployDirectory(webApplicationLocation);
		
		String tempDirectoryName = webApplicationLocation + webApplicationName;
		Properties properties = new Properties();

        readPropertiesFile(properties, tempDirectoryName);

        updatePropertiesFile(properties, "project.name", webApplicationName);
        updatePropertiesFile(properties, "workspace.home", webApplicationLocation + "seamProject");
        updatePropertiesFile(properties, "hibernate.connection.username", userName);
        updatePropertiesFile(properties, "hibernate.connection.password", password);
        updatePropertiesFile(properties, "hibernate.connection.url", "jdbc:postgresql://localhost/"+ databaseName);
		
        writePropertiesFile(properties, tempDirectoryName);
        
        runSeamCommandCreateProject(tempDirectoryName);
        runSeamCommandGenerateEntities(tempDirectoryName);
        runSeamCommandExplodeProject(tempDirectoryName);
        
        removeSeamDir(webApplicationName, webApplicationLocation);
        removeProjectDir(webApplicationLocation);
	}
	
	private int copySeamDir(String webApplicationName, String webApplicationLocation) {
        int result = -99;
        List<String> command = new ArrayList<String>();
        command.add("cp");
        command.add("-r");
        command.add("/opt/Seam/jboss-seam-2.2.2.Final");
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
        }
        return result;
    }
	
	private boolean createDeployDirectory(String webApplicationLocation){
		boolean status;
	    status = new File(webApplicationLocation + "seamProject").mkdir();
	    
	    return status;
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
            System.out.println("STDOUT");
            System.out.println(stdout);
            System.out.println("STDERR");
            System.out.println(stderr);
        } catch (Exception e) {
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
        }
        return result;
    }
	
	private void updatePropertiesFile(Properties properties, String key, String value) {
        properties.getProperty(key);
        properties.setProperty(key, value);
    }

    private void writePropertiesFile(Properties properties, String tempDirectoryNameWithPath) {
        try {
            properties.store(new FileOutputStream(tempDirectoryNameWithPath + "/seam-gen/build.properties"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void readPropertiesFile(Properties properties, String tempDirectoryNameWithPath) {
        try {
            properties.load(new FileInputStream(tempDirectoryNameWithPath + "/seam-gen/build.properties"));
        } catch (IOException e) {
        }

        Set<String> propertiesEntryList = properties.stringPropertyNames();
        Iterator<String> propertiesEntryListIterator = propertiesEntryList.iterator();
        while (propertiesEntryListIterator.hasNext()) {
            String entryString = propertiesEntryListIterator.next();
            System.out.println(entryString + ": " + properties.getProperty(entryString));
        }
    }

}
