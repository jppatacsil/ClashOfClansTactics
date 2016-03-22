import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class UDPClient extends JPanel{

	String username = "anonymous";
	String address = "localhost";
	boolean offenseTaken = false;
	boolean defenseTaken = false;

	JPanel battleScreen; //the card2, battle page
	JPanel attackScreen; //the west panel on battle screen
	JPanel defScreen; //the east panel on battle screen
	
	static JLabel oPortrait; //portrait for offense
	static JLabel dPortrait; //portrait for defense
	
	JLabel headerLabel; //label for battle

	static ImageIcon[] oAvatar = new ImageIcon[7]; //Images for offensive troop
	static ImageIcon[] dAvatar = new ImageIcon[7]; //Images for defensive building

	Font font = new Font("Supercell-Magic", Font.BOLD,36);
	
	//Constructor
	public UDPClient(){
		
		//the battle screen
				battleScreen = new JPanel(new BorderLayout());
				battleScreen.setPreferredSize(new Dimension(700,500));
				battleScreen.setBackground(Color.RED);
				
				//the offense and defense pane
				attackScreen = new JPanel();
				attackScreen.setPreferredSize(new Dimension(350,500));
				attackScreen.setBackground(Color.WHITE);
				
				defScreen = new JPanel();
				defScreen.setPreferredSize(new Dimension(350,500));
				defScreen.setBackground(Color.WHITE);
				
				//Load all the images for offensive troops
				oAvatar[0] = new ImageIcon("unknownAvatar.jpg");
				oAvatar[1] = new ImageIcon("barbsAvatar.jpg");
				oAvatar[2] = new ImageIcon("archerAvatar.jpg");
				oAvatar[3] = new ImageIcon("giantsAvatar.jpg");
				oAvatar[4] = new ImageIcon("balloonAvatar.jpg");
				oAvatar[5] = new ImageIcon("wizardsAvatar.jpg");
				oAvatar[6] = new ImageIcon("dragonAvatar.jpg");

				//Load all images for defensive buildings
				dAvatar[0] = new ImageIcon("unknownAvatar.jpg");
				dAvatar[1] = new ImageIcon("cannonAvatar.jpg");
				dAvatar[2] = new ImageIcon("aTowerAvatar.jpg");
				dAvatar[3] = new ImageIcon("mortarAvatar.jpg");
				dAvatar[4] = new ImageIcon("airDAvatar.jpg");
				dAvatar[5] = new ImageIcon("wTowerAvatar.jpg");
				dAvatar[6] = new ImageIcon("tTowerAvatar.jpg");

				oPortrait = new JLabel(oAvatar[0]);
				dPortrait = new JLabel(dAvatar[0]);
				
				headerLabel = new JLabel("Waiting for players...",SwingConstants.CENTER);
				headerLabel.setFont(font);
				
				battleScreen.add(headerLabel, BorderLayout.NORTH);

				battleScreen.add(attackScreen, BorderLayout.WEST);
				attackScreen.add(oPortrait);
				battleScreen.add(defScreen, BorderLayout.EAST);
				defScreen.add(dPortrait);
				
				add(battleScreen);
				setSize(700,500);
	}
	
	//Set the address
	public void setAddress(String address){
		this.address = address;
	}
	
	//set the username
	public void setName(String name){
		this.username = name;
	}

	public void sendPacket(String packet) throws IOException{
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(this.address); //Change with the server's address if used on another machine	
		
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		
		String sentence = packet; //The defensive tactics that was sent
		sendData = sentence.getBytes();
		
		//Send the request
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
		clientSocket.send(sendPacket);
		
		//Get the reply
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		
		//Print the reply received
		String modifiedSentence = new String(receivePacket.getData());
		System.out.println("RECEIVED BY "+username+": " + modifiedSentence.trim());
		clientSocket.close(); //close the socket
		
		String[] tactics = modifiedSentence.split(",");
		
		if(tactics[0].equals("OFFENSIVE TACTICS") && offenseTaken == true){
			JOptionPane.showMessageDialog(null, "Offense Already Taken!");
			return;
		}
		
		if(tactics[0].equals("DEFENSIVE TACTICS") && defenseTaken == true){
			JOptionPane.showMessageDialog(null, "Defense Already Taken!");
			return;
		}
		
		if(tactics[0].equals("OFFENSIVE TACTICS")){ //If packets sent was for offense
			//Update the offensive side of the battle screen
			offenseTaken = true;
			updateScreen(sentence, tactics);
		}
		
		else{ //If packets sent was for defense
			//Update the defensive side of the battle screen
			defenseTaken = true;
			updateScreen(sentence, tactics);
		}
		
		//If both players are connected, game will now start
		if(oPortrait.getIcon() != oAvatar[0] && dPortrait.getIcon() != dAvatar[0]){
			headerLabel.setText("CLASH!!!");
		}
	}
	
	public static void updateScreen(String sentence, String[] tactics){
		
		if(tactics[0].equals("OFFENSIVE TACTICS")){//Offensive side

			//FOR FIRST OFFENSIVE TROOP
			if(tactics[1].equals("BARBARIANS")){ oPortrait.setIcon(oAvatar[1]);}
			else if(tactics[1].equals("ARCHERS")){ oPortrait.setIcon(oAvatar[2]);}
			else if(tactics[1].equals("GIANTS")){ oPortrait.setIcon(oAvatar[3]);}
			else if(tactics[1].equals("BALLOONS")){ oPortrait.setIcon(oAvatar[4]);}
			else if(tactics[1].equals("WIZARDS")){ oPortrait.setIcon(oAvatar[5]);}
			else{ oPortrait.setIcon(oAvatar[6]);}
			
			/*
			
			try
			{
			Thread.sleep(5000);//1sec
			}
			catch(InterruptedException ex)
			{
			ex.printStackTrace();
			}
			
			//SECOND OFFENSIVE TROOP
			if(tactics[2].equals("Barbarians")){ oPortrait.setIcon(oAvatar[1]);}
			else if(tactics[2].equals("Archers")){ oPortrait.setIcon(oAvatar[2]);}
			else if(tactics[2].equals("Giants")){ oPortrait.setIcon(oAvatar[3]);}
			else if(tactics[2].equals("Balloons")){ oPortrait.setIcon(oAvatar[4]);}
			else if(tactics[2].equals("Wizards")){ oPortrait.setIcon(oAvatar[5]);}
			else{ oPortrait.setIcon(oAvatar[6]);}
			
			try
			{
			Thread.sleep(5000);//1sec
			}
			catch(InterruptedException ex)
			{
			ex.printStackTrace();
			}
			
			//THIRD OFFENSIVE TROOP
			if(tactics[3].equals("Barbarians")){ oPortrait.setIcon(oAvatar[1]);}
			else if(tactics[3].equals("Archers")){ oPortrait.setIcon(oAvatar[2]);}
			else if(tactics[3].equals("Giants")){ oPortrait.setIcon(oAvatar[3]);}
			else if(tactics[3].equals("Balloons")){ oPortrait.setIcon(oAvatar[4]);}
			else if(tactics[3].equals("Wizards")){ oPortrait.setIcon(oAvatar[5]);}
			else{ oPortrait.setIcon(oAvatar[6]);}
			
			try
			{
			Thread.sleep(5000);//1sec
			}
			catch(InterruptedException ex)
			{
			ex.printStackTrace();
			}
			
			oPortrait.setIcon(oAvatar[0]); //DONE
			
			*/
			
		}else if(tactics[0].equals("DEFENSIVE TACTICS")){//Defensive side

			//FIRST DEFENSIVE BUILDING
			if(tactics[1].equals("CANNONS")){ dPortrait.setIcon(dAvatar[1]);}
			else if(tactics[1].equals("ARCHER TOWERS")){ dPortrait.setIcon(dAvatar[2]);}
			else if(tactics[1].equals("MORTARS")){ dPortrait.setIcon(dAvatar[3]);}
			else if(tactics[1].equals("AIR DEFENSE")){ dPortrait.setIcon(dAvatar[4]);}
			else if(tactics[1].equals("WIZARD TOWERS")){ dPortrait.setIcon(dAvatar[5]);}
			else{ dPortrait.setIcon(dAvatar[6]);}
			
			/*
			try
			{
			Thread.sleep(5000);//1sec
			}
			catch(InterruptedException ex)
			{
			ex.printStackTrace();
			}
			
			//SECOND DEFENSIVE BUILDING
			if(tactics[2].equals("Cannons")){ dPortrait.setIcon(dAvatar[1]);}
			else if(tactics[2].equals("Archer Towers")){ dPortrait.setIcon(dAvatar[2]);}
			else if(tactics[2].equals("Mortars")){ dPortrait.setIcon(dAvatar[3]);}
			else if(tactics[2].equals("Air Defense")){ dPortrait.setIcon(dAvatar[4]);}
			else if(tactics[2].equals("Wizard Towers")){ dPortrait.setIcon(dAvatar[5]);}
			else{ dPortrait.setIcon(dAvatar[6]);}
			
			try
			{
			Thread.sleep(5000);//1sec
			}
			catch(InterruptedException ex)
			{
			ex.printStackTrace();
			}
			
			//THIRD DEFENSIVE BUILDING
			if(tactics[3].equals("Cannons")){ dPortrait.setIcon(dAvatar[1]);}
			else if(tactics[3].equals("Archer Towers")){ dPortrait.setIcon(dAvatar[2]);}
			else if(tactics[3].equals("Mortars")){ dPortrait.setIcon(dAvatar[3]);}
			else if(tactics[3].equals("Air Defense")){ dPortrait.setIcon(dAvatar[4]);}
			else if(tactics[3].equals("Wizard Towers")){ dPortrait.setIcon(dAvatar[5]);}
			else{ dPortrait.setIcon(dAvatar[6]);}
			
			try
			{
			Thread.sleep(5000);//1sec
			}
			catch(InterruptedException ex)
			{
			ex.printStackTrace();
			}
			
			dPortrait.setIcon(dAvatar[0]); //DONE
			
			*/
			
		}
	}//End of updateScreen
}
