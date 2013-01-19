package tagEditor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class MP3_FileVisitor extends SimpleFileVisitor<Path> {
	
	private PathMatcher pathMatcher;
	private DefaultMutableTreeNode tree;
	private Element currentDirectory;
	private Element root;
	private Element mp3File;
	private MP3File mp3;
	private MP3Parser parser =  new MP3Parser();;
	private Directory directory;
	private View GUI;
	private long lastTimeCacheChanged = Long.MAX_VALUE;
	private boolean cacheExists = false;
	private boolean cacheTooOld = false;
	private Document doc;
	private Document cacheDoc;
	private DocumentBuilder dBuilder;
	
	public MP3_FileVisitor(View GUI){
		this.pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + "*.mp3*");
		this.GUI = GUI;
			try {
				dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.doc = dBuilder.newDocument();
		
		
	}
	
	@Override
	public FileVisitResult visitFile(Path filePath,BasicFileAttributes basicFileAttributes) {
		if (filePath.getFileName() != null && pathMatcher.matches(filePath.getFileName())) {
			// Everything worked fine, so let's parse the file
			if(cacheExists){ // was the file modified after the cache was created?
				File f = new File(filePath.toString());
				if(cacheDoc.getElementById(filePath.toString()) == null){
					System.out.println("Found element that was not in the cache!");
					cacheTooOld = true;
				}
				else if(f.lastModified() > lastTimeCacheChanged){
					System.out.println("Found element that was modified!");
					cacheTooOld = true;
				}
			}
			if(cacheTooOld || !cacheExists){
			mp3 = new MP3File(); 
			try {
				mp3 = parser.readMP3(filePath);
				mp3.setPath(filePath.toString());
			} catch (IOException e) {
				// Maybe an ID3v24 tag, but nonetheless just continue
				return FileVisitResult.CONTINUE;
			}
			mp3File = XMLControl.MP3ToXML(mp3, this.doc);
			currentDirectory.appendChild(mp3File);
			} else {
				System.out.println("FILE:" + filePath + " war im cache!");
				Element temp = cacheDoc.getElementById(filePath.toString());
				currentDirectory.appendChild(doc.adoptNode(temp.cloneNode(true)));
			}
			cacheTooOld = false;
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path directoryPath,BasicFileAttributes attrs) {
		if (directoryPath.getFileName() != null) {
			directory = new Directory(directoryPath.toString());
			Element tempDirectory = XMLControl.DirectoryToXML(directory, doc);
			String myPath = directoryPath.toString() + "/cache.xml";
			File f = new File(myPath);
			if(!cacheExists){
				cacheExists = f.exists();
				lastTimeCacheChanged = f.lastModified();
			}
			//Does the cache exists? If so, skip the parsing and just use the cache
			if(f.exists()) { 
				try {
					this.cacheDoc = dBuilder.parse(f);
				} catch (SAXException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			if (currentDirectory != null) { // root set? If so, add the working directory onto the current parent directory
				currentDirectory.appendChild(tempDirectory); 
				currentDirectory = tempDirectory;
			} else { // root is not set
				root = tempDirectory;
				currentDirectory = tempDirectory;
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
	public FileVisitResult postVisitDirectory(Path directoryPath, IOException exc) {
		Element tempDirectory = currentDirectory;
		currentDirectory = (Element) currentDirectory.getParentNode();
		if (tempDirectory.getChildNodes().getLength() == 0 && currentDirectory != null) {
			currentDirectory.removeChild(tempDirectory);
		} 
		return FileVisitResult.CONTINUE;
	}
	
	public DefaultMutableTreeNode getTree() {
		doc.appendChild(root);
		TreeModel temp = new DefaultTreeModel(XMLControl.build(doc.getDocumentElement()));
		tree = new DefaultMutableTreeNode(temp);
		GUI.getTree().setModel(temp);
		return tree;
	}
	
	public boolean getCacheTooOld(){
		return this.cacheTooOld;
	}

}