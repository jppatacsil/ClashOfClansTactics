import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Game extends JFrame{
	
	String message; //The message that the client will send to the server
	String altMessage; //If packet was dropped
	
	//Static variables
	public static ChatRoom chatRoom;
	public static Game game;
	public static UDPClient gameClient;
	
	final JOptionPane optionPane = new JOptionPane("Preparing for battle...", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);

	final JDialog dialog = new JDialog();

	static boolean server_user = false;
	static String host;
	static String ip;
	static String username; //Player username
	static String serverPacket;
	static int port;
	static int engagementChoice;
	static int page = 1;
	
	CardLayout layout = new CardLayout(); //the layout
	
	static JPanel screens; //The card layout
	JPanel gameScreen; //the whole game panel
	JPanel formationScreen; //the card1, tactics page
	JPanel offensePane; //the offensive troops
	JPanel defensePane; //the defensive buildings

	Font font = new Font("Supercell-Magic", Font.BOLD,36);

	JLabel offenseLabel; //offensive tactics label
	JLabel defenseLabel; //defensive tactics label
	JPanel header; //the header of the game
	JLabel headerBG; //for header background

	JButton gameButton; //button to start game
	oFormation[] troops = new oFormation[3]; //offensive troops
	dFormation[] buildings = new dFormation[3]; //defensive buildings
	
	//The choices for tactics
	String[] troopChooser = { "Barbarians", "Archers", "Giants", "Balloons", "Wizards", "Dragons" };
	String[] defChooser = {"Cannons", "Archer Towers", "Mortars", "Air Defense", "Wizard Towers", "Tesla Towers"};
	
	switchListener switcher = new switchListener(); //The cardswitching listener
	planListener planner = new planListener(); //The button to show planning window
	
	private Game(){
		super("Clash of Clans: Tactics");
		
		//Ask for username
		username = JOptionPane.showInputDialog("Enter username");
		
		chatRoom = new ChatRoom(); //Instantiate the chatroom
		host = chatRoom.getHost(); //Get the host
		port = chatRoom.getPort(); //Get the port
		
		add(chatRoom, BorderLayout.WEST); //Place the chatroom at the west side
		
		//the whole gameScreen
		gameScreen = new JPanel();
		gameScreen.setLayout(new BorderLayout());
		gameScreen.setPreferredSize(new Dimension(700,700));
		add(gameScreen);
		
		//The screen that will be switching pages
		screens = new JPanel();
		screens.setLayout(layout);
		screens.setPreferredSize(new Dimension(700,500));
		
		//the instruction screen
		formationScreen = new JPanel();
		formationScreen.setPreferredSize(new Dimension(700,500));
		formationScreen.setLayout(new GridLayout(4,1));
		formationScreen.setBackground(Color.YELLOW);
		
		offensePane = new JPanel(new GridLayout(1,3));
		for(int i = 0; i < 3; i++){
			offensePane.add(troops[i] = new oFormation(i,0));
			troops[i].setBackground(Color.WHITE);
			troops[i].setFocusPainted(false);
			troops[i].addActionListener(planner);
		}
		
		offenseLabel = new JLabel("Offense Tactics",SwingConstants.CENTER);
		offenseLabel.setFont(font);
		
		defensePane = new JPanel(new GridLayout(1,3));
		for(int i = 0; i < 3; i++){
			defensePane.add(buildings[i] = new dFormation(i,0));
			buildings[i].setBackground(Color.WHITE);
			buildings[i].setFocusPainted(false);
			buildings[i].addActionListener(planner);
		}
		
		defenseLabel = new JLabel("Defense Tactics",SwingConstants.CENTER);
		defenseLabel.setFont(font);
		
		formationScreen.add(offenseLabel);
		formationScreen.add(offensePane);
		formationScreen.add(defenseLabel);
		formationScreen.add(defensePane);
		
		//The layout cards
		screens.add(formationScreen, "HOME PAGE");
		
		//The game button for start and quit
		gameButton = new JButton("ENGAGE!");
		gameButton.setFont(font);
		gameButton.setPreferredSize(new Dimension(700,100));
		gameButton.setFocusPainted(false);
		gameButton.setBackground(Color.BLACK);
		gameButton.setForeground(Color.WHITE);
		gameButton.addActionListener(switcher);
		
		//Set the banner of game
		header = new JPanel();
		header.setPreferredSize(new Dimension(700,100));
		ImageIcon banner = new ImageIcon(getClass().getResource("banner.jpg"));
		headerBG = new JLabel(banner);
		header.add(headerBG);
		
		//UI design layouting
		gameScreen.add(gameButton, BorderLayout.SOUTH);
		gameScreen.add(screens, BorderLayout.CENTER);
		gameScreen.add(header, BorderLayout.NORTH);

		//Housekeeping
		setSize(1000,700);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		game = new Game(); //Run the game
		
		chatRoom.setUsername(username);
		
		//Ask if run as server, 
		int choice = JOptionPane.showConfirmDialog(null,
				"Create room and run as host?", "YES OR NO?", JOptionPane.YES_NO_OPTION);
		
		if(choice == 0){
			server_user = true;
			new UDPServer(2); //Run the game server
			//new ManInTheMiddle(chatRoom.getIP());
			try {
				gameClient = new UDPClient(chatRoom.getIP(),username); //Instantiate the game client in the localhost
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			screens.add(gameClient, "BATTLE PAGE"); //Add to the card layout
		}else{ //Then run as client
			ip = JOptionPane.showInputDialog("Enter IP address of room server to join");
			try {
				gameClient = new UDPClient(ip,username); //Instantiate the game client
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			screens.add(gameClient, "BATTLE PAGE");
			chatRoom.setIP(ip);
		}
			
	}
	
	//For the switching of cards
	class switchListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			Object o = e.getSource();
			
			if(o == gameButton){
				//Check first if tactics formation is complete
				for(int i=0; i<3; i++){
					if(troops[i].troop.showName(troops[i].troop.type) == "Unknown"){
						JOptionPane.showMessageDialog(null, "Offensive Tactics formation not yet complete!");
						return;
					}
				}
				for(int i=0; i<3; i++){
					if(buildings[i].build.showName(buildings[i].build.type) == "Unknown"){
						JOptionPane.showMessageDialog(null, "Defensive Tactics formation not yet complete!");
						return;
					}
				}
				
				layout.next(screens); //switch screens
 				if(page == 1){
 				
 	 			gameButton.setEnabled(false); //prevent user from going back
 	 			
 				int order = 0;
 				page++; //Set for next page
 				gameButton.setText("Tactics Formation");
 	 			gameClient.getGame(game);
 	 			
 	 			if(gameClient.connected == false){ //Resume the thread
 	 				gameClient.t.resume();
 	 			}
 	 			
					dialog.setTitle("MESSAGE");
					dialog.setModal(true);

					dialog.setContentPane(optionPane);

					dialog.setLocationRelativeTo(null);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.pack();
 				
 				while(gameClient.headerLabel.getText() == "Waiting for players..."){
 					dialog.setVisible(true);
 				}
 				
 				//Show your tactics engagement onscreen
				//ASK IF DEFEND OR ATTACK
	 				Object[] options = {"I will Attack!",
		                    "I will Defend!",
		                    "Oops, not yet ready!"};
						 	engagementChoice = JOptionPane.showOptionDialog(null,
						    "What will you to do?",
						    "ENGAGEMENT CHOICES",
						    JOptionPane.YES_NO_CANCEL_OPTION,
						    JOptionPane.QUESTION_MESSAGE,
						    null,
						    options,
						    options[2]);

				if(engagementChoice == 0){//Send the offensive tactics
					//Get the game state packet
					
					//For offense
					message = "OffensiveTactics ";
					message += troops[0].troop.showName(troops[0].troop.type) + " ";
					message += troops[1].troop.showName(troops[1].troop.type) + " ";
					message += troops[2].troop.showName(troops[2].troop.type);
					
					//In case packet was dropped, send defensive tactics instead
					altMessage = "DefensiveTactics ";
					altMessage += buildings[0].build.showName(buildings[0].build.type) + " ";
					altMessage += buildings[1].build.showName(buildings[1].build.type) + " ";
					altMessage += buildings[2].build.showName(buildings[2].build.type);
					
					gameClient.sendMessage(message);
	
				}else if(engagementChoice == 1){//Send the defensive tactics
					//Get the game state packet
					
					//For defense
					message = "DefensiveTactics ";
					message += buildings[0].build.showName(buildings[0].build.type) + " ";
					message += buildings[1].build.showName(buildings[1].build.type) + " ";
					message += buildings[2].build.showName(buildings[2].build.type);
					
					//In case packet was dropped, send offensive tactics instead
					altMessage = "OffensiveTactics ";
					altMessage += troops[0].troop.showName(troops[0].troop.type) + " ";
					altMessage += troops[1].troop.showName(troops[1].troop.type) + " ";
					altMessage += troops[2].troop.showName(troops[2].troop.type);
					
					gameClient.sendMessage(message);
				}
				
				else{ //Go back to tactics formation
					gameButton.setEnabled(true);
					return;
				}
 				
 				}else{ //Go back to tactics formation and reset tactics
 					page--;
 					for(int i=0; i<3; i++){
 						troops[i].troop.initTroop(0);
 						troops[i].setTroop(0);
 						troops[i].instantiate(0);
 						buildings[i].build.initD(0);
 						buildings[i].setD(0);
 						buildings[i].instantiate(0);
 					}
 					gameClient.offenseTaken = false;
 					gameClient.defenseTaken = false;
 					gameClient.connected = false;
 					gameClient.oPortrait.setIcon(gameClient.oAvatar[0]);
 					gameClient.dPortrait.setIcon(gameClient.dAvatar[0]);
 					gameClient.headerLabel.setText("Waiting for players...");
 					gameClient.sendMessage("Reset game");
 					
 					gameButton.setText("Engage!");
 				}
			}
		}
}
	
	class planListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			Object o = e.getSource();
			
			if(o == troops[0] || o == troops[1] || o == troops[2]){ //If oFormation was clicked, show window for troops
			    String input = (String) JOptionPane.showInputDialog(null, "Choose now...",
			        "Choose which troop to attack?", JOptionPane.QUESTION_MESSAGE, null, // Use
			                                                                        // default
			                                                                        // icon
			        troopChooser, // Array of choices
			        null); // Initial choice
			    
			    if(input == null){ return; }
			    
			    System.out.println("Player has chosen "+input);
			    
			    //Change the troops formation
			    switch(input){
				    case "Barbarians":
				    	if(o == troops[0]){
				    		troops[0].setTroop(1);
				    		troops[0].instantiate(1);
				    		System.out.print("HP: ");
					    	System.out.println(troops[0].troop.hp);
					    	System.out.print("AP: ");
					    	System.out.println(troops[0].troop.ap);
					    	//troopChooser[0] = null;
				    	}
				    	else if(o == troops[1]){ 
				    		troops[1].setTroop(1);
				    		troops[1].instantiate(1);
				    		System.out.print("HP: ");
					    	System.out.println(troops[1].troop.hp);
					    	System.out.print("AP: ");
					    	System.out.println(troops[1].troop.ap);
					    	//troopChooser[0] = null;
				    	}
				    	else{ 
				    		troops[2].setTroop(1);
				    		troops[2].instantiate(1);
				    		System.out.print("HP: ");
					    	System.out.println(troops[2].troop.hp);
					    	System.out.print("AP: ");
					    	System.out.println(troops[2].troop.ap);
					    	//troopChooser[0] = null;
				    	}
				    	break;
				    	
				    case "Archers":
				    	if(o == troops[0]){
				    		troops[0].setTroop(2);
				    		troops[0].instantiate(2);
				    		System.out.print("HP: ");
					    	System.out.println(troops[0].troop.hp);
					    	System.out.print("AP: ");
					    	System.out.println(troops[0].troop.ap);
					    	//troopChooser[1] = null;
				    	}
				    	else if(o == troops[1]){ 
				    		troops[1].setTroop(2);
				    		troops[1].instantiate(2);
				    		System.out.print("HP: ");
					    	System.out.println(troops[1].troop.hp);
					    	System.out.print("AP: ");
					    	System.out.println(troops[1].troop.ap);
					    	//troopChooser[1] = null;
				    	}
				    	else{ 
				    		troops[2].setTroop(2);
				    		troops[2].instantiate(2);
				    		System.out.print("AP: ");
					    	System.out.println(troops[2].troop.hp);
					    	System.out.print("AP: ");
					    	System.out.println(troops[2].troop.ap);
					    	//troopChooser[1] = null;
				    	}
				    	break;
				    case "Giants":
				    	if(o == troops[0]){
				    		troops[0].setTroop(3);
				    		troops[0].instantiate(3);
				    		System.out.print("HP: ");
					    	System.out.println(troops[0].troop.hp);
					    	System.out.print("AP: ");
					    	System.out.println(troops[0].troop.ap);
					    	//troopChooser[2] = null;
				    	}
				    	else if(o == troops[1]){ 
				    		troops[1].setTroop(3);
				    		troops[1].instantiate(3);
				    		System.out.print("HP: ");
					    	System.out.println(troops[1].troop.hp);
					    	System.out.print("AP: ");
					    	System.out.println(troops[1].troop.ap);
					    	//troopChooser[2] = null;
				    	}
				    	else{ 
				    		troops[2].setTroop(3);
				    		troops[2].instantiate(3);
				    		System.out.print("HP: ");
					    	System.out.println(troops[2].troop.hp);
					    	System.out.print("AP: ");
					    	System.out.println(troops[2].troop.ap);
					    	//troopChooser[2] = null;
				    	}
				    	break;
				    case "Balloons":
				    	if(o == troops[0]){
				    		troops[0].setTroop(4);
				    		troops[0].instantiate(4);
				    		System.out.print("HP: ");
					    	System.out.println(troops[0].troop.hp);
					    	System.out.print("AP: ");
					    	System.out.println(troops[0].troop.ap);
					    	//troopChooser[3] = null;
				    	}
				    	else if(o == troops[1]){ 
				    		troops[1].setTroop(4);
				    		troops[1].instantiate(4);
				    		System.out.print("HP: ");
					    	System.out.println(troops[1].troop.hp);
					    	System.out.print("AP: ");
					    	System.out.println(troops[1].troop.ap);
					    	//troopChooser[3] = null;
				    	}
				    	else{ 
				    		troops[2].setTroop(4);
				    		troops[2].instantiate(4);
				    		System.out.print("HP: ");
					    	System.out.println(troops[2].troop.hp);
					    	System.out.print("AP: ");
					    	System.out.println(troops[2].troop.ap);
					    	//troopChooser[3] = null;
				    	}
				    	break;
				    case "Wizards":
				    	if(o == troops[0]){
				    		troops[0].setTroop(5);
				    		troops[0].instantiate(5);
				    		System.out.print("HP: ");
					    	System.out.println(troops[0].troop.hp);
					    	System.out.print("AP: ");
					    	System.out.println(troops[0].troop.ap);
					    	//troopChooser[4] = null;
				    	}
				    	else if(o == troops[1]){ 
				    		troops[1].setTroop(5);
				    		troops[1].instantiate(5);
				    		System.out.print("HP: ");
					    	System.out.println(troops[1].troop.hp);
					    	System.out.print("AP: ");
					    	System.out.println(troops[1].troop.ap);
					    	//troopChooser[4] = null;
				    	}
				    	else{ 
				    		troops[2].setTroop(5);
				    		troops[2].instantiate(5);
				    		System.out.print("HP: ");
					    	System.out.println(troops[2].troop.hp);
					    	System.out.print("AP: ");
					    	System.out.println(troops[2].troop.ap);
					    	//troopChooser[4] = null;
				    	}
				    	break;
				    case "Dragons":
				    	if(o == troops[0]){
				    		troops[0].setTroop(6);
				    		troops[0].instantiate(6);
				    		System.out.print("HP: ");
					    	System.out.println(troops[0].troop.hp);
					    	System.out.print("AP: ");
					    	System.out.println(troops[0].troop.ap);
					    	//troopChooser[5] = null;
				    	}
				    	else if(o == troops[1]){ 
				    		troops[1].setTroop(6);
				    		troops[1].instantiate(6);
				    		System.out.print("HP: ");
					    	System.out.println(troops[1].troop.hp);
					    	System.out.print("AP: ");
					    	System.out.println(troops[1].troop.ap);
					    	//troopChooser[5] = null;
				    	}
				    	else{ 
				    		troops[2].setTroop(6);
				    		troops[2].instantiate(6);
				    		System.out.print("HP: ");
					    	System.out.println(troops[2].troop.hp);
					    	System.out.print("AP: ");
					    	System.out.println(troops[2].troop.ap);
					    	//troopChooser[5] = null;
				    	}
				    	break;
			    }
			    
			}
			
			if(o == buildings[0] || o == buildings[1] || o == buildings[2]){ //If dFormation was clicked, show window for buildings
			    String input = (String) JOptionPane.showInputDialog(null, "Choose now...",
				        "Choose which building to defend?", JOptionPane.QUESTION_MESSAGE, null, // Use
				                                                                        // default
				                                                                        // icon
				        defChooser, // Array of choices
				        null); // Initial choice

				    if(input == null) { return; }
				    
				    System.out.println("Player has chosen "+input); 
				    
				  //Change the defensive buildings formation
				    switch(input){
					    case "Cannons":
					    	if(o == buildings[0]){
					    		buildings[0].setD(1);
					    		buildings[0].instantiate(1);
					    		System.out.print("HP: ");
						    	System.out.println(buildings[0].build.hp);
						    	System.out.print("AP: ");
						    	System.out.println(buildings[0].build.ap);
						    	//defChooser[0] = null;
					    	}
					    	else if(o == buildings[1]){ 
					    		buildings[1].setD(1);
					    		buildings[1].instantiate(1);
					    		System.out.print("HP: ");
						    	System.out.println(buildings[1].build.hp);
						    	System.out.print("AP: ");
						    	System.out.println(buildings[1].build.ap);
						    	//defChooser[0] = null;
					    	}
					    	else{ 
					    		buildings[2].setD(1);
					    		buildings[2].instantiate(1);
					    		System.out.print("HP: ");
						    	System.out.println(buildings[2].build.hp);
						    	System.out.print("AP: ");
						    	System.out.println(buildings[2].build.ap);
						    	//defChooser[0] = null;
					    	}
					    	break;
					    case "Archer Towers":
					    	if(o == buildings[0]){
					    		buildings[0].setD(2);
					    		buildings[0].instantiate(2);
					    		System.out.print("HP: ");
						    	System.out.println(buildings[0].build.hp);
						    	System.out.print("AP: ");
						    	System.out.println(buildings[0].build.ap);
						    	//defChooser[1] = null;
					    	}
					    	else if(o == buildings[1]){ 
					    		buildings[1].setD(2);
					    		buildings[1].instantiate(2);
					    		System.out.print("HP: ");
						    	System.out.println(buildings[1].build.hp);
						    	System.out.print("AP: ");
						    	System.out.println(buildings[1].build.ap);
						    	//defChooser[1] = null;
					    	}
					    	else{ 
					    		buildings[2].setD(2);
					    		buildings[2].instantiate(2);
					    		System.out.print("HP: ");
						    	System.out.println(buildings[2].build.hp);
						    	System.out.print("AP: ");
						    	System.out.println(buildings[2].build.ap);
						    	//defChooser[1] = null;
					    	}
					    	break;
					    case "Mortars":
					    	if(o == buildings[0]){
					    		buildings[0].setD(3);
					    		buildings[0].instantiate(3);
					    		System.out.print("HP: ");
						    	System.out.println(buildings[0].build.hp);
						    	System.out.print("AP: ");
						    	System.out.println(buildings[0].build.ap);
						    	//defChooser[2] = null;
					    	}
					    	else if(o == buildings[1]){ 
					    		buildings[1].setD(3);
					    		buildings[1].instantiate(3);
					    		System.out.print("HP: ");
						    	System.out.println(buildings[1].build.hp);
						    	System.out.print("AP: ");
						    	System.out.println(buildings[1].build.ap);
						    	//defChooser[2] = null;
					    	}
					    	else{ 
					    		buildings[2].setD(3);
					    		buildings[2].instantiate(3);
					    		System.out.print("HP: ");
						    	System.out.println(buildings[2].build.hp);
						    	System.out.print("AP: ");
						    	System.out.println(buildings[2].build.ap);
						    	//defChooser[2] = null;
					    	}
					    	break;
					    case "Air Defense":
					    	if(o == buildings[0]){
					    		buildings[0].setD(4);
					    		buildings[0].instantiate(4);
					    		System.out.print("HP: ");
						    	System.out.println(buildings[0].build.hp);
						    	System.out.print("AP: ");
						    	System.out.println(buildings[0].build.ap);
						    	//defChooser[3] = null;
					    	}
					    	else if(o == buildings[1]){ 
					    		buildings[1].setD(4);
					    		buildings[1].instantiate(4);
					    		System.out.print("HP: ");
						    	System.out.println(buildings[1].build.hp);
						    	System.out.print("AP: ");
						    	System.out.println(buildings[1].build.ap);
						    	//defChooser[3] = null;
					    	}
					    	else{ 
					    		buildings[2].setD(4);
					    		buildings[2].instantiate(4);
					    		System.out.print("HP: ");
						    	System.out.println(buildings[2].build.hp);
						    	System.out.print("AP: ");
						    	System.out.println(buildings[2].build.ap);
						    	//defChooser[3] = null;
					    	}
					    	break;
					    case "Wizard Towers":
					    	if(o == buildings[0]){
					    		buildings[0].setD(5);
					    		buildings[0].instantiate(5);
					    		System.out.print("HP: ");
						    	System.out.println(buildings[0].build.hp);
						    	System.out.print("AP: ");
						    	System.out.println(buildings[0].build.ap);
						    	//defChooser[4] = null;
					    	}
					    	else if(o == buildings[1]){ 
					    		buildings[1].setD(5);
					    		buildings[1].instantiate(5);
					    		System.out.print("HP: ");
						    	System.out.println(buildings[1].build.hp);
						    	System.out.print("AP: ");
						    	System.out.println(buildings[1].build.ap);
						    	//defChooser[4] = null;
					    	}
					    	else{ 
					    		buildings[2].setD(5);
					    		buildings[2].instantiate(5);
					    		System.out.print("HP: ");
						    	System.out.println(buildings[2].build.hp);
						    	System.out.print("AP: ");
						    	System.out.println(buildings[2].build.ap);
						    	//defChooser[4] = null;
					    	}
					    	break;
					    case "Tesla Towers":
					    	if(o == buildings[0]){
					    		buildings[0].setD(6);
					    		buildings[0].instantiate(6);
					    		System.out.print("HP: ");
						    	System.out.println(buildings[0].build.hp);
						    	System.out.print("AP: ");
						    	System.out.println(buildings[0].build.ap);
						    	//defChooser[5] = null;
					    	}
					    	else if(o == buildings[1]){ 
					    		buildings[1].setD(6);
					    		buildings[1].instantiate(6);
					    		System.out.print("HP: ");
						    	System.out.println(buildings[1].build.hp);
						    	System.out.print("AP: ");
						    	System.out.println(buildings[1].build.ap);
						    	//defChooser[5] = null;
					    	}
					    	else{ 
					    		buildings[2].setD(6);
					    		buildings[2].instantiate(6);
					    		System.out.print("HP: ");
						    	System.out.println(buildings[2].build.hp);
						    	System.out.print("AP: ");
						    	System.out.println(buildings[2].build.ap);
						    	//defChooser[5] = null;
					    	}
					    	break;
				    }
			
		}
		
	}
	}
}