package msgnio;


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

import csc4509.SockUtil;

public class MainMsgNioClient {

	public static void main(String[] argv) {	
		if (argv.length != 3) {
			System.out.println("usage: java MainMsgNioClient <ip> <port> <message>");
			return;
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		
		try {
			InetSocketAddress sndAddress = new InetSocketAddress(InetAddress.getByName(argv[0]), Integer.parseInt(argv[1]));
			SocketChannel sc = SocketChannel.open(sndAddress);
			/* Translate argv[2] en buffer */
			buffer.put("test".getBytes());
			buffer.flip();
			sc.write(buffer);
			
		} catch (SocketException se) {
			se.printStackTrace();
			return;
		} catch (IOException se) {
			se.printStackTrace();
			return;
		}
	}
}
