package chat;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

import csc4509.Message;
import csc4509.ReadMessageStatus;

public class lecture extends Thread {
	SocketChannel sc;
	
	public lecture (SocketChannel sc) {
		this.sc = sc;
	}
	
	public void run() {
		Message mess = new Message(1);
		try {
			while (true) {
				while (mess.readMessage(sc) != ReadMessageStatus.ReadDataCompleted);
				String s = (String) mess.getData();
				System.out.println("Message reçu: " + s);
			}
		} catch (SocketException se) {
			se.printStackTrace();
			return;
		} catch (UnknownHostException ue) {
			ue.printStackTrace();
			return;
		} catch (IOException ie) {
			ie.printStackTrace();
			return;
		}
	}
}
