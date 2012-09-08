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
 * Servlet that adds a new photo into the database.
 * @author Yunus Evren
 */
public class AddNewPhoto extends HttpServlet {

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

		String photoId = request.getParameter("photo_id");
		String eventId = request.getParameter("event_id");
		String facebookId = request.getParameter("facebook_id");

		boolean addPhotoResult = false;
		addPhotoResult = addPhoto(photoId, eventId, facebookId);
		if(addPhotoResult){
			out.println("AddNewPhoto: OK");
		}
		else{
			out.println("AddNewPhoto: ERROR");
		}
	}


	/**
	 * Adds a new photo into the database.
	 * @param photoId - Flickr id of photo
	 * @param eventId - event id
	 * @param facebookId - Facebook id of the uploader
	 * @return true if photo entry is successfully inserted
	 */
	private boolean addPhoto(String photoId, String eventId, String facebookId) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");

			Statement insertPhotoStmt = conn.createStatement();
			String insertPhotoQuery = "INSERT INTO photo VALUES ('" + photoId + "', " + eventId + ", '" + facebookId + "')";
			insertPhotoStmt.executeUpdate(insertPhotoQuery);

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

