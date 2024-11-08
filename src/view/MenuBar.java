package view;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import controller.MenuBarControl;
import model.MapConfig;
import model.MapState;

/**
 * The interface for the menubar and its components
 * 
 * @author	Pedro Sampaio
 * @since	0.9
 *
 */
public class MenuBar extends JMenuBar implements Observer {

	// default serial id
	private static final long serialVersionUID = 1L;
	private JMenu mnMap;	// menu group for map buttons - activated only when a map project is loaded
	private JMenu mnEdit;   // edit menu group -- activated only when there is a map loaded
	private JMenuItem mntmSave;
	private JMenuItem mntmSaveAs;

	/**
	 * Constructor that receives necessary parameterss
	 * @param mapStates	the states of the map
	 */
	public MenuBar (MapState mapStates) {
		super();

		// observes map config for updates of maps loaded
		MapConfig.getInstance().addObserver(this);

		// Class that implements actionlistener and will
		// control all interactions with menu bar buttons
		MenuBarControl mbControl = new MenuBarControl(mapStates);

		// file menu group
		JMenu mnFile = new JMenu("File  ");
		mnFile.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(mnFile);

		// new file button
		JMenuItem mntmNew = new JMenuItem("New...          ");
		mntmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		mntmNew.setIcon(new ImageIcon(MainWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/File.gif")));
		// sets action command for this button
		mntmNew.setActionCommand(MenuBarControl.Action.NEW.toString());
		// sets action listener
		mntmNew.addActionListener(mbControl);
		mnFile.add(mntmNew);

		// open file button
		JMenuItem mntmOpen = new JMenuItem("Open...");
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mntmOpen.setIcon(new ImageIcon(MainWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Directory.gif")));
		// sets action command for this button
		mntmOpen.setActionCommand(MenuBarControl.Action.OPEN.toString());
		// sets action listener
		mntmOpen.addActionListener(mbControl);
		mnFile.add(mntmOpen);

		// save file button
		mntmSave = new JMenuItem("Save");
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mntmSave.setIcon(new ImageIcon(MainWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/FloppyDrive.gif")));
		// sets action command for this button
		mntmSave.setActionCommand(MenuBarControl.Action.SAVE.toString());
		// sets action listener
		mntmSave.addActionListener(mbControl);
		mntmSave.setEnabled(false); // when no project is loaded, is disabled;
		mnFile.add(mntmSave);

		// save as file button
		mntmSaveAs = new JMenuItem("Save As...");
		mntmSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK+InputEvent.SHIFT_MASK));
		mntmSaveAs.setIcon(new ImageIcon(MainWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/FloppyDrive.gif")));
		// sets action command for this button
		mntmSaveAs.setActionCommand(MenuBarControl.Action.SAVEAS.toString());
		// sets action listener
		mntmSaveAs.addActionListener(mbControl);
		mntmSaveAs.setEnabled(false); // when no project is loaded, is disabled;
		mnFile.add(mntmSaveAs);

		// separator for organizing 
		JSeparator separatorFileExit = new JSeparator();
		mnFile.add(separatorFileExit);

		// exit button
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setIcon(new ImageIcon(MainWindow.class.getResource("/resources/exitIcon.png")));
		// sets accelerater as alt+F4 to override closing without checking if user wants to save unsaved modifications
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		// sets action command for this button
		mntmExit.setActionCommand(MenuBarControl.Action.EXIT.toString());
		// sets action listener
		mntmExit.addActionListener(mbControl);
		mnFile.add(mntmExit);

		// edit menu group -- activated only when there is a map loaded
		mnEdit = new JMenu("Edit   ");
		mnEdit.setHorizontalAlignment(SwingConstants.CENTER);
		mnEdit.setEnabled(false);
		this.add(mnEdit);

		// undo button
		JMenuItem mntmUndo = new JMenuItem("Undo                     ");
		mntmUndo.setIcon(new ImageIcon(MainWindow.class.getResource("/com/sun/javafx/scene/web/skin/Undo_16x16_JFX.png")));
		mntmUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		// sets action command for this button
		mntmUndo.setActionCommand(MenuBarControl.Action.UNDO.toString());
		// sets action listener
		mntmUndo.addActionListener(mbControl);
		mnEdit.add(mntmUndo);

		// redo button
		JMenuItem mntmRedo = new JMenuItem("Redo");
		mntmRedo.setIcon(new ImageIcon(MainWindow.class.getResource("/com/sun/javafx/scene/web/skin/Redo_16x16_JFX.png")));
		mntmRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
		// sets action command for this button
		mntmRedo.setActionCommand(MenuBarControl.Action.REDO.toString());
		// sets action listener
		mntmRedo.addActionListener(mbControl);
		mnEdit.add(mntmRedo);

		// cut button
		JMenuItem mntmCut = new JMenuItem("Cut");
		mntmCut.setIcon(new ImageIcon(MainWindow.class.getResource("/com/sun/javafx/scene/web/skin/Cut_16x16_JFX.png")));
		mntmCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		// sets action command for this button
		mntmCut.setActionCommand(MenuBarControl.Action.CUT.toString());
		// sets action listener
		mntmCut.addActionListener(mbControl);
		mnEdit.add(mntmCut);

		// copy button
		JMenuItem mntmCopy = new JMenuItem("Copy");
		mntmCopy.setIcon(new ImageIcon(MainWindow.class.getResource("/com/sun/javafx/scene/web/skin/Copy_16x16_JFX.png")));
		mntmCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		// sets action command for this button
		mntmCopy.setActionCommand(MenuBarControl.Action.COPY.toString());
		// sets action listener
		mntmCopy.addActionListener(mbControl);
		mnEdit.add(mntmCopy);

		// paste button
		JMenuItem mntmPaste = new JMenuItem("Paste");
		mntmPaste.setIcon(new ImageIcon(MainWindow.class.getResource("/com/sun/javafx/scene/web/skin/Paste_16x16_JFX.png")));
		mntmPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		// sets action command for this button
		mntmPaste.setActionCommand(MenuBarControl.Action.PASTE.toString());
		// sets action listener
		mntmPaste.addActionListener(mbControl);
		mnEdit.add(mntmPaste);

		// delete button
		JMenuItem mntmDelete = new JMenuItem("Delete");
		mntmDelete.setIcon(new ImageIcon(MainWindow.class.getResource("/resources/deleteIcon.gif")));
		mntmDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		// sets action command for this button
		mntmDelete.setActionCommand(MenuBarControl.Action.DELETE.toString());
		// sets action listener
		mntmDelete.addActionListener(mbControl);
		mnEdit.add(mntmDelete);

		// separator for organizing
		JSeparator separatorEditPreferences = new JSeparator();
		mnEdit.add(separatorEditPreferences);

		// preferences button
		JMenuItem mntmPreferences = new JMenuItem("Preferences...");
		mntmPreferences.setIcon(new ImageIcon(MainWindow.class.getResource("/resources/gearIcon.png")));
		// sets action command for this button
		mntmPreferences.setActionCommand(MenuBarControl.Action.PREFERENCES.toString());
		// sets action listener
		mntmPreferences.addActionListener(mbControl);
		mnEdit.add(mntmPreferences);

		// map menu group - disable when there are no maps loaded
		mnMap = new JMenu("Map  ");
		mnMap.setHorizontalAlignment(SwingConstants.CENTER);
		mnMap.setEnabled(false);
		this.add(mnMap);

		// new tileset button
		JMenuItem mntmNewTileset = new JMenuItem("New Tileset...                     ");
		mntmNewTileset.setIcon(new ImageIcon(MainWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/File.gif")));
		// sets action command for this button
		mntmNewTileset.setActionCommand(MenuBarControl.Action.NEWTILESET.toString());
		// sets action listener
		mntmNewTileset.addActionListener(mbControl);
		mnMap.add(mntmNewTileset);

		// resize map button
		JMenuItem mntmResizeMap = new JMenuItem("Resize Map...");
		mntmResizeMap.setIcon(new ImageIcon(MainWindow.class.getResource("/javax/swing/plaf/metal/icons/ocean/maximize.gif")));
		// sets action command for this button
		mntmResizeMap.setActionCommand(MenuBarControl.Action.RESIZEMAP.toString());
		// sets action listener
		mntmResizeMap.addActionListener(mbControl);
		mnMap.add(mntmResizeMap);

		// help menu group
		JMenu mnHelp = new JMenu("Help   ");
		mnHelp.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(mnHelp);

		// documentation button
		JMenuItem mntmDocumentation = new JMenuItem("Documentation             ");
		mntmDocumentation.setIcon(new ImageIcon(MainWindow.class.getResource("/resources/tbDocumentation.png")));
		// sets action command for this button
		mntmDocumentation.setActionCommand(MenuBarControl.Action.DOCUMENTATION.toString());
		// sets action listener
		mntmDocumentation.addActionListener(mbControl);
		mnHelp.add(mntmDocumentation);

		// about button
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.setIcon(new ImageIcon(MainWindow.class.getResource("/resources/tbAbout.png")));
		// sets action command for this button
		mntmAbout.setActionCommand(MenuBarControl.Action.ABOUT.toString());
		// sets action listener
		mntmAbout.addActionListener(mbControl);
		mnHelp.add(mntmAbout);
	}

	/**
	 * Observes map config to know if a map was loaded
	 * and activate  map and edit menu groups
	 */
	@Override
	public void update(Observable obs, Object arg1) {
		// enables remaining buttons on map load
		if(obs instanceof MapConfig) {
			mnMap.setEnabled(MapConfig.getInstance().isMapLoaded());
			mnEdit.setEnabled(MapConfig.getInstance().isMapLoaded());
			mntmSave.setEnabled(MapConfig.getInstance().isMapLoaded());
			mntmSaveAs.setEnabled(MapConfig.getInstance().isMapLoaded());
		}
	}
}
