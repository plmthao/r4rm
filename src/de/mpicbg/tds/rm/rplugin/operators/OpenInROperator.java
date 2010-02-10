package de.mpicbg.tds.rm.rplugin.operators;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.io.AbstractExampleSetWriter;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import de.mpicbg.tds.rm.rplugin.RUtils;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;

import java.io.File;
import java.util.List;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class OpenInROperator extends AbstractExampleSetWriter {

	private static final String OUTPUT_VAR_NAME = "outputVarName";

//    private InputPort exampleSetInput = getInputPorts().createPort("example set input", true);


	public OpenInROperator(OperatorDescription description) {
		super(description);
	}


	@Override
	public ExampleSet write(ExampleSet exampleSet) throws OperatorException {
		try {
			RConnection connection = new RConnection();

			// 1) convert exampleSet ihnto data-frame and put into the r-workspace
			RList inputAsRList = RUtils.convert2RList(exampleSet);
			String parName = getParameter(OUTPUT_VAR_NAME);
			connection.assign(parName, REXP.createDataFrame(inputAsRList));

			// save the work-space to a temporary file and open R
			File tmpFile = File.createTempFile(parName, ".RData");

			connection.eval("save(" + parName + ",file=\"" + tmpFile.getAbsolutePath().replace("\\", "/") + "\") ");
			connection.eval("rm(list = ls(all = TRUE));");

			connection.close();

			//spawn a new R process
			if (isWindowsPlatform()) {
				Runtime.getRuntime().exec("Rgui " + tmpFile.getAbsolutePath());
			} else if (isMacOSPlatform()) {
				Runtime.getRuntime().exec("open -n -a /Applications/R.app/ " + tmpFile.getAbsolutePath());
			} else { // linux and the rest of the world
				Runtime.getRuntime().exec("R " + tmpFile.getAbsolutePath());
			}

			tmpFile.deleteOnExit();

		} catch (Throwable e) {
			throw new RuntimeException("spawning of R-process failed", e);
		}

		return exampleSet;
	}


	private boolean isMacOSPlatform() {
		return (System.getProperty("mrj.version") != null);
	}


	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();

		types.add(new ParameterTypeString(OUTPUT_VAR_NAME, "name of the variable of the input in the r-workspace", "exampleSet", true));

		return types;
	}


	/**
	 * Try to determine whether this application is running under Windows or some other platform by examing the
	 * "os.name" property.
	 *
	 * @return true if this application is running under a Windows OS
	 */
	public static boolean isWindowsPlatform() {
		String os = System.getProperty("os.name");
		return os != null && os.startsWith("Windows");
	}


}
