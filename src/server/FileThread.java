package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class FileThread extends Thread {

	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	public FileThread(Socket s) {

		socket = s;

	}

	public void run() {

		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			int i = findFile(in.readLine());
			if (i != -1) {
				out.println("$result_fileFound");
				if (in.readLine().equals("$sendFile")) {
					new Thread(new Runnable() {
						public void run() {
							try {
								if (in.readLine().equals("$sendFile_cancel"))
									socket.close();
							} catch (IOException e) {
								return;
							}
						}
					}).start();
					sendFile(i);
					return;
				} else {
					out.close();
					in.close();
					socket.close();
				}
			} else
				out.println("$result_fileNotFound");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void sendFile(int i) {

		try {

			File file = FileServer.files.elementAt(i).file;
			OutputStream send = socket.getOutputStream();
			FileInputStream fileReader = new FileInputStream(file);
			long size = file.length();
			out.println(String.valueOf(size));
			byte[] buffer = new byte[16 * 1024];

			int count;
			while ((count = fileReader.read(buffer)) > 0) {
				send.write(buffer, 0, count);
			}

			send.close();
			fileReader.close();
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void sendList() {

		String[] nameList = new String[FileServer.files.size()];
		for (int i = 0; i < FileServer.files.size(); i++)
			nameList[i] = FileServer.files.elementAt(i).name;

		try {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(nameList);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public int findFile(String a) {
		for (int i = 0; i < FileServer.files.size(); i++)
			if (FileServer.files.elementAt(i).name.equals(a))
				return i;
		return -1;
	}

}
