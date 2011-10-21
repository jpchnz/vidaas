package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("usersList")
public class UsersList extends EntityQuery<Users> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5051750949390587629L;

	private static final String EJBQL = "select users from Users users";

	private static final String[] RESTRICTIONS = {
			"lower(users.department) like lower(concat(#{usersList.users.department},'%'))",
			"lower(users.email) like lower(concat(#{usersList.users.email},'%'))",
			"lower(users.firstName) like lower(concat(#{usersList.users.firstName},'%'))",
			"lower(users.grp) like lower(concat(#{usersList.users.grp},'%'))",
			"lower(users.lastName) like lower(concat(#{usersList.users.lastName},'%'))",
			"lower(users.position) like lower(concat(#{usersList.users.position},'%'))", };

	private Users users = new Users();

	public UsersList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Users getUsers() {
		return users;
	}
}
