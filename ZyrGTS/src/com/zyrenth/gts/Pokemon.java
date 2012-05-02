package com.zyrenth.gts;

import java.io.IOException;
import java.io.Serializable;

public abstract class Pokemon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8841578469549973671L;

	public Pokemon() {
		super();
	}

	public abstract byte[] encode() throws IOException;

	/**
	 * Gets a value indicating if this Pokemon is shiny
	 * @return true if Pokemon is shiny, otherwise false
	 */
	public abstract boolean isShiny();

	public abstract int getPID();

	/**
	 * Gets the original trainer's secret ID
	 * @return the original trainer's secret ID
	 */
	public abstract short getOTSecretID();

	/**
	 * Gets the original trainer's ID
	 * @return the original trainer's sID
	 */
	public abstract short getOTID();

	/**
	 * Gets the National Pokedex number for this pokemon
	 * @return the National Pokedex number for this pokemon
	 */
	public abstract short getNatID();

	/**
	 * Gets the pokemon's nickname
	 * @return the Pokemon's nickname
	 */
	public abstract String getNickname();

	/**
	 * Gets the original trainer's name
	 * @return the original trainer's name
	 */
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