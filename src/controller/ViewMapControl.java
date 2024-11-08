package controller;

import java.awt.Button;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

import model.Clipboard;
import model.Map;
import model.MapConfig;
import model.MapState;
import model.Preferences;
import model.Tile;
import model.Tileset;
import model.TilesetConfig;
import model.Tool;
import test.Config;

/**
 * The mouse controller on viewport panel.
 * Responsible for the interaction with the 2D 
 * map visualization in the viewport via mouse
 * 
 * @author 	Pedro Sampaio
 * @since	0.1
 *
 */
public class ViewMapControl implements Observer {
	
	/**
	 * Mouse controls
	 */
	int dragButton 		= MouseEvent.BUTTON2;		// middle mouse button controls drag movements of the map
	int selectButton	= MouseEvent.BUTTON1;		// left mouse button for select interactions with the map
	int pressedButton;								// the last pressed button
	public static boolean saveState;				// bool that represents if mouse button was relased and state should be saved
	
	/**
	 * Current tool selected for map interaction (obtained via Tool class observation)
	 */
	Tool.SelectTools currentTool;
	
	/**
	 * Mouse cursors
	 */
	
	Cursor defaultCursor;		// the default cursor
	Cursor grabCursor;			// drag map cursor
	
	/**
	 * Tile infos
	 */
	@SuppressWarnings("unused")
	private Map map;				// current map data
	private int defaultUnitV;		// default increment on vertical scroll of jsrollpane
	private ArrayList<Tile> selectedMapTiles;	// selected map tiles at a given moment
	private MapState mapStates;					// a reference to the states of the map
	private int layerIdx;					// current selected layer index
	private static MouseAdapter mouseAdapter;	// mouse adapter with mouse callbacks
	private boolean pasteFromClipboard;		// bool that represents if a paste from clipboard is the current selected tiles
	
	/**
	 * fires a mouse movement to aid on preview update
	 */
	public static void fireMouseMovement() {
		mouseAdapter.mouseMoved(new MouseEvent(new Button(), 0, 0, 0, MapConfig.getMousePosition().x, 
										MapConfig.getMousePosition().y, 1, false));
	}

