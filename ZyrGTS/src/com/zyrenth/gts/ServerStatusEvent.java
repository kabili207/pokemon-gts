package com.zyrenth.gts;

import java.util.EventObject;

public class ServerStatusEvent extends EventObject
{

	
	private static final long serialVersionUID = 4668666769619795002L;

	public enum Status { Starting, Started, Stopping, Stopped }
	
	private Status status;
	
	public ServerStatusEvent(Object source, Status status)
	{
		super(source);
		this.status = status;
		// TODO Auto-generated constructor stub
	}
	
	public Status getStatus()
	{
		return status;
	}
	
}
