package memory;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.swing.JFrame;

import processScheduler.Event;
import processScheduler.EventType;
import processScheduler.Process;
import processScheduler.QueueType;
import processScheduler.ReadyQueueComparator;

public class MemoryManagementUnit {

	private Queue<Process> backgroundQueue = null;
	private Queue<Process> backgroundProcesses = null;
	private Process[] mainMemory = null;
	private List<Partition> partitionList = null;
	private Queue<Process> waitingQueue = null;
	
	private ManagementType management = null;
	
	private int osSize = 0;
	private int memorySize = 0;
	private boolean removed = false;
	
	private MainMemory mm = null;
	private boolean finished = false;
	
	private double timer = 0;
	private List<Event> events = null;
	private Thread threadTimer = null;
	private Thread threadFinished = null;
	private Thread threadFixed = null;
	private Thread threadVariable = null;
	private Thread threadDuration = null;
	private Thread threadProcess = null;
	
	public MemoryManagementUnit(String file, int osSize, int memorySize, ManagementType management) throws NullPointerException {
		this.backgroundQueue = new PriorityQueue<Process>(50, new ReadyQueueComparator(QueueType.background));
		this.waitingQueue = new PriorityQueue<Process>(50, new ReadyQueueComparator(QueueType.background));
		this.backgroundProcesses = new PriorityQueue<Process>(50, new ReadyQueueComparator(QueueType.background));
		this.events = new ArrayList<Event>();
		
		this.mainMemory = new Process[memorySize];
		this.partitionList = new ArrayList<Partition>();
		this.management = management;
		
		this.memorySize = memorySize;
		this.osSize = osSize;
		
		if(file == null) {
			throw new NullPointerException("The file cannot be null");
		}
		FileIn fileIn = new FileIn(file);
		
		
		this.backgroundQueue.clear();
		this.backgroundProcesses.clear();
		for(Process p : fileIn.getProcessesFromFile()) {
			this.backgroundQueue.add(p);
			this.backgroundProcesses.add(p);
		}
		
		mm = new MainMemory(this.memorySize);
		
		this.initializeOsInMemory();
		
		if(management.name().contentEquals("FIXED_SIZE")) {
			//partition the memory
			this.partitionMainMemory();
		}
		this.initializeThreads();
		this.startDuration();
	
	}
	
