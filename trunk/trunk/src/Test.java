
public class Test {
	public static void main(String[] args) {

		ImanContentDAO db = new ImanContentDAOFirebird();
		ImanContent record;
		/*
		ImanContent[] allRecords = db.getAll();
		for (ImanContent temp : allRecords) {
			System.out.println("1: "+temp.getId());
			System.out.println("2: "+temp.getCategory());
			System.out.println("3: "+temp.getTitle());
			System.out.println("4: "+temp.getContent());
			System.out.println("5: "+temp.getDateModified());
			System.out.println("6: "+temp.getHostModified());
			System.out.println("7: "+temp.getHidden());
			System.out.println("---------------------------------------");
		}
		
		record = db.get(10);
		System.out.println("1: "+record.getId());
		System.out.println("2: "+record.getCategory());
		System.out.println("3: "+record.getTitle());
		System.out.println("4: "+record.getContent());
		System.out.println("5: "+record.getDateModified());
		System.out.println("6: "+record.getHostModified());
		System.out.println("7: "+record.getHidden());

		record = new ImanContent();
		record.setTitle("insert from class");
		record.setDateModified((int)System.currentTimeMillis());
		record.setContent(System.getenv().toString());
		record.setHostModified("host");
		record.setHidden(0);
		db.insert(record);

		record = db.get(22);
		record.setTitle("update from class");
		record.setDateModified((int)System.currentTimeMillis());
		record.setContent(System.getenv().toString());
		db.update(record);

		record = new ImanContent();
		record.setId(18);
		db.delete(record);
		*/
	}
}
