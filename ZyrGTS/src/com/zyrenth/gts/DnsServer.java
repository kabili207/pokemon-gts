package com.zyrenth.gts;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

public class DnsServer implements Runnable
{
	
	private int port = 53, maxConnections = 0;
	
	// Listen for incoming connections and handle them
	public void run()
	{
		int i = 0;
		System.out.println("Starting DNS Server");
		try
		{
			//DatagramSocket dnsSock;
			InetAddress googleDns = InetAddress.getByName("8.8.8.8");
			InetAddress localAddr = null;
			
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			for (NetworkInterface netint : Collections.list(nets))
			{
				Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
				for (InetAddress inetAddress : Collections.list(inetAddresses)) {
					if(!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
						localAddr = inetAddress;
				}
			}

			
			DatagramSocket listener = new DatagramSocket(port);
			listener.setReuseAddress(true);
			
			// TODO: Send event on DNS start
			System.out.println("DNS Listening on: " + localAddr.getHostAddress());
			
			while ((i++ < maxConnections) || (maxConnections == 0))
			{
				byte[] buffer = new byte[512];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				
				
				byte[] buffer2 = new byte[512];
				DatagramPacket packet2 = new DatagramPacket(buffer2, buffer2.length);
				
				listener.receive(packet);
				
				//System.out.println(packet.getAddress().getHostAddress());
				byte[] r = packet.getData();
				
				
				DatagramSocket datagramSocket = new DatagramSocket();
				DatagramPacket realDns = new DatagramPacket(r, r.length, googleDns, port);
				
				datagramSocket.send(realDns);
				datagramSocket.receive(packet2);
				
				byte[] rr = Arrays.copyOfRange(buffer2, 0, packet2.getLength());
				
				String resp = new String(rr);

				if(resp.contains("pkvldtprod"))
				{
					// Requested domain is https://pkvldtprod.nintendo.co.jp/
					// DS is attempting to verifying the validity of a pokemon.
					// TODO: Send an event
				}
				
				if(resp.contains("gamestats2"))
				{
					
					byte[] addrByte = localAddr.getAddress();
					//byte[] addrByte = InetAddress.getByName("192.168.1.50").getAddress();
					rr[rr.length -4] = addrByte[0];
					rr[rr.length -3] = addrByte[1];
					rr[rr.length -2] = addrByte[2];
					rr[rr.length -1] = addrByte[3];
				}
				
				DatagramPacket response = new DatagramPacket(rr, rr.length, packet.getAddress(), packet.getPort());
				listener.send(response);
				
			}
			// TODO: Send event on DNS stop
			System.out.println("Stopping DNS");
		}
		catch (IOException ioe)
		{
			System.out.println("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
		}
	}
	
	
}
