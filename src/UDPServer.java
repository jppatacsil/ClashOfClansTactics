import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class UDPServer implements Runnable, Constants{
	
    DatagramSocket serverSocket = null;
    GameState game;
	Thread t = new Thread(this);
	String playerData;
	String[] reply;
	int playerCount = 0;
	int numPlayers;
	int gameStage=WAITING_FOR_PLAYERS;

	public UDPServer(int numPlayers){
		this.numPlayers = numPlayers;
		try {
            serverSocket = new DatagramSocket(PORT);
            serverSocket.setSoTimeout(100);
		} catch (IOException e) {
            System.err.println("Could not listen on port: "+PORT);
            System.exit(-1);
		}catch(Exception e){}
		game = new GameState();

		System.out.println("Game created...");
		
		//Start the game thread
		t.start();
	}
	
	//Method for sending packets to all the clients connected in the server
	public void broadcast(String msg){
		for(Iterator ite=game.getPlayers().keySet().iterator();ite.hasNext();){
			String name=(String)ite.next();
			Player player=(Player)game.getPlayers().get(name);			
			send(player,msg);	
		}
	}
	
	public void send(Player player, String msg){
		DatagramPacket packet;	
		byte buf[] = msg.getBytes();		
		packet = new DatagramPacket(buf, buf.length, player.getAddress(),player.getPort());
		try{
			serverSocket.send(packet);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(true){
			
			// Get the data from players
			byte[] buf = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try{
     			serverSocket.receive(packet);
			}catch(Exception ioe){}
			
			/**
			 * Convert the array of bytes to string
			 */
			playerData=new String(buf);
			
			//remove excess bytes
			playerData = playerData.trim();
			
			if (!playerData.equals("")){
				System.out.println("Player Data>"+playerData);
			}
			
			// process
			switch(gameStage){
				  case WAITING_FOR_PLAYERS: //Game state where server waits for players to connect
						if (playerData.startsWith("CONNECT")){
							String tokens[] = playerData.split(" ");
							Player player=new Player(tokens[1],packet.getAddress(),packet.getPort());
							System.out.println("Player connected: "+tokens[1]);
							game.update(tokens[1].trim(),player);
							broadcast("CONNECTED "+tokens[1]);
							playerCount++;
							if (playerCount==numPlayers){
								gameStage=GAME_START;
							}
						}
					  break;	
				  case GAME_START: //Game state where players are both connected
					  System.out.println("Game State: START");
					  broadcast("START GAME");
					  gameStage=IN_PROGRESS;
					  break;
				  case IN_PROGRESS: //Game stage where players begin clash
					  if(playerData.startsWith("OffensiveTactics")){ //If the server received Offensive/DefensiveTactics, return OFFENSE/DEFENSE GRANTED
						  	System.out.println("Offensive tactics received!\nPlacing troops in battle screen...");
							reply = playerData.split(" ");
							broadcast("Offense: "+reply[1]+" "+reply[2]+" "+reply[3]);
						}else if(playerData.startsWith("DefensiveTactics")){
							System.out.println("Defensive tactics received!\nPlacing buildings in battle screen...");
							reply = playerData.split(" ");
							broadcast("Defense: "+reply[1]+" "+reply[2]+" "+reply[3]);
						}
					  
					  if(playerData.startsWith("Reset")){
						  broadcast("Restarting game...");
						  System.out.println("Game State: END");
						  gameStage=GAME_END;//end the game
					  }
					  break;
				  case GAME_END: //Game stage where players disconnect from the server
					  System.out.println("Restarting game...");
					  gameStage=WAITING_FOR_PLAYERS;
					  playerCount = 0;
					  break;
			}
		}
	}
	
	
	//Main function
	public static void main(String args[]) throws Exception       
	{
		new UDPServer(2);
	}//End of main
}