/**
 *
 */
package de.mpicbg.tds.rm.rplugin;

import com.rapidminer.RapidMiner;
import com.rapidminer.gui.MainFrame;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeString;

import java.io.InputStream;
import java.util.Properties;

/**
 * This class provides hooks for initialization
 *
 * @author Sebastian Land
 */
public class PluginInitializer {

	public static final String R_SERVE_HOST = "rapidminer.rplugin.host";
	public static final String R_SERVE_HOST_DEFAULT = "localhost";
	public static final String R_SERVE_PORT = "rapidminer.rplugin.port";
	public static final int R_SERVE_PORT_DEFAULT = 6311;

	public static void initGui(MainFrame mainframe) {
	}

	public static InputStream getOperatorStream(ClassLoader loader) {
		return null;
	}

	public static void initPluginManager() {
		System.err.println("test");
		RapidMiner.registerRapidMinerProperty(new ParameterTypeString(R_SERVE_HOST, "Define the host where Rserve is running", R_SERVE_HOST_DEFAULT));
		RapidMiner.registerRapidMinerProperty(new ParameterTypeInt(R_SERVE_PORT, "The port where Rserve is running", 0, Integer.MAX_VALUE, R_SERVE_PORT_DEFAULT));
	}

	public static void initFinalChecks() {
	}

	public static void initSplashTexts() {
	}

	public static void initAboutTexts(Properties aboutBoxProperties) {
	}

	public static Boolean showAboutBox() {
		return true;
	}
}
