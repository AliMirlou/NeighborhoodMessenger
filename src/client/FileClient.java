package client;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JProgressBar;

public class FileClient {

	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	public void connect() {

		try {
			socket = new Socket("0.0.0.0", 9998);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void disconnect() {

		try {
			out.println("$disconnect");
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean search(String a) {

		try {
			out.println(a);
			if (in.readLine().equals("$result_fileFound"))
				return true;
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	public void receiveFile(String text, JProgressBar p) {

		try {
			out.println("$sendFile");

			double size = Double.parseDouble(in.readLine());
			double bufferSize = 16 * 1024;
			double step = bufferSize * 100.0 / size;
			int isBig = (int) (1 / step);

			FileOutputStream fileWriter = new FileOutputStream("Downloads/" + text);
			InputStream receive = socket.getInputStream();
			byte[] buffer = new byte[(int) bufferSize];

			int count;
			while ((count = receive.read(buffer)) > 0) {
				fileWriter.write(buffer, 0, count);
				if (step < 1) {
					if (isBig != 0) {
						--isBig;
						continue;
					} else
						isBig = (int) (1 / step);
					p.setValue(p.getValue() + 1);
					continue;
				}
				p.setValue(p.getValue() + (int) step);
			}

			p.setValue(100);

			fileWriter.close();
			receive.close();
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String[] listFiles() {

		try {
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			return (String[]) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			return null;
		}

	}

}
