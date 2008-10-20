import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

public class JavaSpecific {

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
		//record.setRemiseClient( (Integer) rst.getObject(20));
		String cast = "";
		if (javaType.equals("Date")) {
			javaType = "Timestamp";
			cast = "(java.sql.Timestamp) ";
		} else if (javaType.equals("Integer")) {
			return "setObject("+position+", "+cast+javaName+");";
		} else if (javaType.equals("Float")) {
			return "setObject("+position+", "+cast+javaName+");";
		} else if (javaType.equals("Double")) {
			return "setObject("+position+", "+cast+javaName+");";
		} else {
			javaType = javaType.substring(0,1).toUpperCase() + javaType.substring(1);
		}
		return "set"+javaType+"("+position+", "+cast+javaName+");";
	}
	
	public static String createResultSetGetter(String var, String javaType, int position) {
		// creates rst.getXXXX(1);
		String cast = "";
		if (javaType.equals("Date")) {
			javaType = "Timestamp";
		} else if (javaType.equals("Integer")) {
			cast = " (Integer) ";
			javaType = "Object";
		} else if (javaType.equals("Float")) {
			// uncommenting this made code for vpn2 database be ok again, since it removed an invalid cast from double to float 
			//cast = " (Float) ";
			//javaType = "Object";
		} else if (javaType.equals("Double")) {
			cast = " (Double) ";
			javaType = "Object";
		} else {
			javaType = javaType.substring(0,1).toUpperCase() + javaType.substring(1);
		}
		return cast+var+".get"+javaType+"("+position+")";
	}
	
	public static void generateCode(String table) {
		String outPutDir = Main.config.getConfigFileParameter("javaOutputdir");
		outPutDir = outPutDir + File.separator + Main.config.getConfigFileParameter("javaPackage").replaceAll("\\.", System.getProperty("file.separator")) + System.getProperty("file.separator");
		new File(outPutDir).mkdirs();
		ArrayList<String> sourceFilesToCompile = new ArrayList<String>();

		System.out.println("Generating Java code for table: " + table);
		ArrayList<DataFieldFirebird> columnList = new ArrayList<DataFieldFirebird>();
		PrintStream classFile;
		PrintStream daoFile;
		PrintStream daoImpFile;
		FileOutputStream classFileHandle = null;
		FileOutputStream daoFileHandle = null;
		FileOutputStream daoImpFileHandle = null;
		String classFileBuffer = "";
		String daoFileBuffer = "";
		String daoImpFileBuffer = "";
		String tableJavaName = Helpers.underscoreSeparatedToCamelCase(table);
		tableJavaName = tableJavaName.substring(0, 1).toUpperCase() + tableJavaName.substring(1);


		try {
			classFileHandle = new FileOutputStream(outPutDir
					+ tableJavaName + ".java");
			daoFileHandle = new FileOutputStream(outPutDir + tableJavaName
					+ "DAO.java");
			daoImpFileHandle = new FileOutputStream(outPutDir
					+ tableJavaName + "DAOFirebird.java");
			sourceFilesToCompile.add(outPutDir + tableJavaName + ".java");
			sourceFilesToCompile
					.add(outPutDir + tableJavaName + "DAO.java");
			sourceFilesToCompile.add(outPutDir + tableJavaName
					+ "DAOFirebird.java");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Table t = new Table();
		columnList = t.getColumList(table);
		String insertStatementFieldsList = t.getInsertStatementFieldsList();
		String insertStatementPlaceHolders = t.getInsertStatementPlaceHolders();
		String updateStatementFieldsList = t.getUpdateStatementFieldsList();
		String pkWhereStatement = t.getPkWhereStatement();

		// create java class with getters and setters
		classFile = new PrintStream(classFileHandle);
		classFile.println("package "
				+ Main.config.getConfigFileParameter("javaPackage") + ";");
		classFile.println();
		classFile.println("import java.util.Date;");
		classFile.println();
		classFile.println("public class " + tableJavaName + " {");
		for (DataFieldFirebird column : columnList) {
			classFile.println();
			classFile.println("\tprivate " + column.getJavaType() + " "
					+ column.getJavaName() + ";");
			classFile.println("\tpublic " + column.getJavaType()
					+ column.getJavaGetter() + " {");
			classFile.println("\t\treturn " + column.getJavaName() + ";");
			classFile.println("\t}");
			classFile.println();
			classFile.println("\tpublic void" + column.getJavaSetter()
					+ " {");
			classFile.println("\t\tthis." + column.getJavaName() + " = "
					+ column.getJavaName() + ";");
			classFile.println("\t}");
		}
		classFile.println("}");
		classFile.close();

		// create DAO java interface
		daoFile = new PrintStream(daoFileHandle);
		daoFile.println("package "
				+ Main.config.getConfigFileParameter("javaPackage") + ";");
		daoFile.println();
		daoFile.println("import java.util.Date;");
		daoFile.println();
		daoFile.println("public interface " + tableJavaName + "DAO {");
		daoFile.println();
		daoFile.print("\tpublic " + tableJavaName + " get(");
		String getterParams = "";
		for (DataFieldFirebird column : columnList) {
			// String column.getJavaName() =
			// underscoreSeparatedToCamelCase(column.getName());
			if (column.isInPK()) {
				getterParams = getterParams + column.getJavaType() + " "
						+ column.getJavaName() + ", ";
			}
		}
		if (!getterParams.equals("")) {
			getterParams = getterParams.substring(0,
					(getterParams.length() - 2));
			daoFile.print(getterParams);
		}
		daoFile.println(");");
		daoFile.println();
		daoFile.println("\tpublic " + tableJavaName + "[] getAll();");
		daoFile.println();
		daoFile.println("\tpublic " + tableJavaName
				+ "[] getAll(String clause);");
		daoFile.println();
		daoFile.println("\tpublic void insert(" + tableJavaName
				+ " record);");
		daoFile.println();
		daoFile.println("\tpublic void update(" + tableJavaName
				+ " record);");
		daoFile.println();
		daoFile.println("\tpublic void delete(" + tableJavaName
				+ " record);");
		daoFile.println("}");
		daoFile.close();

		// create DAO implementation
		daoImpFile = new PrintStream(daoImpFileHandle);
		daoImpFile.println("package "
				+ Main.config.getConfigFileParameter("javaPackage") + ";");
		daoImpFile.println();
		daoImpFile.println("import java.sql.*;");
		daoImpFile.println("import java.util.ArrayList;");
		daoImpFile.println("import java.util.Properties;");
		daoImpFile.println("import java.util.Date;");
		daoImpFile.println();
		daoImpFile.println("public class " + tableJavaName
				+ "DAOFirebird implements " + tableJavaName + "DAO {");
		daoImpFile.println("\tprivate Connection conn = null;");
		daoImpFile.println();
		daoImpFile.println("\tpublic " + tableJavaName
				+ "DAOFirebird(Connection conn) {");
		daoImpFile.println("\t\tthis.conn = conn;");
		daoImpFile.println("\t}");
		daoImpFile.println();

		// get(PK) method
		daoImpFile.print("\tpublic " + tableJavaName + " get(");
		getterParams = "";
		for (DataFieldFirebird column : columnList) {
			if (column.isInPK()) {
				getterParams = getterParams + column.getJavaType() + " "
						+ column.getJavaName() + ", ";
			}
		}
		if (!getterParams.equals("")) {
			getterParams = getterParams.substring(0,
					(getterParams.length() - 2));
		}
		// make list of columns to fetch, instead of "select *"
		String selectColumns = "";
		for (DataFieldFirebird column : columnList) {
			selectColumns = selectColumns + column.getName() + ",";
		}
		selectColumns = selectColumns.substring(0,
				(selectColumns.length() - 1));
		// System.out.println(selectValues);
		daoImpFile.print(getterParams);
		daoImpFile.println(") {");
		daoImpFile.println("\t\tResultSet rst = null;");
		daoImpFile.println("\t\tPreparedStatement pstmt = null;");
		daoImpFile.println("\t\tString sql = \"select " + selectColumns
				+ " from " + table + " where \"+");
		// build the "where" clause for the primary key. Caution: this
		// clause is also used in the update() and delete() methods!
		String whereClause = "";
		for (DataFieldFirebird column : columnList) {
			if (column.isInPK()) {
				whereClause = whereClause + column.getName() + " = ? and ";
			}
		}
		if (!whereClause.equals("")) {
			whereClause = whereClause.substring(0,
					(whereClause.length() - 5));
		}
		daoImpFile.println("\t\t\"" + whereClause + "\";");
		daoImpFile.println("\t\t" + tableJavaName + " record = new "
				+ tableJavaName + "();");
		daoImpFile.println("\t\ttry {");
		daoImpFile.println("\t\t\tpstmt = conn.prepareStatement(sql);");
		int paramCount = 1;
		for (DataFieldFirebird column : columnList) {
			if (column.isInPK()) {
				daoImpFile.println("\t\t\tpstmt."
						+ JavaSpecific.createPreparedStatementSetter(column
								.getJavaType(), paramCount, column
								.getJavaName()));
				paramCount++;
			}
		}
		daoImpFile.println("\t\t\trst = pstmt.executeQuery();");
		daoImpFile.println("\t\t\twhile(rst.next()) {");
		int columnCount = 1;
		for (DataFieldFirebird column : columnList) {
			daoImpFile.println("\t\t\t\trecord.set"
					+ column.getJavaName().substring(0, 1).toUpperCase()
					+ column.getJavaName().substring(1)
					+ "("
					+ JavaSpecific.createResultSetGetter("rst", column
							.getJavaType(), columnCount) + ");");
			columnCount++;
		}
		daoImpFile.println("\t\t\t}");
		daoImpFile.println("\t\t} catch (SQLException e) {");
		daoImpFile.println("\t\t\te.printStackTrace();");
		daoImpFile.println("\t\t}");
		daoImpFile.println("\t\treturn record;");
		daoImpFile.println("\t}");
		daoImpFile.println();

		// getAll() method
		daoImpFile.println("\tpublic " + tableJavaName + "[] getAll() {");
		daoImpFile.println("\t\tArrayList<" + tableJavaName
				+ "> list = new ArrayList<" + tableJavaName + ">();");
		daoImpFile.println("\t\tResultSet rst = null;");
		daoImpFile.println("\t\tPreparedStatement pstmt = null;");
		daoImpFile.println("\t\tString sql = \"select " + selectColumns
				+ " from " + table + "\";");
		daoImpFile.println("\t\ttry {");
		daoImpFile.println("\t\t\tpstmt = conn.prepareStatement(sql);");
		daoImpFile.println("\t\t\trst = pstmt.executeQuery();");
		daoImpFile.println("\t\t\twhile(rst.next()) {");
		daoImpFile.println("\t\t\t\t" + tableJavaName + " record = new "
				+ tableJavaName + "();");
		columnCount = 1;
		for (DataFieldFirebird column : columnList) {
			daoImpFile.println("\t\t\t\trecord.set"
					+ column.getJavaName().substring(0, 1).toUpperCase()
					+ column.getJavaName().substring(1)
					+ "("
					+ JavaSpecific.createResultSetGetter("rst", column
							.getJavaType(), columnCount) + ");");
			columnCount++;
		}
		daoImpFile.println("\t\t\t\tlist.add(record);");
		daoImpFile.println("\t\t\t}");
		daoImpFile.println("\t\t} catch (SQLException e) {");
		daoImpFile.println("\t\t\te.printStackTrace();");
		daoImpFile.println("\t\t}");
		daoImpFile.println("\t\treturn list.toArray(new " + tableJavaName
				+ "[0]);");
		daoImpFile.println("\t}");
		daoImpFile.println();

		// getAll(String clause) method
		daoImpFile.println("\tpublic " + tableJavaName
				+ "[] getAll(String clause) {");
		daoImpFile.println("\t\tArrayList<" + tableJavaName
				+ "> list = new ArrayList<" + tableJavaName + ">();");
		daoImpFile.println("\t\tResultSet rst = null;");
		daoImpFile.println("\t\tPreparedStatement pstmt = null;");
		daoImpFile.println("\t\tString sql = \"select " + selectColumns
				+ " from " + table + " \" + clause;");
		daoImpFile.println("\t\ttry {");
		daoImpFile.println("\t\t\tpstmt = conn.prepareStatement(sql);");
		daoImpFile.println("\t\t\trst = pstmt.executeQuery();");
		daoImpFile.println("\t\t\twhile(rst.next()) {");
		daoImpFile.println("\t\t\t\t" + tableJavaName + " record = new "
				+ tableJavaName + "();");
		columnCount = 1;
		for (DataFieldFirebird column : columnList) {
			daoImpFile.println("\t\t\t\trecord.set"
					+ column.getJavaName().substring(0, 1).toUpperCase()
					+ column.getJavaName().substring(1)
					+ "("
					+ JavaSpecific.createResultSetGetter("rst", column
							.getJavaType(), columnCount) + ");");
			columnCount++;
		}
		daoImpFile.println("\t\t\t\tlist.add(record);");
		daoImpFile.println("\t\t\t}");
		daoImpFile.println("\t\t} catch (SQLException e) {");
		daoImpFile.println("\t\t\te.printStackTrace();");
		daoImpFile.println("\t\t}");
		daoImpFile.println("\t\treturn list.toArray(new " + tableJavaName
				+ "[0]);");
		daoImpFile.println("\t}");
		daoImpFile.println();

		// insert() method
		daoImpFile.println("\tpublic void insert(" + tableJavaName
				+ " record) {");
		daoImpFile.println("\t\tPreparedStatement pstmt = null;");
		daoImpFile.println("\t\tString stmt = \"insert into " + table
				+ " \"+");
		String insertFields = "";
		String insertPlaceHolders = "";
		for (DataFieldFirebird column : columnList) {
			insertFields = insertFields + column.getName() + ", ";
			insertPlaceHolders = insertPlaceHolders + "?, ";
		}
		insertFields = insertFields.substring(0,
				(insertFields.length() - 2));
		insertPlaceHolders = insertPlaceHolders.substring(0,
				(insertPlaceHolders.length() - 2));
		daoImpFile.println("\t\t\"(" + insertFields + ") \"+ ");
		daoImpFile.println("\t\t\"values (" + insertPlaceHolders + ")\";");
		daoImpFile.println("\t\ttry {");
		daoImpFile.println("\t\t\tpstmt = conn.prepareStatement(stmt);");
		paramCount = 1;
		for (DataFieldFirebird column : columnList) {
			daoImpFile.println("\t\t\tpstmt."
					+ JavaSpecific.createPreparedStatementSetter(column
							.getJavaType(), paramCount, "record.get"
							+ column.getJavaName().substring(0, 1)
									.toUpperCase()
							+ column.getJavaName().substring(1) + "()"));
			paramCount++;
		}
		daoImpFile.println("\t\t\tint result = pstmt.executeUpdate();");
		daoImpFile.println("\t\t} catch (SQLException e) {");
		daoImpFile.println("\t\t\te.printStackTrace();");
		daoImpFile.println("\t\t}");
		daoImpFile.println("\t}");
		daoImpFile.println();

		// update() method
		daoImpFile.println("\tpublic void update(" + tableJavaName
				+ " record) {");
		daoImpFile.println("\t\tPreparedStatement pstmt = null;");
		daoImpFile.println("\t\tString stmt = \"update " + table
				+ " set \"+");
		String updateValues = "";
		for (DataFieldFirebird column : columnList) {
			updateValues = updateValues + column.getName() + " = ?, ";
		}
		daoImpFile.println("\t\t\""
				+ updateValues.substring(0, (updateValues.length() - 2))
				+ " where \"+");
		daoImpFile.println("\t\t\"" + whereClause + "\";");

		daoImpFile.println("\t\ttry {");
		daoImpFile.println("\t\t\tpstmt = conn.prepareStatement(stmt);");
		paramCount = 1;
		for (DataFieldFirebird column : columnList) {
			daoImpFile.println("\t\t\tpstmt."
					+ JavaSpecific.createPreparedStatementSetter(column
							.getJavaType(), paramCount, "record.get"
							+ column.getJavaName().substring(0, 1)
									.toUpperCase()
							+ column.getJavaName().substring(1) + "()"));
			paramCount++;
		}
		for (DataFieldFirebird column : columnList) {
			if (column.isInPK()) {
				daoImpFile
						.println("\t\t\tpstmt."
								+ JavaSpecific
										.createPreparedStatementSetter(
												column.getJavaType(),
												paramCount,
												"record.get"
														+ column
																.getJavaName()
																.substring(
																		0,
																		1)
																.toUpperCase()
														+ column
																.getJavaName()
																.substring(
																		1)
														+ "()"));
				paramCount++;
			}
		}
		daoImpFile.println("\t\t\tint result = pstmt.executeUpdate();");
		daoImpFile.println("\t\t} catch (SQLException e) {");
		daoImpFile.println("\t\t\te.printStackTrace();");
		daoImpFile.println("\t\t}");
		daoImpFile.println("\t}");
		daoImpFile.println();

		// delete() method
		daoImpFile.println("\tpublic void delete(" + tableJavaName
				+ " record) {");
		daoImpFile.println("\t\tPreparedStatement pstmt = null;");
		daoImpFile.println("\t\tString stmt = \"delete from " + table
				+ " where \"+");
		daoImpFile.println("\t\t\"" + whereClause + "\";");
		daoImpFile.println("\t\ttry {");
		daoImpFile.println("\t\t\tpstmt = conn.prepareStatement(stmt);");
		paramCount = 1;
		for (DataFieldFirebird column : columnList) {
			if (column.isInPK()) {
				daoImpFile
						.println("\t\t\tpstmt."
								+ JavaSpecific
										.createPreparedStatementSetter(
												column.getJavaType(),
												paramCount,
												"record.get"
														+ column
																.getJavaName()
																.substring(
																		0,
																		1)
																.toUpperCase()
														+ column
																.getJavaName()
																.substring(
																		1)
														+ "()"));
				paramCount++;
			}
		}
		daoImpFile.println("\t\t\tint result = pstmt.executeUpdate();");
		daoImpFile.println("\t\t} catch (SQLException e) {");
		daoImpFile.println("\t\t\te.printStackTrace();");
		daoImpFile.println("\t\t}");
		daoImpFile.println("\t}");
		daoImpFile.println("}");
		daoImpFile.close();
	}
}
