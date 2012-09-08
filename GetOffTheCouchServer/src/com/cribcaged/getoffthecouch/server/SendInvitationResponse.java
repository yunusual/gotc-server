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
 * Servlet that receives the invitation response of a user and registers it into the database.
 * @author Yunus Evren
 */
public class SendInvitationResponse extends HttpServlet {

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
		String invitationId = request.getParameter("invitation_id");
		int userResponse = Integer.parseInt(request.getParameter("user_response"));

		boolean sendInvitationResult = sendInvitationResponse(facebookId, eventId, invitationId, userResponse);
		if(sendInvitationResult){
			out.println("SendInvitationResponse: OK");
		}
		else{
			out.println("SendInvitationResponse: ERROR");
		}
		
	}


	/**
	 * Stores the invitation response that is received from the user into the database.
	 * Checks if all the users of an event has responded positively. If yes, changes the status
	 * of that event from "pending" to "confirmed".
	 * @param facebookId - Facebook id of the user
	 * @param eventId - event id
	 * @param invitationId - invitation id
	 * @param userResponse - response of the user
	 * @return true if the response is successfully recorded
	 */
	private boolean sendInvitationResponse(String facebookId, String eventId, String invitationId, int userResponse) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");
			
			
			if(userResponse==1){
				// Accepted
				Statement updateNotificationStmt = conn.createStatement();
				String updateNotificationQuery = "UPDATE invitation SET confirmed = 1 WHERE inv_id=" + invitationId;
				updateNotificationStmt.executeUpdate(updateNotificationQuery);
				
				Statement insertUserAttendsStmt = conn.createStatement();
				String insertUserAttendsQuery = "INSERT INTO user_attends VALUES ('" + facebookId + "', " + eventId + ", 0)";
				insertUserAttendsStmt.executeUpdate(insertUserAttendsQuery);
				
				Statement invitedPeopleCountStmt = conn.createStatement();
				String invitedPeopleCountQuery = "SELECT COUNT(*)+1 FROM invitation WHERE event_id=" + eventId;
				ResultSet invitedPeopleCountResult = invitedPeopleCountStmt.executeQuery(invitedPeopleCountQuery);
				invitedPeopleCountResult.next();
				int invitedPeopleCount = invitedPeopleCountResult.getInt(1);
				
				Statement attendingPeopleCountStmt = conn.createStatement();
				String attendingPeopleCountQuery = "SELECT COUNT(*) FROM user_attends WHERE event_id=" + eventId;
				ResultSet attendingPeopleCountResult = attendingPeopleCountStmt.executeQuery(attendingPeopleCountQuery);
				attendingPeopleCountResult.next();
				int attendingPeopleCount = attendingPeopleCountResult.getInt(1);
				
				if(invitedPeopleCount == attendingPeopleCount){
					// Everyone has accepted the event. The event is now confirmed.
					Statement updateEventConfirmedStmt = conn.createStatement();
					String updateEventConfirmedQuery = "UPDATE event SET confirmed = 1 WHERE event_id=" + eventId;
					updateEventConfirmedStmt.executeUpdate(updateEventConfirmedQuery);
					
					// Send notification to attending people
					Statement getAttendingPeopleStmt = conn.createStatement();
					String getAttendingPeopleQuery = "SELECT user_id FROM user_attends WHERE event_id=" + eventId;
					ResultSet getAttendingPeopleResult = getAttendingPeopleStmt.executeQuery(getAttendingPeopleQuery);
					while(getAttendingPeopleResult.next()){
						String userId = getAttendingPeopleResult.getString("user_id");
						String link = "http://localhost:8080/gotc/servlet/com.cribcaged.getoffthecouch.server.SendConfirmation?"
							+"user_id=" + userId;
						URL url = new URL(link);
						URLConnection urlConn = url.openConnection();

						BufferedReader in = 
							new BufferedReader( new InputStreamReader( urlConn.getInputStream() ) );
						int count = 0;

						ArrayList<String> responseList = new ArrayList<String>();
						String response;
						while((response=in.readLine())!=null){
							responseList.add(response);
						}
					}
				}
			}
			else{
				// Rejected
				Statement updateNotificationStmt = conn.createStatement();
				String updateNotificationQuery = "UPDATE invitation SET confirmed=2 WHERE inv_id=" + invitationId;
				updateNotificationStmt.executeUpdate(updateNotificationQuery);
				
				// The event is cancelled since at least one person rejected joining
				Statement updateEventConfirmedStmt = conn.createStatement();
				String updateEventConfirmedQuery = "UPDATE event SET confirmed=2 WHERE event_id=" + eventId;
				updateEventConfirmedStmt.executeUpdate(updateEventConfirmedQuery);
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
		} catch (MalformedURLException e) {
			e.printStackTrace(out);
			return false;
		} catch (IOException e) {
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

