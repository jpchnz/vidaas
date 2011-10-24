package uk.ac.ox.oucs.vidaas.concurrency;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ox.oucs.vidaas.accessDB.TablesOrderingUtility;
import uk.ac.ox.oucs.vidaas.create.CreateTables;
import uk.ac.ox.oucs.vidaas.data.holder.DataHolder;

public class DataLoaderThread implements Runnable{
	
	private String sqlDataDirecotry;
	private String databaseMDBFile;
	private Connection connection;
	private DataHolder dataHolder = null;
	
	
	public DataLoaderThread(String databaseMDBFileValue, String sqlDataDirecotryValue, 
			Connection connectionValue, DataHolder dataHolderValue){
		this.databaseMDBFile = databaseMDBFileValue;
		this.sqlDataDirecotry = sqlDataDirecotryValue;
		this.connection = connectionValue;
		this.dataHolder = dataHolderValue;
		
	}
	
	public void run(){
		//System.out.println("DataLoaderThread started");
		dataHolder.setCurrentStatus("\nStart Parsing Data Files" + dataHolder.getCurrentStatus());	
		
		List<String> orderedTables = TablesOrderingUtility.orderTables(databaseMDBFile);
		
		CreateTables createTables = new CreateTables(sqlDataDirecotry, orderedTables, connection, dataHolder);
		createTables.populateTables();
		
		try {
			if(!connection.isClosed()){
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("*****************                            DataLoaderThread finished");
		dataHolder.setCurrentStatus("\nFinished Parsing Data Files" + dataHolder.getCurrentStatus());
		dataHolder.setOkButton(false);
	}

}
