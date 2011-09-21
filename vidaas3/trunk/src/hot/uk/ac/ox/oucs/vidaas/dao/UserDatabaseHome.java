package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("userDatabaseHome")
public class UserDatabaseHome extends EntityHome<UserDatabase> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8316254403715244387L;
	@In(create = true)
	UsersHome usersHome;
	@In(create = true)
	ProjectDatabaseHome projectDatabaseHome;

	public void setUserDatabaseId(UserDatabaseId id) {
		setId(id);
	}

	public UserDatabaseId getUserDatabaseId() {
		return (UserDatabaseId) getId();
	}

	public UserDatabaseHome() {
		setUserDatabaseId(new UserDatabaseId());
	}

	@Override
	public boolean isIdDefined() {
		if (getUserDatabaseId().getDatabaseId() == 0)
			return false;
		if (getUserDatabaseId().getUserId() == 0)
			return false;
		return true;
	}

	@Override
	protected UserDatabase createInstance() {
		UserDatabase userDatabase = new UserDatabase();
		userDatabase.setId(new UserDatabaseId());
		return userDatabase;
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
		ProjectDatabase projectDatabase = projectDatabaseHome
				.getDefinedInstance();
		if (projectDatabase != null) {
			getInstance().setProjectDatabase(projectDatabase);
		}
	}

	public boolean isWired() {
		if (getInstance().getUsers() == null)
			return false;
		if (getInstance().getProjectDatabase() == null)
			return false;
		return true;
	}

	public UserDatabase getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
