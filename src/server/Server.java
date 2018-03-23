package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {

	ServerSocket publicServer;
	ServerSocket onlinesServer;
	static Vector<ClientThread> clientThread = new Vector<>();
	static int numOfClient = 0;

	public Server() throws IOException {

		publicServer = new ServerSocket(10000);
		onlinesServer = new ServerSocket(9999);

		Users.initialize();

		Thread clientCatch = new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Socket tempSocket = publicServer.accept();
						Socket onlinesSocket = onlinesServer.accept();
						System.out.println("A new client has been connected. (" + numOfClient + ")");
						clientThread.addElement(new ClientThread(tempSocket, onlinesSocket, numOfClient));
						clientThread.lastElement().start();
						numOfClient++;
					} catch (IOException e) {
						return;
					}
				}
			}
		});
		clientCatch.start();

	}

}
