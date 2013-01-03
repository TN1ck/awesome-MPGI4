package tagEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javax.activation.MimetypesFileTypeMap;

/**
 * This class represents the control in MVC. It will communicate between view
 * and model
 * 
 */
public class Control {
	private View GUI;
	private MP3File currentMP3;
	private DefaultMutableTreeNode tree;
	private MP3Saver saver;
	private MP3Parser parser;

	/**
	 * Constructor-method of the control. Will fill the tree when called.
	 */
	public Control() {
		this.GUI = new View(this.getTree(this.tree));
		GUI.getJmiSave().addActionListener(new SaveListener());
		GUI.getInformationArea().addMouseListener(new MouseSaveListener());
		GUI.getJmiOpen().addActionListener(new FileChooserListener());
		GUI.getEditCover().addActionListener(new PictureFileChooserListener());
		this.saver = new MP3Saver();
		this.parser = new MP3Parser();
	}

	/**
	 * All files with .mp3 will be parsed, and if they are correct MP3 Files
	 * transferred into the tree
	 * 
	 */

	public void fillTree(String pathToDirectory) {
		MP3_FileVisitor fileVisitor = new MP3_FileVisitor();
		fileVisitor.pathMatcher = FileSystems.getDefault().getPathMatcher(
				"glob:" + "*.mp3*");

		try {
			Files.walkFileTree(Paths.get(pathToDirectory), fileVisitor);
		}

		catch (IOException e) {

			e.printStackTrace();
		}

	}

	private class MP3_FileVisitor extends SimpleFileVisitor<Path> {
		private PathMatcher pathMatcher;
		private DefaultMutableTreeNode currentDirectory;
		private DefaultMutableTreeNode mp3File;
		private MP3File mp3;
		private Directory directory;

