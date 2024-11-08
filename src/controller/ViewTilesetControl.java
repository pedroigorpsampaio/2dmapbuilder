package controller;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

import model.Clipboard;
import model.Preferences;
import model.Tile;
import model.Tileset;
import model.Tool;
import model.Tool.SelectTools;
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
public class ViewTilesetControl implements MouseListener, MouseMotionListener {
	
	
	/**
	 * Mouse controls
	 */
	final int dragButton 	= MouseEvent.BUTTON2;		// middle mouse button controls drag movements of the map
	final int selectButton	= MouseEvent.BUTTON1;		// left mouse button for select interactions with the map
	int pressedButton;									// the last pressed button
	
	/**
	 * Mouse cursors
	 */
	
	Cursor defaultCursor;		// the default cursor
	Cursor grabCursor;			// drag map cursor	
	
	/**
	 * Tile infos
	 */
	private ArrayList<Tile> selectedTiles;		// current selected tiles 
	private Tileset	tileset;					// current tileset being used
	private JScrollPane scrollPane;				// reference to the scrollpane
	
	/**
	 * Creates the adapter for mouse interactions and its callbacks
	 * adding it to the scrollPane as a mouse and a motion listener
	 * Stores the current tileset used in viewport
	 * 
	 * @param scrollPane	JScrollPane that contains the viewport for map visualization
	 * @param tileset 		the current tileset loaded
	 */
	public ViewTilesetControl(JScrollPane scrollPane, Tileset tileset) {
		
		// stores current tileset
		this.tileset = tileset;
		// stores scroll pane
		this.scrollPane = scrollPane;
		
		// initializes selectedTiles with no tiles selected
		selectedTiles = new ArrayList<Tile>();
		
		// sets tileset selectedTiles reference to be observed by viewers
		tileset.setSelectedTiles(selectedTiles);
		
		// sets mouse cursors
		defaultCursor = scrollPane.getCursor();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
 
		Image image = toolkit.getImage(ViewTilesetControl.class.getResource("/resources/grabIcon.png"));

		try {
			grabCursor = toolkit.createCustomCursor(image , new Point(0, 
													0), "img");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ViewTilesetControl: Could not create a custom cursor");
		}
        
        // adds mouse adapter as listener for the viewport in the scrollpane received in the constructor
        scrollPane.getViewport().addMouseListener(this);
        scrollPane.getViewport().addMouseMotionListener(this);
	}
	
	
	/**
	 * Returns a bool representing if two points
	 * are in the same tile or not
	 * 
	 * @author	Pedro Sampaio	
	 * @since	0.4
	 * @param 	p1		first point of comparison
	 * @param	p2		second point of comparison
	 * @return 			a bool that contains true if points are in the 
	 * 					tile, and false otherwise
	 */
	private boolean isSameTile(Point p1, Point p2) {
		
		// converts points (x,y cartesian coords) to tile indexes in our data structure
		Point tOriginIdx = new Point(p1.x / tileset.getTileSize(), 
									 p1.y / tileset.getTileSize());
		Point tDestinyIdx = new Point(p2.x / tileset.getTileSize(), 
									  p2.y / tileset.getTileSize());
    	// if destiny tile is equal of origin after 
		// tile transformation, its the same tile
    	if(tOriginIdx.x == tDestinyIdx.x && tOriginIdx.y == tDestinyIdx.y)
    		return true;
    	else
    		return false;
	}
	
	// mouse callbacks to be used as controller of the scroll panel that contains viewport
	
	private Point origin;				// origin point of click
	private Point destiny;				// destiny point of click after drag
	private Point shiftOrigin;			// origin point of click when shift is pressed

