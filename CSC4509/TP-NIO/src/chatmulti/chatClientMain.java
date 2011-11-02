package chatmulti;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

import csc4509.Message;

public class chatClientMain {
	public static void main(String[] argv) {	
		String host = null;
		int port;
		if (argv.length == 0) {
			System.out.println("usage: java chatClientMain [machineserveur] <port serveur>");
			return;
		}
		else if (argv.length == 1) {
			host = "localhost";
			port = Integer.parseInt(argv[0]);
		}
		else {
			host = argv[0];
			port = Integer.parseInt(argv[1]);
		}
		InetSocketAddress Address;
		Message mess = new Message(1); 
		try {
			Address = new InetSocketAddress(InetAddress.getByName(host), port);
			SocketChannel sc = SocketChannel.open(Address);
			
			/* Thread lecture */
			lecture l = new lecture(sc);
			l.start();
			
			BufferedReader entree = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Entrez un message:");
			do {
				String tosend = entree.readLine();
				/* Envoie du message */
				if (tosend != null) {
					mess.setMessage(1, tosend);
					mess.write(sc);
					System.out.println("Message envoyé:" + tosend);
				}
				
			} while (true);

		
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
