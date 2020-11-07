package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Queue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import memory.AllocationPolicy;
import memory.FileOut;
import memory.ManagementType;
import processScheduler.Event;
import processScheduler.EventType;
import processScheduler.Process;
import memory.MemoryManagementUnit;

public class MMU_Frame implements ActionListener{

	private JFrame frame = new JFrame("Memory Management Unit Simulator");
	private JTextArea processArea = new JTextArea();
	private JLabel mainMemoryLabel = new JLabel();
	private JLabel processListLabel = new JLabel();
	private JButton continueButton = new JButton();
	private JButton backButton = new JButton();
	private JLabel finishedLabel = new JLabel();
	
	private MemoryManagementUnit mmu = null;
	private Thread threadCheck = null;
	private ManagementType type = null;
	private Path directory = null;
	
	public MMU_Frame(File file, int osSize, int size, ManagementType type, AllocationPolicy policy, Path directory) {
		this.prepareGUI();
		this.mmu = new MemoryManagementUnit(file.toString(), osSize, size, type);
		this.type = type;
		this.directory = directory;
		this.initializeComponents();
		
		mmu.runAlgorithm();
		mmu.initializeInitialFragments(frame);	
		mmu.initializeMainMemory(frame);
		
		this.initializeThread();
		threadCheck.start();
	}
	
	private void prepareGUI() {
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setBackground(Color.white);
		frame.setVisible(true);
		frame.setBounds(200, 50, 1000, 950);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void finishSimulation() {
		this.continueButton.enable();
		this.continueButton.addActionListener(this);
	
		this.finishedLabel.setBounds(150, 800, 200, 30);
		this.finishedLabel.setForeground(Color.red);
		this.finishedLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
		this.finishedLabel.setText("FINISHED");
		this.frame.add(finishedLabel);
		this.frame.repaint();
		
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("# PID, Arrival Time, Size in Memory, Duration, Time of the Event, Event Type\n");
		for(Event event : this.mmu.getEvents()) {
			buffer.append(event.toString() + "\n");
		}
		
		try {
			if(this.type.name().contentEquals("FIXED_SIZE")) {
				FileOut fileOut = new FileOut(buffer.toString(), this.directory, "FIXED");
			} else {
				FileOut fileOut = new FileOut(buffer.toString(), this.directory, "VARIABLE");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initializeThread() {
		  threadCheck = new Thread() {
	        	public void run() {
	        		try {
		        		while(!mmu.getFinished()) {
		        			try {
								Thread.currentThread().sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
		        			// Wait till the process is finished
		        		}
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	        		finishSimulation();
        			
	        	}
	        };
	}
	
	private void initializeComponents() {
		processArea.setBounds(50, 90, 300, 450);
		processArea.setBorder(BorderFactory.createLineBorder(Color.black));
		processArea.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		processArea.setEditable(false);
		frame.add(processArea);
		
		processListLabel.setBounds(100, 50, 300, 30);
		processListLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
		processListLabel.setText("List of Initial Processes");
		frame.add(processListLabel);
		
		continueButton.setBounds(100, 600, 150, 50);
		continueButton.setText("Continue");
		frame.add(continueButton);
		continueButton.disable();
		
		backButton.setBounds(100, 700, 150, 50);
		backButton.setText("Back");
		frame.add(backButton);
		backButton.addActionListener(this);
		
		mainMemoryLabel.setBounds(550, 50, 150, 30);
		mainMemoryLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
		mainMemoryLabel.setText("Main Memory");
		frame.add(mainMemoryLabel);
		
		for(int i = 0; i < 32; i++) {
			JLabel label = new JLabel();
			label.setBounds(810, 80 + (i*25), 50, 20);
			label.setText(Integer.toString(i));
			label.setFont(new Font("Times New Roman", Font.BOLD, 16));
			frame.add(label);
		}
		frame.repaint();
		
		Queue<Process> backgroundList = this.mmu.getBackground();
	
		while(backgroundList.peek()!=null) {
			Process process = backgroundList.poll();
			processArea.append(process.toString() + "\n");
			if(backgroundList.peek()==null) {
				this.mmu.setTimer(process.getArrivalTime());
			}
			
			this.mmu.addEvent(process, process.getArrivalTime(), EventType.ARRIVED);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == backButton) {
			this.frame.dispose();
			Welcome_Window ww = new Welcome_Window();
		} else if(e.getSource() == continueButton) {
			this.frame.dispose();
			Welcome_Window ww = new Welcome_Window();
		}
		
	}
	
}
