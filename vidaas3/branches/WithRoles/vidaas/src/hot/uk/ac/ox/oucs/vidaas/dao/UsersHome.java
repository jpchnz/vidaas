package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("usersHome")
public class UsersHome extends EntityHome<Users> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 510156265459358601L;

	public void setUsersUserId(Integer id) {
		setId(id);
	}

	public Integer getUsersUserId() {
		return (Integer) getId();
	}

	@Override
	protected Users createInstance() {
		Users users = new Users();
		return users;
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

	public Users getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<UserDatabase> getUserDatabases() {
		return getInstance() == null ? null : new ArrayList<UserDatabase>(
				getInstance().getUserDatabases());
	}

	public Logins getLogins() {
		return getInstance() == null ? null : (
				getInstance().getLogins());
	}

	public List<UserProject> getUserProjects() {
		return getInstance() == null ? null : new ArrayList<UserProject>(
				getInstance().getUserProjects());
	}

	public List<SchemaLog> getSchemaLogs() {
		return getInstance() == null ? null : new ArrayList<SchemaLog>(
				getInstance().getSchemaLogs());
	}
	
	@SuppressWarnings("unchecked")
	public List<Users> findUserByEmail(String emailValue){
		Query query = this.getEntityManager().createNamedQuery("Users.findByEmail");
		query.setParameter("email", new String(emailValue));
		List<Users> usersList = query.getResultList();
		return usersList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Users> findUserByFirstName(String firstNameValue){
		Query query = this.getEntityManager().createNamedQuery("Users.findByFirstName");
		query.setParameter("firstName", new String(firstNameValue));
		List<Users> usersList = query.getResultList();
		return usersList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Users> findUserByShibId(String shibId){
        Query query = this.getEntityManager().createNamedQuery("Users.findByShibTargetedId");
        query.setParameter("shibTargetedId", new String(shibId));
        List<Users> usersList = query.getResultList();
        return usersList;
}


}
