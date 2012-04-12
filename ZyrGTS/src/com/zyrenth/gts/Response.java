package com.zyrenth.gts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Response
{
	
	private String data;
	private String p3p;
	private String server;
	private String sname;
	private String len;
	private String cookie;
	
	public Response(String h)
	{
		if(!h.startsWith("HTTP/1.1"))
		{
			data = h;
			return;
		}
		
		String[] lines = h.split("\r\n");
		ArrayList<String> arrLines = new ArrayList<String>();
		
		for(int i = 0; i < lines.length; i++)
		{	
			String line = lines[i];
			if(!line.startsWith("HTTP/1.1"))
				arrLines.add(line);
			
			if(line.startsWith("P3P"))
				this.p3p = line.substring(line.indexOf(": ") + 2);
			else if(line.startsWith("cluster-server"))
				this.server = line.substring(line.indexOf(": ") + 2);
			else if(line.startsWith("X-Server-"))
				this.sname = line.substring(line.indexOf(": ") + 2);
			else if(line.startsWith("Content-Length"))
				this.len = line.substring(line.indexOf(": ") + 2);
			else if(line.startsWith("Set-Cookie"))
				this.cookie = line.substring(line.indexOf(": ") + 2);
			
		}
		
		this.data = combine("\r\n", arrLines.toArray(new String[] {}));

	}
	
	String combine(String glue, String... s)
	{
	  int k=s.length;
	  if (k==0)
	    return null;
	  StringBuilder out=new StringBuilder();
	  out.append(s[0]);
	  for (int x=1;x<k;++x)
	    out.append(glue).append(s[x]);
	  return out.toString();
	}
	
	public String createHeader(int length)
	{
		SimpleDateFormat dFormat = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss z");
		String date = dFormat.format(new Date());
		return "HTTP/1.1 200 OK\r\n"
				+ "Date: " + date + "\r\n"
				+ "Server: Microsoft-IIS/6.0\r\n"
				+ "P3P: CP='NOI ADMa OUR STP'\r\n"
				+ "cluster-server: aphexweb3\r\n"
				+ "X-Server-Name: AW4\r\n"
				+ "X-Powered-By: ASP.NET\r\n"
				+ "Content-Length: " + length + "\r\n"
				+ "Content-Type: text/html\r\n"
				+ "Set-Cookie: ASPSESSIONIDQCDBDDQS=JFDOAMPAGACBDMLNLFBCCNCI; path=/\r\n"
				+ "Cache-control: private\r\n\r\n";
	}
	
	@Override
	public String toString()
	{
		return createHeader(data.length()) + this.data;
		
	}
	
}
