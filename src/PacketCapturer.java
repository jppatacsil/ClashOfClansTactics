import java.util.ArrayList;
import java.util.List;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

public class PacketCapturer {
	
	private static int lastValue1;					//For the tcp line
	private static int lastValue1_2;
	
	public static void main(String[] args) {
		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
		StringBuilder errbuf = new StringBuilder(); // For any error msgs
		int r = Pcap.findAllDevs(alldevs, errbuf);
		if (r != Pcap.OK || alldevs.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s",
					errbuf.toString());
			return;
		}
		System.out.println("Network devices found:");
		int i = 0;
		for (PcapIf device : alldevs) {
			String description = (device.getDescription() != null) ? device
					.getDescription() : "No description available";
			System.out.printf("#%d: %s [%s]\n", i++, device.getName(),
					description);
		}
		PcapIf device = alldevs.get(0); // Get first device in list
		System.out.printf("\nChoosing '%s' on your behalf:\n",
				(device.getDescription() != null) ? device.getDescription()
						: device.getName());
		int snaplen = 64 * 1024; // Capture all packets, no trucation
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 10 * 1000; // 10 seconds in millis
		Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
		if (pcap == null) {
			System.err.printf("Error while opening device for capture: "
					+ errbuf.toString());
			return;
		}
		PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {
			public void nextPacket(PcapPacket packet, String user) {
				Tcp tcp = new Tcp();
            	Ip4 ip = new Ip4();
            	Udp udp = new Udp();
            	int tcpSizeIn = 0;
            	int tcpSizeOut = 0;
            	int udpSizeIn = 0;
            	int udpSizeOut = 0;
            	String sourceIp;
            	String destIp;
            	if(packet.hasHeader(ip) == true){
            		sourceIp = FormatUtils.ip(ip.source());
        			destIp = FormatUtils.ip(ip.destination());
            		
    		if(packet.hasHeader(tcp) == true){
            			
            			if(tcp.destination() == 1500){ //inbound packets
	        				System.out.println("----------------------------------------------");
	            			//System.out.println("*** Packet " + ++packetSniffer.i + " :");
	            			System.out.println("Source Ip : " + sourceIp);
	    					System.out.println("Destination Ip : " + destIp);
	            			System.out.println("Source Port : " + tcp.source());
	            			System.out.println("Destination Port : " + tcp.destination());
	            			System.out.println("----------------------------------------------");
	            			tcpSizeIn = packet.getPacketWirelen();
	            			System.out.println("Size : " + tcpSizeIn);
	            			//tcpSizeIn -= 66;
	            			//tcpSizeVariables.set(ipTable.get(sourceIp),tcpSizeIn);
	            		}
	            		
	            		if(tcp.source() == 1500){ //outbound packets
	            			System.out.println("----------------------------------------------");
	            			//System.out.println("*** Packet " + ++packetSniffer.i + " :");
	            			System.out.println("Source Ip : " + sourceIp);
	    					System.out.println("Destination Ip : " + destIp);
	            			System.out.println("Source Port : " + tcp.source());
	            			System.out.println("Destination Port : " + tcp.destination());
	            			System.out.println("----------------------------------------------");
	            			tcpSizeOut = packet.getPacketWirelen();
	            			System.out.println("Size : " + tcpSizeOut);
	            			//tcpSizeOut -= 66;
	            			//tcpSizeVariables2.set(ipTable.get(sourceIp),tcpSizeIn);
	            		}
	            		
            		}
            		
            		if(packet.hasHeader(udp) == true){
	            		if(udp.destination() == 9876){ //inbound packets
	            			System.out.println("----------------------------------------------");
	            			//System.out.println("*** Packet " + ++packetSniffer.i + " :");
	            			System.out.println("Source Ip : " + sourceIp);
	    					System.out.println("Destination Ip : " + destIp);
	            			System.out.println("Source Port : " + udp.source());
	            			System.out.println("Destination Port : " + udp.destination());
	            			System.out.println("----------------------------------------------");
	            			udpSizeIn = packet.getPacketWirelen();
	            			System.out.println("Size : " + udpSizeIn);
	            			//udpSizeIn -= 66;
	            			//udpSizeVariables.set(ipTable.get(sourceIp),tcpSizeIn);
	            		}
	            		
	            		if(udp.source() == 9876){ //outbound packets
	            			System.out.println("----------------------------------------------");
	            			//System.out.println("*** Packet " + ++packetSniffer.i + " :");
	            			System.out.println("Source Ip : " + sourceIp);
	    					System.out.println("Destination Ip : " + destIp);
	            			System.out.println("Source Port : " + udp.source());
	            			System.out.println("Destination Port : " + udp.destination());
	            			System.out.println("----------------------------------------------");
	            			udpSizeOut = packet.getPacketWirelen();
	            			System.out.println("Size : " + udpSizeOut);
	            			//udpSizeOut -= 66;
	            			//udpSizeVariables2.set(ipTable.get(sourceIp),tcpSizeIn);
	            		}
            		}	
            		
            		lastValue1 = tcpSizeIn + udpSizeIn;
            		lastValue1_2 = tcpSizeOut + udpSizeOut;
            	}
            }  
        };  
  
		// capture first 10 packages
		pcap.loop(Pcap.LOOP_INFINITE, jpacketHandler, "jNetPcap");
		pcap.close();
	}
}
