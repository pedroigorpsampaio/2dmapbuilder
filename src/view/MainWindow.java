package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.border.MatteBorder;

import controller.FileManager;
import controller.KeyboardControl;
import model.Map;
import model.MapConfig;
import model.MapState;
import model.Preferences;
import model.Project;
import model.Tileset;
import model.TilesetConfig;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import java.awt.GridLayout;

/**
 * The main window of the 2D Map Builder that will act like
 * a hub for the tools of creating and visualizing the 2D map.
 * Its main purpose its to group all panels and options in a
 * proper way, allowing the user to manipulate the map while
 * visualizing all changes in real-time and easily find the
 * desired options available on the program.
 * 
 * @author		Pedro Sampaio
 * @version		0.3
 * @since		0.1	
 */
public class MainWindow extends JFrame implements Observer{

	private static final long serialVersionUID = 4091149087177277687L;
	private JPanel contentPane;	// content pane of the program
	private TilesetView tsView;	// the tileset view panel
	private JScrollPane scrollPaneMap;	// map's scroll panel
	private static MapState mapStates;	// the states of the map
	private static boolean firstProject = true; // if its the first project of the current execution
	private Map lastSavedMap; // the reference for the last saved map

	// mantains only one instance of the main window (singleton pattern)
	private static MainWindow instance = null;

	/**
	 * Initializes frame with initial configurations
	 */
	protected MainWindow() {
		// defeats instantiation.
		// general window config
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/resources/icon.png")));
		setTitle("2D Map Builder");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		// center frame
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
	    this.setLocation(x, y);
	    // sets grid bag layout
		getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		// initializes last saved map 
		lastSavedMap = new Map();
	}

	/**
	 * @return returns map config instance
	 * creates the instance if does not exist yet
	 */
	public static MainWindow getInstance() {
		if(instance == null)
			instance = new MainWindow();

		return instance;
	}

	/**
	 * Maximum number of states to keep saved for
	 * undoing and redoing operation
	 */
	private final static int N_STATES = 10;

