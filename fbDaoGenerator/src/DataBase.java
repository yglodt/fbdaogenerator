import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DataBase {
	private static Configuration config =  new Configuration();

	public static ArrayList<String> getTableList() {
		ArrayList<String> tableList = new ArrayList<String>();
		String sql = "select RDB$RELATION_NAME from RDB$RELATIONS where RDB$SYSTEM_FLAG = 0 order by RDB$RELATION_NAME";
		try {
			PreparedStatement pstmt = config.getDbConnection().prepareStatement(sql);
			ResultSet rst = pstmt.executeQuery();
			while(rst.next()) {
				tableList.add(rst.getString(1).trim());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tableList;
	}

	public static String getSchemaVersion() {
		String version = "";
		String sql = config.getConfigFileParameter("schemaVersionSQL");
		if ((sql != null) && (sql != "")) {
			try {
				PreparedStatement pstmt = config.getDbConnection().prepareStatement(sql);
				ResultSet rst = pstmt.executeQuery();
				while(rst.next()) {
					version = rst.getString(1);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return version;
	}
}
