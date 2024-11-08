package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.Map;
import model.MapConfig;
import model.MapState;
import model.Tool;
import test.Config;
import view.AboutWindow;
import view.NewTilesetDialog;
import view.PreferencesDialog;
import view.ResizeDialog;

/**
 * Controls menu bar interactions
 * 
 * @author	Pedro Sampaio
 * @since	0.9
 *
 */
public class MenuBarControl implements ActionListener{
	
	// possible actions to perform based on menu bar buttons
	public enum Action {NEW, OPEN, SAVE, EXIT, UNDO, REDO, CUT, COPY, PASTE, DELETE,
						PREFERENCES, NEWTILESET, RESIZEMAP, DOCUMENTATION, ABOUT, SAVEAS}

	private MapState mapStates; 	// The states of the map in program for undoing and redoing operations

	/**
	 * Constructor that stores mapStates for redoing and undoing operations
	 * @param mapStates	The states of the map in program for undoing and redoing operations
	 */
	public MenuBarControl(MapState mapStates) {
		this.mapStates = mapStates;
	}

	/**
	 * Callback that controls the actions to perform
	 * after a menu item was pressed
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(Config.debug) {
			System.out.println("MenuBar: A menu item was clicked: " + e.getActionCommand());
		}
		
		switch(e.getActionCommand()) {
			case "NEW":
				FileManager.newProject(mapStates);
				break;
			case "OPEN":
				FileManager.open(mapStates); // calls method that opens and loads an existing project into program
				break;
			case "SAVE":
				// act as save as in case there is no save for this project yet
				if(MapConfig.getInstance().getProject().getSaveInfo() == null)
					FileManager.save(mapStates.getCurrentMap(), true);
				else // save over last save (quick save)
					FileManager.save(mapStates.getCurrentMap(), false);
				break;
			case "SAVEAS":
				// act as save as always
				FileManager.save(mapStates.getCurrentMap(), true);
				break;
			case "EXIT":
				// checks if user wants to save unsaved modifications if there are any
				boolean hasChosen = FileManager.getInstance().checkSaveUnsaved(mapStates);
				// Exit program if user saved or discarded information, if closed dialog do not close program
				if(hasChosen)
					System.exit(0);
				break;
			case "COPY":
				Tool.copyToClipboard(mapStates); // use method that copies current selected tiles to clipboard
				break;
			case "PASTE":
				Tool.pasteFromClipboard(mapStates); // use method that pastes current selected tiles from clipboard
				break;
			case "CUT":
				Tool.copyToClipboard(mapStates); // use method that pastes current selected tiles from clipboard
				eraseTiles();				
				break;
			case "UNDO":
				// undo one state
				mapStates.UndoState();
				break;
			case "REDO":
				// redo one state
				mapStates.RedoState();
				break;
			case "DELETE":
				eraseTiles();	// calls method that erases selected tiles
				break;
			case "PREFERENCES":
				new PreferencesDialog(); // opens dialog that collects user preferences
				break;
			case "RESIZEMAP":
				new ResizeDialog(mapStates); // opens dialog that collects resize informations and resizes the map
				break;
			case "NEWTILESET":
				new NewTilesetDialog(); // opens dialog that collects new tileset informations and creates it
				break;
			case "DOCUMENTATION":
				FileManager.openWebpage("https://bitbucket.org/KikoSampaio/2dmapbuilder/wiki/Home"); // opens online documentation of the program
				break;
			case "ABOUT":
				new AboutWindow(); // opens about window
				break;
			default:
				System.err.println("ToolBarControl: Unmapped menu clicked: "+ e.getActionCommand());
				break;
		}
		
	}

	/**
	 * Calls tiles eraser method that deletes
	 * a selection of tiles in the map 
	 */
	private void eraseTiles() {
		Map map = mapStates.getCurrentMap(); // gets current map
		int selectedLayer = map.getSelectedLayer(); // gets current layer selected

		// only erase tiles of selection if a layer is currently selected
		if(selectedLayer >= 0) {
			Tool.eraseSelection(map.getLayers().get(selectedLayer).getSelectedTiles(), mapStates, selectedLayer);
		} else {
			if(Config.debug)
				System.err.println("MenuBar: Cannot delete selected tiles: No layer selected");
		}
	}

}
