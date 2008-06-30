
import lu.sitasoftware.azur.dao.*;

public class Test {
	private static Configuration config =  new Configuration();
	public static void main(String[] args) {

		SimpleTableDAOFirebird dao = new SimpleTableDAOFirebird(config.getDbConnection());
		
		SimpleTable t = new SimpleTable();
		t.setId(9);
		t.setStr01("string "+t.getId());
		t.setTestint01(11);
		dao.insert(t);
	}
}
