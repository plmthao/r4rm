package de.mpicbg.tds.rm.rplugin.operators;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPortExtender;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeText;
import com.rapidminer.parameter.TextType;
import de.mpicbg.tds.rm.rplugin.RUtils;
import org.rosuda.REngine.Rserve.RConnection;

import java.util.List;


/**
 * Document me!
 * <p/>
 * todo use PCA-class/operator as example
 *
 * @author Holger Brandl
 */
public class RSnippetOperator extends Operator {

	public String SCRIPT_PROPERTY = "R-Script";

//    private PortPairExtender dummyPortPairA = new DummyPortPairExtender("inputA", getInputPorts(), getOutputPorts());

	public RConnection connection;

	private OutputPort exampleSetOutput = getOutputPorts().createPort("example set output");

	private InputPortExtender inputs = new InputPortExtender("input table", getInputPorts());
	// todo add a few more inputs
//    private OutputPort originalOutput = getOutputPorts().createPort("original");
//    private OutputPort modelOutput = getOutputPorts().createPort("preprocessing model");


	public RSnippetOperator(OperatorDescription description) {

		super(description);

		inputs.start();
//		getTransformer().addRule(new PassThroughRule(inputs.getManagedPorts().get(0), exampleSetOutput, false));
//        getTransformer().addRule(new PassThroughRule(exampleSetInput, originalOutput, false));
//        getTransformer().addRule(dummyPortPairA.makePassThroughRule());
	}


	@Override
	public void doWork() throws OperatorException {
		String script = getParameterAsString(SCRIPT_PROPERTY);

		try {
			connection = RUtils.createConnection();

			// 1) convert exampleSet ihnto data-frame and put into the r-workspace
			RUtils.push2R(connection, inputs.getData(true));


			// 2) run the script  (remove all linebreaks and other no space whitespace-characters
			connection.eval(RUtils.prepare4RExecution(script));

			// 3) extract output data-frame from R
			ExampleSet outA = RUtils.convert2ExSet(connection.eval("outA"));

			connection.eval("rm(list = ls(all = TRUE));");
			connection.close();

			exampleSetOutput.deliver(outA);

		} catch (Throwable e) {
			connection.close();
			throw new OperatorException("R script execution failed: " + script, e);
		}

//        dummyPortPairA.passDataThrough();
	}


	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();

		ParameterTypeText parScript = new ParameterTypeText(SCRIPT_PROPERTY, "The R-script which should be executed. Inputs are available as inA - inD. Outputs are expected to be named outA-outD.", TextType.PLAIN, "outA = inA;");
		parScript.setExpert(false);
		types.add(parScript);

		return types;
	}
}