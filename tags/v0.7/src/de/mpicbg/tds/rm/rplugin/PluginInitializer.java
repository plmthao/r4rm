/**
 *
 */
package de.mpicbg.tds.rm.rplugin;

import com.rapidminer.gui.MainFrame;

import java.io.InputStream;
import java.util.Properties;

/**
 * This class provides hooks for initialization
 *
 * @author Sebastian Land
 */
public class PluginInitializer {

	public static void initGui(MainFrame mainframe) {
	}

	public static InputStream getOperatorStream(ClassLoader loader) {
		return null;
	}

	public static void initPluginManager() {
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
