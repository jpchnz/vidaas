package uk.ac.ox.oucs.vidaas.concurrency;

//import org.domain.vidaas.session.CreateController;

import uk.ac.ox.oucs.vidaas.accessDB.CVSGenerator;
import uk.ac.ox.oucs.vidaas.accessDB.SchemaGenerator;
import uk.ac.ox.oucs.vidaas.data.holder.DataHolder;

public class MDBParserThread implements Runnable {
	
	private String uploadedMDBFile;
	private String generatedSchemaFile;
	private String sqlDataDirecotry;
	private String csvDataDirectory;
	
	private DataHolder dataHolder = null;
	
	
	
	public MDBParserThread(String uploadedMDBFileValue, String generatedSchemaFileValue,
			String sqlDataDirecotryValue, String csvDataDirectoryValue, DataHolder dataHolderValue){
		this.uploadedMDBFile = uploadedMDBFileValue;
		this.generatedSchemaFile = generatedSchemaFileValue;
		this.sqlDataDirecotry = sqlDataDirecotryValue;
		this.csvDataDirectory = csvDataDirectoryValue;
		this.dataHolder = dataHolderValue;
		
	}
	
	public void run(){
		dataHolder.setCurrentStatus("\nInitializing ...!" + dataHolder.getCurrentStatus());	
		//System.out.println("MDBParserThread started");
		try {
			dataHolder.setCurrentStatus("\nGenerating Data Definition Language" + dataHolder.getCurrentStatus());
			//this.databaseSchemaFormStatus = "Generating Database Schema";
			SchemaGenerator schemaGenerator = new SchemaGenerator(uploadedMDBFile, generatedSchemaFile, dataHolder);
			schemaGenerator.getSchemaFromAcessMDB();
			
			dataHolder.setCurrentStatus("\nGenerating CSV Data and Insert SQL for different tables" + dataHolder.getCurrentStatus());
			//this.databaseSchemaFormStatus = "Generating CSV for different tables";
			// changed to databaseDataDirectory from databaseSchemaDirectory
			CVSGenerator cvsGenerator = new CVSGenerator(uploadedMDBFile, sqlDataDirecotry, csvDataDirectory, dataHolder);
			cvsGenerator.getCVSFromAcessMDB();
			
			dataHolder.setCurrentStatus("\nFinished Parsing" + dataHolder.getCurrentStatus());
			
			dataHolder.setMdbParser(true);
			
			
			dataHolder.setOkButton(false);
			
			System.out.println("MDBParserThread finished");
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

}
