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
 * Servlet that returns the scoreboard data
 * @author Yunus Evren
 */
public class GetScoreboard extends HttpServlet {

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
		result = getScoreboard();
	}


	/**
	 * Gets the current scoreboard and prints the users' names and scores in descending order.
	 * @return true if the scoreboard is printed successfully
	 */
	private boolean getScoreboard() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");
			
			Statement getScoresStmt = conn.createStatement();
			String getScoresQuery = "SELECT user_id, user_name, total_score FROM user ORDER BY total_score DESC";
			ResultSet getScoresResult = getScoresStmt.executeQuery(getScoresQuery);
			String userId = "";
			String userName = "";
			int totalScore = -1;
			while(getScoresResult.next()){
				userId = getScoresResult.getString("user_id");
				userName = getScoresResult.getString("user_name");
				totalScore = getScoresResult.getInt("total_score");
				out.println(userId+"|"+userName+"|"+totalScore);
			}
			getScoresStmt.close();
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

