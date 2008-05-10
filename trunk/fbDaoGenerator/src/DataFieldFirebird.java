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
		typeMapping.put("SHORT", "int");
		typeMapping.put("LONG", "int");
		typeMapping.put("DOUBLE", "double");
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
	
	public static String underscoreSeparatedToCamelCase(String name) {
		// copied from: http://www.mail-archive.com/dev@commons.apache.org/msg03457.html
		name = name.toLowerCase();
        StringBuilder s = new StringBuilder();
        if (name == null) {
                return "";
        }
        int length = name.length();
        boolean upperCase = false;

        for (int i = 0; i < length; i++) {
                char ch = name.charAt(i);
                if (ch == '_') {
                        upperCase = true;
                } else if (upperCase) {
                        s.append(Character.toUpperCase(ch));
                        upperCase = false;
                } else {
                        s.append(ch);
                }
        }
        return s.toString();
	}

	public String getJavaName() {
		return javaName;
	}

	public void setJavaName(String javaName) {
		javaName = underscoreSeparatedToCamelCase(javaName);
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
}
