package de.mpicbg.tds.rm.rplugin.operators;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.metadata.PassThroughRule;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeText;
import com.rapidminer.parameter.TextType;
import com.rapidminer.parameter.UndefinedParameterError;
import de.mpicbg.tds.rm.rplugin.RImageFactory;
import de.mpicbg.tds.rm.rplugin.RUtils;
import de.mpicbg.tds.rm.rplugin.RViewExampleSet2;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;

import java.awt.*;
import java.util.List;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class RPlotOperator extends Operator {

	public String SCRIPT_PROPERTY = "plot-script";

	protected InputPort exampleSetInput = getInputPorts().createPort("example set input", true);
	protected OutputPort exampleSetOutput = getOutputPorts().createPort("example set output");


	public RPlotOperator(OperatorDescription description) {
		super(description);

		getTransformer().addRule(new PassThroughRule(exampleSetInput, exampleSetOutput, false));
	}


	@Override
	public void doWork() throws OperatorException {
		String script = getScript();

		RConnection connection = null;

		try {
			connection = new RConnection();

			// 1) convert exampleSet ihnto data-frame and put into the r-workspace
			RList inputAsRList = null;
			if (exampleSetInput.isConnected()) {
				ExampleSet exampleSet = exampleSetInput.getData();
				inputAsRList = RUtils.convert2RList(exampleSet);
				connection.assign("inA", REXP.createDataFrame(inputAsRList));
			}

			// 2) create the image the script
			Image image = RImageFactory.createImage(connection, script, 900, 700);

			// close the connection to R
			connection.close();

			// todo finalize this
//            exampleSetOutput.deliver(new RViewExampleSet(image));
			exampleSetOutput.deliver(new RViewExampleSet2(script, inputAsRList));

		} catch (Throwable e) {
			connection.close();
			throw new OperatorException("R script execution failed: " + script, e);
		}

//        dummyPortPairA.passDataThrough();
	}


	protected String getScript() throws UndefinedParameterError {
		return getParameterAsString(SCRIPT_PROPERTY);
	}


	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();

		types.add(new ParameterTypeText(SCRIPT_PROPERTY, "The R-script which should be executed. Inputs are available as inA - inD. Outputs are expected to be named outA-outD.", TextType.PLAIN, false));

		return types;
	}
}