package uk.ac.ox.oucs.iam;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ProjectRoleServlet extends HttpServlet {
	private PrintWriter out;
	
	
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
		String command = request.getParameter("checkIsOwner");
		if (command != null) {
			isOwner(command);
		}
		
		command = request.getParameter("checkIsAllowedToCreateDatabaseByRole");
		if (command != null) {
			isAllowedToCreateDatabaseByRole(command);
		}

		command = request.getParameter("getRoles");
		if (command != null) {
			getRoles();
		}
	}
	
	
	/**
	 * List all available roles within IAM
	 */
	private void getRoles() {
		String [] result = UserRoles.getRolesAsArray();
		for (int count = 0; count < result.length; count++) {
			out.println(result[count]);
		}
	}
	
	private void isOwner(String role) {
		boolean result = UserRoles.isOwner(role);
		out.println(result);
	}
	
	private void isAllowedToCreateDatabaseByRole(String role) {
		boolean result = UserRoles.isAllowedToCreateDatabaseByRole(role);
		out.println(result);
	}
}
