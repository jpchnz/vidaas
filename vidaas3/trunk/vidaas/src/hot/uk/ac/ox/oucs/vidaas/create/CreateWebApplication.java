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

	public boolean createWebApplication(String webApplicationName,
			String webApplicationLocation, String databaseName,
			String userName, String password, DataHolder dataHolderValue) {

		int processResult = -88;
		boolean processOutcome = false;

		dataHolder = dataHolderValue;

		String seamLocaionTemp = System.getProperty("seamLocaion");
		String serverLocationTemp = System.getProperty("serverLocation");
		String jdbcDriverJarTemp = System.getProperty("jdbcDriverJar");

		// System.out.println(seamLocaionTemp + "  " + serverLocationTemp + "  "
		// + jdbcDriverJarTemp);

		dataHolder.setCurrentStatus("Building Environment");
		processResult = copySeamDir(webApplicationName, webApplicationLocation,
				seamLocaionTemp);
		if (processResult == 0) {

			// Deploy Directory is webApplicationLocation + /seamProject
			dataHolder.setCurrentStatus("Creating Web Application Direcotry");
			createDeployDirectory(webApplicationLocation);

			String tempDirectoryName = webApplicationLocation
					+ webApplicationName;
			Properties properties = new Properties();

			dataHolder.setCurrentStatus("Reading Default Settings");
			processOutcome = readPropertiesFile(properties, tempDirectoryName);
			if (processOutcome == true) {

				dataHolder.setCurrentStatus("Updating Default Settings");

				updatePropertiesFile(properties, "project.name",
						webApplicationName);
				updatePropertiesFile(properties, "workspace.home",
						webApplicationLocation + "seamProject");

				updatePropertiesFile(properties, "driver.jar",
						jdbcDriverJarTemp);
				updatePropertiesFile(properties, "jboss.home",
						serverLocationTemp);

				updatePropertiesFile(properties,
						"hibernate.connection.username", userName.toLowerCase());
				updatePropertiesFile(properties,
						"hibernate.connection.password", password);
				updatePropertiesFile(properties, "hibernate.connection.url",
						"jdbc:postgresql://localhost/" + databaseName);

				if (processOutcome == true) {
					processOutcome = writePropertiesFile(properties,
							tempDirectoryName);

					dataHolder
							.setCurrentStatus("Creating data interface.  Please wait.");
					// runSeamCommandsSingleStep(tempDirectoryName);

					processResult = runSeamCommandCreateProject(tempDirectoryName);
					if (processResult == 0) {
						// Hack to remove
						processResult = removeMenuPage(webApplicationName,
								webApplicationLocation);
						if (processResult == 0) {
							dataHolder
									.setCurrentStatus("Reverse Engineering From Database");
							processResult = runSeamCommandGenerateEntities(tempDirectoryName);

							if (processResult == 0) {

								dataHolder
										.setCurrentStatus("Deploying Database Interface");
								processResult = runSeamCommandExplodeProject(tempDirectoryName);
								
								if (processResult == 0) {
								/**/
								dataHolder
										.setCurrentStatus("Removing Temporary Files");
								processResult = removeSeamDir(
										webApplicationName,
										webApplicationLocation);
								processResult = removeProjectDir(webApplicationLocation); 
								return true;
								} else {
									dataHolder
									.setCurrentStatus("Failed to deploy Database Interface");
								}
							} else {
								dataHolder
								.setCurrentStatus("Failed to retrieve meta-data from the database");
							}
						} else {
							dataHolder
							.setCurrentStatus("Failed to update the database interface");
						}
					} else {
						dataHolder
						.setCurrentStatus("Failed to create database interface");
					}
				} else {
					dataHolder
					.setCurrentStatus("Failed to update properties for the new data interface");
				} 
			} else {
				dataHolder
				.setCurrentStatus("Failed to read properties for the new data interface");
			}
		} else {
			dataHolder
			.setCurrentStatus("Failed to copy directories for the data interface");
		}
		
		return false;

		// dataHolder.setOkButton(false);
	}

	// Content of jboss-seam-2.2.2.Final dir will be copied to
	// /opt/VIDaaSData/project_1/Rivers/main/romanrivers
	// romanriver is webApplicationName
	private int copySeamDir(String webApplicationName,
			String webApplicationLocation, String seamLocation) {
		int result = -99;
		List<String> command = new ArrayList<String>();
		command.add("cp");
		command.add("-r");
		command.add(seamLocation);
		command.add(webApplicationLocation + webApplicationName);

		SystemCommandExecutor commandExecutor = new SystemCommandExecutor(
				command);
		try {
			result = commandExecutor.executeCommand();
			StringBuilder stdout = commandExecutor
					.getStandardOutputFromCommand();
			StringBuilder stderr = commandExecutor
					.getStandardErrorFromCommand();

			// print the output from the command
			/* */
			System.out.println("STDOUT");
			System.out.println(stdout);
			System.out.println("STDERR");
			System.out.println(stderr);
		} catch (Exception e) {
			e.printStackTrace();
			dataHolder.setCurrentStatus("Failed to Build Environment");
			// dataHolder.setOkButton(false);
		}
		System.out.println("Process Result for Build Environment : " + result);
		return result;
	}

	private boolean createDeployDirectory(String webApplicationLocation) {
		boolean status;
		status = new File(webApplicationLocation + "seamProject").mkdir();

		return status;
	}

	private int runSeamCommandsSingleStep(String tempDirectoryNameWithPath) {
		int result = -99;

		List<String> command = new ArrayList<String>();
		command.add(tempDirectoryNameWithPath + "/VIDaaS-SeamScript.sh");
		command.add(tempDirectoryNameWithPath);

		SystemCommandExecutor commandExecutor = new SystemCommandExecutor(
				command);
		try {
			result = commandExecutor.executeCommand();
			StringBuilder stdout = commandExecutor
					.getStandardOutputFromCommand();
			StringBuilder stderr = commandExecutor
					.getStandardErrorFromCommand();

			// print the output from the command

			System.out.println("STDOUT");
			System.out.println(stdout);
			System.out.println("STDERR");
			System.out.println(stderr);
		} catch (Exception e) {
			e.printStackTrace();
			dataHolder.setCurrentStatus("Failed to Gerate Web Application");
		}

		System.out.println("Process Result: " + result);
		return result;
	}

	private int runSeamCommandCreateProject(String tempDirectoryNameWithPath) {
		int result = -99;
		List<String> command = new ArrayList<String>();
		command.add(tempDirectoryNameWithPath + "/VIDaaS-SeamScript.sh");
		command.add(tempDirectoryNameWithPath);
		command.add("project");

		SystemCommandExecutor commandExecutor = new SystemCommandExecutor(
				command);
		try {
			result = commandExecutor.executeCommand();
			StringBuilder stdout = commandExecutor
					.getStandardOutputFromCommand();
			StringBuilder stderr = commandExecutor
					.getStandardErrorFromCommand();

			// print the output from the command

			System.out.println("STDOUT");
			System.out.println(stdout);
			System.out.println("STDERR");
			System.out.println(stderr);
		} catch (Exception e) {
			e.printStackTrace();
			dataHolder.setCurrentStatus("Failed to create Project");
			// dataHolder.setOkButton(false);
		}
		System.out.println("Process Result: " + result);
		return result;
	}

	private int runSeamCommandGenerateEntities(String tempDirectoryNameWithPath) {
		int result = -99;
		List<String> command = new ArrayList<String>();
		command.add(tempDirectoryNameWithPath + "/VIDaaS-SeamScript.sh");
		command.add(tempDirectoryNameWithPath);
		command.add("entity");

		SystemCommandExecutor commandExecutor = new SystemCommandExecutor(
				command);
		try {
			result = commandExecutor.executeCommand();
			StringBuilder stdout = commandExecutor
					.getStandardOutputFromCommand();
			StringBuilder stderr = commandExecutor
					.getStandardErrorFromCommand();

			// print the output from the command

			System.out.println("STDOUT");
			System.out.println(stdout);
			System.out.println("STDERR");
			System.out.println(stderr);
		} catch (Exception e) {
			e.printStackTrace();
			dataHolder.setCurrentStatus("Failed to do Reverse Engineering");
			// dataHolder.setOkButton(false);
		}
		System.out.println("Process Result: " + result);
		return result;
	}

	private int runSeamCommandExplodeProject(String tempDirectoryNameWithPath) {
		int result = -99;
		List<String> command = new ArrayList<String>();
		command.add(tempDirectoryNameWithPath + "/VIDaaS-SeamScript.sh");
		command.add(tempDirectoryNameWithPath);
		command.add("deploy");

		SystemCommandExecutor commandExecutor = new SystemCommandExecutor(
				command);
		try {
			result = commandExecutor.executeCommand();
			StringBuilder stdout = commandExecutor
					.getStandardOutputFromCommand();
			StringBuilder stderr = commandExecutor
					.getStandardErrorFromCommand();

			// print the output from the command
			/* */
			System.out.println("STDOUT");
			System.out.println(stdout);
			System.out.println("STDERR");
			System.out.println(stderr);
		} catch (Exception e) {
			e.printStackTrace();
			dataHolder.setCurrentStatus("Failed to Deploy Project");
			// dataHolder.setOkButton(false);
		}
		System.out.println("Process Result: " + result);
		return result;
	}

	private int removeSeamDir(String webApplicationName,
			String webApplicationLocation) {
		int result = -99;
		List<String> command = new ArrayList<String>();
		command.add("rm");
		command.add("-r");
		command.add(webApplicationLocation + webApplicationName);

		SystemCommandExecutor commandExecutor = new SystemCommandExecutor(
				command);
		try {
			result = commandExecutor.executeCommand();
			StringBuilder stdout = commandExecutor
					.getStandardOutputFromCommand();
			StringBuilder stderr = commandExecutor
					.getStandardErrorFromCommand();

			// print the output from the command
			System.out.println("STDOUT");
			System.out.println(stdout);
			System.out.println("STDERR");
			System.out.println(stderr);
		} catch (Exception e) {
			e.printStackTrace();
			dataHolder
					.setCurrentStatus("Failed to remove Temportay Directory and Files");
			// dataHolder.setOkButton(false);
		}

		System.out.println("Process Result: " + result);
		return result;
	}

	private int removeMenuPage(String webApplicationName,
			String webApplicationLocation) {
		int result = -99;
		List<String> command = new ArrayList<String>();
		command.add("rm");
		// command.add("*");
		System.out.println("@@@ " + webApplicationLocation + "seamProject/"
				+ webApplicationName + "/view/layout/menu.xhtml");

		// dataHolder.setCurrentStatus(webApplicationLocation + "seamProject/" +
		// webApplicationName + "/exploded-archives/" + webApplicationName +
		// ".war/layout/*");

		command.add(webApplicationLocation + "seamProject/"
				+ webApplicationName + "/view/layout/menu.xhtml");
		SystemCommandExecutor commandExecutor = new SystemCommandExecutor(
				command);
		try {
			result = commandExecutor.executeCommand();
			StringBuilder stdout = commandExecutor
					.getStandardOutputFromCommand();
			StringBuilder stderr = commandExecutor
					.getStandardErrorFromCommand();

			// print the output from the command
			System.out.println("STDOUT");
			System.out.println(stdout);
			System.out.println("STDERR");
			System.out.println(stderr);
		} catch (Exception e) {
			e.printStackTrace();
			dataHolder
					.setCurrentStatus("Failed to remove Temporary Directory and Files");
			// dataHolder.setOkButton(false);
		}

		System.out.println("Process Result: " + result);

		return result;
	}

	private int removeProjectDir(String webApplicationLocation) {
		int result = -99;
		List<String> command = new ArrayList<String>();
		command.add("rm");
		command.add("-r");
		command.add(webApplicationLocation + "seamProject");

		SystemCommandExecutor commandExecutor = new SystemCommandExecutor(
				command);
		try {
			result = commandExecutor.executeCommand();
			StringBuilder stdout = commandExecutor
					.getStandardOutputFromCommand();
			StringBuilder stderr = commandExecutor
					.getStandardErrorFromCommand();

			// print the output from the command
			System.out.println("STDOUT");
			System.out.println(stdout);
			System.out.println("STDERR");
			System.out.println(stderr);
		} catch (Exception e) {
			e.printStackTrace();
			dataHolder
					.setCurrentStatus("Failed to remove Temportay Directory and Files");
			// dataHolder.setOkButton(false);
		}

		System.out.println("Process Result: " + result);
		return result;
	}

	private void updatePropertiesFile(Properties properties, String key,
			String value) {
		properties.getProperty(key);
		properties.setProperty(key, value);
	}

	private boolean writePropertiesFile(Properties properties,
			String tempDirectoryNameWithPath) {
		try {
			properties.store(new FileOutputStream(tempDirectoryNameWithPath
					+ "/seam-gen/build.properties"), null);
			return true;
		} catch (IOException e) {
			dataHolder
					.setCurrentStatus("Failed to update Default Configuration");
			// dataHolder.setOkButton(false);
			e.printStackTrace();
		}
		return false;
	}

	private boolean readPropertiesFile(Properties properties,
			String tempDirectoryNameWithPath) {
		try {
			properties.load(new FileInputStream(tempDirectoryNameWithPath
					+ "/seam-gen/build.properties"));

		} catch (IOException e) {
			e.printStackTrace();
			dataHolder.setCurrentStatus("Failed to read Default Configuration");
			return false;
			// dataHolder.setOkButton(false);
		}

		Set<String> propertiesEntryList = properties.stringPropertyNames();
		Iterator<String> propertiesEntryListIterator = propertiesEntryList
				.iterator();
		while (propertiesEntryListIterator.hasNext()) {
			String entryString = propertiesEntryListIterator.next();
			System.out.println(entryString + ": "
					+ properties.getProperty(entryString));
		}
		return true;
	}

}
