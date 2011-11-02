package asyncnio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import csc4509.Message;
import csc4509.ReadMessageStatus;

public class Serveur {
	
	boolean debug = false;

	public static void main(String[] argv) {
		if (argv.length != 1) {
			System.out.println("usage: java MainTcpNioServer <port>");
			return;
		}
		try {
			ServerSocketChannel ssc = ServerSocketChannel.open();
			/* Socket non bloquant */
			ssc.configureBlocking(false);
	
			ServerSocket ss = ssc.socket();
			InetSocketAddress rcvAddress = new InetSocketAddress( Integer.parseInt(argv[0]) );
			ss.bind( rcvAddress );
			
			/* Initalisation du Selector */
			Selector selector = Selector.open();
			ssc.register(selector, SelectionKey.OP_ACCEPT);
			
			HashMap<SelectionKey,Message> hm = new HashMap<SelectionKey,Message>();
			
			/* Socket en écoute */
			while (true) {
				/* Init selector */
				selector.select();
				Set<SelectionKey> readyKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = readyKeys.iterator();
				while (iterator.hasNext()) {
					SelectionKey key = (SelectionKey) iterator.next();
					
			        if (key.isAcceptable()) {
						/* Accepter une nouvelle connexion */
						SocketChannel sc = ssc.accept();
						sc.configureBlocking(false);
						
						/* Creation du thread pour traitement 
						Recepteur t = new Recepteur(sc);
						t.run(); */
						
						/* Nouveau message */
						Message mess = new Message(1);
						
						/* Ajout de cette connexion au selector */
				        SelectionKey newKey = sc.register( selector, SelectionKey.OP_READ);
				        
						/* Sauvegarde du mesasge dans hashmap */
						hm.put(newKey, mess);
				        
				        iterator.remove();
					}
			        else if (key.isReadable()) {
						SocketChannel sc = (SocketChannel)key.channel();
						
						Message mess = hm.get(key);
						
						/* Read */
						ReadMessageStatus rms = mess.readMessage(sc);
						if (rms == ReadMessageStatus.ReadDataCompleted) {
							String s = null;
							try {
								s = (String) mess.getData();
							} catch (IOException e) {
								e.printStackTrace();
							}
							System.out.println(s);
						}
						else if (rms == ReadMessageStatus.ChannelClosed) {
							hm.remove(key);
						}
						
						iterator.remove();
					}
					else if (key.isWritable()) {

					}
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