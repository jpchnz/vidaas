package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("projectDatabaseList")
public class ProjectDatabaseList extends EntityQuery<ProjectDatabase> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8652656949641907362L;

	private static final String EJBQL = "select projectDatabase from ProjectDatabase projectDatabase";

	private static final String[] RESTRICTIONS = {
			"lower(projectDatabase.connectionString) like lower(concat(#{projectDatabaseList.projectDatabase.connectionString},'%'))",
			"lower(projectDatabase.databaseName) like lower(concat(#{projectDatabaseList.projectDatabase.databaseName},'%'))", };

	private ProjectDatabase projectDatabase = new ProjectDatabase();

	public ProjectDatabaseList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ProjectDatabase getProjectDatabase() {
		return projectDatabase;
	}
}
