package org.snack.irc.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Prompt extends JFrame {

	private static final long serialVersionUID = -9000069368916422378L;

	private final JLabel instructionLabel, typeLabel, targetLabel, messageLabel;
	private final JTextField typeBox, targetBox, messageBox;
	private final JButton saveButton, cancelButton;

	// the add/update prompt
	public Prompt(JFrame frame) {
		super("Send message");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.getContentPane().setLayout(null);

		// set up labels
		instructionLabel = new JLabel("Make the bot send a message.");
		instructionLabel.setBounds(10, 10, 250, 15);
		typeLabel = new JLabel("Type:");
		typeLabel.setBounds(10, 40, 100, 15);
		targetLabel = new JLabel("Target:");
		targetLabel.setBounds(10, 65, 100, 15);
		messageLabel = new JLabel("Message:");
		messageLabel.setBounds(10, 90, 100, 15);
		// add labels
		this.getContentPane().add(instructionLabel);
		this.getContentPane().add(typeLabel);
		this.getContentPane().add(targetLabel);
		this.getContentPane().add(messageLabel);

		// set up fields
		typeBox = new JTextField();
		typeBox.setBounds(50, 37, 250, 20);
		targetBox = new JTextField();
		targetBox.setBounds(50, 62, 250, 20);
		messageBox = new JTextField();
		messageBox.setBounds(50, 87, 250, 20);
		// add fields
		this.getContentPane().add(typeBox);
		this.getContentPane().add(targetBox);
		this.getContentPane().add(messageBox);

		// set up buttons
		saveButton = new JButton("Send");
		saveButton.setBounds(50, 120, 100, 20);
		cancelButton = new JButton("Cancel");
		cancelButton.setBounds(200, 120, 100, 20);
		// add buttons
		this.getContentPane().add(saveButton);
		this.getContentPane().add(cancelButton);

		// add actionlisteners
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String a = typeBox.getText();
				String b = targetBox.getText();
				String c = messageBox.getText();

				// add or update the item
				BotListener.sendCustomMessage(a, b, c);

				// close the frame
				getFrame().dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// close the frame
				getFrame().dispose();
			}
		});

		// adds default info
		typeBox.setText("PRIVMSG");
		targetBox.setText("#");
		messageBox.setText("");

		// finish the frame
		this.pack();
		this.setVisible(true);
		this.setSize(320, 180);
		this.setLocationRelativeTo(frame);
	}

	// used to dispose this box
	private Prompt getFrame() {
		return this;
	}
}