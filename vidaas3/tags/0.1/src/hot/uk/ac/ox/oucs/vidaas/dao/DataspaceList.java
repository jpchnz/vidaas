package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("dataspaceList")
public class DataspaceList extends EntityQuery<Dataspace> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2963284541076079269L;

	private static final String EJBQL = "select dataspace from Dataspace dataspace";

	private static final String[] RESTRICTIONS = {
			"lower(dataspace.databaseBackupPolicy) like lower(concat(#{dataspaceList.dataspace.databaseBackupPolicy},'%'))",
			"lower(dataspace.databaseDescription) like lower(concat(#{dataspaceList.dataspace.databaseDescription},'%'))",
			"lower(dataspace.databaseExpandablePolicy) like lower(concat(#{dataspaceList.dataspace.databaseExpandablePolicy},'%'))",
			"lower(dataspace.databaseType) like lower(concat(#{dataspaceList.dataspace.databaseType},'%'))",
			"lower(dataspace.dataspaceName) like lower(concat(#{dataspaceList.dataspace.dataspaceName},'%'))",
			"lower(dataspace.webApplicationName) like lower(concat(#{dataspaceList.dataspace.webApplicationName},'%'))", };

	private Dataspace dataspace = new Dataspace();

	public DataspaceList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Dataspace getDataspace() {
		return dataspace;
	}
}
