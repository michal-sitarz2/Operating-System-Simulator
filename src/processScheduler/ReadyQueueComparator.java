package processScheduler;

import java.util.Comparator;

public class ReadyQueueComparator implements Comparator<Process> {
	
	private QueueType type = null;
	
	public ReadyQueueComparator(QueueType type) {
		super();
		this.type = type;
	}
	
	private int FCFS_compare(Process p1, Process p2) {
		if(p1.getArrivalTime() < p2.getArrivalTime()) {
			return -1;
		} else if (p1.getArrivalTime() > p2.getArrivalTime()) {
			return 1;
		}
		
		return 0;
	}
	
	private int SJF_compare(Process p1, Process p2) {
		if(p1.getCPUBurst() <= p2.getCPUBurst()) {
			return -1;
		} else if(p1.getCPUBurst() >= p2.getCPUBurst()) {
			return 1;
		}
		
		return 0;
	}
	
	private int PS_compare(Process p1, Process p2) {
		if(p1.getPriority() > p2.getPriority()) {
			return -1;
		} else if(p1.getPriority() < p2.getPriority()) {
			return 1;
		}
		
		return 0;
	}
	
	private int background_compare(Process p1, Process p2) {
		if(p1.getArrivalTime() < p2.getArrivalTime()) {
			return -1;
		} else if (p1.getArrivalTime() > p2.getArrivalTime()) {
			return 1;
		}
		
		return 0;
	}
	
	@Override
	public int compare(Process p1, Process p2) {
		if(type.name().contentEquals("FCFS")) {
			return FCFS_compare(p1, p2);
		} else if (type.name().contentEquals("SJF")) {
			return SJF_compare(p1, p2);
		} else if (type.name().contentEquals("PS")) {
			return PS_compare(p1,p2);
		} else if (type.name().contentEquals("background")) {
			return background_compare(p1,p2);
		} 
		return 0;
		
	}
	
	public void setType(QueueType type) {
		this.type = type;
	}
}
