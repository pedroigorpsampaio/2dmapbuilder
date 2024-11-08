package view;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import controller.FileManager;
import model.MapConfig;
import model.Tileset;
import model.TilesetConfig;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Component;

/**
 * The dialog JPanel for the new tileset
 * funcionality of adding tilesets to the program
 * @author Pedro Sampaio
 * @since  1.1
 *
 */
public class NewTilesetDialog extends JPanel{

	// generated serial
	private static final long serialVersionUID = 6581819392061590267L;
	private String absolutePath;		// tileset source absolute path
	private String tilesetName;			// tileset name

	/**
	 * Constructor
	 */
	public NewTilesetDialog() {

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
		gbl_dialogPanel.columnWidths = new int[]{32, 26, 246, 15, 79, 1, 0};
		gbl_dialogPanel.rowHeights = new int[]{23, 20, 0};
		gbl_dialogPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_dialogPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		dialogPanel.setLayout(gbl_dialogPanel);
		// creates label path 
		JLabel label = new JLabel("Path:");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.WEST;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 1;
		gbc_label.gridy = 0;
		dialogPanel.add(label, gbc_label);
		// source text field
		JTextField tfSourcePath = new JTextField(30);
		// disable interaction with sourcepath text field
		tfSourcePath.setFocusable(false);
		tfSourcePath.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_tfSourcePath = new GridBagConstraints();
		gbc_tfSourcePath.anchor = GridBagConstraints.WEST;
		gbc_tfSourcePath.insets = new Insets(0, 0, 5, 5);
		gbc_tfSourcePath.gridx = 2;
		gbc_tfSourcePath.gridy = 0;
		dialogPanel.add(tfSourcePath, gbc_tfSourcePath);
		
		// strut for spacing
		Component horizontalStrut = Box.createHorizontalStrut(15);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.anchor = GridBagConstraints.WEST;
		gbc_horizontalStrut.fill = GridBagConstraints.VERTICAL;
		gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut.gridx = 3;
		gbc_horizontalStrut.gridy = 0;
		dialogPanel.add(horizontalStrut, gbc_horizontalStrut); // a spacer
		
		// strut for spacing
		Component verticalStrut = Box.createVerticalStrut(15);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.fill = GridBagConstraints.HORIZONTAL;
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 0);
		gbc_verticalStrut.gridx = 5;
		gbc_verticalStrut.gridy = 0;
		dialogPanel.add(verticalStrut, gbc_verticalStrut); // a spacer

		// tileset name label
		JLabel lbName = new JLabel("Name:");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.insets = new Insets(0, 0, 0, 5);
		gbc_label_1.anchor = GridBagConstraints.EAST;
		gbc_label_1.gridx = 1;
		gbc_label_1.gridy = 1;
		dialogPanel.add(lbName, gbc_label_1);

		// tileset name text field
		JTextField tfTilesetName = new JTextField(20);
		GridBagConstraints gbc_tfTilesetName = new GridBagConstraints();
		gbc_tfTilesetName.anchor = GridBagConstraints.NORTHWEST;
		gbc_tfTilesetName.insets = new Insets(0, 0, 0, 5);
		gbc_tfTilesetName.gridwidth = 3;
		gbc_tfTilesetName.gridx = 2;
		gbc_tfTilesetName.gridy = 1;
		dialogPanel.add(tfTilesetName, gbc_tfTilesetName);
		
		// okay button
		final JButton okay = new JButton("Ok");
		okay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// dont go on if tileset name already exists in program
				boolean nameExists = false;
				for(int i = 0; i < TilesetConfig.getInstance().getTilesets().size(); i++) {
					if(tfTilesetName.getText().equals(TilesetConfig.getInstance().getTilesets().get(i).getName()))
						nameExists = true;
				}
				
				if(!nameExists) {
					Window dialogWindow = SwingUtilities.getWindowAncestor(okay); // gets dialog window to be able to close it
					
					// creates new tileset with information provided
					Tileset newTileset = TilesetConfig.getInstance().createTileset(tfTilesetName.getText(), MapConfig.tileSize, absolutePath);
					// get list of tilesets to add new tileset
					ArrayList<Tileset> tilesets = TilesetConfig.getInstance().getTilesets();
					// adds new tileset to the list of tilesets
					tilesets.add(newTileset);
					// sets new tileset as current selected tileset
					TilesetConfig.getInstance().setCurrentTilesetIdx(tilesets.size()-1);
					// notify observers of the newly added and selected tileset
					TilesetConfig.getInstance().dispatchChanges();
					
					// closes dialog window
					if (dialogWindow != null) {
						dialogWindow.setVisible(false);
					}
				}
				else { // inform that name already exist
					JOptionPane.showMessageDialog(MainWindow.getInstance(), "The tileset name \""+tfTilesetName.getText()+"\" is already taken in the current project.\n"
																			+ "In order to import a tileset, a unique name must be provided.");
				}
			}
		});
		okay.setEnabled(false); // disable by default, enabled when a file is selected

		// browse file button
		JButton btnBrowseFile = new JButton("Browse...");
		GridBagConstraints gbc_btnBrowseFile = new GridBagConstraints();
		gbc_btnBrowseFile.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnBrowseFile.insets = new Insets(0, 0, 5, 5);
		gbc_btnBrowseFile.gridx = 4;
		gbc_btnBrowseFile.gridy = 0;
		dialogPanel.add(btnBrowseFile, gbc_btnBrowseFile);
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
 				}
			}
		});

		// shows dialog window
		JOptionPane.showOptionDialog(
				null, 
				dialogPanel, 
				"New Tileset...", 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, 
				new Object[]{okay, cancel}, 
				okay);

	}

}
