package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.StreamSupport;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import processScheduler.QueueType;
import processScheduler.ProcessScheduling_Frame;

public class PSS_Frame implements ActionListener{
	
	private File selectedFile = new File("");
	private Path selectedDirectory = FileSystems.getDefault().getPath(System.getProperty("user.dir"));
	private int contextSwitching = 1;
	private int timeQuantum = 10;
	private QueueType algorithmType = null;
	
	private JFrame frame = new JFrame("Process Scheduler Simulator");
	private JButton backButton = new JButton("Back");
	private JTextField fileText = new JTextField();
	private JButton findFile = new JButton("Search Files");
	private JButton findDirectory = new JButton();
	private JLabel fileInputLabel = new JLabel();
	private JLabel outputDirectoryLabel = new JLabel();
	private JTextField outputText = new JTextField();
	private JLabel additionalInputsLabel = new JLabel();
	private JLabel contextSwitchingLabel = new JLabel();
	private JLabel timeQuantumLabel = new JLabel();
	private JTextField contextSInput = new JTextField();
	private JTextField timeQInput = new JTextField();
	private JButton simulateButton = new JButton();
	private JLabel invalidInputs = new JLabel();
	private JLabel algorithmsLabel = new JLabel();
	private JRadioButton radioButton1 = new JRadioButton();
	private JRadioButton radioButton2 = new JRadioButton();
	private JRadioButton radioButton3 = new JRadioButton();
	private JRadioButton radioButton4 = new JRadioButton();
	private ButtonGroup buttonGroup = new ButtonGroup();
	
	public PSS_Frame(){
		this.prepareGUI();
		this.buttonProperties();
		this.browserInitialize();
		this.browserDirectoryInitialize();
		this.additionalInputsInitialize();
		this.algorithmInitialize();
	}
	
	private void prepareGUI() {
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setBackground(Color.white);
		frame.setVisible(true);
		frame.setBounds(200,200,1000,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
	}
	
	private void algorithmInitialize() {
		algorithmsLabel.setBounds(530, 200, 250, 30);
		algorithmsLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
		frame.add(algorithmsLabel);
		algorithmsLabel.setText("Algorithms");
		//Radio Buttons
		radioButton1.setBounds(530, 270, 100, 30);
		radioButton1.setFont(new Font("Times New Roman", Font.BOLD, 18));
		frame.add(radioButton1);
		radioButton1.setText("FCFS");
		radioButton2.setBounds(530, 330, 100, 30);
		radioButton2.setFont(new Font("Times New Roman", Font.BOLD, 18));
		frame.add(radioButton2);
		radioButton2.setText("SJF");
		radioButton3.setBounds(680, 270, 100, 30);
		radioButton3.setFont(new Font("Times New Roman", Font.BOLD, 18));
		frame.add(radioButton3);
		radioButton3.setText("PS");
		radioButton4.setBounds(680, 330, 100, 30);
		radioButton4.setFont(new Font("Times New Roman", Font.BOLD, 18));
		frame.add(radioButton4);
		radioButton4.setText("RR");

		buttonGroup.add(radioButton1);
		buttonGroup.add(radioButton2);
		buttonGroup.add(radioButton3);
		buttonGroup.add(radioButton4);
	}
	
	
	
	
	private void additionalInputsInitialize() {
		additionalInputsLabel.setBounds(70, 160, 250, 30);
		additionalInputsLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
		frame.add(additionalInputsLabel);
		additionalInputsLabel.setText("Additional Information");
		
		contextSwitchingLabel.setBounds(70, 210, 250, 30);
		contextSwitchingLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
		frame.add(contextSwitchingLabel);
		contextSwitchingLabel.setText("Context Switching");
		
		contextSInput.setBounds(70, 250, 150, 30);
		contextSInput.setFont(new Font("Time New Roman", Font.PLAIN, 16));
		frame.add(contextSInput);
		contextSInput.setText(Integer.toString(contextSwitching));
		
		timeQuantumLabel.setBounds(70, 300, 150, 30);
		timeQuantumLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
		frame.add(timeQuantumLabel);
		timeQuantumLabel.setText("Time Quantum");
		
		timeQInput.setBounds(70, 340, 150, 30);
		timeQInput.setFont(new Font("Time New Roman", Font.PLAIN, 16));
		frame.add(timeQInput);
		timeQInput.setText(Integer.toString(timeQuantum));
	}
	
	
	private void browserInitialize() {
		fileText.setBounds(70, 80, 200, 30);
		frame.add(fileText);
		fileText.setEditable(false);
		findFile.setBounds(280, 80, 110, 30);
		frame.add(findFile);
		findFile.addActionListener(this);
		fileInputLabel.setBounds(70, 40, 100, 30);
		fileInputLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
		fileInputLabel.setText("Input File");
		frame.add(fileInputLabel);
	}
	
	private void browserDirectoryInitialize() {
		outputText.setBounds(530, 80, 200, 30);
		frame.add(outputText);
		outputText.setEditable(false);
		findDirectory.setBounds(740, 80, 160, 30);
		frame.add(findDirectory);
		findDirectory.addActionListener(this);
		findDirectory.setText("Search Directories");
		outputDirectoryLabel.setBounds(530, 40, 150, 30);
		outputDirectoryLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
		outputDirectoryLabel.setText("Output Directory");
		frame.add(outputDirectoryLabel);
	}
	
	private void fileChooserInitialize() {
		JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));

