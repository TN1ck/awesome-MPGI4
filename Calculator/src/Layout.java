import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;




public class Layout extends JFrame implements ActionListener{
	
	private JButton clear, zero, one, 
	two, three, four, five, six, 
	seven, eight, nine, point, 
	divide, multiply, minus, add, solve;
	
	private JTextField textField; 
	
	public Layout(){
		// Initalize UI elements
		this.clear = new JButton("Clear");
		clear.addActionListener(this);
		this.zero = new JButton("0");
		zero.addActionListener(this);
		this.one = new JButton("1");
		one.addActionListener(this);
		this.two = new JButton("2");
		two.addActionListener(this);
		this.three = new JButton("3");
		three.addActionListener(this);
		this.four = new JButton("4");
		four.addActionListener(this);
		this.five = new JButton("5");
		five.addActionListener(this);
		this.six = new JButton("6");
		six.addActionListener(this);
		this.seven = new JButton("7");
		seven.addActionListener(this);
		this.eight = new JButton("8");
		eight.addActionListener(this);
		this.nine = new JButton("9");
		nine.addActionListener(this);
		this.point = new JButton(".");
		point.addActionListener(this);
		this.divide = new JButton("/");
		divide.addActionListener(this);
		this.multiply = new JButton("*");
		multiply.addActionListener(this);
		this.minus = new JButton("-");
		minus.addActionListener(this);
		this.add = new JButton("+");
		add.addActionListener(this);
		this.solve = new JButton("=");
		solve.addActionListener(this);
		this.textField = new JTextField("0");
		textField.addActionListener(this);
		textField.setPreferredSize(new Dimension(200,20));
		
		// Setup Window
		this.setTitle("Calculator");
		this.setSize(400,300);
		
		setLayout();
		
	}
	
	
	
	private void setLayout() {
		
		Container buttonArea = new Container();
		buttonArea.setLayout(new GridLayout(4,4,5,5));
		
		buttonArea.add(seven);
		buttonArea.add(eight);
		buttonArea.add(nine);
		buttonArea.add(divide);
		buttonArea.add(four);
		buttonArea.add(five);
		buttonArea.add(six);
		buttonArea.add(multiply);
		buttonArea.add(one);
		buttonArea.add(two);
		buttonArea.add(three);
		buttonArea.add(minus);
		buttonArea.add(zero);
		buttonArea.add(point);
		buttonArea.add(solve);
		buttonArea.add(add);
		
		
		Container inputArea = new Container();
		inputArea.setLayout(new BorderLayout());
		
		inputArea.add(textField, BorderLayout.CENTER);
		inputArea.add(clear, BorderLayout.EAST);
		
		this.setLayout(new BorderLayout());
		
		add(inputArea, BorderLayout.NORTH);
		add(buttonArea, BorderLayout.CENTER);
		
		
		
			}
	public static void main(String[] args) {
		
		// Create a new Window
		Layout demo = new Layout();

		// Show window
		demo.setVisible(true);

		// Exit program, if window is closed
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private int StringToInt(String button){
		if (button.equals("0")){
			return 0;
		}
		else if (button.equals("1")){
			return 1;
		}
		else if (button.equals("2")){
			return 2;
		}
		else if (button.equals("3")){
			return 3;
		}
		else if (button.equals("4")){
			return 4;
		}
		else if (button.equals("5")){
			return 5;
		}
		else if (button.equals("6")){
			return 6;
		}
		else if (button.equals("7")){
			return 7;
		}
		else if (button.equals("8")){
			return 8;
		}
		else if (button.equals("9")){
			return 9;
		}
		else if (button.equals("Clear")){
			return 10;
		}
		else if (button.equals(".")){
			return 11;
		}
		
		else if (button.equals("/")){
			return 12;
		}
		else if (button.equals("+")){
			return 13;
		}
		else if (button.equals("-")){
			return 14;
		}
		else if (button.equals("*")){
			return 15;
		}
		
		
		else {
			return -1;
		}
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		switch (StringToInt(e.getActionCommand())){
		case 0:
			this.textField.setText(this.textField.getText()+"0");
			break;
		case 1:
			this.textField.setText(this.textField.getText()+"1");
			break;
		case 2:
			this.textField.setText(this.textField.getText()+"2");
			break;
		case 3:
			this.textField.setText(this.textField.getText()+"3");
			break;
		case 4:
			this.textField.setText(this.textField.getText()+"4");
			break;
		case 5:
			this.textField.setText(this.textField.getText()+"5");
			break;
		case 6:
			this.textField.setText(this.textField.getText()+"6");
			break;
		case 7:
			this.textField.setText(this.textField.getText()+"7");
			break;
		case 8:
			this.textField.setText(this.textField.getText()+"8");
			break;
		case 9:
			this.textField.setText(this.textField.getText()+"9");
			break;
		case 10:
			this.textField.setText("");
			break;
		case 11:
			this.textField.setText(this.textField.getText()+".");
			break;
		case 12:
			this.textField.setText(this.textField.getText()+"/");
			break;
		case 13:
			this.textField.setText(this.textField.getText()+"+");
			break;
		case 14:
			this.textField.setText(this.textField.getText()+"-");
			break;
		case 15:
			this.textField.setText(this.textField.getText()+"*");
			break;

		
		}
	}
	
	
	

}



