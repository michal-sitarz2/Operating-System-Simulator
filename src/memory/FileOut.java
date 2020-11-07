package memory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class FileOut {
	
	public FileOut(String input, Path destination, String algorithm) throws IOException {
		this.createDirectory(destination, algorithm);
		this.createFile(destination, algorithm, input);
	}
	private void createDirectory(Path destination, String algorithm) throws IOException{
		String path = destination.toString() + "\\" + algorithm;

		File directory = new File(path);
		
		if(directory.exists()) {
			for(File file: directory.listFiles()) {
				file.delete();
			}
		} else {
			boolean check = directory.mkdir();
			if(!check) {
				throw new IOException("The directory was not created");
			}
		}
	}

	private void createFile(Path directory, String algorithm, String text) throws IOException {
		String file = directory + "\\" + algorithm + "\\" + "Events.txt";
		File file1 = new File(file);
		boolean success = file1.createNewFile();
		if(!success) {
			throw new IOException("The results file was not created");
		}
		
		this.fileWriter(file, text);
	}

	
	private void fileWriter(String file, String text) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(text);
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}

