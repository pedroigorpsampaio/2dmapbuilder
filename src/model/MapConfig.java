package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Observable;

import test.Config;

/**
 * Class that will hold map configurations and informations..
 * These configurations are related to the viewport
 * visualization of the 2D map, so it contains 
 * informations like tile size and map size.
 * It also relates to the cut size of tiles in the
 * tileset since the size of the tile is set in this class
 * As of version 0.5b, contains map camera configurations
 * and mouse position point on map
 * 
 * @author 	Pedro Sampaio
 * @since	0.1
 *
 */
public class MapConfig extends Observable{

	public static int tileSize;			// the size of the map's tile
	public static int mapSizeX;			// number of tiles in map on x-axis (columns)
	public static int mapSizeY;			// number of tiles in map on y-axis (lines)
	public static float preAlpha;		// level of transparency for pre-visualization of selected tiles in map
	public static float zoom;			// map visualization zoom
	private float zoomSpeed = 0.1f;		// map's zoom speed (percentage)
	private int moveY = 0;				// map's current speed on x axis
	private int moveX = 0;				// map's current speed on y axis
	private int speed = 5;				// map's camera speed
	private static float minZoom;	// minimum zoom (maintain min view relative to the tilesize)
	private static float maxZoom;	// maximum zoom (maintain max view relative to the tilesize)
	private int maxLayers = 4;			// maximum number of layers
	private static Point mousePosition;					// the current mouse position on map;
	private static boolean isMouseOnViewport;	// true if mouse is on viewport, false otherwise
	private boolean isMapLoaded;			// represents if there is a map project loaded in programa
	private Project project;				// current project loaded
	
	// mantains only one instance of map config (singleton pattern)
	private static MapConfig instance = null;
	
	protected MapConfig() {
	    // Exists only to defeat instantiation.
	}
	
	/**
	 * @return returns map config instance
	 * creates the instance if does not exist yet
	 */
	public static MapConfig getInstance() {
		if(instance == null)
			instance = new MapConfig();
		
		return instance;
	}

	/**
	 * Sets a default configuration for the map
	 * 
	 * @author	Pedro Sampaio
	 * @since	0.1
	 */
	public static void setDefault() {	
		minZoom = 2;	// minimum zoom (maintain min view relative to the tilesize)
		maxZoom = 5;// maximum zoom (maintain max view relative to the tilesize)
		zoom = minZoom;
		preAlpha = 0.62f;
		MapConfig.setMouseOnViewport(false);
		mousePosition = new Point();
	}
	
	/**
	 * Updates default configuration with
	 * new configuration that is specific to a map
	 * @author Pedro Sampaio
	 * @since 1.2b
	 * @param tilesize		the size of a tile in the map
	 * @param mapSizeXAxis  the number of tiles in the X-axis of map
	 * @param mapSizeYAxis  the number of tiles in the Y-axis of map
	 */
	public static void updateConfig(int tilesize, int mapSizeXAxis, int mapSizeYAxis) {
		tileSize = tilesize;
		mapSizeX = mapSizeXAxis;
		mapSizeY = mapSizeYAxis;
		minZoom = 2;	// minimum zoom (maintain min view relative to the tilesize)
		maxZoom = 5;// maximum zoom (maintain max view relative to the tilesize)
		if(tileSize < 32) // for better performance
			minZoom = 3.2f;
		zoom = minZoom;
	}
	
	/**
	 * Calculates the size of the tile in map 
	 * considering what is current zoom value
	 * 
	 * @author Pedro Sampaio
	 * @return	the size of the tile multiplied by current map zoom
	 * @since	0.5
	 */
	public static int getTileZoomed() {
		return (int) Math.floor(tileSize * MapConfig.zoom);
	}
	
