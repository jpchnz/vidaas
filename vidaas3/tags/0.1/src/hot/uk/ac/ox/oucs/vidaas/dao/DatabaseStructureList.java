package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("databaseStructureList")
public class DatabaseStructureList extends EntityQuery<DatabaseStructure> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5427948809622896758L;

	private static final String EJBQL = "select databaseStructure from DatabaseStructure databaseStructure";

	private static final String[] RESTRICTIONS = {
			"lower(databaseStructure.contentType) like lower(concat(#{databaseStructureList.databaseStructure.contentType},'%'))",
			"lower(databaseStructure.csvDirectory) like lower(concat(#{databaseStructureList.databaseStructure.csvDirectory},'%'))",
			"lower(databaseStructure.databaseDirectory) like lower(concat(#{databaseStructureList.databaseStructure.databaseDirectory},'%'))",
			"lower(databaseStructure.file) like lower(concat(#{databaseStructureList.databaseStructure.file},'%'))",
			"lower(databaseStructure.sqlDirectory) like lower(concat(#{databaseStructureList.databaseStructure.sqlDirectory},'%'))",
			"lower(databaseStructure.status) like lower(concat(#{databaseStructureList.databaseStructure.status},'%'))",
			"lower(databaseStructure.schemaType) like lower(concat(#{databaseStructureList.databaseStructure.schemaType},'%'))",
			"lower(databaseStructure.uploadType) like lower(concat(#{databaseStructureList.databaseStructure.uploadType},'%'))", };

	private DatabaseStructure databaseStructure = new DatabaseStructure();

	public DatabaseStructureList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DatabaseStructure getDatabaseStructure() {
		return databaseStructure;
	}
}
