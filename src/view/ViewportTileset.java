package view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

import controller.ViewTilesetControl;
import model.Preferences;
import model.Tile;
import model.Tileset;

/**
 * Represents the 2D map visualization of the tilesets
 * that the user will use to aid the creation of the maps.
 * Contained in the tileset scrollable panel
 * Observer Pattern: Observers current tileset for 
 * selected tiles changes 
 * 
 * @author	Pedro Sampaio
 * @since	0.2
 *
 */
public class ViewportTileset extends JPanel implements Scrollable, Observer {

	// generated serial
	private static final long serialVersionUID = -5744314487485194780L;
	
	private int tileSize;			// size of a tile in the current tileset
	@SuppressWarnings("unused")
	private String name;			// name of the current tileset
	private Image image;			// Image of the current tileset
	private int imageWidth;			// width of the current tileset image 
	private int imageHeight;		// height of the current tileset image 
	private Grid grid;				// grid lines to help visualization of 2D map
	
	@SuppressWarnings("unused")
	private ViewTilesetControl inputController;

	private ArrayList<Tile> selectedTiles;		// current selected tiles obtained from observing tileset

	@SuppressWarnings("unused")
	private Tileset tileset;	// the current tileset loaded

	/**
	 * Constructor for this class
	 * 
	 * @param scrollPane			// JScrollpane that will contain this viewport
	 * @param tileset				// Tileset that will be shown in this viewport
	 */
    public ViewportTileset(JScrollPane scrollPane, Tileset tileset) {
       this.tileSize = tileset.getTileSize();
       this.name = tileset.getName();
       this.image = tileset.getImage();
       this.imageWidth = image.getWidth(null);
       this.imageHeight = image.getHeight(null);
       this.tileset = tileset;

       // absolute positioning for the viewport
       setLayout(null);
       // set viewport preferred size
       setViewportSize();
       // creates grid to be drawn on top of tileset 
       createGrid();
       // stores the mouse controller for this panel
       this.inputController = new ViewTilesetControl(scrollPane, tileset);
       // observes the tileset class for changes
       tileset.addObserver(this);
       // observes changes in preferences for immediate preview
       Preferences.getInstance().addObserver(this);
       // initializes list of selected tiles for visualization
       selectedTiles = new ArrayList<Tile>();
    }
    
    /**
     * Sets the current viewport size depending on
     * map and tile size.
     * Any resizing should use this method after
     * changing tile and map size properties
     * 
     * @author 	Pedro Sampaio
     * @since	0.2
     */
    private void setViewportSize() {
    	this.setPreferredSize(new Dimension(imageWidth, imageHeight));
	}

	/**
     * Instantiate the grid object that draws lines in 
     * a grid manner to help visualization of 2D map
     * 
     * @author 	Pedro Sampaio
     * @since 	0.2
     */
    private void createGrid() {
    	grid = new Grid(tileSize, (imageWidth/tileSize)+1, (imageHeight/tileSize)+1, Color.YELLOW);  	
    }

    /**
     * paintComponent override for panting components
     */
    @Override
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	
    	// draws tileset image
    	g.drawImage(image, 0, 0, null);
    	
    	// draw grids anyway - grid toggle on preferences are for map only
    	grid.paintGrid(g); 		
    	
    	// draws visualization for the selected tiles
    	SelectTiles(g);
    }
    
	/**
	 * Creates a visualization for the selected tiles
	 * to inform visually the user what tiles are 
	 * currently selected for interactions
	 * 
	 * @author	Pedro Sampaio
	 * @since	0.3
	 * @param 	g		graphic component for painting
	 */
	private void SelectTiles(Graphics g) {

		// iterates through list of selected tiles
		for(int i = 0; i < selectedTiles.size(); i++) {
			// gets current iteration tile
			Tile tile = selectedTiles.get(i);

			//creates a copy of the Graphics instance
	    	Graphics2D g2d = (Graphics2D) g.create();
	    	
	    	// applies alpha to the drawing
	    	AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
	    	g2d.setComposite(ac);
	    	
	    	// creates visualization for the current iteration tile
	    	g2d.setColor(Preferences.selectionColor);
	    	g2d.fillRect(tile.getIndexJ() * tile.getTileSize(), tile.getIndexI() * tile.getTileSize(), 
	    					tile.getTileSize(), tile.getTileSize());
		}
	}

	/**
     * Scrollable overrides to set some scroll configurations
     * for the viewport scrolling in the JScrollPane
     */
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		// TODO Auto-generated method stub
		return new Dimension(imageWidth, imageHeight);
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
	 * Observer methods
	 * update when changes occur in observable object 
	 * 
	 * @author	Pedro Sampaio
	 * @since	0.3
	 */
	@Override
	public void update(Observable obs, Object arg) {
		
		// if tileset object has updates, adjust visualization
		if (obs instanceof Tileset) {
			Tileset ts = (Tileset) obs;
			// stores selected tiles to draw the new visualization
			selectedTiles = ts.getSelectedTiles();
			// repaints on account of changes
			repaint();
		}
		else if(obs instanceof Preferences) {
			// repaint on account of preferences changes for immediate preview
			repaint();
		}

	}

}
