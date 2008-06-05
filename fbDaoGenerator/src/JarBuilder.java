import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;

/**
 * Compile Java source code in a given directory and create a JAR file
 * containing sources and class files.
 *
 * For Ant to find the compiler, lib/tools.jar from the JDK needs to
 * be in the CLASSPATH.
 * 
 * @todo add a way to specify a classpath to pass to the compiler
 * @todo try using JavacExternal Task if Javac Task fails
 * @author cglodt
 */
public class JarBuilder {

	private final File srcDir;
	private final File destJar;

	public JarBuilder(File srcDir, File destJar) {
		this.srcDir = srcDir;
		this.destJar = destJar;
	}
	
	public void compileAndBuildJar() throws BuildException {
		
		Project p = new Project();
		
		Target compileTarget = new Target();
		compileTarget.setName("compile");
		Javac compileTask = new Javac();
		compileTask.setProject(p);
		Path outputPath = new Path(p);
		outputPath.setLocation(this.srcDir);
		compileTask.setSrcdir(outputPath);
		compileTask.setDestdir(this.srcDir);
		compileTarget.addTask(compileTask);
		p.addTarget(compileTarget);
		
		Jar jarTask = new Jar();
		jarTask.setProject(p);
		jarTask.setBasedir(this.srcDir);
		jarTask.setDestFile(this.destJar);
		
		Target jarTarget = new Target();
		jarTarget.setName("jar");
		jarTarget.addDependency("compile");
		jarTarget.addTask(jarTask);
		p.addTarget(jarTarget);
		
		p.executeTarget("jar");		
	}
	
	// For testing purposes
	public static void main(String[] args) {
		new JarBuilder(new File(args[0]), new File(args[1])).compileAndBuildJar();
	}
}
