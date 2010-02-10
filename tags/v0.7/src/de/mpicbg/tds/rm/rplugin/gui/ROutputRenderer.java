package de.mpicbg.tds.rm.rplugin.gui;

import com.rapidminer.operator.IOContainer;
import com.rapidminer.report.Reportable;

import javax.swing.*;
import java.awt.*;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class ROutputRenderer extends com.rapidminer.gui.renderer.AbstractRenderer {

	@Override
	public String getName() {
		return "R Results";
	}


	@Override
	public Component getVisualizationComponent(Object renderable, IOContainer ioContainer) {
		return new JLabel("not yet implemented");
	}


	@Override
	public Reportable createReportable(Object renderable, IOContainer ioContainer, int desiredWidth, int desiredHeight) {
		return null;
	}
}
