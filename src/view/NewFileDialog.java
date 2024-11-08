package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.PlainDocument;

import controller.FileManager;
import controller.IntegerFilter;
import model.MapConfig;
import model.MapState;

/**
 * The dialog panel for the new file feature
 * Collects information for the creation of the new project
 * 
 * @author 	Pedro Sampaio
 * @since	1.2b
 *
 */
public class NewFileDialog extends JPanel {

	// default serial id
	private static final long serialVersionUID = 1L;

	private String absolutePath;	// tileset source absolute path
	private String tilesetName;		// tileset name
	private int tileSize;			// the dimension of a tile
	private int mapSizeX;			// number of tiles in map on x-axis (columns)
	private int mapSizeY;			// number of tiles in map on y-axis (lines)

	private boolean canceled = true; // bool that represents if the dialog was canceled in some way(cancel or x button)

	/**
	 * Constructor
	 * @param mapStates  the states of the map
	 */
	public NewFileDialog(MapState mapStates) {

		// if there are unsaved modifications and a project is loaded,
		// shows dialog asking if user wants to save before creating another project
		if (MapConfig.getInstance().isMapLoaded() && !MapConfig.getInstance().getProject().isSaved() ) {

			// else, ask if user wants to save before creating new project
			int result = JOptionPane.showConfirmDialog(MainWindow.getInstance(), 
					"There are unsaved modifications. Do you wish to save?", "Really Discarding?", 
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (result == JOptionPane.YES_OPTION){
				// user wants to save project before creating new project
				// act as save as in case there is no save for this project yet
				if(MapConfig.getInstance().getProject().getSaveInfo() == null)
					FileManager.save(mapStates.getCurrentMap(), true);
				else // save over last save (quick save)
					FileManager.save(mapStates.getCurrentMap(), false);
			}
			else if(result == JOptionPane.NO_OPTION); // user does not want to save, just move on
			else	// user closed window, do not open new file dialog
				return;
		}

		// cancel button (close window - no new modifications)
		final JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Window dialogWindow = SwingUtilities.getWindowAncestor(cancel);  // gets dialog window to be able to close it

				// closes dialog window
				if (dialogWindow != null) {
					dialogWindow.setVisible(false);
				}
			}
		});

		// creates the dialog panel with its components
		JPanel dialogPanel = new JPanel();
		GridBagLayout gbl_dialogPanel = new GridBagLayout();
		gbl_dialogPanel.columnWidths = new int[]{0, 23, 0, 0, 0};
		gbl_dialogPanel.rowHeights = new int[]{23, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_dialogPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_dialogPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		dialogPanel.setLayout(gbl_dialogPanel);


		// adds to the dialog panel resize components
		// width component (tiles)
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 0, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 1;
		JLabel label_1 = new JLabel("Width (tiles): ");
		dialogPanel.add(label_1, gbc);

		// text fields for dimension input
		// x field
		JTextField xField = new JTextField(5);
		GridBagConstraints gbc_xField = new GridBagConstraints();
		gbc_xField.anchor = GridBagConstraints.WEST;
		gbc_xField.insets = new Insets(0, 0, 5, 5);
		gbc_xField.gridx = 1;
		gbc_xField.gridy = 1;
		dialogPanel.add(xField, gbc_xField);
		xField.setHorizontalAlignment(SwingConstants.RIGHT);
		// default values
		xField.setText("50");

		// height component (tiles)
		GridBagConstraints gbc_1 = new GridBagConstraints();
		gbc_1.insets = new Insets(0, 0, 5, 5);
		gbc_1.gridx = 1;
		gbc_1.gridy = 1;
		JLabel label_2 = new JLabel("Height: (tiles)");
		dialogPanel.add(label_2, gbc_1);
		// y field
		JTextField yField = new JTextField(5);
		yField.setHorizontalAlignment(SwingConstants.RIGHT);
		yField.setText("50");
		GridBagConstraints gbc_yField = new GridBagConstraints();
		gbc_yField.anchor = GridBagConstraints.EAST;
		gbc_yField.insets = new Insets(0, 0, 5, 5);
		gbc_yField.gridx = 1;
		gbc_yField.gridy = 1;
		dialogPanel.add(yField, gbc_yField);

		// tilesize label
		GridBagConstraints gbc_2 = new GridBagConstraints();
		gbc_2.anchor = GridBagConstraints.WEST;
		gbc_2.insets = new Insets(0, 0, 5, 5);
		gbc_2.gridx = 0;
		gbc_2.gridy = 2;
		JLabel label_3 = new JLabel("Tilesize: ");
		dialogPanel.add(label_3, gbc_2);
		// tilesize field
		JTextField tSizeField = new JTextField(5);
		tSizeField.setHorizontalAlignment(SwingConstants.RIGHT);
		tSizeField.setText("32");
		GridBagConstraints gbc_tSizeField = new GridBagConstraints();
		gbc_tSizeField.anchor = GridBagConstraints.WEST;
		gbc_tSizeField.insets = new Insets(0, 0, 5, 5);
		gbc_tSizeField.gridx = 1;
		gbc_tSizeField.gridy = 2;
		dialogPanel.add(tSizeField, gbc_tSizeField);


		// adds filter to allow only integers in text field
		// and a limit of characters in input
		PlainDocument doc = (PlainDocument) xField.getDocument();
		doc.setDocumentFilter(new IntegerFilter(4));
		doc = (PlainDocument) yField.getDocument();
		doc.setDocumentFilter(new IntegerFilter(4));
		doc = (PlainDocument) tSizeField.getDocument();
		doc.setDocumentFilter(new IntegerFilter(3));

		Component verticalStrut = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut.gridx = 0;
		gbc_verticalStrut.gridy = 4;
		dialogPanel.add(verticalStrut, gbc_verticalStrut);

		// creates label path 
		JLabel label = new JLabel("Tileset Path:");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.WEST;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 0;
		gbc_label.gridy = 6;
		dialogPanel.add(label, gbc_label);
		// source text field
		JTextField tfSourcePath = new JTextField(20);
		// disable interaction with sourcepath text field
		tfSourcePath.setFocusable(false);
		tfSourcePath.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_tfSourcePath = new GridBagConstraints();
		gbc_tfSourcePath.anchor = GridBagConstraints.WEST;
		gbc_tfSourcePath.insets = new Insets(0, 0, 5, 5);
		gbc_tfSourcePath.gridx = 1;
		gbc_tfSourcePath.gridy = 6;
		dialogPanel.add(tfSourcePath, gbc_tfSourcePath);

		// browse file button
		JButton btnBrowseFile = new JButton("Browse...");
		GridBagConstraints gbc_btnBrowseFile = new GridBagConstraints();
		gbc_btnBrowseFile.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnBrowseFile.insets = new Insets(0, 0, 5, 5);
		gbc_btnBrowseFile.gridx = 2;
		gbc_btnBrowseFile.gridy = 6;
		dialogPanel.add(btnBrowseFile, gbc_btnBrowseFile);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalStrut.gridx = 3;
		gbc_horizontalStrut.gridy = 6;
		dialogPanel.add(horizontalStrut, gbc_horizontalStrut);

		// tileset name label
		JLabel lbName = new JLabel("Tileset Name:");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.insets = new Insets(0, 0, 0, 5);
		gbc_label_1.anchor = GridBagConstraints.EAST;
		gbc_label_1.gridx = 0;
		gbc_label_1.gridy = 7;
		dialogPanel.add(lbName, gbc_label_1);

		// tileset name text field
		JTextField tfTilesetName = new JTextField(20);
		GridBagConstraints gbc_tfTilesetName = new GridBagConstraints();
		gbc_tfTilesetName.insets = new Insets(0, 0, 0, 5);
		gbc_tfTilesetName.anchor = GridBagConstraints.NORTHWEST;
		gbc_tfTilesetName.gridwidth = 2;
		gbc_tfTilesetName.gridx = 1;
		gbc_tfTilesetName.gridy = 7;
		dialogPanel.add(tfTilesetName, gbc_tfTilesetName);

		// okay button
		final JButton okay = new JButton("Ok");
		okay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// dimensions data
				mapSizeX = Integer.parseInt(xField.getText()); // filter guarantee to be of integer nature
				mapSizeY = Integer.parseInt(yField.getText()); // filter guarantee to be of integer nature
				tileSize = Integer.parseInt(tSizeField.getText()); // filter guarantee to be of integer nature
				// updates tileset name
				tilesetName = tfTilesetName.getText();
				
				// guarantee integers are at least 10 *minimum size*
				if(mapSizeX < 10 || mapSizeY < 10 || tileSize < 10)
					JOptionPane.showMessageDialog(MainWindow.getInstance(), "Values must be bigger or equal ten (10). \nPlease review the provided information.");
				else{
					Window dialogWindow = SwingUtilities.getWindowAncestor(okay); // gets dialog window to be able to close it

					// not cancelled
					canceled = false;		

					// closes dialog window
					if (dialogWindow != null) {
						dialogWindow.setVisible(false);
					}
				}
			}
		});
		okay.setEnabled(false); // disable by default, enabled when a file is selected



		// browse file action listener for choosing files
		btnBrowseFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// shows choose file dialogs with image filter
				absolutePath = FileManager.getInstance().chooseFile(FileManager.getInstance().getImageFilter());

				// sets text field source path with the absolute path
				tfSourcePath.setText(absolutePath);

				if(absolutePath != null) {
					okay.setEnabled(true); // enables okay button
					tilesetName = absolutePath.substring(absolutePath.lastIndexOf("\\")+1).split("\\.")[0];
					if(tfTilesetName.getText().isEmpty()) // if user has not provided a name for the tileset yet
						tfTilesetName.setText(tilesetName); // uses file name as suggestion
					else
						tilesetName = tfTilesetName.getText(); // user has provided a name for the tileset
				}
			}
		});

		// shows dialog window
		JOptionPane.showOptionDialog(
				null, 
				dialogPanel, 
				"New Map", 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, 
				new Object[]{okay, cancel}, 
				okay);



	}

	/** 
	 * Getters
	 */

	/**
	 * @return the absolute path of the tileset selected
	 */
	public String getAbsolutePath() {
		return absolutePath;
	}

	/**
	 * @return the name of the tileset 
	 */
	public String getTilesetName() {
		return tilesetName;
	}

	/**
	 * @return the tile size of the map
	 */
	public int getTileSize() {
		return tileSize;
	}

	/**
	 * @return the number of tiles in map on x-axis (columns)
	 */
	public int getMapSizeX() {
		return mapSizeX;
	}

	/**
	 * @return the  number of tiles in map on y-axis (lines)
	 */
	public int getMapSizeY() {
		return mapSizeY;
	}


	/**
	 * Checks if the dialog was canceled
	 * @return if the dialog was canceled
	 */
	public boolean isCanceled() {
		return canceled;
	}
}
