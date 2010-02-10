package de.mpicbg.tds.rm.rplugin.scratchpad;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class RTest {

	public static void main(String args[]) throws REXPMismatchException, RserveException {
		RConnection connection = new RConnection();
		connection.assign("asdf123", "5");
		double[] doubles = connection.eval("rnorm(10)").asDoubles();
		System.err.println("double are " + doubles.toString());

		// stop here as r-session is not yet working
		if (true)
			return;
//
//        Rsession s = Rsession.newLocalInstance(System.out, null);
//        double[] rand = s.eval("rnorm(10)").asDoubles(); //create java variable from R command
//
//
//        s.set("c", Math.random()); //create R variable from java one
//
//        s.save(new File("save.Rdata"), "c"); //save variables in .Rdata
//        s.rm("c"); //delete variable in R environment
//        s.load(new File("save.Rdata")); //load R variable from .Rdata
//
//
//        s.set("df", new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}}, "x1", "x2", "x3"); //create data frame from given vectors
//        double value = (Double) (cast(s.eval("df$x1[3]"))); //access one value in data frame
//
//
//        s.toJPEG(new File("plot.jpg"), 400, 400, "plot(rnorm(10))"); //create jpeg file from R graphical command (like plot)
//
//        String html = s.asHTML("summary(rnorm(100))"); //format in html using R2HTML
//        System.out.println(html);
//
//        String txt = s.asString("summary(rnorm(100))"); //format in text
//        System.out.println(txt);
//
//
//        System.out.println(s.installPackage("sensitivity", true)); //install and load R package
//        System.out.println(s.installPackage("wavelets", true));
//
//        s.end();
	}
}
