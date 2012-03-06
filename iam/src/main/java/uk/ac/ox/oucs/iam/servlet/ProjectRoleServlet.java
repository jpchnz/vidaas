package uk.ac.ox.oucs.iam.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ox.oucs.iam.utilities.UserRoles;

@SuppressWarnings("serial")
public class ProjectRoleServlet extends HttpServlet {
	private PrintWriter out;
	private UserRoles userRoles = new UserRoles();
	
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		out = response.getWriter();
		
		checkRequest(request);

		out.flush();
		out.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		out = response.getWriter();
		
		checkRequest(request);

		out.flush();
		out.close();
	}
	
	
	/**
	 * Check the request sent in and perform actions based on it.
	 * @param request
	 */
	private void checkRequest(HttpServletRequest request) {
		String command = request.getParameter("isOwner");
		if (command != null) {
			out.println(userRoles.isOwner(command));
		}
		
		command = request.getParameter("isAllowedToCreateDatabaseByRole");
		if (command != null) {
			out.println(userRoles.isAllowedToCreateDatabaseByRole(command));
		}
		
		command = request.getParameter("isAllowedToEditDatabaseByRole");
		if (command != null) {
			out.println(userRoles.isAllowedToEditDatabaseByRole(command));
		}
		
		command = request.getParameter("isAllowedToDeleteDatabaseByRole");
		if (command != null) {
			out.println(userRoles.isAllowedToDeleteDatabaseByRole(command));
		}
		
		command = request.getParameter("isAllowedToAlterOtherUsersRole");
		if (command != null) {
			out.println(userRoles.isAllowedToAlterOtherUsersRole(command));
		}
		
		command = request.getParameter("isAllowedToRemoveProject");
		if (command != null) {
			out.println(userRoles.isAllowedToRemoveProject(command));
		}
		
		command = request.getParameter("isAllowedToAddOthersForProject");
		if (command != null) {
			out.println(userRoles.isAllowedToAddOthersForProject(command));
		}
		
		command = request.getParameter("isAllowedToRemoveOthersForProject");
		if (command != null) {
			out.println(userRoles.isAllowedToRemoveOthersForProject(command));
		}
		
		command = request.getParameter("isAllowedToAddEditOrRemoveDatabaseDataByRole");
		if (command != null) {
			out.println(userRoles.isAllowedToAddEditOrRemoveDatabaseDataByRole(command));
		}
		
		command = request.getParameter("isAllowedToCreateWebAppByRole");
		if (command != null) {
			out.println(userRoles.isAllowedToCreateWebAppByRole(command));
		}
		
		command = request.getParameter("isAllowedToDeleteWebAppByRole");
		if (command != null) {
			out.println(userRoles.isAllowedToDeleteWebAppByRole(command));
		}
		
		command = request.getParameter("isViewProjectData");
		if (command != null) {
			out.println(userRoles.isViewProjectData(command));
		}
		
		command = request.getParameter("isCreatePublicViewsOfData");
		if (command != null) {
			out.println(userRoles.isCreatePublicViewsOfData(command));
		}
		
		command = request.getParameter("isAllowedToRemoveProjectByRole");
		if (command != null) {
			out.println(userRoles.isAllowedToRemoveProjectByRole(command));
		}
		
		command = request.getParameter("isAllowedToEditProjectByRole");
		if (command != null) {
			out.println(userRoles.isAllowedToEditProjectByRole(command));
		}

		command = request.getParameter("getOwnerRole");
		if (command != null) {
			out.println(userRoles.getOwnerRole());
		}
		
		
		command = request.getParameter("getRoles");
		if (command != null) {
			getRoles();
			//out.println(userRoles.isAllowedToCreateDatabaseByRole(command));
		}
		
		command = request.getParameter("getRolesWithoutOwner");
		if (command != null) {
			getRolesExcludeOwner();
			//out.println(userRoles.isAllowedToCreateDatabaseByRole(command));
		}
	}
	
	
	/**
	 * List all available roles within IAM
	 */
	private void getRoles() {
		String [] result = userRoles.getRolesAsArray();
		for (int count = 0; count < result.length; count++) {
			out.println(result[count]);
		}
	}
	
	/**
	 * List all available roles within IAM except for the (special) owner role
	 */
	private void getRolesExcludeOwner() {
		String [] result = userRoles.getRolesAsArrayExcludeOwner();
		for (int count = 0; count < result.length; count++) {
			out.println(result[count]);
		}
	}
}
