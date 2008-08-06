import java.sql.Date;

import com.example.dao.*;


public class Test {
	public static void main(String[] args) {
		Configuration config =  new Configuration();

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
