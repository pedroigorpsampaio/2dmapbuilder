package view;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;

import controller.LayerViewControl;
import model.Layer;
import model.Map;
import model.MapConfig;
import model.MapState;

/**
 * View class for layers panel that contains
 * the existing currently existing layers of the map
 * and lets user adjust opacity for each of them
 * 
 * @author Pedro Sampaio
 * @since	0.8
 *
 */
public class LayerView extends Component implements Observer {

	// generated serial
	private static final long serialVersionUID = 5677317472437248636L;
	
	private JSlider sliderOpacity;		// opacity slider

	private JList<Object> listLayers;	// list existing layers in map for visualization
	
	private MapState mapStates;			// the states of the map in program for layers observations 

	private int nLayers;				// number of existent layers

	/**
	 * Creates and adds the layers view interface
	 * to the content pane of the program
	 * 
	 * @param contentPane	the content pane of the program
	 * @param mapStates 	the states of the map in program for layers observation
	 */
	public LayerView (JPanel contentPane, MapState mapStates) {
		
		// sets map state reference
		this.mapStates = mapStates;
		
		// Class that implements listeners for slider and list of layers
		// and will control all interactions with layer view
		LayerViewControl lViewControl = new LayerViewControl(mapStates);
		
		// Label for the view of layers
		JLabel lblLayers = new JLabel("Layers");
		lblLayers.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblLayers = new GridBagConstraints();
		gbc_lblLayers.gridwidth = 2;
		gbc_lblLayers.anchor = GridBagConstraints.WEST;
		gbc_lblLayers.insets = new Insets(0, 0, 5, 5);
		gbc_lblLayers.gridx = 0;
		gbc_lblLayers.gridy = 2;
		contentPane.add(lblLayers, gbc_lblLayers);
		
		// opacity label for the opacity slider
		JLabel lblOpacity = new JLabel("Opacity");
		lblOpacity.setFont(new Font("Tahoma", Font.PLAIN, 10));
		GridBagConstraints gbc_lblOpacity = new GridBagConstraints();
		gbc_lblOpacity.anchor = GridBagConstraints.EAST;
		gbc_lblOpacity.insets = new Insets(0, 0, 5, 5);
		gbc_lblOpacity.gridx = 0;
		gbc_lblOpacity.gridy = 3;
		contentPane.add(lblOpacity, gbc_lblOpacity);
		
		// opacity slider
		sliderOpacity = new JSlider();
		sliderOpacity.setValue(100);
		GridBagConstraints gbc_sliderOpacity = new GridBagConstraints();
		gbc_sliderOpacity.fill = GridBagConstraints.BOTH;
		gbc_sliderOpacity.insets = new Insets(0, 0, 5, 5);
		gbc_sliderOpacity.gridx = 1;
		gbc_sliderOpacity.gridy = 3;
		contentPane.add(sliderOpacity, gbc_sliderOpacity);
		sliderOpacity.setFocusable(false);

		// scrollable panel for cases when layers exceed view limits
		JScrollPane scrollPaneLayers = new JScrollPane();
		GridBagConstraints gbc_scrollPaneLayers = new GridBagConstraints();
		gbc_scrollPaneLayers.gridwidth = 2;
		gbc_scrollPaneLayers.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneLayers.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneLayers.gridx = 0;
		gbc_scrollPaneLayers.gridy = 4;
		contentPane.add(scrollPaneLayers, gbc_scrollPaneLayers);
		
		// the view for the list of layers that composes the map
		listLayers = new JList<Object>();
		listLayers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listLayers.setModel(new AbstractListModel<Object>() {
			/**
			 *  Begins list with initial existent layer
			 */
			private static final long serialVersionUID = -565323083828193992L;
			String[] values = new String[] {"Layer1"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		
		listLayers.setFocusable(false);
		listLayers.setSelectedIndex(0); // initial layer is set as selected
		//mapStates.getCurrentMap().setSelectedLayer(0);
		nLayers = 1; //initially one layer exists
		
		// add controller to control interactions with slider
		sliderOpacity.addChangeListener(lViewControl);
		// add controller to control interactions with list 
		ListSelectionModel listSelectionModel = listLayers.getSelectionModel();
		listSelectionModel.addListSelectionListener(lViewControl);
		
		// sets the view for the layers as viewport for the scrollabel panel
		scrollPaneLayers.setViewportView(listLayers);
		
		// no layers selected at the beginning, disable slider
		sliderOpacity.setEnabled(false);
		
		// observes map states to obtain the current list of layers
		mapStates.addObserver(this);
		// observes map config to obtain current selected layer
		MapConfig.getInstance().addObserver(this);
	}

	/**
	 * Observer methods
	 * update when changes occur in observable objects
	 * that are being observer by this object
	 * 
	 * @author	Pedro Sampaio
	 * @since	0.8
	 */
	@Override
	public void update(Observable obs, Object arg) {
		
		// mapstate has changed, adjust visualization
		if(obs instanceof MapState) {
			// get current map
			Map currentMap = ((MapState) obs).getCurrentMap();

			// get list of existent layers in current map
			ArrayList<Layer> layers = currentMap.getLayers();
			
			// returns if there is no layers in current map
			// or there are no new layers 
			if(layers.size() == 0 || layers.size() == nLayers)
				return;
			
			// updates number of layers
			nLayers = layers.size();
			
			// updates view list of layers if necessary
			if(listLayers.getModel().getSize() != layers.size()) {
				// new values to update
				String[] newValues = new String[layers.size()];

				// fills new values
				for(int i = 0 ; i < layers.size() ; i++)
					newValues[i] = "Layer"+ (i+1);
				
				// sets the new model with new values
				listLayers.setModel(new AbstractListModel<Object>() {

					private static final long serialVersionUID = 8756460716074871268L;
					String[] values = newValues;
					public int getSize() {
						return values.length;
					}
					public Object getElementAt(int index) {
						return values[index];
					}
				});
			}
			
			// sets the topmost layer as the selected one
			listLayers.setSelectedIndex(layers.size()-1);
			mapStates.getCurrentMap().setSelectedLayer(layers.size()-1);
		}
		if(obs instanceof MapConfig) {
			
			// gets current map
			Map currentMap = mapStates.getCurrentMap();
			
			// gets current selected layer
			int selectedLayer = mapStates.getCurrentMap().getSelectedLayer();
			
			// return if no layer is selected
			if(selectedLayer < 0)
				return;
			
			// activates slider if its disabled
			if(!sliderOpacity.isEnabled())
				sliderOpacity.setEnabled(true);
			
			// gets current layer opacity to adjust opacity slider
			float currentOpacity = currentMap.getLayers().get(selectedLayer).getOpacity();

			// updates opacity slider (converting from (0,1) to (0,100))
			sliderOpacity.setValue((int) (currentOpacity * 100));
		
		}
	}
}
