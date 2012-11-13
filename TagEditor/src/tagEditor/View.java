package tagEditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import resources.TestImages;


/**
 * Class represents the View of the MVC. 
 *
 */
public class View extends JFrame{
	
	// Declare the UI elements
	private JLabel songLabel, artistLabel, albumLabel, yearLabel;
	private JTextField song, artist, album, year;
	private JScrollPane treeView;
	// JLabel will have a ImageIcon inside to display the image
	private JLabel cover;
	
	public JTextField getSong() {
		return song;
	}

	public JTextField getArtist() {
		return artist;
	}

	public JTextField getAlbum() {
		return album;
	}

	public JTextField getYear() {
		return year;
	}

	public JLabel getCover() {
		return cover;
	}


	/** Constructor-Method of the View, will initialize the UI elements, 
	 * sets the listener for the UI representation of the tree and sets the preffered sizes.
	 * 
	 * @param tree The tree, that will hold the data to be represented
	 */
	public View(JTree tree){
		// Initialize UI elements
		this.song = new JTextField("");
		this.artist = new JTextField("");
		this.album = new JTextField("");
		this.year = new JTextField("");
		this.cover = new JLabel();
		
		this.songLabel = new JLabel("Titel:");
		this.artistLabel = new JLabel("Interpret:");
		this.albumLabel = new JLabel("Album:");
		this.yearLabel = new JLabel("Jahr:");
		// The tree should be scroll-able
		this.treeView = new JScrollPane(tree);	
		// Set the preferred dimensions
		this.song.setPreferredSize(new Dimension(200,20));
		this.artist.setPreferredSize(new Dimension(200,20));
		this.album.setPreferredSize(new Dimension(200,20));
		this.year.setPreferredSize(new Dimension(200,20));
		this.treeView.setPreferredSize(new Dimension(200,400));
		
		// Setup Window
		this.setTitle("ID3-Tag-Editor");
		this.setSize(800,500);
		
		setLayout();
		
	}
	
	/**
	 * Sets the layout of the UI. 
	 */
	private void setLayout() {
		
		Container informationArea = new Container();
		// This Layout isn't optimal
		informationArea.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(10,10,10,10);
		informationArea.add(this.song, c);
		c.gridy = 1;
		informationArea.add(this.artist, c);
		c.gridy = 2;
		informationArea.add(this.album, c);
		c.gridy = 3;
		informationArea.add(this.year, c);
		c.gridy = 4;
		informationArea.add(this.cover, c);
		c.gridx = 0;
		c.gridy = 0;
		informationArea.add(this.songLabel, c);
		c.gridy = 1;
		informationArea.add(this.artistLabel, c);
		c.gridy = 2;
		informationArea.add(this.albumLabel, c);
		c.gridy = 3;
		informationArea.add(this.yearLabel, c);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.treeView, informationArea);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(300);
		//this.setLayout(new BorderLayout());
		
		add(splitPane);	
		
		
			}
	public static void main(String[] args) {
		
		
	}
	
	
	

}



