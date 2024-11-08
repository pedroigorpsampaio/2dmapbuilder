package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Observable;

import controller.KeyboardControl;
import controller.ViewMapControl;
import test.Config;

/**
 * 
 * Class that contains tool-related methods
 * and properties
 * 
 * @author	Pedro Sampaio
 * @since	0.3
 *
 */
public class Tool extends Observable {

	/**
	 * Enum for available tools of map creation
	 * 
	 * @author 	Pedro Sampaio
	 * @since	0.3
	 */
	public enum SelectTools {BRUSH, ERASER, SELECTION, COLLIDER, NONE}
	
	private SelectTools currentTool;	// current selected tool

	private Point eraseTilePoint;			// current tile below mouse (for erase tool preview)
	
	private boolean trigger;			// player selected trigger for collider tool?

	/**
	 * @return the trigger
	 */
	public boolean isTrigger() {
		return trigger;
	}

	/**
	 * @param trigger the trigger to set
	 */
	public void setTrigger(boolean trigger) {
		this.trigger = trigger;
	}
	
	/**
	 * Updates trigger state based
	 * on user checkbox input
	 */
	public void updateTrigger() {
		if(trigger == false)
			trigger = true;
		else
			trigger = false;
	}

	// mantains only one instance of tool class (singleton pattern)
	private static Tool instance = null;

	protected Tool() {
		// Exists only to defeat instantiation.
		currentTool = SelectTools.BRUSH;			// starts with brush tool selected
		eraseTilePoint = new Point(-1000,-1000);	// initializes offscreen erase tile point anchor
		trigger = false; // initially trigger is false
	}

	/**
	 * @return returns tool instance
	 * creates the instance if does not exist yet
	 */
	public static Tool getInstance() {
		if(instance == null)
			instance = new Tool();

		return instance;
	}

