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
	
	private boolean parsingStatus = false;

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

	public boolean isParsingStatus() {
		return parsingStatus;
	}

	public void setParsingStatus(boolean parsingStatus) {
		this.parsingStatus = parsingStatus;
	}

	public void run() {
		this.parsingStatus = false;
		dataHolder.setCurrentStatus("\nInitializing ...!"
				+ dataHolder.getCurrentStatus());

		try {
			dataHolder.setCurrentStatus("\nGenerating Data Definition Language"
					+ dataHolder.getCurrentStatus());
			// this.databaseSchemaFormStatus = "Generating Database Schema";
			SchemaGenerator schemaGenerator = new SchemaGenerator(
					uploadedMDBFile, generatedSchemaFile, dataHolder);
			if (schemaGenerator.getSchemaFromAcessMDB()) {

				dataHolder
						.setCurrentStatus("\nGenerating CSV Data and Insert SQL for different tables"
								+ dataHolder.getCurrentStatus());
				// this.databaseSchemaFormStatus =
				// "Generating CSV for different tables";
				// changed to databaseDataDirectory from databaseSchemaDirectory
				CVSGenerator cvsGenerator = new CVSGenerator(uploadedMDBFile,
						sqlDataDirecotry, csvDataDirectory, dataHolder);
				if (cvsGenerator.getCVSFromAcessMDB()) {

					dataHolder
							.setCurrentStatus("\nFinished Creating CSV Data and Insert SQL"
									+ dataHolder.getCurrentStatus());
					dataHolder.setMdbParser(true);
					// System.out.println("MDBParserThread finished");

					dataHolder.setCurrentStatus("\n Creating Tables"
							+ dataHolder.getCurrentStatus());
					CreateTables createTables = new CreateTables(
							generatedSchemaFile, connection, dataHolder);
					if (createTables.createTables()) {

						System.out
								.println("**************                      TableCreatorThread finished");

						dataHolder
								.setCurrentStatus("\nFinished Creating Tables"
										+ dataHolder.getCurrentStatus());
						// dataHolder.setOkButton(false);
						dataHolder.setTableCreator(true);

						dataHolder
								.setCurrentStatus("\n Start Loading Data for Tables"
										+ dataHolder.getCurrentStatus());

						List<String> orderedTables = TablesOrderingUtility
								.orderTables(uploadedMDBFile);
						if (orderedTables != null) {
							CreateTables createTablesL = new CreateTables(
									sqlDataDirecotry, orderedTables,
									connection, dataHolder);
							if (createTablesL.populateTables()) {

								System.out
										.println("*****************                            DataLoaderThread finished");
								/*		*/
								dataHolder
										.setCurrentStatus("\n Finished loading Data for Tables"
												+ dataHolder.getCurrentStatus());
								dataHolder.setOkButton(false);
								this.parsingStatus = true;
							} else {
								dataHolder
										.setCurrentStatus("\n Error in populating tables"
												+ dataHolder.getCurrentStatus());
								dataHolder.setOkButton(false);
							}
						} else {
							dataHolder
									.setCurrentStatus("\n Error in Creating tables"
											+ dataHolder.getCurrentStatus());
							dataHolder.setOkButton(false);
						}
					} else {
						dataHolder
								.setCurrentStatus("\n Error in Creating tables"
										+ dataHolder.getCurrentStatus());
						dataHolder.setOkButton(false);

					}
				} else {
					dataHolder.setCurrentStatus("\n Error in generating Schema"
							+ dataHolder.getCurrentStatus());
					dataHolder.setOkButton(false);
				}
			} else {
				dataHolder
						.setCurrentStatus("\n Error in Parsing uploaded Acess file"
								+ dataHolder.getCurrentStatus());
				dataHolder.setOkButton(false);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (!connection.isClosed()) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
