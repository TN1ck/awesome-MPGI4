package tagEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import java.util.ArrayList;
import java.util.Enumeration;

import javax.activation.MimetypesFileTypeMap;
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
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.apache.commons.codec.binary.Base64;

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
	private String pathToXML;


	/**
	 * Constructor-method of the control. Will fill the tree when called.
	 */
	public Control() {
		this.GUI = new View(this.getTree(this.tree));
		GUI.getJmiSave().addActionListener(new SaveListener());
		GUI.getInformationArea().addMouseListener(new MouseSaveListener());
		GUI.getJmiOpen().addActionListener(new FileChooserListener());
		GUI.getEditCover().addActionListener(new PictureFileChooserListener());
		GUI.addWindowListener(new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			try {
				// I should refactor...
				writeCache((DefaultMutableTreeNode) ((DefaultTreeModel) tree.getUserObject()).getRoot(), pathToXML);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.exit(0);
		}
		});
		this.saver = new MP3Saver();
	}

	/**
	 * All files with .mp3 will be parsed, and if they are correct MP3 Files
	 * transferred into the tree
	 * @throws JAXBException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * 
	 */

	public void fillTree(String pathToDirectory) throws JAXBException, SAXException, IOException, ParserConfigurationException {	
		// Check's if there's a cache, if not, it will parse the directory
		this.pathToXML = pathToDirectory + "/cache.xml";
		MP3_FileVisitor fileVisitor = new MP3_FileVisitor();
		try {
			Files.walkFileTree(Paths.get(pathToDirectory), fileVisitor);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static DefaultMutableTreeNode readCache(File file) throws ParserConfigurationException, SAXException, IOException {
	    // do something with the current node instead of System.out
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
	    return build(doc.getDocumentElement());
	}
	
	public static DefaultMutableTreeNode build(Element e) {
		   DefaultMutableTreeNode result = null;
		   if(e.getNodeName() == "directory"){
			   result = new DefaultMutableTreeNode(new Directory(e.getAttribute("filename")));
			   for(int i = 0; i < e.getChildNodes().getLength(); i++){
				   if(e.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE){
					   result.add(build((Element) e.getChildNodes().item(i)));
				   }
			   }
		   } else {
			   MP3File mp3 = new MP3File();
			   ArrayList<Frame> frameList = new ArrayList<Frame>();
			   mp3.setPath(e.getAttribute("filepath"));
			   NodeList nodes = e.getChildNodes();
			   for(int i = 0; i < nodes.getLength(); i++){
				    Node node = nodes.item(i);
				    switch (node.getNodeName()) {
				   	case "artist": mp3.setArtist(node.getTextContent()); break;
				   	case "album":  mp3.setAlbum(node.getTextContent()); break;
				   	case "song":   mp3.setSong(node.getTextContent()); break;
				   	case "year":   mp3.setYear(node.getTextContent()); break;
				   	case "tagsize":mp3.setSize(new Integer(node.getTextContent())); break;
				   	case "cover":  mp3.setCover(Base64.decodeBase64(node.getTextContent())); break;
				   	case "frame": 
				   		NodeList frames = node.getChildNodes();
				   		Frame frame = new Frame();
				   		frame.setID(node.getAttributes().getNamedItem("ID").getTextContent());
				   		for(int j = 0; j < frames.getLength(); j++){
				   			Node frameNode = frames.item(j);
				   			switch (frameNode.getNodeName()){
				   			case "flags": 			frame.setFlags((short) (int) new Integer (frameNode.getTextContent())); break;
				   			case "encodingflag": 	frame.setEncodingflag(Base64.decodeBase64(frameNode.getTextContent())[0]); break;
				   			case "body":			frame.setBody(Base64.decodeBase64(frameNode.getTextContent())); break;
				   			case "pictureType":		frame.setPictureType(Base64.decodeBase64(frameNode.getTextContent())[0]); break;
				   			case "imageDescription":frame.setImageDescription(Base64.decodeBase64(frameNode.getTextContent())); break;
				   			case "MIMEType":		frame.setMIMEType(Base64.decodeBase64(frameNode.getTextContent()));break;			
				   			}
				   			
				   		}
				   		frameList.add(frame);
				   		break;
				   }
				  mp3.setFrames(frameList);
				  result = new DefaultMutableTreeNode(mp3);
					   
			   }
		   }
		   

		   return result;         
		}
	
	public static void writeCache(DefaultMutableTreeNode root, String pathToDirectory) throws ParserConfigurationException, TransformerException {
	    DefaultMutableTreeNode currentParent = root;
	    
	    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	    Document doc = docBuilder.newDocument();
	    
	    Element rootElement = fillXml(doc,root);
	    doc.appendChild(rootElement);
	    // write the content into xml file
 		TransformerFactory transformerFactory = TransformerFactory.newInstance();
 		Transformer transformer = transformerFactory.newTransformer();
 		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
 		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
 		DOMSource source = new DOMSource(doc);
 		StreamResult result = new StreamResult(new File(pathToDirectory));
  
 		// Output to console for testing
 		// StreamResult result = new StreamResult(System.out);
  
 		transformer.transform(source, result);
	  
	}
	
	private static Element fillXml(Document doc, DefaultMutableTreeNode root){
		 Element currentRoot = doc.createElement("directory"), currentElement = null, SubElement = null, SubSubElement = null;
		 currentRoot.setAttribute("filename", root.getUserObject().toString());
		 for(int i = 0; i < root.getChildCount(); i++){
		    	DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
		    		if(node.getUserObject() instanceof MP3File) {
		    			MP3File mp3 = (MP3File) node.getUserObject();
		    			currentElement = doc.createElement("mp3");
		    			currentElement.setAttribute("filepath", mp3.getPath());
		    			currentRoot.appendChild(currentElement);
		    				SubElement = doc.createElement("artist");
		    				SubElement.appendChild(doc.createTextNode(mp3.getArtist()));
		    				currentElement.appendChild(SubElement);
		    				SubElement = doc.createElement("song");
		    				SubElement.appendChild(doc.createTextNode(mp3.getSong()));
		    				currentElement.appendChild(SubElement);
		    				SubElement = doc.createElement("album");
		    				SubElement.appendChild(doc.createTextNode(mp3.getAlbum()));
		    				currentElement.appendChild(SubElement);
		    				SubElement = doc.createElement("year");
		    				SubElement.appendChild(doc.createTextNode(mp3.getYear()));
		    				currentElement.appendChild(SubElement);
		    				SubElement = doc.createElement("tagsize");
		    				SubElement.appendChild(doc.createTextNode(((Integer)mp3.getSize()).toString()));
		    				currentElement.appendChild(SubElement);
		    				SubElement = doc.createElement("cover");
		    				SubElement.appendChild(doc.createTextNode(new Base64().encodeToString(mp3.getCover())));
		    				currentElement.appendChild(SubElement);
		    				for(Frame f: mp3.getFrames()){
		    					SubElement = doc.createElement("frame");
		    					SubElement.setAttribute("ID", f.getID());
		    					currentElement.appendChild(SubElement);
		    						SubSubElement = doc.createElement("flags");
		    						SubSubElement.appendChild(doc.createTextNode(((Integer)(int) f.getFlags()).toString()));
		    						SubElement.appendChild(SubSubElement);
		    						SubSubElement = doc.createElement("encodingflag");
		    						SubSubElement.appendChild(doc.createTextNode(new Base64().encodeToString(new byte[]{f.getEncodingflag()})));
		    						SubElement.appendChild(SubSubElement);
		    						SubSubElement = doc.createElement("body");
		    						SubSubElement.appendChild(doc.createTextNode(new Base64().encodeToString(f.getBody())));
		    						SubElement.appendChild(SubSubElement);
		    						if(currentElement.getAttribute("ID").equals("APIC")){
		    							SubSubElement = doc.createElement("pictureType");
			    						SubSubElement.appendChild(doc.createTextNode(new Base64().encodeToString(new byte[]{f.getPictureType()})));
			    						SubElement.appendChild(SubSubElement);
			    						SubSubElement = doc.createElement("imageDescription");
			    						SubSubElement.appendChild(doc.createTextNode(new Base64().encodeToString(f.getImageDescription())));
			    						SubElement.appendChild(SubSubElement);
			    						SubSubElement = doc.createElement("MIMEType");
			    						SubSubElement.appendChild(doc.createTextNode(new Base64().encodeToString(f.getMIMEType())));
			    						SubElement.appendChild(SubSubElement);
		    						}
		    				}
		    			
		    		} else {
		    			currentRoot.appendChild(fillXml(doc,(DefaultMutableTreeNode) root.getChildAt(i)));
		    			
		    		}
		    }
		 return currentRoot;
		 
	}
	/**
	 * Will wrap the DefaultMutableTree into a JTree and adds all the listener.
	 * 
	 * @return
	 */
	public JTree getTree(DefaultMutableTreeNode tree) {
		// Initialize the tree
		JTree DataTree = new JTree(tree);
		DataTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
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
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
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
				try {
					fillTree(selectedFile.getPath());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}

	}

	private class PictureFileChooserListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			JFileChooser theFileChooser = new JFileChooser(".");
			theFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			FileFilter filter = new FileNameExtensionFilter("Image file","jpg", "jpeg", "png");
			theFileChooser.setFileFilter(filter);
			int returnval = theFileChooser.showOpenDialog(null);
			if (returnval == JFileChooser.APPROVE_OPTION) {
				File selectedFile = theFileChooser.getSelectedFile();
				try {
					currentMP3
							.setCover(loadFileFromPersistentStore(selectedFile));
				} catch (Exception e) {
					e.printStackTrace();
				}
				ImageIcon newImage = new ImageIcon(currentMP3.getCover());
				GUI.getCover().setIcon(new ImageIcon(newImage.getImage()
											.getScaledInstance(250, 250, java.awt.Image.SCALE_SMOOTH)));
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
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}

	private byte[] loadFileFromPersistentStore(File file) throws Exception,
			FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fileInputStream.read(data);
		fileInputStream.close();
		return data;
	}

	private class MP3_FileVisitor extends SimpleFileVisitor<Path> {
		
		private PathMatcher pathMatcher;
		private DefaultMutableTreeNode currentDirectory;
		private DefaultMutableTreeNode mp3File;
		private MP3File mp3;
		private MP3Parser parser =  new MP3Parser();;
		private Directory directory;
		
		public MP3_FileVisitor(){
			this.pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.mp3*");
		}
		
		@Override
		public FileVisitResult visitFile(Path filePath,BasicFileAttributes basicFileAttributes) {
			if (filePath.getFileName() != null && pathMatcher.matches(filePath.getFileName())) {
				System.out.println("FILE:" + filePath);
				// Everything worked fine, so let's parse the file
				mp3 = new MP3File(); 
				try {
					mp3 = parser.readMP3(filePath);
					mp3.setPath(filePath.toString());
				} catch (IOException e) {
					// Maybe an ID3v24 tag, but nonetheless just continue
					return FileVisitResult.CONTINUE;
				}
				mp3File = new DefaultMutableTreeNode(mp3);
				currentDirectory.add(mp3File);
			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path directoryPath,BasicFileAttributes attrs) {
			if (directoryPath.getFileName() != null) {
				directory = new Directory(directoryPath.getFileName().toString());
				DefaultMutableTreeNode tempDirectory = new DefaultMutableTreeNode(directory);
				String myPath = directoryPath.toString() + "/cache.xml";
				System.out.println(myPath);
				File f = new File(myPath);
				//Does the cache exists? If so, skip the parsing and just use the cache
				if(f.exists()) { 
					try {
						TreeModel temp = new DefaultTreeModel(readCache(f));
						tree = new DefaultMutableTreeNode(temp);
						GUI.getTree().setModel(temp);
						return FileVisitResult.SKIP_SIBLINGS;
					} catch (ParserConfigurationException | SAXException
							| IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else { // We'll need to parse the files
					if (currentDirectory != null) { // root set? If so, add the working directory onto the current parent directory
						currentDirectory.add(tempDirectory); 
						currentDirectory = tempDirectory;
					} else { // root is not set
						currentDirectory = tempDirectory;
						TreeModel temp = new DefaultTreeModel(currentDirectory);
						tree = new DefaultMutableTreeNode(temp);
						GUI.getTree().setModel(temp);	
					}
					System.out.println("DIR: " + directoryPath);
				}
			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path filePath, IOException exc) {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path directoryPath, IOException exc) {
			DefaultMutableTreeNode tempDirectory = currentDirectory;
			currentDirectory = (DefaultMutableTreeNode) currentDirectory.getParent();
			if (tempDirectory.getChildCount() == 0) {
				tempDirectory.removeFromParent();
			} 
			return FileVisitResult.CONTINUE;
		}

	}
	

	
}


