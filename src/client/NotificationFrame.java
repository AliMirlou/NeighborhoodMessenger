package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class NotificationFrame extends JFrame {

	private static final long serialVersionUID = 4337300547394296177L;

	JLabel defaultText = new JLabel("You have new messages from ");
	JLabel name = new JLabel("", JLabel.CENTER);

	public NotificationFrame(int width, int height) {
		setUndecorated(true);
		setPreferredSize(new Dimension(width / 10, height / 15));
		setLocation(width - (width / 10) - 100, height - (height / 15) - 150);
		add(defaultText, BorderLayout.NORTH);
		getContentPane().setBackground(Color.BLACK);
		defaultText.setForeground(Color.WHITE);
		name.setForeground(Color.WHITE);
		name.setFont(new Font("", Font.BOLD, 20));
		pack();
	}

	public void showNewNotif(String s) {
		name.setText(s);
		add(name, BorderLayout.CENTER);
		setVisible(true);
	}

}
