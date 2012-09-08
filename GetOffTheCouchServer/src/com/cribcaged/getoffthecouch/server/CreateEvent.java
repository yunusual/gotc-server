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
 * Servlet that creates an event in the database.
 * @author Yunus Evren
 */
public class CreateEvent extends HttpServlet {

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

		String locId = request.getParameter("loc_id");
		String userId = request.getParameter("user_id");
		String date = request.getParameter("date");
		String time = request.getParameter("time");
		String totalScore = request.getParameter("total_score");
		String details = request.getParameter("details");

		int registrationResult = createEvent(locId, userId, date, time, totalScore, details);
		if(registrationResult!=-1){
			out.println("CreateEvent: OK");
			out.println(registrationResult);
		}
		else{
			out.println("CreateEvent: ERROR");
		}

	}


	/**
	 * Creates an event entry in the database using the input parameters.
	 * @param locId - location id of the event
	 * @param userId - Facebook id of the event organiser
	 * @param date - date of the event
	 * @param time - time of the event
	 * @param totalScore - total score to be earned when the event is completed
	 * @param details - description of the event
	 * @return If the event is successfully created, returns the event id. Else, returns "-1".
	 */
	private int createEvent(String locId, String userId, String date, String time, String totalScore, String details) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");

			Statement eventIdStmt = conn.createStatement();
			String eventIdQuery = "SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA='getoffthecouch' AND TABLE_NAME='event'";
			ResultSet eventIdResult = eventIdStmt.executeQuery(eventIdQuery);
			
			eventIdResult.next();
			int eventId = -1;
			eventId = eventIdResult.getInt(1);

			Statement insertEventStmt = conn.createStatement();
			String insertEventQuery = "INSERT INTO event VALUES (null, " + locId + ", '" + userId + "', '" + details + "', '" + date + "', '" + time + "', " + totalScore + ", 0, 0)";
			insertEventStmt.executeUpdate(insertEventQuery);
			
			Statement insertUserAttendsStmt = conn.createStatement();
			String insertUserAttendsQuery = "INSERT INTO user_attends VALUES ('" + userId + "', " + eventId + ", 0)";
			insertUserAttendsStmt.executeUpdate(insertUserAttendsQuery);
			
			return eventId;
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

