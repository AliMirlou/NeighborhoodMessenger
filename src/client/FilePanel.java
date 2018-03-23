package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class FilePanel extends AnimatedPanel {

	private static final long serialVersionUID = 12L;

	private SpringLayout sp = new SpringLayout();

	private JLabel searchLabel = new JLabel("Enter the exact name of file to search:", JLabel.CENTER);
	JTextField searchField = new JTextField();
	private JButton search = new JButton("Search For File...");
	private JButton closeLid = new JButton("Hide Panel");

	private FileClient fileClient;
	JProgressBar progress;

	public FilePanel(int height, int width, Color color) {
		super(height / 2, width, color);

		fileClient = new FileClient();

		setLayout(sp);

		sp.putConstraint(SpringLayout.NORTH, searchLabel, 3, SpringLayout.NORTH, this);
		sp.putConstraint(SpringLayout.WEST, searchLabel, 3, SpringLayout.WEST, this);
		add(searchLabel, 1, 0);

		sp.putConstraint(SpringLayout.NORTH, searchField, width / 70, SpringLayout.SOUTH, searchLabel);
		sp.putConstraint(SpringLayout.WEST, searchField, 0, SpringLayout.WEST, searchLabel);
		sp.putConstraint(SpringLayout.EAST, searchField, width / 10, SpringLayout.WEST, searchField);
		add(searchField, 1, 0);

		sp.putConstraint(SpringLayout.WEST, search, 0, SpringLayout.WEST, searchLabel);
		sp.putConstraint(SpringLayout.NORTH, search, width / 70, SpringLayout.SOUTH, searchField);
		add(search, 1, 0);

		sp.putConstraint(SpringLayout.SOUTH, closeLid, -3, SpringLayout.SOUTH, this);
		sp.putConstraint(SpringLayout.EAST, closeLid, -3, SpringLayout.EAST, this);
		add(closeLid, 1, 0);

		Actions();
	}

	private void Actions() {

		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tryToFind();
			}
		});

		closeLid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showOrHide();
			}
		});

	}

	protected void tryToFind() {
		fileClient.connect();
		String a = searchField.getText();
		if (fileClient.search(a)) {
			fade();
		} else
			fileClient.disconnect();
	}

	protected void fade() {

		JPanel fadePanel = new JPanel();
		fadePanel.setBackground(new Color(0, 0, 0, 150));
		fadePanel.setPreferredSize(new Dimension(getWidth(), getHeight()));
		fadePanel.repaint();
		fadePanel.revalidate();
		add(fadePanel, 2, 1);
		JPanel choice = new JPanel();
		choice.setPreferredSize(new Dimension(getWidth(), 150));
		choice.setOpaque(false);
		choice.setLayout(new BorderLayout());
		JLabel q = new JLabel("File found. Download it?", JLabel.CENTER);
		choice.add(q, BorderLayout.CENTER);
		JPanel g = new JPanel();
		g.setOpaque(false);
		JButton yes = new JButton("Yes");
		JButton no = new JButton("No");
		g.add(yes);
		g.add(Box.createHorizontalStrut(5));
		g.add(no);
		g.setPreferredSize(new Dimension(getWidth(), 100));
		choice.add(g, BorderLayout.SOUTH);
		add(choice, 3, 2);
		revalidate();
		repaint();

		no.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileClient.disconnect();
				remove(choice);
				remove(fadePanel);
				revalidate();
				repaint();
			}
		});

		yes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remove(choice);
				progress = new JProgressBar(0, 100);
				progress.setValue(0);
				progress.setStringPainted(true);
				progress.setBorder(BorderFactory.createTitledBorder("Receiveing File..."));
				progress.setSize(getWidth(), 100);
				add(progress, 3, 3);
				revalidate();
				repaint();
				fileClient.receiveFile(searchField.getText(), progress);
				remove(fadePanel);
			}
		});

	}

}
