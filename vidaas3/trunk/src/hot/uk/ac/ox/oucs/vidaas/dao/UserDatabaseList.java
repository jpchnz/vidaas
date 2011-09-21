package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("userDatabaseList")
public class UserDatabaseList extends EntityQuery<UserDatabase> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7897110464194305969L;

	private static final String EJBQL = "select userDatabase from UserDatabase userDatabase";

	private static final String[] RESTRICTIONS = { "lower(userDatabase.userRole) like lower(concat(#{userDatabaseList.userDatabase.userRole},'%'))", };

	private UserDatabase userDatabase;

	public UserDatabaseList() {
		userDatabase = new UserDatabase();
		userDatabase.setId(new UserDatabaseId());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public UserDatabase getUserDatabase() {
		return userDatabase;
	}
}
