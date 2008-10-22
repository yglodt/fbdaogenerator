import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class PhpSpecific {
/*
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
*/
	public static void generateCode(String table) {
		String outPutDir = Main.config.getConfigFileParameter("phpOutputdir")+System.getProperty("file.separator");

//		String schemaVersion = DataBase.getSchemaVersion();

		System.out.println("Generating PHP code for table: " + table);
		ArrayList<DataFieldFirebird> columnList = new ArrayList<DataFieldFirebird>();
		PrintStream phpFile;
		FileOutputStream fileHandle = null;
		String ob = "";
		String tableJavaName = Helpers.underscoreSeparatedToCamelCase(table);
		tableJavaName = tableJavaName.substring(0, 1).toUpperCase() + tableJavaName.substring(1);

		try {
			fileHandle = new FileOutputStream(outPutDir+tableJavaName + ".php");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Table t = new Table();
		columnList = t.getColumList(table);
		String insertStatementFieldsList = t.getInsertStatementFieldsList();
		String insertStatementPlaceHolders = t.getInsertStatementPlaceHolders();
		String updateStatementFieldsList = t.getUpdateStatementFieldsList();
		String pkWhereStatement = t.getPkWhereStatement();

		// create PHP class with getters and setters
		phpFile = new PrintStream(fileHandle);
		ob = ob.concat("<?php\n");
		ob = ob.concat("\nclass " + tableJavaName + " {\n");
		
		String tempPkFields = "";
		for (DataFieldFirebird column : columnList) {
			if (column.isInPK()) {
				tempPkFields = tempPkFields + column.getName() + ", ";
			}
		}
		if (tempPkFields.length() > 0) tempPkFields = tempPkFields.substring(0, (tempPkFields.length() - 2));
		ob = ob.concat("\t// Primary Key Field(s): "+tempPkFields+"\n");

		for (DataFieldFirebird column : columnList) {
			ob = ob.concat("\tprivate $" + column.getJavaName() + ";\n");
		}

		ob = ob.concat("\n\tfunction __construct() {}\n");
		ob = ob.concat("\tfunction __destruct() {}\n\n");

		for (DataFieldFirebird column : columnList) {
			ob = ob.concat("\tpublic function" + column.getPHPGetter() + " { return $this->" + column.getJavaName() + "; }\n");
			ob = ob.concat("\tpublic function" + column.getPHPSetter() + " { $this->" + column.getJavaName() + " = $"+ column.getJavaName() + "; }\n");
		}
		ob = ob.concat("}\n");

		// create DAO implementation
		ob = ob.concat("\nclass " + tableJavaName + "DAO {\n");
		ob = ob.concat("\tprivate $conn;\n");
		ob = ob.concat("\tprivate $trans;\n\n");

		ob = ob.concat("\tpublic function __construct($conn) {\n");
		ob = ob.concat("\t\t$this->setConn($conn);\n");
		ob = ob.concat("\t\t$this->trans = ibase_trans($conn);\n");
		ob = ob.concat("\t}\n\n");

		ob = ob.concat("\tpublic function __destruct() {\n");
		ob = ob.concat("\t}\n\n");

		ob = ob.concat("\tprivate function getConn() {\n");
		ob = ob.concat("\t\treturn $this->conn;\n");
		ob = ob.concat("\t}\n\n");

		ob = ob.concat("\tprivate function setConn($conn) {\n");
		ob = ob.concat("\t\t$this->conn = $conn;\n");
		ob = ob.concat("\t}\n\n");

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

		
		// fillObject(PK) method
		ob = ob.concat("\tprivate function fillObject($sth, $returnArray = true) {\n");
		//ob = ob.concat("\t\t$tempArray = array();\n");
        ob = ob.concat("\t\twhile ($row = ibase_fetch_row($sth, IBASE_FETCH_BLOBS)) {\n");
		ob = ob.concat("\t\t\t$temp = new "+tableJavaName+"();\n");
		int columnCount = 0;
		for (DataFieldFirebird column : columnList) {
			ob = ob.concat("\t\t\t$temp->set"
					+ column.getJavaName().substring(0, 1).toUpperCase()
					+ column.getJavaName().substring(1)
					+ "($row["+columnCount+"]);\n");
			columnCount++;
		}
		ob = ob.concat("\t\t\t$tempArray[] = $temp;\n");
        ob = ob.concat("\t\t}\n");
		//ob = ob.concat("\t\treturn (count($tempArray) > 1) ? $tempArray : $tempArray[0];\n");
		ob = ob.concat("\t\treturn ($returnArray == true) ? $tempArray : $tempArray[0];\n");
		ob = ob.concat("\t}\n\n");


		// get(PK) method
		ob = ob.concat("\tpublic function get("+getterParams+") {\n");
		ob = ob.concat("\t\t$query = 'select " + insertStatementFieldsList+ " from " + table + pkWhereStatement + "';\n");
		ob = ob.concat("\t\t$sth = ibase_query($this->getConn(), $query, "+getterParams+");\n");
		ob = ob.concat("\t\treturn $this->fillObject($sth, $returnArray = false);\n");
		ob = ob.concat("\t}\n\n");


		// getAll() method
		ob = ob.concat("\tpublic function getAll() {\n");
		ob = ob.concat("\t\t$query = 'select " + insertStatementFieldsList + " from " + table + "';\n");
		ob = ob.concat("\t\t$sth = ibase_query($this->getConn(), $query);\n");
		ob = ob.concat("\t\treturn $this->fillObject($sth);\n");
		ob = ob.concat("\t}\n\n");


		// getAllWithClause($clause) method
		ob = ob.concat("\tpublic function getAllWithClause($clause) {\n");
		ob = ob.concat("\t\t$parameters = func_get_args();\n");
		ob = ob.concat("\t\t$clause = array_shift($parameters);\n");
		ob = ob.concat("\t\t$query = 'select " + insertStatementFieldsList+ " from " + table + " '.$clause;\n");
		ob = ob.concat("\t\t$sth = call_user_func_array('ibase_query', array_merge(array($this->getConn(), $query), $parameters));\n");
		ob = ob.concat("\t\treturn $this->fillObject($sth);\n");
		ob = ob.concat("\t}\n\n");

		
		// getAllWithClauseAndLimit($from, $to, $clause) method
		ob = ob.concat("\tpublic function getAllWithClauseAndLimit($from, $to, $clause) {\n");
		ob = ob.concat("\t\t$parameters = func_get_args();\n");
		ob = ob.concat("\t\t$from = array_shift($parameters);\n");
		ob = ob.concat("\t\t$to = array_shift($parameters);\n");
		ob = ob.concat("\t\t$clause = array_shift($parameters);\n");
		ob = ob.concat("\t\t$firstSkip = ' first '.(($to + 1) - $from).' skip '.($from - 1);\n");
		ob = ob.concat("\t\t$query = 'select'.\"$firstSkip\".' " + insertStatementFieldsList+ " from " + table + " '.$clause;\n");
		ob = ob.concat("\t\t$sth = call_user_func_array('ibase_query', array_merge(array($this->getConn(), $query), $parameters));\n");
		ob = ob.concat("\t\treturn $this->fillObject($sth);\n");
		ob = ob.concat("\t}\n\n");


		// insert() method
		ob = ob.concat("\tpublic function insert($o) {\n");
		ob = ob.concat("\t\t$stmt = 'insert into "+table+" ("+insertStatementFieldsList+") values ("+insertStatementPlaceHolders+")';\n");
		ob = ob.concat("\t\t$sth = ibase_prepare($this->getConn(), $stmt);\n");
		ob = ob.concat("\t\t$result = ibase_execute($sth, ");
		columnCount = 0;
		String insertValues = "";
		for (DataFieldFirebird column : columnList) {
			insertValues = insertValues + "$o->get"
					+ column.getJavaName().substring(0, 1).toUpperCase()
					+ column.getJavaName().substring(1)
					+ "(), ";
			columnCount++;
		}
		if (!insertValues.equals("")) {
			insertValues = insertValues.substring(0,
					(insertValues.length() - 2));
			ob = ob.concat(insertValues+");");
		}
		ob = ob.concat("\n");
		ob = ob.concat("\t\treturn $result;\n");
		ob = ob.concat("\t}\n\n");


		// update() method
		ob = ob.concat("\tpublic function update($o) {\n");
		ob = ob.concat("\t\t$stmt = 'update "+table+" set "+updateStatementFieldsList+pkWhereStatement+"';\n");
		ob = ob.concat("\t\t$sth = ibase_prepare($this->getConn(), $stmt);\n");
		ob = ob.concat("\t\t$result = ibase_execute($sth, ");
		columnCount = 0;
		insertValues = "";
		for (DataFieldFirebird column : columnList) {
			insertValues = insertValues + "$o->get"
					+ column.getJavaName().substring(0, 1).toUpperCase()
					+ column.getJavaName().substring(1)
					+ "(), ";
			columnCount++;
		}
		
		for (DataFieldFirebird column : columnList) {
			if (column.isInPK()) {
				insertValues = insertValues + "$o->get"
						+ column.getJavaName().substring(0, 1).toUpperCase()
						+ column.getJavaName().substring(1)
						+ "(), ";
			}
			columnCount++;
		}

		if (!insertValues.equals("")) {
			insertValues = insertValues.substring(0,
					(insertValues.length() - 2));
			ob = ob.concat(insertValues+");");
		}
		ob = ob.concat("\n");
		ob = ob.concat("\t\treturn $result;\n");
		ob = ob.concat("\t}\n\n");


		// delete() method
		ob = ob.concat("\tpublic function delete($o) {\n");
		ob = ob.concat("\t\t$stmt = 'delete from "+table+pkWhereStatement+"';\n");
		ob = ob.concat("\t\t$sth = ibase_prepare($this->getConn(), $stmt);\n");
		ob = ob.concat("\t\t$result = ibase_execute($sth, ");
		columnCount = 0;
		insertValues = "";
		for (DataFieldFirebird column : columnList) {
			if (column.isInPK()) {
				insertValues = insertValues + "$o->get"
						+ column.getJavaName().substring(0, 1).toUpperCase()
						+ column.getJavaName().substring(1)
						+ "(), ";
			}
			columnCount++;
		}
		if (!insertValues.equals("")) {
			insertValues = insertValues.substring(0,
					(insertValues.length() - 2));
			ob = ob.concat(insertValues+");");
		}
		ob = ob.concat("\n");
		ob = ob.concat("\t\treturn $result;\n");
		ob = ob.concat("\t}\n\n");

		ob = ob.concat("\tpublic function commit() {\n");
		ob = ob.concat("\t\treturn ibase_commit($this->trans);\n");
		ob = ob.concat("\t}\n\n");

		ob = ob.concat("\tpublic function rollback() {\n");
		ob = ob.concat("\t\treturn ibase_rollback($this->trans);\n");
		ob = ob.concat("\t}\n");

		ob = ob.concat("}\n\n");
		ob = ob.concat("?>");
		phpFile.print(ob);
		phpFile.flush();
		phpFile.close();
	}
}
