package de.anteiku.kittybot;

import java.util.Scanner;

public class ConsoleThread extends Thread{
	
	private Scanner scanner;
	private KittyBot main;
	private boolean running = true;
	
	public ConsoleThread(KittyBot main){
		this.main = main;
		scanner = new Scanner(System.in);
		start();
	}
	
	@Override
	public void run(){
		while(running){
			if(scanner.hasNextLine()){
				command(scanner.nextLine());
			}
		}
	}
	
	public void command(String cmd){
		if(cmd.equalsIgnoreCase("stop")){
			Logger.print("Stopping KittyBot...");
			main.close();
		}
		else if(cmd.equalsIgnoreCase("reload")){
			Logger.print("Unknown Command! Use 'help' or '?' for a overview of commands");
		}
		else if(cmd.equalsIgnoreCase("debug")){
			if(Logger.getDebug()){
				Logger.setDebug(false);
			}
			else{
				Logger.setDebug(true);
			}
			Logger.print("Debug Mode: '" + Logger.getDebug() + "'");
		}
		else{
			Logger.print("Unknown Command! Use 'help' or '?' for a overview of commands");
		}
	}
	
}
