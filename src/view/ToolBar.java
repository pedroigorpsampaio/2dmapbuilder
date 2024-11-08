package view;

import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import controller.ToolBarControl;
import model.MapConfig;
import model.MapState;
import model.Tool;
import model.Tool.SelectTools;

/**
 * View class for the tool bar interface that
 * contains quick access tools for user interactions
 * 
 * @author Pedro Sampaio
 * @since	0.7
 *
 */
public class ToolBar extends JToolBar implements Observer {
	
	// generated serial
	private static final long serialVersionUID = 4268951943328073987L;
	private JToggleButton tbPaint;			// paint brush toggle button
	private JToggleButton tbEraser;			// eraser toogle button
	private JToggleButton tbSelect;			// rect selection toogle button
	private JToggleButton tbCollider;		// collider toogle button
	private JButton tbUndo;					// undo button
	private JButton tbRedo;					// redo button
	private JButton tbSave;
	private JCheckBox cbTrigger;			// trigger collider checkbox

	/**
	 * Constructor of the tool bar interface
	 * 
	 * @param mapStates		the states of the map in program for undoing and redoing operations
	 */
	public ToolBar(MapState mapStates) {
		
		// call super class
		super();
		
		// disable floatable funcitonality (removable from interface)
		this.setFloatable(false);
		this.setFocusable(false);
		
		// Class that implements actionlistener and will
		// control all interactions with tool bar buttons
		ToolBarControl tbControl = new ToolBarControl(mapStates);
		
		// new file quick access button
		JButton tbNew = new JButton("");
		tbNew.setToolTipText("New");
		tbNew.setIcon(new ImageIcon(MainWindow.class.getResource("/resources/tbNew.png")));
		tbNew.setFocusPainted(false);
		tbNew.setActionCommand(ToolBarControl.Action.NEW.toString());
		tbNew.addActionListener(tbControl);
		tbNew.setFocusable(false);
		this.add(tbNew);
		// open file quick access button
		JButton tbOpen = new JButton("");
		tbOpen.setToolTipText("Open");
		tbOpen.setFocusPainted(false);
		tbOpen.setIcon(new ImageIcon(MainWindow.class.getResource("/resources/tbOpen.png")));
		tbOpen.setActionCommand(ToolBarControl.Action.OPEN.toString());
		tbOpen.addActionListener(tbControl);
		tbOpen.setFocusable(false);
		this.add(tbOpen);
		// save quick access button
		tbSave = new JButton("");
		tbSave.setToolTipText("Save");
		tbSave.setFocusPainted(false);
		tbSave.setIcon(new ImageIcon(MainWindow.class.getResource("/resources/tbSave.png")));
		tbSave.setActionCommand(ToolBarControl.Action.SAVE.toString());
		tbSave.addActionListener(tbControl);
		tbSave.setFocusable(false);
		tbSave.setEnabled(false); // initially disabled - enabled when a map is loaded
		this.add(tbSave);
		// visual separator for the tools (unusable button)
		JButton tbSeparator1 = new JButton("  |  ");
		tbSeparator1.setContentAreaFilled(false);
		tbSeparator1.setBorderPainted(false);
		tbSeparator1.setBorder(null);
		tbSeparator1.setFocusable(false);
		this.add(tbSeparator1);
		// Undo step quick access button
		tbUndo = new JButton("");
		tbUndo.setToolTipText("Undo");
		tbUndo.setIcon(new ImageIcon(MainWindow.class.getResource("/resources/tbUndo.png")));
		tbUndo.setFocusPainted(false);
		tbUndo.setActionCommand(ToolBarControl.Action.UNDO.toString());
		tbUndo.addActionListener(tbControl);
		tbUndo.setFocusable(false);
		tbUndo.setEnabled(false); // initially disabled - enabled when an undo is possible
		this.add(tbUndo);
		
		// Redo step quick access button
		tbRedo = new JButton("");
		tbRedo.setToolTipText("Redo");
		// sets the action for this button
		tbRedo.setIcon(new ImageIcon(MainWindow.class.getResource("/resources/tbRedo.png")));
		tbRedo.setFocusPainted(false);
		tbRedo.setActionCommand(ToolBarControl.Action.REDO.toString());
		tbRedo.addActionListener(tbControl);
		tbRedo.setEnabled(false); // initially disabled - enabled when a redo is possible
		tbRedo.setFocusable(false);
		this.add(tbRedo);
		// visual separator for the tools (unusable button)
		JButton tbSeparator2 = new JButton("  |  ");
		tbSeparator2.setContentAreaFilled(false);
		tbSeparator2.setBorderPainted(false);
		tbSeparator2.setBorder(null);
		tbSeparator2.setFocusable(false);
		this.add(tbSeparator2);
		
		// brush/paint tool quick access button
		tbPaint = new JToggleButton ("");
		tbPaint.setToolTipText("Brush (B)");
		tbPaint.setIcon(new ImageIcon(MainWindow.class.getResource("/resources/tbPaint.png")));
		tbPaint.setFocusPainted(false);
		tbPaint.setActionCommand(ToolBarControl.Action.BRUSH.toString());
		tbPaint.addActionListener(tbControl);
		tbPaint.setFocusable(false);
		tbPaint.setEnabled(false); // initially disabled - enabled when a map is loaded
		this.add(tbPaint);
		// eraser tool quick access button
		tbEraser = new JToggleButton("");
		tbEraser.setToolTipText("Erase (E)");
		tbEraser.setIcon(new ImageIcon(MainWindow.class.getResource("/resources/tbEraser.png")));
		tbEraser.setFocusPainted(false);
		tbEraser.setActionCommand(ToolBarControl.Action.ERASE.toString());
		tbEraser.addActionListener(tbControl);
		tbEraser.setFocusable(false);
		tbEraser.setEnabled(false); // initially disabled - enabled when a map is loaded
		this.add(tbEraser);
		// rect selection tool quick access button
		tbSelect = new JToggleButton("");
		tbSelect.setIcon(new ImageIcon(MainWindow.class.getResource("/resources/tbSelect.png")));
		tbSelect.setToolTipText("Select (R)");
		tbSelect.setFocusPainted(false);
		tbSelect.setActionCommand(ToolBarControl.Action.RECTSELECT.toString());
		tbSelect.addActionListener(tbControl);
		tbSelect.setFocusable(false);
		tbSelect.setEnabled(false); // initially disabled - enabled when a map is loaded
		this.add(tbSelect);
		// tb collider button
		tbCollider = new JToggleButton("");
		tbCollider.setIcon(new ImageIcon(MainWindow.class.getResource("/resources/collider.png")));
		tbCollider.setToolTipText("Collider (C)");
		tbCollider.setFocusPainted(false);
		tbCollider.setActionCommand(ToolBarControl.Action.COLLIDER.toString());
		tbCollider.addActionListener(tbControl);
		tbCollider.setFocusable(false);
		tbCollider.setEnabled(false); // initially disabled - enabled when a map is loaded
		this.add(tbCollider);
		// cb trigger checkbox
		cbTrigger = new JCheckBox("Trigger");
		cbTrigger.setActionCommand(ToolBarControl.Action.TRIGGER.toString());
		cbTrigger.addActionListener(tbControl);
		cbTrigger.setToolTipText("Is it a trigger or a physical collider?");
		cbTrigger.setFocusable(false);
		cbTrigger.setVisible(false);
		cbTrigger.setEnabled(false); // initially disabled - enabled when a map is loaded
		this.add(cbTrigger);
		
		// observers selected tool to display selection in interface
		Tool.getInstance().addObserver(this);
		// observers map states to know if its possible to do undo's and redo's
		mapStates.addObserver(this);
		// observes map config to enable buttons when a map is loaded
		MapConfig.getInstance().addObserver(this);
	}
	
