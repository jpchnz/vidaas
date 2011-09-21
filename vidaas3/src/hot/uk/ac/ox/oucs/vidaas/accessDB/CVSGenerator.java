package uk.ac.ox.oucs.vidaas.accessDB;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.DataType;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.Table;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.ox.oucs.vidaas.data.holder.DataHolder;

public class CVSGenerator {

	private String sourceMDBFilePathWithName;
	private String databaseSchemaDirectory;

	private DataHolder dataHolder = null;

	private String[] keyWordsArray = new String[] { "ACTION", "ADMIN", "ARRAY",
			"ASSIGNMENT", "ATOMIC", "ATTRIBUTE", "ATTRIBUTES", "REFERENCES",
			"PRIMARY", "VERBOSE", "USER", "TABLE", "SESSION_USER", "ORDER",
			"LIMIT", "GROUP", "FOREIGN", "CURRENT_USER", "CONSTRAINT", "CAST",
			"EVENTS" };
	private List<String> keyWordsList = Arrays.asList(keyWordsArray);

	private String csvDirectoryURL = null;

	public CVSGenerator(String sourceMDBFilePathWithNameValue,
			String databaseSchemaDirectoryValue, String csvDirectoryURLValue,
			DataHolder dataHolderValue) {
		this.sourceMDBFilePathWithName = sourceMDBFilePathWithNameValue;
		this.databaseSchemaDirectory = databaseSchemaDirectoryValue;
		this.csvDirectoryURL = csvDirectoryURLValue;
		this.dataHolder = dataHolderValue;
	}

	public boolean getCVSFromAcessMDB() {
		dataHolder.setCurrentStatus("\nCreating Data Files as CSV and SQL "
				+ dataHolder.getCurrentStatus());
		return dumpDatabaseAsCVS();

	}

	private boolean dumpDatabaseAsCVS() {
		Database database = null;

		BufferedWriter out = null;
		BufferedWriter outSQL = null;
		try {
			File databaseFile = new File(sourceMDBFilePathWithName);
			database = Database.open(databaseFile, true);

			Iterator<String> tableNamesIterator = database.getTableNames()
					.iterator();
			while (tableNamesIterator.hasNext()) {
				try {
				Table table = database.getTable(tableNamesIterator.next()
						.toString());

				if (!table.getName().contains("_Conflict")) {
					dataHolder
							.setCurrentStatus("\nCreating Data File for 'Table' "
									+ table.getName()
									+ dataHolder.getCurrentStatus());
					String tableModifiedName = referenceKeyWordValidation(
							table.getName(), true);
					FileWriter fstream = new FileWriter(csvDirectoryURL
							+ tableModifiedName + ".csv");
					FileWriter fstreamSQL = new FileWriter(
							databaseSchemaDirectory + tableModifiedName
									+ ".sql");

					out = new BufferedWriter(fstream);
					outSQL = new BufferedWriter(fstreamSQL);

					dumpTable(table, out, outSQL);

					out.close();
					outSQL.close();
				} else {
					dataHolder.setCurrentStatus("\nIgnoring 'Table' "
							+ table.getName() + dataHolder.getCurrentStatus());
				}
				} catch(Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		} catch (IOException ex) {
			return false;
		} finally {
			try {
				database.close();
				// Close the output stream
				out.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		}
		dataHolder
				.setCurrentStatus("\nFinished creating Data Files as CSV and SQL "
						+ dataHolder.getCurrentStatus());
		return true;
	}

	private boolean dumpTable(Table table, BufferedWriter out,
			BufferedWriter outSQL) {
		// System.out.println("Dump Table Called");
		String lineBreak = System.getProperty("line.separator");
		boolean inside = false;
		try {
			for (Index index : table.getIndexes()) {
				index.initialize();
			}
			for (Column col : table.getColumns()) {
				if (!(col.getName().startsWith("s_") || col.getName()
						.startsWith("Gen_"))) {
					if (inside) {
						out.write("#");
					} else {
						inside = true;
					}
					out.write(col.getName());
				}
			}
			out.write(lineBreak);
			String tableModifiedName = referenceKeyWordValidation(
					table.getName(), true);
			for (Map<String, Object> row : Cursor.createCursor(table)) {
				inside = false;
				outSQL.write("INSERT INTO \"" + tableModifiedName + "\" VALUES(");
				for (Map.Entry<String, Object> entry : row.entrySet()) {
					Object v = entry.getValue();
					if (!(entry.getKey().startsWith("s_") || entry.getKey()
							.startsWith("Gen_"))) {
						if (inside) {
							out.write("#");
							outSQL.write(",");
						} else {
							inside = true;
						}
						if (v != null) {
							if (table.getColumn(entry.getKey()).getType() == DataType.TEXT
									|| table.getColumn(entry.getKey())
											.getType() == DataType.SHORT_DATE_TIME
									|| table.getColumn(entry.getKey())
											.getType() == DataType.MEMO) {

								// ' ` Not sure If I need replacement
								// v.toString().replace("`", "'");

								String tempV = v.toString();
								String newTempString = "";
								boolean replacementFinished = false;

								if (v.toString().contains("'")) {
									while (!replacementFinished) {
										String tempString = tempV.toString();
										int index = tempString.indexOf("'", 0);

										newTempString = newTempString
												+ tempString
														.substring(0, index);
										newTempString = newTempString + "\\'";

										if (tempString.substring(index + 1)
												.contains("'")) {
											tempV = tempString
													.substring(index + 1);
											// System.out.println(tempV);
										} else {
											newTempString = newTempString
													+ tempString
															.substring(index + 1);
											replacementFinished = true;
										}
									}
									System.out.println(v.toString() + "  "
											+ newTempString);
									outSQL.write("E'" + newTempString + "'");
								} else {
									outSQL.write("'" + v.toString() + "'");
								}
							} else {
								outSQL.write(v.toString());
							}
							out.write(v.toString());
						} else {
							if (table.getColumn(entry.getKey()).getType() == DataType.TEXT) {
								out.write("Null");
								outSQL.write("Null");
							} else {
								out.write("");
								// out.write("\\N");

								outSQL.write("NULL");
							}
						}
					}
				}
				outSQL.write(");" + lineBreak);
				out.write(lineBreak);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public String referenceKeyWordValidation(String name, boolean table) {
		if (keyWordsList.contains(name.toUpperCase())) {
			if (table) {
				name = name + "_Tab";
			} else {
				name = name + "_Col";
			}

		}
		return name;
	}

}
