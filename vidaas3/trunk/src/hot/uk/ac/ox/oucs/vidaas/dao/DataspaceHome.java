package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("dataspaceHome")
public class DataspaceHome extends EntityHome<Dataspace> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1714890188644915562L;
	@In(create = true)
	ProjectHome projectHome;

	public void setDataspaceDataSpaceId(Integer id) {
		setId(id);
	}

	public Integer getDataspaceDataSpaceId() {
		return (Integer) getId();
	}

	@Override
	protected Dataspace createInstance() {
		Dataspace dataspace = new Dataspace();
		return dataspace;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Project project = projectHome.getDefinedInstance();
		if (project != null) {
			getInstance().setProject(project);
		}
	}

	public boolean isWired() {
		if (getInstance().getProject() == null)
			return false;
		return true;
	}

	public Dataspace getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<ProjectDatabase> getProjectDatabases() {
		return getInstance() == null ? null : new ArrayList<ProjectDatabase>(
				getInstance().getProjectDatabases());
	}
	
	@SuppressWarnings("unchecked")
	public List<Dataspace> findByProjectID(int projectIDValue){
		Query query = this.getEntityManager().createNamedQuery("Dataspace.findByProjectID");
		query.setParameter("projectID", new Integer(projectIDValue));
		List<Dataspace> projectDataspaceList =  query.getResultList();
		return projectDataspaceList;
	}

}
