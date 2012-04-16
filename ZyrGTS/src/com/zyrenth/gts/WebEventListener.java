package com.zyrenth.gts;

/**
 * An event listener for the GTS web server
 * @author kabili
 *
 */
public interface WebEventListener
{
	/**
	 * Occurs whenever the status of the server has changed
	 * @param e A ServerStatusEvent object that contains details about the
	 * server's new status
	 */
	public void onServerStatusChanged(ServerStatusEvent e);
	
	/**
	 * Occurs whenever a Pokemon is received from a game
	 * @param e A PokemonReceivedEvent object that contains the Pokemon that
	 * was received, the Trainer that sent it, and the PID of the game it was
	 * sent from.
	 */
	public void onPokemonReceived(PokemonReceivedEvent e);
	
	/**
	 * Occurs whenever a Pokemon is sent to a game
	 * @param e A PokemonSentEvent object that contains the Pokemon that was
	 * sent and the PID of the game it was sent to.
	 */
	public void onPokemonSent(PokemonSentEvent e);
	
}
