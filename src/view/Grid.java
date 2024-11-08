package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import model.MapConfig;

/**
 * Draws a grid on the map viewport to help visualizing and
 * creating the 2D map with 2D Map Builder
 * 
 * @author  	Pedro Sampaio
 * @since		0.1
 *
 */
public class Grid extends JPanel {
	
	// generated serial
	private static final long serialVersionUID = -5460226037686646275L;
	private int tileSize;
	private int mapSizeX;
	private int mapSizeY;
	private Color lineColor;

	/**
	 * Default Constructor
	 */
    public Grid() {
        this.tileSize = MapConfig.tileSize;
        this.mapSizeX = MapConfig.mapSizeX;
        this.mapSizeY = MapConfig.mapSizeY;
        this.lineColor = Color.BLACK;
    }
    
    /**
     * Constructor with grid parameters
     * 
     * @param tileSize		// size of the tile to consider for grid drawing
     * @param mapSizeX		// number of tiles in x-axis to consider for grid drawing
     * @param mapSizeY		// number of tiles in y-axis to consider for grid drawing
     * @param color			// color of line to be drawn
     */
    public Grid(int tileSize, int mapSizeX, int mapSizeY, Color color) {
        this.tileSize = tileSize;
        this.mapSizeX = mapSizeX;
        this.mapSizeY = mapSizeY;
        this.lineColor = color;
    }

    /**
     * Grid drawing on visible viewport
     * @param bufferedGraphics	buffered graphics to drawn on  
     * @param tileRect  tile rectangle on screen to draw contour
     */
    protected void paintContourGrid(Graphics2D bufferedGraphics, Rectangle tileRect) {
		
		// set color of the lines in grid
    	bufferedGraphics.setColor(lineColor);			
		//set the stroke of the copy, not the original 
		Stroke dashed = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);
		// sets stroke
		bufferedGraphics.setStroke(dashed);
		// draws on buffered image the dashed rectangle
		bufferedGraphics.draw(new Rectangle2D.Double(tileRect.x, tileRect.y, tileRect.width, tileRect.height));

    }
    
    /**
     * Grid drawing in all viewport
     * @param g	graphics component
     */
    protected void paintGrid(Graphics g) {
		
		// set color of the lines in grid
		g.setColor(lineColor);			
		 
		//creates a copy of the Graphics instance
		Graphics2D g2d = (Graphics2D) g.create();
		
		//set the stroke of the copy, not the original 
		Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);
		g2d.setStroke(dashed);
		
		// draw vertical strokes
	    int tileCount = 0;
	    for (int i = 0; i <  tileSize *  mapSizeX; i +=  tileSize) {
	    	g2d.drawLine(i, 0, i,  tileSize * mapSizeY);
	    	tileCount++;
	    }
	    
	    // at this point, tileCount should be equal to mapSizeX
	    assert(tileCount == mapSizeX);
	    
	    tileCount = 0;
	    // draw horizontal strokes
		for (int i = 0; i < tileSize * mapSizeY; i += tileSize) {
			g2d.drawLine(0, i, tileSize * mapSizeX, i);
			tileCount++;
		}
		
		// at this point, tileCount should be equal to mapSizeY
		assert(tileCount == mapSizeY);
		
		//gets rid of the copy
		g2d.dispose();
    }

}