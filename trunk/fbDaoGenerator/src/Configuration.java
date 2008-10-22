import java.io.*;
import java.sql.*;
import java.util.*;

class Configuration {
	private Connection conn = null;
	private Properties configFile = null;

	public Configuration(String confFile) {
		this.configFile = new Properties();
		try {
			this.configFile.load(new FileInputStream(new File(confFile)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Configuration-file not found.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.conn = this.connect();
	}

	public Connection getDbConnection() {
		if (this.conn != null) {
			return this.conn;
		} else {
			return this.connect();
		}
	}

	public String getConfigFileParameter(String key) {
		return this.configFile.getProperty(key);
	}
	
	private Connection connect() {
		Connection conn = null;
		try {
			Class.forName("org.firebirdsql.jdbc.FBDriver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			Properties connInfo = new Properties();
			connInfo.put("user", this.getConfigFileParameter("dbuser"));
			connInfo.put("password", this.getConfigFileParameter("dbpass"));
			connInfo.put("charSet", this.getConfigFileParameter("dbcharset"));
			conn = DriverManager.getConnection ("jdbc:firebirdsql:"+this.getConfigFileParameter("dbalias"), connInfo);
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
		return conn;
	}
}
