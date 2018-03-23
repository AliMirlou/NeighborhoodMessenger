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

	public int login(String text, char[] password) {

		out.println("$loginTry_" + text);
		try {
			String response = in.readLine();
			if (response.equals("$login_error_userAlreadyOn")) {
				return 3;
			} else if (response.equals("$login_error_userNotFound")) {
				return 1;
			} else if (response.equals("$login_error_userBanned")) {
				return 4;
			} else if (response.equals("$login_userFound")) {
				byte[] passByte = new byte[password.length];
				for (int i = 0; i < password.length; i++)
					passByte[i] = (byte) password[i];
				DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
				dOut.writeInt(passByte.length);
				dOut.write(passByte);
				response = in.readLine();
				if (response.equals("$login_success")) {
					me = text;
					return 0;
				} else if (response.equals("$login_error_password"))
					return 2;
			}
		} catch (IOException e) {
		}
		return 5;
	}

	public int register(String text, String pass) {
		out.println("$registerTry_" + text);
		try {
			String response = in.readLine();
			if (response.equals("$registerResult_error_usernameExists"))
				return 1;
			else if (response.equals("$registerResult_usernameOK")) {
				out.println("$continue");
				out.println(pass);
				response = in.readLine();
				if (response.equals("$registerResult_success"))
					return 0;
			}
		} catch (IOException e) {
			e.printStackTrace();
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
