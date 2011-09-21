package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("userProjectList")
public class UserProjectList extends EntityQuery<UserProject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9027591437104310069L;

	private static final String EJBQL = "select userProject from UserProject userProject";

	private static final String[] RESTRICTIONS = { "lower(userProject.userRole) like lower(concat(#{userProjectList.userProject.userRole},'%'))", };

	private UserProject userProject;

	public UserProjectList() {
		userProject = new UserProject();
		userProject.setId(new UserProjectId());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public UserProject getUserProject() {
		return userProject;
	}
}
