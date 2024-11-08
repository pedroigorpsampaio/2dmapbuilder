package controller;

import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.Map;
import model.MapConfig;
import model.MapState;
import test.Config;

/**
 * Controls layer view interactions
 * 
 * @author	Pedro Sampaio
 * @since	0.8
 *
 */
public class LayerViewControl implements ListSelectionListener, ChangeListener  {

	private MapState mapStates; 	// The states of the map in program for undoing and redoing operations
	
	/**
	 * Constructor
	 * Sets MapState reference
	 * @param mapStates	The states of the map in program for undoing and redoing operations
	 */
	public LayerViewControl(MapState mapStates) {
		this.mapStates = mapStates;
	}

	/**
	 * Callback for selected layer list changes
	 */
	@Override
	public void valueChanged(ListSelectionEvent evt) {
		// model for the list
		ListSelectionModel lsm = (ListSelectionModel)evt.getSource();
		// current selected index
		int selectedIdx = -1;
		
		 // Find out which index is selected.
        int minIndex = lsm.getMinSelectionIndex();
        int maxIndex = lsm.getMaxSelectionIndex();
        for (int i = minIndex; i <= maxIndex; i++) {
            if (lsm.isSelectedIndex(i)) {
                selectedIdx = i;
                break;
            }
        }
		
        // logs selected layer if debug is enabled
		if(Config.debug)
			System.out.println("LayerView: Layer selected: Layer" + (selectedIdx+1));
		
		// updates selected layer
		mapStates.getCurrentMap().setSelectedLayer(selectedIdx);
		// notify observers of change
		mapStates.dispatchChanges();
		MapConfig.getInstance().dispatchChanges(false);
	}

	/**
	 * Callback for opacity slider changes
	 */
	@Override
	public void stateChanged(ChangeEvent evt) {
		// jslider that triggered the event
		JSlider source = (JSlider)evt.getSource();
	
		/**
		 * adjust opacity of selected layer
		 */
		// current opacity value from slider
        int opacity = (int)source.getValue();
        
        // gets current map
		Map currentMap = mapStates.getCurrentMap();

		// gets selected layer
		int selectedLayer = mapStates.getCurrentMap().getSelectedLayer();
		
		// returns if no layer is selected
		if(selectedLayer < 0)
			return;
		
		// adjusts current layer opacity (converts from (0,100) to (0,1))
		currentMap.getLayers().get(selectedLayer).setOpacity(opacity / 100f);
		
		// notify observers of the change
		mapStates.dispatchChanges();
        
		if(Config.debug)
			System.out.println("LayerView: Opacity changed: " + opacity);  

	}

}
