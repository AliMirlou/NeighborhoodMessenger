package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

class UserStruct {

	String username;
	String password;
	Socket socket = null;
	int id = -1;
	boolean ban = false;

	public UserStruct(String username, String password) {
		this.username = username;
		this.password = password;
	}

}

public class Users {

	static Vector<UserStruct> users = new Vector<>();
	private static Scanner s;
	private static PrintWriter pw;

	static void initialize() {

		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter("Users.txt", true)), true);
			s = new Scanner(new BufferedReader(new FileReader(new File("Users.txt"))));
			while (s.hasNext()) {
				String u = s.nextLine();
				users.addElement(new UserStruct(u, s.nextLine()));
				ServerGUI.users.append(u + '\n');
			}
			users.elementAt(0).socket = new Socket();
		} catch (IOException e) {
		}

	}

	static void addUser(String a, String b) {

		users.addElement(new UserStruct(a, b));
		pw.println(users.lastElement().username);
		pw.println(users.lastElement().password);

	}

	static int find(String a) {

		for (int i = 0; i < users.size(); i++)
			if (users.elementAt(i).username.equals(a))
				return i;
		return -1;

	}

}
