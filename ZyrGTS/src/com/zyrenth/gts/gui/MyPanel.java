package com.zyrenth.gts.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.zyrenth.gts.DnsServer;
import com.zyrenth.gts.Pokemon;
import com.zyrenth.gts.WebEventListener;
import com.zyrenth.gts.PokemonReceivedEvent;
import com.zyrenth.gts.WebServer;

public class MyPanel extends JPanel
{
	private DnsServer dnsServer;
	private Thread dnsThread;
	
	private JLabel label;
	private WebServer webServer;
	private Thread webThread;
	private Font ttfReal = null;
	private Font ttfBase = null;
	private Font ttf12 = null;
	private JLabel imgPokemon;
	
	public MyPanel()
	{
		super(new BorderLayout());
		try
		{
			String fontFileName = "/fonts/DejaVuSansPkm.ttf";
			InputStream is = this.getClass().getResourceAsStream(fontFileName);
			
			ttfBase = Font.createFont(Font.TRUETYPE_FONT, is);
			ttfReal = ttfBase.deriveFont(Font.PLAIN, 24);
			ttf12 = ttfBase.deriveFont(Font.PLAIN, 15);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		label = new JLabel(
				"\u0024\u21d2\u21d4\u2200\u2203\u2227\u2228"
						+ "\u2460\u2461\u2462\u2463\u2464\u2465\u2466\u2467\u2468\u2469\u246a\u246b\u246c\u246d\u246e\u246f"
						+ "\u2470\u2471\u2472\u2473\u2474\u2475\u2476\u2477\u2478\u2479\u247a\u247b\u247c\u247d\u247e\u247f"
						+ "\u2480\u2481\u2482\u2483\u2484\u2485\u2486\u2487");
		if (ttf12 != null)
			label.setFont(ttf12);
		ImageIcon icon = createImageIcon("/images/sprites/normal/001.png", null);
		imgPokemon = new JLabel(icon);
		
		label.setHorizontalAlignment(JLabel.CENTER);
		this.setPreferredSize(new Dimension(400, 150));
		// label.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.black));
		add(label, BorderLayout.CENTER);
		add(imgPokemon, BorderLayout.WEST);
		
		startGts();
	}
	
	protected ImageIcon createImageIcon(String path, String description)
	{
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null)
		{
			return new ImageIcon(imgURL, description);
		}
		else
		{
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
	
	public void startGts()
	{
		dnsServer = new DnsServer();
		dnsThread = new Thread(dnsServer);
		dnsThread.start();
		
		webServer = new WebServer();
		webThread = new Thread(webServer);
		webServer.addEventListener(new WebEventListener()
		{
			@Override
			public void onPokemonReceived(PokemonReceivedEvent e)
			{
				System.out.println("Processing event");
				try
				{
					Pokemon p = e.getPokemon();
					label.setText(e.getPid() + " sent " + p.getOTName() + "'s "
							+ p.getNickname());
					String natID = getPokemonImage(p);
					imgPokemon.setIcon(createImageIcon(natID,null));
				}
				catch (Throwable ex)
				{
					ex.printStackTrace();
					label.setText("Something failed");
				}
			}
		});
		webThread.start();
	}
	
	private String getPokemonImage(Pokemon p)
	{
		String natID = String.format("%03d", p.getNatID());
		String shiny = p.isShiny() ? "shiny" : "normal";
		return "/images/sprites/"+ shiny +"/"+natID+".png";
		
		// Rotom - 479 f w s m h
		// Giratina - 487 o
		// Shaymin - 492 s
		// Basculin - 550 b
		// Darumaka - 555 d
		// Deerling - 585 s a w
		// Sawsbuck - 586 s a w
		// Meloetta - 648 s
	
	}
}
