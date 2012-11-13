import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.w3c.dom.events.MouseEvent;




public class Viewer extends JFrame implements MouseListener{
	
	private JLabel image;
	private JFileChooser myChooser;
	
	public Viewer(){
		// Initalize UI elements
		myChooser = new JFileChooser();
		image = new JLabel();
		addMouseListener(this);
		// Setup Window
		this.setTitle("Calculator");
		this.setSize(400,300);
		
		setLayout();
		
	}
	
	
	
	private void setLayout() {
		
			this.setLayout(new GridLayout(1,1));
			add(image);		
		
		
			}
	public static void main(String[] args) {
		
		// Create a new Window
		Viewer demo = new Viewer();

		// Show window
		demo.setVisible(true);
		
		

		// Exit program, if window is closed
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}



	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
		
		if (e.getClickCount() %2 == 0) {
			 myChooser.showOpenDialog(this);
			System.out.println(myChooser.getSelectedFile().getName());
			image.setIcon(new ImageIcon(myChooser.getSelectedFile().getPath())); 
			    
		}
			
		
	}



	@Override
	public void mouseEntered(java.awt.event.MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseExited(java.awt.event.MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}



