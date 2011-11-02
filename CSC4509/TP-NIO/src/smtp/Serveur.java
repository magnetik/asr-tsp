package smtp;

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
	public static void main(String[] argv) {
		ReadMessageStatus rms = null;
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
			
			HashMap<SelectionKey,SmtpSession> hm = new HashMap<SelectionKey,SmtpSession>();
			
			/* Socket en écoute */
			while (true) {
				/* Init selector */
				selector.select();
				Set<SelectionKey> readyKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = readyKeys.iterator();
				while (iterator.hasNext()) {
					SelectionKey key = (SelectionKey) iterator.next();
					
			        if (key.isValid() && key.isAcceptable()) {
						/* Accepter une nouvelle connexion */
						SocketChannel sc = ssc.accept();
						sc.configureBlocking(false);
						
						
						/* Nouvelle session SMTP */
						SmtpSession sess = new SmtpSession(sc);
						
						/* Ajout de cette connexion au selector */
				        SelectionKey newKey = sc.register( selector, SelectionKey.OP_READ);
				        
						/* Sauvegarde du mesasge dans hashmap */
						hm.put(newKey, sess);
				        
				        iterator.remove();
				        
				        System.out.println("Nouveau client:");
					}
			        else if (key.isValid() && key.isReadable()) {
						SocketChannel sc = (SocketChannel)key.channel();
						
						SmtpSession sess = hm.get(key);
						
						
						
						iterator.remove();
					}
					else if (!key.isValid()) {
						hm.remove(key);
						iterator.remove();
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

