package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Welcome_Window implements ActionListener{

	
	private JFrame frame = new JFrame("Welcome to OS Simulator");
	private JButton button1 = new JButton("PSS");
	private JButton button2 = new JButton("MMU");
	
	public Welcome_Window(){
		this.prepareGUI();
		this.button1Properties();
		this.button2Properties();
	}
	
	private void prepareGUI() {
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setBackground(Color.white);
		frame.setVisible(true);
		frame.setBounds(200,200,400,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	private void button1Properties() {
		button1.setBounds(60,120,100,100);
		frame.add(button1);
		button1.addActionListener(this);
	}
	
	private void button2Properties() {
		button2.setBounds(200,120,100,100);
		frame.add(button2);
		button2.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == button1) {
			frame.dispose();
			PSS_Frame pss = new PSS_Frame();
			
		} else {
			frame.dispose();
			MMU_Options mmu = new MMU_Options(32, 4);
		}
		
	}

}
