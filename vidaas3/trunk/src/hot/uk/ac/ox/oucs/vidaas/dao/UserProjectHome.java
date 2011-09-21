package uk.ac.ox.oucs.vidaas.dao;

import java.util.List;

import javax.persistence.Query;

import uk.ac.ox.oucs.vidaas.entity.*;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("userProjectHome")
public class UserProjectHome extends EntityHome<UserProject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8679088349813914231L;
	@In(create = true)
	UsersHome usersHome;
	@In(create = true)
	ProjectHome projectHome;

	public void setUserProjectId(UserProjectId id) {
		setId(id);
	}

	public UserProjectId getUserProjectId() {
		return (UserProjectId) getId();
	}

	public UserProjectHome() {
		setUserProjectId(new UserProjectId());
	}

	@Override
	public boolean isIdDefined() {
		if (getUserProjectId().getProjectId() == 0)
			return false;
		if (getUserProjectId().getUserId() == 0)
			return false;
		return true;
	}

	@Override
	protected UserProject createInstance() {
		UserProject userProject = new UserProject();
		userProject.setId(new UserProjectId());
		return userProject;
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
		Project project = projectHome.getDefinedInstance();
		if (project != null) {
			getInstance().setProject(project);
		}
	}

	public boolean isWired() {
		if (getInstance().getUsers() == null)
			return false;
		if (getInstance().getProject() == null)
			return false;
		return true;
	}

	public UserProject getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@SuppressWarnings("unchecked")
	public List<UserProject> findByUserID(int userIDValue){
		Query query = this.getEntityManager().createNamedQuery("UserProject.findByUserID");
		query.setParameter("userID", new Integer(userIDValue));
		List<UserProject> userProjectList = query.getResultList();
		return userProjectList;
	}
	
	@SuppressWarnings("unchecked")
	public List<UserProject> findByProjectID(int projectIDValue){
		Query query = this.getEntityManager().createNamedQuery("UserProject.findByProjectID");
		query.setParameter("projectID", new Integer(projectIDValue));
		List<UserProject> userProjectList = query.getResultList();
		return userProjectList;
	}
	
	@SuppressWarnings("unchecked")
	public List<UserProject> findByUserIDAndProjectID(int userIDValue, int projectIDValue){
		Query query = this.getEntityManager().createNamedQuery("UserProject.findByUserIDAndProjectID");
		query.setParameter("userID", new Integer(userIDValue));
		query.setParameter("projectID", new Integer(projectIDValue));
		List<UserProject> userProjectList = query.getResultList();
		return userProjectList;
	}

}
