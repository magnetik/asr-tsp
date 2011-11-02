package asyncnio;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import csc4509.Message;
import csc4509.ReadMessageStatus;

public class Recepteur extends Thread {
	Message mess = new Message(1); 
	SocketChannel sc = null;
	
	public Recepteur (SocketChannel sc) {
		this.sc = sc;
	}
	
	public void run() {
		while (true) {
			while (mess.readMessage(sc) != ReadMessageStatus.ReadDataCompleted);
			String s = null;
			try {
				s = (String) mess.getData();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(s);
		}
	}
}
