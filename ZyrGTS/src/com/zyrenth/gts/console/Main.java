package com.zyrenth.gts.console;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

import com.zyrenth.gts.DnsServer;
import com.zyrenth.gts.Response;
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
		server.run();
	}


}
