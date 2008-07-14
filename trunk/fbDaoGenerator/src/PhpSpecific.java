import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;


public class PhpSpecific {
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

	public static void generateCode(String table) {
		final Configuration config = new Configuration();
		String outPutDir = config.getConfigFileParameter("outputdir")+"/";
//		System.out.println(outPutDir);
//		outPutDir = outPutDir + File.separator + config.getConfigFileParameter("package").replaceAll("\\.", "/") + "/";
		Helpers.deleteDir(new File(outPutDir));
		new File(outPutDir).mkdirs();
		ArrayList<String> sourceFilesToCompile = new ArrayList<String>();

//		String schemaVersion = DataBase.getSchemaVersion();

		System.out.println("Generating PHP code for table: " + table);
		ArrayList<DataFieldFirebird> columnList = new ArrayList<DataFieldFirebird>();
		PrintStream phpFile;
		FileOutputStream fileHandle = null;
		String tableJavaName = Helpers.underscoreSeparatedToCamelCase(table);
		tableJavaName = tableJavaName.substring(0, 1).toUpperCase() + tableJavaName.substring(1);
		
		
		try {
			fileHandle = new FileOutputStream(outPutDir+tableJavaName + ".phps");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		columnList = Table.getColumList(table);

		// create java class with getters and setters
		phpFile = new PrintStream(fileHandle);
		phpFile.println("<?php");
		phpFile.println();
		phpFile.println("class " + tableJavaName + " {");
		for (DataFieldFirebird column : columnList) {
			phpFile.println("\tprivate $" + column.getJavaName() + ";");
			phpFile.println("\tpublic function" + column.getPHPGetter() + " {");
			phpFile.println("\t\treturn $this->" + column.getJavaName() + ";");
			phpFile.println("\t}");
			phpFile.println("\tpublic function" + column.getPHPSetter() + " {");
			phpFile.println("\t\t$this->" + column.getJavaName() + " = $"+ column.getJavaName() + ";");
			phpFile.println("\t}");
			phpFile.println();
		}
		phpFile.println("}");

		// create DAO implementation
		phpFile.println();
		phpFile.println("class " + tableJavaName + "DAOFirebird {");
		phpFile.println("\tprivate $conn;");
		phpFile.println("\tprivate $trans;");
		phpFile.println();
		
		phpFile.println("\tprotected function getConn() {");
		phpFile.println("\t\treturn $this->conn;");
		phpFile.println("\t}");
		phpFile.println();

		phpFile.println("\tprotected function setConn($conn) {");
		phpFile.println("\t\t$this->conn = $conn;");
		phpFile.println("\t}");
		phpFile.println();

		phpFile.println("\tpublic function __construct($conn) {");
		phpFile.println("\t    $this->setConn($conn);");
		phpFile.println("\t    $this->trans = ibase_trans($conn);");
		phpFile.println("\t}");		
		phpFile.println();



		String fieldsList = "";
		for (DataFieldFirebird column : columnList) {
			fieldsList = fieldsList + column.getName() + ",";
		}
		fieldsList = fieldsList.substring(0,(fieldsList.length() - 1));

		String insertPlaceHolders = "";
		for (DataFieldFirebird column : columnList) {
			insertPlaceHolders = insertPlaceHolders + "?,";
		}
		insertPlaceHolders = insertPlaceHolders.substring(0, (insertPlaceHolders.length() - 1));

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

		String getterParams = "";
		for (DataFieldFirebird column : columnList) {
			if (column.isInPK()) {
				getterParams = getterParams + "$"+ column.getJavaName() + ", ";
			}
		}
		if (!getterParams.equals("")) {
			getterParams = getterParams.substring(0,
					(getterParams.length() - 2));
		}


		
		// get(PK) method
		phpFile.println("\tpublic function get("+getterParams+") {");
		phpFile.println("\t\t$query = \"select " + fieldsList+ " from " + table + " where "+whereClause + "\";");
		phpFile.println("\t\t$sth = ibase_query($this->getDbh(), $query, "+getterParams+");");
		phpFile.println("\t\t$temp = new "+tableJavaName+"();");
        phpFile.println("\t\twhile ($row = ibase_fetch_row($sth, IBASE_FETCH_BLOBS)) {");
		int columnCount = 0;
		for (DataFieldFirebird column : columnList) {
			phpFile.println("\t\t\t\t$temp->set"
					+ column.getJavaName().substring(0, 1).toUpperCase()
					+ column.getJavaName().substring(1)
					+ "($row["+columnCount+"]);");
			columnCount++;
		}
        phpFile.println("\t\t}");
		phpFile.println("\t\treturn $temp;");
		phpFile.println("\t}");
		phpFile.println();

		// getAll() method
		phpFile.println("\tpublic function getAll() {");
		phpFile.println("\t\t$query = \"select " + fieldsList + " from " + table + "\";");
		phpFile.println("\t\t$sth = ibase_query($this->getDbh(), $query);");
		phpFile.println("\t\t$temp = new "+tableJavaName+"();");
        phpFile.println("\t\twhile ($row = ibase_fetch_row($sth, IBASE_FETCH_BLOBS)) {");
		columnCount = 0;
		for (DataFieldFirebird column : columnList) {
			phpFile.println("\t\t\t\t$temp->set"
					+ column.getJavaName().substring(0, 1).toUpperCase()
					+ column.getJavaName().substring(1)
					+ "($row["+columnCount+"]);");
			columnCount++;
		}
		phpFile.println("\t\t\t\t$tempArray[] = $temp;");
        phpFile.println("\t\t}");
		phpFile.println("\t\treturn $tempArray;");
		phpFile.println("\t}");
		phpFile.println();
		
		// getAllWithClause($clause) method
		phpFile.println("\tpublic function getAllWithClause($clause) {");
		phpFile.println("\t\t$query = \"select " + fieldsList+ " from " + table + " where $clause\";");
		phpFile.println("\t\t$sth = ibase_query($this->getDbh(), $query);");
		phpFile.println("\t\t$temp = new "+tableJavaName+"();");
        phpFile.println("\t\twhile ($row = ibase_fetch_row($sth, IBASE_FETCH_BLOBS)) {");
		columnCount = 0;
		for (DataFieldFirebird column : columnList) {
			phpFile.println("\t\t\t\t$temp->set"
					+ column.getJavaName().substring(0, 1).toUpperCase()
					+ column.getJavaName().substring(1)
					+ "($row["+columnCount+"]);");
			columnCount++;
		}
		phpFile.println("\t\t\t\t$tempArray[] = $temp;");
        phpFile.println("\t\t}");
		phpFile.println("\t\treturn $tempArray;");
		phpFile.println("\t}");
		phpFile.println();


		// insert() method
		phpFile.println("\tfunction insert($o) {");		
		phpFile.println("\t\t$stmt = 'insert into "+table+" ("+fieldsList+") values ("+insertPlaceHolders+")';");
		phpFile.println("\t\t$sth = ibase_prepare($this->getDbh(), $stmt);");
		phpFile.println("\t\t$result = ibase_execute($sth, ...);");
		phpFile.println("\t\treturn $result;");
		phpFile.println("\t}");
		phpFile.println();

/*
		phpFile.println("\t\tPreparedStatement pstmt = null;");
		phpFile.println("\t\tString stmt = \"insert into " + table
				+ " \"+");
		phpFile.println("\t\t\"(" + insertFields + ") \"+ ");
		phpFile.println("\t\t\"values (" + insertPlaceHolders + ")\";");
		phpFile.println("\t\ttry {");
		phpFile.println("\t\t\tpstmt = conn.prepareStatement(stmt);");
		int paramCount = 1;
		for (DataFieldFirebird column : columnList) {
			phpFile.println("\t\t\tpstmt."
					+ JavaSpecific.createPreparedStatementSetter(column
							.getJavaType(), paramCount, "record.get"
							+ column.getJavaName().substring(0, 1)
									.toUpperCase()
							+ column.getJavaName().substring(1) + "()"));
			paramCount++;
		}
		phpFile.println("\t\t\tint result = pstmt.executeUpdate();");
		phpFile.println("\t\t} catch (SQLException e) {");
		phpFile.println("\t\t\te.printStackTrace();");
		phpFile.println("\t\t}");
		phpFile.println("\t}");
		phpFile.println();
		*/
		
		
		// update() method
		String updateValues = "";
		for (DataFieldFirebird column : columnList) {
			updateValues = updateValues + column.getName() + " = ?, ";
		}
		updateValues = updateValues.substring(0, (updateValues.length() - 2));
		phpFile.println("\tfunction update($o) {");
		phpFile.println("\t\t$stmt = 'update "+table+" set "+updateValues+" where "+whereClause+"';");
		phpFile.println("\t\t$sth = ibase_prepare($this->getDbh(), $stmt);");
		phpFile.println("\t\t$result = ibase_execute($sth, ...);");
		phpFile.println("\t\treturn $result;");
		phpFile.println("\t}");
		phpFile.println();

/*
		phpFile.println("\t\tPreparedStatement pstmt = null;");
		phpFile.println("\t\tString stmt = \"update " + table + " set \"+");
		phpFile.println("\t\t\""
				+ updateValues.substring(0, (updateValues.length() - 2))
				+ " where \"+");
		phpFile.println("\t\t\"" + whereClause + "\";");

		phpFile.println("\t\ttry {");
		phpFile.println("\t\t\tpstmt = conn.prepareStatement(stmt);");
		int paramCount = 1;
		for (DataFieldFirebird column : columnList) {
			phpFile.println("\t\t\tpstmt."
					+ JavaSpecific.createPreparedStatementSetter(column
							.getJavaType(), paramCount, "record.get"
							+ column.getJavaName().substring(0, 1)
									.toUpperCase()
							+ column.getJavaName().substring(1) + "()"));
			paramCount++;
		}
		for (DataFieldFirebird column : columnList) {
			if (column.isInPK()) {
				phpFile
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
		phpFile.println("\t\t\tint result = pstmt.executeUpdate();");
		phpFile.println("\t\t} catch (SQLException e) {");
		phpFile.println("\t\t\te.printStackTrace();");
		phpFile.println("\t\t}");
		phpFile.println("\t}");
		phpFile.println();
		*/

		// delete() method
		phpFile.println("\tfunction delete($o) {");
		phpFile.println("\t\t$stmt = 'delete from "+table+" where "+whereClause+"';");
		phpFile.println("\t\t$sth = ibase_prepare($this->getDbh(), $stmt);");
		phpFile.println("\t\t$result = ibase_execute($sth, ...);");
		phpFile.println("\t\treturn $result;");
		phpFile.println("\t}");
		phpFile.println();

		/*
		int paramCount = 1;
		for (DataFieldFirebird column : columnList) {
			if (column.isInPK()) {
				phpFile
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
		phpFile.println("\t\t\tint result = pstmt.executeUpdate();");
		phpFile.println("\t\t} catch (SQLException e) {");
		phpFile.println("\t\t\te.printStackTrace();");
		phpFile.println("\t\t}");
		*/

		phpFile.println("\tpublic function commit() {");
		phpFile.println("\t\treturn ibase_commit($this->trans);");
		phpFile.println("\t}");

		phpFile.println();

		phpFile.println("\tpublic function rollback() {");
		phpFile.println("\t\treturn ibase_rollback($this->trans);");
		phpFile.println("\t}");

		phpFile.println("}");
		phpFile.println();
		phpFile.println("?>");
		phpFile.close();
	}
}
