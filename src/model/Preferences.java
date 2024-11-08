package model;

import java.awt.Color;
import java.util.Observable;

/**
 * Class that will hold all user preferences.
 * Useful for toggling grid lines, 
 * choosing background map color
 * and other configurations 
 * 
 * @author 	Pedro Sampaio
 * @since	0.1
 *
 */
public class Preferences extends Observable{

	public static float viewportMouseSensitivityX;		// viewport's mouse sensitivity on x-axis
	public static float viewportMouseSensitivityY;		// viewport's mouse sensitivity on y-axis
	public static Color viewportBackgroundColor;		// viewport's tile background color
	public static Color selectionColor;					// tileset viewport's selection color
	public static Color mapSelectionColor;				// map's viewport selection color
	public static boolean viewportShowGrid;				// option to show or hide grid lines in viewport
	
	// mantains only one instance of preferences (singleton pattern)
	private static Preferences instance = null;
	
	protected Preferences() {
	    // Exists only to defeat instantiation.
	}
	
	/**
	 * @return returns map config instance
	 * creates the instance if does not exist yet
	 */
	public static Preferences getInstance() {
		if(instance == null)
			instance = new Preferences();
		
		return instance;
	}

	/**
	 * Sets a default configuration for the preferences
	 * 
	 * @author	Pedro Sampaio
	 * @since	0.1
	 */
	public static void setDefault() {	
		viewportMouseSensitivityX = 5f;
		viewportMouseSensitivityY = 5f;
		viewportBackgroundColor = Color.GRAY;
		selectionColor = Color.BLUE;
		mapSelectionColor = new Color(0.25f, 0.66f, 1f);
		viewportShowGrid = true;
	}
	
	public void dispatchChanges() {
		setChanged();
		notifyObservers();
	}
}
