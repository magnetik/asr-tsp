package objectmnio;

/*************************************************************************
 * Cette programme recoit un message (datagramme UDP) sur un DatagramSocket
 * associe au port argv[0].
 * 
 * ex: java DgramRecv 2005
 *
 * Par receive, il attend un message (emis par send) et imprime son
 * contenu sur la sortie standard.
 * 
 * receive recupere UN message sur le socket, celui qui est en tete
 * de la queue. Des receive ulterieurs permettraient de recuperer les
 * messages suivants eventuels.
 ************************************************************************/

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import csc4509.Message;
import csc4509.SockUtil;

public class client {

	public static void main(String[] argv) {	
		if (argv.length != 3) {
			System.out.println("usage: java MainRecv <ip> <port> <message>");
			return;
		}
		Message mess = new Message(1);
		try {
			InetSocketAddress sndAddress = new InetSocketAddress(InetAddress.getByName(argv[0]), Integer.parseInt(argv[1]));
			SocketChannel sc = SocketChannel.open(sndAddress);
			
			mess.setMessage(1,"Test");
			mess.write(sc);
		
		} catch (SocketException se) {
			se.printStackTrace();
			return;
		} catch (IOException se) {
			se.printStackTrace();
			return;
		}
	}
}
