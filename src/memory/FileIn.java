package memory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import processScheduler.Process;

public class FileIn {
	
	private List<Process> data = null;
	private int numberOfProcesses = 0;
	
	public FileIn(String file) throws IllegalArgumentException {
		if(!file.endsWith(".txt")) {
			throw new IllegalArgumentException("The file must be a .txt file");
		}
		
		this.data = new ArrayList<Process>();
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
						int size = Integer.parseInt(s[2]);
						int duration = Integer.parseInt(s[3]);
			
						this.data.add(new Process(pid, arrivalTime, size, duration));
					} else if(s.length == 1) {
						this.numberOfProcesses = Integer.parseInt(s[0]);
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
	
	public int getNumberOfProcesses() {
		return this.numberOfProcesses;
	}
	
	public List<Process> getProcessesFromFile() {
		return this.data;
	}
	
}