import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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

public class LoadTester extends JFrame implements ActionListener {
	
	JLabel serverLabel = new JLabel("Server URL: ",JLabel.CENTER);
	JLabel clientsLabel = new JLabel("Number of Clients: ",JLabel.CENTER);
	JLabel iterationLabel = new JLabel("Number of iterations: ",JLabel.CENTER);
	JTextField serverInput = new JTextField();
	SpinnerModel model;
	JSpinner clientsInput, iterationInput;
	JButton test = new JButton("Test");
	
	String[] troops = {"Barbarians","Archers","Giants","Balloons","Wizards","Dragons"};
	String[] builds = {"Cannons","ArcherTowers","Mortars","AirDefense","WizardTowers","TeslaTowers"};
	String[] commands = new String [6];
	
	Random rand;
	int random_num = 0;
	
	LoadTesterClient c = new LoadTesterClient();
	
	String serverURL;
	DatagramSocket serverSocket = null;
	
	//Constructor of the load-tester server
	private LoadTester(){
		super("Load Tester");

		int min = 0;
	    int max = 1000;
	    int step = 2;
	    int initValue = 0;
	    model = new SpinnerNumberModel(initValue, min, max, step);
		
		clientsInput = new JSpinner(model);
		iterationInput = new JSpinner(model);
		
		setLayout(new GridLayout(2,2));
		
		add(serverLabel);
		add(serverInput);
		
		//add(clientsLabel);
		//add(clientsInput);
		
		//add(iterationLabel);
		//add(iterationInput);
		
		add(new JLabel("Click button to test >>>> ",JLabel.CENTER));
		add(test);
		test.addActionListener(this);
		
		//Set random commands
		String cmd = "";
		for(int i=0; i<6; i++){
			for(int j=0; j<6; j++){
				for(int k=0; k<6; k++){
					if(j%2 == 0){
						cmd = cmd + " " + troops[k];
					}
					else{
						cmd = cmd + " " + builds[k];
					}
				}
					cmd = cmd + "\n";
			}
			commands[i] = cmd;
		}
		
		pack();
		setSize(400,150);
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new LoadTester();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if(e.getSource() == test){
			serverURL = serverInput.getText();
			
			if(serverURL.isEmpty() == true){
				JOptionPane.showMessageDialog(null, "ServerURL not filled!");
				return;
			}
			
			System.out.println("Testing load...");
			random_num = (int) (Math.random() * ( 4 - 0 )); //Get random num
			try {
				for(int i=0; i<1000; i++){
					c.tester(commands[random_num], serverURL); //Load dummy client with random commands
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
	}

}
