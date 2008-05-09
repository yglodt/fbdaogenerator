import java.io.*;
import java.sql.*;
import java.util.*;

class Configuration {
	private Connection conn = null;
	private Properties configFile = null;

	public Configuration() {
		this.configFile = new Properties();
		try {
			this.configFile.load(new FileInputStream(new File("fbdaogenerator.ini")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

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
			this.conn = DriverManager.getConnection ("jdbc:firebirdsql:"+this.getConfigFileParameter("dbalias"), connInfo);
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
	}

	public Connection getDbConnection() {
		// maybe check here if connection is valid, if not, the reconnect
		return this.conn;
	}

	public String getConfigFileParameter(String key) {
		return this.configFile.getProperty(key);
	}
}
