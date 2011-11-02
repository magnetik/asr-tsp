package asyncnio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

import csc4509.Message;

public class Client {

	public static void main(String[] argv) {	
		if (argv.length != 4) {
			System.out.println("usage: java client <machine serveur> <port serveur> <periode> <chaine de caractere>");
			return;
		}
		Message mess = new Message(1);
		try {
			InetSocketAddress sndAddress = new InetSocketAddress(InetAddress.getByName(argv[0]), Integer.parseInt(argv[1]));
			SocketChannel sc = SocketChannel.open(sndAddress);
			
			do {
				mess.setMessage(1,argv[3]);
				mess.write(sc);
				
				try {
					Thread.currentThread();
					Thread.sleep(Integer.parseInt(argv[2])*1000);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (true);
		
		} catch (SocketException se) {
			se.printStackTrace();
			return;
		} catch (IOException se) {
			se.printStackTrace();
			return;
		}
	}
}