	/**
	 * Adds all selected tiles in tileset 
	 * in the 2d map in the corresponding layer.
	 * 
	 * 
	 * @author	Pedro Sampaio
	 * @param 	relativePoint 	the relative position of the mouse point
	 * @param	mapStates		the states of the map
	 * @param   tileset			the current tileset origin of the tiles selected
	 * @param	hover			if is a click or just a mouse hover on point
	 * @since	0.5
	 * 
	 */
	public static void brushTiles(Point relativePoint, MapState mapStates, Tileset tileset, boolean hover) {
		// selected tiles
		ArrayList<Tile> tsSelTiles = tileset.getSelectedTiles();    	

		// if there are no selected tiles there is no need to brush
		if(tileset.getSelectedTiles().isEmpty()) {
			return;
		}          

		// get coords in tileset of first tile to be the anchor of positioning
		Point tileAnchor = new Point(tsSelTiles.get(0).getIndexJ(),tsSelTiles.get(0).getIndexI());

		// gets tile coords relative to the click scroll-relative position
		Point tileClicked	= new Point(relativePoint.x / MapConfig.getTileZoomed(), 
				relativePoint.y /  MapConfig.getTileZoomed());

		// gets map in current state (creates a copy to add to state list)
		Map currentMap = mapStates.getCurrentMap().createCopy();

		// current map copy must not be the same object as the original
		assert(!currentMap.equals(mapStates.getCurrentMap()));

		// gets layers of current map state
		ArrayList<Layer> layers = currentMap.getLayers();

		// current map copy layers must not be the same object as the original
		assert(!layers.equals(mapStates.getCurrentMap().getLayers()));

		// current map copy tiles must not be the same object as the original
		assert(!layers.get(0).getTiles().equals(mapStates.getCurrentMap().getLayers().get(0).getTiles()));


		// iterates through selected tiles positioning them
		// in their respective positions anchored by the first tile in selection
		for(int i = 0; i < tsSelTiles.size(); i++) {
			// the position after using top left tile as anchor
			Point offsetPos = new Point (
					tileClicked.x - (tileAnchor.x - tsSelTiles.get(i).getIndexJ()),
					tileClicked.y - (tileAnchor.y - tsSelTiles.get(i).getIndexI())
					);         		

			// create tile to add in a layer of the map in the current state
			Tile newTile = new Tile(tsSelTiles.get(i).getIndexI(), tsSelTiles.get(i).getIndexJ(), tileset);
			// adds map draw coords to complete necessary information for pre-visualiazing selected tiles
			newTile.setDrawI(offsetPos.y); newTile.setDrawJ(offsetPos.x); newTile.setComplete(true);
			// updates list with all needed info
			tsSelTiles.set(i, newTile);

			// if tile is out of world bounds, do not add it to the map
			if(offsetPos.x < 0 || offsetPos.y < 0 || offsetPos.x > 
			(MapConfig.mapSizeX-1) || offsetPos.y > (MapConfig.mapSizeY-1))
				continue;

			// if brush is for hover visualization, dont add selection to data
			if(hover)
				continue;

			// flag that represents if a layer with a free spot was found for the tile
			boolean foundLayer = false;

			// finds layer of the tile (if its on top of another tile, creates a new layer)
			// iterating ascending through layers until find a empty spot for the tile
			// creating new layer if needed
			for(int j = 0; j < layers.size(); j++) {
				// get tiles from the iteration layer
				Tile[][] iterTiles = layers.get(j).getTiles();

				// if position is free on layer tiles, found a layer for the tile
				if(iterTiles[offsetPos.y][offsetPos.x] == null) {
					// adds tile to the free position
					iterTiles[offsetPos.y][offsetPos.x] = newTile;
					// updates found layer bool to true
					foundLayer = true;

					// breaks search for spot
					break;
				}
			}

			// if layer wasnt found in existing ones, creates another one (if max layers not exceeded)
			if(!foundLayer) {
				if(layers.size() < MapConfig.getInstance().getMaxLayers()) {
					// creates new layer on top of others (last pos of layers list)
					Layer newLayer = new Layer(layers.size(), 1f);
					// adds tile to the new layers tiles matrix
					newLayer.getTiles()[offsetPos.y][offsetPos.x] = newTile;
					// adds layers to the list of layers in the map
					layers.add(newLayer);

					if(Config.debug)
						System.out.println("Map: Layer"+layers.size()+" created for tile: "+offsetPos);
				}
				else { // removes top layer object and puts new object
					Layer topLayer = layers.get(layers.size()-1);
					topLayer.getTiles()[offsetPos.y][offsetPos.x] = newTile;
				}
			}


			// adds new state to the map states if its time to save (mouse released)
			if(ViewMapControl.saveState) {
				mapStates.AddState(currentMap); // addstate already notify observers
				ViewMapControl.saveState = false;
			} // if its not time to save
			else // updates current state (update state method notify observers already)
				mapStates.UpdateState(currentMap);

		}

	}   

