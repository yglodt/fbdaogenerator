import java.io.File;
 
public class Main {
	public static Configuration config = null;

	public static void main(String[] args) {
		config = new Configuration(args[0]);
		Helpers.deleteDir(new File(config.getConfigFileParameter("javaOutputdir")));
		Helpers.deleteDir(new File(config.getConfigFileParameter("phpOutputdir")));
		new File(config.getConfigFileParameter("javaOutputdir")).mkdirs();
		new File(config.getConfigFileParameter("phpOutputdir")).mkdirs();

		int generateJavaCode = Integer.parseInt(config.getConfigFileParameter("generateJavaCode"));
		int generatePhpCode = Integer.parseInt(config.getConfigFileParameter("generatePhpCode"));
		int generateOnlyHibernateAnnotatedPojos = Integer.parseInt(config.getConfigFileParameter("generateOnlyHibernateAnnotatedPojos"));
		
		for (String table : DataBase.getTableList()) {
			if (generateJavaCode == 1) {
				JavaSpecific.generateCode(table, 0);
			}
			if (generateOnlyHibernateAnnotatedPojos == 1 ) {
				JavaSpecific.generateCode(table, 1);
			}
			if (generatePhpCode == 1) {
				PhpSpecific.generateCode(table);
			}
		}

		if (generateJavaCode == 1) {
			System.out.println("Compiling classes and creating jar file...");
			String schemaVersion = DataBase.getSchemaVersion();
			JarBuilder jb = new JarBuilder(new File(config.getConfigFileParameter("javaOutputdir")), new File(config.getConfigFileParameter("jarFilename").replace("$V", schemaVersion)));
			jb.compileAndBuildJar();
		}
	}
}
