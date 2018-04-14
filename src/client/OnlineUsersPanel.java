package client;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.Scrollable;

public class OnlineUsersPanel extends JPanel implements Scrollable {

	private static final long serialVersionUID = -8538206417145222824L;

	static Vector<UserPanel> onlines = new Vector<>();

	private final BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
	private final Dimension size;

	public OnlineUsersPanel(int width, int height) {
		setLayout(layout);
		size = new Dimension(width, height);
	}

	public void addPanel(int w, int h, String u) {
		onlines.addElement(new UserPanel(w, h, onlines.size(), u));
		add(onlines.lastElement());
		repaint();
		revalidate();
	}

	public void removePanel(String u) {
		int i = findUser(u);
		onlines.removeElementAt(i);
		remove(i);
		if (i == UserPanel.selectedUser) {
			ClientGUI.message.setVisible(false);
			ClientGUI.messages.setVisible(false);
			UserPanel.selectedUser = -1;
		}
		repaint();
		revalidate();
	}

	public Dimension getPreferredScrollableViewportSize() {
		return size;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return getIncrement(orientation);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return getIncrement(orientation);
	}

	private int getIncrement(int orientation) {
		if (orientation == JScrollBar.VERTICAL) {
			return size.height;
		} else {
			return size.width;
		}
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	private int findUser(String u) {
		for (int i = 0; i < onlines.size(); i++)
			if (onlines.elementAt(i).username.getText().equals(u))
				return i;
		return -1;
	}

}