	/**
	 * Adds all copied tiles from clipboard 
	 * to the 2d map in the corresponding layer.
	 * 
	 * 
	 * @author	Pedro Sampaio
	 * @param 	relativePoint 	the relative position of the mouse point
	 * @param	mapStates		the states of the map
	 * @param   copiedTiles		the current copied tiles present in clipboard
	 * @param	hover			if is a click or just a mouse hover on point(for preview)
	 * @since	1.0b
	 * 
	 */
	public static void brushTiles(Point relativePoint, MapState mapStates, ArrayList<Tile> copiedTiles, boolean hover) {

		// selected tiles
		ArrayList<Tile> tsSelTiles = copiedTiles;

		// if there are no selected tiles there is no need to brush
		if(copiedTiles.isEmpty()) {
			return;
		}          

		// get coords in tileset of first tile to be the anchor of positioning
		Point tileAnchor = new Point(tsSelTiles.get(0).getMatrixJ(),
									tsSelTiles.get(0).getMatrixI());

		// gets tile coords relative to the click scroll-relative position
		Point tileClicked	= new Point(relativePoint.x / MapConfig.getTileZoomed(), 
				relativePoint.y /  MapConfig.getTileZoomed());

		// gets map in current state (creates a copy to add to state list)
		Map currentMap = mapStates.getCurrentMap().createCopy();

		// current map copy must not be the same object as the original
		assert(!currentMap.equals(mapStates.getCurrentMap()));

		// gets layers of current map state
		ArrayList<Layer> layers = currentMap.getLayers();

		// current map copy layers must not be the same object as the original
		assert(!layers.equals(mapStates.getCurrentMap().getLayers()));

		// current map copy tiles must not be the same object as the original
		assert(!layers.get(0).getTiles().equals(mapStates.getCurrentMap().getLayers().get(0).getTiles()));


		// iterates through selected tiles positioning them
		// in their respective positions anchored by the first tile in selection
		for(int i = 0; i < tsSelTiles.size(); i++) {
			// the position after using top left tile as anchor
			Point offsetPos = new Point (
					tileClicked.x - (tileAnchor.x - tsSelTiles.get(i).getMatrixJ()),
					tileClicked.y - (tileAnchor.y - tsSelTiles.get(i).getMatrixI())
					);         		

			// get tile to add in a layer of the map in the current state
			Tile newTile = tsSelTiles.get(i);
			// adds map draw coords to complete necessary information for pre-visualiazing selected tiles
			newTile.setDrawI(offsetPos.y); newTile.setDrawJ(offsetPos.x); newTile.setComplete(true);
			// updates list with all needed info
			tsSelTiles.set(i, newTile);

			// if tile is out of world bounds, do not add it to the map
			if(offsetPos.x < 0 || offsetPos.y < 0 || offsetPos.x > 
			(MapConfig.mapSizeX-1) || offsetPos.y > (MapConfig.mapSizeY-1))
				continue;

			// if brush is for hover visualization, dont add selection to data
			if(hover)
				continue;

			// flag that represents if a layer with a free spot was found for the tile
			boolean foundLayer = false;

			// finds layer of the tile (if its on top of another tile, creates a new layer)
			// iterating ascending through layers until find a empty spot for the tile
			// creating new layer if needed
			for(int j = 0; j < layers.size(); j++) {
				// get tiles from the iteration layer
				Tile[][] iterTiles = layers.get(j).getTiles();

				// if position is free on layer tiles, found a layer for the tile
				if(iterTiles[offsetPos.y][offsetPos.x] == null) {
					// adds tile to the free position
					iterTiles[offsetPos.y][offsetPos.x] = newTile;
					// updates found layer bool to true
					foundLayer = true;

					// breaks search for spot
					break;
				}
			}

			// if layer wasnt found in existing ones, creates another one (if max layers not exceeded)
			if(!foundLayer) {
				if(layers.size() < MapConfig.getInstance().getMaxLayers()) {
					// creates new layer on top of others (last pos of layers list)
					Layer newLayer = new Layer(layers.size(), 1f);
					// adds tile to the new layers tiles matrix
					newLayer.getTiles()[offsetPos.y][offsetPos.x] = newTile;
					// adds layers to the list of layers in the map
					layers.add(newLayer);

					if(Config.debug)
						System.out.println("Map: Layer"+layers.size()+" created for tile: "+offsetPos);
				}
				else { // removes top layer object and puts new object
					Layer topLayer = layers.get(layers.size()-1);
					topLayer.getTiles()[offsetPos.y][offsetPos.x] = newTile;
				}
			}


			// adds new state to the map states if its time to save (mouse released)
			if(ViewMapControl.saveState) {
				mapStates.AddState(currentMap); // addstate already notify observers
				ViewMapControl.saveState = false;
			} // if its not time to save
			else // updates current state (update state method notify observers already)
				mapStates.UpdateState(currentMap);

		}
	}

