package model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Class that represents a tileset to be used in map creation.
 * Contains all necessary informations to store, visualize
 * and use a tileset in order to design a 2D map
 * 
 * @author	Pedro Sampaio
 * @since	0.2
 *
 */
public class Tileset extends Observable {
	
	private int id;				// the id of the tileset in the map project context (based on order of import)
	private int firstID;		// the id of the first tile of this tileset in the map project context
	private int tileCount;		// the number of tiles contained in this tileset
	private int tileSizeX;		// number of tiles in tileset on x-axis (columns) 
	private int tileSizeY;		// number of tiles in tileset on y-axis (lines) 
	private String name;		// the name of the tileset
	private int tileSize;		// the size of tiles in the tileset (square tiles)
	private Image image;		// the source image of the tileset
	private String imagePath;	// the image path for the tileset
	
	private ArrayList<Tile> selectedTiles;		// list of current selected tiles in viewport

	
	
	/**
	 * Class constructor
	 * 
	 * @param name 			desired name for the tileset
	 * @param tileSize 		desired tile size for the tileset
	 * @param image 		desired image for the tileset
	 * @param imagePath 	the path of the image
	 * @param firstID		the id(global - for all tilesets) of the first tile in the tileset
	 */
	public Tileset(String name, int tileSize, Image image, String imagePath, int firstID) {
		this.setName(name);
		this.setTileSize(tileSize);
		this.setImage(image);
		this.setImagePath(imagePath);
		this.setFirstID(firstID);
		
		// never let tilesize be bigger than image 
		if(image.getHeight(null) < tileSize)
			this.tileSize = image.getHeight(null);
		if(image.getWidth(null) < tileSize)
			this.tileSize = image.getWidth(null);
		
		// image cannot be null at this point
		assert(image != null);
		
		// calculates tile with image's width and height info, and the size of a tile
		this.tileSizeX = (int) Math.ceil(image.getWidth(null)/(float)(tileSize));
		this.tileSizeY = (int) Math.ceil(image.getHeight(null)/(float)(tileSize));

		this.tileCount = this.tileSizeX * this.tileSizeY;
		
		selectedTiles = new ArrayList<Tile>(); // initializes list of selected tiles
	}
	
	/**
	 * Class constructor
	 * Receives all informations for cases of tileset loaded from save file
	 * 
	 * @param name 			desired name for the tileset
	 * @param tileSize 		desired tile size for the tileset
	 * @param image 		desired image for the tileset
	 * @param imagePath 	the path of the image
	 * @param firstID		the id(global - for all tilesets) of the first tile in the tileset
	 * @param tilecount		the number of tiles that exists in the tileset
	 */
	public Tileset(String name, int tileSize, Image image, String imagePath, int firstID, int tilecount) {
		this.setName(name);
		this.setTileSize(tileSize);
		this.setImage(image);
		this.setImagePath(imagePath);
		this.setFirstID(firstID);
		this.tileCount = tilecount;
		
		// never let tilesize be bigger than image 
		if(image.getHeight(null) < tileSize)
			this.tileSize = image.getHeight(null);
		if(image.getWidth(null) < tileSize)
			this.tileSize = image.getWidth(null);
		
		// image cannot be null at this point
		assert(image != null);
		
		// calculates tile with image's width and height info, and the size of a tile
		this.tileSizeX = (int) Math.ceil(image.getWidth(null)/(float)(tileSize));
		this.tileSizeY = (int) Math.ceil(image.getHeight(null)/(float)(tileSize));
		
		selectedTiles = new ArrayList<Tile>(); // initializes list of selected tiles
	}
	
	/**
	 * Getters and Setters of object properties
	 */

	/**
	 * @return the id
	 */
	protected int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	protected void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the id of the first tile of this tileset in the map project context
	 */
	public int getFirstID() {
		return firstID;
	}

	/**
	 * @param firstID the firstID to set
	 */
	public void setFirstID(int firstID) {
		this.firstID = firstID;
	}

	/**
	 * @return the number of tiles contained in this tileset
	 */
	public int getTileCount() {
		return tileCount;
	}

	/**
	 * @return the name of the tileset
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the size of tiles in the tileset (square tiles)
	 */
	public int getTileSize() {
		return tileSize;
	}

	/**
	 * @param tileSize the tileSize to set
	 */
	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	/**
	 * @return the source image of the tileset
	 */
	public BufferedImage getImage() {
		return (BufferedImage) image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(Image image) {
		this.image = image;
	}

	/**
	 * @return the number of tiles in tileset on x-axis (columns) 
	 */
	public int getTileSizeX() {
		return tileSizeX;
	}

	/**
	 * @return the number of tiles in tileset on y-axis (lines) 
	 */
	public int getTileSizeY() {
		return tileSizeY;
	}

	/**
	 * @return the selectedTiles list, a list of current selected tiles in viewport
	 */
	public ArrayList<Tile> getSelectedTiles() {
		return selectedTiles;
	}

	/**
	 * @param selectedTiles the selectedTiles to set
	 */
	public void setSelectedTiles(ArrayList<Tile> selectedTiles) {
		this.selectedTiles = selectedTiles;
	}
	
	/**
	 * dispatch changes in selected tiles for observers
	 */
	public void selectedTilesDispatchChanges() {
		setChanged();
		notifyObservers();
	}


	/**
	 * @return the image path of the tileset
	 */
	public String getImagePath() {
		return imagePath;
	}



	/**
	 * @param imagePath the image path for the tileset
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

}
