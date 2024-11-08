package view;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.PlainDocument;

import controller.IntegerFilter;
import model.MapConfig;
import model.MapState;

/**
 * The dialog JPanel for the map boundaries resize functionality
 * @author Pedro Sampaio
 * @since  1.1
 *
 */
public class ResizeDialog extends JPanel{

	// default serial id
	private static final long serialVersionUID = 1L;


	/**
	 * Constructor
	 * @param mapStates	the states of the map (for resizing data)
	 */
	public ResizeDialog (MapState mapStates) {

		// text fields for dimension input
		JTextField xField = new JTextField(5);
		JTextField yField = new JTextField(5);
		xField.setHorizontalAlignment(SwingConstants.RIGHT);
		yField.setHorizontalAlignment(SwingConstants.RIGHT);

		// set default values as current ones
		xField.setText(Integer.toString(MapConfig.mapSizeX));
		yField.setText(Integer.toString(MapConfig.mapSizeY));

		// creates the dialog panel with its components
		JPanel dialogPanel = new JPanel();
		dialogPanel.add(new JLabel("Width (tiles): "));
		dialogPanel.add(xField);
		dialogPanel.add(new JLabel("Height: (tiles)"));
		dialogPanel.add(yField);

		// adds filter to allow only integers in text field
		// and a limit of characters in input
		PlainDocument doc = (PlainDocument) xField.getDocument();
		doc.setDocumentFilter(new IntegerFilter(4));
		doc = (PlainDocument) yField.getDocument();
		doc.setDocumentFilter(new IntegerFilter(4));

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

		// okay button
		final JButton okay = new JButton("Ok");
		okay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int mapSizeX = Integer.parseInt(xField.getText()); // filter guarantee to be of integer nature
				int mapSizeY = Integer.parseInt(yField.getText()); // filter guarantee to be of integer nature
				
				// guarantee integers are at least 10 *minimum size*
				if(mapSizeX < 10 || mapSizeY < 10)
					JOptionPane.showMessageDialog(MainWindow.getInstance(), "Values must be bigger or equal ten (10). \nPlease review the provided information.");
				else{
					Window dialogWindow = SwingUtilities.getWindowAncestor(okay); // gets dialog window to be able to close it

					// resize window
					MapConfig.mapSizeX = Integer.parseInt(xField.getText()); // filter guarantee to be of integer nature
					MapConfig.mapSizeY = Integer.parseInt(yField.getText()); // filter guarantee to be of integer nature
					MapConfig.getInstance().dispatchChanges(false);
					// redimensions map data
					mapStates.getCurrentMap().resizeMap();

					// closes dialog window
					if (dialogWindow != null) {
						dialogWindow.setVisible(false);
					}
				}
			}
		});

		// shows dialog window
		JOptionPane.showOptionDialog(
				null, 
				dialogPanel, 
				"Resize Map...", 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, 
				new Object[]{okay, cancel}, 
				okay);
	}

}
