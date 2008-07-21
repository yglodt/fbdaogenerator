import java.io.File;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {

		final Configuration config = new Configuration();
		Helpers.deleteDir(new File(config.getConfigFileParameter("javaOutputdir")));
		Helpers.deleteDir(new File(config.getConfigFileParameter("phpOutputdir")));

		int generateJavaCode = Integer.parseInt(config.getConfigFileParameter("generateJavaCode"));
		int generatePhpCode = Integer.parseInt(config.getConfigFileParameter("generatePhpCode"));

		for (String table : DataBase.getTableList()) {
			if (generateJavaCode == 1) JavaSpecific.generateCode(table);
			if (generatePhpCode == 1) PhpSpecific.generateCode(table);
		}

	}
}
