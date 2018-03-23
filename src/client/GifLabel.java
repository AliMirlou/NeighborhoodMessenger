package client;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class GifLabel extends JLabel {

	private static final long serialVersionUID = 22L;

	private BufferedImage image;
	private RescaleOp rescale;

	private float direction = 1.15f;

	private Timer timer;
	private TimerTask timerTask;

	public GifLabel() {
		try {
			image = toBufferedImage((ImageIO.read(new File("back.png")).getScaledInstance(ClientGUI.frameWidth,
					ClientGUI.frameHeight, Image.SCALE_DEFAULT)));
			setIcon(new ImageIcon(image));

			timerTask = new TimerTask() {
				private int count = 25;

				public void run() {
					count++;
					if (count > 50) {
						count = 0;
						if (direction < 0)
							direction = 1.15f;
						else
							direction = -0.02f;
					}
					rescale = new RescaleOp(1, direction, null);
					rescale.filter(image, image);
					repaint();
				}
			};
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage)
			return (BufferedImage) img;

		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		return bimage;
	}

	public void start() {
		timer = new Timer();
		timer.scheduleAtFixedRate(timerTask, 0, 100);
	}

	public void stop() {
		timer.cancel();
		timer.purge();
	}

}