	/**
	 * Creates a map with the received layers, tilesets and colliders
	 * information and returns it. The layers information contains
	 * a string representing the tiles informations for each layer
	 * and each tileset contains the remaining informations needed
	 * to infer from what tileset the tile information is from.
	 * Collider mask contains information about tile physical
	 * and trigger collider that exists in the loaded map.
	 * Sets selected layer as 0 (first layer)
	 * 
	 * @author Pedro Sampaio
	 * @since 1.5
	 * @param layers	the layers of the map to be created containing all tile informations for all layers
	 * @param tilesets	the tilesets of the map containing the remaining data necessary for the map creation
	 * @param colliders the collider mask from the loaded map
	 * @return	the created map with the received information, or null if map creation could not be done correctly
	 */
	public Map createMap(String[] layers, ArrayList<Tileset> tilesets, Collider[][] colliders) {
		
		// debugs broken string
		if(Config.debug) {
			System.out.print("-----------------------\n Map Loading from File \n-----------------------\n");
		}
		
		// the list of layers for the created map;
		ArrayList<Layer> mapLayers = new ArrayList<Layer>();
		// the matrix of tiles for each layer
		Tile[][] tiles;
		int i = 0; int j = 0;
		String[] lines = null;
		String[] tIDs = null;
		// for each layer string received, breaks the string to get the information needed
		for(int l = 0; l < layers.length; l++) {
			// separate the lines from the whole grid string
			lines = layers[l].split("[\r\n]+");
			
			// for matrix initialization, lets see how many columns there is
			tIDs = lines[0].split(",");
			
			// initialize tiles matrix for this layer
			tiles = new Tile[lines.length][tIDs.length];
			
			// iterates for each line existing in the layer grid
			for(i = 0; i < lines.length; i++) {
				// gets the tile IDs from the current line i
				tIDs = lines[i].split(",");
				// for each tID found, creates a new tile
				for(j = 0; j < tIDs.length; j++) {
					// informations of the tile
					int tileID = Integer.parseInt(tIDs[j]); // the global id of the tile
					
					// if tile id is 0 we dont need to create
					// a tile, since it does not exist
					if(tileID == 0)
						continue;
					
					int tMatrixI = i;	// the i-index of the tile in the map grid
					int tMatrixJ = j; 	// the j-index of the tile in the map grid
					// information we still need to find
					Tileset tileTS = null;	// the tileset for the tile
					int tIndexI = 0;		// the i-index of the tile in the tileset
					int tIndexJ = 0;		// the j-index of the tile in the tileset

					// lets search through the tilesets to find the remaining info needed
					for(int k = 0; k < tilesets.size(); k++) {		
						int tileSizeX = tilesets.get(k).getTileSizeX();	// the number of tiles of the tileset in x-axis
						int tileCount = tilesets.get(k).getTileCount(); // the number of tiles in the tileset
						int firstID = tilesets.get(k).getFirstID();  // the firstID of the tileset
						
						// tile belongs to the current tileset if tileID
						// is within tilesets ID limits [firstID, firstID+tileCount[
						if(tileID >= firstID && tileID < (firstID + tileCount)) {
							tileTS = tilesets.get(k); // stores tile tileset
							// calculates the index I of tile in tileset
							tIndexI = (tileID - firstID) / tileSizeX;
							tIndexJ = (tileID - firstID) % tileSizeX;
							// done finding needed info
							break;
						}

					}
					
					// could not find a tile tileset, abort map creation 
					if(tileTS == null) {
						// debugs the problematic tile
						if(Config.debug) {
							System.out.println("\nCould not find tileset of tileID: "+tileID);
						}
						return null;
					}
					
					// now that we have all information, lets create the tile and add to the matrix of tiles
					tiles[tMatrixI][tMatrixJ] = new Tile(tIndexI, tIndexJ, tileTS, tileID, tMatrixI, tMatrixJ);
					
					// debugs broken string
					if(Config.debug) {
						System.out.print(tIDs[j]+",");
					}
				}
				// debugs broken string
				if(Config.debug) {
					System.out.print("\n");
				}
			}		
			
			// creates layer that will contain the tiles for the current
			// layer and adds to the list of map layers
			mapLayers.add(new Layer(tiles, l, 1));

			// debugs broken string
			if(Config.debug) {
				System.out.print("\n");
			}
		}

		// return the new map created with all information
		// (selected layer is set as the first one - 0)
		return new Map(mapLayers, 0, colliders);
	}
	
