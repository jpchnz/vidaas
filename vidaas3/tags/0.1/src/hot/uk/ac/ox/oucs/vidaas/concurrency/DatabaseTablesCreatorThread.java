package uk.ac.ox.oucs.vidaas.concurrency;

import java.sql.Connection;
import java.sql.SQLException;

//import org.domain.vidaas.session.CreateController;

import uk.ac.ox.oucs.vidaas.create.CreateTables;
import uk.ac.ox.oucs.vidaas.data.holder.DataHolder;

public class DatabaseTablesCreatorThread implements Runnable {
	
	private String databaseSchemaFile;
	private Connection connection;
	
	private DataHolder dataHolder = null;
	
	
	
	public DatabaseTablesCreatorThread(String databaseSchemaFileValue, 
			Connection connectionValue, DataHolder dataHolderValue){
		this.databaseSchemaFile = databaseSchemaFileValue;
		this.connection = connectionValue;		
		this.dataHolder = dataHolderValue;
		
	}
	
	public void run(){
		System.out.println("DatabaseCreatorThread started");
		dataHolder.setCurrentStatus("\nStart Parsing Schema File" + dataHolder.getCurrentStatus());	
		CreateTables createTables = new CreateTables(databaseSchemaFile, connection, dataHolder);
		createTables.createTables();
		
		try {
			if(!connection.isClosed()){
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("**************                      DatabaseCreatorThread finished");
		dataHolder.setCurrentStatus("\nFinished Parsing Schema File" + dataHolder.getCurrentStatus());
		dataHolder.setOkButton(false);
		dataHolder.setTableCreator(true);
		
		
	}

}
