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
 * Servlet that returns the check-in status of the participants of an event
 * @author Yunus Evren
 */
public class GetCheckinStatus extends HttpServlet {

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
		result = getCheckinStatus(eventId);
	}


	/**
	 * Gets the check-in status of each event participant and prints it with their name.
	 * @param eventId - event id
	 * @return true if the check-in status info is successfully printed
	 */
	private boolean getCheckinStatus(String eventId) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");
			
			Statement getFounderStmt = conn.createStatement();
			String getFounderQuery = "SELECT u.user_name, u.user_id, ua.checkin_status FROM user u, user_attends ua, event e WHERE e.founder=u.user_id AND e.founder=ua.user_id AND e.event_id=ua.event_id AND e.event_id=" + eventId;
			ResultSet getFounderResult = getFounderStmt.executeQuery(getFounderQuery);
			
			getFounderResult.next();
			String founderId = getFounderResult.getString("user_id");
			out.println(getFounderResult.getString("user_name")+"|" + getFounderResult.getInt("checkin_status"));
			
			Statement getParticipantsStmt = conn.createStatement();
			String getParticipantsQuery = "SELECT u.user_name, ua.checkin_status FROM user u, user_attends ua WHERE u.user_id=ua.user_id AND ua.event_id=" + eventId + " AND ua.user_id<>'" + founderId + "'";
			ResultSet getParticipantsResult = getParticipantsStmt.executeQuery(getParticipantsQuery);
			
			while(getParticipantsResult.next()){
				String name = getParticipantsResult.getString("user_name");
				int confirmed = getParticipantsResult.getInt("checkin_status");
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

