package gui;

import java.io.IOException;

import gui.Booting_Frame;
import gui.Welcome_Window;

public class Booting {
	public static void main(String[] args) throws IOException, InterruptedException {
		
		Thread thread = new Thread() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				Booting_Frame bf = new Booting_Frame();
				
				try {
					Thread.currentThread().sleep(6000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					bf.getFrame().dispose();
				}
				
			}
			
		};
		
		thread.start();
		thread.join();
		
		Welcome_Window ww = new Welcome_Window();
	}
}
