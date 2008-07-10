import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {

		for (String s : args) {
			System.out.println("Arg: " + s);
		}

		ArrayList<String> tableList = DataBase.getTableList();
		// TODO: delete dir before starting at all

		// String[] sourceFilesToCompile = {};
		// int sourceFileCounter = 0;

		final Configuration config = new Configuration();
		int generateJavaCode = Integer.parseInt(config.getConfigFileParameter("generateJavaCode"));
		int generatePhpCode = Integer.parseInt(config.getConfigFileParameter("generatePhpCode"));
		
		for (String table : tableList) {
			System.out.println(table);
			if (generateJavaCode == 1) JavaSpecific.generateCode(table);
			if (generatePhpCode == 1) PhpSpecific.generateCode(table);
		}
	}
}
