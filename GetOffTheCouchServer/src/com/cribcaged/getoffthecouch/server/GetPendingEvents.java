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
import java.util.Calendar;
import java.util.Date;

/**
 * Servlet that returns the pending events of a user.
 * @author Yunus Evren
 */
public class GetPendingEvents extends HttpServlet {

	private PrintWriter out;
	private Connection conn;

	public void init(ServletConfig config)
	throws ServletException {

		super.init(config);
	}

	public void doGet(HttpServletRequest request,
			HttpServletResponse response)
	throws ServletException, IOException {

		String facebookId = request.getParameter("facebook_id");

		out = response.getWriter();
		
		boolean result = false;
		result = getPendingEvents(facebookId);
	}


	/**
	 * Gets the pending events of the given user from the database and prints their details.
	 * @param facebookId - Facebook id of the user
	 * @return true if the pending event details are printed successfully
	 */
	private boolean getPendingEvents(String facebookId) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");
			
			SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -1);
			String currentDate = yearFormat.format(cal.getTime());
			
			Statement checkStmt = conn.createStatement();
			String checkQuery = "SELECT COUNT(*) FROM event e, user_attends u WHERE u.user_id='" + facebookId + "' AND u.event_id=e.event_id AND e.date>'" + currentDate + "' AND e.completed=0";
			ResultSet checkQueryResult = checkStmt.executeQuery(checkQuery);
			checkQueryResult.next();
			int count = checkQueryResult.getInt(1);
			
			if(count==0){
				return false;
			}
			
			Statement getEventsStmt = conn.createStatement();
			String getEventsQuery = "SELECT e.event_id, e.loc_id, l.cat_id, l.loc_name, u.user_name, e.details, e.date, e.time, e.total_score, l.photo_thumb " +
						"FROM event e, location l, user_attends ua, user u WHERE ua.user_id='" + facebookId + "' AND e.founder=u.user_id AND e.loc_id=l.loc_id " +
						"AND ua.event_id=e.event_id AND e.date>'" + currentDate + "' AND e.confirmed=0 ORDER BY e.date ASC";
			ResultSet getEventsResult = getEventsStmt.executeQuery(getEventsQuery);
			int eventId = -1;
			int locationId = -1;
			int categoryId = -1;
			String locationName = "";
			String founderName = "";
			String details = "";
			String dateAndTime = "";
			int totalScore = -1;
			String photoThumb = "";
			
			while(getEventsResult.next()){
				eventId = getEventsResult.getInt("event_id");
				locationId = getEventsResult.getInt("loc_id");
				categoryId = getEventsResult.getInt("cat_id");
				locationName = getEventsResult.getString("loc_name");
				founderName = getEventsResult.getString("user_name");
				details = getEventsResult.getString("details");
				String date = getEventsResult.getString("date");
				String time = getEventsResult.getString("time");
				SimpleDateFormat sqlFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					Date dateObject = sqlFormatter.parse(date + " " + time);
					SimpleDateFormat outputFormatter = new SimpleDateFormat("d MMM yyyy, HH:mm");
					dateAndTime = outputFormatter.format(dateObject);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				totalScore = getEventsResult.getInt("total_score");
				photoThumb = getEventsResult.getString("photo_thumb");
				String output = eventId+"|"+locationId+"|"+categoryId+"|"+locationName+"|"+founderName+"|"+details+"|"+dateAndTime+"|"+totalScore+"|"+photoThumb;
				out.println(output);
			}
			getEventsStmt.close();
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

