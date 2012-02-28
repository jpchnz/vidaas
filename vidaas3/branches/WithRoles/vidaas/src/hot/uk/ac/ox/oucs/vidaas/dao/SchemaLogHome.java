package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("schemaLogHome")
public class SchemaLogHome extends EntityHome<SchemaLog> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2480935733733013854L;
	@In(create = true)
	UsersHome usersHome;
	@In(create = true)
	DatabaseStructureHome databaseStructureHome;

	public void setSchemaLogId(SchemaLogId id) {
		setId(id);
	}

	public SchemaLogId getSchemaLogId() {
		return (SchemaLogId) getId();
	}

	public SchemaLogHome() {
		setSchemaLogId(new SchemaLogId());
	}

	@Override
	public boolean isIdDefined() {
		if (getSchemaLogId().getStructureId() == 0)
			return false;
		if (getSchemaLogId().getUserId() == 0)
			return false;
		return true;
	}

	@Override
	protected SchemaLog createInstance() {
		SchemaLog schemaLog = new SchemaLog();
		schemaLog.setId(new SchemaLogId());
		return schemaLog;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Users users = usersHome.getDefinedInstance();
		if (users != null) {
			getInstance().setUsers(users);
		}
		DatabaseStructure databaseStructure = databaseStructureHome
				.getDefinedInstance();
		if (databaseStructure != null) {
			getInstance().setDatabaseStructure(databaseStructure);
		}
	}

	public boolean isWired() {
		if (getInstance().getUsers() == null)
			return false;
		if (getInstance().getDatabaseStructure() == null)
			return false;
		return true;
	}

	public SchemaLog getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
