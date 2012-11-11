package com.zyrenth.gts;

import java.util.EventObject;

public class PokemonSentEvent extends EventObject {

	private static final long serialVersionUID = 941139007028445079L;
	private Pokemon pokemon;
	private String pid;

	public PokemonSentEvent(Object source, Pokemon pkm, String pid) {
		super(source);
		this.pokemon = pkm;
		this.pid = pid;
		// TODO Auto-generated constructor stub
	}

	public Pokemon getPokemon() {
		return pokemon;
	}

	public String getPid() {
		return pid;
	}
}
