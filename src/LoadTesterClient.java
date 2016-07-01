import java.io.*;
import java.net.*;
 class LoadTesterClient { 

	public void tester(String sentence, String address) throws Exception    {
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));       
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(address);       
		
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024]; 
		
		sendData = sentence.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);  
		clientSocket.send(sendPacket); 
		
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);    
		clientSocket.receive(receivePacket);    
		String modifiedSentence = new String(receivePacket.getData());    
		
		} 
	
} 