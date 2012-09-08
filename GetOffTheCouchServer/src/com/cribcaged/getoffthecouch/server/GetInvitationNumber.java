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
 * Servlet that returns the total number of invitations of a user.
 * @author Yunus Evren
 */
public class GetInvitationNumber extends HttpServlet {

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

		int getInvitationNumberResult = getInvitationNumber(facebookId);
		if(getInvitationNumberResult!=-1){
			out.println("GetInvitationNumber: OK");
			out.println(getInvitationNumberResult);
		}
		else{
			out.println("GetInvitationNumber: ERROR");
		}
		
	}


	/**
	 * Gets the number of event invitations from the database and prints it.
	 * @param facebookId - Facebook id of the user
	 * @return the number of invitations the user has. "-1" if it fails.
	 */
	private int getInvitationNumber(String facebookId) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");
			
			Statement checkStmt = conn.createStatement();
			String checkQuery = "SELECT COUNT(*) FROM invitation WHERE invitee='" + facebookId + "' AND confirmed=0";
			ResultSet checkQueryResult = checkStmt.executeQuery(checkQuery);
			checkQueryResult.next();
			int count = checkQueryResult.getInt(1);
			return count;
		} catch (InstantiationException e) {
			e.printStackTrace(out);
			return -1;
		} catch (IllegalAccessException e) {
			e.printStackTrace(out);
			return -1;
		} catch (ClassNotFoundException e) {
			e.printStackTrace(out);
			return -1;
		} catch (SQLException e) {
			e.printStackTrace(out);
			return -1;
		}
	}


	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
	throws ServletException, IOException {

	}

	public void destroy() {
	}
}