	/**
	 * Rectangular selection between two points in the screen.
	 * Using p1 and p2 as anchors, find all tiles
	 * contained by the rectangular selection created
	 * by the two points (p1: top-left vertex; p2: bottom-right vertex)
	 * Overload: Rectangular selection of tiles in tileset
	 * 
	 * @author	Pedro Sampaio
	 * @since	0.4
	 * @param	p1				First point (top-left vertex) of rectangle selection (must be relative to scroll)
	 * @param	p2				Second point (bottom-right vertex) of rectangle selection (must be relative to scroll)
	 * @param	tileset			The tileset that is currently loaded in the program
	 * @param	selectedTiles	The list of tiles that will contain the selected of tiles
	 * @param	tilesize		The size of a tile
	 * @param	limitX			the limit in the X-axis for selection (bounds)
	 * @param   limitY			the limit in the Y-axis for selection (bounds)
	 */
	public static void RectSelect(Point p1, Point p2, Tileset tileset, ArrayList<Tile> selectedTiles, int tilesize, int limitX, int limitY)
	{
		// converts points (x,y cartesian coords) to tile indexes in our data structure
		Point tOriginIdx = new Point(p1.x / tilesize, 
				p1.y / tilesize);
		Point tDestinyIdx = new Point(p2.x / tilesize, 
				p2.y / tilesize);

		// clamps points to avoid getting out of tileset bounds
		tOriginIdx.x = Math.max(0, Math.min(tOriginIdx.x, limitX - 1));
		tOriginIdx.y = Math.max(0, Math.min(tOriginIdx.y, limitY - 1));
		tDestinyIdx.x = Math.max(0, Math.min(tDestinyIdx.x, limitX - 1));
		tDestinyIdx.y = Math.max(0, Math.min(tDestinyIdx.y, limitY - 1));

		// asserts points guaranteeing that limits are correctly set
		assert(tOriginIdx.x >= 0 && tOriginIdx.x < limitX);
		assert(tOriginIdx.y >= 0 && tOriginIdx.y < limitY);
		assert(tDestinyIdx.x >= 0 && tDestinyIdx.x < limitX);
		assert(tDestinyIdx.y >= 0 && tDestinyIdx.y < limitY);

		/** 
		 * adjusts origin and destiny depending on the direction of selection
		 * to cover all directions correctly, changing looping order if needed
		 **/

		// aux point for swaps
		Point tAuxIdx = null;
		// volatile values depending on direction and change of origin and destiny
		int i_init, i_limit, i_increment;
		int j_init, j_limit, j_increment;
		// bools to help changing directions
		boolean isTopRightDir = false;
		boolean isBottomLeftDir = false;

		// swapping covers few cases but now enough (for >^ and <V direction ie)
		if(tDestinyIdx.y < tOriginIdx.y || tDestinyIdx.x < tOriginIdx.x)
		{
			// swap origin and destiny
			tAuxIdx = tDestinyIdx;
			tDestinyIdx = tOriginIdx;
			tOriginIdx = tAuxIdx;
		}

		// normal direction loop params
		i_init = tOriginIdx.y; i_limit = tDestinyIdx.y; i_increment = 1;
		j_init = tOriginIdx.x; j_limit = tDestinyIdx.x; j_increment = 1;

		// <v direction (inverted origin and destiny)
		if(tDestinyIdx.y < tOriginIdx.y && tDestinyIdx.x > tOriginIdx.x)
		{
			// adjust parameters of loop
			i_increment = -1; j_increment = 1;
			// use flag to help change direction of loop
			isBottomLeftDir = true;
		}

		// >^ direction (inverted origin and destiny)
		if(tDestinyIdx.y > tOriginIdx.y && tDestinyIdx.x < tOriginIdx.x) {
			// adjust increment of loop
			i_increment = 1; j_increment = -1;
			// use flag to help change direction of loop limit
			isTopRightDir = true;	
		}

		// clears current selected tiles list
		// to add the new rectangular selection
		selectedTiles.clear();

		// iterates in square shape like form to select tiles contained in rect selection
		for(int i = i_init; isBottomLeftDir ? i >= i_limit : i <= i_limit; i = i + i_increment) {
			for(int j = j_init; isTopRightDir ? j >= j_limit : j <= j_limit; j = j + j_increment) {
				// adds tile to the selected tiles list
				selectedTiles.add(new Tile(i, j, tileset));
			}
		}

	}

