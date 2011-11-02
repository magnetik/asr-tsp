package msgnio;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import csc4509.SockUtil;

public class MainMsgNioServer {

	public static void main(String[] argv) {
		ByteBuffer buffer = ByteBuffer.allocate(1024);

		if (argv.length != 1) {
			System.out.println("usage: java MainMsgNioServer <port>");
			return;
		}
		try {
			ServerSocketChannel ssc = ServerSocketChannel.open();
			ServerSocket ss = ssc.socket();
			InetSocketAddress rcvAddress = new InetSocketAddress( Integer.parseInt(argv[0]) );
			ss.bind( rcvAddress );
			/* Socket en écoute */
			while (true) {
				SocketChannel sc = ssc.accept();
				while (true) {
					int bitsLus = sc.read(buffer);
					buffer.flip(); /* Position 0 ; limit placé à la fin du buffer -> On est prêt à lire */
					if(bitsLus>0) {
						System.out.println("Recu : " + new String(buffer.array(), 0, bitsLus));
					}
					buffer.rewind();
				}
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
