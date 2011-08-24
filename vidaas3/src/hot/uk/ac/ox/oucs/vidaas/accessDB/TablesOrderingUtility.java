package uk.ac.ox.oucs.vidaas.accessDB;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Relationship;
import com.healthmarketscience.jackcess.Table;

public class TablesOrderingUtility {

    private static String[] keyWordsArray = new String[]{"ACTION", "ADMIN", "ARRAY", "ASSIGNMENT", "ATOMIC", "ATTRIBUTE",
        "ATTRIBUTES", "REFERENCES", "PRIMARY", "VERBOSE", "USER", "TABLE", "SESSION_USER", "ORDER", "LIMIT", "GROUP",
        "FOREIGN", "CURRENT_USER", "CONSTRAINT", "CAST", "EVENTS"};
    private static List<String> keyWordsList = Arrays.asList(keyWordsArray);

    public static List<String> orderTables(String mdbFileURL) {
        Map<String, Relationship> relationshipArray = new HashMap<String, Relationship>();

        Map<String, Set<String>> foreignKeyContainerTable = new HashMap<String, Set<String>>();
        List<String> tablesNotReferenced = new ArrayList<String>();

        File databaseFile = new File(mdbFileURL);
        Database database = null;
        try {
            database = Database.open(databaseFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> tablesName = null;
		try {
			tablesName = database.getTableNames();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        List<String> tableNamesList = new ArrayList<String>();

        Iterator<String> iterTablesName = tablesName.iterator();

        String tableName = "";
        while (iterTablesName.hasNext()) {
            tableName = iterTablesName.next();
            if (!tableName.contains("_Conflict")) {
                tableNamesList.add(tableName);
            }
        }

        for (int i = 0; i < tableNamesList.size(); i++) {
            for (int j = 0; j < tableNamesList.size(); j++) {
                //System.out.println("In While Condition");
                try {
                    Table tempTable1 = database.getTable(tableNamesList.get(i));
                    Table tempTable2 = database.getTable(tableNamesList.get(j));
                    //System.out.println(tempTable1.getName() + "  " + tempTable2.getName());
                    if (!(tempTable1.getName().equalsIgnoreCase(tempTable2.getName()))) {

                        List<Relationship> relationship = database.getRelationships(tempTable1, tempTable2);
                        for (int p = 0; p < relationship.size(); p++) {
                            Relationship relationshipTemp = relationship.get(p);
                            relationshipArray.put(relationshipTemp.getName(), relationshipTemp);
                            //System.out.println("**************   Relationship Name: " + relationshipTemp.getName());
                        }
                    }
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        }

        Iterator<String> keySetIterator = relationshipArray.keySet().iterator();
        while (keySetIterator.hasNext()) {
            Relationship relationshipTemp = relationshipArray.get(keySetIterator.next());
            //String fromColumn = relationshipTemp.getFromColumns().get(0).getName();
            //String toColumn = relationshipTemp.getToColumns().get(0).getName();
            String fromTable = referenceKeyWordValidation(relationshipTemp.getFromTable().getName(), true);
            String toTable = referenceKeyWordValidation(relationshipTemp.getToTable().getName(), true);

            if (foreignKeyContainerTable.containsKey(toTable)) {
                Set<String> referencedTables = foreignKeyContainerTable.get(toTable);
                referencedTables.add(fromTable);
                foreignKeyContainerTable.put(toTable, referencedTables);
            } else {
                Set<String> tempSet = new HashSet<String>();
                tempSet.add(fromTable);
                foreignKeyContainerTable.put(toTable, tempSet);
            }
        }

        for (int i = 0; i < tableNamesList.size(); i++) {
            if (!foreignKeyContainerTable.containsKey(referenceKeyWordValidation(tableNamesList.get(i), true))) {
                tablesNotReferenced.add(tableNamesList.get(i));
                System.out.println(tableNamesList.get(i) + " ");
            }
            //referenceKeyWordValidation(table.getName(),true)
        }

        tableReOrdering(foreignKeyContainerTable, tablesNotReferenced, tableNamesList.size());
        return tablesNotReferenced;
    }

    private static void tableReOrdering(Map<String, Set<String>> foreignKeyContainerTable,
        List<String> tablesNotReferenced, int totalTablesNumber) {
    	/*
        System.out.println(foreignKeyContainerTable.size() + "  " + tablesNotReferenced.size()
                + " " + totalTablesNumber);*/

        Set<String> containerTableSet = foreignKeyContainerTable.keySet();
        Iterator<String> tableSetIterator = containerTableSet.iterator();

        while (tableSetIterator.hasNext()) {

            // Table Referencing ...!
            String tempTableName = tableSetIterator.next();

            // List of tables Referenced
            Set<String> tempSet = foreignKeyContainerTable.get(tempTableName);

            for (int i = 0; i < tablesNotReferenced.size(); i++) {
                tempSet.remove(tablesNotReferenced.get(i));
            }
        }

        Iterator<String> tableSetIterator1 = containerTableSet.iterator();

        while (tableSetIterator1.hasNext()) {

            // Table Referencing ...!
            String tempTableName = tableSetIterator1.next();

            // List of tables Referenced
            Set<String> tempSet = foreignKeyContainerTable.get(tempTableName);

            if (tempSet.isEmpty()) {
                if (!tablesNotReferenced.contains(tempTableName)) {
                    tablesNotReferenced.add(tempTableName);
                }
                //foreignKeyContainerTable.remove(tempTableName);
            }
        }

        if (tablesNotReferenced.size() != totalTablesNumber) {
            tableReOrdering(foreignKeyContainerTable, tablesNotReferenced, totalTablesNumber);
        } /*else {
            for (int i = 0; i < tablesNotReferenced.size(); i++) {
                System.out.print(tablesNotReferenced.get(i) + " ");
            }
        }*/

    }

    private static String referenceKeyWordValidation(String name, boolean table) {
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
