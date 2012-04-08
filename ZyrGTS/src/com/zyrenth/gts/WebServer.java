package com.zyrenth.gts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class WebServer implements Runnable
{
	private static int port = 80, maxConnections = 0;
	
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
			// dnsServer = new DnsServer();
			// dnsThread = new Thread(dnsServer);
			// dnsThread.start();
			
			ServerSocket listener = new ServerSocket(port);
			Socket server;
			
			// TODO: Send an event on web start
			
			while ((i++ < maxConnections) || (maxConnections == 0))
			{				
				server = listener.accept();
				//System.out.println("Connection established");
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
		
		// TODO: Send an event on web stop
		System.out.println("Stopping Web");
	}
	
	private List<WebEventListener> _listeners =
		new ArrayList<WebEventListener>();
	
	public synchronized void addEventListener(WebEventListener listener)
	{
		_listeners.add(listener);
	}
	
	public synchronized void removeEventListener(
			WebEventListener listener)
	{
		_listeners.remove(listener);
	}
	
	// call this method whenever you want to notify
	// the event listeners of the particular event
	private synchronized void firePokemonReceivedEvent(Pokemon pkm, String pid)
	{
		//System.out.println("Fireing event");
		PokemonReceivedEvent event = new PokemonReceivedEvent(this, pkm, pid);
		Iterator<WebEventListener> i = _listeners.iterator();
		while (i.hasNext())
		{
			//System.out.println("Sending event");
			i.next().onPokemonReceived(event);
		}
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
			//System.out.println("Running web thread");
			input = "";
			
			try
			{
				// Get input from the client
				BufferedReader d = new BufferedReader(new InputStreamReader(
						server.getInputStream()));
				
				while ((line = d.readLine()) != null && d.ready())
				{
					input = input + line + "\r\n";
					// out.println("I got:" + line);
				}
				
				// Now write to the client
				//System.out.println(input);
				
				Request req = new Request(input);
				if (req.isValid())
				{
					 //System.out.println(req.getPage());
					// System.out.println(req.getAction());
					
					//for (Map.Entry<String, String> kvp : req.getVariables()
					//		.entrySet())
					//{
						// System.out.println(kvp.getKey() + " = " +
						// kvp.getValue());
					//}
					if (req.getGeneration() == Helper.Generation.V)
						processGenV(req, server);
					
				}
				
				// System.out.println("Overall message is:" + input);
				// out.println("Overall message is:" + input);
				
				server.close();
			}
			catch (IOException ioe)
			{
				// TODO Send error event
				System.out.println("IOException on socket listen: " + ioe);
				ioe.printStackTrace();
			}
			catch (Exception e)
			{
				// TODO Send error event
				System.out.println("IOException on socket listen: " + e);
				e.printStackTrace();
			}
			//System.out.println("Ending web thread");
		}
		
		private void processGenV(Request req, Socket server) throws Exception
		{
			PrintStream out = new PrintStream(server.getOutputStream());
			if (req.getVariables().size() == 1)
			{
				
				Response resp = new Response("c9KcX1Cry3QKS2Ai7yxL6QiQGeBGeQKR");
				// System.out.println(resp.toString());
				out.print(resp.toString());
				out.flush();
			}
			else
			{
				String action = req.getAction();
				String resp = "";
				if (action.equals("info"))
					resp = "\u0001\u0000";
				else if (action.equals("setProfile"))
					resp = "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000";
				else if (action.equals("delete"))
					resp = "\u0001\u0000";
				else if (action.equals("search"))
					resp = "\u0001\u0000";
				else if (action.equals("result"))
					resp = "\u0005\u0000";
				else if (action.equals("post"))
				{
					String data = req.getVariables().get("data");
					byte[] bytes = Helper.b64Decode(data);
					byte[] decrypt = Pokemon.makePkm(bytes);
					//FileOutputStream fos = new FileOutputStream("/tmp/test.pkm");
					
					//fos.write(decrypt);

					Pokemon p = new Pokemon(decrypt);

					firePokemonReceivedEvent(p, req.getVariables().get("pid"));
					// System.out.println(p.getOTName());

					resp = "\u000c\u0000";
					
				}
				
				try
				{
					
					String hash = "HZEdGCzcGGLvguqUEKQN"
							+ Helper.b64Encode(resp) + "HZEdGCzcGGLvguqUEKQN";
					hash = Helper.sha1(hash);
					//System.out.println(hash);
					resp += hash;
					Response resp2 = new Response(resp);
					// System.out.println(resp.toString());
					out.print(resp2.toString());
				}
				catch (NoSuchAlgorithmException e)
				{
					// TODO: Send error event
					 System.out.println("No algorithm");
				}
				catch (Exception e)
				{
					// TODO: Send error event
					System.out.println("Error responding: " + e);
				}
				
			}
		}
	}
}