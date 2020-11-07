package processScheduler;

import java.awt.Color;
import java.util.Random;

/**
 * PSS Values Table
 * --------------------------------------------------
 * | PID | Arrival Time | CPU Burst Time | Priority | 
 * --------------------------------------------------
 * 
 * PSS I/O Blocked Values Table
 * -----------------------------------------------------------------------------------------
 * | PID | Arrival Time | CPU Burst Time | Priority | I/O Block Start | I/O Block Duration | 
 * -----------------------------------------------------------------------------------------
 * 
 * 
 * MMU Values Table
 * ----------------------------------------
 * | PID | Arrival Time | Size | Duration | 
 * ----------------------------------------
 *
 */

public class Process {

	//Process Scheduler
	private int pid = 0;
	private double arrivalTime = 0;
	private double cpuBurst = 0.0;
	private int priority = 0;
	
	private double currentTime = 0;
	private EventType currentEvent = null;
	
	//I/O Block 
	private double blockStart = 0;
	private double blockDuration = 0;
	private double timeBlocked = 0;
	
	
	//Memory Management Unit
	private int size = 0;
	private int duration = 0;
	private String name = null;
	private Color color = null;
	private int currentDuration = 0;

	//calculation result
	private double timeAround = 0;
	private double waitedAround = 0;
	private double finishTime = 0;

	public Process(int pid, double arrivalTime, double cpuBurst, int priority, double currentTime) throws IllegalArgumentException {
		if(pid < 0 || priority < 0 || arrivalTime < 0 || cpuBurst < 0) {
			throw new IllegalArgumentException("Process defined incorrectly");
		}
		this.pid = pid;
		this.arrivalTime = arrivalTime;
		this.cpuBurst = cpuBurst;
		this.priority = priority;
		this.currentTime = currentTime;
		
		this.setColor();
	}
	
	public Process(int pid, double arrivalTime, double cpuBurst, int priority, double blockStart, double blockDuration) throws IllegalArgumentException {
		if(pid < 0 || priority < 0 || arrivalTime < 0 || cpuBurst < 0) {
			throw new IllegalArgumentException("Process defined incorrectly");
		}
		
		if(blockStart + blockDuration > cpuBurst - arrivalTime) {
			throw new IllegalArgumentException();
		}
		
		this.pid = pid;
		this.arrivalTime = arrivalTime;
		this.cpuBurst = cpuBurst;
		this.priority = priority;
		this.blockStart = blockStart;
		this.blockDuration = blockDuration;
		
		this.setColor();
	}
	
	public Process(int pid, double arrivalTime, double cpuBurst, int priority) throws IllegalArgumentException{
		if(pid < 0 || priority < 0 || arrivalTime < 0 || cpuBurst < 0) {
			throw new IllegalArgumentException("Process defined incorrectly");
		}
		this.pid = pid;
		this.arrivalTime = arrivalTime;
		this.cpuBurst = cpuBurst;
		this.priority = priority;
		
		this.setColor();
	}

	public Process(int pid, double arrivalTime, int size, int duration) throws IllegalArgumentException {
		if(pid < 0 || arrivalTime < 0 || size < 0) {
			throw new IllegalArgumentException("Process defined incorrectly");
		}
		
		this.duration = duration;
		this.pid = pid;
		this.arrivalTime = arrivalTime;
		this.size = size;
		this.currentDuration = duration;
		
		this.setColor();
	}
	
	public Process(String name, double arrivalTime, int size, int duration) throws IllegalArgumentException, NullPointerException {
		if(arrivalTime < 0 || size < 0 || duration == 0 || duration < -1) {
			throw new IllegalArgumentException("Process defined incorrectly");
		}
		if(name == null) {
			throw new NullPointerException();
		}
		this.pid = 0;
		this.name = name;
		this.arrivalTime = arrivalTime;
		this.size = size;
		this.duration = duration;
		
		this.color = Color.LIGHT_GRAY;
	}
	
	private void setColor() {
		Random rand = new Random();
		
		float r = (float) (rand.nextFloat() / 2f + 0.5);
		float g = (float) (rand.nextFloat() / 2f + 0.5);
		float b = (float) (rand.nextFloat() / 2f + 0.5);
		
		this.color = new Color(r,g,b);
	}
	
	public void setTimeBlocked(double time) {
		this.timeBlocked = time;
	}
	
	public double getTimeBlocked() {
		return this.timeBlocked;
	}
	
	public double getBlockStart() {
		return this.blockStart;
	}
	
	public double getBlockDuration() {
		return this.blockDuration;
	}
	
	public void setCurrentEvent(EventType type) {
		this.currentEvent = type;
	}
	
	public EventType getCurrentEvent() {
		return this.currentEvent;
	}
	
	public void decCurrentDuration(int time) {
		this.currentDuration = this.currentDuration - time; 
	}
	
	public int getCurrentDuration() {
		return this.currentDuration;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public double getDuration() {
		return this.duration;
	}
	
	public int getPID() {
		return this.pid;
	}
	
	public double getArrivalTime() {
		return this.arrivalTime;
	}
	
	public double getCPUBurst() {
		return this.cpuBurst;
	}
	
	public int getPriority() {
		return this.priority;
	}
	
	public void setFinishTime(double time) {
		finishTime = time;
	}
	
	public double getFinishTime() {
		return this.finishTime;
	}
	
	public void calculateTimeAround() {
		double timeAround = (this.finishTime - this.arrivalTime);
		this.timeAround = (double) Math.round(timeAround * 1000) / 1000;
	}
	
	public void calculateWaitedAround() {
		double waitedAround = (timeAround - cpuBurst);
		this.waitedAround = (double) Math.round(waitedAround* 1000) / 1000;
	}
	
	public double getTimeAround() {
		return this.timeAround;
	}
	
	public double getWaitedAround() {
		return this.waitedAround;
	}
	
	public double getCurrentTime() {
		return this.currentTime;
	}
	
	public void setCurrentTime(double time) {
		this.currentTime = time;
	}
	
	public void addCurrentTime(double time) {
		this.currentTime += time;
	}
	
	public void setArrivalTime(double time) {
		this.arrivalTime = time;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String printFinal() {
		return pid + "  " + arrivalTime + "  " + finishTime + "  " + cpuBurst + "  " + priority + "  " + timeAround + "  " + waitedAround;
	}
	
	@Override
	public String toString() {
		if(cpuBurst != 0) {
			return pid + "  " + arrivalTime + "  " + cpuBurst + "  " + priority + " ";
		}
		if(name==null) {
			return pid + "  " + arrivalTime + "  " + size + " " + duration;
		}
		return pid + "  " + name + "  " + arrivalTime + "  " + size + " " + duration;
		
	}
}
