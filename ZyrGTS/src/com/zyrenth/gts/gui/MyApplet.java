package com.zyrenth.gts.gui;
import java.awt.BorderLayout;
import javax.swing.JApplet;


public class MyApplet extends JApplet
{
	private static final long serialVersionUID = -452479009754353016L;

	public void init() {
	    //Execute a job on the event-dispatching thread:
	    //creating this applet's GUI.
	    try {
	        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
	            public void run() {
	                createGUI();
	            }
	        });
	    } catch (Exception e) {
	        System.err.println("createGUI didn't successfully complete");
	    }
	}

	private void createGUI() {
	    getContentPane().add(new MyPanel(), BorderLayout.CENTER);
	}
}
