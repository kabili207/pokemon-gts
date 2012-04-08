package com.zyrenth.gts.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.JFrame;


public class MyFrame extends JFrame
{
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		//WindowUtilities.setNativeLookAndFeel();
	    MyFrame f = new MyFrame();
	    //f.setSize(400, 150);
	    Container content = f.getContentPane();
	    content.setBackground(Color.white);
	    content.setLayout(new BorderLayout()); 
	    content.add(new MyPanel());
	    //f.addWindowListener();
	    f.setTitle("Frame test");
	    f.setVisible(true);
	   // f.setResizable(false);
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    f.pack();
	}
	
}
