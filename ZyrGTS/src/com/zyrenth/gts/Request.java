package com.zyrenth.gts;

import java.util.HashMap;
import java.util.Map;

import com.zyrenth.gts.Helper.Generation;

public class Request
{
	private boolean valid;
	private String page;
	private String request;
	public String getRequest()
	{
		return request;
	}

	private String action;
	private Map<String, String> variables;
	private Generation generation;
		
	public Request(String data)
	{
		
		if(!data.startsWith("GET"))
		{
			valid = false;
		
		}
		if(data.contains("/syachi2ds/web/"))
		{
			// Black and White - Gen V
			this.generation = Helper.Generation.V;
			
			this.request = data.substring(data.indexOf("/syachi2ds/web/") + 15,
					data.indexOf("HTTP/1.1") - 1);
			
			Map<String, String> map = new HashMap<String, String>();
			String[] strings = request.split("\\?");
			page=strings[0];
			action = request.substring(request.indexOf("/") + 1,
					request.indexOf(".asp?"));
			
			String[] kvp = strings[1].split("&");
			for(int i =0; i < kvp.length; i++)
			{
				String[] kv = kvp[i].split("=");
				map.put(kv[0], kv[1]);
				
			}
			variables = map;
			valid = true;
		}
		else
		{
			// Not a DS header
			// Throw exception?
			valid = false;
		}
	}
	
	public Map<String, String> getVariables()
	{
		return variables;
	}

	public boolean isValid()
	{
		return valid;
	}

	public String getPage()
	{
		return page;
	}

	public String getAction()
	{
		return action;
	}

	public Generation getGeneration()
	{
		return generation;
	}
	
}
