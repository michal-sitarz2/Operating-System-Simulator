package memory;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import processScheduler.Process;

public class MainMemory{

	private static final int CURRENT_Y = 400;
	private List<Memory> rectangles = null; 
	private List<Memory> initialFragments = null;
	private static final int SIZE = 25;
	 
	public MainMemory(int size) {
		this.rectangles = new ArrayList<Memory>();
		this.initialFragments = new ArrayList<Memory>();
	}
	
	public void addMemoryComponent(Process p, int size, int position) {
		
		if(p==null) {
			Memory memory = new Memory(90 + (position * SIZE), CURRENT_Y, size, Color.white);
			this.rectangles.add(memory);
		} else if(p.getPID() == -1) {
			Memory memory = new Memory(90 + (position * SIZE), CURRENT_Y, size, Color.BLACK);
			this.rectangles.add(memory);
		} else {
			Memory memory = new Memory(90 + (position * SIZE), CURRENT_Y, size, p);
			this.rectangles.add(memory);
		}
		
	}
	
	public void addMemoryComponent(int size, int position, Color c) {
		Memory memory = new Memory(90 + (position * SIZE), CURRENT_Y, size, Color.BLACK);
		this.rectangles.add(memory);
	}
	
	public void setFragment(int startAddress, int size) {
		Memory memory = new Memory(90 + (startAddress * SIZE), CURRENT_Y, size);
		this.initialFragments.add(memory);
	}
	
	public List<Memory> getInitialFragments(){
		return this.initialFragments;
	}
	
	public List<Memory> getRectangles() {
		return this.rectangles;
	}
}