    @Override
    public void mousePressed(MouseEvent e) {
    	
    	// if a mouse is pressed on tileset, give back brush focus 
    	// to tileset tiles instead of clipboard copied tiles
    	// if focus is on clipboard
    	if(Clipboard.getInstance().isPaste()) {
        	Clipboard.getInstance().setPaste(false);
        	Clipboard.getInstance().dispatchChanges();
    	}
    	
    	// updates last pressed button
    	pressedButton = e.getButton();
    	
    	if(Config.debug) {
    		System.out.println("Tileset: A tile was selected from tileset: "+ tileset.getName());
    	}
    	
    	// gets click position relative to the scroll
    	Point relativeClick = new Point(e.getPoint().x + scrollPane.getViewport().getViewPosition().x,
    									e.getPoint().y + scrollPane.getViewport().getViewPosition().y);
    	
    	// log relative click position in tileset viewport if debug is enabled
    	if(Config.debug)
    		System.out.println("Tileset clicked in pos: " + relativeClick);
    	
    	// updates origin for dragging movement
		origin = new Point(e.getPoint());
		
		// updates shift origin if shift is not pressed
		if(!KeyboardControl.isShiftPressed())
			shiftOrigin = relativeClick;
    	
    	switch(e.getButton())
    	{
    		case dragButton:
            	// change to grab cursor
            	scrollPane.setCursor(grabCursor);
        		break;
    		case selectButton:
    			// pass control to select method to add tile to selected tiles
    			Tool.SelectTile(relativeClick, tileset, selectedTiles, shiftOrigin, false);
            	// makes sures that brush will be enabled for painting
            	Tool.getInstance().setCurrentTool(SelectTools.BRUSH, true);
    			break;
    		default:
    			if(Config.debug)
    				System.out.println("ViewTilesetControl.mouseAdapter.mousePressed: unknown mouse button pressed");
    			break;
    	}

    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
    	scrollPane.setCursor(defaultCursor);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {

    	// gets viewport reference
    	JViewport viewPort = scrollPane.getViewport();
    	
    	// drags viewport screen if dragButton is the pressed button
        if (origin != null && pressedButton == dragButton) {
            
            if (viewPort != null) {
                int deltaX = origin.x - e.getX();
                int deltaY = origin.y - e.getY();

                Rectangle view = viewPort.getViewRect();
                view.x = (int)(deltaX * Preferences.viewportMouseSensitivityX * 1/60f);
                view.y = (int)(deltaY * Preferences.viewportMouseSensitivityY * 1/60f);
                
                viewPort.scrollRectToVisible(view);
            }
           
        }
        // update data to be ready for rectangle selection 
        else if (origin != null && pressedButton == selectButton) {
        	
        	// dont do drag selection if ctrl or clicked are pressed
        	if(KeyboardControl.isCtrlPressed() || KeyboardControl.isShiftPressed())
        		return;
        	
        	destiny = new Point(e.getX(),  e.getY());
        	
        	// if origin and destiny are from the same tile, dont bother doing rect select
        	if (isSameTile(origin, destiny)) {
        		//selects destiny tile for cases that are returning from different tiles selection
        		Point relativeDestiny = new Point(destiny.x + scrollPane.getViewport().getViewPosition().x,
        										  destiny.y + scrollPane.getViewport().getViewPosition().y);
        		Tool.SelectTile(relativeDestiny, tileset, selectedTiles, shiftOrigin, false);
            	// makes sures that brush will be enabled for painting
            	Tool.getInstance().setCurrentTool(SelectTools.BRUSH, true);
        		return;
        	}
        	         	
        	// gets origin and destiny position relative to the scroll
        	if (viewPort != null) {
            	
            	Point relativeOrigin = new Point(origin.x + viewPort.getViewPosition().x,
            									 origin.y + viewPort.getViewPosition().y);
            	Point relativeDestiny = new Point(destiny.x + viewPort.getViewPosition().x,
            									  destiny.y + viewPort.getViewPosition().y);

            	// creates the rectangular selection passing the relatives origin and destiny of mouse drag
            	Tool.RectSelect(relativeOrigin, relativeDestiny, tileset, selectedTiles, 
            					tileset.getTileSize(), tileset.getTileSizeX(), tileset.getTileSizeY());
            	// makes sures that brush will be enabled for painting
            	Tool.getInstance().setCurrentTool(SelectTools.BRUSH, true);
            	// notify observers of tileset that a change has occurred
            	tileset.selectedTilesDispatchChanges();
        	}
        	else
        		System.out.println("ViewTilesetControl.mouseAdapter.mouseDragged: Could not reference viewport");
        }
    }    
	
	/**
	 * @return the current selected tiles
	 */
	public ArrayList<Tile> getSelectedTiles() {
		return selectedTiles;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
