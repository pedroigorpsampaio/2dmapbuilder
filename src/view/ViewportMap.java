package view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

import controller.ViewMapControl;
import model.Clipboard;
import model.Collider;
import model.Layer;
import model.Map;
import model.MapConfig;
import model.MapState;
import model.Preferences;
import model.Tile;
import model.Tileset;
import model.TilesetConfig;
import model.Tool;
import model.Tool.SelectTools;
import test.Config;

/**
 * Represents the 2D map visualization that the user will
 * interact to create and design the maps, contained
 * in the main window of the program 2D map builder
 * Observer Pattern: Observers map data for changes
 * that occur in it (map manipulation)
 * 
 * @author	Pedro Sampaio
 * @since	0.1
 *
 */
public class ViewportMap extends JPanel implements Scrollable, Observer {

	// generated serial
	private static final long serialVersionUID = -5744314487485194780L;
	
	private Grid grid;				// grid lines to help visualization of 2D map
	@SuppressWarnings("unused")
	private ViewMapControl inputController;	// mouse input controller

	// the map data in current state updated via Map observation
	private Map map;
	// scrollable panel that contains this viewport
	private JScrollPane scrollPane;
	
	// for fps debug
	private int fps;
	private long lastTime;
	private JLabel fpsLabel;

	private int vScrollSpeed;	// jscrollpane vertical scroll move unit speed on mouse wheel
	
	private ArrayList<Tile> selectedTiles;		// current selected tiles obtained from observing tileset

	private Tileset tileset;					// current tileset being used in program
	
	Tool.SelectTools currentTool; 	// current tool used (obtained through observation of Tool class)

	private ArrayList<Tile> selectedMapTiles;  // current selected tiles obtained from observing mapstates

	private MapState mapStates;		// reference to the states of the map

	private MapPositionView mapPosDisplay; // Map position label for displaying current mouse tile position

	/**
	 * Constructor for this class
	 * 
	 * @param 	scrollPane		JScrollpane that will contain this viewport
	 * @param 	mapStates 		the map states that represents the 2d map in different states
	 * 							(multiple states for undoing and redoing operations)
	 */
    public ViewportMap(JScrollPane scrollPane, MapState mapStates) {
    	
		this.scrollPane = scrollPane;
		this.vScrollSpeed = 20;
		ArrayList<Tileset> tilesets = TilesetConfig.getInstance().getTilesets(); // gets list of tilesets
		this.tileset = tilesets.get(TilesetConfig.getInstance().getCurrentTilesetIdx()); // gets current tileset (initial)
		this.mapStates = mapStates;		// stores reference to the states of the map
	
		
		scrollPane.getVerticalScrollBar().setUnitIncrement(vScrollSpeed);
		// set viewport preferred size
		setViewportSize();
		// creates grid to be drawn on top 
		createGrid();
		// stores map reference for the current state
		this.map = mapStates.getCurrentMap();
		// creates the mouse controller for this panel
		inputController = new ViewMapControl(scrollPane, tileset, mapStates);
		
		// initializes list of selected tiles (from tileset) for visualization
		selectedTiles = new ArrayList<Tile>();
		// initializes list of selected map tiles for visualization
		selectedMapTiles = new ArrayList<Tile>();
		
		// observes the map states list for changes in states
		mapStates.addObserver(this);
		// observes changes in map configuration
		MapConfig.getInstance().addObserver(this);
		// observes changes in tileset for selected tiles info
		tileset.addObserver(this);
		// osberves changes in selected tileset on tileset config 
		TilesetConfig.getInstance().addObserver(this);
		// observes changes in clipboard to use clipboard tiles instead of tileset tiles
		Clipboard.getInstance().addObserver(this);
		// observes changes in user preferences
		Preferences.getInstance().addObserver(this);

		// gets current tool selection to proper display
		currentTool = Tool.getInstance().getCurrentTool();
		// observes changes in selected map tools for proper display
		Tool.getInstance().addObserver(this);
		
		// for fps debug
		if(Config.debug) {
			fps = 0;
			lastTime = System.currentTimeMillis();
			fpsLabel = new JLabel("FPS:" + Integer.toString(fps));
			JFrame fpsFrame = new JFrame();
			fpsFrame.setVisible(true);
			fpsFrame.setSize(80, 60);
			fpsFrame.setLocation(1024, 0);
			fpsFrame.setAlwaysOnTop(true);
			fpsFrame.setFocusable(false);
			fpsFrame.repaint();
			fpsFrame.getContentPane().add(fpsLabel);
			fpsLabel.setVisible(true);
		}
		//scrollPane.getParent().add(fpsLabel, 1);
	
		// creates Map position label for displaying current mouse tile position
		mapPosDisplay = new MapPositionView(SwingUtilities.getWindowAncestor(scrollPane));
		// display is not visible unless mouse enters viewport
		mapPosDisplay.setVisible(false);
    }
    
