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
 * Servlet that returns the current valid Flickr API token to make Flickr calls.
 * Additionally returns the Flickr Group id for a given event category.
 * @author Yunus Evren
 */
public class GetFlickrToken extends HttpServlet {

	private final String TOKEN = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
	
	private PrintWriter out;
	private Connection conn;

	public void init(ServletConfig config)
	throws ServletException {

		super.init(config);
	}

	public void doGet(HttpServletRequest request,
			HttpServletResponse response)
	throws ServletException, IOException {

		String categoryId = request.getParameter("category_id");
		
		out = response.getWriter();
		
		boolean result = false;
		result = getGroupId(categoryId);
	}


	/**
	 * Gets the Flickr Group id from the database according to the input event category.
	 * Prints the Flickr Group id and the Flickr API token.
	 * @param categoryId - category id
	 * @return true if the Flickr Group id and the API token is successfully printed
	 */
	private boolean getGroupId(String categoryId) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");

			Statement getInvitationsStmt = conn.createStatement();
			String getInvitationsQuery = "SELECT group_id FROM category WHERE cat_id=" + categoryId;
			ResultSet getInvitationsResult = getInvitationsStmt.executeQuery(getInvitationsQuery);
			getInvitationsResult.next();
			String groupId = getInvitationsResult.getString("group_id");
			out.println(TOKEN+"|"+groupId);
			
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

