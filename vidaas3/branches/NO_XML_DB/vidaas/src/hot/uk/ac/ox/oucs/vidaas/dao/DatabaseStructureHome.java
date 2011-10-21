package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("databaseStructureHome")
public class DatabaseStructureHome extends EntityHome<DatabaseStructure> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6837848770516057593L;

	public void setDatabaseStructureStructureId(Integer id) {
		setId(id);
	}

	public Integer getDatabaseStructureStructureId() {
		return (Integer) getId();
	}

	@Override
	protected DatabaseStructure createInstance() {
		DatabaseStructure databaseStructure = new DatabaseStructure();
		return databaseStructure;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public DatabaseStructure getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<ProjectDatabase> getProjectDatabases() {
		return getInstance() == null ? null : new ArrayList<ProjectDatabase>(
				getInstance().getProjectDatabases());
	}

	public List<SchemaLog> getSchemaLogs() {
		return getInstance() == null ? null : new ArrayList<SchemaLog>(
				getInstance().getSchemaLogs());
	}

}
