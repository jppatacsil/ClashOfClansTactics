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


public class UDPClient extends JPanel implements Runnable, Constants{
	
	String username = "anonymous";
	String address = "localhost";
	String serverData;
	String[] reply;
	String[] offenseTroops = new String[3];
	String[] defenseBuilds = new String[3];
	
	int gameStage=WAITING_FOR_PLAYERS;
	
	boolean connected = false;
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
	
	static oFormation[] troops = new oFormation[3]; //offensive troops
	static dFormation[] buildings = new dFormation[3]; //defensive buildings

	Font font = new Font("Supercell-Magic", Font.BOLD,36);
	
	DatagramSocket clientSocket = new DatagramSocket();
	Thread t = new Thread(this);
	
	//Constructor
	public UDPClient(String server, String name) throws Exception{
		this.address = server;
		this.username = name;
		
		clientSocket.setSoTimeout(100);
		
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
				
				t.start();
	}
	
	//Set the address
	public void setAddress(String address){
		this.address = address;
	}
	
	//set the username
	public void setName(String name){
		this.username = name;
	}
	
	public String getServerData(){
		return serverData;
	}
	
	//Method to send packets to server
	public void sendMessage(String sentence){
		try{
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			InetAddress IPAddress = InetAddress.getByName(this.address); //Change with the server's address if used on another machine	
			
			byte[] sendData = new byte[1024];
			sendData = sentence.getBytes();
			
        	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
        	clientSocket.send(sendPacket);
        }catch(Exception e){}
	}
	
	@Override
	public void run() { //Method to listen from server
		while(true){
			try{
				Thread.sleep(1);
			}catch(Exception ioe){}
			
			//Get the data from players
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try{
	 			clientSocket.receive(receivePacket);
			}catch(Exception ioe){/*lazy exception handling :)*/}
			
			serverData=new String(receiveData);
			this.serverData=serverData.trim();
			
			//if (!serverData.equals("")){
			//	System.out.println("Server Data>" +serverData);
			//}
	
			//Study the following kids. 
			if (!connected && serverData.startsWith("CONNECTED")){
				connected=true;
				System.out.println("Connected.");
			}else if (!connected){
				System.out.println("Connecting..");				
				sendMessage("CONNECT "+username);
			}else if (connected){
				
				reply = serverData.split(" ");
				
				//CHECK IF BOTH PLAYERS ARE CONNECTED
				if(reply[0].equalsIgnoreCase("START")){
					headerLabel.setText("PLAYERS CONNECTED!");
				}
				
				//IF BOTH PLAYERS ARE CONNECTED, THEN START CLASH
				if(offenseTaken == true && defenseTaken == true){
					clash(0);
				}
				
				//CHECK IF OFFENSIVE SIDE HAS ALREADY BEEN TAKEN
				if(reply[0].equalsIgnoreCase("OFFENSE:") && offenseTaken == true){
					JOptionPane.showMessageDialog(null, "Offense Already Taken!");
					return;
				}
				
				//CHECK IF DEFENSIVE SIDE HAS ALREADY BEEN TAKEN
				if(reply[0].equalsIgnoreCase("DEFENSE:") && defenseTaken == true){
					JOptionPane.showMessageDialog(null, "Defense Already Taken!");
					return;
				}
				
				//UPDATE THE OFFENSE SIDE IF PACKETS SENT WAS FOR OFFENSE
				if(reply[0].equalsIgnoreCase("OFFENSE:")){
					offenseTaken = true;
					//Get the offensive troops
					offenseTroops[0] = reply[1];
					offenseTroops[1] = reply[2];
					offenseTroops[2] = reply[3];
					updateScreen(reply[0], reply[1], 0);
				}
				
				//UPDATE THE DEFENSE SIDE IF PACKETS SENT WAS FOR DEFENSE
				else if(reply[0].equalsIgnoreCase("DEFENSE:")){
					defenseTaken = true;
					//Get the defensive buildings
					defenseBuilds[0] = reply[1];
					defenseBuilds[1] = reply[2];
					defenseBuilds[2] = reply[3];
					updateScreen(reply[0], reply[1], 0);
				}
	
			}

		}			
	}
	
	//This method updates the battle screen
	public static void updateScreen(String playSide, String tactic, int order){
		
		if(playSide.equalsIgnoreCase("OFFENSE:")){//Offensive side

			//FOR THE OFFENSIVE SIDE
			if(tactic.equalsIgnoreCase("BARBARIANS")){ 
				oPortrait.setIcon(oAvatar[1]);
				troops[order] = new oFormation(order,1);
			}else if(tactic.equalsIgnoreCase("ARCHERS")){ 
				oPortrait.setIcon(oAvatar[2]);
				troops[order] = new oFormation(order,2);
			}else if(tactic.equalsIgnoreCase("GIANTS")){ 
				oPortrait.setIcon(oAvatar[3]);
				troops[order] = new oFormation(order,3);
			}else if(tactic.equalsIgnoreCase("BALLOONS")){ 
				oPortrait.setIcon(oAvatar[4]);
				troops[order] = new oFormation(order,4);
			}else if(tactic.equalsIgnoreCase("WIZARDS")){ 
				oPortrait.setIcon(oAvatar[5]);
				troops[order] = new oFormation(order,5);
			}else{ 
				oPortrait.setIcon(oAvatar[6]);
				troops[order] = new oFormation(order,6);
			}
		}else if(playSide.equalsIgnoreCase("DEFENSE:")){//Defensive side

			//FOR THE DEFENSIVE SIDE
			if(tactic.equalsIgnoreCase("CANNONS")){ 
				dPortrait.setIcon(dAvatar[1]);
				buildings[order] = new dFormation(order,1);
			}else if(tactic.equalsIgnoreCase("ARCHERTOWERS")){ 
				dPortrait.setIcon(dAvatar[2]);
				buildings[order] = new dFormation(order,2);
			}else if(tactic.equalsIgnoreCase("MORTARS")){ 
				dPortrait.setIcon(dAvatar[3]);
				buildings[order] = new dFormation(order,3);
			}else if(tactic.equalsIgnoreCase("AIRDEFENSE")){ 
				dPortrait.setIcon(dAvatar[4]);
				buildings[order] = new dFormation(order,4);
			}else if(tactic.equalsIgnoreCase("WIZARDTOWERS")){ 
				dPortrait.setIcon(dAvatar[5]);
				buildings[order] = new dFormation(order,5);
			}else{ 
				dPortrait.setIcon(dAvatar[6]);
				buildings[order] = new dFormation(order,6);
			}

		}
	}//End of updateScreen
	
