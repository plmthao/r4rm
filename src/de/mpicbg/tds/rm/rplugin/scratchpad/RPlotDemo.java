package de.mpicbg.tds.rm.rplugin.scratchpad;
// RPlotDemo demo - REngine and graphics
//
// $Id: RPlotDemo.java 2767 2007-05-24 16:49:25Z urbanek $
//

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Random;


/**
 * A demonstration of the use of Rserver and graphics devices to create graphics in R, pull them into Java and display
 * them. It is a really simple demo.
 */
public class RPlotDemo extends Canvas {

	public static Random random = new Random();


	public static void main(String args[], String cmd) throws RserveException {
		plotImage(cmd, null, new RConnection());
	}


	public static void plotImage(String cmd, String plotTitle, RConnection c) {
		try {
			String device = "jpeg"; // device we'll call (this would work with pretty much any bitmap device)

			// connect to Rserve (if the user specified a server at the command line, use it, otherwise connect locally)

			// if Cairo is installed, we can get much nicer graphics, so try to load it
//            if (c.parseAndEval("suppressWarnings(require('Cairo',quietly=TRUE))").asInteger() > 0)
//                device = "CairoJPEG"; // great, we can use Cairo device
//            else
//                System.out.println("(consider installing Cairo package for better bitmap output)");

			// we are careful here - not all R binaries support jpeg
			// so we rather capture any failures
//            REXP xp = c.parseAndEval("try(" + device + "('test.jpg',quality=90))");
			File imageFile = File.createTempFile("rplot", device);
			String fileName = imageFile.getName();
			new File(fileName).deleteOnExit();

			REXP xp = c.parseAndEval("try(" + device + "('" + fileName + "',quality=95))");

			if (xp.inherits("try-error")) { // if the result is of the class try-error then there was a problem
				System.err.println("Can't open " + device + " graphics device:\n" + xp.asString());
				// this is analogous to 'warnings', but for us it's sufficient to get just the 1st warning
				REXP w = c.eval("if (exists('last.warning') && length(last.warning)>0) names(last.warning)[1] else 0");
				if (w.isString()) System.err.println(w.asString());
				return;
			}

			// ok, so the device should be fine - let's plot - replace this by any plotting code you desire ...
			c.parseAndEval(cmd + "; dev.off()");
//            c.parseAndEval("data(iris); attach(iris); plot(Sepal.Length, Petal.Length, col=unclass(Species)); dev.off()");

			// There is no I/O API in REngine because it's actually more efficient to use R for this
			// we limit the file size to 1MB which should be sufficient and we delete the file as well
			xp = c.parseAndEval("r=readBin('" + fileName + "','raw',1024*1024); unlink('" + fileName + "'); r");

			// now this is pretty boring AWT stuff - create an image from the data and display it ...
			Image img = Toolkit.getDefaultToolkit().createImage(xp.asBytes());

			Frame f = new Frame(plotTitle != null ? plotTitle : "R plot");
			f.add(new RPlotDemo(img));
			f.addWindowListener(new WindowAdapter() { // just so we can close the window


				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			f.pack();
			f.setVisible(true);

		} catch (RserveException rse) { // RserveException (transport layer - e.g. Rserve is not running)
			System.out.println(rse);
		} catch (REXPMismatchException mme) { // REXP mismatch exception (we got something we didn't think we get)
			System.out.println(mme);
			mme.printStackTrace();
		} catch (Exception e) { // something else
			System.out.println("Something went wrong, but it's not the Rserve: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}


	Image img;


	public RPlotDemo(Image img) {
		this.img = img;
		MediaTracker mediaTracker = new MediaTracker(this);
		mediaTracker.addImage(img, 0);
		try {
			mediaTracker.waitForID(0);
		} catch (InterruptedException ie) {
			System.err.println(ie);
			System.exit(1);
		}
		setSize(img.getWidth(null), img.getHeight(null));
	}


	public void paint(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}
}
