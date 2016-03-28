import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/*
This is the load-tester of the game wherein its functions are:
	Creates 1000 dummy client accounts without UI
	Simultaneous create of N-game sessions with M clients for each game
	session on server
	Auto-send of commands using UDP
	Auto-send of randomized Lorem-ipsum texts using TCP
	Should be able to change the target server’s IP address.
*/

public class LoadTester extends JFrame implements Runnable, Constants {
	
	String randomText[] = {"Just","A","Rather","Very","Intelligent","System"};
	String commands[] = {"CONNECT","OffensiveTactics","DefensiveTactics:","Start","Restart"};
	String serverURL;
	DatagramSocket serverSocket = null;
	
	//Constructor of the load-tester server
	private LoadTester(){
		super("Load Tester");
		
		JLabel serverLabel = new JLabel("Server URL: ",JLabel.CENTER);
		JLabel clientsLabel = new JLabel("Number of Clients: ",JLabel.CENTER);
		JLabel iterationLabel = new JLabel("Number of iterations: ",JLabel.CENTER);
		
		JTextField serverInput = new JTextField();
		
		int min = 0;
	    int max = 1000;
	    int step = 2;
	    int initValue = 0;
	    SpinnerModel model = new SpinnerNumberModel(initValue, min, max, step);
		
		JSpinner clientsInput = new JSpinner(model);
		JSpinner iterationInput = new JSpinner(model);
		
		setLayout(new GridLayout(3,2));
		
		add(serverLabel);
		add(serverInput);
		
		add(clientsLabel);
		add(clientsInput);
		
		add(iterationLabel);
		add(iterationInput);
		
		pack();
		setSize(400,150);
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	//Run the thread server for the load-tester
	public void run() {
		// TODO Auto-generated method stub
			try {
				serverSocket = new DatagramSocket(PORT);
			} catch (SocketException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}             
			byte[] receiveData = new byte[1024];            
			byte[] sendData = new byte[1024]; 
			
			while(true){                 
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);                   
				try {
					serverSocket.receive(receivePacket);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				String sentence = new String( receivePacket.getData());                   
				System.out.println("RECEIVED: " + sentence);                   
				InetAddress IPAddress = receivePacket.getAddress();                   
				int port = receivePacket.getPort();                   
				
				String capitalizedSentence = sentence.toUpperCase();                   
				sendData = capitalizedSentence.getBytes();                   
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);                   
				try {
					serverSocket.send(sendPacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}                
			}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new LoadTester();
	}

}
