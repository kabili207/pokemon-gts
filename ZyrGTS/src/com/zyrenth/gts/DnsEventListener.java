package com.zyrenth.gts;

import java.net.InetAddress;

public interface DnsEventListener
{

	/**
	 * Occurs whenever the DNS server's status changes
	 * @param event a ServerStatusEvent object that contains the server's new status
	 * @param address an InetAddress object that the DNS server is listening on. This
	 * will always be null except for the Started status
	 */
	void onServerStatusChanged(ServerStatusEvent event, InetAddress address);
	
}
