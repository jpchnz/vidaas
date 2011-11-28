package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import uk.ac.ox.oucs.vidaas.entity.ProjectDatabase;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("projectDatabaseHome")
@SuppressWarnings("unchecked")
public class ProjectDatabaseHome extends EntityHome<ProjectDatabase> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4168872321283112638L;
	@In(create = true)
	DataspaceHome dataspaceHome;
	@In(create = true)
	WebApplicationHome webApplicationHome;
	@In(create = true)
	DatabaseStructureHome databaseStructureHome;

	public void setProjectDatabaseDatabaseId(Integer id) {
		setId(id);
	}

	public Integer getProjectDatabaseDatabaseId() {
		return (Integer) getId();
	}

	@Override
	protected ProjectDatabase createInstance() {
		ProjectDatabase projectDatabase = new ProjectDatabase();
		return projectDatabase;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Dataspace dataspace = dataspaceHome.getDefinedInstance();
		if (dataspace != null) {
			getInstance().setDataspace(dataspace);
		}
		WebApplication webApplication = webApplicationHome.getDefinedInstance();
		if (webApplication != null) {
			getInstance().setWebApplication(webApplication);
		}
		DatabaseStructure databaseStructure = databaseStructureHome
				.getDefinedInstance();
		if (databaseStructure != null) {
			getInstance().setDatabaseStructure(databaseStructure);
		}
	}

	public boolean isWired() {
		if (getInstance().getDataspace() == null)
			return false;
		if (getInstance().getWebApplication() == null)
			return false;
		if (getInstance().getDatabaseStructure() == null)
			return false;
		return true;
	}

	public ProjectDatabase getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<UserDatabase> getUserDatabases() {
		return getInstance() == null ? null : new ArrayList<UserDatabase>(
				getInstance().getUserDatabases());
	}
	
	/*
	 * Projects don't have Direct Databases
	public List<ProjectDatabase> findByProjectID(int projectIDValue){
		Query query = this.getEntityManager().createNamedQuery("ProjectDatabase.findByProjectID");
		query.setParameter("projectId", new Integer(projectIDValue));		
		List<ProjectDatabase> projectDatabaseList = query.getResultList();
		return projectDatabaseList;
	}
	*/
	
	public List<ProjectDatabase> findByDatabaseID(int databaseIDValue){
		Query query = this.getEntityManager().createNamedQuery("ProjectDatabase.findByDatabaseID");
		query.setParameter("databaseID", new Integer(databaseIDValue));		
		List<ProjectDatabase> projectDatabaseList = query.getResultList();
		return projectDatabaseList;
	}
	
	public List<ProjectDatabase> findByDataspaceID(int dataspaceIDValue){
		Query query = this.getEntityManager().createNamedQuery("ProjectDatabase.findByDataspaceID");
		query.setParameter("dataSpaceID", new Integer(dataspaceIDValue));		
		List<ProjectDatabase> projectDatabaseList = query.getResultList();
		return projectDatabaseList;
	}
	
	//ProjectDatabase.findByDatabaseName
	public List<ProjectDatabase> findByDatabaseName(String databaseNameValue){
		Query query = this.getEntityManager().createNamedQuery("ProjectDatabase.findByDatabaseName");
		query.setParameter("databaseName", new String(databaseNameValue));		
		List<ProjectDatabase> projectDatabaseList = query.getResultList();
		return projectDatabaseList;
	}
}
