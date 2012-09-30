package com.zyrenth.gts;

import java.net.InetAddress;

/**
 * An event listener for the GTS Domain Name Server
 * @author kabili
 */
public interface DnsEventListener
{

	/**
	 * Occurs whenever the DNS server's status changes
	 * @param event a ServerStatusEvent object that contains the server's new status
	 * @param address an InetAddress object that the DNS server is listening on. This
	 * will always be null except for the Started status
	 */
	void onServerStatusChanged(ServerStatusEvent event, InetAddress address);

	/**
	 * Occurs whenever the DNS server detects the game trying to check with Nintendo's
	 * servers if a Pokemon is valid or not. This mostly occurs with poorly hacked
	 * Pokemon, however it can happen with legitimate event Pokemon. If a Pokemon is
	 * deemed invalid it will not be sent to the GTS.
	 * @param address
	 */
	void onValidityCheck(InetAddress address);
	
	/**
	 * Occurs whenever the server encounters an error while running. The most
	 * common error is a <code>BindException</code> that occurs during startup.
	 * @param e The Exception that occurred
	 */
	public void onServerError(Exception e);
	
}
