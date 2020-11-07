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

import memory.AllocationPolicy;
import memory.ManagementType;

public class MMU_Options implements ActionListener{

	private JFrame frame = new JFrame("Memory Management Unit");
	private JButton backButton = new JButton();
	private JButton findFile = new JButton();
	private JButton startButton = new JButton();
	private JLabel algorithmsLabel = new JLabel();
	private JLabel allocationLabel = new JLabel();
	private JLabel fileInputLabel = new JLabel();
	private JLabel invalidInputs = new JLabel();
	private JTextField fileText = new JTextField();
	private JRadioButton radioButton1 = new JRadioButton();
	private JRadioButton radioButton2 = new JRadioButton();
	private JRadioButton radioButton3 = new JRadioButton();
	private JRadioButton radioButton4 = new JRadioButton();
	private ButtonGroup buttonGroup1 = new ButtonGroup();
	private ButtonGroup buttonGroup2 = new ButtonGroup();
	private JLabel outputDirectoryLabel = new JLabel();
	private JTextField outputText = new JTextField();
	private JButton findDirectory = new JButton();
	
	private int size = 0;
	private int osSize = 0;
	private File selectedFile = null;
	private ManagementType management = null;
	private AllocationPolicy allocation = null;
	private Path selectedDirectory = FileSystems.getDefault().getPath(System.getProperty("user.dir"));
	
	
	public MMU_Options(int size, int osSize) {
		this.prepareGUI();
		this.intializeAlg();
		this.browserInitialize();
		this.browserDirectoryInitialize();
		this.size = size;
		this.osSize = osSize;
	}
	
	
	private void prepareGUI() {
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setBackground(Color.white);
		frame.setVisible(true);
		frame.setBounds(200,200,800,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        backButton.setBounds(600, 450, 110, 40);
		frame.add(backButton);
		backButton.addActionListener(this);
		backButton.setText("Back");
		
		startButton.setBounds(450, 450, 110, 40);
		frame.add(startButton);
		startButton.addActionListener(this);
		startButton.setText("Start");
	}
	
	private void browserInitialize() {
		fileText.setBounds(70, 80, 200, 30);
		frame.add(fileText);
		fileText.setEditable(false);
		findFile.setBounds(280, 80, 110, 30);
		frame.add(findFile);
		findFile.addActionListener(this);
		findFile.setText("Search");
		fileInputLabel.setBounds(70, 40, 100, 30);
		fileInputLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
		fileInputLabel.setText("Input File");
		frame.add(fileInputLabel);
	}
	
	private void browserDirectoryInitialize() {
		outputText.setBounds(430, 80, 200, 30);
		frame.add(outputText);
		outputText.setEditable(false);
		findDirectory.setBounds(640, 80, 110, 30);
		frame.add(findDirectory);
		findDirectory.addActionListener(this);
		findDirectory.setText("Search");
		outputDirectoryLabel.setBounds(430, 40, 150, 30);
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
	
	private void intializeAlg() {
		algorithmsLabel.setBounds(100, 250, 250, 30);
		algorithmsLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
		frame.add(algorithmsLabel);
		algorithmsLabel.setText("Algorithms");
		//Radio Buttons
		radioButton1.setBounds(100, 300, 200, 30);
		radioButton1.setFont(new Font("Times New Roman", Font.BOLD, 18));
		frame.add(radioButton1);
		radioButton1.setText("Fixed Size");
		radioButton2.setBounds(100, 340, 200, 30);
		radioButton2.setFont(new Font("Times New Roman", Font.BOLD, 18));
		frame.add(radioButton2);
		radioButton2.setText("Variable Size");
		
		buttonGroup1.add(radioButton1);
		buttonGroup1.add(radioButton2);
		
		allocationLabel.setBounds(450, 250, 250, 30);
		allocationLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
		frame.add(allocationLabel);
		allocationLabel.setText("Allocation Policy");
		
		radioButton4.setBounds(450, 300, 200, 30);
		radioButton4.setFont(new Font("Times New Roman", Font.BOLD, 18));
		frame.add(radioButton4);
		radioButton4.setText("First Fit");
		
		buttonGroup2.add(radioButton4);
		
	}


	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == findFile) {
			this.fileChooserInitialize();
			if(selectedFile != null){
				fileText.setText(selectedFile.toString());
			}
		} else if(event.getSource() == findDirectory) {
			this.directoryChooserInitialize();
			if(selectedDirectory != null){
				outputText.setText(selectedDirectory.toString());
			}
		} else if(event.getSource() == backButton) {
			frame.dispose();
			Welcome_Window ww = new Welcome_Window();
		} else if(event.getSource() == startButton) {
			boolean check = true;
			if(radioButton1.isSelected()) {
				management = ManagementType.FIXED_SIZE;
			} else if(radioButton2.isSelected()) {
				management = ManagementType.VARIABLE_SIZE;	
			} else if(radioButton3.isSelected()) {
				management = ManagementType.PAGING;
			}
			
			if(radioButton4.isSelected()) {
				allocation = AllocationPolicy.FIRST;
			}
			
			if(management == null) {
				check = false;
			}
			if(allocation == null) {
				check = false; 
			}
			
			if(selectedFile == null) {
				check = false;
			} else if(!selectedFile.toString().endsWith(".txt")) {
				check = false;
			}
			
			if(!check) {
				invalidInputs.setBounds(100,460,400,50);
				invalidInputs.setFont(new Font("Times New Roman", Font.BOLD, 20));
				invalidInputs.setForeground(Color.RED);
				frame.add(invalidInputs);
				invalidInputs.setText("Please fill everything correctly");
				return;
			}
			
			frame.dispose();
			MMU_Frame mmu = new MMU_Frame(selectedFile, osSize, size, management, allocation, selectedDirectory);
		}
		
	}
	
	
}
