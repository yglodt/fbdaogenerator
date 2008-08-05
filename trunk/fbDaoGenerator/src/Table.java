import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Table {
	private static Configuration config =  new Configuration();
	
	private String insertStatementFieldsList;
	private String insertStatementPlaceHolders;
	private String updateStatementFieldsList;
	private String pkWhereStatement;

	public Table() {
		super();
	}

	public ArrayList<DataFieldFirebird> getColumList(String table) {
		ArrayList<DataFieldFirebird> columnList = new ArrayList<DataFieldFirebird>();
		ArrayList<String> pkFields = new ArrayList<String>();
		
		// fetch colums that form the primary key
	    String sql = "select S.RDB$FIELD_NAME AS FIELDNAME "+
	    "from RDB$RELATION_CONSTRAINTS RC "+
	    "left join RDB$INDEX_SEGMENTS S on (S.RDB$INDEX_NAME = RC.RDB$INDEX_NAME) "+
	    "left join RDB$RELATION_FIELDS RF on (RF.RDB$FIELD_NAME = S.RDB$FIELD_NAME) "+
	    "left join RDB$FIELDS F on (F.RDB$FIELD_NAME = RF.RDB$FIELD_SOURCE) "+
	    "left join RDB$TYPES T on (T.RDB$TYPE = F.RDB$FIELD_TYPE) "+
	    "where RC.RDB$RELATION_NAME = ? "+
	    "and RF.RDB$RELATION_NAME = ? "+
	    "and T.RDB$FIELD_NAME = 'RDB$FIELD_TYPE' "+
	    "and RC.RDB$CONSTRAINT_TYPE = 'PRIMARY KEY' "+
	    "order by S.RDB$FIELD_POSITION";
		try {
			PreparedStatement pstmt = config.getDbConnection().prepareStatement(sql);
			pstmt.setString(1, table);
			pstmt.setString(2, table);
			ResultSet rst = pstmt.executeQuery();
			while(rst.next()) {
				pkFields.add(rst.getString(1).trim());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// fetch all columns
		sql = "select RF.RDB$FIELD_NAME, T.RDB$TYPE_NAME, "+
		"F.RDB$CHARACTER_LENGTH, RF.RDB$DEFAULT_VALUE, RF.RDB$NULL_FLAG "+
		"from RDB$RELATION_FIELDS RF "+
		"left join RDB$FIELDS F on (RF.RDB$FIELD_SOURCE = F.RDB$FIELD_NAME) "+
		"left join RDB$TYPES T on (F.RDB$FIELD_TYPE = T.RDB$TYPE) and (T.RDB$FIELD_NAME = 'RDB$FIELD_TYPE') "+
		"where RDB$RELATION_NAME = ? "+
		"and F.RDB$SYSTEM_FLAG = 0 "+
		"order by RDB$FIELD_POSITION ";
		String insertStatementFieldsList = "";
		String insertStatementPlaceHolders = "";
		String updateStatementFieldsList = "";
		String pkWhereStatement = " where ";
		try {
			PreparedStatement pstmt = config.getDbConnection().prepareStatement(sql);
			pstmt.setString(1, table);
			ResultSet rst = pstmt.executeQuery();
			while(rst.next()) {
				insertStatementFieldsList = insertStatementFieldsList + rst.getString("RDB$FIELD_NAME").trim() + ",";
				insertStatementPlaceHolders = insertStatementPlaceHolders + "?,";
				updateStatementFieldsList = updateStatementFieldsList + rst.getString("RDB$FIELD_NAME").trim() + "=?,";
				DataFieldFirebird dff = new DataFieldFirebird(rst.getString("RDB$FIELD_NAME").trim(), rst.getString("RDB$TYPE_NAME").trim(), rst.getInt("RDB$CHARACTER_LENGTH"), rst.getInt("RDB$NULL_FLAG"));
				if (pkFields.contains(rst.getString(1).trim())) {
					dff.setInPK(true);
					pkWhereStatement = pkWhereStatement + rst.getString(1).trim() + "=? and ";
				}
				columnList.add(dff);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		this.setInsertStatementFieldsList(insertStatementFieldsList.substring(0, insertStatementFieldsList.length()-1));
		this.setInsertStatementPlaceHolders(insertStatementPlaceHolders.substring(0, insertStatementPlaceHolders.length()-1));
		this.setUpdateStatementFieldsList(updateStatementFieldsList.substring(0, updateStatementFieldsList.length()-1));
		this.setPkWhereStatement(pkWhereStatement.substring(0, pkWhereStatement.length()-5));

		return columnList;
	}

	public String getInsertStatementFieldsList() {
		return insertStatementFieldsList;
	}

	private void setInsertStatementFieldsList(String insertStatementFieldsList) {
		this.insertStatementFieldsList = insertStatementFieldsList;
	}

	public String getInsertStatementPlaceHolders() {
		return insertStatementPlaceHolders;
	}

	private void setInsertStatementPlaceHolders(String insertStatementPlaceHolders) {
		this.insertStatementPlaceHolders = insertStatementPlaceHolders;
	}

	public String getUpdateStatementFieldsList() {
		return updateStatementFieldsList;
	}

	private void setUpdateStatementFieldsList(String updateStatementFieldsList) {
		this.updateStatementFieldsList = updateStatementFieldsList;
	}

	public String getPkWhereStatement() {
		return pkWhereStatement;
	}

	private void setPkWhereStatement(String pkWhereStatement) {
		this.pkWhereStatement = pkWhereStatement;
	}
}
