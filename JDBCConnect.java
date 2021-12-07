import java.sql.*;

// Connect to CS410 Final Project Sandbox
// used in DBShell.java
public class JDBCConnect {

	public static Connection establishConnection(int port, String pwd) throws ClassNotFoundException{
		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("jdbc:mysql://localhost:"+port+"/test?verifyServerCertificate=false&useSSL=true");
			con = DriverManager.getConnection("jdbc:mysql://localhost:"+port+"/test?verifyServerCertificate=false&useSSL=true&serverTimezone=UTC", "msandbox",
					pwd);
		} catch( SQLException e )
		{
			System.out.println(e.getMessage());
		}
		return con;
	}
}

