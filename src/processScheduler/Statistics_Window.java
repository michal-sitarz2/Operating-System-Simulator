package processScheduler;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import gui.Welcome_Window;
import processScheduler.Cell;
import processScheduler.CellTime;
import processScheduler.Event;
import processScheduler.FileOut;
import processScheduler.GanttChart;
import processScheduler.QueueType;

public class Statistics_Window implements ActionListener{

	private JFrame frame = new JFrame("Statistics");
	private JTextArea detailsArea = new JTextArea();
	private JButton returnButton = new JButton();
	private JLabel chartLabel = new JLabel();
	private JLabel outputLabel = new JLabel();
	
	private File selectedFile = null;
	private Path selectedDirectory = null;
	private int contextSwitching = 0;
	private int timeQuantum = 0;
	private QueueType algorithm = null;
	
	private double averageAroundTime = 0;
	private double averageWaitedTime = 0;
	private List<Event> events = null;
	private GanttChart gantt = null;
	
	public Statistics_Window(File selectedFile, Path selectedDirectory, int contextSwitching, int timeQuantum, QueueType algorithm, List<Event> events) {
		this.selectedFile = selectedFile;
		this.selectedDirectory = selectedDirectory;
		this.contextSwitching = contextSwitching;
		this.timeQuantum = timeQuantum;
		this.algorithm = algorithm;
		
		this.events = events;
		
		this.gantt = new GanttChart(algorithm, timeQuantum);
		this.generateFile1Output();
		
		this.initializeGUI();
		this.initializeChart();
		this.initializeOutputs();
		this.detailsArea.setText(this.generateFile1Output().toString());
		
		try {
			this.fileSave();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private StringBuffer generateFile1Output() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Algorithm Type:\t" + this.algorithm + "\n");
		buffer.append("Context Switching:\t" + this.contextSwitching + "\n");
		buffer.append("Time Quantum:\t" + this.timeQuantum + "\n");
		buffer.append("From file:\t" + this.selectedFile.toString() + "\n");
		buffer.append("Destination: " + this.selectedDirectory  + "\n");
		buffer.append("\nCalculations:");
		
		this.calculateAverages();
		buffer.append("\nAverage Turnaround Time: " + this.averageAroundTime);
		buffer.append("\nAverage Waited Time: " + this.averageWaitedTime);
		buffer.append("\nTotal Time: " + this.getTotalTime());
		
		return buffer;
	}
	
	
	private void fileSave() throws IOException {
		StringBuffer buffer1 = this.generateFile1Output();
		buffer1.append("\n\n# PID, Arrival Time, Finished Time, CPU Bursts, Priority, Turnarond time, Waited Time\n");
		StringBuffer buffer2 = new StringBuffer();
		buffer2.append("# PID, Arrival Time, CPU Bursts, Priority, Time of the Event\n\n");
		
		Collections.sort(this.events, new Comparator<Event>() {
		    @Override
		    public int compare(Event e1, Event e2) {
		        return Double.compare(e1.time, e2.time);
		    }
		});
		
		for(Event e : this.events) {
			if(e.type.name().contentEquals("TERMINATED")) {
				buffer1.append(e.process.printFinal() + "\n");
			}
			buffer2.append(e.process.toString() + " " + e.time + " " + e.type.name() + "\n");
		}
		
		FileOut fileOut = new FileOut(buffer1.toString(), buffer2.toString(), this.selectedDirectory, this.algorithm.toString());
		
	}
	
	private void calculateAverages() {
		double sumTimeAround = 0;
		double sumWaitedAround = 0;
		int counter = 0;
		
		for(Event e : this.events) {
			if(e.type.name().contentEquals("TERMINATED")) {
				sumTimeAround += e.process.getTimeAround();
				sumWaitedAround += e.process.getWaitedAround();
				counter++;
			}
		}
		
		this.averageAroundTime = (double) Math.round((sumTimeAround / counter)* 1000) / 1000;
		this.averageWaitedTime = (double) Math.round((sumWaitedAround / counter)* 1000) / 1000;
		
		
	}
	
	private double getTotalTime() {
		Event event = this.events.get(events.size()-1);
		return event.process.getFinishTime();
	}
	
	private void initializeOutputs() {
		detailsArea.setBounds(50, 450, 700, 250);
		detailsArea.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		detailsArea.setBorder(BorderFactory.createLineBorder(Color.black));
		frame.add(detailsArea);
		detailsArea.setEditable(false);
		
		outputLabel.setBounds(50, 420, 300, 30);
		frame.add(outputLabel);
		outputLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
		outputLabel.setText("Result of Calculations");
		
		returnButton.setBounds(780, 535, 130, 50);
		frame.add(returnButton);
		returnButton.setText("Return");
		returnButton.addActionListener(this);
		
	}
	
	private void initializeGUI() {
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setBackground(Color.white);
		frame.setVisible(true);
		frame.setBounds(100, 100, 1000, 780);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
	}
	
	private void initializeChart() {
		chartLabel.setBounds(50, 30, 200, 20);
		frame.add(chartLabel);
		chartLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
		chartLabel.setText("Gantt Chart");
		
		List<Event> scheduledEvents = new ArrayList<Event>();
		
		for(Event event : events) {
			if(event.type.name().contentEquals("SCHEDULED") || event.type.name().contentEquals("IO_BLOCKED") || event.type.name().contentEquals("TERMINATED")) {
				scheduledEvents.add(event);
			}
		}
	
		
		for(int i = 0; i < scheduledEvents.size(); i++) {
			Event event = scheduledEvents.get(i);
			if(event.type.name().contentEquals("SCHEDULED")) {
				if(event.process.getBlockDuration()!=0) {
					if(i+1 < scheduledEvents.size()) {
						if(scheduledEvents.get(i+1).type.name().contentEquals("IO_BLOCKED")) {
							gantt.addProcess(event, true, 0);
						} else {
							gantt.addProcess(event, false, 0);
						}
					} else {
						gantt.addProcess(event, false, 0);
					}
				} else {
					if(timeQuantum!=0) {
						if(i+1 < scheduledEvents.size() && scheduledEvents.get(i+1).type.name().contentEquals("TERMINATED") ) {
							gantt.addProcess(event, true, scheduledEvents.get(i+1).time - event.time);
						} else {
							gantt.addProcess(event, true, 0);
						}
					} else {
						gantt.addProcess(event, true, 0);
					}
				}
			}
		}
		
		for(Cell cell : gantt.getCellList()) {
			frame.add(cell);
		}
		
		for(CellTime cell : gantt.getCellTimeList()) {
			frame.add(cell);
		}
	
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == returnButton) {
			frame.dispose();
			Welcome_Window ww = new Welcome_Window();
		}
	}

	
}
