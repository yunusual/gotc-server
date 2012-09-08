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
 * Servlet that returns the list of locations for an event category.
 * @author Yunus Evren
 */
public class GetLocations extends HttpServlet {

	private PrintWriter out;
	private Connection conn;

	public void init(ServletConfig config)
	throws ServletException {

		super.init(config);
	}

	public void doGet(HttpServletRequest request,
			HttpServletResponse response)
	throws ServletException, IOException {

		String categoryId = request.getParameter("cat_id");
		
		out = response.getWriter();
		boolean result = false;
		result = getCategories(categoryId);
	}


	/**
	 * Gets all the locations for an event category and prints their details.
	 * @param categoryId - category id
	 * @return true if the location details are printed successfully
	 */
	private boolean getCategories(String categoryId) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");
			
			Statement getLocationsStmt = conn.createStatement();
			String getLocationsQuery = "SELECT * FROM location WHERE cat_id = " + categoryId;
			ResultSet getLocationsResult = getLocationsStmt.executeQuery(getLocationsQuery);
			int id = 0;
			String name = "";
			String desc = "";
			String photoThumb = "";
			String photoLarge = "";
			String latitude = "";
			String longitude = "";
			int score = 0;
			while(getLocationsResult.next()){
				id = getLocationsResult.getInt("loc_id");
				name = getLocationsResult.getString("loc_name");
				desc = getLocationsResult.getString("description");
				photoThumb = getLocationsResult.getString("photo_thumb");
				photoLarge = getLocationsResult.getString("photo_large");
				latitude = getLocationsResult.getString("latitude");
				longitude = getLocationsResult.getString("longitude");
				score = getLocationsResult.getInt("score");
				out.println(id+"|"+name+"|"+desc+"|"+photoThumb+"|"+photoLarge+"|"+latitude+"|"+longitude+"|"+score);
			}
			getLocationsStmt.close();
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

