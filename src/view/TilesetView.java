package view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

import controller.TilesetComboBoxControl;
import model.Preferences;
import model.Tileset;
import model.TilesetConfig;

/**
 * Class that represents the view of
 * the tileset panel in the main window
 * 
 * @author Pedro Sampaio
 * @since 0.2
 */
public class TilesetView implements Observer {
	
	private Tileset tileset;	// the current tileset in view
	private JScrollPane scrollPaneTileset;	// the scroll panel that contains the tileset view
	private ViewportTileset vpTileset; // the tileset viewport
	private JComboBox<String> comboBoxTileset; // the comboBox of tilesets loaded in program
	
	/**
	 * Constructor for the class that represents the view of
	 * the tileset panel in the main window
	 * 
	 * @param contentPane	the content pane of the program
	 */
	public TilesetView (Container contentPane) {

		// build label tilesets
		JLabel lblTilesets = new JLabel("Tilesets");
		lblTilesets.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblTilesets = new GridBagConstraints();
		gbc_lblTilesets.gridwidth = 2;
		gbc_lblTilesets.anchor = GridBagConstraints.WEST;
		gbc_lblTilesets.insets = new Insets(0, 0, 5, 5);
		gbc_lblTilesets.gridx = 0;
		gbc_lblTilesets.gridy = 6;
		contentPane.add(lblTilesets, gbc_lblTilesets);
		
		// build combobox of tilesets
		comboBoxTileset = new JComboBox<String>();
		comboBoxTileset.setFocusable(false);
		// add a listener for the combobox to adjust currently selected tileset
		comboBoxTileset.addActionListener(new TilesetComboBoxControl(comboBoxTileset));
		GridBagConstraints gbc_comboBoxTileset = new GridBagConstraints();
		gbc_comboBoxTileset.gridwidth = 2;
		gbc_comboBoxTileset.insets = new Insets(0, 0, 5, 5);
		gbc_comboBoxTileset.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxTileset.gridx = 0;
		gbc_comboBoxTileset.gridy = 7;
		// update combobox with current existent tilesets
		updateTilesetList();
		contentPane.add(comboBoxTileset, gbc_comboBoxTileset);
		
		// builds scrollpanel of the tileset view
		scrollPaneTileset = new JScrollPane();
		scrollPaneTileset.setBorder(new MatteBorder(1, 1, 1, 1, (Color) Color.LIGHT_GRAY));
		GridBagConstraints gbc_scrollPaneTileset = new GridBagConstraints();
		gbc_scrollPaneTileset.gridwidth = 2;
		gbc_scrollPaneTileset.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneTileset.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneTileset.gridx = 0;
		gbc_scrollPaneTileset.gridy = 8;
		contentPane.add(scrollPaneTileset, gbc_scrollPaneTileset);
		
		// observes tilesetConfig for changes in selected tileset
		TilesetConfig.getInstance().addObserver(this);
	}

	/**
	 * @return the tileset
	 */
	public Tileset getTileset() {
		return tileset;
	}

	/**
	 * Sets current tile set reference
	 * and draws the tileset on panel
	 * do nothing if tileset received is null
	 * 
	 * @param tileset the tileset to set
	 */
	public void setTileset(Tileset tileset) {
		if(tileset == null)
			return;
		
		// if viewport tileset exists already
		// we must remove it from observables list
		if(vpTileset != null) {
			Preferences.getInstance().deleteObserver(vpTileset);
			this.tileset.deleteObserver(vpTileset);			// removes from old tileset
		}	
		
		this.tileset = tileset; 
		// draws the viewport that contains the representation of the current tileset
		drawViewportTileset(scrollPaneTileset, tileset);
	}
	
	/**
	 * Instantiate the viewport that will be used in the tileset JScrollPane
	 * Represents the 2D visualization of the tilesets that will interact
	 * with the user to aid the 2D map creation
	 * If viewport exists, changes selected tile of viewport
	 * 
	 * @param 	scrollPaneTileset	the JScrollPane that will contain the viewport 
	 * @param	tileset				the current tileset loaded
	 * @since	0.2
	 */
	private void drawViewportTileset(JScrollPane scrollPaneTileset, Tileset tileset) {
		
		// if viewport tileset exists already
		// we must remove old viewport tileset mouse listeners
		if(vpTileset != null) {
			
			for(int i = 0; i < scrollPaneTileset.getViewport().getMouseListeners().length; i ++)		
				scrollPaneTileset.getViewport().removeMouseListener(scrollPaneTileset.getViewport().getMouseListeners()[i]);
			
			for(int i = 0; i < scrollPaneTileset.getViewport().getMouseMotionListeners().length; i ++)		
				scrollPaneTileset.getViewport().removeMouseMotionListener(scrollPaneTileset.getViewport().getMouseMotionListeners()[i]);
		}
			
		// creates new viewport with new tileset
		vpTileset = new ViewportTileset(scrollPaneTileset, tileset);
	
		// updates viewport view with new tileset
		scrollPaneTileset.setViewportView(vpTileset);
	}
	
	/**
	 * Updates list of tilesets existents
	 * for combobox interactions
	 * 
	 * @author Pedro Sampaip
	 * @since  1.1
	 */
	private void updateTilesetList() {
		// there are no tilesets loaded in project
		if(TilesetConfig.getInstance().getTilesets().size() <= 0)
			return;
		
		// gets current list of tilesets
		ArrayList<Tileset> tilesets = TilesetConfig.getInstance().getTilesets();
		// gets selected tileset idx
		int tsIdx = TilesetConfig.getInstance().getCurrentTilesetIdx();
		
		// removes all existing itens im combobox 
		comboBoxTileset.removeAllItems();
		// for each tileset in list of tilesets, adds tileset name to the combobox list
		for(int i = 0; i < tilesets.size(); i++) {
			comboBoxTileset.addItem(tilesets.get(i).getName());
		} // lose the correct selected tileset idx
		
		// select in combobox the currently selected tileset
		// triggering the action that resets the correct selected tileset
		comboBoxTileset.setSelectedIndex(tsIdx);
	}

	/**
	 * Observer methods
	 * update when changes occur in observable object 
	 * 
	 * @author	Pedro Sampaio
	 * @since	1.1
	 */
	@Override
	public void update(Observable obs, Object arg) {
		
		// if TilesetConfig object has updates, adjust visualization
		// on account of tileset selected changes
		if (obs instanceof TilesetConfig) {	
			// set new tileset and draw new view
			setTileset(TilesetConfig.getInstance().getCurrentTileset());
			// update combobox list
			updateTilesetList();
		}
	}

}
