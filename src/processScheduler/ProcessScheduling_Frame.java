package processScheduler;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import gui.Welcome_Window;
import processScheduler.Event;
import processScheduler.EventType;
import processScheduler.Process;
import processScheduler.QueueType;
import processScheduler.ReadyQueueComparator;
import processScheduler.FileIn;

public class ProcessScheduling_Frame implements ActionListener {

	private JFrame frame = new JFrame();
	private JLabel readyQLabel = new JLabel();
	private JTextArea readyQArea = new JTextArea();
	private JLabel schedulerLabel = new JLabel();
	private JTextArea currentlyScheduled = new JTextArea();
	private JLabel finishedLabel = new JLabel();
	private JTextArea finishedArea = new JTextArea();
	private JLabel finishConfirmedLabel = new JLabel();
	private JButton continueButton = new JButton();
	private JButton exitButton = new JButton();
	private JLabel blockLabel = new JLabel();
	private JTextArea ioBlockArea = new JTextArea();
	
	private int totalProcessNumber = 0;
	private Thread threadReady = null;
	private Thread threadRead = null;
	private Thread threadCPU = null;
	private Thread threadIO = null;
	private Thread threadFinal = null;
	
	private File selectedFile = null;
	private Path selectedDirectory = null;
	private int contextSwitching = 0;
	private int timeQuantum = 0;
	private QueueType algorithm = null;
	
	private List<Process> ioBlockQueue = null;
	private List<Process> finishedProcesses = null;
	private Process currentProcess = null;
	private boolean first = true;
	private boolean firstR = true;
	private List<Process> readyQ = null;
	private int finishedCounter = 0;
	private LinkedList<Process> backgroundL = null;
	private Queue<Process> background = null;
	private double time = 0;
	private double timer = 0;
	private List<Event> events = null;
	
	
	public ProcessScheduling_Frame(File selectedFile, Path selectedDirectory, int contextSwitching, int timeQuantum, QueueType algorithm) throws NullPointerException {   
		this.readyQ = new ArrayList<Process>();
		this.finishedProcesses = new ArrayList<Process>();
		this.ioBlockQueue = new ArrayList<Process>();
		
		if(selectedFile==null) {
			throw new NullPointerException("There has to be a file.");
		}
		this.selectedFile = selectedFile;
		this.selectedDirectory = selectedDirectory;
		this.contextSwitching = contextSwitching;
		this.timeQuantum = timeQuantum;
		this.algorithm = algorithm;
		
		this.events = new ArrayList<Event>();		
		
		this.prepareGUI();
		this.JInitialize();
		
		this.runReadyQueue(this.algorithm);
	}
	
