import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GameState{
	//This is a map(key-value pair) of <player name,NetPlayer>
	private Map players=new HashMap();
	
	//Empty constructor
	public GameState(){}
	

	//Update the game state upon action of player
	public void update(String name, Player player){
		players.put(name,player);
	}
	
	
	//String representation of the players
	public String toString(){
		String retval="";
		for(Iterator ite=players.keySet().iterator();ite.hasNext();){
			String name=(String)ite.next();
			Player player=(Player)players.get(name);
			retval+=player.toString()+":";
		}
		return retval;
	}

	//Gets the list of players connected
	public Map getPlayers(){
		return players;
	}
}