	/**
	 * Gets mouse position based on the tiles in map
	 * @param scrollPanePos the current SrollPane position for relative calculations
	 * @return Point containing the converted mouse position
	 */
	public static Point getMouseTilePosition(Point scrollPanePos) {
		if(isMouseOnViewport) {
			return new Point((int)(Math.floor((mousePosition.y + scrollPanePos.y) / getTileZoomed())),
								(int)(Math.floor((mousePosition.x + scrollPanePos.x) / getTileZoomed())));
		}
		else
			return null;
	}
	
	/**
	 * dispatch changes in the map config for observers
	 * 
	 * @author	Pedro Sampaio
	 * @param	zoom	if its a zoom change that triggered the update
	 * @since	0.5
	 */
	public void dispatchChanges(boolean zoom) {
		setChanged();
		notifyObservers(zoom);
	}

	/**
	 * @return the moveY
	 */
	public int getMoveY() {
		return moveY;
	}

	/**
	 * @param moveY the moveY to set
	 */
	public void setMoveY(int moveY) {
		this.moveY = moveY;
	}

	/**
	 * @return the moveX
	 */
	public int getMoveX() {
		return moveX;
	}

	/**
	 * @param moveX the moveX to set
	 */
	public void setMoveX(int moveX) {
		this.moveX = moveX;
	}

	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	/**
	 * @return the maxZoom
	 */
	public float getMaxZoom() {
		return maxZoom;
	}

	/**
	 * @param maxZ the maxZoom to set
	 */
	public void setMaxZoom(float maxZ) {
		maxZoom = maxZ;
	}

	/**
	 * @return the minZoom
	 */
	public float getMinZoom() {
		return minZoom;
	}

	/**
	 * @param minZ the minZoom to set
	 */
	public void setMinZoom(float minZ) {
		minZoom = minZ;
	}

	/**
	 * @return the maxLayers
	 */
	public int getMaxLayers() {
		return maxLayers;
	}

	/**
	 * @param maxLayers the maxLayers to set
	 */
	public void setMaxLayers(int maxLayers) {
		this.maxLayers = maxLayers;
	}

	/**
	 * @return the mousePosition
	 */
	public static Point getMousePosition() {
		return mousePosition;
	}

	/**
	 * @param mousePosition the mousePosition to set
	 */
	public static void setMousePosition(Point mousePosition) {
		MapConfig.mousePosition = mousePosition;
	}

	/**
	 * @return the isMouseOnViewport
	 */
	public static boolean isMouseOnViewport() {
		return isMouseOnViewport;
	}

	/**
	 * @param isMouseOnViewport the isMouseOnViewport to set
	 */
	public static void setMouseOnViewport(boolean isMouseOnViewport) {
		MapConfig.isMouseOnViewport = isMouseOnViewport;
	}

	/**
	 * @return the zoomInSpeed
	 */
	public float getZoomInSpeed() {
		return (1+zoomSpeed);
	}

	/**
	 * @return the zoomOutSpeed
	 */
	public float getZoomOutSpeed() {
		return (1-zoomSpeed);
	}

	/**
	 * @return if a map project is loaded in program
	 */
	public boolean isMapLoaded() {
		return isMapLoaded;
	}

	/**
	 * @param isMapLoaded the boolean that represents if a map is loaded in program
	 */
	public void setMapLoaded(boolean isMapLoaded) {
		this.isMapLoaded = isMapLoaded;
	}

	/**
	 * @return the project currently loaded in program
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param project sets the current project of the program
	 */
	public void setProject(Project project) {
		this.project = project;
	}

}
