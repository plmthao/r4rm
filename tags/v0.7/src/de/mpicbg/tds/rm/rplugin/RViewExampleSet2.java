package de.mpicbg.tds.rm.rplugin;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.set.SimpleExampleSet;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.example.table.MemoryExampleTable;
import org.rosuda.REngine.RList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class RViewExampleSet2 extends SimpleExampleSet {


	private String script;
	private RList rlist;


	public RViewExampleSet2(String script, RList exampleSet) {
		this(new MemoryExampleTable(new ArrayList<Attribute>()));

		this.script = script;
		this.rlist = exampleSet;
	}


	public RViewExampleSet2(ExampleTable exampleTable) {
		super(exampleTable);
	}


	public RViewExampleSet2(ExampleTable exampleTable, List<Attribute> regularAttributes) {
		super(exampleTable, regularAttributes);
	}


	public RViewExampleSet2(ExampleTable exampleTable, Map<Attribute, String> specialAttributes) {
		super(exampleTable, specialAttributes);
	}


	public RViewExampleSet2(ExampleTable exampleTable, List<Attribute> regularAttributes, Map<Attribute, String> specialAttributes) {
		super(exampleTable, regularAttributes, specialAttributes);
	}


	public RViewExampleSet2(SimpleExampleSet exampleSet) {
		super(exampleSet);
	}


	public String getScript() {
		return script;
	}


	public RList getRList() {
		return rlist;
	}
}