	/**
	 * Finds what tile was clicked in the viewport and adds it to
	 * selected tiles list. 
	 * The way of adding varies if ctrl or shift keys
	 * are pressed.
	 * no key pressed: 		Resets selected list to contain only the new selected item
	 * CTRL key pressed: 	Adds to the selected tiles list maintaining previous selected items
	 * SHIFT key pressed:	Selects tiles in a rectangular shape based on the clicked tile and 
	 * 						the first tile clicked in the shift interaction
	 *  
	 * @param 	relativeClick		the relative position of the click 
	 * @param	tileset				the tileset that is currently loaded in the program
	 * @param	selectedTiles		the list of tiles that will contain the selection of tiles
	 * @param 	shiftOrigin 		the position origin of the shift click
	 * @param	ofMap				if it is the map's viewport or the tileset's viewport that was clicked
	 * @since	0.3
	 */
	public static void SelectTile(Point relativeClick, Tileset tileset, ArrayList<Tile> selectedTiles, Point shiftOrigin, boolean ofMap) {

		/**
		 * Necessary infos about tile clicked
		 */
		// finds the selected tile indices
		Point tSelectedIdx;
		if(ofMap)
			tSelectedIdx = new Point(relativeClick.x / MapConfig.getTileZoomed(), 
					relativeClick.y /  MapConfig.getTileZoomed());
		else
			tSelectedIdx = new Point(relativeClick.x / tileset.getTileSize(), 
					relativeClick.y / tileset.getTileSize());

		// creates tile to add to selection
		// no layer as of now (only when added to the map)
		Tile tSelected = new Tile(tSelectedIdx.y, tSelectedIdx.x, tileset);

		/**
		 * Controls what to do with different keys pressed.
		 * Gives preference to Shift when both shift and control
		 * are pressed.
		 */
		if(!KeyboardControl.isCtrlPressed() && !KeyboardControl.isShiftPressed()) {		// no keys pressed
			// clears selected tiles list with clear() (faster than removeAll)
			selectedTiles.clear();
			// adds to selectedTiles list
			selectedTiles.add(tSelected);
		}
		else if(KeyboardControl.isShiftPressed()) {										// at least shift is pressed (control may be pressed as well)

			// sets the shift anchor tile as (0,0) if no other tiles are selected
			if (selectedTiles.isEmpty() || shiftOrigin == null) {
				// if no tile anchor of shift is selected
				// lets use (0,0) tile as anchor
				shiftOrigin = new Point(0,0);
			}

			// assure that shiftOrigin is not null
			assert(shiftOrigin != null);

			// do rectangular selections from first selected tile in
			// the current shift interaction to the new tile clicked
			if(ofMap)
				Tool.RectSelect(shiftOrigin, relativeClick, tileset, selectedTiles, 
						MapConfig.getTileZoomed(), MapConfig.mapSizeX, MapConfig.mapSizeY);
			else
				Tool.RectSelect(shiftOrigin, relativeClick, tileset, selectedTiles, 
						tileset.getTileSize(), tileset.getTileSizeX(), tileset.getTileSizeY());

		}
		else {																			// only control is pressed
			// guarantee controls logic
			assert(!KeyboardControl.isShiftPressed() && KeyboardControl.isCtrlPressed());

			// adds to selectedTiles list maintaining previous selected tiles 
			// but only adds if current tile isnt selected yet
			// if it is selected, removes selection
			boolean isSelectedAlready = false; 
			int i;
			for(i = 0; i < selectedTiles.size(); i++) {
				if	(selectedTiles.get(i).getIndexI() == tSelected.getIndexI() &&
						selectedTiles.get(i).getIndexJ() == tSelected.getIndexJ()) {
					isSelectedAlready = true;
					break;
				}
			}

			if(isSelectedAlready)
				selectedTiles.remove(i);
			else
				selectedTiles.add(tSelected);
		}

		/**
		 * notify tileset observers of changes in selected tiles if selected tiles are from tileset
		 */
		if(!ofMap)
			tileset.selectedTilesDispatchChanges();
	}

