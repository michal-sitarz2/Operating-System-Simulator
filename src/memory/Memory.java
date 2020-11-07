package memory;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import processScheduler.Process;

@SuppressWarnings("serial")
public class Memory extends JPanel{
	
	private static final int SIZE = 25;
	public Process p = null;
	private int xPos = 0;
	private int yPos = 0;
	private int size = 0;
	
	public Memory(int xPos, int yPos, int size, Process p) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.size = size;
		this.p = p;
		
	}
	
	public void setProperties() {
		if(p!=null) {
			if(p.getName() == null) {
				add(new JLabel("P" + p.getPID()));
			} else {
				add(new JLabel("P" + p.getPID() + " - " + p.getName()));
			}
			
			
			setBounds(yPos, xPos, 400, SIZE * size);
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
			setBackground(p.getColor());
		} else {
			setBounds(yPos, xPos, 400, SIZE * size);
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
			setBackground(Color.white);
		}
		
	}
	
	public Memory(int xPos, int yPos, int size, Color c) {
		setBounds(yPos, xPos, 400, SIZE * size);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setBackground(c);
	}
	
	public void setProperties2() {
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setBounds(yPos, xPos, 400, SIZE * size);
	}
	
	public Memory(int xPos, int yPos, int size) {
		this.yPos = yPos;
		this.xPos = xPos;
		this.size = size;
		
	}
	
}
