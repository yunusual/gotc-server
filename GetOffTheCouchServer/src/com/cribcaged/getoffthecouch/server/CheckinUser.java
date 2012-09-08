package com.cribcaged.getoffthecouch.server;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Servlet that checks the user in.
 * @author Yunus Evren
 */
public class CheckinUser extends HttpServlet {

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
		String eventId = request.getParameter("event_id");

		boolean sendInvitationResult = checkinUser(facebookId, eventId);
		if(sendInvitationResult){
			out.println("SendInvitationResponse: OK");
		}
		else{
			out.println("SendInvitationResponse: ERROR");
		}

	}


	/**
	 * Updates the check-in status of the user in the database.
	 * Checks if all the event participants have checked in. If yes, then the event gets completed.
	 * @param facebookId - Facebook id of the user
	 * @param eventId - event id to check-in
	 * @return true if the checkin is succesfully recorded
	 */
	private boolean checkinUser(String facebookId, String eventId) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");

			Statement updateCheckinStmt = conn.createStatement();
			String updateCheckinQuery = "UPDATE user_attends SET checkin_status=1 WHERE user_id='" + facebookId + "' AND event_id=" + eventId;
			updateCheckinStmt.executeUpdate(updateCheckinQuery);

			Statement invitedPeopleCountStmt = conn.createStatement();
			String invitedPeopleCountQuery = "SELECT COUNT(*) FROM user_attends WHERE event_id=" + eventId;
			ResultSet invitedPeopleCountResult = invitedPeopleCountStmt.executeQuery(invitedPeopleCountQuery);
			invitedPeopleCountResult.next();
			int invitedPeopleCount = invitedPeopleCountResult.getInt(1);

			Statement checkedinPeopleCountStmt = conn.createStatement();
			String checkedinPeopleCountQuery = "SELECT COUNT(*) FROM user_attends WHERE event_id=" + eventId + " AND checkin_status=1";
			ResultSet checkedinPeopleCountResult = checkedinPeopleCountStmt.executeQuery(checkedinPeopleCountQuery);
			checkedinPeopleCountResult.next();
			int checkedinPeopleCount = checkedinPeopleCountResult.getInt(1);

			if(invitedPeopleCount == checkedinPeopleCount){
				// Everyone has checked in. The event is completed
				Statement updateEventConfirmedStmt = conn.createStatement();
				String updateEventConfirmedQuery = "UPDATE event SET completed=1 WHERE event_id=" + eventId;
				updateEventConfirmedStmt.executeUpdate(updateEventConfirmedQuery);

				// Update user scores
				Statement getParticipantsStmt = conn.createStatement();
				String getParticipantsQuery = "SELECT ua.user_id, e.total_score FROM user_attends ua, event e WHERE ua.event_id=" + eventId + " AND e.event_id=" + eventId;
				ResultSet getParticipantsResult = getParticipantsStmt.executeQuery(getParticipantsQuery);
				
				while(getParticipantsResult.next()){
					String userId = getParticipantsResult.getString("user_id");
					int total_score = getParticipantsResult.getInt("total_score");
					
					Statement updateUserScoreStmt = conn.createStatement();
					String updateUserScoreQuery = "UPDATE user SET total_score=total_score+" + total_score + " WHERE user_id='" + userId + "'";
					updateUserScoreStmt.executeUpdate(updateUserScoreQuery);
				}
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

