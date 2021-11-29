import java.io.*;
import java.sql.*;
import java.util.*;

// Connect to CS410 Final Project Sandbox
public class JDBCConnect {

	public static Connection makeConnection() {
		Connection con = null;
		try
		{	
			
			/*
				* STEP 1 and 2
				* LOAD the Database DRIVER and obtain a CONNECTION
				* 
				* */
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("jdbc:mysql://b4869ae9f778f4:37791160@us-cdbr-east-04.cleardb.com/heroku_b378fabbe121395?reconnect=true");
			con = DriverManager.getConnection("jdbc:mysql://b4869ae9f778f4:37791160@us-cdbr-east-04.cleardb.com/heroku_b378fabbe121395?reconnect=true", "b4869ae9f778f4",
					"37791160");
			// Do something with the Connection
			System.out.println("Connection succeeded!");
			System.out.println();
			
				
		}
		catch( ClassNotFoundException e )
		{
			System.out.println(e.getMessage());
		}
		catch ( SQLException e) {
			System.out.println(e.getMessage());
		}
		return con;
	}

	// TODO: we can write code for queries in this file and pass in connection

	// TODO: remove once confirm you can get connected. I had to download the mysql-connector-java jar file and add it to my PATH
	public static void main(String[] args) {
		try {

			Connection conn = makeConnection();
			

			conn.close();
			System.out.println();
			System.out.println("Database [test db] connection closed");
			System.out.println();
		} catch (Exception ex) {
			// handle the error
			System.err.println(ex);
		}
	}
		

}
