package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	Socket socket;

	static PrintWriter out;
	BufferedReader in;

	String me = null;
	String you;

	public Client() throws UnknownHostException, IOException {
		socket = new Socket("0.0.0.0", 10000);
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public int login(String username, char[] password) throws IOException {

		out.println("$loginTry_" + username);
		String response = in.readLine();
		switch (response) {
		case "$login_error_userAlreadyOn":
			return 3;
		case "$login_error_userNotFound":
			return 1;
		case "$login_error_userBanned":
			return 4;
		case "$login_userFound":
			byte[] passByte = new byte[password.length];
			for (int i = 0; i < password.length; ++i)
				passByte[i] = (byte) password[i];
			DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
			dOut.writeInt(passByte.length);
			dOut.write(passByte);
			response = in.readLine();
			switch (response) {
			case "$login_success":
				me = username;
				return 0;
			case "$login_error_password":
				return 2;
			}
		}
		return 5;
	}

	public int register(String text, String pass) throws IOException {
		out.println("$registerTry_" + text);
		String response = in.readLine();
		switch (response) {
		case "$registerResult_error_usernameExists":
			return 1;
		case "$registerResult_usernameOK":
			out.println("$continue");
			out.println(pass);
			response = in.readLine();
			if (response.equals("$registerResult_success"))
				return 0;
		}
		return 2;
	}

	public int handle(String temp) {
		String[] splitted = temp.split("_");
		switch (splitted[0].substring(1, splitted[0].length())) {
		case "logout":
			me = null;
			return 0;
		case "changeUserResult":
			if (splitted[1].equals("successful"))
				you = splitted[2];
			return 1;
		case "history":
			if (splitted[1].equals("start"))
				return 2;
		case "connect":
			return 3;
		}
		return 4;
	}

}
