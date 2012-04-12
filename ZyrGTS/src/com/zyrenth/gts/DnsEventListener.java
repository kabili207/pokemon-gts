package com.zyrenth.gts;

import java.net.InetAddress;

public interface DnsEventListener
{

	void onServerStatusChanged(ServerStatusEvent event, InetAddress address);
	
}
