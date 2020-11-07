package processScheduler;

import java.awt.Color;

import javax.swing.JLabel;

@SuppressWarnings("serial")
public class CellTime extends JLabel{

	
	public CellTime(double x, double y, double time) {
		
		setBounds((int) x,(int) y, 40, 30);
		setText(Double.toString(time));
		setBackground(Color.white);
		setHorizontalAlignment(CENTER);
		
	}
	
}
