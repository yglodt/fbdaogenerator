import java.io.File;

public class Main {

	public static void main(String[] args) {

		final Configuration config = new Configuration();
		Helpers.deleteDir(new File(config.getConfigFileParameter("javaOutputdir")));
		Helpers.deleteDir(new File(config.getConfigFileParameter("phpOutputdir")));
		new File(config.getConfigFileParameter("javaOutputdir")).mkdirs();
		new File(config.getConfigFileParameter("phpOutputdir")).mkdirs();

		int generateJavaCode = Integer.parseInt(config.getConfigFileParameter("generateJavaCode"));
		int generatePhpCode = Integer.parseInt(config.getConfigFileParameter("generatePhpCode"));

		for (String table : DataBase.getTableList()) {
			if (generateJavaCode == 1) JavaSpecific.generateCode(table);
			if (generatePhpCode == 1) PhpSpecific.generateCode(table);
		}

	}
}