	private void initializeThreads() {
		threadTimer = new Thread() {
			public void run() {
				while(true) {
					try {
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					incTimer();
				}
			}
		};
		
		threadFinished = new Thread() {
			public void run() {
				while(true) {
					try {
						Thread.currentThread().sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(getBackgroundQ().isEmpty() &&  getProcessDuration()) {
						finished = true;
						threadTimer.stop();
						threadDuration.stop();
						if(management.name().contentEquals("FIXED_SIZE")) {
							threadFixed.stop();
						} else {
							threadVariable.stop();
						}
						
						Thread.currentThread().stop();
					}
				}
			}
		};
	}
	
	private void initializeOsInMemory() {
		Process os = new Process("OS", 0, this.osSize, -1);
		
		for(int i = 0; i<this.osSize; i++) {
			this.mainMemory[i] = os;
		}
		for(int i = this.osSize; i<32; i++) {
			this.mainMemory[i] = null;
		}
	}
	
	private void partitionMainMemory() {
		Partition p2 = new Partition(4, 5);
		Partition p3 = new Partition(9, 4);
		Partition p4 = new Partition(13, 9);
		Partition p5 = new Partition(22, 7);
		Partition p6 = new Partition(29, 2);
		
		this.partitionList.add(p2);
		this.partitionList.add(p3);
		this.partitionList.add(p4);
		this.partitionList.add(p5);
		this.partitionList.add(p6);
	}
	
	private void fixed() {
		this.waitingQueue.clear();
		
		threadFixed = new Thread() {
			public void run() {
				Process[] mainMemory = new Process[32];
				threadTimer.start();
				threadFinished.start();
				while(true) {
					//First fit
					try {
						Thread.currentThread().sleep(100);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					while(getBackgroundQ().peek()!=null) {
						
						Process process = getBackgroundQ().poll();
						boolean added = false;
						mainMemory = getMainMemory();
						
						for(Partition partition : partitionList) {
							if(process.getSize() <= partition.size && mainMemory[partition.startAdress] == null) {
								int location = partition.startAdress;
								try {
									Thread.currentThread().sleep(1000);
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
								for(int i = partition.startAdress; i < partition.startAdress + process.getSize(); i++) {
									mainMemory[i] = process;
								}
								added = true;
								addEvent(process, getTimer(), EventType.ALLOCATED, location);
								break;
							}
						}
						if(!added) {
							waitingQueue.add(process);
							added = false;
						}
					}
					
					if(waitingQueue.isEmpty()) {
						break;
					}
					
					try {
						Thread.currentThread().sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					for(Process process : waitingQueue) {
						backgroundQAdd(process);
					}
					
					waitingQueue.clear();
				}
			}
		};
		
		threadFixed.start();	
	}
	
	private boolean getProcessDuration() {
		for(int i = 4; i < 32; i++) {
			if(mainMemory[i]!=null) {
				if(mainMemory[i].getDuration()!=-1) {
					return false;
				}
			}
		}
		return true;
	}
	
	private synchronized void backgroundQAdd(Process p) {
		this.backgroundQueue.add(p);
	}
	
	private synchronized Queue<Process> getBackgroundQ(){
		return this.backgroundQueue;
	}
	
	public synchronized void addEvent(Process p, double t, EventType type) {
		Event event = new Event(p, t, type);
		events.add(event);
	}
	
	private synchronized void addEvent(Process process, double time, EventType type, int location) {
		Event event = new Event(process, time, type, location);
		events.add(event);
	}
	
	public List<Event> getEvents(){
		return this.events;
	}
	
	private void incTimer() {
		this.timer++;
	}
	
	public void setTimer(double timer) {
		this.timer = timer;
	}
	
	private double getTimer() {
		return this.timer;
	}
	
	public Process[] getMain() {
		return this.mainMemory;
	}
	
	private void variable() {
		this.waitingQueue.clear();
		
		threadVariable = new Thread() {
			public void run() {
				Process[] mainMemory = new Process[32];
				threadTimer.start();
				threadFinished.start();
				while(true) {
					try {
						Thread.currentThread().sleep(500);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					//First fit
					while(backgroundQueue.peek()!=null) {
						try {
							Thread.currentThread().sleep(1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						Process process = backgroundQueue.poll();
						boolean added = false;
						boolean notNull = false;
						mainMemory = getMainMemory();
						for(int i = 4; i<32; i++) {
							if(process.getSize() + i >= 32) {
								break;
							}
							if(mainMemory[i] == null) {
								for(int j = i; j < process.getSize() + i; j++) {
									if(mainMemory[j] != null) {
										notNull = true;
										break;
									}
								}
								if(!notNull) {
									for(int j = i; j < i + process.getSize(); j++) {
										mainMemory[j] = process;
										added = true;
									}
									addEvent(process, getTimer(), EventType.ALLOCATED, i);
									break;
								}
							} else {
								continue;
							}
						}
						if(!added) {
							waitingQueue.add(process);
							added = true;
						}
						
					}
					
					if(waitingQueue.isEmpty()) {
						break;
					}
					
					try {
						Thread.currentThread().sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					for(Process process : waitingQueue) {
						backgroundQueue.add(process);
					}
					waitingQueue.clear();			
				}
			}
		};
		
		threadVariable.start();
	}
	
	public void runAlgorithm() {
		switch(this.management.name()){
		case "FIXED_SIZE": 
			this.fixed();
			break;
		case "VARIABLE_SIZE":
			this.variable();
			break;
//		case "PAGING":
//			this.paging();
//			break;
		}
	}
	
	public Queue<Process> getWaitingQueue(){
		return this.waitingQueue;
	}
	
	public synchronized Queue<Process> getBackground(){
		return this.backgroundProcesses;
	}
	
	public void initializeInitialFragments(JFrame frame) {
		mm.addMemoryComponent(mainMemory[0], 4, 0);
		
		for(Memory memory : mm.getRectangles()) {
			frame.add(memory);
			memory.setProperties();
		}
		
		if(this.management.name().contentEquals("FIXED_SIZE")) {
			for(Partition partition : this.partitionList) {
				mm.setFragment(partition.startAdress, partition.size);
			}
			for(Memory memory : mm.getInitialFragments()) {
				frame.add(memory);
				memory.setProperties2();
				memory.setBackground(new Color(0.0f, 0.0f, 0.0f, 0f));
				memory.setOpaque(false);
			}
		} else if(this.management.name().contentEquals("VARIABLE_SIZE")) {
			mm.setFragment(4, 27);
			for(Memory memory : mm.getInitialFragments()) {
				frame.add(memory);
				memory.setProperties2();
				
				memory.setBackground(new Color(0.0f, 0.0f, 0.0f, 0f));
				memory.setOpaque(false);
			}
		} else if(this.management.name().contentEquals("PAGINATION")) {
			
		}
		
	}
	
	public void initializeMainMemory(JFrame frame) {
		threadProcess = new Thread() {
			public void run() {
				mm.getRectangles().clear();
				Process[] mainMemory = null;
				
				while(true) {
						try {
							Thread.currentThread().sleep(1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						
						for(Memory memory : mm.getRectangles()) {
							frame.remove(memory);
						}
						
						mainMemory = getMainMemory();
						
						mm.getRectangles().clear();
						
						for(int i = 4; i<31; i++) {
							if(mainMemory[i] != null) {
								mm.addMemoryComponent(mainMemory[i], mainMemory[i].getSize(), i);
								i += mainMemory[i].getSize() - 1;
								
							} 
							
							
						}
						frame.repaint();
						for(Memory memory : mm.getRectangles()) {
							if(!getRemoved()) {
							try {
								Thread.currentThread().sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						frame.add(memory);
						memory.setProperties();
						}
						setRemovedFalse();
					}
			}
		};
		
		threadProcess.start();

	}
	
	private synchronized Process[] getMainMemory() {
		return this.mainMemory;
	}
	
	private synchronized void removeFromMain(int index) {
		this.mainMemory[index] = null;
		this.removed = true;
	}
	
	private synchronized boolean getRemoved() {
		return this.removed;
	}
	
	private synchronized void setRemovedFalse() {
		this.removed = true;
	}
	
	public boolean getFinished() {
		return this.finished;
	}
	
	public void startDuration() {
		threadDuration = new Thread() {
			public void run() {
				List<Process> processes = new ArrayList<Process>();
				List<Process> removed = new ArrayList<Process>();
				while(true) {
					try {
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for(Process process : getMainMemory()) {
						if(process!=null) {
							if(!processes.contains(process) && process.getDuration() != -1) {
								processes.add(process);
							}
						}
					}
					
					for(int i = 0; i<processes.size(); i++) {
						Process process = processes.get(i);
						process.decCurrentDuration(1);
						if(process.getCurrentDuration() == 0) {
							processes.remove(process);
							removed.add(process);
						}
					}
					
					if(!removed.isEmpty()) {
						Process[] processArray = getMainMemory();
						for(Process process : removed) {
							boolean remove = false;
							for(int i = 0; i<32; i++) {
								Process p = processArray[i];
								if(process == p) {
									if(!remove) {
										try {
											Thread.currentThread().sleep(500);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										addEvent(process, getTimer(), EventType.DEALLOCATED);
										remove = true;
									}
									removeFromMain(i);
								}
							}
						}
						removed.clear();
					}
					
				}
			}
		};
		
		threadDuration.start();
	}
	
}
