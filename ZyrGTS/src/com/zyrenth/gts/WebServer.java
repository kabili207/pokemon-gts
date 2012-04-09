package com.zyrenth.gts;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class WebServer implements Runnable
{
	private static int port = 80, maxConnections = 0;
	private LinkedList<Pokemon> queuePokemon = new LinkedList<Pokemon>();
	
	public WebServer()
	{
	}
	
	// Listen for incoming connections and handle them
	public void run()
	{
		
		int i = 0;
		
		System.out.println("Starting Web Server");
		fireStatusChangedtEvent(ServerStatusEvent.Status.Starting);
		try
		{
			File f = new File("/tmp/Tympole.pkm");
			byte[] b = getBytesFromFile(f);
			queuePokemon.add(new Pokemon(b));
			
			f = new File("/tmp/Saanaito.pkm");
			b = getBytesFromFile(f);
			queuePokemon.add(new Pokemon(b));
			
			ServerSocket listener = new ServerSocket(port);
			Socket server;
			
			// TODO: Send an event on web start
			fireStatusChangedtEvent(ServerStatusEvent.Status.Started);
			
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
	
	public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
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
	private synchronized void firePokemonReceivedEvent(Pokemon pkm, Trainer t, String pid)
	{
		//System.out.println("Fireing event");
		PokemonReceivedEvent event = new PokemonReceivedEvent(this, pkm, t, pid);
		Iterator<WebEventListener> i = _listeners.iterator();
		while (i.hasNext())
		{
			//System.out.println("Sending event");
			i.next().onPokemonReceived(event);
		}
	}
	
	// call this method whenever you want to notify
	// the event listeners of the particular event
	private synchronized void firePokemonSentEvent(Pokemon pkm, String pid)
	{
		//System.out.println("Fireing event");
		PokemonSentEvent event = new PokemonSentEvent(this, pkm, pid);
		Iterator<WebEventListener> i = _listeners.iterator();
		while (i.hasNext())
		{
			//System.out.println("Sending event");
			i.next().onPokemonSent(event);
		}
	}
	
	// call this method whenever you want to notify
	// the event listeners of the particular event
	private synchronized void fireStatusChangedtEvent(ServerStatusEvent.Status status)
	{
		//System.out.println("Fireing event");
		ServerStatusEvent event = new ServerStatusEvent(this, status);
		Iterator<WebEventListener> i = _listeners.iterator();
		while (i.hasNext())
		{
			//System.out.println("Sending event");
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
				System.out.println(action);
				String resp = "";
				Pokemon pkm = null;
				
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
					if(queuePokemon.size() == 0)
					{
						resp = "\u0005\u0000";
					}
					else
					{
						pkm = queuePokemon.pop();
						byte[] data = pkm.getData();
						byte[] encoded = pkm.encode();
						String bin = "";
						for(int i =0; i < encoded.length; ++i)
						 bin += (char)encoded[i];
						
					    // Adding GTS data to end of file
					    bin += "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000";
					    bin += "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000";
					    bin += ((char)data[0x08]) + ((char)data[0x09]); // new String(Arrays.copyOfRange(data, 0x08, 0x0a)); // id
					    if (((data[0x40]) & 0x04) != 0)
					    	bin += "\u0003"; // Gender
					    else
					    	bin += (char)(((int)(data[0x40]) & 2) + 1);
					    bin += (char)data[0x8c]; // Level
					    bin += "\u0001\u0000\u0003\u0000\u0000\u0000\u0000\u0000";
					    bin += "\u00db\u0007\u0003" + '\n' + "\u0000\u0000\u0000\u0000"; // Date deposited (10 Mar 2011)
					    bin += "\u00db\u0007\u0003\u0016\u0001\u0030\u0000\u0000"; // Date traded (?)
					    bin += ((char)data[0x00]) + ((char)data[0x01]) + ((char)data[0x02]) + ((char)data[0x03]); //new String(Arrays.copyOfRange(data, 0x00, 0x04)); // PID
					    bin += ((char)data[0x0c]) + ((char)data[0x0d]); // OT ID
					    bin += ((char)data[0x0e]) + ((char)data[0x0f]); // OT Secret ID
					    byte[] otName = Arrays.copyOfRange(data, 0x68, 0x78);
					    for(int i =0; i < otName.length; ++i)
							 bin += (char)otName[i]; // OT Name
					    bin += "\u00DB\u0002"; // Country, City
					    bin += "\u0046\u0001\u0015\u0002"; // Sprite, Exchanged (?), Version, Lang
					    bin += "\u0001\u0000"; // Unknown

						resp = bin;
					}
				}
				else if (action.equals("post"))
				{
					String data = req.getVariables().get("data");
					byte[] bytes = Helper.b64Decode(data);
					byte[] decrypt = Pokemon.makePkm(bytes);
					//FileOutputStream fos = new FileOutputStream("/tmp/test.pkm");
					
					//fos.write(decrypt);
					Trainer t = processGenVTrainer(bytes);
					Pokemon p = new Pokemon(decrypt);

					firePokemonReceivedEvent(p, t, req.getVariables().get("pid"));
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
					if(pkm != null)
						firePokemonSentEvent(pkm, req.getVariables().get("pid"));
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
		
		private Trainer processGenVTrainer(byte[] data)
		{
			Trainer t = new Trainer();
			
			byte[] b = Arrays.copyOfRange(data, 0x118, 0x12f);
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));
			
			byte gender = data[0x102];
			try
			{
				t.gender = gender == (byte)0x0 ? Helper.Gender.Male : Helper.Gender.Female;
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