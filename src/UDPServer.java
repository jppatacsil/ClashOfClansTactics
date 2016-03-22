import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class UDPServer implements Constants{
	
	static int gameStage=WAITING_FOR_PLAYERS;
	
	public UDPServer(){}
	
	//Main function
	public static void main(String args[]) throws Exception       
	{
		
		DatagramSocket serverSocket = new DatagramSocket(9876);             
		byte[] receiveData = new byte[1024];             
		byte[] sendData = new byte[1024];
		
		System.out.println("Game created...");
		
		//Keep receiving packets
		while(true)
		{   
			//Receive the packet sent by client to the server
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);          
			serverSocket.receive(receivePacket);                   
			String sentence = new String( receivePacket.getData());         
			System.out.println("RECEIVED BY SERVER: " + sentence.trim()); //Display the packet received

			//Trace where the packet was sent
			InetAddress IPAddress = receivePacket.getAddress();           
			int port = receivePacket.getPort();              
			String capitalizedSentence = sentence.toUpperCase();
			
			//Send the packet to the other client connected in the server
			sendData = capitalizedSentence.getBytes();                   
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);                   
			serverSocket.send(sendPacket);
			
			}
		}//End of main
}