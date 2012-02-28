package uk.ac.ox.oucs.vidaas.utility;

import java.util.ArrayList;
import java.util.List;

public class DirectoryUtilities {
	
	public int removeProjectDir(String directoryLocation) {
		int result = -99;
		List<String> command = new ArrayList<String>();
		command.add("rm");
		command.add("-r");
		command.add(directoryLocation);

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
			
			// dataHolder.setOkButton(false);
		}

		System.out.println("Process Result: " + result);
		return result;
	}

}
