package gui;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Booting_Frame {

	JFrame frame = new JFrame("Process Scheduler Simulator");
	JTextArea textArea = new JTextArea();
	
	public Booting_Frame(){
		this.prepareGUI();
		this.outputBootingInfo();
	}
	
	public JFrame getFrame() {
		return this.frame;
	}
	
	private void prepareGUI() {
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setBackground(Color.white);
		frame.setVisible(true);
		frame.setBounds(200,200,400,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
	}
	
	public void outputBootingInfo(){
		textArea.setBounds(60,120,100,100);
		textArea.setEditable(false);
		frame.add(textArea);
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					textArea.append("System is Booting");
					BufferedReader reader = new BufferedReader(new FileReader("boot.txt"));
					String line;
					while((line = reader.readLine()) != null) {
						textArea.append(line + "\n");
						Thread.currentThread().sleep(900);
					}
					reader.close();
				} catch(Exception e) {
					System.out.println(e);
				}
			}
		};
		
		thread.start();
		
		
	}
}
