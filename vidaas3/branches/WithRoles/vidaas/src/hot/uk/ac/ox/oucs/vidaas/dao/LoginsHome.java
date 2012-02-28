package uk.ac.ox.oucs.vidaas.dao;

import java.util.List;

import javax.persistence.Query;

import uk.ac.ox.oucs.vidaas.entity.*;

import uk.ac.ox.oucs.vidaas.entity.Logins;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("loginsHome")
@SuppressWarnings("unchecked")
public class LoginsHome extends EntityHome<Logins> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5807391696613608639L;
	@In(create = true)
	UsersHome usersHome;

	public void setLoginsUserName(String id) {
		setId(id);
	}

	public String getLoginsUserName() {
		return (String) getId();
	}

	@Override
	protected Logins createInstance() {
		Logins logins = new Logins();
		return logins;
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
	}

	public boolean isWired() {
		if (getInstance().getUsers() == null)
			return false;
		return true;
	}

	public Logins getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	public List<Logins> findByUserName(String userNameValue){
		Query query = this.getEntityManager().createNamedQuery("Logins.findByUserName");
		query.setParameter("userName", new String(userNameValue));
		List<Logins> loginsList = query.getResultList();
		return loginsList;
	}
	
	public List<Logins> findByShibTargetedId(String shibTargetedId){
        Query query = this.getEntityManager().createNamedQuery("Logins.findByUserShibTargetedId");
        query.setParameter("shibTargetedId", new String(shibTargetedId));
        List<Logins> loginsList = query.getResultList();
        return loginsList;
}


}
