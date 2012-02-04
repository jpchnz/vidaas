package uk.ac.ox.oucs.vidaas.download;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jboss.seam.log.Log;

import uk.ac.ox.oucs.vidaas.manager.ConnectionManager;

public class DownloadDatabase {
	
	private final String rootStorageDirectory = "/opt/VIDaaSData/";
	
	public String dumpDatabaseForDownload(Integer projectID, String dataspaceName, String databaseName, 
			String databaseVersion, Log logger){
		
		logger.info("dumpDatabaseForDownload() called");

		String projectName = "project_" + projectID;

		// databaseDirectory = /opt/vidaasData/project_2/db_test/
		String databaseDirectory = rootStorageDirectory + projectName + "/"
				+ dataspaceName + "/" + databaseVersion + "/";
		
		String csvDumpFile = stringValidation(databaseName) + ".csv";
		
		dumpDatabaseForDownload(databaseDirectory + csvDumpFile, stringValidation(databaseName));
		
		return databaseDirectory + csvDumpFile;
	}
	
	private void dumpDatabaseForDownload(String dumpFileNameValue, String databaseName) {
		System.out.println("dumpDatabaseForDownload: " + dumpFileNameValue + "  " +  databaseName);
		try {	
			Process p;
            ProcessBuilder pb;
            
            List<String> cmds = new ArrayList<String>();
            cmds.add("/usr/bin/pg_dump");
            cmds.add("-i");
            cmds.add("-h");
            cmds.add("localhost");
            cmds.add("-p");
            cmds.add("5432");
            cmds.add("-U");
            cmds.add(new ConnectionManager().getAdminUserName());
            cmds.add("-a");
            cmds.add("-F");
            cmds.add("P");
            cmds.add("-b");
            cmds.add("-v");
            cmds.add("-f");
            cmds.add(dumpFileNameValue);
            cmds.add(databaseName.toLowerCase());
            
            pb = new ProcessBuilder(cmds);
            pb.environment().put("PGPASSWORD", new ConnectionManager().getAdminUserNamePW());
            //pb.redirectErrorStream(true);
            p = pb.start();
            try {
                InputStream is = p.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String ll;
                while ((ll = br.readLine()) != null) {
                    System.out.println(ll);
                }
                InputStream isE = p.getErrorStream();
                InputStreamReader isrE = new InputStreamReader(isE);
                BufferedReader brE = new BufferedReader(isrE);
                String llE;
                while ((llE = brE.readLine()) != null) {
                    System.out.println(llE);
                }
            } catch (IOException e) {
                System.out.println(e);
                //log("ERROR "+e.getMessage());
            }

		} catch (Exception e) {

		}
	}
	
	private String stringValidation(String input) {
		Pattern escaper = Pattern.compile("(^[\\d]*)");
		Pattern escaper2 = Pattern.compile("[^a-zA-z0-9]");

		String newString = escaper.matcher(input).replaceAll("");
		String newString2 = escaper2.matcher(newString).replaceAll("");

		return newString2;

	}

}
