package de.mpicbg.tds.rm.rplugin.operators;

import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPortExtender;
import de.mpicbg.tds.rm.rplugin.RUtils;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class OpenInROperator extends Operator {


//    private InputPort exampleSetInput = getInputPorts().createPort("example set input", true);
	private InputPortExtender inputs = new InputPortExtender("input table", getInputPorts());


	public OpenInROperator(OperatorDescription description) {
		super(description);

		inputs.start();
	}


	public void doWork() throws OperatorException {

		try {
			RConnection connection = RUtils.createConnection();

			// 1) convert exampleSet ihnto data-frame and put into the r-workspace
			List<IOObject> inputs = this.inputs.getData(true);


			List<String> parNames = RUtils.push2R(connection, inputs);
			// save the work-space to a temporary file and open R
			File tmpFile = File.createTempFile("rplugin", ".RData");

			String allParams = parNames.toString().replace("[", "").replace("]", "").replace(" ", "");
			connection.eval("save(" + allParams + ",file=\"" + tmpFile.getAbsolutePath().replace("\\", "/") + "\") ");
			connection.eval("rm(list = ls(all = TRUE));");

			connection.close();

			//spawn a new R process
			if (isWindowsPlatform()) {
				Runtime.getRuntime().exec("Rgui " + tmpFile.getAbsolutePath());
			} else if (isMacOSPlatform()) {

				// transfer the file to the local computer
				if (!RUtils.getHost().equals("localhost")) {
					// create another local file  and transfer the workspace file from the rserver
					REXP xp = connection.parseAndEval("r=readBin('" + tmpFile.getAbsolutePath() + "','raw',2024*2024); unlink('" + tmpFile.getAbsolutePath() + "'); r");
					FileOutputStream oo = new FileOutputStream(tmpFile);
					oo.write(xp.asBytes());
					oo.close();
				}

				Runtime.getRuntime().exec("open -n -a /Applications/R.app/ " + tmpFile.getAbsolutePath());
			} else { // linux and the rest of the world
				Runtime.getRuntime().exec("R " + tmpFile.getAbsolutePath());
			}

			tmpFile.deleteOnExit();

		} catch (Throwable e) {
			throw new RuntimeException("spawning of R-process failed", e);
		}
	}


	private boolean isMacOSPlatform() {
		return (System.getProperty("mrj.version") != null);
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
