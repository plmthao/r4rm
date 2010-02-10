package de.mpicbg.tds.rm.rplugin.parameterized;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeAttribute;
import com.rapidminer.parameter.ParameterTypeText;
import com.rapidminer.parameter.UndefinedParameterError;
import de.mpicbg.tds.rm.rplugin.operators.RPlotOperator;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class LatticePlotOperator extends RPlotOperator {

	public String NOMINAL_ATTRIBUTE = "Group by";
	public String READOUT_ATTRIBUTE = "Readout";

	String script = "par(oma=c(6,1,1,1));\n" +
			"boxplot(READOUT_ATTRIBUTE~NOMINAL_ATTRIBUTE, data = inA, las=2);\n" +
			"title(main = \"READOUT_ATTRIBUTE grouped by NOMINAL_ATTRIBUTE\");";


	public LatticePlotOperator(OperatorDescription description) {
		super(description);
	}


	protected String getScript() throws UndefinedParameterError {

		String plotScript = super.getScript();


		plotScript = plotScript.replace("NOMINAL_ATTRIBUTE", getParameter(NOMINAL_ATTRIBUTE));
		plotScript = plotScript.replace("READOUT_ATTRIBUTE", getParameter(READOUT_ATTRIBUTE));

		return plotScript;
	}


	@Override
	public java.util.List<ParameterType> getParameterTypes() {
		java.util.List<ParameterType> types = super.getParameterTypes();

		types.add(new ParameterTypeAttribute(NOMINAL_ATTRIBUTE, "The target attribute", exampleSetInput, false));
		types.add(new ParameterTypeAttribute(READOUT_ATTRIBUTE, "The readout to be investigated", exampleSetInput, false));

		// set the default script for the operator
		ParameterTypeText scriptPar = (ParameterTypeText) types.get(0);
		scriptPar.setDefaultValue(script);
		scriptPar.setExpert(true);

		return types;
	}
}