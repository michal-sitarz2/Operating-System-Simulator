package processScheduler;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import processScheduler.QueueType;
import processScheduler.ReadyQueueComparator;
import processScheduler.Process;


public class FileIn extends Thread{

	private String file = null;
	private Queue<Process> data = null;
	private int numberOfProcesses = 0;
	private boolean ioBlock = false;
	private QueueType algorithm = null;
	private List<Process> newlyAdded = null;
	
	public FileIn(String file, QueueType algorithm) throws IllegalArgumentException {
		this.newlyAdded = new ArrayList<Process>();
		this.algorithm = algorithm;
		if(!file.endsWith(".txt")) {
			throw new IllegalArgumentException("The file must be a .txt file");
		}
		this.file = file;
		
		this.data = new PriorityQueue<Process>(50, new ReadyQueueComparator(QueueType.background));
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null) {
				if(!line.contains("#")) {
					String[] s = line.split(" ");
					if(s.length==4) {
						int pid = Integer.parseInt(s[0]);
						double arrivalTime = Double.parseDouble(s[1]);
						double cpuBurst = Double.parseDouble(s[2]);
						int priority = Integer.parseInt(s[3]);
			
						this.data.add(new Process(pid, arrivalTime, cpuBurst, priority));
					} else if(s.length == 1) {
						this.numberOfProcesses = Integer.parseInt(s[0]);
					} else if(s.length == 6 && algorithm.name().contentEquals("RR")) {
						int pid = Integer.parseInt(s[0]);
						double arrivalTime = Double.parseDouble(s[1]);
						double cpuBurst = Double.parseDouble(s[2]);
						int priority = Integer.parseInt(s[3]);
						
						Process process = new Process(pid, arrivalTime, cpuBurst, priority);
						this.data.add(process);
					} else if(s.length == 6) {
						int pid = Integer.parseInt(s[0]);
						double arrivalTime = Double.parseDouble(s[1]);
						double cpuBurst = Double.parseDouble(s[2]);
						int priority = Integer.parseInt(s[3]);
						double blockStart = Double.parseDouble(s[4]);
						double blockDuration = Double.parseDouble(s[5]);
						
						this.ioBlock = true;
						Process process = new Process(pid, arrivalTime, cpuBurst, priority, blockStart, blockDuration);
						this.data.add(process);
					} else {
							throw new IllegalArgumentException("The processes were identified incorrectly");
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
		
	@Override
	public void run() {
		BufferedReader reader = null;
		while(true) {
		try {
			
			Thread.currentThread().sleep(100);
			
			reader = new BufferedReader(new FileReader(file));
			String line;
			
			while((line = reader.readLine()) != null) {
				
					if(!line.contains("#") && !line.startsWith(" ")) {
						String[] s = line.split(" ");
						
						if(s.length == 1) {
							setNumberOfProcesses(Integer.parseInt(s[0]));
						} else if(s.length == 4 || s.length == 6) {
							
						}
						else if(s.length == 3) {
								int pid = Integer.parseInt(s[0]);
								double cpuBurst = Double.parseDouble(s[1]);
								int priority = Integer.parseInt(s[2]);
									
								Process process = new Process(pid, 0, cpuBurst, priority);
								boolean check = true;
								for(Process p : this.data) {
									if(p.getPID() == process.getPID()) {
										check = false;
										break;
									}
								}
								if(check) {
									this.data.add(process);
									addtoNewlyAdded(process);
								}
								
								
						} else if(s.length == 5 && algorithm.name().contentEquals("RR")) {
								throw new IllegalArgumentException("The program does not support IO Blocking for RR");
						} else if(s.length == 5) {
								int pid = Integer.parseInt(s[0]);
								double cpuBurst = Double.parseDouble(s[1]);
								int priority = Integer.parseInt(s[2]);
								double blockStart = Double.parseDouble(s[3]);
								double blockDuration = Double.parseDouble(s[4]);
								
								Process process = new Process(pid, 0, cpuBurst, priority, blockStart, blockDuration);
								this.ioBlock = true;
								boolean check = true;
								for(Process p : this.data) {
									if(p.getPID() == process.getPID()) {
										check = false;
										break;
									}
								}
								if(check) {
									this.data.add(process);
									addtoNewlyAdded(process);
								}
						} else {
								throw new IllegalArgumentException("The processes were identified incorrectly");
						}
					}
				}
			
		} catch(IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
		
	}
	
	
	private synchronized void addtoNewlyAdded(Process process) {
		this.newlyAdded.add(process);
	}
	
	public synchronized List<Process> getNewlyAdded(){
		return this.newlyAdded;
	}
	
	public synchronized void removeFromNewlyAdded() {
		this.newlyAdded.remove(0);
	}
	
	public synchronized int getNumberOfProcesses() {
		return this.numberOfProcesses;
	}
	
	public synchronized void setNumberOfProcesses(int number) {
		this.numberOfProcesses = number;
	}
	
	public Queue<Process> getProcessesFromFile() {
		return this.data;
	}
	
	public boolean getIOBlock() {
		return this.ioBlock;
	}
	
}
