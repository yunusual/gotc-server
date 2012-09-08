package com.cribcaged.getoffthecouch.server;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gcm.server.*;

/**
 * Servlet that sends an event confirmation notification to a specific user.
 * @author Yunus Evren
 */
public class SendConfirmation extends HttpServlet {

	private final static String API_KEY = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

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
		String userId = request.getParameter("user_id");
		sendConfirmation(userId);
	}


	/**
	 * Gets the device id of the user from the database.
	 * Forms a notification message and sends it using GCM.
	 * @param userId - Facebook id of the user that will receive the notification
	 * @return true if the notification is successfully sent
	 */
	private boolean sendConfirmation(String userId) {
		String regId = "";
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/getoffthecouch", "XXXXXX", "XXXXXX");
			
			int count = 0;
			Statement checkRegStatusStmt = conn.createStatement();
			String checkRegStatusQuery = "SELECT COUNT(*) FROM user WHERE user_id='" + userId + "' AND reg_status=1";
			ResultSet checkRegStatusResult = checkRegStatusStmt.executeQuery(checkRegStatusQuery);
			checkRegStatusResult.next();
			count = checkRegStatusResult.getInt(1);
			
			if(count==0){
				out.println("SendMessage: ERROR");
				out.println("User is not subscribed. Notification was not sent");
				return false;
			}
			
			Statement getRegIdStmt = conn.createStatement();
			String getRegIdQuery = "SELECT reg_id FROM user WHERE user_id='" + userId + "' AND reg_status=1";
			ResultSet getRegIdResult = getRegIdStmt.executeQuery(getRegIdQuery);
			getRegIdResult.next();
			regId = getRegIdResult.getString(1);
			
			Sender sender = new Sender(API_KEY);
			Message message = new Message.Builder().addData("user", "EventConfirmed").build();
			ArrayList<String> devices = new ArrayList<String>();
			devices.add(regId);
			
			MulticastResult result = sender.send(message, devices, 5);
			out.println("SendMessage: OK");
			List<Result> results = result.getResults();
			for(Result r : results){
				if (r.getMessageId() != null) {
					String canonicalRegId = r.getCanonicalRegistrationId();
					if (canonicalRegId != null) {
						// same device has more than on registration ID: update database
						out.println("same device has more than on registration ID: update database");
						
						Statement updateStmt = conn.createStatement();
						String updateQuery = "UPDATE user SET reg_id='" + regId + "' WHERE user_id = '" + userId + "'";
						updateStmt.executeUpdate(updateQuery);
					}
				}
				else {
					String error = r.getErrorCodeName();
					if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
						Statement unregistrationStmt = conn.createStatement();
						String unregistrationQuery = "UPDATE user SET reg_status=0 WHERE user_id='" + userId  + "'";
						unregistrationStmt.executeUpdate(unregistrationQuery);
					}
					out.println("error: " + error);
				}
			}
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			out.println("SendMessage: ERROR");
			e.printStackTrace(out);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			out.println("SendMessage: ERROR");
			e.printStackTrace(out);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			out.println("SendMessage: ERROR");
			e.printStackTrace(out);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			out.println("SendMessage: ERROR");
			e.printStackTrace(out);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			out.println("SendMessage: ERROR");
			e.printStackTrace(out);
		}
		return false;
	}


	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
	throws ServletException, IOException {

	}

	public void destroy() {
	}
}

