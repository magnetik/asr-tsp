package chat;

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
		if (argv.length != 2) {
			System.out.println("usage: java chatClientMain <machineserveur> <port serveur>");
			return;
		}
		InetSocketAddress Address;
		Message mess = new Message(1); 
		try {
			Address = new InetSocketAddress(InetAddress.getByName(argv[0]), Integer.parseInt(argv[1]));
			SocketChannel sc = SocketChannel.open(Address);
			
			/* Thread lecture */
			lecture l = new lecture(sc);
			l.start();
			
			BufferedReader entree = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Entrez un message:");
			do {
				String tosend = entree.readLine();
				/* Envoie du message */
				mess.setMessage(1, tosend);
				mess.write(sc);
				System.out.println("Message envoyé :" + tosend);
				
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
