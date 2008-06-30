
public class Helpers {

	public static String underscoreSeparatedToCamelCase(String name) {
		// copied from:
		// http://www.mail-archive.com/dev@commons.apache.org/msg03457.html
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

}
