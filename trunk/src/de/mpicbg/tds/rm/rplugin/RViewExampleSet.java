package de.mpicbg.tds.rm.rplugin;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.set.SimpleExampleSet;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.example.table.MemoryExampleTable;

import java.awt.*;
import java.util.List;
import java.util.Map;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class RViewExampleSet extends SimpleExampleSet {


	private Image image;


	public RViewExampleSet(Image img) {
		this(new MemoryExampleTable());
		image = img;
	}


	public RViewExampleSet(ExampleTable exampleTable) {
		super(exampleTable);
	}


	public RViewExampleSet(ExampleTable exampleTable, List<Attribute> regularAttributes) {
		super(exampleTable, regularAttributes);
	}


	public RViewExampleSet(ExampleTable exampleTable, Map<Attribute, String> specialAttributes) {
		super(exampleTable, specialAttributes);
	}


	public RViewExampleSet(ExampleTable exampleTable, List<Attribute> regularAttributes, Map<Attribute, String> specialAttributes) {
		super(exampleTable, regularAttributes, specialAttributes);
	}


	public RViewExampleSet(SimpleExampleSet exampleSet) {
		super(exampleSet);
	}


	public Image getImage() {
		return image;
	}
}
