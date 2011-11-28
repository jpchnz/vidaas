package uk.ac.ox.oucs.vidaas.delete;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ox.oucs.vidaas.utility.SystemCommandExecutor;

public class DeleteWebApplication {
	
	public boolean undeployWebApplication(String webApplicationName){
		
		String serverLocationTemp = System.getProperty("serverLocation");
		
		removeWebApplication(webApplicationName + ".war", serverLocationTemp + "/server/default/deploy/");
		
		File file = new File(serverLocationTemp + "/server/default/deploy/" + webApplicationName + ".war");
		
		return !file.isDirectory();
	}

	private int removeWebApplication(String webApplicationName, String webApplicationLocation) {
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
        	System.out.println("Failed to undeploy the Web Application");
        	//dataHolder.setOkButton(false);
        }
        return result;
    }
}
