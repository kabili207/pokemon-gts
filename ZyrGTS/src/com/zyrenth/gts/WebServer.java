package com.zyrenth.gts;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

public class WebServer implements Runnable
{
	private static int port = 80, maxConnections = 0;
	private DnsServer dnsServer;
	private Thread dnsThread;
	
	public WebServer()
	{
	}
	
	// Listen for incoming connections and handle them
	public void run()
	{
		
		int i = 0;
		
		System.out.println("Starting Web Server");
		try
		{
			//dnsServer = new DnsServer();
			//dnsThread = new Thread(dnsServer);
			//dnsThread.start();
			
			ServerSocket listener = new ServerSocket(port);
			Socket server;
			
			while ((i++ < maxConnections) || (maxConnections == 0))
			{
				doComms connection;
				
				server = listener.accept();
				System.out.println("Connection established");
				doComms conn_c = new doComms(server);
				Thread t = new Thread(conn_c);
				t.start();
			}
		}
		catch (IOException ioe)
		{
			System.out.println("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
		}
		System.out.println("Stopping Web");
	}
	
	class doComms implements Runnable
	{
		private Socket server;
		private String line, input;
		
		doComms(Socket server)
		{
			this.server = server;
		}
		
		public void run()
		{
			System.out.println("Running web thread");
			input = "";
			
			try
			{
				// Get input from the client
				BufferedReader d
		          = new BufferedReader(new InputStreamReader(server
							.getInputStream()));
				PrintStream out = new PrintStream(server.getOutputStream());
				

			
				while ((line = d.readLine()) != null && d.ready())
				{
					input = input + line + "\r\n";
					// out.println("I got:" + line);
				}

				// Now write to the client
				
				Request req = new Request(input);
				if(req.isValid()){
					System.out.println(req.getPage());
					//System.out.println(req.getAction());
					
					for(Map.Entry<String,String> kvp : req.getVariables().entrySet())
					{
					//	System.out.println(kvp.getKey() + " = " + kvp.getValue());
					}
					
					if(req.getVariables().size() == 1)
					{
						Response resp = new Response("c9KcX1Cry3QKS2Ai7yxL6QiQGeBGeQKR");
						//System.out.println(resp.toString());
						out.print(resp.toString());
						out.flush();
					} else {
						String action = req.getAction();
						String resp = "";
						if(action.equals("info"))
							resp = "\u0001\u0000";
						else if(action.equals("setProfile"))
							resp = "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000";
						else if(action.equals("delete"))
							resp = "\u0001\u0000";
						else if(action.equals("search"))
							resp = "\u0001\u0000";
						else if(action.equals("result"))
							resp = "\u0005\u0000";
						else if(action.equals("post"))
						{
							resp = "\u000c\u0000";
							String data = req.getVariables().get("data");
							byte[] bytes = Helper.b64Decode(data);
							byte[] decrypt = Pokemon.makePkm(bytes);
							FileOutputStream fos = new FileOutputStream("/tmp/test.pkm");

						    fos.write(decrypt);
							
						}
						
						try
						{
							
							String hash = "HZEdGCzcGGLvguqUEKQN" +
							Helper.b64Encode(resp) +
							"HZEdGCzcGGLvguqUEKQN";
							hash = Helper.sha1(hash);
							System.out.println(hash);
							resp += hash;
							Response resp2 = new Response(resp);
							//System.out.println(resp.toString());
							out.print(resp2.toString());
						}
						catch (NoSuchAlgorithmException e)
						{
							System.out.println("No algorithm");
						}
						catch (Exception e)
						{
						}

					}
				}
				
				//System.out.println("Overall message is:" + input);
				// out.println("Overall message is:" + input);
				
				server.close();
			}
			catch (IOException ioe)
			{
				System.out.println("IOException on socket listen: " + ioe);
				ioe.printStackTrace();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}