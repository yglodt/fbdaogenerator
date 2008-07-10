import java.util.HashMap;


public class DataFieldFirebird {
	private String name;
	private HashMap<String, String> typeMapping = new HashMap<String, String>();
	private String type;
	private String javaType;
//	private String javaGetter;
//	private String javaSetter;
	private String javaName;
	private int length;
	private int notNull;
	private boolean isInPK = false;

	public DataFieldFirebird(String name, String type, int length, int notNull) {
		// HashMap which makes the link between data types of Firebird and Java
		typeMapping.put("SHORT", "Integer");
		typeMapping.put("INT", "Integer");
		typeMapping.put("INTEGER", "Integer");
		typeMapping.put("INT64", "Integer");
		typeMapping.put("LONG", "Integer");
		typeMapping.put("DOUBLE", "double");
		typeMapping.put("FLOAT", "float");
		typeMapping.put("VARYING", "String");
		typeMapping.put("TEXT", "String");
		typeMapping.put("BLOB", "String");
		typeMapping.put("TIMESTAMP", "Date");
		this.setName(name);
		this.setType(type);
		this.setJavaType(typeMapping.get(type));
		this.setJavaName(name);
		this.setLength(length);
		this.setNotNull(notNull);
		this.setInPK(false);
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getNotNull() {
		return notNull;
	}

	public void setNotNull(int notNull) {
		this.notNull = notNull;
	}
/*
	public String getJavaName() {
		return javaName;
	}

	public void setJavaName(String javaName) {
		javaName = javaName.toLowerCase();
		this.javaName = javaName;
	}
*/
	public boolean isInPK() {
		return isInPK;
	}

	public void setInPK(boolean isInPK) {
		this.isInPK = isInPK;
	}
	
	public String getJavaName() {
		return javaName;
	}

	public void setJavaName(String javaName) {
		javaName = Helpers.underscoreSeparatedToCamelCase(javaName);
		String temp = JavaSpecific.replaceKeyWords(javaName);
		if (javaName.equals(temp)) {
			this.javaName = javaName;
		} else {
			this.javaName = temp;			
		}
	}

	public String getJavaGetter() {
		return " get"+this.getJavaName().substring(0,1).toUpperCase() + this.getJavaName().substring(1)+"()";
	}

	public String getJavaSetter() {
		return " set"+this.getJavaName().substring(0,1).toUpperCase() + this.getJavaName().substring(1)+"("+this.getJavaType()+" "+this.getJavaName()+")";
	}

	public String getPHPGetter() {
		return " get"+this.getJavaName().substring(0,1).toUpperCase() + this.getJavaName().substring(1)+"()";
	}

	public String getPHPSetter() {
		return " set"+this.getJavaName().substring(0,1).toUpperCase() + this.getJavaName().substring(1)+"($"+this.getJavaName()+")";
	}
}
