package processScheduler;

public class Event {
	
	public Process process = null;
	public double time = 0;
	public EventType type = null;
	public int location = -1;
	
	public Event(Process process, double time, EventType type) {
		this.process = process;
		this.time = time;
		this.type = type;
	}
	
	public Event(Process process, double time, EventType type, int location) {
		this.process = process;
		this.time = time;
		this.type = type;
		this.location = location;
	}
	
	@Override
	public String toString() {
		if(this.location==-1) {
			return process.toString() + "  " + time + "  " + type;
		}
		return process.toString() + "  " + time + "  " + type + " to memory location " + location;
	}
}
