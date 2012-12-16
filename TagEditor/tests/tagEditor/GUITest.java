package tagEditor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import javax.swing.JTree;

import org.junit.Test;

public class GUITest{
	
	@Test
	public void TestTree (){
		
		Control myControler = new Control();
		assertNotNull(myControler.getTree());
		System.out.println(myControler.getTree().getComponentCount());
			
		myControler.fillTree("Mp3 Sammlung");
		System.out.println(myControler.getTree().getComponentCount());
		
	}
	
	
	

}
