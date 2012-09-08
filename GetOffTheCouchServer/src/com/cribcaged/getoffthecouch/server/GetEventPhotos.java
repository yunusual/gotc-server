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
 * Servlet that returns the list of photos that belong to an event
 * @author Yunus Evren
 */
public class GetEventPhotos extends HttpServlet {

	private final String TOKEN = "72157631023904238-e82dca90321fd387";
	
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
		result = getPhotos(eventId);
	}


	/**
	 * Gets the photos of an event from the database and prints them.
	 * @param eventId - event id
	 * @return true if the photo details are printed successfully
	 */
	private boolean getPhotos(String eventId) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");
			
			Statement getPhotosStmt = conn.createStatement();
			String getPhotosQuery = "SELECT p.photo_id, u.user_name FROM photo p, user u WHERE p.user_id=u.user_id AND event_id=" + eventId;
			ResultSet getPhotosResult = getPhotosStmt.executeQuery(getPhotosQuery);
			String photoId = "";
			String userName = "";
			while(getPhotosResult.next()){
				photoId = getPhotosResult.getString("photo_id");
				userName = getPhotosResult.getString("user_name");
				out.println(photoId+"|"+eventId+ "|"+userName+"|"+TOKEN);
			}
			getPhotosStmt.close();
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

