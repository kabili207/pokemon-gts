package com.zyrenth.gts;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The web server that is used by the GTS to send and receive Pokemon.
 * The server needs to be run on port 80 which may require administrative
 * privileges.
 * 
 * @author kabili
 */
public class WebServer implements Runnable
{
	private static final int PORT = 80;
	private static final int MAX_CONNECTIONS = 0;
	private static final Object QUEUE_LOCK = new Object();
	
	private LinkedBlockingQueue<Pokemon> queuePokemon = new LinkedBlockingQueue<Pokemon>();
		
	public WebServer()
	{
	}
	
	// Listen for incoming connections and handle them
	public void run()
	{
		
		int i = 0;
		
		fireStatusChangedEvent(ServerStatusEvent.Status.Starting);
		try
		{
			
			//File f = new File("/tmp/Saanaito.pkm");
			//byte[] b = Helper.getBytesFromFile(f);
			//queuePokemon.add(new Pokemon(b));
			
			// f = new File("/tmp/Tympole.pkm");
			// b = getBytesFromFile(f);
			// queuePokemon.add(new Pokemon(b));
			
			ServerSocket listener = new ServerSocket(PORT);
			Socket server;
			
			// TODO: Send an event on web start
			fireStatusChangedEvent(ServerStatusEvent.Status.Started);
			
			while ((i++ < MAX_CONNECTIONS) || (MAX_CONNECTIONS == 0))
			{
				server = listener.accept();
				// System.out.println("Connection established");
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
		
		fireStatusChangedEvent(ServerStatusEvent.Status.Stopped);
		//System.out.println("Stopping Web");
	}
	
	public void addPokemon(Pokemon p)
	{
		synchronized(QUEUE_LOCK)
		{
			queuePokemon.add(p);
		}
	}
	
	private List<WebEventListener> _listeners = new ArrayList<WebEventListener>();
	
	public synchronized void addEventListener(WebEventListener listener)
	{
		_listeners.add(listener);
	}
	
	public synchronized void removeEventListener(WebEventListener listener)
	{
		_listeners.remove(listener);
	}
	
	// call this method whenever you want to notify
	// the event listeners of the particular event
	private synchronized void firePokemonReceivedEvent(Pokemon pkm, Trainer t, String pid)
	{
		PokemonReceivedEvent event = new PokemonReceivedEvent(this, pkm, t, pid);
		Iterator<WebEventListener> i = _listeners.iterator();
		while (i.hasNext())
		{
			i.next().onPokemonReceived(event);
		}
	}
	
	/**
	 * Fires an event to any listeners telling them the specified Pokemon was sent
	 * @param pkm the Pokemon that was sent
	 * @param pid the PID of the game the Pokemon was sent to
	 */
	private synchronized void firePokemonSentEvent(Pokemon pkm, String pid)
	{
		PokemonSentEvent event = new PokemonSentEvent(this, pkm, pid);
		Iterator<WebEventListener> i = _listeners.iterator();
		while (i.hasNext())
		{
			i.next().onPokemonSent(event);
		}
	}
	
	/**
	 * Fires a status changed event to any listeners registered.
	 * @param status the server's new status
	 */
	private synchronized void fireStatusChangedEvent(ServerStatusEvent.Status status)
	{
		ServerStatusEvent event = new ServerStatusEvent(this, status);
		Iterator<WebEventListener> i = _listeners.iterator();
		while (i.hasNext())
		{
			i.next().onServerStatusChanged(event);
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
			input = "";
			
			try
			{
				// Get input from the client
				BufferedReader d = new BufferedReader(new InputStreamReader(server.getInputStream()));
				
				while ((line = d.readLine()) != null && d.ready())
				{
					input = input + line + "\r\n";
				}
				
				// Now write to the client
				//System.out.println(input);
				
				Request req = new Request(input);
				if (req.isValid())
				{
					if (req.getGeneration() == Helper.Generation.V)
						processGenV(req, server);
					
				}
								
				server.close();
			}
			catch (NoSuchAlgorithmException nsae)
			{
				// TODO Send error event
				System.out.println("Algorithm not found: ");
				nsae.printStackTrace();
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
			// System.out.println("Ending web thread");
		}
		
		private void processGenV(Request req, Socket server) throws Exception
		{
			// PrintStream out = new PrintStream(server.getOutputStream());
			DataOutputStream out = new DataOutputStream(server.getOutputStream());
			if (req.getVariables().size() == 1)
			{
				
				Response resp = new Response("c9KcX1Cry3QKS2Ai7yxL6QiQGeBGeQKR");
				// System.out.println(resp.toString());
				out.write(resp.toString().getBytes());
				out.flush();
			}
			else
			{
				String action = req.getAction();
				//System.out.println(action);
				String resp = "";
				PokemonGen5 pkm = null;
				
				if (action.equals("info"))
					resp = "\u0001\u0000";
				else if (action.equals("setProfile"))
					resp = "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000";
				else if (action.equals("delete"))
					resp = "\u0001\u0000";
				else if (action.equals("search"))
					resp = "\u0001\u0000";
				else if (action.equals("result"))
				{
					synchronized(QUEUE_LOCK)
					{
						for(Pokemon p : queuePokemon)
						{
							if(!(p instanceof PokemonGen5))
							{
								// TODO: Try to create a Gen 5 pokemon instead
								continue;
							}
							pkm = (PokemonGen5)p;
							break;
						}
						if(pkm != null)
							queuePokemon.remove(pkm);
					}
					if (pkm == null)
					{
						resp = "\u0005\u0000";
					}
					else
					{
						byte[] data = pkm.getData();
						byte[] encoded = pkm.encode();
						
						ByteArrayOutputStream bais = new ByteArrayOutputStream();
						
						// Adding GTS data to end of file
						bais.write(encoded);
						bais.write(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
						bais.write(Arrays.copyOfRange(data, 0x08, 0x0a)); // id
						if (((data[0x40]) & 0x04) != 0)
							bais.write(0x3); // Gender
						else
							bais.write(((data[0x40]) & 2) + 1);
						bais.write(data[0x8c]); // Level
						bais.write(new byte[] { 1, 0, 3, 0, 0, 0, 0, 0 });
						bais.write(new byte[] { (byte) 0xdb, 7, 3, 0xa, 0, 0, 0, 0 }); // Date deposited (10 Mar 2011)
						bais.write(new byte[] { (byte) 0xdb, 7, 3, 0x16, 1, 0x30, 0, 0 }); // Date traded (?)
						
						bais.write(Arrays.copyOfRange(data, 0x00, 0x04)); // PID
						bais.write(Arrays.copyOfRange(data, 0x0c, 0x0e)); // OT ID
						bais.write(Arrays.copyOfRange(data, 0x0e, 0x10)); // OT Secret ID
						bais.write(Arrays.copyOfRange(data, 0x68, 0x78)); // OT Name
						
						bais.write(new byte[] { (byte) 0xdb, 2 }); // Country, City
						
						bais.write(new byte[] { 0x46, 1, 0x15, 2 }); // Sprite, Exchanged (?), Version, Lang
						
						bais.write(new byte[] { 1, 0 }); // Unknown
						
						byte[] bb = bais.toByteArray();
						String hash = "HZEdGCzcGGLvguqUEKQN" + Helper.b64Encode(bb) + "HZEdGCzcGGLvguqUEKQN";
						hash = Helper.sha1(hash);
						
						Response resp2 = new Response("");
						out.write(resp2.createHeader(bb.length + hash.length()).getBytes());
						out.write(bb);
						out.writeBytes(hash);
						if (pkm != null)
							firePokemonSentEvent(pkm, req.getVariables().get("pid"));
						return;
						
					}
				}
				else if (action.equals("post"))
				{
					String data = req.getVariables().get("data");
					byte[] bytes = Helper.b64Decode(data);
					byte[] decrypt = PokemonGen5.makePkm(bytes);
					// FileOutputStream fos = new
					// FileOutputStream("/tmp/test.pkm");
					
					// fos.write(decrypt);
					Trainer t = processGenVTrainer(bytes);
					Pokemon p = new PokemonGen5(decrypt);
					
					firePokemonReceivedEvent(p, t, req.getVariables().get("pid"));
					// System.out.println(p.getOTName());
					
					resp = "\u000c\u0000";
					
				}
				
				String hash = "HZEdGCzcGGLvguqUEKQN" + Helper.b64Encode(resp) + "HZEdGCzcGGLvguqUEKQN";
				hash = Helper.sha1(hash);
				// System.out.println(hash);
				resp += hash;
				Response resp2 = new Response(resp);
				// System.out.println(resp.toString());
				out.write(resp2.toString().getBytes());
				if (pkm != null)
					firePokemonSentEvent(pkm, req.getVariables().get("pid"));
				
			}
		}
		
		private Trainer processGenVTrainer(byte[] data)
		{
			Trainer t = new Trainer();
			
			byte[] b = Arrays.copyOfRange(data, 0x118, 0x12f);
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));
			
			byte gender = data[0x102];
			try
			{
				t.gender = gender == (byte) 0x0 ? Helper.Gender.Male : Helper.Gender.Female;
				t.ID = Short.reverseBytes(in.readShort());
				t.SecretID = Short.reverseBytes(in.readShort());
				
				String name = "";
				
				for (int i = 0; i < b.length / 2; ++i)
				{
					try
					{
						char c = Character.reverseBytes(in.readChar());
						if (c == '\uffff')
							break;
						name += c;
					}
					catch (IOException e)
					{
					}
				}
				t.name = name;
			}
			catch (Throwable ex)
			{
				
			}
			return t;
		}
	}
}