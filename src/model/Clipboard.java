package model;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Class that represents the clipboard:
 * the user's current copied tiles
 * @author Pedro Sampaio
 * @since  1.0b
 *
 */
public class Clipboard extends Observable{

/**
 * Pattern:
 * Observer,
 * Singleton
 */
	
	private ArrayList<Tile> copiedTiles;		// 	the list containing current tiles copied by user
	private boolean paste;						//	boolean to represent if paste is wanted
	
	// mantains only one instance of map config (singleton pattern)
	private static Clipboard instance = null;
	
	protected Clipboard() {
	    // defeats instantiation.
		copiedTiles = new ArrayList<Tile>();	// initializes copied tiles tiles list
		paste = false;
	}
	
	/**
	 * @return returns Clipboard instance
	 * Creates the instance if does not exist yet
	 */
	public static Clipboard getInstance() {
		if(instance == null)
			instance = new Clipboard();
		
		return instance;
	}

	/**
	 * @return the copiedTiles list containing current tiles copied by user
	 */
	public ArrayList<Tile> getCopiedTiles() {
		return copiedTiles;
	}

	/**
	 * @param copiedTiles the copiedTiles to set
	 */
	public void setCopiedTiles(ArrayList<Tile> copiedTiles) {
		this.copiedTiles = copiedTiles;
	}

	/**
	 * @return the paste
	 */
	public boolean isPaste() {
		return paste;
	}

	/**
	 * @param paste the paste to set
	 */
	public void setPaste(boolean paste) {
		this.paste = paste;
	}
	
	/**
	 * dispatch changes in the clipboard for observers
	 * 
	 * @author	Pedro Sampaio
	 * @since	1.0b
	 */
	public void dispatchChanges() {
		setChanged();
		notifyObservers();
	}

}
