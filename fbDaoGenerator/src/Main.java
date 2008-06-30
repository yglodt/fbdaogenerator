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

		for (String table : tableList) {
			JavaSpecific.generateCode(table);
		}
	}
}
