package com.zyrenth.gts.console;

import com.zyrenth.gts.DnsServer;
import com.zyrenth.gts.PokemonSentEvent;
import com.zyrenth.gts.ServerStatusEvent;
import com.zyrenth.gts.WebEventListener;
import com.zyrenth.gts.PokemonReceivedEvent;
import com.zyrenth.gts.WebServer;

public class Main
{
	private static DnsServer dnsServer;
	private static Thread dnsThread;
	public static void main(String[] args)
	{
		dnsServer = new DnsServer();
		dnsThread = new Thread(dnsServer);
		dnsThread.start();
		
		WebServer server = new WebServer();
		server.addEventListener(new WebEventListener(){
			@Override
			public void onPokemonReceived(PokemonReceivedEvent e)
			{
				// TODO Auto-generated method stub
				System.out.println(e.getPid() + " sent " + e.getPokemon().getOTName());
			}

			@Override
			public void onPokemonSent(PokemonSentEvent e)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onServerStatusChanged(ServerStatusEvent e)
			{
				// TODO Auto-generated method stub
				
			}
		});
		server.run();
	}


}
