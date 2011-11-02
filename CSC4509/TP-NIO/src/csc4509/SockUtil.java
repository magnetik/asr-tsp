package csc4509;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;

public class SockUtil {
	public static final int bufSize = 6500000;
	public static final boolean debugState = true;

	public static int fillbuf(ByteBuffer dataBuf, int num) {
		// creates the pattern byte that will be used to fill the buffer
		byte pattern[] = ((Integer.valueOf(num)).toString()).getBytes();
		int i, size = dataBuf.limit();
		// fill the buffer with the pattern and avoid buffer overflow
		for (i = 0; (i + pattern.length) < size; i += pattern.length) {
			for (int j = 0; j < pattern.length; j++) {
				dataBuf.put(pattern[j]) ;
			}
		}
		// finish to fill the buffer with pattern starting byte
		for (; i < size; i++)
			dataBuf.put(pattern[0]);
		return i;
	}

	public static int fillbuf(ByteBuffer dataBuf, String pat) {
		if (dataBuf == null) {
			return 0;
		}
		// fill the buffer
		byte pattern[] = pat.getBytes();
		int i, size = dataBuf.limit() -dataBuf.position();
		for (i = 0; i < size - pattern.length; i += pattern.length) {
			for (int j = 0; j < pattern.length; j++) {
				dataBuf.put(pattern[j]);
			}
		}
		for (; i < size; i++)
			dataBuf.put(pattern[0]);
		return i;
	}

	public static int printMess(ByteBuffer dataBuf) {
		int count;
		dataBuf.flip();
		count =  dataBuf.limit();
		if (debugState) {
			System.out.println("	Data size       : " + count);
		}
		WritableByteChannel outChan = Channels.newChannel(System.out);
		if (count > 0) {
			if (count > 10) {
				// only write the head of the data
				dataBuf.limit(10);
			}
			System.out.println("	Message content :");
			// restreindre la sortie 
			try {
				outChan.write(dataBuf);
				if (count > 10) {
					System.out.println("...");
				} else {
					System.out.println();
				}
			} catch (IOException se) {
				se.printStackTrace();
				return 0;
			}
		}

		return count;
	}
	public static long printMess(ByteBuffer[] dataBuf) {
		long count=0L;
		for(int i=0; i< dataBuf.length;i++){
			dataBuf[i].flip();
			count =  dataBuf[i].limit();
			if (debugState) {
				System.out.println("	Data size       : " + count);
			}
			WritableByteChannel outChan = Channels.newChannel(System.out);
			if (count > 0) {
				if (count > 10) {
					// only write the head of the data
					dataBuf[i].limit(10);
				}
				System.out.println("	Message content :");
				// restreindre la sortie 
				try {
					outChan.write(dataBuf[i]);
					if (count > 10) {
						System.out.println("...");
					} else {
						System.out.println();
					}
				} catch (IOException se) {
					se.printStackTrace();
					return 0;
				}
			}
		}

		return count;
	}

	public static void printDebug(DatagramPacket dgramPack) {
		if (debugState) {
			System.out.println("Datagram received from");
			System.out.println("	Ip address  : " + dgramPack.getAddress());
			System.out.println("	Port number : " + dgramPack.getPort());
			System.out.println("	Data size   : " + dgramPack.getLength());
		}
	}

	public static void printDebug(InetSocketAddress senderAddress) {
		if (debugState) {
			System.out.println("Datagram received from");
			System.out.println("	Ip address  : "
					+ senderAddress.getAddress());
			System.out.println("	Port number : " + senderAddress.getPort());
		}
	}

	public static void printDebug(Socket sockCon) {
		if (debugState) {
			System.out.println("Data received from");
			System.out.println("	Ip address  : " + sockCon.getInetAddress());
			System.out.println("	Port number : " + sockCon.getPort());
		}
	}
}
