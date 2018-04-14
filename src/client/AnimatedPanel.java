package client;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class AnimatedPanel extends JLayeredPane {

	private static final long serialVersionUID = -6358400323949431910L;

	private int speed;
	private Timer timer;
	private int width;
	private JPanel back;

	public AnimatedPanel(int height, int width, Color color) {

		setPreferredSize(new Dimension(0, height));
		back = new JPanel();
		back.setBackground(color);
		back.setPreferredSize(new Dimension(width, height));
		add(back, 0, 0);

		this.width = width;
		speed = -width / 250;

		timer = new Timer();

	}

	void showOrHide() {

		speed *= -1;
		timer.cancel();
		timer.purge();
		timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				setPreferredSize(new Dimension(getWidth() + speed, getHeight()));
				revalidate();
				repaint();
				if ((getWidth() + speed >= (width / 5)) || (getWidth() + speed <= 0)) {
					timer.cancel();
					timer.purge();
				}
			}
		};
		timer.scheduleAtFixedRate(task, 0, 1);

	}

	boolean isOpen() {
		return (speed > 0) ? true : false;
	}

}