	/**
	 * Creates the adapter for mouse interactions and its callbacks
	 * adding it to the scrollPane as a mouse and a motion listener
	 * 
	 * @param 	scrollPane		JScrollPane that contains the viewport for map visualization
	 * @param   tileset			the current tileset loaded
	 * @param 	mapStates 		the map states that represents the 2d map in different states
	 * 							(multiple states for undoing and redoing operations)
	 */
	public ViewMapControl(JScrollPane scrollPane, Tileset tileset, MapState mapStates) {
		this.map = mapStates.getCurrentMap();
		this.defaultUnitV = scrollPane.getVerticalScrollBar().getUnitIncrement();
		saveState = false;
		this.mapStates = mapStates;

		// initially a paste from clipboard is not the selected tiles
		// only when player use paste shortcut/button
		pasteFromClipboard = false;
		// observers clipboard to find out if a paste should be used
		Clipboard.getInstance().addObserver(this);
		
		// sets initial interaction tool
		currentTool = Tool.getInstance().getCurrentTool();
		// observes tool for changes in selected tools
		Tool.getInstance().addObserver(this);
		// observes mapconfig for changes in selected layer
		MapConfig.getInstance().addObserver(this);
		// observes tilesetconfig for changes in selected tileset
		TilesetConfig.getInstance().addObserver(this);
		
		// initializes selected map tiles with no tiles selected
		// using the first layer as initial layer
		layerIdx = 0;
		selectedMapTiles = (mapStates.getCurrentMap().getLayers().get(layerIdx).getSelectedTiles());
		
		// sets mouse cursors
		defaultCursor = scrollPane.getCursor();
		Toolkit toolkit = Toolkit.getDefaultToolkit();

		Image image = toolkit.getImage(ViewMapControl.class.getResource("/resources/grabIcon.png"));

		try {
			grabCursor = toolkit.createCustomCursor(image , new Point(0, 0), "img");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ViewMapControl: Could not create a custom cursor");
		}
		
		// creates the mouse adapter to be used as controller of the scroll panel that contains viewport
		mouseAdapter = new MouseAdapter() {
			
			private Point origin;  // origin click point
			private Point tOrigin; // origin tile point
			private Point shiftOrigin;			// origin point of click when shift is pressed
			private Point destiny;       	

            @Override
            public void mousePressed(MouseEvent e) {
        		
        		// gets currently selected tileset
        		Tileset tileset =  TilesetConfig.getInstance().getCurrentTileset();
            	       	
            	// updates last pressed button
            	pressedButton = e.getButton();          
            	
            	// gets click position relative to the scroll
            	Point relativeClick = new Point((e.getPoint().x + scrollPane.getViewport().getViewPosition().x),
            									(e.getPoint().y + scrollPane.getViewport().getViewPosition().y));
            	
            	// updates origin
        		origin = new Point(e.getPoint());
        		// origin tile click  
            	tOrigin = new Point((origin.x + scrollPane.getViewport().getViewPosition().x) / MapConfig.getTileZoomed(),
            						(origin.y + scrollPane.getViewport().getViewPosition().y) / MapConfig.getTileZoomed());
            	
        		// updates shift origin if shift is not pressed
        		if(!KeyboardControl.isShiftPressed())
        			shiftOrigin = relativeClick;
            	
            	// only change origin of drag movement if drag button is used
            	if(e.getButton() == dragButton) {
                	// change to grab cursor
                	scrollPane.setCursor(grabCursor);
            	}
            	else if (e.getButton() == selectButton) {
            		// switch actions depending on what tool is currently selected
            		switch (currentTool) {
            			case BRUSH:
            				// if its not a paste from clipboard, use tileset selected tiles to brush
            				if(!pasteFromClipboard)
            					Tool.brushTiles(relativeClick, mapStates, tileset, false);	// user wants to paint selected tiles
            				else { // use a paste from clipboard to brush (user has pressed ctrl+v or used paste button)
            					if(Config.debug) {
            						System.out.println("ViewMapControl: A paste of size " + 
            											Clipboard.getInstance().getCopiedTiles().size() + " is in course");
            					}
            					
            					// use brushtiles overload that paints with clipboard content
            					Tool.brushTiles(relativeClick, mapStates,
            									Clipboard.getInstance().getCopiedTiles(), false);
            				}
            				break;
            			case ERASER:
            				Tool.eraseTile(relativeClick, mapStates, false);		// user wants to erase tile
            				break;
            			case SELECTION:
                			// pass control to select method to add tile to selected tiles
                			Tool.SelectTile(relativeClick, tileset, selectedMapTiles, shiftOrigin, true);
                			if(Config.debug) {
                				System.out.println("Map: There are " + selectedMapTiles.size() + " selected tiles in map");
                			}
                			mapStates.getCurrentMap().getLayers().get(mapStates.getCurrentMap().getSelectedLayer()).setSelectedTiles(selectedMapTiles);
                        	// notify observers of map that a change has occurred
                        	mapStates.dispatchChanges();
            				break;
            			case COLLIDER:
                			// pass control to toggle collider method to add or remove collider to selected tiles
                			Tool.ToggleCollider(relativeClick, mapStates, Tool.getInstance().isTrigger(), false);
            				break;	
            			case NONE:
            				break;
						default:
							if(Config.debug) 
								System.out.println("ViewMapControl.mouseAdapter.mousePressed: Unable to identify current tool");
							break;
            		}
            	}
            }

			@Override
            public void mouseReleased(MouseEvent e) {
        		
            	scrollPane.setCursor(defaultCursor);
            	//save state should happen
            	if(pressedButton == selectButton)
            		saveState = true;
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
            	
        		// gets currently selected tileset
        		Tileset tileset =  TilesetConfig.getInstance().getCurrentTileset();
            	
            	// gets click position relative to the scroll
            	Point relativeClick = new Point((e.getX() + scrollPane.getViewport().getViewPosition().x),
            									(e.getY() + scrollPane.getViewport().getViewPosition().y));
            	
                if (origin != null && pressedButton == dragButton) {
                	
                    JViewport viewPort = scrollPane.getViewport();
                    
                    if (viewPort != null) {
                        int deltaX = origin.x - e.getX();
                        int deltaY = origin.y - e.getY();

                        Rectangle view = viewPort.getViewRect();
                        view.x = (int)(deltaX * Preferences.viewportMouseSensitivityX * 1/64f);
                        view.y = (int)(deltaY * Preferences.viewportMouseSensitivityY * 1/64f);
                        
                        viewPort.scrollRectToVisible(view);
                    }
                   
                }
                else if(pressedButton == selectButton) {
                	
                	// check if dragged tile is in the same tile of origin
                	//origin tile
                	
                	// gets tile coords relative to the mouse scroll-relative position
                	Point tDestiny	= new Point(relativeClick.x / MapConfig.getTileZoomed(), 
                							    relativeClick.y /  MapConfig.getTileZoomed());
                	
                	// bool that stores if drag ocurrs in the same tile as last addition
                	boolean inSameTile = (tOrigin.x == tDestiny.x && tOrigin.y == tDestiny.y) ? true : false;

                	JViewport viewPort;
					// switch actions depending on what tool is currently selected
            		switch (currentTool) {
            			case BRUSH:
            				// only brush tiles if drag is on different tile than last addition
            				if(!inSameTile) {
            					if(!pasteFromClipboard) { // use tileset selected tiles if its a paste is not happening
            						Tool.brushTiles(relativeClick, mapStates, tileset, false);	// user wants to paint selected tiles
            					}
                				else { // use a paste from clipboard to brush (user has pressed ctrl+v or used paste button)
                					if(Config.debug) {
                						System.out.println("ViewMapControl: A paste of size " + 
                											Clipboard.getInstance().getCopiedTiles().size() + " is in course");
                					}

                					// use brushtiles overload that paints with clipboard content
                					Tool.brushTiles(relativeClick, mapStates,
                									Clipboard.getInstance().getCopiedTiles(), false);
                				}
            					tOrigin = tDestiny;
            				}
            				break;
            			case ERASER:
            				// only erase tiles if drag is on different tile than last addition
            				if(!inSameTile) {
            					Tool.eraseTile(relativeClick, mapStates, false);		// user wants to erase tile
            					tOrigin = tDestiny;
            				}
            				break;
            			case COLLIDER:
            				// only erase tiles if drag is on different tile than last addition
            				if(!inSameTile) {
            					Tool.ToggleCollider(relativeClick, mapStates, Tool.getInstance().isTrigger(), false);
            					tOrigin = tDestiny;
            				}
            				break;	
            			case SELECTION:
            				// dont do drag selection if ctrl or clicked are pressed
                        	if(KeyboardControl.isCtrlPressed() || KeyboardControl.isShiftPressed())
                        		return;
                        	
                        	destiny = new Point(e.getX(),  e.getY());
                        	
                        	// if origin and destiny are from the same tile, dont bother doing rect select
                        	if (inSameTile) {
                        		//selects destiny tile for cases that are returning from different tiles selection
                        		Point relativeDestiny = new Point(destiny.x + scrollPane.getViewport().getViewPosition().x,
                        										  destiny.y + scrollPane.getViewport().getViewPosition().y);
                        		Tool.SelectTile(relativeDestiny, tileset, selectedMapTiles, shiftOrigin, true);

                    			if(Config.debug) {
                    				System.out.println("Map: There are " + selectedMapTiles.size() + " selected tiles in map");
                    			}
                    			
                    			mapStates.getCurrentMap().getLayers().get(mapStates.getCurrentMap().getSelectedLayer()).setSelectedTiles(selectedMapTiles);
                            	// notify observers of map that a change has occurred
                            	mapStates.dispatchChanges();
                    			
                        		return;
                        	}
                        	viewPort = scrollPane.getViewport();
                        	// gets origin and destiny position relative to the scroll
                        	if (viewPort != null) {
                            	
                            	Point relativeOrigin = new Point(origin.x + viewPort.getViewPosition().x,
                            									 origin.y + viewPort.getViewPosition().y);
                            	Point relativeDestiny = new Point(destiny.x + viewPort.getViewPosition().x,
                            									  destiny.y + viewPort.getViewPosition().y);

                            	// creates the rectangular selection passing the relatives origin and destiny of mouse drag
                            	Tool.RectSelect(relativeOrigin, relativeDestiny, tileset, selectedMapTiles, 
                            			MapConfig.getTileZoomed(), MapConfig.mapSizeX, MapConfig.mapSizeY);
                            	
                    			if(Config.debug) {
                    				System.out.println("Map: There are " + selectedMapTiles.size() + " selected tiles in map");
                    			}
                    			mapStates.getCurrentMap().getLayers().get(mapStates.getCurrentMap().getSelectedLayer()).setSelectedTiles(selectedMapTiles);
                            	// notify observers of map that a change has occurred
                            	mapStates.dispatchChanges();
                        	}
                        	else
                        		System.out.println("ViewTilesetControl.mouseAdapter.mouseDragged: Could not reference viewport");
            				break;
            			case NONE:
            				break;
						default:
							if(Config.debug) 
								System.out.println("ViewMapControl.mouseAdapter.mousePressed: Unable to identify current tool");
							break;
            		}
                }
            }    
            
            // mouse entered viewport 
            @Override
            public void mouseEntered(MouseEvent e) {
            	MapConfig.setMouseOnViewport(true);
            	MapConfig.getInstance().dispatchChanges(false); // notify observers
            }
            
            // mouse left viewport
            @Override
            public void mouseExited(MouseEvent e) {
            	MapConfig.setMouseOnViewport(false);
            	MapConfig.getInstance().dispatchChanges(false); // notify observers
            }
            
            // updates mouse position on map's viewport 
            public void mouseMoved(MouseEvent e) {
            	
        		// gets currently selected tileset
        		Tileset tileset =  TilesetConfig.getInstance().getCurrentTileset();
        		
            	// updates mouse position
                MapConfig.setMousePosition(new Point (e.getX(), e.getY()));   
                // notify observers of changes in mouse position
                MapConfig.getInstance().dispatchChanges(false);
                
                // if mouse is not on viewport, return and do not adjust previews
                if(!MapConfig.isMouseOnViewport())
                	return;
                
                /**
                 *  display previews if mouse is on viewport
                 */
                // switch current tool to proper display
                switch (currentTool) {
	    			case BRUSH:
	    				// use selected tiles from tileset if a paste is not happening
	    				if(!pasteFromClipboard) {
		    				Tool.brushTiles(new Point(MapConfig.getMousePosition().x + scrollPane.getViewport().getViewPosition().x,
	        						MapConfig.getMousePosition().y + scrollPane.getViewport().getViewPosition().y), mapStates, tileset, true);
	    				}
        				else { // use a paste from clipboard to brush (user has pressed ctrl+v or used paste button)
        					if(Config.debug) {
        						System.out.println("ViewMapControl: A paste of size " + 
        											Clipboard.getInstance().getCopiedTiles().size() + " is in course");
        					}
        					
        					// use brushtiles overload that paints with clipboard content
        					Tool.brushTiles(new Point(MapConfig.getMousePosition().x + scrollPane.getViewport().getViewPosition().x,
	        								MapConfig.getMousePosition().y + scrollPane.getViewport().getViewPosition().y), mapStates,
        									Clipboard.getInstance().getCopiedTiles(), true);
        				}
	    				break;
	    			case ERASER:
	    				Tool.eraseTile(new Point(MapConfig.getMousePosition().x + scrollPane.getViewport().getViewPosition().x,
        						MapConfig.getMousePosition().y + scrollPane.getViewport().getViewPosition().y), mapStates, true);
	    				break;
	    			case SELECTION:
	    				break;
	    			case NONE:
	    				break;
					default:
						if(Config.debug) 
							System.out.println("ViewMapControl.mouseAdapter.mousePressed: Unable to identify current tool");
						break;
	    		}             
             }       	

        };
        
        /**
         * Mousewheel scroll listener to change map view zoom
         */
        scrollPane.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {

                // if user is pressing control, zoom functionality is desired
                if(KeyboardControl.isCtrlPressed()) {
                	// disables vertical scroll moving to zoom
                	scrollPane.getVerticalScrollBar().setUnitIncrement(0);
                	
                	float preZoom = MapConfig.zoom;
                	
                	MapConfig mConfig = MapConfig.getInstance();
                	// update zoom depending on user input
                	if(e.getWheelRotation() > 0)
                		MapConfig.zoom *= MapConfig.getInstance().getZoomOutSpeed();
                	else
                		MapConfig.zoom *= MapConfig.getInstance().getZoomInSpeed();

                	// clamp zoom between min a max zoom
                	MapConfig.zoom = Math.max(mConfig.getMinZoom(), Math.min(MapConfig.zoom, mConfig.getMaxZoom()));
                	
                	if(Config.debug)
                		System.out.println("ViewMapControl: CurrentZoom: " + MapConfig.zoom);
                	
                	// zooms with mouse position as anchor 
                	// translates only if zoom occurs
                	if(preZoom != MapConfig.zoom) {
	                	Point pos = scrollPane.getViewport().getViewPosition();
	                	Point point = MapConfig.getMousePosition();
	                	double zoomFactor = MapConfig.zoom / preZoom;

	                    int newX = (int)(point.x*(zoomFactor - 1f) + zoomFactor*pos.x);
	                    int newY = (int)(point.y*(zoomFactor - 1f) + zoomFactor*pos.y);

	                	scrollPane.getViewport().setViewPosition(new Point(newX, newY));
	                	// notify observers
	                	MapConfig.getInstance().dispatchChanges(true);
                	}

                }
                else
                	scrollPane.getVerticalScrollBar().setUnitIncrement(defaultUnitV); // reactivate vertical scroll moving
            }  
         
        });

        
        // adds mouse adapter as listener for the viewport in the scrollpane received in the constructor
        scrollPane.getViewport().addMouseListener(mouseAdapter);
        scrollPane.getViewport().addMouseMotionListener(mouseAdapter);
	}
	
	/**
	 * Observer methods
	 * update when changes occur in observable objects
	 * that are being observed by this object
	 * 
	 * @author	Pedro Sampaio
	 * @since	0.7
	 */
	@Override
	public void update(Observable obs, Object arg1) {

		// change in current tool selected
		if (obs instanceof Tool) {
			// updates current tool selected
			currentTool = Tool.getInstance().getCurrentTool();
		}
		// change in current layer selected
		else if (obs instanceof MapConfig) {
			// updates current selection tiles based on selected layer
			// if a layer is selected and its different from previous one
			int newLayerIdx = mapStates.getCurrentMap().getSelectedLayer();
			if(newLayerIdx >= 0 && newLayerIdx != layerIdx) {
				// updates layer idx
				layerIdx = newLayerIdx;
				// change current reference of selected tiles to new layer's reference
				selectedMapTiles = mapStates.getCurrentMap().getLayers().get(newLayerIdx).getSelectedTiles();
			}
		}
		// change in clipboard means a paste is desired
		else if (obs instanceof Clipboard) {
			// sets bool that represents if clipboard should be used to brush tiles
			pasteFromClipboard = Clipboard.getInstance().isPaste();
		}
	}
}
