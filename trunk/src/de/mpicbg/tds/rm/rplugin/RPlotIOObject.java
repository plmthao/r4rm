package de.mpicbg.tds.rm.rplugin;

import com.rapidminer.example.set.SimpleExampleSet;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.operator.AbstractIOObject;
import com.rapidminer.operator.IOObject;
import com.rapidminer.report.Reportable;

import java.util.List;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class RPlotIOObject extends SimpleExampleSet implements Reportable {


	private String script;
	private List<IOObject> inputs;


	public RPlotIOObject(String script, List<IOObject> inputs) {
		super(new MemoryExampleTable());
		this.script = script;
		this.inputs = inputs;
	}


	public String getScript() {
		return script;
	}


	public List<IOObject> getInputs() {
		return inputs;
	}
}