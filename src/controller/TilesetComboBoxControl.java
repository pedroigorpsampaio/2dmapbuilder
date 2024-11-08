package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import model.TilesetConfig;

/**
 * Controller for the tileset combobox
 * to adjust current tileset based on combobox selection
 * 
 * @author Pedro Sampaio
 * @since  1.1
 *
 */
public class TilesetComboBoxControl implements ActionListener{
	
	JComboBox<String> cbTileset; // reference to the combo box of tileset view
	
	/**
	 * Constructor
	 * Sets the reference for the combo box of tileset view
	 * @param cbTileset	the reference to the combo box of tileset view
	 */
	public TilesetComboBoxControl (JComboBox<String> cbTileset) {
		this.cbTileset = cbTileset;
	}

	@Override
	 public void actionPerformed(ActionEvent actionEvent) {
		// sets the currently selected tileset based on combobox selected index if a tileset is selected
		if(cbTileset.getSelectedIndex() >= 0) {
		    TilesetConfig.getInstance().setCurrentTilesetIdx(cbTileset.getSelectedIndex());
		    // notify observers of tilesetconfig for changes in currently selected tileset
		    TilesetConfig.getInstance().dispatchChanges();
		}
	 }
	

}
