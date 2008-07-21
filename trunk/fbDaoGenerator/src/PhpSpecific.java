import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
		final Configuration config = new Configuration();
		String outPutDir = config.getConfigFileParameter("phpOutputdir")+System.getProperty("file.separator");

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
		columnList = Table.getColumList(table);

		// create java class with getters and setters
		phpFile = new PrintStream(fileHandle);
		ob = ob.concat("<?php\n");
		ob = ob.concat("\nclass " + tableJavaName + " {\n\n");
		
		String tempPkFields = "";
		for (DataFieldFirebird column : columnList) {
			if (column.isInPK()) {
				tempPkFields = tempPkFields + column.getName() + ", ";
			}
		}
		if (tempPkFields.length() > 0) tempPkFields = tempPkFields.substring(0, (tempPkFields.length() - 2));
		ob = ob.concat("\t// Primary Key Fields: "+tempPkFields+"\n\n");

		for (DataFieldFirebird column : columnList) {
			ob = ob.concat("\tprivate $" + column.getJavaName() + ";\n");
		}

		for (DataFieldFirebird column : columnList) {
			ob = ob.concat("\n\tpublic function" + column.getPHPGetter() + " {\n");
			ob = ob.concat("\t\treturn $this->" + column.getJavaName() + ";\n");
			ob = ob.concat("\t}\n");
			ob = ob.concat("\n\tpublic function" + column.getPHPSetter() + " {\n");
			ob = ob.concat("\t\t$this->" + column.getJavaName() + " = $"+ column.getJavaName() + ";\n");
			ob = ob.concat("\t}\n");
		}
		ob = ob.concat("}\n");

		// create DAO implementation
		ob = ob.concat("\nclass " + tableJavaName + "DAOFirebird {\n");
		ob = ob.concat("\tprivate $conn;\n");
		ob = ob.concat("\tprivate $trans;\n\n");

		ob = ob.concat("\tprotected function getConn() {\n");
		ob = ob.concat("\t\treturn $this->conn;\n");
		ob = ob.concat("\t}\n\n");

		ob = ob.concat("\tprotected function setConn($conn) {\n");
		ob = ob.concat("\t\t$this->conn = $conn;\n");
		ob = ob.concat("\t}\n\n");

		ob = ob.concat("\tpublic function __construct($conn) {\n");
		ob = ob.concat("\t\t$this->setConn($conn);\n");
		ob = ob.concat("\t\t$this->trans = ibase_trans($conn);\n");
		ob = ob.concat("\t}\n\n");


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
		ob = ob.concat("\tpublic function get("+getterParams+") {\n");
		ob = ob.concat("\t\t$query = 'select " + fieldsList+ " from " + table + " where "+whereClause + "';\n");
		ob = ob.concat("\t\t$sth = ibase_query($this->getConn(), $query, "+getterParams+");\n");
		ob = ob.concat("\t\t$temp = new "+tableJavaName+"();\n");
        ob = ob.concat("\t\twhile ($row = ibase_fetch_row($sth, IBASE_FETCH_BLOBS)) {\n");
		int columnCount = 0;
		for (DataFieldFirebird column : columnList) {
			ob = ob.concat("\t\t\t$temp->set"
					+ column.getJavaName().substring(0, 1).toUpperCase()
					+ column.getJavaName().substring(1)
					+ "($row["+columnCount+"]);\n");
			columnCount++;
		}
        ob = ob.concat("\t\t}\n");
		ob = ob.concat("\t\treturn $temp;\n");
		ob = ob.concat("\t}\n\n");


		// getAll() method
		ob = ob.concat("\tpublic function getAll() {\n");
		ob = ob.concat("\t\t$query = 'select " + fieldsList + " from " + table + "';\n");
		ob = ob.concat("\t\t$sth = ibase_query($this->getConn(), $query);\n");
		ob = ob.concat("\t\t$temp = new "+tableJavaName+"();\n");
        ob = ob.concat("\t\twhile ($row = ibase_fetch_row($sth, IBASE_FETCH_BLOBS)) {\n");
		columnCount = 0;
		for (DataFieldFirebird column : columnList) {
			ob = ob.concat("\t\t\t$temp->set"
					+ column.getJavaName().substring(0, 1).toUpperCase()
					+ column.getJavaName().substring(1)
					+ "($row["+columnCount+"]);\n");
			columnCount++;
		}
		ob = ob.concat("\t\t\t$tempArray[] = $temp;\n");
        ob = ob.concat("\t\t}\n");
		ob = ob.concat("\t\treturn $tempArray;\n");
		ob = ob.concat("\t}\n\n");


		// getAllWithClause($clause) method
		ob = ob.concat("\tpublic function getAllWithClause($clause) {\n");
		ob = ob.concat("\t\t$query = 'select " + fieldsList+ " from " + table + " where '.$clause;\n");
		ob = ob.concat("\t\t$sth = ibase_query($this->getConn(), $query);\n");
		ob = ob.concat("\t\t$temp = new "+tableJavaName+"();\n");
        ob = ob.concat("\t\twhile ($row = ibase_fetch_row($sth, IBASE_FETCH_BLOBS)) {\n");
		columnCount = 0;
		for (DataFieldFirebird column : columnList) {
			ob = ob.concat("\t\t\t$temp->set"
					+ column.getJavaName().substring(0, 1).toUpperCase()
					+ column.getJavaName().substring(1)
					+ "($row["+columnCount+"]);\n");
			columnCount++;
		}
		ob = ob.concat("\t\t\t$tempArray[] = $temp;\n");
        ob = ob.concat("\t\t}\n");
		ob = ob.concat("\t\treturn $tempArray;\n");
		ob = ob.concat("\t}\n\n");


		// insert() method
		ob = ob.concat("\tfunction insert($o) {\n");
		ob = ob.concat("\t\t$stmt = 'insert into "+table+" ("+fieldsList+") values ("+insertPlaceHolders+")';\n");
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
		String updateValues = "";
		for (DataFieldFirebird column : columnList) {
			updateValues = updateValues + column.getName() + " = ?, ";
		}
		updateValues = updateValues.substring(0, (updateValues.length() - 2));
		ob = ob.concat("\tfunction update($o) {\n");
		ob = ob.concat("\t\t$stmt = 'update "+table+" set "+updateValues+" where "+whereClause+"';\n");
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
		ob = ob.concat("\tfunction delete($o) {\n");
		ob = ob.concat("\t\t$stmt = 'delete from "+table+" where "+whereClause+"';\n");
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
