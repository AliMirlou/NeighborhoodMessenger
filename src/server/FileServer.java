package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

class fileStruct {

	File file;
	String name;

}

public class FileServer extends Thread {

	ServerSocket fileServer;

	static Vector<fileStruct> files = new Vector<>();

	public void run() {

		File[] list = (new File("storage")).listFiles();

		for (int i = 0; i < list.length; i++) {
			files.addElement(new fileStruct());
			files.elementAt(i).file = list[i];
			files.elementAt(i).name = list[i].getName();
		}

		try {
			fileServer = new ServerSocket(9998);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while (true) {
			try {
				Socket fileSocket = fileServer.accept();
				new FileThread(fileSocket).start();
			} catch (IOException e) {
				return;
			}
		}

	}

}
