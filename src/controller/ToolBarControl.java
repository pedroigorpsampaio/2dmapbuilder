package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.MapConfig;
import model.MapState;
import model.Tool;
import test.Config;

/**
 * Controls quick access tool bar user interactions
 * 
 * @author	Pedro Sampaio
 * @since	0.7
 *
 */
public class ToolBarControl implements ActionListener{
	
	// possible actions to perform based on tool bar buttons
	public enum Action {NEW, OPEN, SAVE, UNDO, REDO, BRUSH, ERASE, RECTSELECT, COLLIDER, TRIGGER}

	private MapState mapStates; 	// The states of the map in program for undoing and redoing operations

	/**
	 * Constructor that stores mapStates for redoing and undoing operations
	 * @param mapStates	The states of the map in program for undoing and redoing operations
	 */
	public ToolBarControl(MapState mapStates) {
		this.mapStates = mapStates;
	}

	/**
	 * Callback that controls the actions to perform
	 * after a tool on tool bar has been clicked
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(Config.debug) {
			System.out.println("ToolBar: A toolbar tool was clicked: " + e.getActionCommand());
		}

		// gets current selected tool
		Tool.SelectTools selectedTool = Tool.getInstance().getCurrentTool();
		
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
			case "UNDO":
				// undo one state
				mapStates.UndoState();
				break;
			case "REDO":
				// redo one state
				mapStates.RedoState();
				break;
			case "BRUSH":
				// updates selected tool
				selectedTool = Tool.SelectTools.BRUSH;
				break;
			case "ERASE":
				// updates selected tool
				selectedTool = Tool.SelectTools.ERASER;
				break;
			case "RECTSELECT":
				// updates selected tool
				selectedTool = Tool.SelectTools.SELECTION;
				break;
			case "COLLIDER":
				// updates selected tool
				selectedTool = Tool.SelectTools.COLLIDER;
				break;
			case "TRIGGER":
				// updates trigger for collider tool
				Tool.getInstance().updateTrigger();
				break;
			default:
				System.err.println("ToolBarControl: Unmapped tool clicked: "+ e.getActionCommand());
				break;
		}
		
		// updates selected tool for map interaction
		Tool.getInstance().setCurrentTool(selectedTool, true);
		// notify observers of map interaction tools
		Tool.getInstance().dispatchChanges();
	}

}
