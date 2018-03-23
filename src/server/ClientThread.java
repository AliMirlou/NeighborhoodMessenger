package server;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class ClientThread extends Thread {

	int id;

	private Socket socket;
	private Socket secondPerson;
	private Socket onlines;

	String username;

	int indexFirst;
	int indexSecond = -1;

	boolean loggedIn = false;

	private PrintWriter fileW;
	PrintWriter out;
	private PrintWriter outSecond = null;
	private BufferedReader in;
	private PrintWriter onlineUpdater;

	private boolean run = true;

	public ClientThread(Socket publicSocket, Socket onlineS, int i) {

		socket = publicSocket;
		id = i;
		onlines = onlineS;

	}

	public void run() {
		try {
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			onlineUpdater = new PrintWriter(new BufferedWriter(new OutputStreamWriter(onlines.getOutputStream())),
					true);
			while (run && (!loggedIn) || (outSecond == null)) {
				String firstChoice = in.readLine();
				commandHandler(firstChoice);
				while (run && loggedIn && (indexSecond != -1)) {
					String i = in.readLine();
					if ((i.length() != 0) && (i.charAt(0) == '$')) {
						commandHandler(i);
						continue;
					}
					fileW.println(username + ": " + i);
					if (indexSecond == 0)
						new Thread(new Lobby(username + ": " + i, id)).start();
					else if (Server.clientThread
							.elementAt(Users.users.elementAt(indexSecond).id).indexSecond == indexFirst)
						outSecond.println(username + ": " + i);
					else
						outSecond.println("$connect_" + username);
				}
			}
		} catch (IOException e) {
			return;
		}
	}

	private void commandHandler(String i) {
		String[] command = i.split("_");
		switch (command[0].substring(1, command[0].length())) {
		case "changeUser":
			changeUser(command[1]);
			break;
		case "file":
			fileHandle(command[1], command[2]);
			break;
		case "loginTry":
			catchLogin(command[1]);
			break;
		case "registerTry":
			catchRegister(command[1]);
			break;
		case "exit":
			if (loggedIn)
				logout();
			System.out.println("Client " + id + " has disconnected.");
			run = false;
			break;
		case "logout":
			logout();
			out.println("$logout");
			break;
		}
	}

	private void fileHandle(String string, String name) {

		try {
			if (string.equals(string)) {
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				BufferedImage im = (BufferedImage) ois.readObject();
				outSecond.println("$image");
				ObjectOutputStream oos = new ObjectOutputStream(secondPerson.getOutputStream());
				oos.writeObject(im);
				oos.flush();
				oos.close();
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void logout() {
		loggedIn = false;
		Users.users.elementAt(indexFirst).socket = null;

		for (int i = 0; i < Server.clientThread.size(); i++) {
			if ((Server.clientThread.elementAt(i).loggedIn) && (i != id)) {
				Server.clientThread.elementAt(i).onlineUpdater.println("$removeOnline_" + username);
			}
		}

		System.out.println("User " + username + " has logged out. (" + id + ")");
	}

	private void changeUser(String string) {
		int tempIndex = Users.find(string);
		if (tempIndex != -1) {
			if (Users.users.elementAt(tempIndex).socket != null) {
				indexSecond = tempIndex;
				try {
					out.println("$changeUserResult_successful_" + string);
					File file;
					if (indexSecond != 0) {
						file = new File("chat history/" + username + "~" + Users.users.elementAt(indexSecond).username
								+ ".txt");
						if (!file.exists())
							file = new File("chat history/" + Users.users.elementAt(indexSecond).username + "~"
									+ username + ".txt");
						secondPerson = Users.users.elementAt(indexSecond).socket;
						outSecond = new PrintWriter(
								new BufferedWriter(new OutputStreamWriter(secondPerson.getOutputStream())), true);
					} else
						file = new File("chat history/Lobby.txt");
					fileW = new PrintWriter(new BufferedWriter(new FileWriter(file, true)), true);
					Scanner s = new Scanner(new BufferedReader(new FileReader(file)));
					out.println("$history_start");
					while (s.hasNext())
						out.println(s.nextLine());
					out.println("$history_end");
					s.close();
					System.out.println(username + " changed it's second person to " + string + ".");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else
				out.println("$changeUserResult_notConnected");
		} else
			out.println("$changeUserResult_notFound");
	}

	private void catchLogin(String user) {

		indexFirst = Users.find(user);
		if (indexFirst != -1) {
			if (!Users.users.elementAt(indexFirst).ban) {
				if (Users.users.elementAt(indexFirst).socket == null) {
					out.println("$login_userFound");
					try {
						DataInputStream dis = new DataInputStream(socket.getInputStream());
						int length = dis.readInt();
						byte[] pass = new byte[length];
						dis.readFully(pass);
						char[] charPass = new char[length];
						for (int i = 0; i < charPass.length; i++)
							charPass[i] = (char) pass[i];
						if (Arrays.equals(charPass, Users.users.elementAt(indexFirst).password.toCharArray())) {
							loggedIn = true;
							username = user;
							Users.users.elementAt(indexFirst).socket = socket;
							Users.users.elementAt(indexFirst).id = id;
							System.out.println("Client " + username + " has been successfully logged in. (" + id + ")");
							out.println("$login_success");

							for (int i = 0; i < Server.clientThread.size(); i++) {
								if ((Server.clientThread.elementAt(i).loggedIn) && (i != id)) {
									Server.clientThread.elementAt(i).onlineUpdater.println("$addOnline_" + username);
									onlineUpdater.println("$addOnline_" + Server.clientThread.elementAt(i).username);
								}
							}
						} else {
							out.println("$login_error_password");
						}
					} catch (IOException e) {
					}
				} else {
					out.println("$login_error_userAlreadyOn");
				}
			} else {
				out.println("$login_error_userBanned");
			}
		} else {
			out.println("$login_error_userNotFound");
		}
	}

	private void catchRegister(String user) {

		indexFirst = Users.find(user);
		if (indexFirst == -1) {
			out.println("$registerResult_usernameOK");
			try {
				if (in.readLine().equals("$continue")) {
					String charPass = in.readLine();
					Users.addUser(user, charPass);
					System.out.println("A new user has successfully registered. (" + user + ")");
					out.println("$registerResult_success");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			out.println("$registerResult_error_usernameExists");
		}

	}

}
