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
 * Servlet that returns the names and participation status of an event's participants
 * @author Yunus Evren
 */
public class GetPendingParticipants extends HttpServlet {

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
	 * Gets the name and participation status of the event's participants and prints them.
	 * @param eventId - event id
	 * @return true if the participant names and statuses are printed successfully
	 */
	private boolean getEventParticipants(String eventId) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");
			
			Statement getFounderStmt = conn.createStatement();
			String getFounderQuery = "SELECT u.user_name FROM event e, user u WHERE e.founder=u.user_id AND e.event_id=" + eventId;
			ResultSet getFounderResult = getFounderStmt.executeQuery(getFounderQuery);
			
			getFounderResult.next();
			out.println(getFounderResult.getString("user_name")+"|1");
			
			Statement getParticipantsStmt = conn.createStatement();
			String getParticipantsQuery = "SELECT u.user_name, i.confirmed FROM invitation i, user u WHERE u.user_id=i.invitee AND i.event_id=" + eventId;
			ResultSet getParticipantsResult = getParticipantsStmt.executeQuery(getParticipantsQuery);
			
			while(getParticipantsResult.next()){
				String name = getParticipantsResult.getString("user_name");
				int confirmed = getParticipantsResult.getInt("confirmed");
				if(confirmed==1){
					out.println(name+"|1");
				}
				else{
					out.println(name+"|0");
				}
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