		int returnValue = jfc.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			Path path = Paths.get(jfc.getSelectedFile().getAbsolutePath());
		    String[] splitPath = StreamSupport.stream(path.spliterator(), false).map(Path::toString).toArray(String[]::new);
			this.selectedFile = new File(splitPath[splitPath.length-1]);
			
		}
	}
	
	private void directoryChooserInitialize() {
		JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnValue = jfc.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedDirectory = Paths.get(jfc.getSelectedFile().getAbsolutePath());
		}
	}
	
	
	private void buttonProperties() {
			backButton.setBounds(780,460,100,50);
			frame.add(backButton);
			backButton.addActionListener(this);
			simulateButton.setBounds(540,460,200,50);
			frame.add(simulateButton);
			simulateButton.addActionListener(this);
			simulateButton.setText("Simulate");
	}

	@Override
	public void actionPerformed(ActionEvent event) throws NullPointerException {
		boolean check = true;
		if(event.getSource() == findFile) {
			this.fileChooserInitialize();
			if(selectedFile != null){
				fileText.setText(selectedFile.toString());
			}
		} else if(event.getSource() == findDirectory) {
			this.directoryChooserInitialize();
			outputText.setText(selectedDirectory.toString());
		} else if(event.getSource() == backButton) {
			frame.dispose();
			Welcome_Window ww = new Welcome_Window();
		} else if(event.getSource() == simulateButton) {
			if(radioButton1.isSelected()) {
				algorithmType = QueueType.FCFS;
			} else if(radioButton2.isSelected()) {
				algorithmType = QueueType.SJF;		
			} else if(radioButton3.isSelected()) {
				algorithmType = QueueType.PS;
			} else if(radioButton4.isSelected()) {
				algorithmType = QueueType.RR;
			}
			
			//Validations for inputs
			
			if(algorithmType == null) {
				check = false;
			}
			if(selectedFile == null) {
				check = false;
			} else if(!selectedFile.toString().endsWith(".txt")) {
				check = false;
			}
			try {
				if(Integer.parseInt(timeQInput.getText()) < 10) {
					check = false;
				} else if(Integer.parseInt(contextSInput.getText()) < 1) {
					check = false;
				} else {
					this.timeQuantum = Integer.parseInt(timeQInput.getText());
					this.contextSwitching = Integer.parseInt(contextSInput.getText());
				}
			} catch(NumberFormatException e) {
				check = false;
			}
			
			
			if(!check) {
				invalidInputs.setBounds(100,460,400,50);
				invalidInputs.setFont(new Font("Times New Roman", Font.BOLD, 20));
				invalidInputs.setForeground(Color.RED);
				frame.add(invalidInputs);
				invalidInputs.setText("Please fill in all the fields correctly to continue");
				return;
			}
			
			frame.dispose();
			ProcessScheduling_Frame pssFrame = new ProcessScheduling_Frame(this.selectedFile, this.selectedDirectory, this.contextSwitching, this.timeQuantum, this.algorithmType);			
		}
		
	}
	
}
