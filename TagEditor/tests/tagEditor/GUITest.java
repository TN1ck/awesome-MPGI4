package tagEditor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import javax.swing.JTree;

import org.junit.Test;

public class GUITest{
	

	@Test
	public void TestControl (){
		
		Control control = new Control();
		
		control.fillTree("Mp3 Sammlung");
		System.out.println(control.getRawTreeNode().getChildCount());
	
	}
}
