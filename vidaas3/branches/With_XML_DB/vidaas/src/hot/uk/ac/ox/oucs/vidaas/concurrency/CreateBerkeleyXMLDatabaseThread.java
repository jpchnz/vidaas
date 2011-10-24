package uk.ac.ox.oucs.vidaas.concurrency;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import uk.ac.ox.oucs.vidaas.create.CreateBerkeleyXMLDatabase;


public class CreateBerkeleyXMLDatabaseThread implements Runnable {
	
	private CreateBerkeleyXMLDatabase createBerkeleyXMLDatabase = null;
	
	private String containerPath;
	private String databaseName;
	private String path;
	private String file;
	private BufferedWriter out;
	
	public CreateBerkeleyXMLDatabaseThread(String containerPathValue,
			String databaseNameValue, String pathValue, String fileValue, BufferedWriter outValue){
		this.containerPath = containerPathValue;
		this.databaseName = databaseNameValue;
		this.path = pathValue;
		this.file = fileValue;
		this.out = outValue;
	}
	
	public void run(){
		try {
			out.write("Thread started: " + new java.util.Date() + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(createBerkeleyXMLDatabase == null){
			createBerkeleyXMLDatabase = new CreateBerkeleyXMLDatabase();
		}
		
		createBerkeleyXMLDatabase.loadXMLFileInContainer(containerPath, databaseName + ".dbxml", path, file, out);
		
		try {
			out.write("Thread Finished: " + new java.util.Date() + "\n");
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
