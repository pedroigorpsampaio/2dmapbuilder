package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.SwingUtilities;

import model.MapConfig;
import model.Tool;
import test.Config;

/**
 * Controls keyboard user interactions
 * 
 * @author Pedro Sampaio
 *
 */
public class KeyboardControl implements KeyListener {
	
	/**
	 * Keyboard controls
	 */
	private static boolean isCtrlPressed = false;		// bool that represents if ctrl key is pressed at the moment
	private static boolean isShiftPressed = false;		// bool that represents if shift key is pressed at the moment
	private static boolean isRightKeyPressed = false;	// bool that represents if right key is pressed at the moment
	private static boolean isLeftKeyPressed = false;	// bool that represents if left key is pressed at the moment
	private static boolean isUpKeyPressed = false;		// bool that represents if up key is pressed at the moment
	private static boolean isDownKeyPressed = false;	// bool that represents if down key is pressed at the moment
	
	/**
	 * Keyboard constants
	 */
	final static int keyCtrl = 17;			// Key code for the ctrl key
	final static int keyShift = 16;			// Key code for the shift key
	final static int keyRight = 39;			// Key code for right arrow key
	final static int keyLeft = 37;			// Key code for left arrow key
	final static int keyUp = 38;			// Key code for up arrow key
	final static int keyDown = 40;			// Key code for down arrow key
	final static int keyW = 87;				// Key code for W key
	final static int keyA = 65;				// Key code for A key
	final static int keyS = 83;				// Key code for S key
	final static int keyD = 68;				// Key code for D key
	final static int keyB = 66;				// Key code for B key (Brush tool shortcut)
	final static int keyE = 69;				// Key code for E key (Eraser tool shortcut)
	final static int keyR = 82;				// Key code for R key (RectSelect tool shortcut)

	/**
	 * 
	 */
	public KeyboardControl() {
		// creates keyboard control loop
		/**
		 * Keyboard loop control (in new thread)
		 */
		final int INPUT_DELAY = 1000/60;		

	    Thread t1 = new Thread(new Runnable() {
	    public void run()
	    {
	    	//loop
	    	while(true) {
		    	// control inputs
	    		 // Runs inside of the Swing UI thread
	            SwingUtilities.invokeLater(new Runnable() {
	              public void run() {
	            	  controlInputs();
	              }
	            });

		    	// wait to control inputs again
		    	try {
					Thread.sleep(INPUT_DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.err.println("KeyboardControl: Could not make thread sleep");
				}
	    	}
	    }});  
	    t1.start();
	}

	/**
	 * Keyboard callbacks
	 * For responsive interaction, callbacks 
	 * just toogle key flags on/off
	 * Any changes are made based on the flags on
	 * the keyboard control loop
	 * 
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		
		// if a map is not loaded, get out
		if(!MapConfig.getInstance().isMapLoaded())
			return;

		// updates key controls bools
		// if key is pressed, sets it to true
		switch(e.getKeyCode())
		{
			case keyCtrl:
				isCtrlPressed = true;
	    		break;
			case keyShift:
				isShiftPressed = true;
				break;
			case keyRight:
			case keyD:
				if(!isCtrlPressed)
					isRightKeyPressed = true;
				break;
			case keyLeft:
			case keyA:
				if(!isCtrlPressed)
					isLeftKeyPressed = true;
				break;
			case keyUp:
			case keyW:
				if(!isCtrlPressed)
					isUpKeyPressed = true;
				break;
			case keyDown:
			case keyS:
				if(!isCtrlPressed)
					isDownKeyPressed = true;
				break;
			case keyB:
				// updates selected tool for map interaction (setCurrentTool already notifies observers)
				Tool.getInstance().setCurrentTool(Tool.SelectTools.BRUSH);
				break;
			case keyE:
				// updates selected tool for map interaction (setCurrentTool already notifies observers)
				Tool.getInstance().setCurrentTool(Tool.SelectTools.ERASER);
				break;
			case keyR:
				// updates selected tool for map interaction (setCurrentTool already notifies observers)
				Tool.getInstance().setCurrentTool(Tool.SelectTools.SELECTION);
				break;
			default:
				if(Config.debug)
					System.out.println("KeyboardControl.keyPressed: unmapped keyboard button pressed. KeyCode: "+ e.getKeyCode());
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

		// if a map is not loaded, get out
		if(!MapConfig.getInstance().isMapLoaded())
			return;
		
		// updates key controls bools
		// if key is released, sets it to false
		switch(e.getKeyCode())
		{
			case keyCtrl:
				isCtrlPressed = false;
	    		break;
			case keyShift:
				isShiftPressed = false;
				break;
			case keyRight:
			case keyD:
				isRightKeyPressed = false;
				break;
			case keyLeft:
			case keyA:
				isLeftKeyPressed = false;
				break;
			case keyUp:
			case keyW:
				isUpKeyPressed = false;
				break;
			case keyDown:
			case keyS:
				isDownKeyPressed = false;
				break;
			default:
				if(Config.debug)
					System.out.println("KeyboardControl.keyReleased: unmapped keyboard button released. KeyCode: "+ e.getKeyCode());
				break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Getter and setters
	 */

	/**
	 * @return bool that represents if ctrl key is pressed at the moment
	 */
	public static boolean isCtrlPressed() {
		return isCtrlPressed;
	}

	/**
	 * @return bool that represents if shift key is pressed at the moment
	 */
	public static boolean isShiftPressed() {
		return isShiftPressed;
	}

	/**
	 * @return the isRightArrowPressed
	 */
	public static boolean isRightArrowPressed() {
		return isRightKeyPressed;
	}

	/**
	 * @param isRightArrowPressed the isRightArrowPressed to set
	 */
	public static void setRightArrowPressed(boolean isRightArrowPressed) {
		KeyboardControl.isRightKeyPressed = isRightArrowPressed;
	}
	
	/**
	 * Controls keyboard inputs and actions in map
	 * 
	 * @author	Pedro Sampaio
	 * @since	0.5b
	 */
	private void controlInputs() {

		// if a map is not loaded, get out
		if(!MapConfig.getInstance().isMapLoaded())
			return;
		
		MapConfig mConfig = MapConfig.getInstance();
		
		// mConfig movements on x axis (horizontal)
		if(isRightKeyPressed) { 	
			mConfig.setMoveX((int) (mConfig.getSpeed() * MapConfig.zoom));		// right movement, positive speed on x axis 
			mConfig.setMoveY(0);												// blocks Y movemen
		}
		else if(isLeftKeyPressed) {		
			mConfig.setMoveX((int) (-mConfig.getSpeed() * MapConfig.zoom));	// left movement, negative speed on x axis
			mConfig.setMoveY(0);											// blocks Y movemen
		}
		else
			mConfig.setMoveX(0);					// no moevement on x-axis
		// mConfig movements on y axis (vertical)
		if(isUpKeyPressed) {
			mConfig.setMoveY((int) (-mConfig.getSpeed() * MapConfig.zoom));	// up movement, negative speed on y axis
			mConfig.setMoveX(0);											// blocks X movemen

		}
		else if(isDownKeyPressed) {
			mConfig.setMoveY((int) (mConfig.getSpeed() * MapConfig.zoom));		// dpwn movement, positive speed on y axis
			mConfig.setMoveX(0);												// blocks X movement
			
		}
		else										// no movement on y-axis
			mConfig.setMoveY(0);
		
		// dispatch changes if there are movements
		if(mConfig.getMoveX() != 0 || mConfig.getMoveY() != 0)
			mConfig.dispatchChanges(false);
	}

}
