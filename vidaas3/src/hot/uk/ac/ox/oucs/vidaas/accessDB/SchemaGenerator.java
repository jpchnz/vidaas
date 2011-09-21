package uk.ac.ox.oucs.vidaas.accessDB;

import com.healthmarketscience.jackcess.*;
import com.healthmarketscience.jackcess.IndexData.ColumnDescriptor;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.ox.oucs.vidaas.data.holder.DataHolder;

public class SchemaGenerator {

	private String databaseFielURL = null;
	private String schemaFileURL = null;

	private DataHolder dataHolder = null;

	private String tableName = "";
	private String[] keyWordsArray = new String[] { "ACTION", "ADMIN", "ARRAY",
			"ASSIGNMENT", "ATOMIC", "ATTRIBUTE", "ATTRIBUTES", "REFERENCES",
			"PRIMARY", "VERBOSE", "USER", "TABLE", "SESSION_USER", "ORDER",
			"LIMIT", "GROUP", "FOREIGN", "CURRENT_USER", "CONSTRAINT", "CAST",
			"EVENTS", "ORDER" };

	private List<String> keyWordsList = Arrays.asList(keyWordsArray);

	private List<String> tableNamesList = new ArrayList<String>();
	private Map<String, Relationship> relationshipArray = new HashMap<String, Relationship>();
	private StringBuffer stringBuffer = new StringBuffer();

	public SchemaGenerator(String databaseFileURLValue,
			String schemaFileURLValue, DataHolder dataHolderValue) {
		databaseFielURL = databaseFileURLValue;
		schemaFileURL = schemaFileURLValue;
		this.dataHolder = dataHolderValue;
	}