	//Method to begin clash
	public void clash(int order){
		//GAME CORE LOGIC
		//Traverse the defensive tactic formation of the enemy
		 while(buildings[0].build.hp > 0){ //Fight until the other is destroyed
			 //dPortrait.setIcon(dAvatar[buildings[0].build.type]);
			 //if(order == 0) updateScreen("OFFENSE:",offenseTroops[0],order);
			 if(order == 1) updateScreen("OFFENSE:",offenseTroops[1],order);
			 if(order == 2) updateScreen("OFFENSE:",offenseTroops[2],order);
			 //oPortrait.setIcon(oAvatar[troops[order].troop.type]);
			
			//CLASH!!! The attacker hits first
			buildings[0].build.hp = buildings[0].build.hp - troops[order].troop.ap;
			//Counter attack if still alive
			if(buildings[0].build.hp > 0) troops[order].troop.hp = troops[order].troop.hp - buildings[0].build.ap;
			
			System.out.println("Clash "+(order+1)+":");
			System.out.println("Defense[0] HP Remaining: "+buildings[0].build.hp);
			System.out.println("Offense["+order+"] HP Remaining: "+troops[order].troop.hp);
			
			//Decrement counter of building or troop
			 if(buildings[0].build.hp <= 0){
				JOptionPane.showMessageDialog(null, "\nCLASH!!! WHO WINS???");
				headerLabel.setText("OFFENSE WINS!");
				break;
			 }
			 else if(troops[order].troop.hp <= 0){
				JOptionPane.showMessageDialog(null, "\nCLASH!!! WHO WINS???");
				headerLabel.setText("DEFENSE WINS!");
				 order++;
				 if(order == 3){
					 headerLabel.setText("DEFENSE SUCCESSFUL!");
					 return;
				 }
			 }
		 }

		 /*
		 while(buildings[1].build.hp > 0){
			 //dPortrait.setIcon(dAvatar[buildings[1].build.type]);
			 //oPortrait.setIcon(oAvatar[troops[order].troop.type]);
			
			//CLASH!!! The attacker hits first
			buildings[1].build.hp = buildings[1].build.hp - troops[order].troop.ap;
			//Counter attack if still alive
			if(buildings[1].build.hp > 0) troops[order].troop.hp = troops[order].troop.hp - buildings[1].build.ap;
			
			System.out.println("Defense[1] HP Remaining: "+buildings[1].build.hp);
			System.out.println("Offense["+order+"] HP Remaining: "+troops[order].troop.hp);
			
			//Decrement counter of building or troop
			 if(buildings[1].build.hp <= 0){
				JOptionPane.showMessageDialog(null, "\nCLASH!!! WHO WINS???");
				headerLabel.setText("OFFENSE WINS!");
				 break;
			 }
			 else if(troops[order].troop.hp <= 0){
				JOptionPane.showMessageDialog(null, "\nCLASH!!! WHO WINS???");
				headerLabel.setText("DEFENSE WINS!");
				 order++;
				 if(order == 3){
					 headerLabel.setText("DEFENSE SUCCESSFUL!");
					 return;
				 }
			 }
		 }

		 while(buildings[2].build.hp > 0){
			dPortrait.setIcon(dAvatar[buildings[2].build.type]);
			oPortrait.setIcon(oAvatar[troops[order].troop.type]);
			
			//CLASH!!! The attacker hits first
			buildings[2].build.hp = buildings[2].build.hp - troops[order].troop.ap;
			//Counter attack if still alive
			if(buildings[2].build.hp > 0) troops[order].troop.hp = troops[order].troop.hp - buildings[2].build.ap;
			
			System.out.println("Defense[2] HP Remaining: "+buildings[2].build.hp);
			System.out.println("Offense["+order+"] HP Remaining: "+troops[order].troop.hp);
			
			//Decrement counter of building or troop
			 if(buildings[2].build.hp <= 0){
				JOptionPane.showMessageDialog(null, "\nCLASH!!! WHO WINS???");
				headerLabel.setText("OFFENSE SUCCESSFUL!");
			 break;
			 }
			 else if(troops[order].troop.hp <= 0){
				JOptionPane.showMessageDialog(null, "\nCLASH!!! WHO WINS???");
				headerLabel.setText("DEFENSE WINS!");
				 order++;
				 if(order == 3){
					 headerLabel.setText("DEFENSE SUCCESSFUL!");
					 break;
				 }
			 }
			 
		 }
		 
		 */

	}
	
}
