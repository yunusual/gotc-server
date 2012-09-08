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
 * Servlet that returns the name of the participants of an event
 * @author Yunus Evren
 */
public class GetEventParticipants extends HttpServlet {

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
		result = getEventParticipants(eventId);
	}


	/**
	 * Gets the participants of an event and prints their names.
	 * @param eventId - event id
	 * @return true if the participants names are successfully printed
	 */
	private boolean getEventParticipants(String eventId) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");
			
			Statement getParticipantsStmt = conn.createStatement();
			String getParticipantsQuery = "SELECT u.user_name FROM user_attends ua, user u WHERE u.user_id=ua.user_id AND ua.event_id=" + eventId;
			ResultSet getParticipantsResult = getParticipantsStmt.executeQuery(getParticipantsQuery);
			
			while(getParticipantsResult.next()){
				out.println(getParticipantsResult.getString("user_name"));
			}
			getParticipantsStmt.close();
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

