package model;

import java.util.ArrayList;

/**
 * Class that represents the map being created in the program.
 * The map is a collection of tiles arranged in a certain way
 * that composes the 2D world in a tile-based game.
 * Considering that we have different layers for map creation,
 * the map is the collection of all layers with all of its tiles
 * 
 * @author	Pedro Sampaio
 * @since	0.3
 *
 */
public class Map {

	private ArrayList<Layer> layers;	// main data of the program: List that contains all the layers that composes the map
	private int selectedLayer;			// current selected layer
	private Collider[][] colliders;		// the colliders of map

	/**
	 * @return the colliders
	 */
	public Collider[][] getColliders() {
		return colliders;
	}

	/**
	 * @param colliders the colliders to set
	 */
	public void setColliders(Collider[][] colliders) {
		this.colliders = colliders;
	}

	/**
	 * Constructor for this class
	 * Creates the first layer of the map
	 * 
	 * @author 	Pedro Sampaio
	 * @since	0.5
	 */
	public Map() {
		// initializes layers
		layers = new ArrayList<Layer>();
		// creates and adds first layer (idx 0) to list of layers (map) with full opacity (1f)
		layers.add(new Layer(0, 1f));
		// initially first layer is selected
		selectedLayer = 0;
		// initialize colliders
		colliders = new Collider[MapConfig.mapSizeY][MapConfig.mapSizeX];
	}
	
	/**
	 * Constructor for this class
	 * For copy, receive all properties via paramater 
	 * 
	 * @author Pedro Sampaio
	 * @param layers 	receives the list of layers that contain all info of the tiles in layers for the map
	 * @param selectedLayer the current selected layer
	 * @param colliders		the map colliders
	 * @since 0.5
	 */
	public Map(ArrayList<Layer> layers, int selectedLayer, Collider[][] colliders) {
		this.layers = layers;
		this.selectedLayer = selectedLayer;
		this.colliders = colliders;
	}

	/**
	 * @return the existing layers of the map
	 */
	public ArrayList<Layer> getLayers() {
		return layers;
	}

	/**
	 * Creates a complete copy of the map
	 * @author  Pedro Sampaio
	 * @since	0.7b
	 * @return	the copy of the map
	 */
	public Map createCopy() {
		ArrayList<Layer> copyLayers = new ArrayList<Layer>();
		for(int i = 0; i < layers.size(); i++) {
			copyLayers.add(new Layer(layers.get(i).createCopy(), layers.get(i).getzIndex(), layers.get(i).getOpacity()));
		}
		
		// makes a hard copy of colliders mask
		Collider[][] colCopy = new Collider[MapConfig.mapSizeY][MapConfig.mapSizeX];
		for(int i = 0; i < colliders.length; i++) {
			for(int j = 0; j < colliders[i].length; j++) {
				if(colliders[i][j] != null)
					colCopy[i][j] = colliders[i][j];
			}
		}
		
		return new Map(copyLayers, selectedLayer, colCopy);
	}

	/**
	 * @return the selectedLayer
	 */
	public int getSelectedLayer() {
		return selectedLayer;
	}

	/**
	 * @param selectedLayer the selectedLayer to set
	 */
	public void setSelectedLayer(int selectedLayer) {
		this.selectedLayer = selectedLayer;
	}
	
	/**
	 * Resize map in all layers with current map size
	 * 
	 * @author Pedro Sampaio
	 * @since  1.1
	 * 
	 */
	public void resizeMap() {
		for(int i = 0; i < layers.size(); i++) {
			layers.get(i).resizeMap();
		}
	}

}
