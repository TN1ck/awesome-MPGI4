package tagEditor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

import javax.swing.Box.Filler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class CacheTests {

	@Test
	public void TestXML (){
		
		Control myControl = new Control();
		DefaultMutableTreeNode newRoot = null;
		
		try {
			myControl.fillTree("tests");
		} 
		catch (JAXBException | SAXException | IOException | ParserConfigurationException e) {
			fail("Exception while loading directory");
		}
		
		DefaultMutableTreeNode myRoot= myControl.getRoot();
		
		try {
			XMLControl.writeCache(myRoot, "tests/cache.xml");
			
		} catch (ParserConfigurationException | TransformerException e) {
			
			fail("Exception while writing xml ");

		}
		
		try {
			newRoot = XMLControl.readCache(new File("tests/cache.xml"));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			
			fail("Exception while writing reading  xml ");
		}
		
		
		assertEquals(myRoot.isRoot(), newRoot.isRoot());
		
		assertEquals(myRoot.getChildCount(), newRoot.getChildCount());
		int count = myRoot.getChildCount();
		
		for (int i = 0; i < count; i++) {
			DefaultMutableTreeNode originalChild = (DefaultMutableTreeNode) myRoot.getChildAt(i);
			DefaultMutableTreeNode newChild = (DefaultMutableTreeNode) newRoot.getChildAt(i);
			
			MP3File oldFile = (MP3File) originalChild.getUserObject();
			MP3File newFile = (MP3File) newChild.getUserObject();
			
			assertEquals(oldFile.album, newFile.album);
			assertEquals(oldFile.artist, newFile.artist);
			assertEquals(oldFile.path, newFile.path);
			assertEquals(oldFile.song, newFile.song);
			assertEquals(oldFile.year, newFile.year);
						
		}
	}

	
}
