package de.mpicbg.tds.rm.rplugin;

import com.rapidminer.example.set.SimpleExampleSet;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.operator.IOObject;
import com.rapidminer.report.Reportable;

import java.awt.*;
import java.util.Map;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class RPlotIOObject extends SimpleExampleSet implements Reportable {


	private Image image;
	private String script;
	private Map<String, IOObject> pushTable;


	public RPlotIOObject(Image image, String script, Map<String, IOObject> pushTable) {
		super(new MemoryExampleTable());
		this.image = image;
		this.script = script;
		this.pushTable = pushTable;
	}


	public String getScript() {
		return script;
	}


	public Map<String, IOObject> getPushTable() {
		return pushTable;
	}


	public Image getImage() {
		return image;
	}
}