	/** 
	 * Receives updates from observed objects for proper display
	 */
	@Override
	public void update(Observable obs, Object arg) {
		// if map is not loaded, return
		if(!MapConfig.getInstance().isMapLoaded())
			return;
		
		if(obs instanceof Tool) {
			
			Tool tool = (Tool) obs;
			if(tool.getCurrentTool() == SelectTools.BRUSH) { // select brush and deselect others
				tbPaint.setSelected(true);
				tbEraser.setSelected(false);
				tbSelect.setSelected(false);
				tbCollider.setSelected(false);
				cbTrigger.setEnabled(false);
				cbTrigger.setVisible(false);
			}
			else if(tool.getCurrentTool() == SelectTools.ERASER) { // select eraser and deselect others
				tbPaint.setSelected(false);
				tbEraser.setSelected(true);
				tbSelect.setSelected(false);
				tbCollider.setSelected(false);
				cbTrigger.setEnabled(false);
				cbTrigger.setVisible(false);
			}
			else if(tool.getCurrentTool() == SelectTools.SELECTION) { // select rectselect and deselect others
				tbPaint.setSelected(false);
				tbEraser.setSelected(false);
				tbSelect.setSelected(true);
				tbCollider.setSelected(false);
				cbTrigger.setEnabled(false);
				cbTrigger.setVisible(false);
			}
			else if(tool.getCurrentTool() == SelectTools.COLLIDER) { // select collider enabling trigger cb and deselect others
				tbPaint.setSelected(false);
				tbEraser.setSelected(false);
				tbSelect.setSelected(false);
				tbCollider.setSelected(true);
				cbTrigger.setEnabled(true);
				cbTrigger.setVisible(true);
			}
			else {													// deselect all
				tbPaint.setSelected(false);
				tbEraser.setSelected(false);
				tbSelect.setSelected(false);
				tbCollider.setSelected(false);
				cbTrigger.setEnabled(false);
				cbTrigger.setVisible(false);
			}
		}
		else if(obs instanceof MapState) {
			MapState mState = (MapState) obs;
			// checks if its possible to do undo and adjust visualization
			if(mState.isUndoPossible()) 
				tbUndo.setEnabled(true);
			else
				tbUndo.setEnabled(false);
			// checks if its possible to do redo and adjust visualization
			if(mState.isRedoPossible()) 
				tbRedo.setEnabled(true);
			else
				tbRedo.setEnabled(false);
		}
		else if(obs instanceof MapConfig) {
			// enables disabled buttons
			if(MapConfig.getInstance().isMapLoaded()) {
				tbSave.setEnabled(true);
				tbPaint.setEnabled(true);
				tbEraser.setEnabled(true);
				tbSelect.setEnabled(true);
				tbCollider.setEnabled(true);
			}
		}
	}

}
