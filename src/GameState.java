import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GameState{
	//This is a map(key-value pair) of <Username,Player>
	private Map players=new HashMap();
	
	//Empty constructor
	public GameState(){}
	

	//Update the game state upon action of player
	public void update(String name, Player player){
		players.put(name,player);
	}

	//Gets the list of players connected
	public Map getPlayers(){
		return players;
	}
}
