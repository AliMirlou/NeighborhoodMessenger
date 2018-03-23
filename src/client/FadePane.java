package client;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class FadePane extends JPanel {

	private static final long serialVersionUID = 20L;

	private float direction = -0.05f;
	private FadeLabel label;

	public FadePane(String back, String fadeBack, String text) {
		setLayout(new BorderLayout());
		JLabel background = new JLabel();
		background.setLayout(new GridBagLayout());
		try {
			background.setIcon(new ImageIcon(ImageIO.read(new File(back))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		add(background);

		label = new FadeLabel(fadeBack, text);
		background.add(label);

		Timer timer = new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				float alpha = label.getAlpha();
				alpha += direction;
				if (alpha < 0) {
					alpha = 0;
					direction = 0.05f;
				} else if (alpha > 1) {
					alpha = 1;
					direction = -0.05f;
				}
				label.setAlpha(alpha);
			}
		});
		timer.setRepeats(true);
		timer.setCoalesce(true);
		timer.start();
	}

	private class FadeLabel extends JLabel {

		private static final long serialVersionUID = 21L;

		private float alpha;
		private BufferedImage background;

		public FadeLabel(String back, String text) {
			if (back != null) {
				try {
					background = ImageIO.read(getClass().getResource(back));
				} catch (Exception e) {
				}
			}
			setText(text);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
			setAlpha(1f);
		}

		public void setAlpha(float value) {
			if (alpha != value) {
				float old = alpha;
				alpha = value;
				firePropertyChange("alpha", old, alpha);
				repaint();
			}
		}

		public float getAlpha() {
			return alpha;
		}

		public Dimension getPreferredSize() {
			return background == null ? super.getPreferredSize()
					: new Dimension(background.getWidth(), background.getHeight());
		}

		public void paint(Graphics g) {
			// This is one of the few times I would directly override paint
			// This makes sure that the entire paint chain is now using
			// the alpha composite, including borders and child components
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));
			super.paint(g2d);
			g2d.dispose();
		}

		protected void paintComponent(Graphics g) {
			// This is one of the few times that doing this before the super call
			// will work...
			if (background != null) {
				int x = (getWidth() - background.getWidth()) / 2;
				int y = (getHeight() - background.getHeight()) / 2;
				g.drawImage(background, x, y, this);
			}
			super.paintComponent(g);
		}
	}
}
