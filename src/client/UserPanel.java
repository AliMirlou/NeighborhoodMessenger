package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class UserPanel extends JPanel {

	private static final long serialVersionUID = 101L;

	static int selectedUser = -1;

	public int index;
	public JLabel username;

	public UserPanel(int width, int height, int i, String user) {

		setPreferredSize(new Dimension(width, height));
		setBackground(Color.WHITE);
		setLayout(new BorderLayout());

		index = i;
		username = new JLabel(user, JLabel.CENTER);
		username.setFont(new Font("", Font.BOLD, 20));
		add(username, BorderLayout.CENTER);

		addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
				if (index != selectedUser)
					setBackground(Color.WHITE);
			}

			public void mouseEntered(MouseEvent e) {
				if (index != selectedUser)
					setBackground(Color.GRAY);
			}

			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					JPopupMenu options = new JPopupMenu();
					options.show(e.getComponent(), e.getX(), e.getY());
				} else {
					if (selectedUser != -1)
						OnlineUsersPanel.onlines.elementAt(selectedUser).setBackground(Color.WHITE);
					setBackground(new Color(50, 240, 50));
					selectedUser = index;
					Client.out.println("$changeUser_" + username.getText());
					ClientGUI.message.setVisible(true);
					ClientGUI.messages.setVisible(true);
					ClientGUI.messages.removeAll();
				}
			}
		});

	}

}
