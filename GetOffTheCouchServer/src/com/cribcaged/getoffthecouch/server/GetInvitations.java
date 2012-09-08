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
 * Servlet that returns a list of event invitation details.
 * @author Yunus Evren
 */
public class GetInvitations extends HttpServlet {

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
		result = getInvitations(facebookId);
	}


	/**
	 * Gets the invitations from the database and prints their details.
	 * @param facebookId - Facebook id of the user
	 * @return true if the list of invitation details are printed successfully
	 */
	private boolean getInvitations(String facebookId) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");
			
			Statement checkStmt = conn.createStatement();
			String checkQuery = "SELECT COUNT(*) FROM invitation WHERE invitee='" + facebookId + "' AND confirmed=0";
			ResultSet checkQueryResult = checkStmt.executeQuery(checkQuery);
			checkQueryResult.next();
			int count = checkQueryResult.getInt(1);
			
			if(count==0){
				return false;
			}
			
			Statement getInvitationsStmt = conn.createStatement();
			String getInvitationsQuery = "SELECT i.inv_id, l.loc_name, e.date, e.time, u.user_name, e.total_score, l.cat_id, i.event_id, l.photo_thumb " +
						"FROM invitation i, location l, event e, user u WHERE i.event_id=e.event_id AND e.loc_id = l.loc_id " +
						"AND i.invitee='" + facebookId + "' AND u.user_id=i.inviter AND i.confirmed=0";
			ResultSet getInvitationsResult = getInvitationsStmt.executeQuery(getInvitationsQuery);
			int invitationId = -1;
			String locationName = "";
			String dateAndTime = "";
			String userName = "";
			int totalScore = -1;
			int categoryId = -1;
			int eventId = -1;
			String photoThumb = "";
			
			while(getInvitationsResult.next()){
				invitationId = getInvitationsResult.getInt("inv_id");
				locationName = getInvitationsResult.getString("loc_name");
				String date = getInvitationsResult.getString("date");
				String time = getInvitationsResult.getString("time");
				SimpleDateFormat sqlFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					Date dateObject = sqlFormatter.parse(date + " " + time);
					SimpleDateFormat outputFormatter = new SimpleDateFormat("d MMM yyyy, HH:mm");
					dateAndTime = outputFormatter.format(dateObject);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				userName = getInvitationsResult.getString("user_name");
				totalScore = getInvitationsResult.getInt("total_score");
				categoryId = getInvitationsResult.getInt("cat_id");
				eventId = getInvitationsResult.getInt("event_id");
				photoThumb = getInvitationsResult.getString("photo_thumb");
				String output = invitationId+"|"+locationName+"|"+dateAndTime+"|"+userName+"|"+totalScore+"|"+categoryId+"|"+eventId+"|"+photoThumb;
				out.println(output);
			}
			getInvitationsStmt.close();
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