	/**
	 * Launch the application creating a new frame (JFrame)
	 * that is configured by this class constructor.
	 * 
	 * @param args		Program parameters
	 * @since			0.1
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					// activates openGL 2d Acceleration
					System.setProperty("sun.java2d.opengl","True");
					System.setProperty("sun.java2d.ddscale","True");
					//System.setProperty("sun.java2d.trace", "log");

					// sets default configurations
					MapConfig.setDefault();
					Preferences.setDefault();

					// creates the states of the program with the max states
					// being each state a map in some point of time
					mapStates = new MapState(N_STATES); 

					// creates main frame
					instance = new MainWindow();

					instance.buildFrame();

					// adds the keyboard listener to the main frame
					instance.setVisible(true);
					instance.setFocusable(true);
					instance.requestFocus();
					// passes the camera for map control
					instance.addKeyListener(new KeyboardControl());
					
					// close behaviour (ask if use wants to close if there are unsaved modifications)
					instance.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
					instance.addWindowListener(new java.awt.event.WindowAdapter() {
					    @Override
					    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
					    	// if no project is loaded, exit
					    	if (!MapConfig.getInstance().isMapLoaded())
					    		System.exit(0);
					    	// if project is saved, exit
					    	if (MapConfig.getInstance().getProject().isSaved())
					    		System.exit(0);
					    	// else, ask if user wants to save before closing project
					    	int result = JOptionPane.showConfirmDialog(instance, 
						            "There are unsaved modifications. Do you wish to save?", "Really Closing?", 
						            JOptionPane.YES_NO_OPTION,
						            JOptionPane.QUESTION_MESSAGE);
					        if (result == JOptionPane.YES_OPTION){
					        	// user wants to save project before closing program
					        	// act as save as in case there is no save for this project yet
								if(MapConfig.getInstance().getProject().getSaveInfo() == null)
									FileManager.save(mapStates.getCurrentMap(), true);
								else // save over last save (quick save)
									FileManager.save(mapStates.getCurrentMap(), false);
								// exit program after save is done
					            System.exit(0);
					        }
					        else if(result == JOptionPane.NO_OPTION) // user does not want to save, just exit
					        	System.exit(0);
					    }
					});

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create and configure the main frame of 2D Map Builder.
	 * This constructor will build all interface objects
	 * that composes the main window of 2D Map builder and
	 * it was generated by the WindowBuilder Java GUI designer
	 * @param camera camera for map control
	 * 
	 * @since			0.1
	 */
	private void buildFrame() {

		// builds menu bar of program
		buildMenuBar();

		// configure content pane
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] {44, 234, 455, 0};
		gbl_contentPane.rowHeights = new int[] {30, 0, 30, 20, 105, 20, 30, 20, 180, 30};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0};
		contentPane.setLayout(gbl_contentPane);
		// adds separator for visual aid
		JSeparator separatorPanels = new JSeparator();
		separatorPanels.setForeground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_separatorPanels = new GridBagConstraints();
		gbc_separatorPanels.gridwidth = 3;
		gbc_separatorPanels.fill = GridBagConstraints.BOTH;
		gbc_separatorPanels.insets = new Insets(0, -5, 5, -5);
		gbc_separatorPanels.gridx = 0;
		gbc_separatorPanels.gridy = 1;
		contentPane.add(separatorPanels, gbc_separatorPanels);

		// Builds toolbar with quick access tools
		buildToolBar();

		// Build layers panel
		buildLayerPanel();

		// Builds map scroll pane 
		buildMapScrollPane();

		// builds tileset panel with no tilesets if no map is loaded yet
		if(!MapConfig.getInstance().isMapLoaded())
			buildTilesetPanel(null); 
	}

	private void buildMapScrollPane() {
		scrollPaneMap = new JScrollPane();
		scrollPaneMap.setBorder(new MatteBorder(1, 1, 1, 1, (Color) Color.LIGHT_GRAY));
		scrollPaneMap.getViewport().setMinimumSize(new Dimension(5000,5000));
		GridBagConstraints gbc_scrollPaneMap = new GridBagConstraints();
		gbc_scrollPaneMap.gridheight = 6;
		gbc_scrollPaneMap.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPaneMap.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneMap.gridx = 2;
		gbc_scrollPaneMap.gridy = 3;
		contentPane.add(scrollPaneMap, gbc_scrollPaneMap);	
	}

	/**
	 * Creates a project for the 2D map builder
	 * with the received information in parameter
	 * 
	 * @author Pedro Sampaio
	 * @since 1.2b
	 * @param project	the project with the necessary information to create it
	 */
	public void createProject(Project project) {	

		// if project is null stop the procedure informing the problem
		if(project == null) {
			System.err.println("MainWindow.createProject: Error loading project: Project parameter can't be null");
			new Exception().printStackTrace();
			return;
		}
		
		// sets window title with project file (if it is a saved project already)
		if(project.getSaveInfo() != null)
			instance.setTitle(project.getSaveInfo()[1] + " - " + "2D Map Builder");
		else	// sets untitled if its not saved yet
			instance.setTitle("Untitled*" + " - " + "2D Map Builder");

		// updates map configuration informations as soon as 
		// possible for map creation with correct sizes
		MapConfig.updateConfig(project.getTileSize(), project.getMapSizeX(), project.getMapSizeY());
		// sets the current project of the program
		MapConfig.getInstance().setProject(project);
		
		// a map is loaded bool 
		MapConfig.getInstance().setMapLoaded(true);
		
		// delete old observers
		ArrayList<Tileset> tilesets = TilesetConfig.getInstance().getTilesets();
		for(int i = 0; i < tilesets.size(); i++)
			tilesets.get(i).deleteObservers();

		// resets list of tilesets
		TilesetConfig.getInstance().getTilesets().clear();

		TilesetConfig.getInstance().deleteObservers();
		MapConfig.getInstance().deleteObservers();
		Preferences.getInstance().deleteObservers();
		mapStates.deleteObservers();

		// cleans content pane to rebuild it from the scratch
		contentPane.removeAll();
		// calls garbage collector
		Runtime rt = Runtime.getRuntime(); 
		rt.gc();
		// rebuilds frame
		buildFrame();
		revalidate();
		repaint();
		
		// adds tilesets from project to the program's list of tilesets
		for(int i = 0; i < project.getTilesets().size(); i++) {
			Tileset tileset = project.getTilesets().get(i); // the tileset
			//recalculates first ID
			tileset.setFirstID(TilesetConfig.getInstance().calculateFirstID());
			// adds tileset to the list of tilesets
			TilesetConfig.getInstance().getTilesets().add(tileset);
		}
		
		// sets current selected tileset as the first one in the list
		TilesetConfig.getInstance().setCurrentTilesetIdx(0);

		// builds tileset panel
		buildTilesetPanel(TilesetConfig.getInstance().getCurrentTileset()); // builds tileset panel with selected tileset

		// adds project saved map to the list of map states 
		// (if its a new project a new map is created)
		mapStates.AddState(project.getMap());
		// removes old states if its not first project
		if(!firstProject)
			mapStates.RemoveOldStates();

		// draws the viewport that contains the representation of the 2D map
		drawViewport(scrollPaneMap, mapStates);

		// notify that a map has been loaded
		MapConfig.getInstance().dispatchChanges(false);

		// updates flag that stores if this is first project loaded in this execution
		if(firstProject) 
			firstProject = false;
	
		// observes map states in case of changes in project for updating title
		mapStates.addObserver(instance);	
	}

	/**
	 * Builds the tileset view and its components
	 * 
	 * @author Pedro Sampaio
	 * @since 0.1
	 * 
	 */
	private void buildTilesetPanel(Tileset tileset) {
		// resets tileset view if it exists already
		//		if(tsView != null) {
		//			tsView.getComp
		//		}
		tsView = new TilesetView(contentPane);
		tsView.setTileset(tileset);
	}

	/**
	 * Builds the menu bar and its components
	 * 
	 * @author	Pedro Sampaio
	 * @since	0.1
	 */
	private void buildMenuBar() {
		// creates menu bar interface
		MenuBar menuBar = new MenuBar(mapStates);
		// adds to the frame
		setJMenuBar(menuBar);
	}

	/**
	 * Create and configure tool bar quick access buttons
	 * 
	 * @since	0.1
	 */
	private void buildToolBar() {
		// the main toolbar
		ToolBar toolBar = new ToolBar(mapStates);

		// creates grid bag constraints to adjust with main window interface
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.fill = GridBagConstraints.VERTICAL;
		gbc_toolBar.anchor = GridBagConstraints.WEST;
		gbc_toolBar.gridwidth = 3;
		gbc_toolBar.insets = new Insets(0, 0, 5, 5);
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 0;

		// adds toolbar to the contentpane
		contentPane.add(toolBar, gbc_toolBar);
	}

	/**
	 * Create and configure layers panel 
	 * 
	 * @since 0.1
	 */
	private void buildLayerPanel() {
		LayerView lView = new LayerView(contentPane, mapStates);
		contentPane.add(lView);
	}

	/**
	 * Instantiate the viewport that will be used in the Map JScrollPane
	 * Represents the 2D map visualization that the user will
	 * interact to create and design the maps
	 * 
	 * @param 	scrollPaneMap	the JScrollPane that will contain the viewport 
	 * @param 	mapStates 		the map states of the program containing maps in different points in time
	 * @since	0.1
	 */
	private void drawViewport(JScrollPane scrollPaneMap, MapState mapStates) {
		ViewportMap viewport = new ViewportMap(scrollPaneMap, mapStates);
		scrollPaneMap.setViewportView(viewport);
	}

	/**
	 * Updates visualization of the saving state of the program
	 * where a "*" in the window title, next to the project name,
	 * is displayed when there are unsaved modifications 
	 * @author Pedro Sampaio
	 * @since 1.4
	 * @param saved		if its up-to-date with save or not
	 */
	public void setSaved(boolean saved) {
		// get out of here if there is no save yet
		if(MapConfig.getInstance().getProject().getSaveInfo() == null)
			return;
		
		// gets current title
		String updatedTitle;

		// puts a "*" in the title in case of unsaved state
		if(!saved)
			updatedTitle = MapConfig.getInstance().getProject().getSaveInfo()[1]+"* - 2D Map Builder";
		else { // removes "*" from title in case of saved state
			updatedTitle = MapConfig.getInstance().getProject().getSaveInfo()[1]+" - 2D Map Builder";
			// updates last saved map
			lastSavedMap = mapStates.getCurrentMap();
		}
		// sets updated title of window
		instance.setTitle(updatedTitle);
	}
	
	/**
	 * @return the last saved map stored
	 */
	public Map getLastSavedMap() {
		return lastSavedMap;
	}

	/**
	 * @param lastSavedMap	the last saved map
	 */
	public void setLastSavedMap(Map lastSavedMap) {
		this.lastSavedMap = lastSavedMap;
	}

	/**
	 * Receives updates on map modifications
	 * to adjust window title to display that
	 * there are unsaved modifications
	 */
	@Override
	public void update(Observable obs, Object arg1) {
		if(obs instanceof MapState) {
			// updates title if last saved map isnt the current state
			if(!lastSavedMap.equals(mapStates.getCurrentMap()))
				setSaved(false);
			else { // makes sure that display is correctly set to saved (no "*")
				setSaved(true);
				// and sets project as saved
				MapConfig.getInstance().getProject().setSaved(true);
			}
		}

	}
}
