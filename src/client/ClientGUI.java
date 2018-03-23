package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class ClientGUI extends JFrame {

	private static final long serialVersionUID = 2L;

	private Client client;
	private Socket onlines;

	static int frameWidth;
	static int frameHeight;

	protected int pX;
	protected int pY;

	private JPanel c;
	private SpringLayout sp;

	private JMenuBar menubar = new JMenuBar();
	private JMenu logo = new JMenu();
	private JMenuItem setting = new JMenuItem("Settings");
	private JMenuItem recFile = new JMenuItem("Show/Hide File Panel...");
	private JMenuItem about = new JMenuItem("About");
	private JMenuItem minimize = new JMenuItem("Minimize");
	private JMenuItem exit = new JMenuItem("Exit");
	private JMenu account = new JMenu("Sign in/Register");
	private JMenuItem signin = new JMenuItem("Sign in");
	private JMenuItem register = new JMenuItem("Register");
	private JMenuItem profile = new JMenuItem("Profile");
	private JMenuItem signout = new JMenuItem("Sign Out");

	private Image banner;
	private GifLabel gifBackLabel;
	private JLabel blurBackLabel;

	static JTextField message = new JTextField(0);
	static MessageArea messages;
	private FilePanel filePanel;
	private OnlineUsersPanel onlinePanel;
	// private NotificationFrame notif;

	private Font font = new Font("", Font.BOLD, 20);

	private class KeyboardDispatcher implements KeyEventDispatcher {
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.getID() == KeyEvent.KEY_PRESSED) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (page == 1) {
						handleLogin();
					} else if (page == 2) {
						handleRegister();
					} else if (page == 3) {
						String text = message.getText();
						Client.out.println(text);
						messages.addMessagePanel("Me: " + text);
						message.setText("");
					}
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					if (page == 3) {
						message.setVisible(false);
						messages.setVisible(false);
						OnlineUsersPanel.onlines.elementAt(UserPanel.selectedUser).setBackground(Color.WHITE);
						UserPanel.selectedUser = -1;
					}
				}
			}
			return false;
		}
	}

	KeyboardDispatcher dispatch = new KeyboardDispatcher();
	KeyboardFocusManager manager;
	int page = 0;

	JTextField userText = new JTextField();
	JLabel userError = new JLabel();

	JPasswordField passwordText = new JPasswordField();
	JLabel passError = new JLabel();

	JTextField registerPass = new JTextField();

	public ClientGUI() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		try {
			setIconImage(ImageIO.read(new File("icon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		setUndecorated(true);
		setResizable(false);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenSize.getWidth();
		int screenHeight = (int) screenSize.getHeight();
		setPreferredSize(new Dimension(screenWidth * 7 / 12, screenHeight * 7 / 12));
		setLocation(screenWidth * 5 / 24, screenHeight * 5 / 24);
		frameWidth = screenWidth * 7 / 12;
		frameHeight = screenHeight * 7 / 12;

		sp = new SpringLayout();
		c = new JPanel(sp);
		setContentPane(c);

		blurBackLabel = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().createImage("NeighborhoodBlured.jpg")
				.getScaledInstance(frameWidth, frameHeight, Image.SCALE_DEFAULT)));

		banner = Toolkit.getDefaultToolkit().createImage("banner.png");

		// notif = new NotificationFrame(screenWidth, screenHeight);

		manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(dispatch);

		buildMenu();

		WelcomeGUI();
	}

	private void buildMenu() {

		try {
			logo.setIcon(new ImageIcon(ImageIO.read(new File("icon.png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		logo.add(setting);
		logo.add(about);
		logo.addSeparator();
		logo.add(minimize);
		logo.add(exit);

		changeMenu(0);

		menubar.add(logo);
		menubar.add(Box.createHorizontalGlue());
		menubar.add(account);

		menubar.setBackground(new Color(50, 240, 50));
		menubar.setBorderPainted(false);

		menuItemsActions();

		dragAndMove(menubar);

		setJMenuBar(menubar);

	}

	private void menuItemsActions() {

		recFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filePanel.showOrHide();
				filePanel.searchField.requestFocusInWindow();
			}
		});

		minimize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setState(ICONIFIED);
			}
		});

		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Client.out.println("$exit");
				} catch (Exception useless) {
				}
				dispose();
				System.gc();
				System.exit(0);
			}
		});

		signin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LoginGUI();
			}
		});

		register.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RegisterGUI();
			}
		});

		signout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.out.println("$logout");
			}
		});

	}

	private void WelcomeGUI() {

		page = 0;
		c.removeAll();

		JLabel loading = new JLabel();
		loading.setFont(new Font("", Font.BOLD, 30));
		loading.setForeground(new Color(255, 255, 255));
		sp.putConstraint(SpringLayout.WEST, loading, 5, SpringLayout.WEST, c);
		sp.putConstraint(SpringLayout.SOUTH, loading, -5, SpringLayout.SOUTH, c);
		c.add(loading);

		JLabel gifStorage = new JLabel();
		gifStorage.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage("loading.gif")
				.getScaledInstance(frameWidth / 10, frameHeight / 5, Image.SCALE_DEFAULT)));
		sp.putConstraint(SpringLayout.EAST, gifStorage, 0, SpringLayout.EAST, c);
		sp.putConstraint(SpringLayout.SOUTH, gifStorage, 0, SpringLayout.SOUTH, c);
		c.add(gifStorage);

		c.add(blurBackLabel);

		revalidate();
		repaint();
		pack();
		setVisible(true);

		while (true) {

			loading.setText("Connecting To Server");
			pack();
			// try {
			// Thread.sleep(1000);
			// } catch (InterruptedException e1) {
			// e1.printStackTrace();
			// }

			try {

				client = new Client();
				onlines = new Socket("localhost", 9999);

				filePanel = new FilePanel(frameHeight, frameWidth, new Color(130, 130, 130));

				LoginGUI();
				return;

			} catch (IOException e) {
				loading.setText("Server Not Available... Retrying Again In A Second");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}

		}

	}

	private void LoginGUI() {

		page = 1;
		c.removeAll();

		gifBackLabel = new GifLabel();
		gifBackLabel.start();

		changeMenu(0);

		JLabel bannerLabel = new JLabel(new ImageIcon(banner));
		sp.putConstraint(SpringLayout.WEST, bannerLabel, frameWidth / 2 - 550, SpringLayout.WEST, c);
		sp.putConstraint(SpringLayout.NORTH, bannerLabel, 100, SpringLayout.NORTH, c);
		c.add(bannerLabel);

		///////////////////////////////////////////////////////////////////////////////////////////////////////

		JPanel userPanel = new JPanel();
		userPanel.setPreferredSize(new Dimension(frameWidth / 3, frameHeight / 20 + 200));
		userPanel.setOpaque(false);

		JLabel userLabel = new JLabel("Username");
		userLabel.setPreferredSize(new Dimension(100, 70));
		userPanel.add(userLabel);

		userText.setHorizontalAlignment(JTextField.CENTER);
		userText.setPreferredSize(new Dimension(frameWidth / 3, frameHeight / 20));
		userPanel.add(userText);

		userError.setForeground(new Color(255, 0, 0));
		userPanel.add(userError);

		sp.putConstraint(SpringLayout.WEST, userPanel, frameWidth / 3, SpringLayout.WEST, c);
		sp.putConstraint(SpringLayout.NORTH, userPanel, frameHeight / 4, SpringLayout.NORTH, c);
		c.add(userPanel);

		///////////////////////////////////////////////////////////////////////////////////////////////////////

		JPanel passPanel = new JPanel();
		passPanel.setPreferredSize(new Dimension(frameWidth / 3, frameHeight / 20 + 110));
		passPanel.setOpaque(false);

		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setPreferredSize(new Dimension(100, 70));
		passPanel.add(passwordLabel);

		passwordText.setHorizontalAlignment(JTextField.CENTER);
		passwordText.setEchoChar('*');
		passwordText.setPreferredSize(new Dimension(frameWidth / 3, frameHeight / 20));
		passPanel.add(passwordText);

		passError.setForeground(new Color(255, 0, 0));
		passPanel.add(passError);

		sp.putConstraint(SpringLayout.WEST, passPanel, frameWidth / 3, SpringLayout.WEST, c);
		sp.putConstraint(SpringLayout.NORTH, passPanel, frameHeight * 5 / 12, SpringLayout.NORTH, c);
		c.add(passPanel);

		///////////////////////////////////////////////////////////////////////////////////////////////////////

		JPanel buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(frameWidth / 5, frameHeight / 20 + 150));
		buttonPanel.setOpaque(false);

		JButton loginButton = new JButton();
		loginButton.setOpaque(false);
		loginButton.setFocusPainted(false);
		loginButton.setBorderPainted(false);
		loginButton.setContentAreaFilled(false);
		loginButton.setPreferredSize(new Dimension(frameWidth / 5, frameHeight / 16));
		try {
			loginButton.setIcon(new ImageIcon(ImageIO.read(new File("Buttons/login.png"))));
			loginButton.setRolloverIcon(new ImageIcon(ImageIO.read(new File("Buttons/login hover.png"))));
			loginButton.setPressedIcon(new ImageIcon(ImageIO.read(new File("Buttons/login pressed.png"))));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		buttonPanel.add(loginButton);

		JPanel seperator = new JPanel();
		seperator.setPreferredSize(new Dimension(frameWidth / 4, frameHeight / 30));
		seperator.setOpaque(false);
		buttonPanel.add(seperator);

		JButton registerButton = new JButton("New To The Neighborhood? Click To Register!");
		registerButton.setOpaque(false);
		registerButton.setFocusPainted(false);
		registerButton.setBorderPainted(false);
		registerButton.setContentAreaFilled(false);
		buttonPanel.add(registerButton);

		sp.putConstraint(SpringLayout.WEST, buttonPanel, frameWidth * 2 / 5, SpringLayout.WEST, c);
		sp.putConstraint(SpringLayout.NORTH, buttonPanel, frameHeight * 15 / 24, SpringLayout.NORTH, c);
		c.add(buttonPanel);

		///////////////////////////////////////////////////////////////////////////////////////////////////////

		userText.requestFocusInWindow();
		c.add(gifBackLabel);
		changeFont(c, font);
		pack();

		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				handleLogin();
			}
		});

		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RegisterGUI();
			}
		});

	}

	private void RegisterGUI() {

		page = 2;
		c.removeAll();

		///////////////////////////////////////////////////////////////////////////////////////////////////////

		JPanel userPanel = new JPanel();
		userPanel.setPreferredSize(new Dimension(frameWidth / 3, frameHeight / 20 + 200));
		userPanel.setOpaque(false);

		JLabel userLabel = new JLabel("Enter A Username");
		userLabel.setPreferredSize(new Dimension(200, 70));
		userPanel.add(userLabel);

		userText.setHorizontalAlignment(JTextField.CENTER);
		userText.setPreferredSize(new Dimension(frameWidth / 3, frameHeight / 20));
		// userText.setToolTipText("<html><font size=\"6\">Just don't start it with
		// $</font></html>");
		userPanel.add(userText);

		userError.setForeground(new Color(255, 0, 0));
		userPanel.add(userError);

		sp.putConstraint(SpringLayout.WEST, userPanel, frameWidth / 3, SpringLayout.WEST, c);
		sp.putConstraint(SpringLayout.NORTH, userPanel, frameHeight / 4, SpringLayout.NORTH, c);
		c.add(userPanel);

		JPanel userReq = new JPanel();
		userReq.setLayout(new BoxLayout(userReq, BoxLayout.Y_AXIS));
		userReq.add(new JLabel("Just don't start it with $"));
		userReq.add(Box.createVerticalStrut(4));
		userReq.add(new JLabel("You can't change it later"));
		userReq.setOpaque(false);
		sp.putConstraint(SpringLayout.WEST, userReq, 30, SpringLayout.EAST, userPanel);
		sp.putConstraint(SpringLayout.NORTH, userReq, 95, SpringLayout.NORTH, userPanel);
		c.add(userReq);
		userReq.setVisible(false);

		userText.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				userReq.setVisible(false);
			}

			public void focusGained(FocusEvent e) {
				userReq.setVisible(true);
			}
		});

		///////////////////////////////////////////////////////////////////////////////////////////////////////

		JPanel passPanel = new JPanel();
		passPanel.setPreferredSize(new Dimension(frameWidth / 3, frameHeight / 20 + 110));
		passPanel.setOpaque(false);

		JLabel passwordLabel = new JLabel("Enter A Password");
		passwordLabel.setPreferredSize(new Dimension(200, 70));
		passPanel.add(passwordLabel);

		registerPass.setHorizontalAlignment(JTextField.CENTER);
		registerPass.setPreferredSize(new Dimension(frameWidth / 3, frameHeight / 20));
		passPanel.add(registerPass);

		passError.setForeground(new Color(255, 0, 0));
		passPanel.add(passError);

		sp.putConstraint(SpringLayout.WEST, passPanel, frameWidth / 3, SpringLayout.WEST, c);
		sp.putConstraint(SpringLayout.NORTH, passPanel, frameHeight * 5 / 12, SpringLayout.NORTH, c);
		c.add(passPanel);

		JPanel passReq = new JPanel();
		passReq.add(new JLabel("Just don't forget your password... :)"));
		passReq.setOpaque(false);
		sp.putConstraint(SpringLayout.WEST, passReq, 30, SpringLayout.EAST, passPanel);
		sp.putConstraint(SpringLayout.NORTH, passReq, 95, SpringLayout.NORTH, passPanel);
		c.add(passReq);
		passReq.setVisible(false);

		registerPass.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				passReq.setVisible(false);
			}

			public void focusGained(FocusEvent e) {
				passReq.setVisible(true);
			}
		});

		///////////////////////////////////////////////////////////////////////////////////////////////////////

		JButton registerButton = new JButton();
		registerButton.setOpaque(false);
		registerButton.setFocusPainted(false);
		registerButton.setBorderPainted(false);
		registerButton.setContentAreaFilled(false);
		registerButton.setPreferredSize(new Dimension(frameWidth / 5, frameHeight / 16));
		try {
			registerButton.setIcon(new ImageIcon(ImageIO.read(new File("Buttons/register.png"))));
			registerButton.setRolloverIcon(new ImageIcon(ImageIO.read(new File("Buttons/register hover.png"))));
			registerButton.setPressedIcon(new ImageIcon(ImageIO.read(new File("Buttons/register pressed.png"))));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		sp.putConstraint(SpringLayout.WEST, registerButton, frameWidth * 2 / 5, SpringLayout.WEST, c);
		sp.putConstraint(SpringLayout.NORTH, registerButton, frameHeight * 15 / 24, SpringLayout.NORTH, c);
		c.add(registerButton);

		///////////////////////////////////////////////////////////////////////////////////////////////////////

		c.add(gifBackLabel);
		changeFont(c, font);
		pack();

		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleRegister();
			}
		});

	}

	private void MainGUI() {

		page = 3;

		gifBackLabel.stop();
		c.removeAll();

		changeMenu(1);

		onlinePanel = new OnlineUsersPanel(frameWidth / 5, frameHeight);
		onlinePanel.addPanel(frameWidth / 5, 70, "Lobby");
		JScrollPane jsp = new JScrollPane(onlinePanel);
		sp.putConstraint(SpringLayout.WEST, jsp, 0, SpringLayout.WEST, c);
		add(jsp);
		new Thread(new Runnable() {
			public void run() {
				BufferedReader onlinesReceiver = null;
				try {
					onlinesReceiver = new BufferedReader(new InputStreamReader(onlines.getInputStream()));
				} catch (IOException e) {
					e.printStackTrace();
				}
				while (true) {
					try {
						String[] onlineCommand = onlinesReceiver.readLine().split("_");
						if (onlineCommand[0].equals("$addOnline"))
							onlinePanel.addPanel(frameWidth / 5, 70, onlineCommand[1]);
						else
							onlinePanel.removePanel(onlineCommand[1]);
					} catch (IOException e) {
						break;
					}
				}
				try {
					onlines.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				WelcomeGUI();
				return;
			}
		}).start();

		JButton send = new JButton("Media");
		JPopupMenu sendPop = new JPopupMenu();
		sp.putConstraint(SpringLayout.EAST, send, 0, SpringLayout.EAST, c);
		sp.putConstraint(SpringLayout.SOUTH, send, 0, SpringLayout.SOUTH, c);
		JMenuItem image = new JMenuItem("Image");
		JMenuItem voice = new JMenuItem("Voice");
		sendPop.add(image);
		sendPop.add(voice);
		send.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				sendPop.show(e.getComponent(), e.getX(), e.getY());
			}
		});
		image.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(new File("."));
				if (chooser.showOpenDialog(new JFrame()) == JFileChooser.APPROVE_OPTION) {
					// client.sendFile(chooser.getSelectedFile(), 0);
				}
			}
		});
		c.add(send);

		sp.putConstraint(SpringLayout.EAST, filePanel, 0, SpringLayout.EAST, c);
		c.add(filePanel);

		sp.putConstraint(SpringLayout.SOUTH, message, 0, SpringLayout.SOUTH, c);
		sp.putConstraint(SpringLayout.EAST, message, 0, SpringLayout.WEST, send);
		sp.putConstraint(SpringLayout.WEST, message, 0, SpringLayout.EAST, jsp);
		message.setVisible(false);
		c.add(message);

		messages = new MessageArea(0, 0);
		JScrollPane messageArea = new JScrollPane(messages);
		messageArea.getViewport().setOpaque(false);
		sp.putConstraint(SpringLayout.WEST, messageArea, 0, SpringLayout.EAST, jsp);
		sp.putConstraint(SpringLayout.NORTH, messageArea, 0, SpringLayout.NORTH, c);
		sp.putConstraint(SpringLayout.SOUTH, messageArea, 0, SpringLayout.NORTH, message);
		sp.putConstraint(SpringLayout.EAST, messageArea, 0, SpringLayout.EAST, c);
		messageArea.setOpaque(false);
		messages.setVisible(false);
		c.add(messageArea);

		new Thread(new Runnable() {
			public void run() {
				try {
					int commandResult;
					String temp = client.in.readLine();
					// messages.addImagePanel(ImageIO.read(new File("banner.png")));
					while (true) {
						if ((temp.length() != 0) && (temp.charAt(0) == '$')) {
							commandResult = client.handle(temp);
							if (commandResult == 0) {
								LoginGUI();
								return;
							} else if (commandResult == 2) {
								while (true) {
									temp = client.in.readLine();
									if (temp.charAt(0) == '$')
										break;
									if (temp.startsWith(client.me))
										temp = "Me" + temp.substring(temp.indexOf(":"));
									messages.addMessagePanel(temp);
									messageArea.getVerticalScrollBar()
											.setValue(messageArea.getVerticalScrollBar().getMaximum());
								}
							}
							// else if(commandResult == 3)
							// notif.showNewNotif(temp.substring(10));
							else
								temp = client.in.readLine();
							continue;
						}
						messages.addMessagePanel(temp);
						temp = client.in.readLine();
					}
				} catch (IOException e) {
					return;
				}
			}
		}).start();

		c.add(blurBackLabel);
		message.requestFocusInWindow();
		changeFont(c, font);
		pack();

	}

	private void changeMenu(int i) {

		account.removeAll();
		switch (i) {
		case 0:
			account.setIcon(null);
			account.setText("Sign in/Register");
			account.add(signin);
			account.add(register);
			break;
		case 1:
			account.setText(client.me);
			try {
				account.setIcon(new ImageIcon(ImageIO.read(new File("users/unknown.png"))));
			} catch (IOException e) {
				e.printStackTrace();
			}
			account.add(profile);
			account.add(recFile);
			account.add(signout);
			logo.getItem(1).setEnabled(true);
			break;
		}

	}

	private void handleLogin() {

		if (!userText.getText().equals("")) {
			userError.setText("");
			if (passwordText.getPassword().length != 0) {
				int result = client.login(userText.getText(), passwordText.getPassword());
				switch (result) {
				case 0:
					passError.setText("");
					changeMenu(1);
					MainGUI();
					return;
				case 1:
					userError.setText("No user was found with the given username");
					return;
				case 2:
					passError.setText("Wrong password for the given username");
					return;
				case 3:
					userError.setText("Another device is logged in with the given username");
					return;
				case 4:
					userError.setText("This user is banned from the server");
					return;
				}
				passwordText.setText("");
			} else
				passError.setText("Password Required");
		} else {
			userError.setText("Username Required");
			if (passwordText.getPassword().length == 0)
				passError.setText("Password Required");
			else
				passError.setText("");
		}

	}

	public void handleRegister() {

		if (!userText.getText().equals("")) {
			if (!(userText.getText().charAt(0) == '$')) {
				userError.setText("");
				if (registerPass.getText().length() != 0) {
					int result = client.register(userText.getText(), registerPass.getText());
					switch (result) {
					case 0:
						passError.setText("");
						LoginGUI();
						return;
					case 1:
						userError.setText("The given username already exists");
						return;
					}
					registerPass.setText("");
				} else
					passError.setText("Password Required");
			} else
				userError.setText("Username Not Valid");
		} else {
			userError.setText("Username Required");
			if (registerPass.getText().length() == 0)
				passError.setText("Password Required");
			else
				passError.setText("");
		}

	}

	private void changeFont(Component component, Font font) {
		component.setFont(font);
		if (component instanceof Container) {
			for (Component child : ((Container) component).getComponents()) {
				changeFont(child, font);
			}
		}
	}

	private void dragAndMove(Component component) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				pX = e.getX();
				pY = e.getY();
			}
		});
		component.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent evt) {
				setLocation(getLocation().x + evt.getX() - pX, getLocation().y + evt.getY() - pY);
			}
		});
	}

}
