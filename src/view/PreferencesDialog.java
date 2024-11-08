package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import model.Preferences;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Dialog of preferences menu containing
 * customizations that user can make
 * 
 * @author Pedro Sampaio
 * @since  1.2
 */
public class PreferencesDialog extends JPanel {

	// default serial id
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public PreferencesDialog () {

		// size of color picker button icons
		int cBtnWidth = 30;
		int cBtnHeight = 15;
		Dimension cBtnDimension = new Dimension((int) (cBtnWidth*1.32f), (int) (cBtnHeight*1.32f));

		// creates the dialog panel with its components
		JPanel dialogPanel = new JPanel();
		GridBagLayout gbl_dialogPanel = new GridBagLayout();
		gbl_dialogPanel.columnWidths = new int[]{0, 101, 0};
		gbl_dialogPanel.rowHeights = new int[]{14, 39, 23, 20, 0, 20, 0, 20, 3, 24, 0};
		gbl_dialogPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_dialogPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		dialogPanel.setLayout(gbl_dialogPanel);


		// close button (close window)
		final JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Window dialogWindow = SwingUtilities.getWindowAncestor(close);  // gets dialog window to be able to close it

				// closes dialog window
				if (dialogWindow != null) {
					dialogWindow.setVisible(false);
				}
			}
		});

		// Map Background Color
		GridBagConstraints gbc_2 = new GridBagConstraints();
		gbc_2.anchor = GridBagConstraints.NORTHWEST;
		gbc_2.insets = new Insets(0, 0, 5, 5);
		gbc_2.gridx = 0;
		gbc_2.gridy = 2;
		JLabel label_3 = new JLabel("Map Background Color: ");
		dialogPanel.add(label_3, gbc_2);

		// BackgroundColor button 
		final JButton btnBGColor = new JButton();
		// sets icon of button - a rectangle filled with current color
		btnBGColor.setIcon(createColorIcon(Preferences.viewportBackgroundColor, cBtnWidth, cBtnHeight));
		// sets size of button 
		btnBGColor.setPreferredSize(cBtnDimension);
		GridBagConstraints gbc_btnBGColor = new GridBagConstraints();
		gbc_btnBGColor.insets = new Insets(0, 0, 5, 0);
		gbc_btnBGColor.gridy = 2;
		gbc_btnBGColor.gridx = 1;
		dialogPanel.add(btnBGColor, gbc_btnBGColor);
		// BackgroundColor button action listener
		btnBGColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// pick selection color of tileset
				Color initialColor = Preferences.viewportBackgroundColor;
				Color newColor = JColorChooser.showDialog(null, "Pick a Color...", initialColor);
				if(newColor != null) { // only sets color if user has chosen one
					Preferences.viewportBackgroundColor = newColor; // sets new color
					Preferences.getInstance().dispatchChanges(); // notify observers
					// updates button icon
					btnBGColor.setIcon(createColorIcon(newColor, cBtnWidth, cBtnHeight));
				}
			}
		});

		// Map Selection Color
		GridBagConstraints gbc_1 = new GridBagConstraints();
		gbc_1.anchor = GridBagConstraints.WEST;
		gbc_1.insets = new Insets(0, 0, 5, 5);
		gbc_1.gridx = 0;
		gbc_1.gridy = 4;
		JLabel label_1 = new JLabel("Map Selection Color: ");
		dialogPanel.add(label_1, gbc_1);

		// Map Selection Color Button
		JButton btnMapSelColor = new JButton();
		// sets icon of button - a rectangle filled with current color
		btnMapSelColor.setIcon(createColorIcon(Preferences.mapSelectionColor, cBtnWidth, cBtnHeight));
		// sets size of button 
		btnMapSelColor.setPreferredSize(cBtnDimension);
		// Map Selection color button action listener
		btnMapSelColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// pick selection color of tileset
				Color initialColor = Preferences.mapSelectionColor;
				Color newColor = JColorChooser.showDialog(null, "Pick a Color...", initialColor);
				if(newColor != null) { // only sets color if user has chosen one
					Preferences.mapSelectionColor = newColor; // sets new color
					Preferences.getInstance().dispatchChanges(); // notify observers
					// updates button icon
					btnMapSelColor.setIcon(createColorIcon(newColor, cBtnWidth, cBtnHeight));
				}
			}
		});
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(0, 0, 5, 0);
		gbc_button.gridx = 1;
		gbc_button.gridy = 4;
		dialogPanel.add(btnMapSelColor, gbc_button);

		// Tileset Selection Color
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 0, 5, 5);
		gbc.gridx = 0;
		gbc.gridy = 6;
		JLabel label = new JLabel("Tileset Selection Color: ");
		dialogPanel.add(label, gbc);

		// Tileset Selection Color Button
		JButton btnTsSelColor = new JButton();
		// sets icon of button - a rectangle filled with current color
		btnTsSelColor.setIcon(createColorIcon(Preferences.selectionColor, cBtnWidth, cBtnHeight));
		// sets size of button 
		btnTsSelColor.setPreferredSize(cBtnDimension);
		// Tileset Selection color button action listener
		btnTsSelColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// pick selection color of tileset
				Color initialColor = Preferences.selectionColor;
				Color newColor = JColorChooser.showDialog(null, "Pick a Color...", initialColor);
				if(newColor != null) { // only sets color if user has chosen one
					Preferences.selectionColor = newColor; // sets new color
					Preferences.getInstance().dispatchChanges(); // notify observers
					// updates button icon
					btnTsSelColor.setIcon(createColorIcon(newColor, cBtnWidth, cBtnHeight));
				}
			}
		});
		GridBagConstraints gbc_button_1 = new GridBagConstraints();
		gbc_button_1.insets = new Insets(0, 0, 5, 0);
		gbc_button_1.gridx = 1;
		gbc_button_1.gridy = 6;
		dialogPanel.add(btnTsSelColor, gbc_button_1);

		// Show Grid Option
		GridBagConstraints gbcGrid = new GridBagConstraints();
		gbcGrid.insets = new Insets(0, 0, 5, 5);
		gbcGrid.anchor = GridBagConstraints.WEST;
		gbcGrid.gridx = 0;
		gbcGrid.gridy = 8;
		JLabel lbGrid = new JLabel("Show Grid: ");
		dialogPanel.add(lbGrid, gbcGrid);

		// show/hide grid checkbox
		JCheckBox cbGrid = new JCheckBox();
		cbGrid.setSelected(Preferences.viewportShowGrid); // sets current preference
		// show/hide grid checkbox listener
		cbGrid.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Preferences.viewportShowGrid = cbGrid.isSelected(); // sets the new preference
				Preferences.getInstance().dispatchChanges(); // notify observers for immediate preview
			}
		});
		GridBagConstraints gbc_cbGrid = new GridBagConstraints();
		gbc_cbGrid.insets = new Insets(0, 0, 5, 0);
		gbc_cbGrid.gridx = 1;
		gbc_cbGrid.gridy = 8;
		dialogPanel.add(cbGrid, gbc_cbGrid);	
		
		// shows dialog window
		JOptionPane.showOptionDialog(
				null, 
				dialogPanel, 
				"Preferences", 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, 
				new Object[]{close}, 
				close);		
	}

	/**
	 * Creates a square shape icon
	 * of color received in parameter
	 * 
	 * @author Pedro Sampaio
	 * @param color		The Color to paint the image icon square
	 * @param width		The width of the image icon square
	 * @param height	The height of the image icon square
	 * @return 			The square shape image icon created with the color received
	 * @since 1.2
	 */
	private ImageIcon createColorIcon(Color color, int width, int height) {

		// creates a buffered image to draw onto
		BufferedImage bImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		// create graphics2D component to be able to draw on buffered image
		Graphics2D bGraphics = bImg.createGraphics();
		// set color of draw
		bGraphics.setColor(color);
		// draw shape on buffered image
		bGraphics.fillRect(0, 0, width, height);
		// disposes uneeded graphics
		bGraphics.dispose();
		// return an imageicon from the buffered image
		return new ImageIcon(bImg);
	}

}
