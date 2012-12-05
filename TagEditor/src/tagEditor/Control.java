package tagEditor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

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
	private DefaultMutableTreeNode tree;

	/**
	 * Constructor-method of the control. Will fill the tree when called.
	 */
	public Control() {
		this.GUI = new View(this.getTree());
		GUI.getJmiSave().addActionListener(new SaveListener());
		GUI.getInformationArea().addMouseListener(new MouseSaveListener());
		GUI.getJmiOpen().addActionListener(new FileChooserListener());
	}

	/**
	 * Method to fill the tree, just for testing atm
	 * 
	 */
	
	private void fillTree(String pathToDirectory){
		MP3_FileVisitor fileVisitor = new MP3_FileVisitor();
        fileVisitor.pathMatcher = FileSystems.getDefault().
        getPathMatcher("glob:" + "*.mp3*");
        try {
			Files.walkFileTree(Paths.get(pathToDirectory), fileVisitor);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private class MP3_FileVisitor extends SimpleFileVisitor<Path>{
		private PathMatcher pathMatcher;
		private DefaultMutableTreeNode currentDirectory;
		private DefaultMutableTreeNode mp3File;
		private MP3Parser Parser = new MP3Parser();
		private MP3File mp3;
		private Directory directory;
		
		@Override
		public FileVisitResult visitFile(Path filePath, BasicFileAttributes basicFileAttributes) {
			if(filePath.getFileName() != null && pathMatcher.matches(filePath.getFileName())){
				System.out.println("FILE:" + filePath);
				mp3 = new MP3File();
				try {
					mp3 = Parser.readMP3(filePath);
				} catch (IOException e) {
					return FileVisitResult.CONTINUE;
				}
				mp3File =  new DefaultMutableTreeNode(mp3);
				currentDirectory.add(mp3File);
		
			}
			return FileVisitResult.CONTINUE;
		}
		
		@Override
	    public FileVisitResult preVisitDirectory(Path directoryPath, BasicFileAttributes attrs) {
	        if (directoryPath.getFileName() != null){
	        		directory = new Directory(directoryPath.getFileName().toString());
	        		DefaultMutableTreeNode tempDirectory = new DefaultMutableTreeNode(directory); 
	        		if(currentDirectory != null){
		        		currentDirectory.add(tempDirectory);
		        		currentDirectory = tempDirectory;
	        		} else {
	        			currentDirectory = tempDirectory;
	        			TreeModel temp = new DefaultTreeModel(currentDirectory);
	        			tree = new DefaultMutableTreeNode(temp);
	        			GUI.getTree().setModel(temp);
	        		}
	            	System.out.println("DIR: " + directoryPath);
	        }
	        return FileVisitResult.CONTINUE;
	    }
		
		@Override
	    public FileVisitResult visitFileFailed(Path filePath, IOException exc) {
	        return FileVisitResult.CONTINUE;
	    }
		
		@Override
		public FileVisitResult postVisitDirectory(Path directoryPath,IOException exc){
			currentDirectory = (DefaultMutableTreeNode) currentDirectory.getParent();
			return FileVisitResult.CONTINUE;
		}
		
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
		DataTree.setCellRenderer(new CustomIconRenderer());
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
			if (node.isLeaf() && nodeInfo instanceof MP3File ) {
				MP3File mp3 = (MP3File) nodeInfo;

				currentMP3 = mp3;

				// Update the UI
				GUI.getSong().setText(mp3.getSong());
				GUI.getArtist().setText(mp3.getArtist());
				GUI.getAlbum().setText(mp3.getAlbum());
				GUI.getYear().setText(mp3.getYear());
				if(mp3.getCover() != null){
					ImageIcon newImage = new ImageIcon(mp3.getCover());
					GUI.getCover().setIcon(new ImageIcon(newImage.getImage().getScaledInstance(250,250,java.awt.Image.SCALE_SMOOTH)));
				}
				else {
					ImageIcon newImage = new ImageIcon(Paths.get("./ressources/noimage.jpg").toString());
					GUI.getCover().setIcon(new ImageIcon(newImage.getImage().getScaledInstance(250,250,java.awt.Image.SCALE_SMOOTH)));
				}
			} 
			else{
				GUI.getSong().setText("");
				GUI.getArtist().setText("");
				GUI.getAlbum().setText("");
				GUI.getYear().setText("");
				GUI.getCover().setIcon(new ImageIcon(Paths.get("./ressources/spaceholder.png").toString()));
			
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
	private class FileChooserListener implements ActionListener {
		 public void actionPerformed(ActionEvent actionEvent) {
		        JFileChooser theFileChooser = new JFileChooser(".");
		        theFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		        int returnval = theFileChooser.showOpenDialog(null);
		        if (returnval == JFileChooser.APPROVE_OPTION) {
		          File selectedFile = theFileChooser.getSelectedFile();
		          System.out.println(selectedFile.getPath());
		          fillTree(selectedFile.getPath());
		        } 
		      }
		 
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

class CustomIconRenderer extends DefaultTreeCellRenderer {
	Icon mp3Icon;
	Icon directoryIcon;
	public CustomIconRenderer() {
		mp3Icon = new DefaultTreeCellRenderer().getDefaultLeafIcon();
		directoryIcon = new DefaultTreeCellRenderer().getDefaultClosedIcon();
	}
	public Component getTreeCellRendererComponent(JTree tree,
	Object value,boolean sel,boolean expanded,boolean leaf,
	int row,boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel,expanded, leaf, row, hasFocus);
		Object nodeObj = ((DefaultMutableTreeNode)value).getUserObject();
		// check whatever you need to on the node user object
		if (nodeObj instanceof Directory) {
			setIcon(directoryIcon);
		} else {
			setIcon(mp3Icon);
		}
		return this;
	}
}

