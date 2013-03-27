package org.snack.irc.main;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.snack.irc.settings.Config;

public class Monitor {
	private static boolean showInterface;

	private static JFrame monitorFrame;
	private static JPanel monitorPanel;
	private static JScrollPane monitorScrollPane;
	private static JTextArea monitorTextArea;
	private static JMenuBar menuBar;
	private static JMenu mainMenu;
	private static JMenuItem menuSend, menuQuit;

	private static Monitor m;

	public static Monitor getInstance() {
		if (m == null) {
			m = new Monitor(Config.sett_bool.get("INTERFACE"));
		}
		return m;
	}

	private Monitor(boolean showInterface) {
		Monitor.showInterface = showInterface;
		if (showInterface) {
			createAndShowGUI();
		}
		Monitor.print("Initialized interface");
	}

	/**
	 * A frame that shows all messages the server returns
	 */
	public static void createAndShowGUI() {
		// make the interface parts
		monitorFrame = new JFrame();
		monitorPanel = new JPanel();
		monitorScrollPane = new JScrollPane();
		monitorTextArea = new JTextArea();
		// set up the menu
		menuBar = new JMenuBar();

		// the main menu
		mainMenu = new JMenu("Main");
		mainMenu.setMnemonic(KeyEvent.VK_M);
		mainMenu.getAccessibleContext().setAccessibleDescription("Main menu.");
		menuBar.add(mainMenu);

		// load menu item
		menuSend = new JMenuItem("Send message", KeyEvent.VK_S);
		menuSend.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menuSend.getAccessibleContext().setAccessibleDescription("Send a message");
		menuSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Prompt(getFrame());
			}
		});
		menuQuit = new JMenuItem("Shutdown", KeyEvent.VK_Q);
		menuQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		menuQuit.getAccessibleContext().setAccessibleDescription("Shut the bot down");
		menuQuit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		mainMenu.add(menuSend);
		mainMenu.add(menuQuit);
		monitorFrame.setJMenuBar(menuBar);
		// set up the frame
		monitorFrame.setTitle("Server Monitor");
		monitorFrame.setSize(600, 800);
		monitorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		monitorFrame.setResizable(false);
		monitorFrame.setLocationRelativeTo(null);
		monitorFrame.getContentPane().setLayout(null);
		// set up the text area
		monitorTextArea.setToolTipText("");
		monitorTextArea.setEditable(false);
		// set up the frame to hold the text area
		monitorScrollPane.getViewport().add(monitorTextArea);
		monitorScrollPane.setPreferredSize(new Dimension(monitorFrame.getWidth() - 20, monitorFrame.getHeight() - 55));
		monitorPanel.add(monitorScrollPane);
		monitorPanel.setBounds(-5, 0, monitorFrame.getWidth(), monitorFrame.getHeight());
		monitorFrame.getContentPane().add(monitorPanel);
		// show frame
		monitorFrame.setVisible(true);
	}

	private static JFrame getFrame() {
		return monitorFrame;
	}

	/**
	 * Prints a message to the monitor
	 * 
	 * @param message
	 *            message to print
	 */
	public static void print(String message) {
		if (showInterface) {
			if (monitorTextArea.getLineCount() > Config.sett_int.get("SCROLLBACK")) {
				monitorTextArea.remove(0);
			}
			monitorTextArea.append(message + "\n");
		} else {
			System.out.println(message);
		}
	}

}