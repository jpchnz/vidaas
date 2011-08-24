package uk.ac.ox.oucs.vidaas.concurrency;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ox.oucs.vidaas.accessDB.CVSGenerator;
import uk.ac.ox.oucs.vidaas.accessDB.SchemaGenerator;
import uk.ac.ox.oucs.vidaas.accessDB.TablesOrderingUtility;
import uk.ac.ox.oucs.vidaas.create.CreateTables;
import uk.ac.ox.oucs.vidaas.data.holder.DataHolder;

public class ParseCreateLoaderThread implements Runnable {

	private String uploadedMDBFile;
	private String generatedSchemaFile;
	private String sqlDataDirecotry;
	private String csvDataDirectory;

	private DataHolder dataHolder = null;

	private Connection connection;

	public ParseCreateLoaderThread(String uploadedMDBFileValue,
			String generatedSchemaFileValue, String sqlDataDirecotryValue,
			String csvDataDirectoryValue, DataHolder dataHolderValue,
			Connection connectionValue) {
		this.uploadedMDBFile = uploadedMDBFileValue;
		this.generatedSchemaFile = generatedSchemaFileValue;
		this.sqlDataDirecotry = sqlDataDirecotryValue;
		this.csvDataDirectory = csvDataDirectoryValue;
		this.dataHolder = dataHolderValue;
		this.connection = connectionValue;
	}

	public void run() {
		dataHolder.setCurrentStatus("\nInitializing ...!"
				+ dataHolder.getCurrentStatus());

		try {
			dataHolder.setCurrentStatus("\nGenerating Data Definition Language"
					+ dataHolder.getCurrentStatus());
			// this.databaseSchemaFormStatus = "Generating Database Schema";
			SchemaGenerator schemaGenerator = new SchemaGenerator(
					uploadedMDBFile, generatedSchemaFile, dataHolder);
			schemaGenerator.getSchemaFromAcessMDB();

			dataHolder
					.setCurrentStatus("\nGenerating CSV Data and Insert SQL for different tables"
							+ dataHolder.getCurrentStatus());
			// this.databaseSchemaFormStatus =
			// "Generating CSV for different tables";
			// changed to databaseDataDirectory from databaseSchemaDirectory
			CVSGenerator cvsGenerator = new CVSGenerator(uploadedMDBFile,
					sqlDataDirecotry, csvDataDirectory, dataHolder);
			cvsGenerator.getCVSFromAcessMDB();

			dataHolder.setCurrentStatus("\nFinished Parsing"
					+ dataHolder.getCurrentStatus());
			dataHolder.setMdbParser(true);
			// System.out.println("MDBParserThread finished");

			dataHolder.setCurrentStatus("\nStart Parsing Schema File"
					+ dataHolder.getCurrentStatus());
			CreateTables createTables = new CreateTables(generatedSchemaFile,
					connection, dataHolder);
			createTables.createTables();

			System.out
					.println("**************                      TableCreatorThread finished");

			dataHolder.setCurrentStatus("\nFinished Parsing Schema File"
					+ dataHolder.getCurrentStatus());
			// dataHolder.setOkButton(false);
			dataHolder.setTableCreator(true);

			dataHolder.setCurrentStatus("\nStart Parsing Data Files"
					+ dataHolder.getCurrentStatus());

			List<String> orderedTables = TablesOrderingUtility
					.orderTables(uploadedMDBFile);

			CreateTables createTablesL = new CreateTables(sqlDataDirecotry,
					orderedTables, connection, dataHolder);
			createTablesL.populateTables();

			System.out
					.println("*****************                            DataLoaderThread finished");
			dataHolder.setCurrentStatus("\nFinished Parsing Data Files"
					+ dataHolder.getCurrentStatus());
			dataHolder.setOkButton(false);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (!connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		try {
			if (!connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
