package tagEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;

import resources.TestImages;

/**
 * This class represents the control in MVC. It will communicate between view
 * and model
 * 
 */
public class Control {
	private View GUI;
	private MP3File currentMP3;
	private DefaultMutableTreeNode tree = new DefaultMutableTreeNode();

	/**
	 * Constructor-method of the control. Will fill the tree when called.
	 */
	public Control() {
		this.fillTree();
		this.GUI = new View(this.getTree());
		GUI.getJmiSave().addActionListener(new SaveListener());
		GUI.getInformationArea().addMouseListener(new MouseSaveListener());
	}

	/**
	 * Method to fill the tree, just for testing atm
	 * 
	 */
	private void fillTree() {
		DefaultMutableTreeNode directory;
		DefaultMutableTreeNode mp3File;

		directory = new DefaultMutableTreeNode(new Directory("Refused"));
		tree.add(directory);

		mp3File = new DefaultMutableTreeNode(new MP3File("Refused",
				"New Noise", "The ...", "1998",
				this.bytesToPicture(TestImages.jpeg)));
		directory.add(mp3File);

		mp3File = new DefaultMutableTreeNode(new MP3File("Nirvana", "In Utero",
				"Dumb", "1993", this.bytesToPicture(TestImages.png)));
		directory.add(mp3File);
	}

	private BufferedImage bytesToPicture(byte[] picture) {
		BufferedImage returnPicture = null;
		try {
			returnPicture = ImageIO.read(new ByteArrayInputStream(picture));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnPicture;
	}

	public JTree getTree() {
		// Initialize the tree
		JTree DataTree = new JTree(this.tree);
		DataTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		// Listeners are strange...
		DataTree.addTreeSelectionListener(new TreeListener());
		return DataTree;

	}

	public View getGUI() {
		return this.GUI;
	}

	private class TreeListener implements TreeSelectionListener {

		public void valueChanged(TreeSelectionEvent e) {
			// Get the element that is the depth of the current selection
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath()
					.getLastPathComponent();
			// System.out.println("You selected " + node);
			if (node == null)
				return;
			Object nodeInfo = node.getUserObject();
			if (node.isLeaf()) {
				MP3File mp3 = (MP3File) nodeInfo;

				currentMP3 = mp3;

				// Update the UI
				GUI.getSong().setText(mp3.getSong());
				GUI.getArtist().setText(mp3.getArtist());
				GUI.getAlbum().setText(mp3.getAlbum());
				GUI.getYear().setText(mp3.getYear());
				GUI.getCover().setIcon(new ImageIcon(mp3.getCover()));
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Create a new Window
		Control demoControl = new Control();

		// Show window
		demoControl.getGUI().setVisible(true);

		// Exit program, if window is closed
		demoControl.getGUI().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	private class SaveListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
	
			if (arg0.getActionCommand() == "Metadaten speichern...") {
	
				if (currentMP3 != null) {
					currentMP3.setAlbum(GUI.getAlbum().getText());
					currentMP3.setArtist(GUI.getArtist().getText());
					currentMP3.setYear(GUI.getYear().getText());
					currentMP3.setSong(GUI.getSong().getText());
					GUI.getTree().updateUI();
				}
			}
	
		}
	}
	private class MouseSaveListener implements MouseListener {
		public void mouseClicked(MouseEvent e){
			if (currentMP3 != null) {
				currentMP3.setAlbum(GUI.getAlbum().getText());
				currentMP3.setArtist(GUI.getArtist().getText());
				currentMP3.setYear(GUI.getYear().getText());
				currentMP3.setSong(GUI.getSong().getText());
				GUI.getTree().updateUI();
			}
		}
		public void mousePressed(MouseEvent e) {
		       
		}

		public void mouseReleased(MouseEvent e) {
		      
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

	}

}
