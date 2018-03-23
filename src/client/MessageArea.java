package client;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.Scrollable;

public class MessageArea extends JPanel implements Scrollable {

	private static final long serialVersionUID = 200L;

	private final Dimension size;
	GridBagConstraints gc;

	public MessageArea(int width, int height) {
		setOpaque(false);
		setLayout(new GridBagLayout());
		gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.VERTICAL;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(3, 3, 2, 0);
		gc.gridy = 0;
		size = new Dimension(width, height);
	}

	public void addMessagePanel(String u) {
		add(new MessagePanel(u), gc);
		gc.gridy++;
		repaint();
		revalidate();
	}

	public void removeMessagePanel(int i) {
		remove(i);
		repaint();
		revalidate();
	}

	public void addImagePanel(BufferedImage read) {
		if (read.getHeight() > 200)
			read = GifLabel.toBufferedImage(
					read.getScaledInstance(read.getWidth() * 200 / read.getHeight(), 200, Image.SCALE_DEFAULT));
		add(new MessagePanel(read), gc);
		gc.gridy++;
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

}
