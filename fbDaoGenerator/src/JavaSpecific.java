import java.util.ArrayList;
import java.util.Arrays;

public class JavaSpecific {
	/**
	 * @param keyword
	 * @return
	 */
	public static String replaceKeyWords(String keyword) {
		String[] reservedNames = {
		"abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized",
		"boolean", "do", "if", "private", "this", "break", "double", "implements", "protected", "throw",
		"byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient",
		"catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void",
		"class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while"				
		};
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(reservedNames));
		if (list.contains(keyword)) {
			return keyword.substring(0,1).toUpperCase() + keyword.substring(1);
		} else {
			return keyword;		
		}
	}
	
	public static String createPreparedStatementSetter(String javaType, int position, String javaName) {
		String cast = "";
		if (javaType.equals("Date")) {
			javaType = "Timestamp";
			cast = "(java.sql.Timestamp) ";
		} else if (javaType.equals("Integer")) {
			return "setInt("+position+", "+cast+javaName+");";
		} else {
			javaType = javaType.substring(0,1).toUpperCase() + javaType.substring(1);
		}
		return "set"+javaType+"("+position+", "+cast+javaName+");";
	}
	
	public static String createResultSetGetter(String javaType, int position) {
		if (javaType.equals("Date")) {
			javaType = "Timestamp";
		} else if (javaType.equals("Integer")) {
			javaType = "Int";
		} else {
			javaType = javaType.substring(0,1).toUpperCase() + javaType.substring(1);
		}
		return "get"+javaType+"("+position+")";
	}
}
