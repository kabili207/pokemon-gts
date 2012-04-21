package com.zyrenth.gts.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.net.InetAddress;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.zyrenth.gts.DnsEventListener;
import com.zyrenth.gts.DnsServer;
import com.zyrenth.gts.Helper;
import com.zyrenth.gts.Pokemon;
import com.zyrenth.gts.PokemonSentEvent;
import com.zyrenth.gts.ServerStatusEvent;
import com.zyrenth.gts.Trainer;
import com.zyrenth.gts.WebEventListener;
import com.zyrenth.gts.PokemonReceivedEvent;
import com.zyrenth.gts.WebServer;

public class MyPanel extends JPanel
{
	
	private static final long serialVersionUID = 7071594099866792128L;
	private DnsServer dnsServer;
	private Thread dnsThread;
	
	private JLabel label;
	private WebServer webServer;
	private Thread webThread;
	private Font ttfBase = null;
	private Font ttf12 = null;
	private JLabel imgPokemon;
	private JLabel lblDnsIcon;
	private JLabel lblWebIcon;
	
	public MyPanel()
	{
		super(new BorderLayout());
		try
		{
			String fontFileName = "/fonts/DejaVuSansPkm.ttf";
			InputStream is = this.getClass().getResourceAsStream(fontFileName);
			
			ttfBase = Font.createFont(Font.TRUETYPE_FONT, is);
			ttf12 = ttfBase.deriveFont(Font.PLAIN, 15);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// label = new JLabel(
		// "\u0024\u21d2\u21d4\u2200\u2203\u2227\u2228"
		// + "\u2460\u2461\u2462\u2463\u2464\u2465\u2466\u2467\u2468\u2469\u246a\u246b\u246c\u246d\u246e\u246f"
		// + "\u2470\u2471\u2472\u2473\u2474\u2475\u2476\u2477\u2478\u2479\u247a\u247b\u247c\u247d\u247e\u247f"
		// + "\u2480\u2481\u2482\u2483\u2484\u2485\u2486\u2487");
		label = new JLabel("Loading...");
		if (ttf12 != null)
			label.setFont(ttf12);
		ImageIcon icon = createImageIcon("/images/sprites/normal/001.png", null);
		imgPokemon = new JLabel(icon);
		
		label.setHorizontalAlignment(JLabel.CENTER);
		this.setPreferredSize(new Dimension(600, 350));
		JPanel pnlGTS = new JPanel();
		pnlGTS.setPreferredSize(new Dimension(150, 350));
		// label.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.black));
		add(pnlGTS, BorderLayout.WEST);
		
		JTabbedPane tpTabPane = new JTabbedPane();
		
		JComponent panel1 = new JPanel();
		tpTabPane.addTab("Tab 1", panel1);
		tpTabPane.setMnemonicAt(0, KeyEvent.VK_1);

		JComponent panel2 = new JPanel();
		tpTabPane.addTab("Tab 2", panel2);
		tpTabPane.setMnemonicAt(1, KeyEvent.VK_2);

		JComponent panel3 = new JPanel();
		tpTabPane.addTab("Tab 3", panel3);
		
		add(tpTabPane, BorderLayout.CENTER);
		//add(label, BorderLayout.CENTER);
		//add(imgPokemon, BorderLayout.WEST);
		

		JToolBar toolbar = new JToolBar();
		//toolbar.setLayout(new BorderLayout());
		toolbar.setFloatable(false);

		ImageIcon imgDNS = createImageIcon("/images/icons/ledred.png", "DNS server not started");
		ImageIcon imgWeb = createImageIcon("/images/icons/ledred.png", "Web server not started");
		
		toolbar.add(lblDnsIcon = new JLabel(imgDNS));
		toolbar.addSeparator(new Dimension(5,5));
		toolbar.add(lblWebIcon = new JLabel(imgWeb));
		toolbar.addSeparator(new Dimension(5,5));
		toolbar.add(label);
		//toolbar.add(toolbarStatus = new JLabel("",JLabel.RIGHT), BorderLayout.EAST);
		
		add(toolbar,BorderLayout.SOUTH);
		
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				startGts();
			}
		}).start();
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
		
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{
				public void run()
				{
					dnsServer.addEventListener(new DnsEventListener()
					{
						
						@Override
						public void onServerStatusChanged(final ServerStatusEvent e, final InetAddress address)
						{
							if (e.getStatus() == ServerStatusEvent.Status.Starting)
							{
								//System.out.println("Starting DNS Server");
								lblDnsIcon.setIcon(createImageIcon("/images/icons/ledyellow.png", "DNS server is starting"));
							}
							if (e.getStatus() == ServerStatusEvent.Status.Started){
								label.setText("Listening on " + address.getHostAddress());
								lblDnsIcon.setIcon(createImageIcon("/images/icons/ledgreen.png", "DNS server has started"));
							}
							
						}

						@Override
						public void onValidityCheck(InetAddress address) {
							// TODO Auto-generated method stub
							
						}
						
					});
					
				}
			});
		}
		catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		dnsThread.start();
		
		webServer = new WebServer();
		webThread = new Thread(webServer);
		
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{
				public void run()
				{
					webServer.addEventListener(new WebEventListener()
					{
						@Override
						public void onPokemonReceived(final PokemonReceivedEvent e)
						{
							System.out.println("Processing event");
							try
							{
								Pokemon p = e.getPokemon();
								Trainer t = e.getTrainer();
								
								// TODO: Add gender check to same
								boolean same = t.name.equals(p.getOTName()) && t.ID == p.getOTID() && t.SecretID == p.getOTSecretID();
								String gender = t.gender == Helper.Gender.Male ? "his" : "her";
								
								String natID = getPokemonImage(p);
								label.setText(t.name + " sent " + (same ? gender : (p.getOTName() + "'s")) + " " + p.getNickname());
								imgPokemon.setIcon(createImageIcon(natID, null));
							}
							catch (Throwable ex)
							{
								ex.printStackTrace();
								label.setText("Something failed");
							}
						}
						
						@Override
						public void onServerStatusChanged(ServerStatusEvent e)
						{
							if (e.getStatus() == ServerStatusEvent.Status.Starting)
							{
								//System.out.println("Starting DNS Server");
								lblWebIcon.setIcon(createImageIcon("/images/icons/ledyellow.png", "Web server is starting"));
							}
							if (e.getStatus() == ServerStatusEvent.Status.Started){
								//label.setText("Listening on " + address.getHostAddress());
								lblWebIcon.setIcon(createImageIcon("/images/icons/ledgreen.png", "Web server has started"));
							}
							
						}
						
						@Override
						public void onPokemonSent(final PokemonSentEvent e)
						{
							String natID = getPokemonImage(e.getPokemon());
							imgPokemon.setIcon(createImageIcon(natID, null));
							label.setText("Sent " + e.getPokemon().getNickname() + " to " + e.getPid());
							
						}
					});
				}
			});
		}
		catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		webThread.start();
	}
	
	private String getPokemonImage(Pokemon p)
	{
		String natID = String.format("%03d", p.getNatID());
		String shiny = p.isShiny() ? "shiny" : "normal";
		return "/images/sprites/" + shiny + "/" + natID + ".png";
		
		// Rotom - 479 f w s m h
		// Giratina - 487 o
		// Shaymin - 492 s
		// Basculin - 550 b
		// Darumaka - 555 d
		// Deerling - 585 s a w
		// Sawsbuck - 586 s a w
		// Meloetta - 648 s
		
	}
	
	public JMenuBar getMenuBar()
	{
	
		//Where the GUI is created:
		JMenuBar menuBar;
		JMenu menu, submenu;
		JMenuItem menuItem;
		JRadioButtonMenuItem rbMenuItem;
		JCheckBoxMenuItem cbMenuItem;

		//Create the menu bar.
		menuBar = new JMenuBar();

		//Build the first menu.
		menu = new JMenu("A Menu");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
		        "The only menu in this program that has menu items");
		menuBar.add(menu);

		//a group of JMenuItems
		menuItem = new JMenuItem("A text-only menu item",
		                         KeyEvent.VK_T);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
		        "This doesn't really do anything");
		menu.add(menuItem);

		menuItem = new JMenuItem("Both text and icon",
		                         new ImageIcon("images/middle.gif"));
		menuItem.setMnemonic(KeyEvent.VK_B);
		menu.add(menuItem);

		menuItem = new JMenuItem(new ImageIcon("images/middle.gif"));
		menuItem.setMnemonic(KeyEvent.VK_D);
		menu.add(menuItem);

		//a group of radio button menu items
		menu.addSeparator();
		ButtonGroup group = new ButtonGroup();
		rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
		rbMenuItem.setSelected(true);
		rbMenuItem.setMnemonic(KeyEvent.VK_R);
		group.add(rbMenuItem);
		menu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("Another one");
		rbMenuItem.setMnemonic(KeyEvent.VK_O);
		group.add(rbMenuItem);
		menu.add(rbMenuItem);

		//a group of check box menu items
		menu.addSeparator();
		cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
		cbMenuItem.setMnemonic(KeyEvent.VK_C);
		menu.add(cbMenuItem);

		cbMenuItem = new JCheckBoxMenuItem("Another one");
		cbMenuItem.setMnemonic(KeyEvent.VK_H);
		menu.add(cbMenuItem);

		//a submenu
		menu.addSeparator();
		submenu = new JMenu("A submenu");
		submenu.setMnemonic(KeyEvent.VK_S);

		menuItem = new JMenuItem("An item in the submenu");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_2, ActionEvent.ALT_MASK));
		submenu.add(menuItem);

		menuItem = new JMenuItem("Another item");
		submenu.add(menuItem);
		menu.add(submenu);

		//Build second menu in the menu bar.
		menu = new JMenu("Another Menu");
		menu.setMnemonic(KeyEvent.VK_N);
		menu.getAccessibleContext().setAccessibleDescription(
		        "This menu does nothing");
		menuBar.add(menu);
		return menuBar;
	}
}
