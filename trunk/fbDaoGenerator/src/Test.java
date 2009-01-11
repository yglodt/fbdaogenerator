import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.firebirdsql.jdbc.FBDatabaseMetaData;
import org.firebirdsql.jdbc.FBResultSet;
import org.firebirdsql.jdbc.FBResultSetMetaData;

//import com.example.dao.*;
//import lu.sitasoftware.azur.dao.*;

public class Test {
	public static void main(String[] args) {
		/*
		Configuration config =  new Configuration("atest.ini");
		Connection conn = config.getDbConnection();

		try {
	    System.out.println("Got Connection.");
	    Statement st = conn.createStatement();
	    st.executeUpdate("create table survey (id int,name varchar(30));");
	    st.executeUpdate("insert into survey (id,name ) values (1,'nameValue');");

	    ResultSet rsColumns = null;
	    DatabaseMetaData meta;
		meta = conn.getMetaData();
	    rsColumns = meta.getColumns(null, null, "ATEST", null);
	    while (rsColumns.next()) {
	      String columnName = rsColumns.getString("COLUMN_NAME");
	      System.out.println("column name=" + columnName);
	      String columnType = rsColumns.getString("TYPE_NAME");
	      System.out.println("type:" + columnType);
	      int size = rsColumns.getInt("COLUMN_SIZE");
	      System.out.println("size:" + size);
	      int nullable = rsColumns.getInt("NULLABLE");
	      if (nullable == DatabaseMetaData.columnNullable) {
	        System.out.println("nullable true");
	      } else {
	        System.out.println("nullable false");
	      }
	      int position = rsColumns.getInt("ORDINAL_POSITION");
	      System.out.println("position:" + position);
	      
	    }

	    st.executeUpdate("drop table survey;");

	    st.close();
	    conn.close();
	    
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
		
		Configuration config =  new Configuration("atest.ini");
		Connection conn = config.getDbConnection();
		FBDatabaseMetaData db = null;
		ResultSet rst = null;
		ResultSet rst2 = null;
		
		//FBResultSet catalog = (FBResultSet) db.getCatalogs();
		
		
		try {
			db = (FBDatabaseMetaData) conn.getMetaData();
			rst = db.getColumns(null, null, "ATEST", "%");
			rst2 = db.getPrimaryKeys(null, "ATEST", null);
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//String sql = "select first 1 * from atest";
		try {
			//PreparedStatement pstmt = conn.prepareStatement(sql);
			//pstmt.setString(1, table);
			//ResultSet rst = pstmt.executeQuery();
			while(rst.next()) {
				/*
				 * table:
				TABLE_CAT
				TABLE_SCHEM
				TABLE_NAME
				COLUMN_NAME
				DATA_TYPE
				TYPE_NAME
				COLUMN_SIZE
				BUFFER_LENGTH
				DECIMAL_DIGITS
				NUM_PREC_RADIX
				NULLABLE
				REMARKS
				COLUMN_DEF
				SQL_DATA_TYPE
				SQL_DATETIME_SUB
				CHAR_OCTET_LENGTH
				ORDINAL_POSITION
				IS_NULLABLE
				
				pk: 
				TABLE_CAT
				TABLE_SCHEM
				TABLE_NAME
				COLUMN_NAME
				KEY_SEQ
				PK_NAME

					System.out.println(rst.getString("DATA_TYPE"));
					System.out.println(rst.getString("TYPE_NAME"));
					System.out.println(rst.getString("COLUMN_NAME"));
					System.out.println(rst.getString("SQL_DATA_TYPE"));
					System.out.println(rst.getString("COLUMN_DEF"));

				 */
				FBResultSetMetaData md = (FBResultSetMetaData) rst.getMetaData();
				//System.out.println("count: "+md.getColumnCount());
				
				for (int i = 1; i <= md.getColumnCount(); i++) {
					String name = md.getColumnName(i);
					String type = md.getColumnClassName(i);
					int length = md.getColumnDisplaySize(i);
					int nullable = 1;
					System.out.println(i+": "+name+": "+rst.getString("TYPE_NAME"));
				}
			

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}


		String noDossier = "690";
		//ArticleDAOFirebird dao = new ArticleDAOFirebird(config.getDbConnection());
		//Article[] a = aDao.getAll("where NO_DOSSIER = "+Application.getConfigFileParameter("noDossier")+" and ACTIF_INTERVENTION = 'T' and ACTIF = 'T' and LIBELLE1 <> '' and LIBELLE1 is not NULL order by SIMPLE(LIBELLE1)"); //  TODO: $tempuserlimit
		//Article[] a = dao.getAll("where NO_DOSSIER = '"+noDossier+"' and ACTIF_INTERVENTION = 'T' and ACTIF = 'T' and LIBELLE1 <> '' and LIBELLE1 is not NULL order by SIMPLE(LIBELLE1)"); //  TODO: $tempuserlimit

		//System.out.println(a.length);
		
		/*
		AtestDAOFirebird dao = new AtestDAOFirebird(config.getDbConnection());
		Atest t = dao.get(3);
		*/
/*		t.setId(null);
		t.setFloat01(null);
		t.setNu01(null);
		t.setTs01(new java.sql.Timestamp(System.currentTimeMillis()));
		dao.update(t);
		*/
		
		/*
		SimpleTableDAOFirebird dao = new SimpleTableDAOFirebird(config.getDbConnection());

		SimpleTable t = dao.get(10);
		System.out.println(t.getTestint01());
		t.setDateTest(new java.sql.Timestamp(System.currentTimeMillis()));
		t.setStr01(String.valueOf(System.currentTimeMillis()));
		dao.update(t);
		SimpleTable tt = new SimpleTable();
		tt.setId(111);
		dao.delete(tt);

		SimpleTable ttt = new SimpleTable();
		ttt.setId(111);
		ttt.setDateTest(new java.sql.Timestamp(System.currentTimeMillis()));
		ttt.setStr01(String.valueOf(System.currentTimeMillis()));
		dao.insert(ttt);
		
		//t.setTestint01(null);
		//dao.insert(t);
		 */
/*

		TableTestDAOFirebird dao = new TableTestDAOFirebird(config.getDbConnection());
		TableTest t = dao.get(2);
		t.setStringField("");
//		t.setStringField("NEW STRING");
//		t.clearModifiedFields();
//		t.setStringField("NEW STRING2");
		dao.update(t);
*/
		
	}
}
