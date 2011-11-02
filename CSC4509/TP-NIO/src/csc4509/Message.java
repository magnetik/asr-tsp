package csc4509;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Message {
	private ByteBuffer bufferHeader = ByteBuffer.allocate(8);
	private ByteBuffer bufferPayload = null;
	
	private int messType = 1;
	private int messSize = 0;
	byte[] message;
	
	private SocketChannel sc;
	
	private ReadMessageStatus readState = ReadMessageStatus.ReadUnstarted;
	
	public Message (int type) {
		this.messType = type;
		
	}
	public Message (int type, int size) {
		this.messType = type;
		this.messSize = size;
	}
	
	public void write (SocketChannel sc) {
		try {
			ByteBuffer bufferPayload = ByteBuffer.allocate(messSize);
			
			/* Rewind buffer */
			bufferHeader.rewind();
			bufferPayload.rewind();
			
			/* Remplissage des buffers */
			bufferHeader.putInt(messType);
			bufferHeader.putInt(messSize);
			bufferPayload.put(message);
			
			/* Flip buffers */
			bufferHeader.flip();
			bufferPayload.flip();
			
			/* Envoie */
			sc.write(bufferHeader);
			sc.write(bufferPayload);
			
		/* Exceptions */	
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
	
	public void readFullMessage() {
		while (this.readMessage(this.getSc()) != ReadMessageStatus.ReadDataCompleted);
	}
	
	public ReadMessageStatus readMessage (SocketChannel sc) {
		try {
			switch (readState) {
				case ReadUnstarted:
					bufferHeader.rewind();
					int res = sc.read(bufferHeader);
					if (res == 0) {
						return ReadMessageStatus.ReadUnstarted;
					}
					else if (res == -1) {
						return ReadMessageStatus.ChannelClosed;
					}
					readState = ReadMessageStatus.ReadHeaderStarted;
					return readState;
					
				case ReadHeaderStarted:
					/* Read from socket channel */
					if (sc.read(bufferHeader) == -1) {
						return ReadMessageStatus.ChannelClosed;
					}
					if (bufferHeader.position() == bufferHeader.capacity()) {
						readState = ReadMessageStatus.ReadHeaderCompleted;
						return readState;
					}
					else {
						return ReadMessageStatus.ReadHeaderStarted;
					}
					
				case ReadHeaderCompleted:
					bufferHeader.flip();
					messType = bufferHeader.getInt();
					messSize = bufferHeader.getInt();
					
					bufferPayload = ByteBuffer.allocate(messSize);
					
					if (sc.read(bufferPayload) == -1) {
						return ReadMessageStatus.ChannelClosed;
					}
					if (buffEnded(bufferPayload)) { /* Read is finished */
						readState = ReadMessageStatus.ReadDataCompleted;

					}
					else { /* There is still something to read */
						readState = ReadMessageStatus.ReadDataStarted;
					}
					return readState;
					
				case ReadDataStarted:
					if (sc.read(bufferPayload) == -1) { 
						return ReadMessageStatus.ChannelClosed;
					}
					if (buffEnded(bufferPayload)) { /* Nothing to read */
						readState = ReadMessageStatus.ReadDataCompleted;
						return readState;
					}
					else {
						return ReadMessageStatus.ReadDataStarted;
					}
					
				case ReadDataCompleted:
					readState = ReadMessageStatus.ReadUnstarted;
					return null;
					
					
				case ChannelClosed:
					readState = ReadMessageStatus.ReadUnstarted;
					return null;
					
				default:
					return null;
				}
			
		/* Exceptions */	
		} catch (SocketException se) {
			//se.printStackTrace();
			return ReadMessageStatus.ChannelClosed;
		} catch (UnknownHostException ue) {
			//ue.printStackTrace();
			return ReadMessageStatus.ChannelClosed;
		} catch (IOException ie) {
			//ie.printStackTrace();
			return ReadMessageStatus.ChannelClosed;
		}
	}
	private boolean buffEnded(ByteBuffer bf) {
		if (bufferPayload.position() == bufferPayload.capacity()) {
			bufferPayload.flip();
			
			/* Read values from buffer */
			message = new byte[messSize];
			bufferPayload.get(message, 0, messSize);
			
			/* Buffer reading is ended */
			return true;
		}
		else {
			/* There is still data to read */
			return false;
		}
		
	}
	public void printMessage() {
		System.out.println(new String(message));
	}
	
	public String toString() {
		return "Type:" + messType + "; Taille :" + messSize + "; Recu : " + new String(message);
	}
	
	public void printDebug(String msg) {
		System.out.println(msg + "Type:" + messType + "; Taille :" + messSize + "; Recu : " + new String(message));
	}

	public void setType(int type) {
		this.messType = type;
	}

	public int getType() {
		return messType;
	}

	public int getContentSize() {
		return messSize;
	}
	
	public byte[] getMessage() {
		return message;
	}
	
	/* public int setMessage(byte[] pattern) {
		this.messSize = pattern.length;
		this.message = new byte[pattern.length];
		this.message = pattern;
		return 1;
	} */
	public int setMessage(int type, Serializable data) throws IOException {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(bo);
		oo.writeObject(data);
		oo.close();
		bo.close();
		
		this.messSize = bo.size();
		this.message = new byte[bo.size()];
		this.message = bo.toByteArray();
		
		return 1;
	}
	
	public Object getData() throws IOException {
		Object res = null;
		ByteArrayInputStream bi = new ByteArrayInputStream(message);

		ObjectInputStream oi = new ObjectInputStream(bi);
		try {
			res = oi.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		oi.close();
		bi.close();
		
		return res;
		
	}
	public String getString() {
		String s = null;
		try {
			s = (String) getData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}
	public void setSc(SocketChannel sc) {
		this.sc = sc;
	}
	public SocketChannel getSc() {
		return sc;
	}
	
	public SocketAddress getHost() {
		return sc.socket().getRemoteSocketAddress();
	}
	public int getPort() {
		return sc.socket().getPort();
	}
	public int getLocalPort() {
		return sc.socket().getLocalPort();
	}
}
