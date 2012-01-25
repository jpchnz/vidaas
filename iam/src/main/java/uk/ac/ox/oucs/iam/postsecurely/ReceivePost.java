package uk.ac.ox.oucs.iam.postsecurely;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReceivePost extends HttpServlet {
	private PrintWriter out;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		out = response.getWriter();

		// Let's not do anything here ... we should just worry about POST for
		// now

		out.flush();
		out.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		out = response.getWriter();

		checkRequest(request);

		out.flush();
		out.close();
	}

	private void checkRequest(HttpServletRequest request) {
		Enumeration<?> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String data = (String) e.nextElement();
			if (data != null) {
				out.println("" + data + " - ");
				out.println("" + request.getParameter(data) + "");
			}
		}
	}
}