	/**
	 * Method for erasing tiles
	 * in the 2d map in the corresponding layer.
	 * If parameter hover is true, just preview the tile
	 * that is going to be erased if a click happens
	 * 
	 * @author	Pedro Sampaio
	 * @param 	relativePoint 	the relative position of the mouse point
	 * @param	mapStates		the states of the map
	 * @param	hover			if is a click or just a mouse hover on point
	 * @since	0.5
	 * 
	 */
	public static void eraseTile(Point relativePoint, MapState mapStates, boolean hover) {

		// only erase tile if mouse is on viewport
		if(!MapConfig.isMouseOnViewport())
			return;

		// gets tile coords relative to the mouse scroll-relative position
		Point tileInd	= new Point(relativePoint.x / MapConfig.getTileZoomed(), 
				relativePoint.y /  MapConfig.getTileZoomed());

		// updates erase-to-be tile point for visualization
		Tool.getInstance().setEraseTilePoint(tileInd);

		// if its hover, we are done
		if(hover)
			return;

		//else, we must remove tile(if exists) from data structure

		// gets map in current state (creates a copy to add to state list)
		Map currentMap = mapStates.getCurrentMap().createCopy();

		// gets layers of current map state
		ArrayList<Layer> layers = currentMap.getLayers();

		// bool that represents if a tile was found to delete
		boolean tileFound = false;
		
		// returns if mouse click is out of bounds
		if(tileInd.y >= MapConfig.mapSizeY || tileInd.x >= MapConfig.mapSizeX || tileInd.y < 0 || tileInd.x < 0)
			return;

		// find the biggest layer that contains a tile;
		int i;
		for(i = layers.size()-1; i >= 0; i--) {
			Layer iLayer = layers.get(i);

			// found tile to delete
			if(iLayer.getTiles()[tileInd.y][tileInd.x] != null) {
				iLayer.getTiles()[tileInd.y][tileInd.x] = null;
				if(Config.debug) {
					System.out.println("ViewMapControl: Deleting tile: "+tileInd);
				}
				tileFound = true;

				break;		// break to not delete tiles in lower layers
			}
		}

		// adds new state to the map states (addstate already updates current state)
		// if there was a change in the map structure
		if(tileFound) {       		
			// checks if layer has no tiles and if so, delete it
			// (only if its not first layer)
			if(i > 0) // if erased tile wasnt in first layer
				if(layers.get(i).isEmpty()) { // if after erase tilecount of layer is smaller than 1 
					layers.remove(i); // remove layer that has no tiles and isnt the first one
					if(currentMap.getSelectedLayer() == i) { // if selected layer is the same deleted
						currentMap.setSelectedLayer(i-1);	// decreases select layer by one
					}
				}
			// adds new state to the map states if its time to save (mouse released)
			if(ViewMapControl.saveState) {
				mapStates.AddState(currentMap); // addstate already notify observers
				ViewMapControl.saveState = false;
			} // if its not time to save
			else // updates current state (update state method notify observers already)
				mapStates.UpdateState(currentMap);          		
		}
		else // notify observers for previews changes
			Tool.getInstance().dispatchChanges();


	} 
	
	/**
	 * Adds a collider to a tile area based on user mouse input
	 * or remove an existent collider from a tile area.
	 * 
	 * @param 	relativePoint 	the relative position of the mouse point
	 * @param	mapStates		the states of the map
	 * @param 	trigger			if it is a trigger collider or a physical collider
	 * @param	hover			if is a click or just a mouse hover on point
	 */
	public static void ToggleCollider(Point relativePoint, MapState mapStates, boolean trigger, boolean hover) {
		
		// only erase tile if mouse is on viewport
		if(!MapConfig.isMouseOnViewport())
			return;

		// gets tile coords relative to the mouse scroll-relative position
		Point tileInd	= new Point(relativePoint.x / MapConfig.getTileZoomed(), 
				relativePoint.y /  MapConfig.getTileZoomed());
		
		// if its hover, we are done
		if(hover)
			return;
		
		//else, we must toogle collider from data structure

		// gets map in current state (creates a copy to add to state list)
		Map currentMap = mapStates.getCurrentMap().createCopy();
		
		// returns if mouse click is out of bounds
		if(tileInd.y >= MapConfig.mapSizeY || tileInd.x >= MapConfig.mapSizeX || tileInd.y < 0 || tileInd.x < 0)
			return;
		
		// check if collider exists already
		if(currentMap.getColliders()[tileInd.y][tileInd.x] != null) {
			currentMap.getColliders()[tileInd.y][tileInd.x] = null; // removes collider
		}
		else {
			// if its a trigger, create a trigger
			if(trigger)
				currentMap.getColliders()[tileInd.y][tileInd.x] = new Collider(2, tileInd.y, tileInd.x, true); // creates trigger collider
			else // creates a physical collider
				currentMap.getColliders()[tileInd.y][tileInd.x] = new Collider(1, tileInd.y, tileInd.x, false); // creates physical collider
		}
		
		// adds new state to the map states if its time to save (mouse released)
		if(ViewMapControl.saveState) {
			mapStates.AddState(currentMap); // addstate already notify observers
			ViewMapControl.saveState = false;
		} // if its not time to save
		else // updates current state (update state method notify observers already)
			mapStates.UpdateState(currentMap);   

	}


