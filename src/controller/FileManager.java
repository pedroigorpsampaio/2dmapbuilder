package controller;

import java.awt.Desktop;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import model.Collider;
import model.Map;
import model.MapConfig;
import model.MapState;
import model.Project;
import model.Tile;
import model.Tileset;
import model.TilesetConfig;
import test.Config;
import view.MainWindow;
import view.NewFileDialog;

/**
 * Controls file related operations
 * 
 * @author Pedro Sampaio
 * @since 1.1
 *
 */
public class FileManager {

	/**
	 * Patterns:
	 * Singleton
	 */

	/** 
	 * file formats filters
	 */
	private FileNameExtensionFilter m2d;
	private FileNameExtensionFilter imageFilter;

	// the instance of filechooser 
	// keeps only one instance to remember last folder
	// and optimize file choosing
	JFileChooser fileChooser = null;

	// mantains only one instance of map config (singleton pattern)
	private static FileManager instance = null;

	// defeats instantiation.
	protected FileManager() {	
		// creates filter for 2D map builder files
		setM2d(new FileNameExtensionFilter("2D Map Builder files (*.m2d)", "m2d"));
		// creates filter for searching only image files
		setImageFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
		// initializes filechooser
		fileChooser = new JFileChooser(){
			// default serial id
			private static final long serialVersionUID = 1L;

			// overrides approve selection 
			// to show replacing alert in case of existing file
			@Override
			public void approveSelection(){
				File f = getSelectedFile();
				if(f.exists() && getDialogType() == SAVE_DIALOG){
					int result = JOptionPane.showConfirmDialog(this,"Do you want to replace "
							+ "the existing file?","Existing file",JOptionPane.YES_NO_OPTION);
					switch(result){
					case JOptionPane.YES_OPTION:
						super.approveSelection();
						return;
					case JOptionPane.NO_OPTION:
						return;
					case JOptionPane.CLOSED_OPTION:
						return;
					}
				}
				super.approveSelection();
			}        
		};
		// sets initial dir for filechooser
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));	//sets initial dir as user home
	}

	/**
	 * @return returns map config instance
	 * creates the instance if does not exist yet
	 */
	public static FileManager getInstance() {
		if(instance == null)
			instance = new FileManager();

		return instance;
	}

	/**
	 * Opens choose file dialog
	 * 
	 * @author Pedro Sampaio
	 * @param  fileFilter  extension filter to filter desired file extensions
	 * @since  1.0b
	 * @return the absolute path of the file choosed
	 */
	public String chooseFile(FileNameExtensionFilter fileFilter) {

		fileChooser.resetChoosableFileFilters(); // resets file filters
		fileChooser.addChoosableFileFilter(fileFilter);  // adds received choosable filter
		fileChooser.setAcceptAllFileFilterUsed(false);  // force filter received to be the only types of files searched

		int result = 0;
		try {
			result = fileChooser.showOpenDialog(MainWindow.getInstance());
		} catch (HeadlessException e1) {
			System.err.println("Could not open choose file dialog");
			e1.printStackTrace();
		}
		// file was chosen
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();

			if(Config.debug) {
				System.out.println("Selected file: " + selectedFile.getAbsolutePath());
			}
			return selectedFile.getAbsolutePath();
		}

		// file was not chosen
		return null;
	}

	/**
	 * Loads a project from a file of 2D Map Builder extension (.m2d)
	 * which carries a XML notation. Loads all information necessary
	 * for using it in the program and continue developing a 2D game
	 * from the point where it was last saved.
	 * 
	 * @author Pedro Sampaio
	 * @since 1.5
	 * @param absolutePath 	the path for the file containing the information to load is stored
	 * @return the project with the loaded information from save file
	 */
	public Project loadFileDOM(String absolutePath) {

		try {

			// the file containing the information to be loaded
			File fXmlFile = new File(absolutePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// folder of the save file (to get tileset images)
			String projFolder = absolutePath.substring(0, absolutePath.lastIndexOf("\\")+1);

			//optional, but recommended
			// reduction of redundancies
			doc.getDocumentElement().normalize();

			// debugs info read
			if(Config.debug) {
				System.out.println("\n----------------------------");
				System.out.println("Loading file: "+absolutePath);	
				System.out.println("----------------------------");
				System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			}

			/** 
			 * Retrieving basic information about the project's map
			 */

			// width of the map
			int mapWidth = Integer.parseInt(doc.getElementsByTagName("mapwidth").item(0).getTextContent());
			// height of the map
			int mapHeight = Integer.parseInt(doc.getElementsByTagName("mapheight").item(0).getTextContent());
			// width of the map
			int tileSize = Integer.parseInt(doc.getElementsByTagName("tilesize").item(0).getTextContent());

			// debugs basic map info read
			if(Config.debug) {
				System.out.println("\nBasic Map Info:");	
				System.out.println("Map Width: "+mapWidth);	
				System.out.println("Map Height: "+mapHeight);	
				System.out.println("Tile Size: "+tileSize);	
			}

			/**
			 * Retrieving tilesets information
			 */
			NodeList tsList = doc.getElementsByTagName("tileset"); // list of tileset nodes
			ArrayList<Tileset> tilesets = new ArrayList<Tileset>(); // the list of tilesets to be contained in the project

			if(Config.debug) {
				System.out.println("\nTileset Info:");	
			}

			// iterates through list of tilesets nodes to gather information
			for (int i = 0; i < tsList.getLength(); i++) {

				Node tsNode = tsList.item(i); // the current tileset node

				// gets information only if node is of element type
				if (tsNode.getNodeType() == Node.ELEMENT_NODE) {

					// the current element of tileset list
					Element tsElem = (Element) tsNode;

					// information gathered
					String tsName = tsElem.getElementsByTagName("name").item(0).getTextContent();
					String tsSource = tsElem.getElementsByTagName("source").item(0).getTextContent();
					int tsFirstID = Integer.parseInt(tsElem.getElementsByTagName("firstid").item(0).getTextContent());
					int tsTileCount = Integer.parseInt(tsElem.getElementsByTagName("tilecount").item(0).getTextContent());
					int tsTileSize = Integer.parseInt(tsElem.getElementsByTagName("tilesize").item(0).getTextContent());

					// creates tileset and adds to the list of tilesets
					tilesets.add(TilesetConfig.getInstance().createTileset(tsName, tsTileSize, projFolder+tsSource, tsFirstID, tsTileCount));

					// debugs tileset info read
					if(Config.debug) {
						System.out.println("Name : " + tsName);
						System.out.println("Source : " + tsSource);
						System.out.println("First ID : " + tsFirstID);
						System.out.println("Tilecount : " + tsTileCount);
						System.out.println("Tilesize : " + tsTileSize+"\n");
					}

				}
			}

			/**
			 * Retrieving layers and tiles information
			 */
			NodeList lList = doc.getElementsByTagName("layer"); // list of layer nodes
			String[] layers = new String[lList.getLength()];

			if(Config.debug) {
				System.out.println("\nLayers Info:");	
			}

			// iterates through list of layer nodes to gather information
			for (int i = 0; i < lList.getLength(); i++) {

				Node lNode = lList.item(i); // the current layer node

				// adds to the layers string array (removing initial break line char that is an extra unecessary info)
				layers[i] =  lNode.getTextContent().substring(1, lNode.getTextContent().length());

				// debugs tileset info read
				if(Config.debug) {
					System.out.println("Layer"+(i+1)+":\n" + lNode.getTextContent());
				}

			}
			
			/**
		     * Loads colliders from map collider mask file
		     */
	        // the file containing the information to be loaded
			String fileName = absolutePath.substring(absolutePath.lastIndexOf("\\")+1).split("\\.")[0];
			File initialFile = new File(projFolder + fileName + ".col");
	        InputStream colFile = new FileInputStream(initialFile);
	        
	        // buffered reader to read collider file
	        BufferedReader colReader = new BufferedReader(new InputStreamReader(colFile));
	        // iterates through lines and columns to get collider mask
	        String line;
	        Collider[][] colliders = new Collider[mapHeight][mapWidth]; 
	        
	        int i = 0;
	        try {
	            while ((line = colReader.readLine()) != null) {
	                String[] cols = line.split(",");
	                for (int j = 0; j < cols.length; j++) {
	                    if(cols[j].equals("1"))  // creates collider if 1 is the info
	                        colliders[i][j] = new Collider(1, i, j, false);
	                    else if(cols[j].equals("2"))
	                    	colliders[i][j] = new Collider(2, i, j, true);
	                }
	                i++;
	            }
	        } catch (IOException e) {
	            System.err.println("Could not read file: " + projFolder + fileName + ".col");
	            e.printStackTrace();
	        }


			// creates a map with the information gathered on layers, tilesets, and colliders
			Map loadedMap = MapConfig.getInstance().createMap(layers, tilesets, colliders);

			// createMap was not able to create the map with the informations provided 
			if(loadedMap == null) {
				System.err.println("\nError: could not create map with save file information");
			}

			// creates project with obtained information from save file
			Project project = new Project(loadedMap, tilesets, tileSize, mapWidth, mapHeight);
			// gets save info for project
			String[] saveInfo = new String[2];
			saveInfo[0] = projFolder;
			saveInfo[1] = absolutePath.substring(absolutePath.lastIndexOf("\\")+1);
			// sets project save info
			project.setSaveInfo(saveInfo);

			// returns the project created with loaded information
			return project;
		} catch (Exception e) { // error loading saved file
			System.err.println("Could not load save file: "+absolutePath+". Throw message: "+e.getMessage());
			// shows info message to user
			JOptionPane.showMessageDialog(MainWindow.getInstance(), "Tileset(s) could not be loaded. Are you sure \nthey are in the same folder as the project being loaded?");
			e.printStackTrace();
			return null;
		}

	}


	/**
	 * Opens save file dialog for collecting
	 * save file informations
	 * 
	 * @author Pedro Sampaio
	 * @param  fileFilter  extension filter to filter desired file extensions
	 * @return the absolute path and the name choosed for the file in a array
	 * 		   string[0] - path
	 * 		   string[i] - filename
	 * @since 1.2b
	 */
	public String[] chooseSaveFile(FileNameExtensionFilter fileFilter) {
		
		fileChooser.resetChoosableFileFilters(); // resets file filters
		fileChooser.addChoosableFileFilter(fileFilter);  // adds received filter
		fileChooser.setAcceptAllFileFilterUsed(false);  // force filter received to be the only types of files searched

		int result = 0;
		try {
			result = fileChooser.showSaveDialog(MainWindow.getInstance());
		} catch (HeadlessException e) {
			System.err.println("Could not open save file dialog");
			e.printStackTrace();
		}
		// dir/file was chosen
		if (result == JFileChooser.APPROVE_OPTION) {		
			String[] saveInfo = new String[2];
			// builds and returns save information
			saveInfo[0] = fileChooser.getCurrentDirectory().toString();
			saveInfo[1] = fileChooser.getSelectedFile().getName();

			if(Config.debug) {
				System.out.println("Save Dir: " + saveInfo[0] + " | Save Name: " + saveInfo[1]);
			}
			return saveInfo;
		}

		// dir/file was not chosen
		return null;
	}

	/**
	 * Saves the project in a file of 2D Map Builder extension (.m2d)
	 * which carries a XML notation. Stores all information necessary
	 * for reusing it in the program and for developing a 2D game
	 * based on the created map. It also saves collider mask in a .col file
	 * 
	 * @author Pedro Sampaio
	 * @since  1.3
	 * @param project	the project to be saved contained all necessary information 
	 * @param filename 	the filename of the save to be created
	 * @param path 		the path where the save will be created
	 * @return	boolean that represents if a file was saved or not
	 */
	public boolean saveFileDOM(Project project, String path, String filename) {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			//add elements to Document

			// create the root element
			Element rootElement = doc.createElement("map");
			//append root element to document
			doc.appendChild(rootElement);

			Element elem; // elements to add to document

			// create map data elements and place them under root

			// map width in tiles
			elem = doc.createElement("mapwidth");
			elem.appendChild(doc.createTextNode(Integer.toString(project.getMapSizeX())));
			rootElement.appendChild(elem);

			// map height in tiles
			elem = doc.createElement("mapheight");
			elem.appendChild(doc.createTextNode(Integer.toString(project.getMapSizeY())));
			rootElement.appendChild(elem);

			// map tile size in pixels
			elem = doc.createElement("tilesize");
			elem.appendChild(doc.createTextNode(Integer.toString(project.getTileSize())));
			rootElement.appendChild(elem);

			// create elements for tilesets
			// map tile size in pixels
			for(int i = 0; i < project.getTilesets().size(); i++) {
				elem = doc.createElement("tileset");
				Tileset ts = project.getTilesets().get(i); // the iteration tileset	
				elem.appendChild(getInsetNode(doc, "name", ts.getName()));						// name of the tile	
				// gets extension from absolute path 
				String imgExtension = ts.getImagePath().split("\\.")[1];
				if(Config.debug)
					System.out.println("Saving file of extension: ."+imgExtension + " in folder: "+path);
				// saves tileset source image in the save destiny directory
				saveImage(ts.getImage(), ts.getName(), imgExtension, path);
				// appends the rest of tileset information
				elem.appendChild(getInsetNode(doc, "source", ts.getName()+"."+imgExtension));			// image source of tile (to be copied to save folder)
				elem.appendChild(getInsetNode(doc, "firstid", Integer.toString(ts.getFirstID())));		// first id that is present in this tileset (global id considering all tilesets)
				elem.appendChild(getInsetNode(doc, "tilecount", Integer.toString(ts.getTileCount())));	// the count of tiles in this tileset to help get global id
				elem.appendChild(getInsetNode(doc, "tilesize", Integer.toString(ts.getTileSize())));	// tile size of the tile in this tileset 
				rootElement.appendChild(elem);
			}

			// create elements for layers of the map 
			for(int l = 0; l < project.getMap().getLayers().size(); l++) {
				//  beggining of a layer element in document 
				elem = doc.createElement("layer");

				// matrix of tiles for current layer
				Tile[][] tiles = project.getMap().getLayers().get(l).getTiles();

				// iterates through matrix to fill tile data to the save file
				for(int i = 0; i < tiles.length; i++) {
					elem.appendChild(doc.createTextNode("\n")); // break lines
					for(int j = 0; j < tiles[i].length; j++) {
						// if null, write 0 representing that there are no tiles in this position
						if(tiles[i][j] == null)
							elem.appendChild(doc.createTextNode("0")); // null data
						else // write tile data
							elem.appendChild(doc.createTextNode(Integer.toString(tiles[i][j].getId()))); // tile data

						// if its not last data append "," (string splitter)
						if(!((i == tiles.length - 1) &&  (j == tiles[i].length - 1)))
							elem.appendChild(doc.createTextNode(","));	
					}
				}

				elem.appendChild(doc.createTextNode("\n")); // break last lines
				// end of a layer element in document
				rootElement.appendChild(elem);
			}
			
			// saves collider mask in other file .col
			Collider[][] colliders = project.getMap().getColliders();
			// collider save data
			String colSaveData = "";
			
			// iterates through matrix to fill collider data to the save file
			for(int i = 0; i < colliders.length; i++) {
				for(int j = 0; j < colliders[i].length; j++) {
					// if null, write 0 representing that there are no collider in this position
					if(colliders[i][j] == null)
						colSaveData += "0"; // null data
					else // write collider data
						colSaveData += Integer.toString(colliders[i][j].getId()); // tile data

					// if its not last data append "," (string splitter)
					if(!((i == colliders.length - 1) &&  (j == colliders[i].length - 1)))
						colSaveData += ",";	
				}
				colSaveData += "\n"; // break lines
			}
			
			// save collider data in file .col
			String fileNameWOExt = filename.replace(".m2d", "");
			PrintWriter colFile = new PrintWriter(path + "/" + fileNameWOExt + ".col");
			colFile.print(colSaveData);
			colFile.close();

			//for output to file, console
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			//for pretty print
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);

			//write to console or file
			StreamResult console = new StreamResult(System.out);
			String fileNameExt = filename;
			if(!filename.contains(".m2d"))	// adds extension if name does not contains it
				fileNameExt += ".m2d";
			StreamResult file = new StreamResult(new File(path + "/" + fileNameExt));

			//write data
			if(Config.debug) // debugs xml data created
				transformer.transform(source, console);
			transformer.transform(source, file);


		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Saves images in the directory path provided
	 * 
	 * @param image			the image to be saved
	 * @param filename		The filename for the saved image
	 * @param ext			the extension for the saved image
	 * @param path			the path to save the image
	 */
	private static void saveImage(BufferedImage image, String filename, String ext, String path) {

		File file = new File(path + "/" + filename + "." + ext);
		if(Config.debug) {
			System.out.println("Saving image: "+ file.getPath());
		}
		try {
			ImageIO.write(image, ext, file);  // ignore returned boolean
		} catch(IOException e) {
			System.err.println("Could not save image: " + file.getPath() +
					": " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Method for creating
	 * a node with the given parameters
	 * 
	 * @param doc		// the doc to attach node
	 * @param name		// name of the node
	 * @param value		// value of the node
	 * @return	the node created
	 */
	private static Node getInsetNode(Document doc, String name, String value) {
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value));
		return node;
	}

	/**
	 * @return the 2D map builder extension filter
	 */
	public FileNameExtensionFilter get2DMapBuilderFilter() {
		return m2d;
	}

	/**
	 * @param m2d the 2D map builder extension filter
	 */
	private void setM2d(FileNameExtensionFilter m2d) {
		this.m2d = m2d;
	}

	/**
	 * @return the filter for image extensions
	 */
	public FileNameExtensionFilter getImageFilter() {
		return imageFilter;
	}

	/**
	 * @param imageFilter the filter for image extensions
	 */
	private void setImageFilter(FileNameExtensionFilter imageFilter) {
		this.imageFilter = imageFilter;
	}

	/**
	 * Saves the current project in its current state
	 * in the desired location obtained by user input
	 * or in the project save directory last obtained
	 * via user input depending on boolean received
	 * 
	 * @author Pedro Sampaio
	 * @param map 	the current state of the map in the program
	 * @param saveAs if this save is a save as save or not (gets new info or use current info)
	 * @since 1.3
	 * @return true if save was made, false otherwise
	 */
	public static boolean save(Map map, boolean saveAs) {

		String[] saveInfo; // the save info to use for storing the save
		if(saveAs) {	// save as, get new info from user
			// get save info
			saveInfo = FileManager.getInstance().chooseSaveFile(FileManager.getInstance().get2DMapBuilderFilter());	
		} else { // gets current save info from project
			saveInfo = MapConfig.getInstance().getProject().getSaveInfo();
		}

		// if save info is not set correctly, return
		if(saveInfo == null)
			return false;

		// makes sure project is up-to-date 
		Project project = MapConfig.getInstance().getProject();
		// updates project tilesets
		project.setTilesets(TilesetConfig.getInstance().getTilesets());
		// updates project map
		project.setMap(map);
		// updates project map dimensions
		project.setMapSizeX(MapConfig.mapSizeX);
		project.setMapSizeY(MapConfig.mapSizeY);
		// updates save info
		project.setSaveInfo(saveInfo);
		// saves the file with all project info on desired location
		// while copying the tilesets source images to the destiny
		FileManager.getInstance().saveFileDOM(project, saveInfo[0], saveInfo[1]);
		// updates status of project to saved
		MapConfig.getInstance().getProject().setSaved(true);
		// dispatch project saved for observers
		MapConfig.getInstance().dispatchChanges(false);

		// updates window title to show that project is saved (removes *)
		MainWindow.getInstance().setTitle(project.getSaveInfo()[1] + " - 2D Map Builder");
		MainWindow.getInstance().setLastSavedMap(map);
		
		// returns that a save was made
		return true;
	}
	
	/**
	 * Creates a new project with user
	 * provided informations
	 * 
	 * @author Pedro Sampaio
	 * @param mapStates	the states of the map
	 * @since  1.6
	 */
	public static void newProject(MapState mapStates) {
		// creates dialog panel to collect new file informations from user
		NewFileDialog nFileDialog = new NewFileDialog(mapStates);
		
		// returns if cancel was clicked
		if(nFileDialog.isCanceled())
			return;
		// create tileset with obtained info
		Tileset tileset = TilesetConfig.getInstance().createTileset(nFileDialog.getTilesetName(),
							nFileDialog.getTileSize(), nFileDialog.getAbsolutePath());
		// creates project with obtained information
		MainWindow.getInstance().createProject(new Project(tileset, nFileDialog.getTileSize(), 
												nFileDialog.getMapSizeX(), nFileDialog.getMapSizeY()));
	}
	
	/**
	 * Opens an existing project chosen by the user, 
	 * loading it into the program. Asks user if wants to save in cases
	 * of unsaved modifications in current project.
	 * 
	 * @author Pedro Sampaio
	 * @param mapStates 	the states of the map to check if current project is saved
	 * @since 1.6
	 */
	public static void open(MapState mapStates) {
		// checks if user wants to save if there are unsaved modifications
		FileManager.getInstance().checkSaveUnsaved(mapStates);
		
		// shows choose file dialogs with 2dmapbuilder file filter to choose a saved map
		String sFilePath = FileManager.getInstance().chooseFile(FileManager.getInstance().get2DMapBuilderFilter());
		
		// if user has not informed the file, return
		if(sFilePath == null)
			return;
		System.out.println(sFilePath);
		
		// loads the saved information into a new project
		Project project = FileManager.getInstance().loadFileDOM(sFilePath);
		
		// error happened while loading project
		if(project == null) {
			System.err.println("Could not load project");
			return;
		}
		
		// creates project with loaded information
		MainWindow.getInstance().createProject(project);
	}

	/**
	 * Creates a dialog allowing user to save
	 * unsaved modifications
	 * @author Pedro Sampaio
	 * @since	1.5
	 * @param mapStates	the states of the map
	 * @return true if user saved correctly or discarded modifications, false if user closed window
	 */
	public boolean checkSaveUnsaved(MapState mapStates) {
		// if there are unsaved modifications,
		// shows dialog asking if user wants to save
		if (MapConfig.getInstance().isMapLoaded() && !MapConfig.getInstance().getProject().isSaved() ) {
			boolean saved; // if save was made
			// ask if user wants to save
			int result = JOptionPane.showConfirmDialog(MainWindow.getInstance(), 
					"There are unsaved modifications. Do you wish to save?", "Really Discarding?", 
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (result == JOptionPane.YES_OPTION){
				// user wants to save project 
				// act as" save as" in case there is no save for this project yet
				if(MapConfig.getInstance().getProject().getSaveInfo() == null)
					saved = FileManager.save(mapStates.getCurrentMap(), true);
				else // save over last save (quick save)
					saved = FileManager.save(mapStates.getCurrentMap(), false);
				
				// if save was not made and user wanted to save, return false
				// representing that the operation should be done again for safety
				if(!saved)
					return false;
			}
			else if(result == JOptionPane.NO_OPTION); // user does not want to save, just move on
			else	// user closed window
				return false;
		}
		return true;
	}
	

	/**
	 * Opens a page in user's default browser
	 * 
	 * @author Pedro Sampaio
	 * @since 1.6
	 * @param url	the page's url to open in default browser
	 */
	public static void openWebpage(String url) {
	    try {
	        Desktop.getDesktop().browse(new URL(url).toURI());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