    /**
     * Sets the current viewport size depending on
     * map and tile size.
     * Any resizing should use this method after
     * changing tile and map size properties
     */
    private void setViewportSize() {
    	
    	this.setPreferredSize(new Dimension((MapConfig.getTileZoomed() * MapConfig.mapSizeX)+2, 
    							(MapConfig.getTileZoomed() * MapConfig.mapSizeY)+2));
	}

	/**
     * Instantiate the grid object that draws lines in 
     * a grid manner to help visualization of 2D map
     * 
     * @author 	Pedro Sampaio
     * @since 	0.1
     */
    private void createGrid() {
    	grid = new Grid();  	
    }

    /**
     * paintComponent override for panting components
     */

    Graphics2D bufferedGraphics;	// buffered graphics to draw buffered image
    BufferedImage bufferedImage;	// buffered image to drawn all drawings before on screen draw
    Graphics2D secBufferedGraphics;	// second buffered graphics to draw buffered image
    BufferedImage secBufferedImage;	// second buffered image to drawn all drawings before on screen draw
    
    @Override
    protected void paintComponent(Graphics g) {

    	// creates a buffered graphics to draw on
    	// before drawing on actual screen
    	// (Performance improvement)
        Rectangle vp = scrollPane.getViewport().getViewRect();
        int w = vp.width;
        int h = vp.height;
        // added more pixels to avoid flicker on borders
        bufferedImage = new BufferedImage((int)Math.ceil(w/MapConfig.zoom) + 5, (int)Math.ceil(h/MapConfig.zoom) + 5, BufferedImage.TYPE_INT_ARGB);
        bufferedGraphics = bufferedImage.createGraphics();
        // added more pixels to avoid flicker on borders
        secBufferedImage = new BufferedImage((int)Math.ceil(w/MapConfig.zoom) + 5, (int)Math.ceil(h/MapConfig.zoom) + 5, BufferedImage.TYPE_INT_ARGB);
        secBufferedGraphics = secBufferedImage.createGraphics();
    
       	super.paintComponent(g);
    	Graphics2D g2 = (Graphics2D)g.create();
    	g2.scale(MapConfig.zoom, MapConfig.zoom);

       
    	// calculates fps
    	float deltaTime = (System.currentTimeMillis() - lastTime) / 1000f;
  
    	if(deltaTime > 0)
    		fps = Math.round ( 1 / (deltaTime));
    	
    	// updates last time called for fps debug
    	lastTime = System.currentTimeMillis();
    	
    	// updates label fps
    	if(Config.debug)
    		fpsLabel.setText("FPS:" + Integer.toString(fps));
    	
    	// draw background in user preference color
    	paintBackground(g2);
    	
    	// draw map
    	//creates a copy of the Graphics instance
    	Graphics2D g2d = (Graphics2D) g.create();
    	
    	// draw map tiles
    	drawTiles(g2, g2d);

    	// draw visualization of selected tiles if there are any
    	// and also if mouse position is in map's viewport
    	// only draws if brush tool is the selected one
    	if(selectedTiles.size() > 0 && MapConfig.isMouseOnViewport() && currentTool == SelectTools.BRUSH)
    		drawSelectedTiles(g2);
    	
    	// if eraser tool is the one selected, draws a rect
    	// float over the tile to be erased with transparency
    	if(currentTool == SelectTools.ERASER)
    		drawEraseRect(g2);
    	
    	if(currentTool == SelectTools.SELECTION)
    		selectMapTiles(g2);
    	
    	// dispose copies
    	if(g2 != null)
    		g2.dispose();
    	if(g2d != null)
    		g2d.dispose();
    	if(bufferedGraphics != null)
    		bufferedGraphics.dispose();
    	if(secBufferedGraphics != null)
    		secBufferedGraphics.dispose();

    }

