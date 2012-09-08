package com.cribcaged.getoffthecouch.server;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Servlet that registers a user into the database and switches his/her notification status on.
 * @author Yunus Evren
 */
public class Registration extends HttpServlet {

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
		String name = request.getParameter("name");
		String regId = request.getParameter("reg_id");

		boolean registrationResult = register(facebookId, name, regId);
		if(registrationResult){
			out.println("Registration: OK");
		}
		else{
			out.println("Registration: ERROR");
		}
		
	}


	/**
	 * If the user is authenticating for the first time, creates a row in the user table.
	 * If the user is already registered in the database, switches the notification status on.
	 * @param facebookId - Facebook id of the user
	 * @param name - full name of the user
	 * @param regId - registration id for the device of the user to be used in GCM notifications
	 * @return true if the registration is successful
	 */
	private boolean register(String facebookId, String name, String regId) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");
			
			Statement checkStmt = conn.createStatement();
			String checkQuery = "SELECT COUNT(*) FROM user WHERE user_id='" + facebookId + "'";
			ResultSet checkQueryResult = checkStmt.executeQuery(checkQuery);
			checkQueryResult.next();
			int count = checkQueryResult.getInt(1);
			if(count>0){
				// User is already listed on DB
				Statement updateStmt = conn.createStatement();
				String updateQuery = "UPDATE user SET reg_status = 1 WHERE user_id = '" + facebookId + "'";
				updateStmt.executeUpdate(updateQuery);
			}
			else{
				Statement registrationStmt = conn.createStatement();
				String registrationQuery = "INSERT INTO user VALUES ('" + facebookId + "', '" + name + "', '" + regId + "', 1, 0)";
				registrationStmt.executeUpdate(registrationQuery);
			}
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

