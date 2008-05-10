package tests;

import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Date;

public class imanContentDAOFirebird implements imanContentDAO {
	private Connection conn = null;

	public imanContentDAOFirebird() {
		try {
			Class.forName("org.firebirdsql.jdbc.FBDriver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			Properties connInfo = new Properties();
			connInfo.put("user", "SYSDBA");
			connInfo.put("password", "masterkey");
			connInfo.put("charSet", "ISO-8859-1");
			this.conn = DriverManager.getConnection("jdbc:firebirdsql:localhost:i-man", connInfo);
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
	}

	public imanContent get(int id) {
		ResultSet rst = null;
		PreparedStatement pstmt = null;
		String sql = "select * from IMAN_CONTENT where "+
		"ID = ?";
		imanContent record = new imanContent();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			rst = pstmt.executeQuery();
			while(rst.next()) {
				record.setId(rst.getInt(1));
				record.setCategory(rst.getInt(2));
				record.setTitle(rst.getString(3));
				record.setContent(rst.getString(4));
				record.setDateModified(rst.getInt(5));
				record.setHostModified(rst.getString(6));
				record.setHidden(rst.getInt(7));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return record;
	}

	public imanContent[] getAll() {
		ArrayList<imanContent> list = new ArrayList<imanContent>();
		ResultSet rst = null;
		PreparedStatement pstmt = null;
		String sql = "select * from IMAN_CONTENT";
		try {
			pstmt = conn.prepareStatement(sql);
			rst = pstmt.executeQuery();
			while(rst.next()) {
				imanContent record = new imanContent();
				record.setId(rst.getInt(1));
				record.setCategory(rst.getInt(2));
				record.setTitle(rst.getString(3));
				record.setContent(rst.getString(4));
				record.setDateModified(rst.getInt(5));
				record.setHostModified(rst.getString(6));
				record.setHidden(rst.getInt(7));
				list.add(record);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list.toArray(new imanContent[0]);
	}

	public void insert(imanContent record) {
		PreparedStatement pstmt = null;
		String stmt = "insert into IMAN_CONTENT ("+
		"ID"+
		") values ("+
		"?)";
		try {
			pstmt = conn.prepareStatement(stmt);
			pstmt.setInt(1, record.getId());
			int result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void update(imanContent record) {
		PreparedStatement pstmt = null;
		String stmt = "update IMAN_CONTENT set "+
		"TITLE = ? "+
		"where ID = ?";
		try {
			pstmt = conn.prepareStatement(stmt);
			pstmt.setString(1, record.getTitle());
			pstmt.setInt(2, record.getId());
			int result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void delete(imanContent record) {
		PreparedStatement pstmt = null;
		String stmt = "delete from IMAN_CONTENT where "+
		"ID = ?";
		try {
			pstmt = conn.prepareStatement(stmt);
			pstmt.setInt(1, record.getId());
			int result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
