package model;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Class that contains a limited number of map states
 * to be able to navigate between then with undo and redo
 * operations
 * 
 * @author	Pedro Sampaio
 * @since	0.5
 *
 */
public class MapState extends Observable {
	
	private int maxStates;				// maximum number of states to save in total
	private ArrayList<Map> mapStates;	// the saved list of map states
	private int	seek;					// the current seek position (current state is affected by the seek)
	private boolean isSeekShifted;		// bool that represents if seek is shifted (not in the last pos of list)
	
	/**
	 * Constructor initializes the list of states
	 * and stores the limit and initialize
	 * remaining properties.
	 * 
	 * @param	maxStates	maximum number of states to save in total
	 */
	public MapState(int maxStates) {
		super();
		this.maxStates = maxStates;
		
		// initializes list of map states
		mapStates = new ArrayList<Map>();

		// initially seek is in the last pos of array
		// shift occurs when undo/redo is used
		seek = mapStates.size() - 1;
		isSeekShifted = false;
	}
	
	/**
	 * Updates current map state
	 * @author Pedro Sampaio
	 * @since	0.7b
	 * @param map	the new data for the current map state
	 */
	public void UpdateState(Map map) {

		// creates map states if there are 
		// none to go back to
		if(mapStates.size() == 1) {	
			AddState(map);
			return;
		}
			
		// updates current mapstate
		mapStates.set(seek, map);
		
		// checks if state is equal of saved one
		if(getCurrentMap().equals(MapConfig.getInstance().getProject().getMap()))
			MapConfig.getInstance().getProject().setSaved(true);		// updates that it is equal as current saved
		else
			MapConfig.getInstance().getProject().setSaved(false);		// updates that it is different as current saved (unsaved)
		
		// notify map config observers for saved changes
		MapConfig.getInstance().dispatchChanges(false);
		
		// notify observers
		dispatchChanges();
	}
	
	/**
	 * Adds a new state to the list of states
	 * controlling the size of the list
	 * 
	 * @author	Pedro Sampaio
	 * @since	0.5
	 * @param	map		New state to be added
	 */
	public void AddState(Map map) {
		// if limit is reached, removes oldest state from list
		if(mapStates.size() >= maxStates) {
			mapStates.remove(0);	// oldest state is removed
		}
		
		// if seek is shifted, removes all states newer 
		// than shift position losing redo option
		if(isSeekShifted) {
			
			// removes newer states than seek pos
			for(int i = mapStates.size() - 1; i > seek; i--)
				mapStates.remove(i);
			
			// after removal, seek is in last pos of list and no longer shifted
			isSeekShifted = false;	
		}
		
		// now we are ready to add the new state
		mapStates.add(map);
		
		// updates seek position
		seek = mapStates.size() - 1;
		
		// checks if state is equal of saved one
		if(getCurrentMap().equals(MapConfig.getInstance().getProject().getMap()))
			MapConfig.getInstance().getProject().setSaved(true);		// updates that it is equal as current saved
		else
			MapConfig.getInstance().getProject().setSaved(false);		// updates that it is different as current saved (unsaved)
		
		// notify map config observers for saved changes
		MapConfig.getInstance().dispatchChanges(false);
		
		// notify observers
		dispatchChanges();
	}
	
	/**
	 * Removes all states from states list
	 * except the current one
	 * @author Pedro Sampaio
	 * @since	1.2b
	 */
	public void RemoveOldStates() {

		// saves current map
		Map currentMap = getCurrentMap();
		
		// clear map states list
		mapStates.clear();
		
		// updates seek
		seek = 0;
		isSeekShifted = false;
			
		// add current mapstate
		mapStates.add(currentMap);
		// notify observers
		dispatchChanges();
	}
	
	/**
	 * Undo the current state.
	 * Each time this method is called, seek
	 * moves one state in the past until the oldest 
	 * state is reached.
	 * 
	 * @author	Pedro Sampaio
	 * @since	0.5
	 * 
	 */
	public void UndoState() {
		
		// moves seek one state in the past
		// if it isnt on oldest state yet
		if(seek > 0) {
			seek--;
			
			// since seek has been moved, we need to update isSeekShifted to true
			if(!isSeekShifted)
				isSeekShifted = true;
			
			// notify observers
			dispatchChanges();
		}
	}
	
	/**
	 * Redo to a future state.
	 * Each time this method is called, seek
	 * moves one state in the future until the newest 
	 * state is reached.
	 * 
	 * @author	Pedro Sampaio
	 * @since	0.5
	 * 
	 */
	public void RedoState() {
		
		// moves seek one state in the future
		// if it isnt on newest state yet
		if(seek < (mapStates.size() - 1)) {
			seek++;

			// since seek has been moved, we need to update isSeekShifted to true
			if(!isSeekShifted)
				isSeekShifted = true;
			
			
			// notify observers
			dispatchChanges();
		}
		
	}
	
	/**
	 * @author Pedro Sampaio
	 * @since  0.5
	 * @return if is possible undoing a state (there are older states stored)
	 */
	public boolean isUndoPossible() {
		return (seek > 0);
	}
	
	/**
	 * @author Pedro Sampaio
	 * @since  0.5
	 * @return if is possible redoing a state (there are newer states stored)
	 */
	public boolean isRedoPossible() {
		return (seek < (mapStates.size() - 1));
	}

	/**
	 * @author Pedro Sampaio
	 * @since  0.5
	 * @return the map in the current state that is the actual map to work with
	 */
	public Map getCurrentMap() {
		// makes sure that bounds are respected
		if(seek >= mapStates.size())
			seek = mapStates.size()-1;
		if(seek < 0)
			seek = 0;
		
		return mapStates.get(seek);
	}
	

	/**
	 * dispatch changes in the map state for observers
	 * 
	 * @author	Pedro Sampaio
	 * @since	0.5
	 */
	public void dispatchChanges() {
		setChanged();
		notifyObservers();
	}
}
