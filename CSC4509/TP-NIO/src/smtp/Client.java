package smtp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

import chat.lecture;
import csc4509.Message;

public class Client {
	public static void main(String[] argv) {	
		if (argv.length != 2) {
			System.out.println("usage: java Client <IP SmtpServer> <Port>");
			return;
		}
		InetSocketAddress Address;
		try {
			Address = new InetSocketAddress(InetAddress.getByName(argv[0]), Integer.parseInt(argv[1]));
			SocketChannel sc = SocketChannel.open(Address);
			
			SmtpSession sess = new SmtpSession(sc);
			
			BufferedReader entree = new BufferedReader(new InputStreamReader(System.in));
			
			do {
				System.out.println("Entrez un destinataire:");
				String to = entree.readLine();
				sess.setTo(to);
				System.out.println("Entrez votre adresse:");
				String from = entree.readLine();
				sess.setFrom(from);
				System.out.println("Entrez votre message terminé par un .:");
				String text = null;
				String msg = null;
				do {
					text = entree.readLine();
					if (text.equals(".")) {
						break;
					}
					else {
						msg.concat("\n" + text);
					}
				} while (true);
				sess.setText(msg);
				/* All fields are ok, let's send it ! */
				sess.sendMail();
				System.out.println("Message envoyé :");
				
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

