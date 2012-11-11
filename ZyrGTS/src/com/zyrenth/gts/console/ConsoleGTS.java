package com.zyrenth.gts.console;

import java.net.InetAddress;

import com.zyrenth.gts.DnsEventListener;
import com.zyrenth.gts.DnsServer;
import com.zyrenth.gts.Helper;
import com.zyrenth.gts.Pokemon;
import com.zyrenth.gts.PokemonSentEvent;
import com.zyrenth.gts.ServerStatusEvent;
import com.zyrenth.gts.Trainer;
import com.zyrenth.gts.WebEventListener;
import com.zyrenth.gts.PokemonReceivedEvent;
import com.zyrenth.gts.WebServer;

public class ConsoleGTS {
	private static DnsServer dnsServer;
	private static Thread dnsThread;

	public static void main(String[] args) {
		// System.out.println(Helper.getAppDataDirectory());
		// if(1 > 0)
		// return;

		dnsServer = new DnsServer();
		dnsServer.addEventListener(new DnsEventListener() {

			@Override
			public void onServerStatusChanged(final ServerStatusEvent e, final InetAddress address) {
				if (e.getStatus() == ServerStatusEvent.Status.Starting) {
					// System.out.println("Starting DNS Server");
					System.out.println("DNS server is starting");
				}
				if (e.getStatus() == ServerStatusEvent.Status.Started) {
					System.out.println("DNS server listening on " + address.getHostAddress());
					System.out.println("DNS server has started");
				}

			}

			@Override
			public void onValidityCheck(InetAddress address) {
				// TODO Auto-generated method stub
				System.out.println("WARNING: " + address.getHostAddress() + " is checking Nintendo's server to see if pokemon is valid!");
			}

			@Override
			public void onServerError(Exception e) {
				// TODO Auto-generated method stub
				System.out.println("Server encountered an error: " + e);
				e.printStackTrace();

			}

		});

		dnsThread = new Thread(dnsServer);
		dnsThread.start();

		WebServer server = new WebServer();
		server.addEventListener(new WebEventListener() {
			@Override
			public void onPokemonReceived(PokemonReceivedEvent e) {
				// TODO Auto-generated method stub
				Pokemon p = e.getPokemon();
				Trainer t = e.getTrainer();

				// TODO: Add gender check to same
				boolean same = t.name.equals(p.getOTName()) && t.ID == p.getOTID() && t.SecretID == p.getOTSecretID();
				String gender = t.gender == Helper.Gender.Male ? "his" : "her";

				System.out.println(t.name + " sent " + (same ? gender : (p.getOTName() + "'s")) + " " + p.getNickname());
			}

			@Override
			public void onPokemonSent(PokemonSentEvent e) {
				// TODO Auto-generated method stub
				System.out.println(e.getPokemon().getNickname() + " was sent to " + e.getPid());

			}

			@Override
			public void onServerStatusChanged(ServerStatusEvent e) {
				// TODO Auto-generated method stub

				if (e.getStatus() == ServerStatusEvent.Status.Starting) {
					System.out.println("Web server is starting");
				}
				if (e.getStatus() == ServerStatusEvent.Status.Started) {
					// label.setText("Listening on " +
					// address.getHostAddress());
					System.out.println("Web server has started");
				}
			}

			@Override
			public void onServerError(Exception e) {
				// TODO Auto-generated method stub
				System.out.println("Server encountered an error: " + e);
				e.printStackTrace();

			}
		});
		server.run();
	}

}
