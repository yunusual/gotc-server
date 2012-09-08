package com.cribcaged.getoffthecouch.server;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Servlet that unregisters a user from the database and switches his/her notification status off.
 * @author Yunus Evren
 */
public class Unregistration extends HttpServlet {

	private PrintWriter out;
	private Connection conn;

	public void init(ServletConfig config)
	throws ServletException {

		super.init(config);
	}

	public void doGet(HttpServletRequest request,
			HttpServletResponse response)
	throws ServletException, IOException {

		out = response.getWriter();

		String facebookId = request.getParameter("facebook_id");

		boolean unregistrationResult = unregister(facebookId);
		if(unregistrationResult){
			out.println("Unregistration: OK");
		}
		else{
			out.println("Unregistration: ERROR");
		}
	}


	/**
	 * Unregisters the user by setting the notification status off.
	 * @param facebookId - Facebook id of the user
	 * @return true if the user is successfully unregistered
	 */
	private boolean unregister(String facebookId) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");
			
			Statement unregistrationStmt = conn.createStatement();
			String unregistrationQuery = "UPDATE user SET reg_status=0 WHERE user_id='" +facebookId + "'";
			unregistrationStmt.executeUpdate(unregistrationQuery);
			return true;
		} catch (InstantiationException e) {
			e.printStackTrace(out);
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace(out);
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace(out);
			return false;
		} catch (SQLException e) {
			e.printStackTrace(out);
			return false;
		}
	}


	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
	throws ServletException, IOException {

	}

	public void destroy() {
	}
}

