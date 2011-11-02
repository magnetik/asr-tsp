package objectmnio;

/***********************************************************************
 * Ce programme emet a destination du port argv[1] sur la machine
 * argv[0] un message (datagramme UDP) constitue de argv[2] caracteres
 *
 * ex: java DgramSend linux03 2005 1000	
 * 
 * L'emission se fait par send sur un DatagramSocket dans le domaine
 * INET.
 ***********************************************************************/

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

import csc4509.ReadMessageStatus;
import csc4509.SockUtil;
import csc4509.Message;

public class server {
	
	boolean debug = false;

	public static void main(String[] argv) {
		if (argv.length != 1) {
			System.out.println("usage: java MainTcpNioServer <port>");
			return;
		}
		try {
			ServerSocketChannel ssc = ServerSocketChannel.open();
			ServerSocket ss = ssc.socket();
			InetSocketAddress rcvAddress = new InetSocketAddress( Integer.parseInt(argv[0]) );
			ss.bind( rcvAddress );
			Message mess = new Message(1); 
			/* Socket en écoute */
			while (true) {
				SocketChannel sc = ssc.accept();
				while (true) {
					
					while (mess.readMessage(sc) != ReadMessageStatus.ReadDataCompleted);
					String s = (String) mess.getData();
					System.out.println(s);
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
