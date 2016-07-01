import java.awt.Dimension;
import java.awt.TextArea;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JFrame;

public class ManInTheMiddle extends JFrame implements Runnable{
	public static String sentence2;
	public static TextArea text = new TextArea();
	public static JFrame frame = new JFrame();
	public static String address;
	Thread t = new Thread(this);
	
	//Constructor
	public ManInTheMiddle(String address){
		this.address = address;
		System.out.println("ManInTheMiddle running...");
		t.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		DatagramSocket clientSocket = null;
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket(9876);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		InetAddress IPAddress2 = null;
		try {
			IPAddress2 = InetAddress.getByName(this.address);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    byte[] receiveData = new byte[1024];             
		byte[] sendData = new byte[1024];
		
		while(true){
		DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
		try {
			serverSocket.receive(receivePacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sentence2= new String( receivePacket.getData());
		InetAddress IPAddress = receivePacket.getAddress();
		int port = receivePacket.getPort();
		String capitalizedSentence = sentence2.toUpperCase();
		sendData = capitalizedSentence.getBytes();       
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);                   
		try {
			serverSocket.send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(sentence2);
		DatagramPacket sendPacket1 = new DatagramPacket(sendData, sendData.length, IPAddress2, 9876);  
		try {
			clientSocket.send(sendPacket1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		DatagramPacket receivePacket1 = new DatagramPacket(receiveData, receiveData.length);    
		try {
			clientSocket.receive(receivePacket1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
		String modifiedSentence = new String(receivePacket1.getData());    
		//clientSocket.close();
		runTextWindow(sentence2,IPAddress,IPAddress2);
		}
	}
	
	public static void main(String args[]) throws Exception {
		new ManInTheMiddle("192.168.0.13");
	}
		
	public static void runTextWindow(String sentence2,InetAddress IPAddress, InetAddress IPAddress2){
		text.setEditable(false);
		text.setPreferredSize(new Dimension(500,500));
		text.append("RECEIVED FROM: "+IPAddress);
		text.append("\nTACTICS:"+sentence2);
		text.append("SEND TO:"+IPAddress2);
		frame.add(text);
		frame.setPreferredSize(new Dimension(500,500));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.pack();
	}

}
