import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Main {
	static Configuration config =  new Configuration();

	public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
	}
	
	public static void main(String[] args) {

		for (String s: args) {
            System.out.println("Arg: "+s);
        }

		ArrayList<String> tableList = DataBase.getTableList();
		// TODO: delete dir before starting at all
		String outPutDir = config.getConfigFileParameter("outputdir");		
	    Main.deleteDir(new File(outPutDir));

	    /*
	     * http://exampledepot.com/egs/java.io/DeleteDir.html 
	     * http://forum.java.sun.com/thread.jspa?threadID=563148&messageID=3415560
	     * http://forum.java.sun.com/thread.jspa?threadID=5180140&messageID=9700001
	     * http://www.bytemycode.com/snippets/snippet/188/
	     * http://java.sun.com/j2se/1.4.2/docs/api/java/io/FilePermission.html
	     * http://snippets.dzone.com/tag/directory/2
	     * http://www.dreamincode.net/code/snippet1444.htm
	     */

	    new File(outPutDir).mkdirs();
		outPutDir = outPutDir + File.separator;
		ArrayList<String> sourceFilesToCompile = new ArrayList<String>();
//		String[] sourceFilesToCompile = {};
		//int sourceFileCounter = 0;
		String schemaVersion = DataBase.getSchemaVersion();

		for (String table : tableList) {
			System.out.println("Generating java code for table: "+table);
			ArrayList<DataFieldFirebird> columnList = new ArrayList<DataFieldFirebird>();
			PrintStream classFile;
			PrintStream daoFile;
			PrintStream daoImpFile;
			FileOutputStream classFileHandle = null;
			FileOutputStream daoFileHandle = null;
			FileOutputStream daoImpFileHandle = null;
			String tableJavaName = underscoreSeparatedToCamelCase(table);
			tableJavaName = tableJavaName.substring(0,1).toUpperCase() + tableJavaName.substring(1);
			try {
				classFileHandle = new FileOutputStream(outPutDir+tableJavaName+".java");
				daoFileHandle = new FileOutputStream(outPutDir+tableJavaName+"DAO.java");
				daoImpFileHandle = new FileOutputStream(outPutDir+tableJavaName+"DAOFirebird.java");
				sourceFilesToCompile.add(outPutDir+tableJavaName+".java");
				sourceFilesToCompile.add(outPutDir+tableJavaName+"DAO.java");
				sourceFilesToCompile.add(outPutDir+tableJavaName+"DAOFirebird.java");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			columnList = Table.getColumList(table);

			// create java class with getters and setters
			classFile = new PrintStream(classFileHandle);
			classFile.println("package "+config.getConfigFileParameter("package")+";");
			classFile.println();
			classFile.println("import java.util.Date;");
			classFile.println();
			classFile.println("public class "+tableJavaName+" {");
			for (DataFieldFirebird column: columnList) {
				classFile.println();
				classFile.println("\tprivate "+column.getJavaType()+" "+column.getJavaName()+";");
				classFile.println("\tpublic "+column.getJavaType()+column.getJavaGetter()+" {");
				classFile.println("\t\treturn "+column.getJavaName()+";");
				classFile.println("\t}");
				classFile.println();
				classFile.println("\tpublic void"+column.getJavaSetter()+" {");
				classFile.println("\t\tthis."+column.getJavaName()+" = "+column.getJavaName()+";");
				classFile.println("\t}");
			}
			classFile.println("}");
			classFile.close();
			
			// create DAO java interface
			daoFile = new PrintStream(daoFileHandle);
			daoFile.println("package "+config.getConfigFileParameter("package")+";");
			daoFile.println();
			daoFile.println("import java.util.Date;");
			daoFile.println();
			daoFile.println("public interface "+tableJavaName+"DAO {");
			daoFile.println();
			daoFile.print("\tpublic "+tableJavaName+" get(");
			String getterParams = "";
			for (DataFieldFirebird column: columnList) {
//				String column.getJavaName() = underscoreSeparatedToCamelCase(column.getName());
				if (column.isInPK()) {
					getterParams = getterParams + column.getJavaType()+" "+ column.getJavaName() + ", ";
				}
			}
			if (!getterParams.equals("")) {
				getterParams = getterParams.substring(0, (getterParams.length()-2));
				daoFile.print(getterParams);
			}
			daoFile.println(");");
			daoFile.println();
			daoFile.println("\tpublic "+tableJavaName+"[] getAll();");
			daoFile.println();
			daoFile.println("\tpublic void insert("+tableJavaName+" record);");
			daoFile.println();
			daoFile.println("\tpublic void update("+tableJavaName+" record);");
			daoFile.println();
			daoFile.println("\tpublic void delete("+tableJavaName+" record);");
			daoFile.println("}");
			daoFile.close();

			// create DAO implementation
			daoImpFile = new PrintStream(daoImpFileHandle);
			daoImpFile.println("package "+config.getConfigFileParameter("package")+";");
			daoImpFile.println();
			daoImpFile.println("import java.sql.*;");
			daoImpFile.println("import java.util.ArrayList;");
			daoImpFile.println("import java.util.Properties;");
			daoImpFile.println("import java.util.Date;");
			daoImpFile.println();
			daoImpFile.println("public class "+tableJavaName+"DAOFirebird implements "+tableJavaName+"DAO {");
			daoImpFile.println("\tprivate Connection conn = null;");
			daoImpFile.println();
			daoImpFile.println("\tpublic "+tableJavaName+"DAOFirebird(Connection conn) {");
			daoImpFile.println("\t\tthis.conn = conn;");
			daoImpFile.println("\t}");
			daoImpFile.println();

			// get(PK) method
			daoImpFile.print("\tpublic "+tableJavaName+" get(");
			getterParams = "";
			for (DataFieldFirebird column: columnList) {
				if (column.isInPK()) {
					getterParams = getterParams + column.getJavaType()+" "+ column.getJavaName() + ", ";
				}
			}
			if (!getterParams.equals("")) {
				getterParams = getterParams.substring(0, (getterParams.length()-2));
			}
			daoImpFile.print(getterParams);
			daoImpFile.println(") {");
			daoImpFile.println("\t\tResultSet rst = null;");
			daoImpFile.println("\t\tPreparedStatement pstmt = null;");
			daoImpFile.println("\t\tString sql = \"select * from "+table+" where \"+");
			// build the "where" clause for the primary key. Caution: this clause is also used in the update() and delete() methods!
			String whereClause = "";
			for (DataFieldFirebird column: columnList) {
				if (column.isInPK()) {
					whereClause = whereClause + column.getName() + " = ? and ";
				}
			}
			if (!whereClause.equals("")) {
				whereClause = whereClause.substring(0, (whereClause.length()-5));
			}
			daoImpFile.println("\t\t\""+whereClause+"\";");
			daoImpFile.println("\t\t"+tableJavaName+" record = new "+tableJavaName+"();");
			daoImpFile.println("\t\ttry {");
			daoImpFile.println("\t\t\tpstmt = conn.prepareStatement(sql);");
			int paramCount = 1;
			for (DataFieldFirebird column: columnList) {
				if (column.isInPK()) {
					daoImpFile.println("\t\t\tpstmt."+JavaSpecific.createPreparedStatementSetter(column.getJavaType(), paramCount, column.getJavaName()));
//					daoImpFile.println("\t\t\tpstmt.set"+column.getJavaType().substring(0,1).toUpperCase() + column.getJavaType().substring(1)+"("+paramCount+", "+column.getJavaName()+");");
					paramCount++;
				}
			}
			daoImpFile.println("\t\t\trst = pstmt.executeQuery();");
			daoImpFile.println("\t\t\twhile(rst.next()) {");
			int columnCount = 1;
			for (DataFieldFirebird column: columnList) {
				daoImpFile.println("\t\t\t\trecord.set"+column.getJavaName().substring(0,1).toUpperCase() + column.getJavaName().substring(1)+"(rst.get"+column.getJavaType().substring(0,1).toUpperCase() + column.getJavaType().substring(1)+"("+columnCount+"));");
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
			daoImpFile.println("\tpublic "+tableJavaName+"[] getAll() {");
			daoImpFile.println("\t\tArrayList<"+tableJavaName+"> list = new ArrayList<"+tableJavaName+">();");
			daoImpFile.println("\t\tResultSet rst = null;");
			daoImpFile.println("\t\tPreparedStatement pstmt = null;");
			
			// make list of colums to fetch
			String selectValues = "";
			for (DataFieldFirebird column: columnList) {
				selectValues = selectValues + column.getName() + " = ?, ";
			}
			selectValues = selectValues.substring(0, (selectValues.length()-2));
			
			daoImpFile.println("\t\tString sql = \"select "+selectValues+" from "+table+"\";");
			daoImpFile.println("\t\ttry {");
			daoImpFile.println("\t\t\tpstmt = conn.prepareStatement(sql);");
			daoImpFile.println("\t\t\trst = pstmt.executeQuery();");
			daoImpFile.println("\t\t\twhile(rst.next()) {");
			daoImpFile.println("\t\t\t\t"+tableJavaName+" record = new "+tableJavaName+"();");
			columnCount = 1;
			for (DataFieldFirebird column: columnList) {
				daoImpFile.println("\t\t\t\trecord.set"+column.getJavaName().substring(0,1).toUpperCase() + column.getJavaName().substring(1)+"(rst.get"+column.getJavaType().substring(0,1).toUpperCase() + column.getJavaType().substring(1)+"("+columnCount+"));");
				columnCount++;
			}
			daoImpFile.println("\t\t\t\tlist.add(record);");
			daoImpFile.println("\t\t\t}");
			daoImpFile.println("\t\t} catch (SQLException e) {");
			daoImpFile.println("\t\t\te.printStackTrace();");
			daoImpFile.println("\t\t}");
			daoImpFile.println("\t\treturn list.toArray(new "+tableJavaName+"[0]);");
			daoImpFile.println("\t}");
			daoImpFile.println();

			// getAll(String clause) method
			daoImpFile.println("\tpublic "+tableJavaName+"[] getAll(String clause) {");
			daoImpFile.println("\t\tArrayList<"+tableJavaName+"> list = new ArrayList<"+tableJavaName+">();");
			daoImpFile.println("\t\tResultSet rst = null;");
			daoImpFile.println("\t\tPreparedStatement pstmt = null;");
			daoImpFile.println("\t\tString sql = \"select * from "+table+" \" + clause;");
			daoImpFile.println("\t\ttry {");
			daoImpFile.println("\t\t\tpstmt = conn.prepareStatement(sql);");
			daoImpFile.println("\t\t\trst = pstmt.executeQuery();");
			daoImpFile.println("\t\t\twhile(rst.next()) {");
			daoImpFile.println("\t\t\t\t"+tableJavaName+" record = new "+tableJavaName+"();");
			columnCount = 1;
			for (DataFieldFirebird column: columnList) {
				daoImpFile.println("\t\t\t\trecord.set"+column.getJavaName().substring(0,1).toUpperCase() + column.getJavaName().substring(1)+"(rst.get"+column.getJavaType().substring(0,1).toUpperCase() + column.getJavaType().substring(1)+"("+columnCount+"));");
				columnCount++;
			}
			daoImpFile.println("\t\t\t\tlist.add(record);");
			daoImpFile.println("\t\t\t}");
			daoImpFile.println("\t\t} catch (SQLException e) {");
			daoImpFile.println("\t\t\te.printStackTrace();");
			daoImpFile.println("\t\t}");
			daoImpFile.println("\t\treturn list.toArray(new "+tableJavaName+"[0]);");
			daoImpFile.println("\t}");
			daoImpFile.println();

			
			// insert() method
			daoImpFile.println("\tpublic void insert("+tableJavaName+" record) {");
			daoImpFile.println("\t\tPreparedStatement pstmt = null;");
			daoImpFile.println("\t\tString stmt = \"insert into "+table+" \"+");
			String insertFields = "";
			String insertPlaceHolders = "";
			for (DataFieldFirebird column: columnList) {
				insertFields = insertFields + column.getName() + ", ";
				insertPlaceHolders = insertPlaceHolders + "?, ";
			}
			insertFields = insertFields.substring(0, (insertFields.length()-2));
			insertPlaceHolders = insertPlaceHolders.substring(0, (insertPlaceHolders.length()-2));
			daoImpFile.println("\t\t\"("+insertFields+") \"+ ");
			daoImpFile.println("\t\t\"values ("+insertPlaceHolders+")\";");
			daoImpFile.println("\t\ttry {");
			daoImpFile.println("\t\t\tpstmt = conn.prepareStatement(stmt);");
			paramCount = 1;
			for (DataFieldFirebird column: columnList) {
				daoImpFile.println("\t\t\tpstmt."+JavaSpecific.createPreparedStatementSetter(column.getJavaType(), paramCount, "record.get"+column.getJavaName().substring(0,1).toUpperCase()+column.getJavaName().substring(1)+"()"));
				paramCount++;
			}
			daoImpFile.println("\t\t\tint result = pstmt.executeUpdate();");
			daoImpFile.println("\t\t} catch (SQLException e) {");
			daoImpFile.println("\t\t\te.printStackTrace();");
			daoImpFile.println("\t\t}");
			daoImpFile.println("\t}");
			daoImpFile.println();

			// update() method
			daoImpFile.println("\tpublic void update("+tableJavaName+" record) {");
			daoImpFile.println("\t\tPreparedStatement pstmt = null;");
			daoImpFile.println("\t\tString stmt = \"update "+table+" set \"+");
			String updateValues = "";
			for (DataFieldFirebird column: columnList) {
				updateValues = updateValues + column.getName() + " = ?, ";
			}
			daoImpFile.println("\t\t\""+updateValues.substring(0, (updateValues.length()-2))+" where \"+");
			daoImpFile.println("\t\t\""+whereClause+"\";");

			daoImpFile.println("\t\ttry {");
			daoImpFile.println("\t\t\tpstmt = conn.prepareStatement(stmt);");
			paramCount = 1;
			for (DataFieldFirebird column: columnList) {
				daoImpFile.println("\t\t\tpstmt."+JavaSpecific.createPreparedStatementSetter(column.getJavaType(), paramCount, "record.get"+column.getJavaName().substring(0,1).toUpperCase()+column.getJavaName().substring(1)+"()"));
				paramCount++;
			}
			for (DataFieldFirebird column: columnList) {
				if (column.isInPK()) {
					daoImpFile.println("\t\t\tpstmt."+JavaSpecific.createPreparedStatementSetter(column.getJavaType(), paramCount, "record.get"+column.getJavaName().substring(0,1).toUpperCase()+column.getJavaName().substring(1)+"()"));
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
			daoImpFile.println("\tpublic void delete("+tableJavaName+" record) {");
			daoImpFile.println("\t\tPreparedStatement pstmt = null;");
			daoImpFile.println("\t\tString stmt = \"delete from "+table+" where \"+");
			daoImpFile.println("\t\t\""+whereClause+"\";");			
			daoImpFile.println("\t\ttry {");
			daoImpFile.println("\t\t\tpstmt = conn.prepareStatement(stmt);");
			paramCount = 1;
			for (DataFieldFirebird column: columnList) {
				if (column.isInPK()) {
					daoImpFile.println("\t\t\tpstmt."+JavaSpecific.createPreparedStatementSetter(column.getJavaType(), paramCount, "record.get"+column.getJavaName().substring(0,1).toUpperCase()+column.getJavaName().substring(1)+"()"));
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

		File sourceFilesToCompileFile = new File(outPutDir+File.separator+"sourceFileList.txt");
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(sourceFilesToCompileFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        for (int i = 0; i < sourceFilesToCompile.size(); i++) {
                try {
					fileWriter.write(sourceFilesToCompile.get(i) + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
        }
        try {
			fileWriter.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		//sun.tools.javac.Main c= new sun.tools.javac.Main(out, "javac"); 
		String s = null;
    	String compilerCommandline = config.getConfigFileParameter("javaCompiler")+" -target "+config.getConfigFileParameter("targetVm")+" -d "+config.getConfigFileParameter("outputdir")+" -classpath "+config.getConfigFileParameter("outputdir")+" -sourcepath "+config.getConfigFileParameter("outputdir") + " @"+config.getConfigFileParameter("outputdir")+File.separator+"sourceFileList.txt";
    	System.out.println("Compiling the generated code: "+compilerCommandline);
        try {
            Process compilerProcess = Runtime.getRuntime().exec(compilerCommandline, (String[]) sourceFilesToCompile.toArray(new String[sourceFilesToCompile.size()]));
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(compilerProcess.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(compilerProcess.getErrorStream()));

			while ((s = stdInput.readLine()) != null) {
			    System.out.println(s);
			}

			while ((s = stdError.readLine()) != null) {
			    System.out.println(s);
			}
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

		// create the package
		// with help from: http://www.exampledepot.com/egs/java.util.zip/CreateZip.html
		String[] filenames = new File(outPutDir+File.separator+config.getConfigFileParameter("package").replace(".", "/")+File.separator).list();
        String outFilename = config.getConfigFileParameter("package_filename").replace("$V", "-"+schemaVersion);
        System.out.println("Creating java package: "+outFilename);
        ZipOutputStream out = null;
		byte[] buf = new byte[1024];

	    try {
			out = new ZipOutputStream(new FileOutputStream(outFilename));
	        for (int i=0; i<filenames.length; i++) {
	            // this sucker will silently die if you feed him dirt
	            FileInputStream in = new FileInputStream(config.getConfigFileParameter("outputdir")+File.separator+config.getConfigFileParameter("package").replace(".", "/")+File.separator+filenames[i]);

	            System.out.println("Adding class to package: "+config.getConfigFileParameter("package").replace(".", "/")+File.separator+filenames[i]);
	            out.putNextEntry(new ZipEntry(config.getConfigFileParameter("package").replace(".", "/")+File.separator+filenames[i]));

	            int len;
	            while ((len = in.read(buf)) > 0) {
	                out.write(buf, 0, len);
	            }
	            out.closeEntry();
	            in.close();
	        }
	        System.out.println("Closing the package.");
	        out.close();
	    } catch (IOException e) {
	    }
	}

	public static String underscoreSeparatedToCamelCase(String name) {
		// copied from: http://www.mail-archive.com/dev@commons.apache.org/msg03457.html
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

}
