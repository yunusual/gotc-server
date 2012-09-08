package com.cribcaged.getoffthecouch.server;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Servlet that returns the name of the invited people of an event.
 * @author Yunus Evren
 */
public class GetInvitedFriends extends HttpServlet {

	private PrintWriter out;
	private Connection conn;

	public void init(ServletConfig config)
	throws ServletException {

		super.init(config);
	}

	public void doGet(HttpServletRequest request,
			HttpServletResponse response)
	throws ServletException, IOException {

		String eventId = request.getParameter("event_id");

		out = response.getWriter();
		
		boolean result = false;
		result = getInvitations(eventId);
	}


	/**
	 * Gets the name of each invited people to an event and prints them.
	 * @param eventId - event id
	 * @return true if the participants are printed successfully
	 */
	private boolean getInvitations(String eventId) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");
			
			Statement getInvitationFriendsStmt = conn.createStatement();
			String getInvitationFriendsQuery = "SELECT DISTINCT u.user_name FROM invitation i, user u WHERE i.event_id=" + eventId + " AND i.invitee=u.user_id";
			ResultSet getInvitationFriendsResult = getInvitationFriendsStmt.executeQuery(getInvitationFriendsQuery);
			
			while(getInvitationFriendsResult.next()){
				out.println(getInvitationFriendsResult.getString("user_name"));
			}
			getInvitationFriendsStmt.close();
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

