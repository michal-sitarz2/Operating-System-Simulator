package processScheduler;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

@SuppressWarnings("serial")
public class Cell extends JLabel{
	
		private CellTime startTime = null;
		private CellTime endTime = null;
		
	    public Cell(int x ,int y, Event event, int quantum, boolean ioCheck, double duration){
	        setSize((int) (event.process.getCPUBurst()) * 2 , 40);
	        setForeground(Color.BLACK);
	        setHorizontalAlignment(CENTER); 
	        setBackground(event.process.getColor());
	        setText("P" + event.process.getPID());
	        setLocation(x, y);
	        setOpaque(true);
	        
	        if(event.process.getBlockDuration() != 0) {
	        	this.startTime = new CellTime(x-10, y+40, event.time); 
	        	if(ioCheck) {
	        		this.endTime = new CellTime(x + (event.process.getCPUBurst() * 2) - 30, y+40, event.time + event.process.getBlockStart());
	        	} else {
	        		this.endTime = new CellTime(x + (event.process.getCPUBurst() * 2) - 30, y+40, event.process.getFinishTime());
	        	}
	        	
	        } else if(quantum == 0) {
		        this.startTime = new CellTime(x-10, y+40, event.time); 
		        this.endTime = new CellTime(x + (event.process.getCPUBurst() * 2) - 30, y+40, event.time + event.process.getCPUBurst());
	        } else {
	        	this.startTime = new CellTime(x-10, y+40, event.time); 
	        	if(duration!=0) {
	        		this.endTime = new CellTime(x + (event.process.getCPUBurst() * 2) - 30, y+40, event.time + duration);
	        	} else {
	        		this.endTime = new CellTime(x + (event.process.getCPUBurst() * 2) - 30, y+40, event.time + quantum);
	        	}
	        }
	        	
	    }
	    
	    public Cell(int x, int y) {
	    	setBounds((int) x,(int) y, 200, 30);
	    	setFont(new Font("Times New Roman", Font.BOLD, 20));
			setText("No more space for the graph...");
			setForeground(Color.red);
			setBackground(Color.white);
			setHorizontalAlignment(CENTER);
	    }
	    
	    public CellTime getStart() {
	    	return this.startTime;
	    }
	    
	    public CellTime getEnd() {
	    	return this.endTime;
	    }
}
