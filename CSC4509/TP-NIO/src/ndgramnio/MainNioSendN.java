package ndgramnio;
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
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import csc4509.SockUtil;

public class MainNioSendN {

	public static void main(String[] argv) {
		DatagramChannel sndChan;
		ByteBuffer dataBuf;
		InetAddress destAddr;
		InetSocketAddress rcvAddress;
		int N;
		int sent = 0; 
		if (argv.length != 4) {
			System.out.println("usage: java DgramSend <machine> <port> <size> <numberOfMessage>");
			return;
		}
		dataBuf = ByteBuffer.allocate(Integer.parseInt(argv[2]));
		N = Integer.parseInt(argv[3]);
		try {

			sndChan = DatagramChannel.open();
			// on recupere l'adresse IP de la machine cible
			destAddr = InetAddress.getByName(argv[0]);
			rcvAddress = new InetSocketAddress(destAddr, Integer.parseInt(argv[1]));

			// on remplit le buffer de donnees
			SockUtil.fillbuf(dataBuf, 1);
			// envoi du datagramme
			// prepare the buffer for write
			dataBuf.flip();
			for (int i=0; i < N; i++) {
				sent = sndChan.send(dataBuf, rcvAddress);
				System.out.println("Dgramsend: sent " + sent +" bytes.");
				dataBuf.rewind();
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