	public boolean getSchemaFromAcessMDB() {
		dataHolder
				.setCurrentStatus("\nAccessing Microsoft Access Database .mdb file"
						+ dataHolder.getCurrentStatus());
		if (databaseFielURL != null) {
			try {
				File databaseFile = new File(databaseFielURL);
				Database database = Database.open(databaseFile, true);

				Set<String> tablesName = database.getTableNames();
				Iterator<String> iterTablesName = tablesName.iterator();

				while (iterTablesName.hasNext()) {
					tableName = iterTablesName.next();
					dataHolder.setCurrentStatus("\nParsing Schema for "
							+ tableName + dataHolder.getCurrentStatus());
					// System.out.println();

					try {
						Table table = database.getTable(tableName);
						if (!table.getName().trim().startsWith("~TMP")) {
							createTableStructure(table);
						}
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}

				getForeignKeys(database);

				if (schemaFileURL != null) {
					OutputStream outputStream = new FileOutputStream(
							schemaFileURL);
					Writer writer = new OutputStreamWriter(outputStream);
					writer.write(stringBuffer.toString());
					writer.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		dataHolder.setCurrentStatus("\nSuccessfully Created Database Schema"
				+ dataHolder.getCurrentStatus());
		return true;
	}

	private boolean createTableStructure(Table table) {
		// StringBuffer stringBuffer = new StringBuffer();

		if (!table.getName().contains("_Conflict")) {
			tableNamesList.add(table.getName());
			stringBuffer.append("CREATE TABLE \""
					+ referenceKeyWordValidation(table.getName(), true)
					+ "\" ( \n");
			for (Column column : table.getColumns()) {
				if (!(column.getName().startsWith("s_") || column.getName()
						.startsWith("Gen_"))) {
					try {
						//DataType.
						if ((column.getType() == DataType.SHORT_DATE_TIME)) {
							stringBuffer.append("\t \""
									+ referenceKeyWordValidation(
											column.getName(), false)
									+ "\"  timestamp without time zone, \n");
							continue;
						}
						if ((column.getType() == DataType.LONG)) {
							stringBuffer.append("\t\""
									+ referenceKeyWordValidation(
											column.getName(), false)
									+ "\"  integer, \n");
							continue;
						}
						if ((column.getType() == DataType.TEXT)) {
							stringBuffer.append("\t\""
									+ referenceKeyWordValidation(
											column.getName(), false)
									+ "\"  character varying(255), \n");
							continue;
						}

						if ((column.getType() == DataType.DOUBLE)) {
							stringBuffer.append("\t\""
									+ referenceKeyWordValidation(
											column.getName(), false)
									+ "\"  decimal, \n");
							continue;
						}
						if ((column.getType() == DataType.MEMO)) {
							stringBuffer.append("\t\""
									+ referenceKeyWordValidation(
											column.getName(), false)
									+ "\"  text, \n");
							continue;
						}
						if (!(column.getType() == DataType.GUID)) {
							stringBuffer.append("\t\""
									+ referenceKeyWordValidation(
											column.getName(), false) + "\"  "
									+ column.getType() + ", \n");
							continue;
						}

					} catch (Exception ex) {
						Logger.getLogger(SchemaGenerator.class.getName()).log(
								Level.SEVERE, null, ex);
						return false;
					}

				}
			}
			List<String> primaryKeyList = getPrimaryKeys(table);
			if (primaryKeyList.isEmpty()) {
				// the boundary of search from index 0 till second parameter
				// (int/index) to the method
				stringBuffer = stringBuffer.deleteCharAt(stringBuffer
						.lastIndexOf(",", stringBuffer.length()));
			} else {
				stringBuffer.append("\tprimary key (");
				for (int i = 0; i < primaryKeyList.size(); i++) {
					String tempColumnName = primaryKeyList.get(i);
					dataHolder.setCurrentStatus("\nCreating Primary Key  "
							+ tempColumnName + dataHolder.getCurrentStatus());
					if (i == (primaryKeyList.size() - 1)) {
						stringBuffer.append("\""
								+ tempColumnName + "\")\n");

					} else {
						stringBuffer.append("\""
								+ tempColumnName + "\",");

					}
				}
			}
			stringBuffer.append("); \n");
		} else {
			dataHolder.setCurrentStatus("\nIgnoring Table  " + table.getName()
					+ dataHolder.getCurrentStatus());
		}
		return true;
	}

	public List<String> getPrimaryKeys(Table table) {
		List<String> primaryKeyList = new ArrayList<String>();
		for (Index index : table.getIndexes()) {
			for (ColumnDescriptor columnDesc : index.getColumns()) {
				if (index.isPrimaryKey()) {
					Column tempColumn = columnDesc.getColumn();
					// System.out.print(tempColumn.getName() + ": " +
					// tempColumn.getType() + "  ");
					// System.out.print(index.isPrimaryKey() + "  " +
					// index.isForeignKey() + " ");
					primaryKeyList.add(referenceKeyWordValidation(
							tempColumn.getName(), false));
				}
			}
		}
		return primaryKeyList;
	}

	private boolean getForeignKeys(Database database) {
		dataHolder.setCurrentStatus("\nAdding Foreign Key Constraints"
				+ dataHolder.getCurrentStatus());
		// System.out.println(tableNamesList.size());
		for (int i = 0; i < tableNamesList.size(); i++) {
			for (int j = 0; j < tableNamesList.size(); j++) {
				// System.out.println("In While Condition");
				try {
					Table tempTable1 = database.getTable(tableNamesList.get(i));
					Table tempTable2 = database.getTable(tableNamesList.get(j));
					// System.out.println(tempTable1.getName() + "  " +
					// tempTable2.getName());
					if (!(tempTable1.getName().equalsIgnoreCase(tempTable2
							.getName()))) {

						List<Relationship> relationship = database
								.getRelationships(tempTable1, tempTable2);
						for (int p = 0; p < relationship.size(); p++) {
							Relationship relationshipTemp = relationship.get(p);
							relationshipArray.put(relationshipTemp.getName(),
									relationshipTemp);
							// System.out.println("**************   Relationship Name: "
							// + relationshipTemp.getName());
						}
					}
				} catch (Exception exp) {
					exp.printStackTrace();
					return false;
				}
			}
		}
		// System.out.println("Relationship Array Size: " +
		// relationshipArray.size());
		createForeignKeyRelation();
		return true;
	}

	private void createForeignKeyRelation() {
		Iterator<String> keySetIterator = relationshipArray.keySet().iterator();
		while (keySetIterator.hasNext()) {
			Relationship relationshipTemp = relationshipArray
					.get(keySetIterator.next());
			String fromColumn = referenceKeyWordValidation(relationshipTemp
					.getFromColumns().get(0).getName(), false);
			String toColumn = referenceKeyWordValidation(relationshipTemp
					.getToColumns().get(0).getName(), false);
			String fromTable = referenceKeyWordValidation(relationshipTemp
					.getFromTable().getName(), true);
			String toTable = referenceKeyWordValidation(relationshipTemp
					.getToTable().getName(), true);

			// System.out.println(fromColumn + " " + toColumn + " " + fromTable
			// + " " + toTable);
			/**/
			stringBuffer.append("Alter table \"" + toTable
					+ "\" add  foreign key (\"" + toColumn + "\") "
					+ "references \"" + fromTable + "\" (\"" + fromColumn
					+ "\") ON UPDATE NO ACTION ON DELETE NO ACTION; \n");
		}
		// System.out.println(stringBuffer);

	}

	private String referenceKeyWordValidation(String name, boolean table) {
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
