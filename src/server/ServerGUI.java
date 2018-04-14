package server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerGUI extends JFrame {

	private static final long serialVersionUID = 8268252063967577738L;

	private Server server;
	private FileServer fileServer;

	static JTextArea users;

	JButton upload = new JButton("Upload A New File To Server");
	JButton fileStop = new JButton("Close File Server");
	JButton mainStop = new JButton("Close Main Server");
	JButton allStop = new JButton("Close All Server");

	public ServerGUI() {
		users = new JTextArea();
		users.setEditable(false);
		users.setBackground(Color.LIGHT_GRAY);
		add(users, BorderLayout.NORTH);

		JTextField command = new JTextField();
		command.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = command.getText();
				int space = input.indexOf(' ');
				String c = input.substring(0, space);
				if (c.equals("ban")) {
					Users.users.elementAt(Users.find(input.substring(space + 1, input.length()))).ban = true;
				}
			}
		});
		add(command, BorderLayout.CENTER);

		try {
			server = new Server();
			fileServer = new FileServer();
			fileServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Box group = Box.createHorizontalBox();

		group.add(upload, BorderLayout.NORTH);
		upload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File("."));
				fc.setMultiSelectionEnabled(true);
				if (fc.showDialog(null, "Select") == JFileChooser.APPROVE_OPTION) {
					File[] files = fc.getSelectedFiles();
					for (int i = 0; i < files.length; i++) {
						try {
							Files.copy(files[i].toPath(), new File("storage/" + files[i].getName()).toPath(),
									StandardCopyOption.COPY_ATTRIBUTES);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});

		group.add(mainStop);
		mainStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					server.publicServer.close();
					server.onlinesServer.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				dispose();
				System.exit(0);
			}
		});

		group.add(fileStop);
		fileStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					fileServer.fileServer.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		group.add(allStop);
		allStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					server.publicServer.close();
					server.onlinesServer.close();
					fileServer.fileServer.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});

		add(group, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocation(200, 50);
		setPreferredSize(new Dimension(1000, 700));
		pack();
		setVisible(true);
	}

}
