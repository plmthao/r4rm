package de.mpicbg.tds.rm.rplugin;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import java.awt.*;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class RImageFactory {

    public static Image createImage(RConnection connection, String script, int width, int height) throws REngineException, RserveException, REXPMismatchException {
        String device = "jpeg"; // device we'll call (this would work with pretty much any bitmap device)

        String tempFileName = "rmPlotFile." + device;
        REXP xp = connection.parseAndEval("try(" + device + "('" + tempFileName + "',quality=95, width = " + width + ", height = " + height + "))");

        if (xp.inherits("try-error")) { // if the result is of the class try-error then there was a problem
            System.err.println("Can't open " + device + " graphics device:\n" + xp.asString());
            // this is analogous to 'warnings', but for us it's sufficient to get just the 1st warning
            REXP w = connection.eval("if (exists('last.warning') && length(last.warning)>0) names(last.warning)[1] else 0");
            if (w.isString()) System.err.println(w.asString());
            return null;
        }

        // ok, so the device should be fine - let's plot - replace this by any plotting code you desire ...
        String preparedScript = RUtils.prepare4RExecution(script);

        String[] splitScript = preparedScript.split(";");
        for (String s : splitScript) {
            try {
                connection.parseAndEval(RUtils.prepare4RExecution(s + ";"));
            } catch (REngineException e) {
                throw new RuntimeException("Error while executing line: " + s);
            }
        }
//        connection.parseAndEval(preparedScript);

        // close the image
        connection.parseAndEval("dev.off();");

        // There is no I/O API in REngine because it's actually more efficient to use R for this
        // we limit the file size to 1MB which should be sufficient and we delete the file as well
        xp = connection.parseAndEval("r=readBin('" + tempFileName + "','raw',2024*2024); unlink('" + tempFileName + "'); r");

        // now this is pretty boring AWT stuff - create an image from the data and display it ...
        return Toolkit.getDefaultToolkit().createImage(xp.asBytes());
    }
}
