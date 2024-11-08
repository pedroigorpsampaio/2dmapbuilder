package model;

import java.util.ArrayList;

/**
 * Class that represents a project
 * created in 2D map builder
 * containing the necessary information
 * 
 * @author 	Pedro Sampaio
 * @since	1.2b
 *
 */
public class Project {

	private Map map;						// the map with all layers and tiles information
	private ArrayList<Tileset> tilesets; 	// the list of tilesets contained in the project
	private int tileSize;					// the size of the map's tile
	private int mapSizeX;					// number of tiles in map on x-axis (columns)
	private int mapSizeY;					// number of tiles in map on y-axis (lines)
	
	private boolean saved;					// if the project is up to date with the saved file
	private String[] saveInfo;				// save info from last save to quick save feature
	
	/**
	 * Constructor for project class
	 * Receives all informations ready
	 * 
	 * @param map		the map with all layers and tiles information
	 * @param tilesets	the list of tilesets contained in the project
	 * @param tileSize	the size of the map's tile
	 * @param mapSizeX	number of tiles in map on x-axis (columns)
	 * @param mapSizeY	number of tiles in map on y-axis (lines)
	 */
	public Project(Map map, ArrayList<Tileset> tilesets, int tileSize, int mapSizeX, int mapSizeY) {
		this.map = map;
		this.tilesets = tilesets;
		this.tileSize = tileSize;
		this.mapSizeX = mapSizeX;
		this.mapSizeY = mapSizeY;
	}
	
	/**
	 * Constructor for new projects
	 * Receives informations and create tileset list
	 * adding the tileset received in parameter
	 * 
	 * @param tileset	the single tile set to create project with
	 * @param tileSize	the size of the map's tile
	 * @param mapSizeX	number of tiles in map on x-axis (columns)
	 * @param mapSizeY	number of tiles in map on y-axis (lines)
	 */
	public Project(Tileset tileset, int tileSize, int mapSizeX, int mapSizeY) {
		this.tileSize = tileSize;
		this.mapSizeX = mapSizeX;
		this.mapSizeY = mapSizeY;
		tilesets = new ArrayList<>();
		tilesets.add(tileset);
	}

	/**
	 * Constructor for projects
	 * that are loaded from save files
	 */
	public Project() {
		// information will be loaded from save file
	}

	/**
	 * @return the map with all layers and tiles information
	 */
	public Map getMap() {
		if(map == null)
			map = new Map();
		return map;
	}

	/**
	 * @param map the map with all layers and tiles information to set
	 */
	public void setMap(Map map) {
		this.map = map;
	}

	/**
	 * @return the list of tilesets contained in the project
	 */
	public ArrayList<Tileset> getTilesets() {
		return tilesets;
	}

	/**
	 * @param tilesets the list of tilesets contained in the project
	 */
	public void setTilesets(ArrayList<Tileset> tilesets) {
		this.tilesets = tilesets;
	}

	/**
	 * @return the size of the map's tile
	 */
	public int getTileSize() {
		return tileSize;
	}

	/**
	 * @param tileSize the size of the map's tile
	 */
	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	/**
	 * @return the number of tiles in map on x-axis (columns)
	 */
	public int getMapSizeX() {
		return mapSizeX;
	}

	/**
	 * @param mapSizeX number of tiles in map on x-axis (columns)
	 */
	public void setMapSizeX(int mapSizeX) {
		this.mapSizeX = mapSizeX;
	}

	/**
	 * @return the number of tiles in map on y-axis (lines)
	 */
	public int getMapSizeY() {
		return mapSizeY;
	}

	/**
	 * @param mapSizeY the number of tiles in map on y-axis (lines)
	 */
	public void setMapSizeY(int mapSizeY) {
		this.mapSizeY = mapSizeY;
	}

	/**
	 * @return if the project is up to date with the saved file
	 */
	public boolean isSaved() {
		return saved;
	}

	/**
	 * @param saved if the project is up to date with the saved file
	 */
	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	/**
	 * @return the save info from this project
	 * <br> str[0] == path <br> str[1] == filename
	 */
	public String[] getSaveInfo() {
		return saveInfo;
	}

	/**
	 * @param saveInfo save info for this project
	 * <br> str[0] == path <br> str[1] == filename
	 */
	public void setSaveInfo(String[] saveInfo) {
		this.saveInfo = saveInfo;
	}
	
}
