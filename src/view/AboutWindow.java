package view;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import controller.FileManager;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * 
 * The visualization of the about
 * window that contains information about the project
 * 
 * @author Pedro Sampaio
 * @since 1.6
 *
 */
public class AboutWindow extends JDialog {
	
	// default serial id
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor that creates
	 * the view for the about window
	 * and displays it
	 */
	public AboutWindow() {

		// general window config
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/resources/icon.png")));
		setTitle("About 2D Map Builder");
		this.setResizable(false);
		
		// center frame
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(600, 420);
	    int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);    
		setBounds(x, y, 600, 420);
		
		// creates the grid bag layout for the dialog
	    GridBagLayout gridBagLayout = new GridBagLayout();
	    gridBagLayout.columnWidths = new int[]{362, 0};
	    gridBagLayout.rowHeights = new int[]{25, 107, 13, 107, 107, 0};
	    gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
	    gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
	    getContentPane().setLayout(gridBagLayout);
	    
	    // draws 2D map builder logo
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.insets = new Insets(0, 0, 5, 0);
	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    JLabel lb2DMP = new JLabel(new ImageIcon(MainWindow.class.getResource("/resources/logo2DMP.png")));
	    getContentPane().add(lb2DMP, gbc);
	    
	    // draws icad logo
	    GridBagConstraints gbcIcad = new GridBagConstraints();
	    gbcIcad.insets = new Insets(0, 0, 5, 0);
	    gbcIcad.gridx = 0;
	    gbcIcad.gridy = 3;
	    JLabel lbICAD = new JLabel(new ImageIcon(MainWindow.class.getResource("/resources/icadLogo.png")));
	    getContentPane().add(lbICAD, gbcIcad);
	    
	    // adds the text for the about
	    // about text
	    String aboutStr = "<html><center>2D Map Builder was developed by Pedro Sampaio in 2017, with<br>the support of the research laboratory ICAD/VisionLab"
	    		+ " from <br>PUC-Rio - Pontifical Catholic University of Rio de Janeiro.<br><br> "
	    		+ "<br><center>Copyright \u00a9 2017 Pedro Sampaio</center></html>";
	    
	    GridBagConstraints gbc_About = new GridBagConstraints();
	    gbc_About.gridx = 0;
	    gbc_About.gridy = 4;
	    JLabel lbAbout = new JLabel(aboutStr);
	    getContentPane().add(lbAbout, gbc_About);

	    // icad webpage link
	    JButton btnIcadPage = new JButton("<html><a href='http://www.icad.puc-rio.br'>http://www.icad.puc-rio.br</center></html>");
	    btnIcadPage.setContentAreaFilled(false);
	    btnIcadPage.setBorderPainted(true);
	    btnIcadPage.setFocusPainted(false);
	    btnIcadPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// goes to icad page if clicked
				FileManager.openWebpage("http://www.icad.puc-rio.br");
			}
		});

	    // webpage link constraints
	    GridBagConstraints gbc_webBtn = new GridBagConstraints();
	    gbc_webBtn.insets = new Insets(35, 0, 5, 0);
	    gbc_webBtn.gridx = 0;
	    gbc_webBtn.gridy = 4;
	    // adds webpage link
	    getContentPane().add(btnIcadPage, gbc_webBtn);
	    
		// sets dialog config
		this.setModal (true);
		this.setModalityType (ModalityType.APPLICATION_MODAL);
		this.setVisible(true);

		// packs dialog
	   // this.pack();
	}
	
}
