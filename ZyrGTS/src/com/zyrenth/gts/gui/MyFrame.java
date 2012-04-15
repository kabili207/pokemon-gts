package com.zyrenth.gts.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MyFrame extends JFrame
{
	
	private static final long serialVersionUID = -6427365071807252220L;
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		// WindowUtilities.setNativeLookAndFeel();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				MyFrame f = new MyFrame();
				// f.setSize(400, 150);
				Container content = f.getContentPane();
				content.setBackground(Color.white);
				content.setLayout(new BorderLayout());
				MyPanel panel = new MyPanel();
				content.add(panel);
				f.setJMenuBar(panel.getMenuBar());
				// f.addWindowListener();
				f.setTitle("Frame test");
				f.setVisible(true);
				// f.setResizable(false);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.pack();
			}
		});
	}
	
}
