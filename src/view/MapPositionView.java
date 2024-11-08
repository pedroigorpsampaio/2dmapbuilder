package view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

/**
 * View for the current mouse tile position on map
 * displayed to help the user map positioning
 * 
 * @author Pedro Sampaio
 * @since	1.2
 *
 */
public class MapPositionView extends JPanel {
	
	// default serial id
	private static final long serialVersionUID = 1L;
	private JLabel statusLabel;	// label for the position

	/**
	 * Constructor
	 * creates the visualization of the current mouse tile position on map
	 * @param contentPane the content pane of the main frame
	 */
	public MapPositionView (Container contentPane) {
		
		// if receives null content pane, return
		if(contentPane == null)
			return;
		
		this.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.setPreferredSize(new Dimension(contentPane.getWidth(), 16));
		statusLabel = new JLabel("[0,0]");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_pos = new GridBagConstraints();
		gbc_pos.insets = new Insets(0, 0, 0, 0);
		gbc_pos.fill = GridBagConstraints.VERTICAL;
		gbc_pos.gridy = 9;
		gbc_pos.gridx = 2;
		this.add(statusLabel, gbc_pos);
		contentPane.add(this, gbc_pos);
	}

	/**
	 * Updates label text
	 * @param text to update label with
	 */
	protected void updateText(String text) {
		this.statusLabel.setText(text);
	}
}
