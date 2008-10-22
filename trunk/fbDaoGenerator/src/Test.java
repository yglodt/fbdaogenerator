import java.sql.Date;

import com.example.dao.*;
import lu.sitasoftware.azur.dao.*;

public class Test {
	public static void main(String[] args) {
		//Configuration config =  new Configuration();

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
