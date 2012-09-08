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
 * Servlet that returns the coordinates of an event location.
 * @author Yunus Evren
 */
public class GetLocationCoordinates extends HttpServlet {

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
		result = getLocationCoordinates(eventId);
	}


	/**
	 * Gets the latitude and longitude of an event location and prints them.
	 * @param eventId - event id
	 * @return true if the coordinates are printed successfully
	 */
	private boolean getLocationCoordinates(String eventId) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");
			
			Statement getLocationCoordinatesStmt = conn.createStatement();
			String getLocationCoordinatesQuery = "SELECT l.latitude, l.longitude FROM location l, event e WHERE e.event_id=" + eventId + " AND e.loc_id=l.loc_id";
			ResultSet getLocationCoordinatesResult = getLocationCoordinatesStmt.executeQuery(getLocationCoordinatesQuery);
			
			getLocationCoordinatesResult.next();
			
			out.println(getLocationCoordinatesResult.getString("latitude") + "|" + getLocationCoordinatesResult.getString("longitude"));
			
			getLocationCoordinatesStmt.close();
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

