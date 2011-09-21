package uk.ac.ox.oucs.vidaas.dao;

import uk.ac.ox.oucs.vidaas.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("projectList")
public class ProjectList extends EntityQuery<Project> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8528115613771776504L;

	private static final String EJBQL = "select project from Project project";

	private static final String[] RESTRICTIONS = {
			"lower(project.description) like lower(concat(#{projectList.project.description},'%'))",
			"lower(project.title) like lower(concat(#{projectList.project.title},'%'))", };

	private Project project = new Project();

	public ProjectList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Project getProject() {
		return project;
	}
}
