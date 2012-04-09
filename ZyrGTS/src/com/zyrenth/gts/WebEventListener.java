package com.zyrenth.gts;


public interface WebEventListener
{
	public void onServerStatusChanged(ServerStatusEvent e);
	
	public void onPokemonReceived(PokemonReceivedEvent e);
	
	public void onPokemonSent(PokemonSentEvent e);
	
}
