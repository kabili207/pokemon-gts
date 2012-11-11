package com.zyrenth.gts;

import java.util.EventObject;

public class PokemonReceivedEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1899892307141683805L;
	private Pokemon pokemon;
	private String pid;
	private Trainer trainer;

	public PokemonReceivedEvent(Object source, Pokemon pkm, Trainer t, String pid) {
		super(source);
		this.pokemon = pkm;
		this.pid = pid;
		this.trainer = t;
		// TODO Auto-generated constructor stub
	}

	public Pokemon getPokemon() {
		return pokemon;
	}

	public Trainer getTrainer() {
		return trainer;
	}

	public String getPid() {
		return pid;
	}

}