		@Override
		public FileVisitResult visitFile(Path filePath,
				BasicFileAttributes basicFileAttributes) {

			if (filePath.getFileName() != null
					&& pathMatcher.matches(filePath.getFileName())) {

				System.out.println("FILE:" + filePath);

				mp3 = new MP3File();

				try {
					mp3 = parser.readMP3(filePath);
					mp3.setPath(filePath.toString());
				}

				catch (IOException e) {
					return FileVisitResult.CONTINUE;
				}

				mp3File = new DefaultMutableTreeNode(mp3);

				currentDirectory.add(mp3File);

			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path directoryPath,
				BasicFileAttributes attrs) {

			if (directoryPath.getFileName() != null) {

				directory = new Directory(directoryPath.getFileName()
						.toString());
				DefaultMutableTreeNode tempDirectory = new DefaultMutableTreeNode(
						directory);
				
				if (currentDirectory != null) {
					
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
		public FileVisitResult postVisitDirectory(Path directoryPath,
				IOException exc) {
			DefaultMutableTreeNode tempDirectory = currentDirectory;
			currentDirectory = (DefaultMutableTreeNode) currentDirectory
					.getParent();
			if (tempDirectory.getChildCount() == 0) {
				tempDirectory.removeFromParent();
			}
			return FileVisitResult.CONTINUE;
		}

	}

	/**
	 * Will wrap the DefaultMutableTree into a JTree and adds all the listener.
	 * 
	 * @return
	 */
	public JTree getTree(DefaultMutableTreeNode tree) {
		// Initialize the tree
		JTree DataTree = new JTree(tree);
		DataTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		// Listeners are strange...
		DataTree.addTreeSelectionListener(new TreeListener());
		return DataTree;

	}
	public DefaultMutableTreeNode getRawTreeNode() {
		return tree;
	}

	public View getGUI() {
		return this.GUI;
	}

	private class TreeListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			// Get the element that is the depth of the current selection
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath()
					.getLastPathComponent();
			// System.out.println("You selected " + node);
			if (node == null)
				return;
			Object nodeInfo = node.getUserObject();
			if (node.isLeaf() && nodeInfo instanceof MP3File) {
				MP3File mp3 = (MP3File) nodeInfo;

				currentMP3 = mp3;

				// Update the UI
				GUI.setVisibilityOfInfoArea(true);
				GUI.getSong().setText(mp3.getSong());
				GUI.getArtist().setText(mp3.getArtist());
				GUI.getAlbum().setText(mp3.getAlbum());
				GUI.getYear().setText(mp3.getYear());
				if (mp3.getCover() != null) {
					ImageIcon newImage = new ImageIcon(mp3.getCover());
					GUI.getCover().setIcon(
							new ImageIcon(newImage.getImage()
									.getScaledInstance(250, 250,
											java.awt.Image.SCALE_SMOOTH)));
				} else {
					ImageIcon newImage = new ImageIcon(Paths.get(
							"./ressources/noimage.jpg").toString());
					GUI.getCover().setIcon(
							new ImageIcon(newImage.getImage()
									.getScaledInstance(250, 250,
											java.awt.Image.SCALE_SMOOTH)));
				}
			} else {
				GUI.setVisibilityOfInfoArea(false);

			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Create a new Window
		Control mainProgram = new Control();
		// Show window
		mainProgram.getGUI().setVisible(true);
		// Exit program, if window is closed
		mainProgram.getGUI().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	/**
	 * FileChooseListener for choosing the directory to be parsed
	 * 
	 * 
	 */
	private class FileChooserListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			JFileChooser theFileChooser = new JFileChooser(".");
			theFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnval = theFileChooser.showOpenDialog(null);
			if (returnval == JFileChooser.APPROVE_OPTION) {
				File selectedFile = theFileChooser.getSelectedFile();
				fillTree(selectedFile.getPath());
			}
		}

	}

	private class PictureFileChooserListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			JFileChooser theFileChooser = new JFileChooser(".");
			theFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			FileFilter filter = new FileNameExtensionFilter("Image file",
					"jpg", "jpeg", "png");
			theFileChooser.setFileFilter(filter);
			int returnval = theFileChooser.showOpenDialog(null);

			if (returnval == JFileChooser.APPROVE_OPTION) {
				File selectedFile = theFileChooser.getSelectedFile();

				try {
					currentMP3
							.setCover(loadFileFromPersistentStore(selectedFile));
				}

				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ImageIcon newImage = new ImageIcon(currentMP3.getCover());
				GUI.getCover().setIcon(
						new ImageIcon(newImage.getImage().getScaledInstance(
								250, 250, java.awt.Image.SCALE_SMOOTH)));
				GUI.getCover().updateUI();
				MimetypesFileTypeMap mtftp = new MimetypesFileTypeMap();
				mtftp.addMimeTypes("png jpg jpeg");
				currentMP3.setPictureMIME("image/"
						+ mtftp.getContentType(selectedFile));
				System.out.println(currentMP3.getPictureMIME());

			}
		}
	}

	private class SaveListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub

			if (arg0.getActionCommand() == "Metadaten speichern...") {

				if (currentMP3 != null) {
					currentMP3.setAlbum(GUI.getAlbum().getText());
					currentMP3.setArtist(GUI.getArtist().getText());
					currentMP3.setYear(GUI.getYear().getText());
					currentMP3.setSong(GUI.getSong().getText());
					try {
						saver.saveMP3(currentMP3);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}
	}

	private class MouseSaveListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (currentMP3 != null) {
				currentMP3.setAlbum(GUI.getAlbum().getText());
				currentMP3.setArtist(GUI.getArtist().getText());
				currentMP3.setYear(GUI.getYear().getText());
				currentMP3.setSong(GUI.getSong().getText());
				try {
					saver.saveMP3(currentMP3);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				GUI.getTree().updateUI();
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

	}

	private byte[] loadFileFromPersistentStore(File file) throws Exception,
			FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fileInputStream.read(data);
		fileInputStream.close();
		return data;
	}

}
