package client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;

public class MessagePanel extends JPanel {

	private static final long serialVersionUID = 210L;

	private JLabel messageLabel = new JLabel();
	private Font font = new Font("", Font.BOLD, 20);

	public MessagePanel(String message) {

		setBorder(new TextBubbleBorder(Color.WHITE, 30, 30, 0));
		setOpaque(false);

		messageLabel.setText(message);
		messageLabel.setFont(font);
		add(messageLabel);

	}

	public MessagePanel(BufferedImage image) {

		setOpaque(false);

		messageLabel.setIcon(new ImageIcon(image));
		add(messageLabel);

	}

	private class TextBubbleBorder extends AbstractBorder {

		private static final long serialVersionUID = 211L;

		private Color color;
		private int thickness;
		private int radii;
		private Insets insets = null;
		private BasicStroke stroke = null;
		private int strokePad;
		RenderingHints hints;

		TextBubbleBorder(Color color, int thickness, int radii, int pointerSize) {
			this.thickness = thickness;
			this.radii = radii;
			this.color = color;

			stroke = new BasicStroke(thickness);
			strokePad = thickness / 2;

			hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int pad = radii - 20;
			int bottomPad = pad;
			insets = new Insets(pad, pad, bottomPad, pad);
		}

		public Insets getBorderInsets(Component c) {
			return insets;
		}

		public Insets getBorderInsets(Component c, Insets insets) {
			return getBorderInsets(c);
		}

		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

			Graphics2D g2 = (Graphics2D) g;

			int bottomLineY = height - thickness;

			RoundRectangle2D.Double bubble = new RoundRectangle2D.Double(0 + strokePad, 0 + strokePad,
					width - thickness, bottomLineY, radii, radii);

			Polygon pointer = new Polygon();

			// left point
			pointer.addPoint(strokePad + radii, bottomLineY);

			// right point
			pointer.addPoint(strokePad + radii, bottomLineY);

			// bottom point
			pointer.addPoint(strokePad + radii, height - strokePad);

			Area area = new Area(bubble);
			area.add(new Area(pointer));

			g2.setRenderingHints(hints);
			g2.setColor(color);
			g2.fillRoundRect(0 + strokePad, 0 + strokePad, width - thickness, bottomLineY, radii, radii);
			g2.setStroke(stroke);
			g2.draw(area);
		}
	}

}
