package dgramnio;

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
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import csc4509.SockUtil;

public class MainNioRecv {

	public static void main(String[] argv) {
		DatagramChannel rcvChan;
		DatagramSocket rcvSock;
		InetSocketAddress rcvAddress;
		InetSocketAddress senderAddress;
		ByteBuffer dataBuf;
		
		if (argv.length != 1) {
			System.out.println("usage: java MainRecv <port>");
			return;
		}
		try {
			rcvChan = DatagramChannel.open();
			rcvSock = rcvChan.socket();
			rcvAddress = new InetSocketAddress(Integer.parseInt(argv[0]));
			rcvSock.bind(rcvAddress);
			dataBuf = ByteBuffer.allocate(SockUtil.bufSize);
			// receive data in
			senderAddress = (InetSocketAddress) rcvChan.receive(dataBuf);
			SockUtil.printDebug(senderAddress);
			SockUtil.printMess(dataBuf);
		} catch (SocketException se) {
			se.printStackTrace();
			return;
		} catch (IOException se) {
			se.printStackTrace();
			return;
		}
	}
}
