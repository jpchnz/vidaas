package uk.ac.ox.oucs.vidaas.create;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import uk.ac.ox.oucs.vidaas.data.holder.DataHolder;

public class CreateTables {

	private String ddlFileWithURL = null;
	private String dataSQLDirectory = null;

	public List<String> orderedTableNames = null;

	private Connection connection = null;
	
	private DataHolder dataHolder = null;

	public CreateTables(String ddlFileWithURLValue,
			String dataSQLDirectoryValue, List<String> orderedTableNamesValues,
			Connection connectionValue, DataHolder dataHolderValue) {
		this.ddlFileWithURL = ddlFileWithURLValue;
		this.dataSQLDirectory = dataSQLDirectoryValue;
		this.orderedTableNames = orderedTableNamesValues;
		this.connection = connectionValue;
		this.dataHolder = dataHolderValue;
	}
	
	public CreateTables( String dataSQLDirectoryValue, List<String> orderedTableNamesValues,
			Connection connectionValue, DataHolder dataHolderValue) {
		this.dataSQLDirectory = dataSQLDirectoryValue;
		this.orderedTableNames = orderedTableNamesValues;
		this.connection = connectionValue;
		this.dataHolder = dataHolderValue;
	}

	public CreateTables(String ddlFileWithURLValue,
			String dataSQLDirectoryValue, Connection connectionValue, DataHolder dataHolderValue) {
		this.ddlFileWithURL = ddlFileWithURLValue;
		this.dataSQLDirectory = dataSQLDirectoryValue;
		this.connection = connectionValue;
		this.dataHolder = dataHolderValue;
	}

	public CreateTables(String ddlFileWithURLValue, Connection connectionValue, DataHolder dataHolderValue) {
		this.ddlFileWithURL = ddlFileWithURLValue;
		this.connection = connectionValue;
		this.dataHolder = dataHolderValue;
	}

	public boolean createTables() {
		if (ddlFileWithURL != null && connection != null) {
			InputStream inputStream = null;
			Statement statement = null;
			try {
				statement = connection.createStatement();
				inputStream = new FileInputStream(ddlFileWithURL);
			} catch (SQLException e1) {
				e1.printStackTrace();
				return false;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			InputStreamReader reader = new InputStreamReader(inputStream);
			BufferedReader buffered = new BufferedReader(reader);
			String mainStatement = "";
			String line;
			try {
				while ((line = buffered.readLine()) != null) {
					mainStatement = mainStatement + line + "\n";
					// System.out.println("Line: \n" + line);
					if (line.endsWith(";") || line.contains(";")) {
						try {
							statement.executeUpdate(mainStatement);
						} catch (SQLException e) {
							System.out.println(mainStatement);
							// e.printStackTrace();
						}
						// System.out.println("Main Statement \n" +
						// mainStatement);
						dataHolder.setCurrentStatus("\n" + mainStatement + "\n\n" + dataHolder.getCurrentStatus());	
						mainStatement = "";
					}
				}
				buffered.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	public boolean populateTables(){
		try {
			if (dataSQLDirectory != null && !connection.isClosed() && orderedTableNames != null) {
				InputStream inputStream = null;
				Statement statement = null;
				for (int i = 0; i < orderedTableNames.size(); i++) {
					try {
						statement = connection.createStatement();
						inputStream = new FileInputStream(dataSQLDirectory + orderedTableNames.get(i) + ".sql");
						dataHolder.setCurrentStatus("\nLoading Data from " + orderedTableNames.get(i) + ".sql" + 
								dataHolder.getCurrentStatus());	
					} catch (SQLException e1) {
						e1.printStackTrace();
						return false;
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						return false;
					}
					
					InputStreamReader reader = new InputStreamReader(inputStream);
					BufferedReader buffered = new BufferedReader(reader);
					String mainStatement = "";
					String line;
					try {
						while ((line = buffered.readLine()) != null) {
							mainStatement = mainStatement + line + "\n";
							System.out.println("Line: \n" + line);
							if (line.endsWith(";") || line.contains(";")) {
								try {
									statement.executeUpdate(mainStatement);
								} catch (SQLException e) {
									System.out.println("Error in Statement: " + mainStatement);
									e.printStackTrace();
								}
								// System.out.println("Main Statement \n" +
								// mainStatement);
								mainStatement = "";
							}
						}
						buffered.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			    }
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	

}