	/**
	 * Method for erasing tiles
	 * in the 2d map in a given layer.
	 * 
	 * @author	Pedro Sampaio
	 * @param 	tile 			the tile information containing matrix indexes of the tile to be deleted
	 * @param	mapStates		the states of the map
	 * @param	layer			the layer to delete the tile
	 * @since	1.0
	 * 
	 */
	private static boolean eraseTile(Tile tile, MapState mapStates, int layer) {

		// boolean that represents if a tile was found with given params
		boolean tileFound = false;
		Map currentMap = mapStates.getCurrentMap().createCopy();
		ArrayList<Layer> layers = currentMap.getLayers();
		Layer selLayer = layers.get(layer);


		// if info at matrix with given params isn't null, a tile to delete was found
		if(selLayer.getTiles()[tile.getIndexI()][tile.getIndexJ()] != null) {
			selLayer.getTiles()[tile.getIndexI()][tile.getIndexJ()] = null; // delete tile
			tileFound = true; // updates tileFound bool
		}

		// if there was a change in the map structure
		// do the necessary updates
		if(tileFound) {       		
			// checks if layer has no tiles and if so, delete it
			// (only if its not first layer)
			if(selLayer.isEmpty() && layer > 0) { // if after erase tilecount of layer is smaller than 1 and its not first layer
				layers.remove(layer); // remove layer that has no tiles and isnt the first one
				if(currentMap.getSelectedLayer() == layer) { // if selected layer is the same deleted
					currentMap.setSelectedLayer(layer-1);	// decreases select layer by one
				}
			}
			// adds new state to the map states if its time to save (mouse released)
			if(ViewMapControl.saveState) {
				mapStates.AddState(currentMap); // addstate already notify observers
				ViewMapControl.saveState = false;
			} // if its not time to save
			else // updates current state (update state method notify observers already)
				mapStates.UpdateState(currentMap);          		
		}
		else // notify observers for previews changes
			Tool.getInstance().dispatchChanges();

		return tileFound;
	}

	/**
	 * Erases a selection of tile
	 * of current selected layer
	 * 
	 * @author Pedro Sampaio
	 * @since 1.0
	 * @param selection	the array of tiles to be erased
	 * @param mapStates	the states map with all its states
	 * @param selectedLayer the selected layer to erase tiles from
	 */
	public static void eraseSelection(ArrayList<Tile> selection, MapState mapStates, int selectedLayer)
	{
		if(Config.debug) {
			System.out.println("Tool: There are "+selection.size()+" possible tiles to be erased"
					+ "	from layer"+(selectedLayer+1));
		}

		// iterates through the selection tiles to erase existent ones
		for(int i = 0; i < selection.size(); i++) {
			Tile tile = selection.get(i);	// tile to be erased

			eraseTile(tile, mapStates, selectedLayer); // erases tile if found

			if(mapStates.getCurrentMap().getSelectedLayer() != selectedLayer) // layer was emptied and deleted, we dont want to continue here
				break;
		}

	}

