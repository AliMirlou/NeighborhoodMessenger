package server;

public class Lobby implements Runnable {

	String text;
	int id;

	public Lobby(String t, int i) {

		text = t;
		id = i;

	}

	public void run() {

		for (int i = 0; i < Server.numOfClient; i++) {
			if ((i != id) && (Server.clientThread.elementAt(i).indexSecond == 0)) {
				Server.clientThread.elementAt(i).out.println(text);
			}
		}

	}

}
