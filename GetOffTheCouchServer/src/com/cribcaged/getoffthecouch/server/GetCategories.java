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
 * Servlet that returns the list of categories from the database
 * @author Yunus Evren
 */
public class GetCategories extends HttpServlet {

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
		boolean result = false;
		result = getCategories();
	}


	/**
	 * Gets the event categories from the database and prints their details.
	 * @return true if the categories are successfully printed
	 */
	private boolean getCategories() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");
			
			Statement getCategoriesStmt = conn.createStatement();
			String getCategoriesQuery = "SELECT * FROM category";
			ResultSet getCategoriesResult = getCategoriesStmt.executeQuery(getCategoriesQuery);
			String categoryId = "";
			String categoryName = "";
			while(getCategoriesResult.next()){
				categoryId = getCategoriesResult.getString("cat_id");
				categoryName = getCategoriesResult.getString("name");
				out.println(categoryId+"|"+categoryName);
			}
			getCategoriesStmt.close();
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