	/**
	 * Copies the current selected tiles of 
	 * current selected layer to the clipboard for further uses
	 * 
	 * @author Pedro Sampaio
	 * @since 1.0
	 * @param mapStates	the states map with all its states
	 */
	public static void copyToClipboard(MapState mapStates)
	{
		Map currentMap = mapStates.getCurrentMap();	// current map
		ArrayList<Layer> layers = currentMap.getLayers(); // layers of current map
		Layer selLayer = layers.get(currentMap.getSelectedLayer());	// selected layer
		ArrayList<Tile> selTiles = selLayer.getSelectedTiles(); // selected tiles of selected layer
		ArrayList<Tile> copiedTiles = new ArrayList<>();	// the copied tiles with full information of each tile

		// if there are selected tiles, copy to clipboard
		if(!selTiles.isEmpty()) {
			// build a new list of tiles with full information (obtained from map data matrix)
			// in selection indexI and indexJ represents position of tile in map, not in tileset
			for(int i = 0 ; i < selTiles.size(); i++) {
				if(selLayer.getTiles()[selTiles.get(i).getIndexI()][selTiles.get(i).getIndexJ()] != null) {
					Tile tile = selLayer.getTiles()[selTiles.get(i).getIndexI()][selTiles.get(i).getIndexJ()];
					// adds information for paste operation anchoring 
					tile.setMatrixI(selTiles.get(i).getIndexI());
					tile.setMatrixJ(selTiles.get(i).getIndexJ());
					copiedTiles.add(tile);
				}
			}

			// debugs copied tiles list size
			if(Config.debug)
				System.out.println("Tool: Copied "+(copiedTiles.size())+" tiles from map");

			// copy selected tiles to clipboard if at least one tile in selection exists 
			if(!copiedTiles.isEmpty()) {
				Clipboard.getInstance().setCopiedTiles(copiedTiles);
			}
			else { // nothing to copy
				if(Config.debug) 
					System.out.println("Tool: Nothing to copy on layer"+(currentMap.getSelectedLayer()+1));
				// empties clipboard list since user copied a selection with no tiles
				Clipboard.getInstance().setCopiedTiles(new ArrayList<>());
			}

		} else { // nothing to copy
			if(Config.debug)
				System.out.println("Tool: Nothing to copy on layer"+(currentMap.getSelectedLayer()+1));	
		}
	}

	/**
	 * Pastes the current copied tiles of 
	 * clipboard to the list used to brush tiles in map
	 * 
	 * @author Pedro Sampaio
	 * @since 1.0
	 * @param mapStates	the states map with all its states
	 */
	public static void pasteFromClipboard(MapState mapStates) {
		// only notify clipboard observers (for paste related operations)
		// if clipboard is not empty
		if(!Clipboard.getInstance().getCopiedTiles().isEmpty()) {
			// sets that paste is wanted
			Clipboard.getInstance().setPaste(true);
			// dispatch changes meaning that a paste action has happened
			Clipboard.getInstance().dispatchChanges();
			// sets current tool as brush to be able to paint clipboard content
			Tool.getInstance().setCurrentTool(SelectTools.BRUSH);
			Tool.getInstance().dispatchChanges();
		}
	}

	/**
	 * @return the currentTool
	 */
	public SelectTools getCurrentTool() {
		return currentTool;
	}

	/**
	 * @param currentTool the currentTool to set
	 */
	public void setCurrentTool(SelectTools currentTool) {
		// updates selected tool for map interaction
		this.currentTool = currentTool;
		// notify observers of map interaction tools
		dispatchChanges();
		// fire mouse movement for force update on preview
		ViewMapControl.fireMouseMovement();
	}

	/**
	 * dispatch changes in the tools selection for observers
	 * 
	 * @author	Pedro Sampaio
	 * @since	0.7
	 */
	public void dispatchChanges() {
		setChanged();
		notifyObservers();
	}

	/**
	 * @return the eraseTilePoint
	 */
	public Point getEraseTilePoint() {
		return eraseTilePoint;
	}

	/**
	 * @param eraseTilePoint the eraseTilePoint to set
	 */
	public void setEraseTilePoint(Point eraseTilePoint) {
		this.eraseTilePoint = eraseTilePoint;
	}

	/**
	 * 
	 * @param currentTool current tool to set
	 * @param tilesetClick	if it was a click in tileset that selected the tool (for brush automatic selection)
	 */
	public void setCurrentTool(SelectTools currentTool, boolean tilesetClick) {
		// updates selected tool for map interaction
		this.currentTool = currentTool;
		// notify observers of map interaction tools
		dispatchChanges();
		// fire mouse movement only if its not a tile click that selected thte tool
		if(!tilesetClick) {
			// fire mouse movement for force update on preview
			ViewMapControl.fireMouseMovement();
		}
	}

}
