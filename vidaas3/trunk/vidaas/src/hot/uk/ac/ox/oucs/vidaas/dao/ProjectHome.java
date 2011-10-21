package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("projectHome")
public class ProjectHome extends EntityHome<Project> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 54056676674689743L;

	public void setProjectProjectId(Integer id) {
		setId(id);
	}

	public Integer getProjectProjectId() {
		return (Integer) getId();
	}

	@Override
	protected Project createInstance() {
		Project project = new Project();
		return project;
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

	public Project getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<Dataspace> getDataspaces() {
		return getInstance() == null ? null : new ArrayList<Dataspace>(
				getInstance().getDataspaces());
	}

	public List<UserProject> getUserProjects() {
		return getInstance() == null ? null : new ArrayList<UserProject>(
				getInstance().getUserProjects());
	}

}
