package com.zyrenth.gts;

import java.io.IOException;

public abstract class Pokemon {

	public Pokemon() {
		super();
	}

	public abstract byte[] encode() throws IOException;

	public abstract boolean isShiny();

	public abstract int getPID();

	public abstract short getOTSecretID();

	public abstract short getOTID();

	public abstract short getNatID();

	public abstract String getNickname();

	public abstract String getOTName();
	
	public static byte[] makePkm(byte[] bytes) throws IOException
	{
		throw new IOException();
	}

	public static byte[] decode(byte[] bytes) throws IOException
	{
		throw new IOException();
	}
	
	public static byte[] encode(byte[] bytes) throws IOException
	{
		throw new IOException();
	}

}