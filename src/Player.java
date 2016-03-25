import java.net.InetAddress;

public class Player {

	private InetAddress address;
	private int port;

	private String name;
	
	private oFormation[] troops = new oFormation[3]; //offensive troops
	private dFormation[] buildings = new dFormation[3]; //defensive buildings
	
	public Player(String name,InetAddress address,int port){
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
}
