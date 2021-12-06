import java.io.*;
import java.sql.*;
import java.util.*;

// Connect to CS410 Final Project Sandbox
public class JDBCConnect {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
		if (args.length<3){
			System.out.println("Usage DBConnectTest <yourportnumber> <sandbox password> <dbname>");
		}
		else{
			Connection con = null;
			Statement stmt = null, stmt2 = null;
			try
			{	
				int nRemotePort = Integer.parseInt(args[0]); // remote port number of your database
				String strDbPassword = args[1];                    // database login password
				String dbName = args[2];  
				
				/*
				 * STEP 1 and 2
				 * LOAD the Database DRIVER and obtain a CONNECTION
				 * 
				 * */
				Class.forName("com.mysql.cj.jdbc.Driver");
				System.out.println("jdbc:mysql://localhost:"+nRemotePort+"/test?verifyServerCertificate=false&useSSL=true");
				con = DriverManager.getConnection("jdbc:mysql://localhost:"+nRemotePort+"/test?verifyServerCertificate=false&useSSL=true&serverTimezone=UTC", "msandbox",
						strDbPassword);
				// Do something with the Connection
				System.out.println("Database [test db] connection succeeded!");
				System.out.println();
				
				// TODO: we can add functions for transactions here
				
			}
			catch( SQLException e )
			{
				System.out.println(e.getMessage());
				con.rollback(); // In case of any exception, we roll back to the database state we had before starting this transaction
			}
			// finally{
				
			// 	/*
			// 	 * STEP 5
			// 	 * CLOSE CONNECTION AND SSH SESSION
			// 	 * 
			// 	 * */
				
			// 	if(stmt!=null)
			// 		stmt.close();
				
			// 	if(stmt2!=null)
			// 		stmt2.close();
				
			// 	con.setAutoCommit(true); // restore dafault mode
			// 	con.close();
			// }

		}
	}

}