	@SuppressWarnings("deprecation")
	private void JInitialize() {
		readyQLabel.setBounds(100, 10, 200, 30);
		frame.add(readyQLabel);
		readyQLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
		readyQLabel.setText("Ready Queue");
		
		readyQArea.setBounds(100, 50, 350, 460);
		frame.add(readyQArea);
		readyQArea.setBorder(BorderFactory.createLineBorder(Color.black));
		readyQArea.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		readyQArea.setText("");
		readyQArea.setEditable(false);
		
		schedulerLabel.setBounds(100, 530, 200, 30);
		frame.add(schedulerLabel);
		schedulerLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
		schedulerLabel.setText("Currently Executing");
		
		currentlyScheduled.setBounds(100, 570, 350, 150);
		frame.add(currentlyScheduled);
		currentlyScheduled.setBorder(BorderFactory.createLineBorder(Color.black));
		currentlyScheduled.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		currentlyScheduled.setEditable(false);
		
		blockLabel.setBounds(580, 530, 200, 30);
		frame.add(blockLabel);
		blockLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
		blockLabel.setText("I/O Blocked");
		
		ioBlockArea.setBounds(580, 570, 350, 150);
		frame.add(ioBlockArea);
		ioBlockArea.setBorder(BorderFactory.createLineBorder(Color.black));
		ioBlockArea.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		ioBlockArea.setEditable(false);
		
		finishedLabel.setBounds(580, 10, 200, 30);
		frame.add(finishedLabel);
		finishedLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
		finishedLabel.setText("Finished Processes");
		
		finishedArea.setBounds(580, 50, 350, 460);
		frame.add(finishedArea);
		finishedArea.setBorder(BorderFactory.createLineBorder(Color.black));
		finishedArea.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		finishedArea.setText("");
		finishedArea.setEditable(false);
		
		finishConfirmedLabel.setBounds(465, 200, 200, 30);
		frame.add(finishConfirmedLabel);
		finishConfirmedLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
		finishConfirmedLabel.setText("Processing...");
		
		continueButton.setBounds(300, 800, 150, 50);
		frame.add(continueButton);
		continueButton.setText("Continue");
		continueButton.disable();
		
		exitButton.setBounds(510, 800, 150, 50);
		frame.add(exitButton);
		exitButton.setText("Exit");
		exitButton.addActionListener(this);
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == exitButton) {
			frame.dispose();
			Welcome_Window ww = new Welcome_Window();
		} else if(e.getSource() == continueButton) {
			frame.dispose();
			Statistics_Window sw = new Statistics_Window(selectedFile, selectedDirectory, contextSwitching, timeQuantum, algorithm, events);
		}
	}
	
	private void activateButton() {
		continueButton.enable();
		continueButton.addActionListener(this);
	}
	
	
	private void prepareGUI() {
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setBackground(Color.white);
		frame.setVisible(true);
		frame.setBounds(300,50,1000,950);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
	}
	

	private void processCalculations() {
		for(Event e : this.events) {
			if(e.type.name().contentEquals("TERMINATED")) {
				e.process.calculateTimeAround();
				e.process.calculateWaitedAround();
			}
		}
	}
	
	private synchronized void addToBackground(Process process) {
		this.background.add(process);
	}
	
	private synchronized Queue<Process> getBackground(){
		return this.background;
	}
	
	private synchronized void addToBackgroundL(Process process) {
		this.backgroundL.add(process);
	}
	
	private synchronized LinkedList<Process> getBackgroundL(){
		return this.backgroundL;
	}
	
	
	
	private void addEvent(Process p, double currentTime, EventType event) {
		this.events.add(new Event(p, currentTime, event));
	}
	
	private synchronized void appendReady(Process process) {
		readyQArea.append(process.toString() + " " + process.getCurrentEvent() + "\n");
	}
	
	private synchronized void removeReady() {
		readyQArea.setText("");
		for(Process p : readyQ) {
			readyQArea.append(p.toString() + " " + p.getCurrentEvent() + "\n");
		}
	}
	
	private void runReadyQueue(QueueType qt) {
		FileIn fileInput = new FileIn(selectedFile.toString(), qt);
	
		this.backgroundL = new LinkedList<Process>();
		this.background = new PriorityQueue<Process>(50, new ReadyQueueComparator(qt));
		
		
		
		threadReady = new Thread() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				while(true) {
					try {
						Thread.currentThread().sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if(!getBackground().isEmpty()) {
						while(getCurrentProcess()==null) {
							if(first) {
								first = false;
							} else {
								try {
									Thread.currentThread().sleep(contextSwitching * 500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							Process runningProcess = getBackground().poll();
							getReadyQueue().remove(runningProcess);
							removeReady();
							addEvent(runningProcess, getCurrentTime(), EventType.SCHEDULED);

							setCurrentProcess(runningProcess);
						}
					} else if(!getBackgroundL().isEmpty()) {
						while(getCurrentProcess()==null) {
							if(first) {
								first = false;
							} else {
								try {
									Thread.currentThread().sleep(contextSwitching * 500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							Process runningProcess = getBackgroundL().poll();
							getReadyQueue().remove(runningProcess);
							removeReady();
							addEvent(runningProcess, getCurrentTime(), EventType.SCHEDULED);
				
							setCurrentProcess(runningProcess);
						}
					}
					
					
				}
			}
		};
		
		
		
		threadRead = new Thread() {
			public void run() {
				
				Queue<Process> list = null;
				if(!algorithm.name().contentEquals("RR")) {
					list = new PriorityQueue<Process>(50, new ReadyQueueComparator(QueueType.background));
					list = fileInput.getProcessesFromFile();
				
				} else {
					list = new LinkedList<Process>();
					list = fileInput.getProcessesFromFile();
				}
				if(list.peek() == null) {
					throw new NullPointerException("The input file is empty or wrongly formatted");
				}
				setCurrentTime(list.peek().getArrivalTime());
				
				try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				if(fileInput.getIOBlock()) {
					threadIO.start();
				}
				
				while(!list.isEmpty()) {
					
					Process process = list.poll();
					addEvent(process, process.getArrivalTime(), EventType.ARRIVED);
					process.setCurrentEvent(EventType.ARRIVED);
					appendReady(process);
					if(algorithm.name().contentEquals("RR")) {
						addToBackgroundL(process);
					} else {
						addToBackground(process);
					}
					
					addReadyQueue(process);
						try {
							if(first && timeQuantum<10) {
								Thread.currentThread().sleep(1000);
							}
							if(list.peek()!=null) {
								Thread.currentThread().sleep((long) (list.peek().getArrivalTime() - process.getArrivalTime()) * 200);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
				
				totalProcessNumber = fileInput.getNumberOfProcesses();
				
				threadFinal.start();
				
				fileInput.start();
				
				while(true) {
					try {
						Thread.currentThread().sleep(100);
						if(!fileInput.getNewlyAdded().isEmpty()) {
							Process p = fileInput.getNewlyAdded().get(0);
							if(!getBackground().contains(p)) {
								p.setArrivalTime(getTimer());
								addEvent(p, p.getArrivalTime(), EventType.ARRIVED);
								p.setCurrentEvent(EventType.ARRIVED);
								appendReady(p);
								if(qt.name().contentEquals("RR")) {
									addToBackgroundL(p);
								} else if(qt.name().contentEquals("FCFS") && fileInput.getIOBlock()){
									threadIO.start();
									addToBackgroundL(p);
								} else {
									addToBackground(p);
								}
								addReadyQueue(p);
								totalProcessNumber++;
								fileInput.removeFromNewlyAdded();
								
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		threadCPU = new Thread() {
			public void run() {
				while(true) {
					try {
						Thread.currentThread().sleep(100);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					if(getCurrentProcess()!=null) {
						
						Process currentProcess = getCurrentProcess();
						if(!algorithm.name().contentEquals("RR")) {
							currentlyScheduled.setText(currentProcess.toString() + " " + EventType.SCHEDULED);
							try {
								if(currentProcess.getBlockDuration()==0) {
									for(int i = 0; i<currentProcess.getCPUBurst(); i++) {
										Thread.currentThread().sleep(100);
										incTimer();
									}
									addCurrentTime(currentProcess.getCPUBurst());
									terminateProcess(currentProcess, getCurrentTime());
								} else {
									if(currentProcess.getCurrentTime()==0) {
										double duration = currentProcess.getBlockStart();
										for(int i = 0; i<duration; i++) {
											Thread.currentThread().sleep(100);
											incTimer();
										}
										addCurrentTime(duration);
										currentProcess.setCurrentTime(currentProcess.getBlockStart() + currentProcess.getBlockDuration());
										blockProcess(currentProcess, getCurrentTime());
									} else {
										double duration = currentProcess.getCPUBurst() - currentProcess.getCurrentTime();
										for(int i = 0; i<duration; i++) {
											Thread.currentThread().sleep(100);
											incTimer();
										}
										addCurrentTime(duration);
										terminateProcess(currentProcess, getCurrentTime());
									}
									
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
						} else {
							double sleep = 0;
							if(timeQuantum + currentProcess.getCurrentTime() <= currentProcess.getCPUBurst()) {
								sleep = timeQuantum;
								currentProcess.addCurrentTime(timeQuantum);
								
							} else {
								sleep = currentProcess.getCPUBurst() - currentProcess.getCurrentTime();
								currentProcess.setCurrentTime(currentProcess.getCPUBurst());
							}
							currentlyScheduled.setText(currentProcess.toString() + " " + EventType.SCHEDULED);
							
							try {
								
								if(firstR && timeQuantum <= 20) {
									Thread.currentThread().sleep(1500);
									firstR = false;
								} 
								
								for(int i = 0; i<sleep; i++) {
									Thread.currentThread().sleep(100);
									incTimer();
								}
								
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							addCurrentTime(sleep);
							
							if(currentProcess.getCurrentTime() == currentProcess.getCPUBurst()) {
								terminateProcess(currentProcess, getCurrentTime());
							} else if(getBackgroundL().isEmpty()) {
								continue;
							}
							else {
								preEmptyProcess(currentProcess, getCurrentTime());
							}
							
						} 
						
						setCurrentProcess(null);
					}
				}
			}
		};
		
		
		threadIO = new Thread() {
			public void run() {
				while(true) {
					try {
						Thread.currentThread().sleep(100);
						if(!ioBlockQueue.isEmpty()) {
							Process blockedProcess = ioBlockQueue.remove(0);
							ioBlockArea.setText(blockedProcess.toString() + " " + EventType.IO_BLOCKED.name());
							Thread.currentThread().sleep((long) (blockedProcess.getBlockDuration() * 100));
							
							unBlockProcess(blockedProcess, blockedProcess.getTimeBlocked() + blockedProcess.getBlockDuration());
							
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					
				}
			}
		};
		
		
		threadFinal = new Thread() {
			public void run() {		
				while(finishedCounter!=totalProcessNumber) {
					try {
						Thread.currentThread().sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// Do nothing, wait for the processes to finish
				}
				
				//Stopping all the threads
				threadReady.stop();
				threadRead.stop();
				threadCPU.stop();
				threadIO.stop();
				fileInput.stop();
				
				processCalculations();
				
				finishConfirmedLabel.setText("Finished");
				finishConfirmedLabel.setForeground(Color.red);
				activateButton();
			}
		};
		
		threadReady.start();
		threadRead.start();
		threadCPU.start();
	}
	
	private void blockProcess(Process process, double time) {
		process.setCurrentEvent(EventType.IO_BLOCKED);
		process.setTimeBlocked(time);
		addEvent(process, time, EventType.IO_BLOCKED);
		
		currentlyScheduled.setText("");
		ioBlockQueue.add(process);
		
		if(!getBackgroundL().isEmpty() || !getBackground().isEmpty()) {
			addCurrentTime(contextSwitching);
		}
	}
	
	private void unBlockProcess(Process process, double time) {
		
		if(background.isEmpty() && backgroundL.isEmpty() && currentProcess==null) {	
			
			process.setCurrentEvent(EventType.SCHEDULED);
			addEvent(process, time, EventType.SCHEDULED);
			
			setCurrentTime(time);
			ioBlockQueue.remove(process);
			ioBlockArea.setText("");
			
			setCurrentProcess(process);
		} else {
			process.setCurrentEvent(EventType.PREEMPTED);
			addEvent(process, time, EventType.PREEMPTED);
			
			ioBlockQueue.remove(process);
			ioBlockArea.setText("");
			
			if(this.algorithm.name().contentEquals("SJF") || this.algorithm.name().contentEquals("PS")) {
				this.background.add(process);
			} else {
				this.backgroundL.addLast(process);
			}
			addReadyQueue(process);
			appendReady(process);
		}
	}
	
	private void terminateProcess(Process process, double time) {
		process.setCurrentEvent(null);
		process.setFinishTime(time);
		addEvent(process, time, EventType.TERMINATED);
	
		currentlyScheduled.setText("");
		finishedArea.append(process + " " + EventType.TERMINATED.name() + " at " + process.getFinishTime() + "\n");
		
		if(!getBackground().isEmpty() || !getBackgroundL().isEmpty()) {
			addCurrentTime(contextSwitching);
		}
		
		setTimer(getCurrentTime());
		
		finishedCounter++;
		this.finishedProcesses.add(process);	
	}
	
	private void preEmptyProcess(Process process, double time) {
		process.setCurrentEvent(EventType.PREEMPTED);
		addEvent(process, time, EventType.PREEMPTED);
		
		this.backgroundL.addLast(process);
		currentlyScheduled.setText("");
		addReadyQueue(process);
		appendReady(process);
		if(!getBackgroundL().isEmpty()) {
			addCurrentTime(contextSwitching);
		}
		
		setTimer(getCurrentTime());
		
	}
	
	private void incTimer() {
		this.timer += 1;
	}
	
	private void setTimer(double time) {
		this.timer = time;
	}
	
	private double getTimer() {
		return this.timer;
	}
	
	private synchronized void addReadyQueue(Process p) {
		this.readyQ.add(p);
	}
	
	private synchronized List<Process> getReadyQueue(){
		return this.readyQ;
	}
	
	private Process getCurrentProcess() {
		return this.currentProcess;
	}
	
	private void setCurrentProcess(Process currentProcess) {
		this.currentProcess = currentProcess;
	}
	
	private double getCurrentTime() {
		return this.time;
	}
	
	private void addCurrentTime(double time) {
		this.time += time;
	}
	
	private void setCurrentTime(double time) {
		this.time = time;
	}
	
}


