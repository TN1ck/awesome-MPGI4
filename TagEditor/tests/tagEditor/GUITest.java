package tagEditor;

import static org.junit.Assert.assertEquals;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.junit.Before;
import org.junit.Test;

import resources.TestImages;

public class GUITest{
	
	private View gui;
	private Control control;
	private MP3File mp3;
	private DefaultMutableTreeNode mp3File;
	private TreePath path;
	
	@Before
	public void setUp() {
		control = new Control();
		mp3 = new MP3File("Nirvana",  "Dumb","In Utero", "1993", TestImages.png);
		mp3.setPath("./Nirvana - Dumb");
		mp3File = new DefaultMutableTreeNode(mp3);
		gui = new View(control.getTree(mp3File));
		gui.getTree().setExpandsSelectedPaths(true);
		path = gui.getTree().getPathForRow(0);
		
	}

	
	@Test
	public void shouldChangeInfoArea(){
		gui.setVisibilityOfInfoArea(true);
		gui.getSong().setText(mp3.getSong());
		gui.getArtist().setText(mp3.getArtist());
		gui.getAlbum().setText(mp3.getAlbum());
		gui.getYear().setText(mp3.getYear());
		gui.getTree().setSelectionPath(path);
		//01_What_i_hope.mp3 should be selected
		assertEquals("Nirvana", gui.getArtist().getText());
		assertEquals("1993", gui.getYear().getText());
		assertEquals("In Utero", gui.getAlbum().getText());
		assertEquals("Dumb", gui.getSong().getText());
	}
	
	
	
	
}
