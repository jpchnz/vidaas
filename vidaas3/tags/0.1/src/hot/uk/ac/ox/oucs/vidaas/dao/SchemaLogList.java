package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("schemaLogList")
public class SchemaLogList extends EntityQuery<SchemaLog> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8721372644603460405L;

	private static final String EJBQL = "select schemaLog from SchemaLog schemaLog";

	private static final String[] RESTRICTIONS = { "lower(schemaLog.changeLog) like lower(concat(#{schemaLogList.schemaLog.changeLog},'%'))", };

	private SchemaLog schemaLog;

	public SchemaLogList() {
		schemaLog = new SchemaLog();
		schemaLog.setId(new SchemaLogId());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public SchemaLog getSchemaLog() {
		return schemaLog;
	}
}