    /**
     * Simple method that draws a transparent
     * rectangle on map's selection color over
     * the tile that mouse is on to display what
     * tile will be erased by the eraser tool
     * 
     * @author 	Pedro Sampaio
     * @since	0.7
     * @param	g		scaled graphics component depending on zoom
     */
	private void drawEraseRect(Graphics2D g) {
		
		// creates a copy of the graphics component
		Graphics2D gCpy = (Graphics2D) g.create(); 
		// sets color of selection with map selection color pref
		gCpy.setColor(Preferences.mapSelectionColor);
		// applies transparency to the drawing
    	AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, MapConfig.preAlpha * 0.75f);
    	gCpy.setComposite(ac);
		// draws the rectangle on current tile below mouse
		gCpy.fillRect(Tool.getInstance().getEraseTilePoint().x * MapConfig.tileSize, 
						Tool.getInstance().getEraseTilePoint().y* MapConfig.tileSize,  MapConfig.tileSize,  MapConfig.tileSize);
		// dispose copy of graphics component
		gCpy.dispose();
	}

	/**
     * Draws a visualization of selected tiles in the map viewport
     * on mouse position with transparency to not block the actual map vision
     * 
     * @author	Pedro Sampaio
     * @since	0.5
     * @param g				graphics component
     * @param unscaledG		graphics component unscaled
     */
    private void drawSelectedTiles(Graphics2D g2) {
    	
    	// should have at list  one selected tile and mouse must be in viewport
    	assert(selectedTiles.size() > 0);
    	assert(MapConfig.isMouseOnViewport());
    	
    	//creates a copy of the Graphics instance
    	Graphics2D g2Alpha = (Graphics2D) g2.create();
    	Graphics2D g3Alpha = (Graphics2D) g2.create();
	
    	// applies alpha to the drawing
    	AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, MapConfig.preAlpha);
    	g2Alpha.setComposite(ac);
    	// creates another graphic with different alpha for merging a colored 
    	// rect with tile and give the tiles another color
    	AlphaComposite ac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, MapConfig.preAlpha/2);
    	g3Alpha.setComposite(ac2);
    	g3Alpha.setColor(new Color(0.25f, 0.66f, 1f));
    	
    	// iterates through selected tiles to drawn pre-visualization
    	for(int i = 0; i < selectedTiles.size() ; i++) {
    		Tile tile = selectedTiles.get(i);	// iteration tile
    		//only draws if tile has all needed info
    		if(tile.isComplete()) {
				// clamps for image source bounds
				int sImgX = tile.getIndexJ() * tile.getTileSize(); int sImgY = tile.getIndexI() * tile.getTileSize();
				int tileSizeX = tile.getTileSize(); int tileSizeY = tile.getTileSize();
				if(tileSizeX > tile.getTileset().getImage().getWidth())
					tileSizeX = tile.getTileset().getImage().getWidth();
				if(tileSizeY > tile.getTileset().getImage().getHeight())
					tileSizeY = tile.getTileset().getImage().getHeight();
				if(sImgX + tileSizeX > tile.getTileset().getImage().getWidth()) // raster limit on X
					sImgX = (tile.getTileset().getImage().getWidth() - tileSizeX);
   				if(sImgY + tileSizeY > tile.getTileset().getImage().getHeight()) // raster limit on Y
   					sImgY = (tile.getTileset().getImage().getHeight() - tileSizeY);
   				if(sImgX < 0) sImgX = 0;
   				if(sImgY < 0) sImgY = 0;
   				
    			// gets the subimage that represents the tile in the tileset
				BufferedImage tImage = tile.getTileset().getImage()
										.getSubimage(sImgX, sImgY, tileSizeX, tileSizeY);
				
				int x = (int) ((tile.getDrawJ() * MapConfig.tileSize));
				int y = (int) ((tile.getDrawI() * MapConfig.tileSize));
				boolean isVisible = isPointVisible(new Point(x,y));
				
				// color image for better visualization
				//BufferedImage coloredImage = colorImage(0.8f,0.7f,1f, tImage);
  				
				// draws the subimage(tile) if it is visible on extended view
				if(isVisible) {
					bufferedGraphics.drawImage(tImage, x, y, null); 
					g2Alpha.drawImage(toCompatibleImage(tImage), x, y, null); 
					// merge with a rect to change colors of visualization
					g3Alpha.fillRect(x, y, tImage.getWidth(), tImage.getHeight());
				}
    		}
    	}
    	
    	// dispose copies
    	g2Alpha.dispose();
    	g3Alpha.dispose();
	}
    
    /**
     * Colors an image with specified color.
     * @author therealfarfetchd
     * @param r Red value. Between 0 and 1
     * @param g Green value. Between 0 and 1
     * @param b Blue value. Between 0 and 1
     * @param src The image to color
     * @return The colored image
     */
    protected BufferedImage colorImage(float r, float g, float b, BufferedImage src) {

        // Copy image ( who made that so complicated :< )
        BufferedImage newImage = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TRANSLUCENT);
        Graphics2D graphics = newImage.createGraphics();
        graphics.drawImage(src, 0, 0, null);
        graphics.dispose();

        // Color image
        for (int i = 0; i < newImage.getWidth(); i++) {
            for (int j = 0; j < newImage.getHeight(); j++) {
                int ax = newImage.getColorModel().getAlpha(newImage.getRaster().getDataElements(i, j, null));
                int rx = newImage.getColorModel().getRed(newImage.getRaster().getDataElements(i, j, null));
                int gx = newImage.getColorModel().getGreen(newImage.getRaster().getDataElements(i, j, null));
                int bx = newImage.getColorModel().getBlue(newImage.getRaster().getDataElements(i, j, null));
                rx *= r;
                gx *= g;
                bx *= b;
                newImage.setRGB(i, j, (ax << 24) | (rx << 16) | (gx << 8) | (bx << 0));
            }
        }
        return newImage;
    }

	/**
     * Draws existent tiles in all layers of the map
     * Tiles are surrounded with a dashed contour if 
     * user decided to visualize the grid
     * 
     * @author	Pedro Sampaio
     * @since	0.5
     * @param g				graphics component
     * @param unscaledG		graphics component unscaled
     */
    private void drawTiles(Graphics g, Graphics unscaledG) {
		// gets map layers
    	ArrayList<Layer> layers = map.getLayers();
    	// layers list size cant be bigger than max layers
    	assert(layers.size() <= MapConfig.getInstance().getMaxLayers());
    	// creates a copy of graphics component
    	Graphics2D gCpy = (Graphics2D) g.create();
    	// view rectangle from viewport
		Rectangle viewRect = scrollPane.getViewport().getViewRect();
		Rectangle zoomRect = new Rectangle((int)(viewRect.x/MapConfig.zoom), (int)(viewRect.y/MapConfig.zoom),
											(int)(viewRect.width/MapConfig.zoom), (int)(viewRect.height/MapConfig.zoom));
		
		//int offset_x = math.floor(camera.pos_x % tileSize)
		int first_tile_x = (int) Math.floor(zoomRect.x / MapConfig.tileSize);
		int first_tile_y = (int) Math.floor(zoomRect.y / MapConfig.tileSize);
		int offset_x = (int) Math.floor(zoomRect.x % MapConfig.tileSize);
		int offset_y = (int) Math.floor(zoomRect.y % MapConfig.tileSize);
		int last_tile_x = (int) Math.ceil(zoomRect.width / MapConfig.tileSize) + 2; //  + 2 makes sure that we have enough tiles for smooth transition
		int last_tile_y = (int) Math.ceil(zoomRect.height /MapConfig.tileSize) + 2;  // + 2 makes sure that we have enough tiles for smooth transition
    	
    	// iterates through layers drawing tiles one by one
    	for(int l = 0; l < layers.size(); l++ ) {
        	// applies layers current transparency to the drawing
        	AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, layers.get(l).getOpacity());
        	bufferedGraphics.setComposite(ac);
    		// gets tiles in current layer
    		Tile[][] lTiles = layers.get(l).getTiles();

    		// make sure not to go out of bounds
			if(last_tile_x > MapConfig.mapSizeX)
				last_tile_x = MapConfig.mapSizeX;
			if(last_tile_y > MapConfig.mapSizeY)
				last_tile_y = MapConfig.mapSizeY;
			
    		// draws each tile
    		for(int i = 0; i < last_tile_y; i++) {
    			for(int j = 0; j < last_tile_x; j++) {

    				int dataI = first_tile_y + i;
    				int dataJ = first_tile_x + j;
    				
    				// make sure not to go out of bounds
    				if(dataI < 0)
    					dataI = 0;
    				if(dataJ < 0)
    					dataJ = 0;
    				if(dataI >= MapConfig.mapSizeY)
    					dataI = MapConfig.mapSizeY - 1;
    				if(dataJ >= MapConfig.mapSizeX)
    					dataJ = MapConfig.mapSizeX - 1;
    				
    				// if there are no tiles in position, dont draw nothing
    				if(lTiles[dataI][dataJ] != null) {
        				// gets tilesize for cutting the tile in tileset
        				int tSize = lTiles[dataI][dataJ].getTileSize();
        				// gets indexes of tile in tileset
        				int tIdxI = lTiles[dataI][dataJ].getIndexI();
        				int tIdxJ = lTiles[dataI][dataJ].getIndexJ();	
        				
        				// clamps for image source bounds
        				int sImgX = tIdxJ * tSize; int sImgY = tIdxI * tSize;
        				int tileSizeX = tSize; int tileSizeY = tSize;
        				if(tileSizeX > lTiles[dataI][dataJ].getTileset().getImage().getWidth())
        					tileSizeX = lTiles[dataI][dataJ].getTileset().getImage().getWidth();
        				if(tileSizeY > lTiles[dataI][dataJ].getTileset().getImage().getHeight())
        					tileSizeY = lTiles[dataI][dataJ].getTileset().getImage().getHeight();
        				if(sImgX + tSize > lTiles[dataI][dataJ].getTileset().getImage().getWidth()) // raster limit on X
        					sImgX = (lTiles[dataI][dataJ].getTileset().getImage().getWidth() - tileSizeX);
           				if(sImgY + tSize > lTiles[dataI][dataJ].getTileset().getImage().getHeight()) // raster limit on Y
           					sImgY = (lTiles[dataI][dataJ].getTileset().getImage().getHeight() - tileSizeY);
           				if(sImgX < 0) sImgX = 0;
           				if(sImgY < 0) sImgY = 0;
        				
        				// gets the subimage that represents the tile in the tileset
        				BufferedImage tImage = lTiles[dataI][dataJ].getTileset().getImage()
        										.getSubimage(sImgX, sImgY, tileSizeX, tileSizeY);
        				
        				int x = (int) ((j * MapConfig.tileSize) - offset_x);
        				int y = (int) ((i * MapConfig.tileSize) - offset_y);
        				boolean isVisible = true;//isPointVisible(new Point(x,y));
          				
        				// draws the subimage(tile) if it is visible on extended view
        				if(isVisible) {
        					bufferedGraphics.drawImage(tImage, x, y, null); 
        					// draw contour grid if user wants it to be drawn (and if its top layer tile)
		    				if(Preferences.viewportShowGrid && l == layers.size()-1)
		    					grid.paintContourGrid(secBufferedGraphics, new Rectangle((int)(x),(int)(y),
				    											MapConfig.tileSize, MapConfig.tileSize));
        				}
    				}
    				else {
						// draws grid for each tile (even if null)
						// draw contour grid if user wants it to be drawn 
    					int x = (int) ((j * MapConfig.tileSize) - offset_x);
        				int y = (int) ((i * MapConfig.tileSize) - offset_y);
        				boolean isVisible = true;//isPointVisible(new Point(x,y));
        				// only draw grid if its is on viewport (visible)
        				if(isVisible) {
		    				if(Preferences.viewportShowGrid && l == layers.size()-1)
		    					grid.paintContourGrid(secBufferedGraphics, new Rectangle((int)(x),(int)(y),
										MapConfig.tileSize, MapConfig.tileSize));
        				}
        				
    				}
    				

    				int x = (int) ((j * MapConfig.tileSize) - offset_x);
    				int y = (int) ((i * MapConfig.tileSize) - offset_y);
    				
    				// draw visualization of collider if it exists in tile
    				Collider collider = mapStates.getCurrentMap().getColliders()[dataI][dataJ];
    				if(collider != null) {
    					if(collider.isTrigger()) // if it is a trigger, draw trigger symbol
    						drawCollider(bufferedGraphics, true, new Rectangle((int)(x),(int)(y),
		    											MapConfig.tileSize, MapConfig.tileSize));
    					else // draws physical collider symbol
    						drawCollider(bufferedGraphics, false, new Rectangle((int)(x),(int)(y),
									MapConfig.tileSize, MapConfig.tileSize));
    				}

    			}
    		}
    		
    		
    	}

		// draws bufferedimage containing the map for the current viewport position
    	gCpy.drawImage(toCompatibleImage(bufferedImage), (int) (viewRect.x / MapConfig.zoom), (int) (viewRect.y / MapConfig.zoom), this);

    	// draws grid (scaled)
    	((Graphics2D) unscaledG).scale(MapConfig.zoom, MapConfig.zoom);
    	unscaledG.drawImage(toCompatibleImage(secBufferedImage), (int) (viewRect.x  / MapConfig.zoom), (int) (viewRect.y  / MapConfig.zoom), this);
	}
    
    /**
     * Draws symbols that represents if a tile
     * has a trigger or a physical collider attached
     * 
     * @param g2d 		Graphics2D object to draw
     * @param isTrigger	if it is a trigger collider or a physical collider
     * @param rect 		rectangle containing coordinates for the drawing
     */
    private void drawCollider(Graphics2D g2d, boolean isTrigger, Rectangle rect) {
    	if(isTrigger) {
    		g2d.setColor(Color.BLACK);
    		g2d.drawString("T", (int)(rect.x+rect.getWidth()/1.5f), (int)(rect.y+rect.getHeight()));
    		g2d.setColor(Color.CYAN);
    		g2d.drawString("T", (int)(rect.x+rect.getWidth()/1.5f)+1, (int)(rect.y+rect.getHeight()+1));
    	} else {
    		g2d.setColor(Color.BLACK);
    		g2d.drawString("P", (int)(rect.x+rect.getWidth()/1.5f), (int)(rect.y+rect.getHeight()));
    		g2d.setColor(Color.WHITE);
    		g2d.drawString("P", (int)(rect.x+rect.getWidth()/1.5f)+1, (int)(rect.y+rect.getHeight()+1));
    	}

	}

	/**
     * Checks if point is visible on viewport (tilesize extended)
     * 
     * @author	Pedro Sampaio
     * @since  	0.5b
     * @param 	p		point to check if is visible in extended view rect
     * @return	a boolean that represents if point is visible on viewport (extended) or not
     */
    private boolean isPointVisible(Point p) {
    	// check if view contains tile to be drawn
		// gets viewport rectangle
		Rectangle viewRect = scrollPane.getViewport().getViewRect();
		// extends rectangle by one tilesize(zoom applied) to be able to draw transition of tile
		Rectangle extViewRect = new Rectangle((int) (viewRect.x/MapConfig.zoom) - MapConfig.getTileZoomed(),
												(int) (viewRect.y/MapConfig.zoom) - MapConfig.getTileZoomed(), 
												(int) (viewRect.width/MapConfig.zoom)  + MapConfig.getTileZoomed(),
												(int) (viewRect.height/MapConfig.zoom) + MapConfig.getTileZoomed());
		// return if tile is visible in extended view rect
		return extViewRect.contains(p);
    }

	/**
     * Paints the background of viewport in user's preference color (only visible part)
     * 
     * @param g		Graphics component received via paintComponent
     */
    private void paintBackground(Graphics g) {
    	
    	g.setColor(Preferences.viewportBackgroundColor);
    	Rectangle viewRect = scrollPane.getViewport().getViewRect();

    	g.fillRect((int) (viewRect.x/MapConfig.zoom), (int) (viewRect.y/MapConfig.zoom),
					(int)Math.ceil(viewRect.width/MapConfig.zoom) + 2 , (int)Math.ceil(viewRect.height/MapConfig.zoom) + 2);// added more pixels to avoid flicker on borders
    			

	}
    
    /**
     * Creates a compatible image for performance improvement
     * @author Consty
     * @param image	The image to create a compatible image
     * @return	the compatible image
     * @since 0.5b
     */
    private BufferedImage toCompatibleImage(BufferedImage image)
    {
        // obtain the current system graphical settings
        GraphicsConfiguration gfx_config = GraphicsEnvironment.
            getLocalGraphicsEnvironment().getDefaultScreenDevice().
            getDefaultConfiguration();

        /*
         * if image is already compatible and optimized for current system 
         * settings, simply return it
         */
        if (image.getColorModel().equals(gfx_config.getColorModel()))
            return image;

        // image is not optimized, so create a new image that is
        BufferedImage new_image = gfx_config.createCompatibleImage(
                image.getWidth(), image.getHeight(), image.getTransparency());

        // get the graphics context of the new image to draw the old image on
        Graphics2D g2d = (Graphics2D) new_image.getGraphics();

        // actually draw the image and dispose of context no longer needed
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // return the new optimized image
        return new_image; 
    }
    
    /**
	 * Creates a visualization for the selected map tiles
	 * to inform visually the user what tiles are 
	 * currently selected for interactions
	 * 
	 * @author	Pedro Sampaio
	 * @since	0.9
	 * @param 	g		graphic component for painting
	 */
	private void selectMapTiles(Graphics2D g) {
		
		// iterates through list of selected tiles
		for(int i = 0; i < selectedMapTiles.size(); i++) {
			// gets current iteration tile
			Tile tile = selectedMapTiles.get(i);

			//creates a copy of the Graphics instance
	    	Graphics2D g2d = (Graphics2D) g.create();
	    	
	    	// applies alpha to the drawing
	    	AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
	    	g2d.setComposite(ac);
	    	
	    	// creates visualization for the current iteration tile
	    	g2d.setColor(Preferences.mapSelectionColor);
	    	g2d.fillRect(tile.getIndexJ() * MapConfig.tileSize, tile.getIndexI() *  MapConfig.tileSize, 
	    			 MapConfig.tileSize,  MapConfig.tileSize);
		}
	}

	/**
     * Scrollable overrides to set some scroll configurations
     * for the viewport scrolling in the JScrollPane
     */
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		// TODO Auto-generated method stub
		return new Dimension(MapConfig.getTileZoomed() * MapConfig.mapSizeX, MapConfig.getTileZoomed() * MapConfig.mapSizeY);
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		// TODO Auto-generated method stub
		return 10;
	}
	
	/**
	 * Updates the current tileset
	 * selected by player adjusting
	 * necessary operations
	 * 
	 * @author Pedro Sampaio
	 * @param newTileset	the newly selected tileset to observe
	 */
	public void updateTileset(Tileset newTileset) {
		// reinitializes list of selected tiles (from tileset) for visualization
		selectedTiles = new ArrayList<Tile>();
		
		// stop observing changes in old tileset
		tileset.deleteObserver(this);
		
		// sets new tileset reference
		tileset = newTileset;
		
		// starts observing the new tileset
		newTileset.addObserver(this);
	}

	/**
	 * Observer methods
	 * update when changes occur in observable objects
	 * that are being observer by this object
	 * 
	 * @author	Pedro Sampaio
	 * @since	0.5
	 */
	@Override
	public void update(Observable obs, Object arg) {
		
		// if map object has updates, adjust visualization
		if (obs instanceof MapState) {
			// stores new map state to draw the new visualization
			map = ((MapState) obs).getCurrentMap();
			
			// if there is a layer selected, update selected map tiles
			int obsLayer = map.getSelectedLayer();

			if(obsLayer >= 0) {
				selectedMapTiles = map.getLayers().get(obsLayer).getSelectedTiles();
			}

		}
		else if (obs instanceof MapConfig) {
			// updates view preferred size on account of zoom changes
			setViewportSize();
			
			// update scroll by camera
			MapConfig mConfig = MapConfig.getInstance();
			
			int hBarMove = (int) (scrollPane.getHorizontalScrollBar().getValue()+mConfig.getMoveX());
			int vBarMove = (int) (scrollPane.getVerticalScrollBar().getValue()+ mConfig.getMoveY());

			scrollPane.getHorizontalScrollBar().setValue(hBarMove);
			scrollPane.getVerticalScrollBar().setValue(vBarMove);
			
			// display of current mouse tile position on map
			if(MapConfig.isMouseOnViewport()) { // if mouse its on map viewport, display current position
				mapPosDisplay.setVisible(true); // sets visible
				// updates current tile position label text
				Point tPos = new Point(MapConfig.getMouseTilePosition(scrollPane.getViewport().getViewPosition()));
				mapPosDisplay.updateText("["+tPos.x+","+tPos.y+"]");
			}
			else								// else, don't
				mapPosDisplay.setVisible(false);
			
			// revalidates on account of viewport resizes
			revalidate();
		}
		// if tileset object has updates, adjust pre visualization
		else if (obs instanceof Tileset) {
			Tileset ts = (Tileset) obs;
			// stores selected tiles to draw the new pre visualization
			selectedTiles = ts.getSelectedTiles();
		}
		// if clipboard is to be previewed on map instaed of tileset
		else if (obs instanceof Clipboard) {
			// stores copied tiles from clipboard to draw the new pre visualization
			selectedTiles = Clipboard.getInstance().getCopiedTiles();
			
			// visualizing debug
			if(Config.debug) {
				System.out.println("ViewMap: Visualizaing "+selectedTiles.size()+" tiles from clipboard");
			}
		}
		else if (obs instanceof Tool){
			// updates current selected tool
			currentTool = (((Tool) obs).getCurrentTool());
		}
		else if (obs instanceof TilesetConfig){
			// updates current selected tileset
			updateTileset(TilesetConfig.getInstance().getCurrentTileset());
		}
		
		repaint();
		
	}

}
