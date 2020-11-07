package processScheduler;

import java.util.ArrayList;
import java.util.List;

public class GanttChart {
	
	private int yCoordinate = 0;
	private int xCoordinate = 0;
	
	private int quantum = 0;
	private boolean tooMany = false;
	
	private List<Cell> cellList = null;
	private List<CellTime> cellTimeList = null;
	
	
	public GanttChart(QueueType algorithm, int quantum) {
		this.cellList = new ArrayList<Cell>();
		this.cellTimeList = new ArrayList<CellTime>();
		this.xCoordinate = 65;
		this.yCoordinate = 80;
		if(algorithm.name().contentEquals("RR")) {
			this.quantum = quantum;
		}
		
	}
	
	public void addProcess(Event event, boolean ioCheck, double duration) {
		if(this.xCoordinate + event.process.getCPUBurst() >=910) {
			this.xCoordinate = 50;
			this.yCoordinate += 80;
		} 
		if(this.yCoordinate <= 370) {
			Cell cell = new Cell(xCoordinate, yCoordinate, event, quantum, ioCheck, duration);
			this.cellList.add(cell);
			
			this.cellTimeList.add(cell.getStart());
			this.cellTimeList.add(cell.getEnd());
			
			this.xCoordinate += (int) (event.process.getCPUBurst() * 2) + 20;
		} else {
			tooMany = true;
			if(tooMany) {
				Cell cell = new Cell(450, 410);
				this.cellList.add(cell);
			}
		}
	}
	
	public List<Cell> getCellList(){
		return this.cellList;
	}
	
	public List<CellTime> getCellTimeList(){
		return this.cellTimeList;
	}
}
