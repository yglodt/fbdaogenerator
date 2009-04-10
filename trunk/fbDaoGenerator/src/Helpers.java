import java.io.File;

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

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// The directory is now empty so delete it
		return dir.delete();
	}

}
