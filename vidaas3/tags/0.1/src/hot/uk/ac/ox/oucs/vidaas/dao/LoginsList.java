package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("loginsList")
public class LoginsList extends EntityQuery<Logins> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7058766972613398268L;

	private static final String EJBQL = "select logins from Logins logins";

	private static final String[] RESTRICTIONS = {
			"lower(logins.userName) like lower(concat(#{loginsList.logins.userName},'%'))",
			"lower(logins.password) like lower(concat(#{loginsList.logins.password},'%'))", };

	private Logins logins = new Logins();

	public LoginsList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Logins getLogins() {
		return logins;
	}
}
