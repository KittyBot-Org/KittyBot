package de.anteiku.kittybot;

import de.anteiku.kittybot.utils.Logger;

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
		switch(cmd.toLowerCase()) {
			case "stop":
				Logger.print("Stopping KittyBot...");
				main.close();
				break;
			case "reload":
				Logger.print("Reloading KittyBot...");
				break;
			case "debug":
				Logger.setDebug(!Logger.getDebug());
				Logger.print("Debug Mode: '" + Logger.getDebug() + "'");
				break;
			default:
				Logger.print("Unknown Command! Use 'help' or '?' for a overview of commands");
				break;
		}
	}
	
}
