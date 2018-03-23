package client;

import java.io.Serializable;

public class Message implements Serializable {

	private static final long serialVersionUID = 3L;

	public String type, sender, content, recipient;

	public Message(String type, String sender, String content, String recipient) {
		this.type = type;
		this.sender = sender;
		this.content = content;
		this.recipient = recipient;
	}

	public String toString() {
		return type + "|" + sender + "|" + content + "|" + recipient;
	}
}
