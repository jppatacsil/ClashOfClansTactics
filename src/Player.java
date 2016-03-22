import java.net.InetAddress;

public class Player {

	private InetAddress address;
	private int port;
	private int playerSide;
	private String name;
	private oFormation[] troops = new oFormation[3]; //offensive troops
	private dFormation[] buildings = new dFormation[3]; //defensive buildings
	
	public Player(String name,InetAddress address, int port){
		this.address = address;
		this.port = port;
		this.name = name;
	}

	public InetAddress getAddress(){
		return address;
	}
	
	public int getPort(){
		return port;
	}

	public String getName(){
		return name;
	}
	
	public String toString(){
		String retval="";
		retval+=playerSide+",";
		retval+=name+",";
		if(playerSide==0){ //Offense
		retval+=troops[0]+",";
		retval+=troops[1]+",";
		retval+=troops[2]+",";
		}else{				//Defense
		retval+=buildings[0]+",";
		retval+=buildings[1]+",";
		retval+=buildings[2]+",";
		}
		return retval;
	}	
}
