package smtp;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.StringTokenizer;

import csc4509.Message;
import csc4509.ReadMessageStatus;

public class SmtpSession {
	Message messageSnd;
	Message messageRcv;

	SmtpState sendState;
	SmtpState rcpState;
	
	String from; /* Save from */
	String host;
	String to;
	String text;
	
	public SmtpSession(SocketChannel sc) {
		/* Init Smtp Session */
		messageSnd = new Message(1);
		messageSnd.setSc(sc);
		messageRcv = new Message(1);
		messageRcv.setSc(sc);
		sendState = SmtpState.Unstarted;
		rcpState = SmtpState.Unstarted;
	}
	
	private void sendMessage(int type, String message) {
		try {
			messageSnd.setMessage(type, message);
			messageSnd.write(messageSnd.getSc());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public SmtpState fromMessageToState(Message m) {
		String code = null;
		String suite = null;
		StringTokenizer st = null;
	
		/* A message is readed */
		st = new StringTokenizer(m.getString()," ");
		/* Read the first token : the code */
		
		code = st.nextToken();
		if (m.getType() == 250) {
			suite = st.nextToken();
			if (suite.equalsIgnoreCase("Hello")) {
				this.host = code;
				
				this.rcpState = SmtpState.HelloReceived;
				return this.rcpState;
			}
			else if(code.equalsIgnoreCase("MAIL")) {
				this.from = suite.replace(" ", "");
				
				this.rcpState = SmtpState.MailFromReceived;
				return this.rcpState;
			}
			else if(code.equalsIgnoreCase("RCPT")) {
				this.to = st.nextToken().replace(" ", "");
				
				this.rcpState = SmtpState.RcptReceived;
				return this.rcpState;
			}
		}
		/* To continue */
		else if (code.equalsIgnoreCase(".")) {
			this.rcpState = SmtpState.DataEnded;
		}
		else {
			return this.rcpState;
		}
	}
	
	public SmtpState waitSendAck() {
		SmtpState state = null;
		/* Get the response */
		messageRcv.readFullMessage();
		/* We  have a full response */
		/* let's read the corresponding state */
		state = fromMessageToState(messageRcv);
		
		return state;
	}
	public boolean sendMail() {
		SmtpState state = null;
		
		do {
			sendStep();
			state = waitSendAck();
		} while (state != SmtpState.DataEnded);
		
		
		
		return true;
	}
	
	public SmtpState sendStep() {
		switch (sendState) {
		case Unstarted:
			sendMessage(0,"Helo");
			System.out.println("HELO sended!");
			
			sendState = SmtpState.HelloSended;
			return sendState;
			
		case HelloReceived:
			sendMessage(0,"MAIL FROM: " + from);
			System.out.println("MAIL FROM sended!");
			
			sendState = SmtpState.MailFromSended;
			return sendState;
			
		case MailFromReceived:
			sendMessage(0,"RCP TO:" + to);
			System.out.println("RCP TO sended !");
			
			sendState = SmtpState.RcptSended;
			return sendState;
			
		case RcptReceived:
			sendMessage(0,"DATA");
			System.out.println("DATA sended !");
			
			sendState = SmtpState.DataSended;
			return sendState;
			
		case DataReceived:
			sendMessage(0,this.text);
			System.out.println("");
			
			sendState = SmtpState.DataFinished;
			return sendState;
			
		case DataEnded:
			sendMessage(0,"QUIT");
			System.out.println("QUIT sended !");
			
			sendState = SmtpState.QuitSended;
			return sendState;
		}
	}
	
	public SmtpState receive() {
		String code = null;
		StringTokenizer st = null;
		messageRcv.readFullMessage();
		/* A message is readed */
		st = new StringTokenizer(messageRcv.getString()," ");
		/* Read the first token : the code */
		code = st.nextToken();
		if (code.equalsIgnoreCase("helo")) {
			/* Helo message received */
			this.host = st.nextToken();
			/* Send good answer */
			sendMessage(250,this.host + " Hello localhost[127.0.0.1]");
			
			this.rcpState = SmtpState.MailFromReceived;
			return this.rcpState;
		}
		else if (code.equalsIgnoreCase("mail from")) {
			/* Mail from received */
			st = new StringTokenizer(messageRcv.getString(),":");
			this.from = st.nextToken().replace(" ", "");
			
			/* Envoi de la réponse adaptée */
			sendMessage(250,"OK");
			
			this.rcpState = SmtpState.MailFromReceived;
			return this.rcpState;
		}
		else if (code.equalsIgnoreCase("rcpt to")) {
			/* RCPT TO received */
			st = new StringTokenizer(messageRcv.getString(),":");
			this.to = st.nextToken().replace(" ", "");
			
			/* Envoie de la réponse adaptée */
			sendMessage(250,"OK");
			
			this.rcpState = SmtpState.RcptReceived;
			return this.rcpState;
		}
	}
	public SmtpState getSendState() {
		return sendState;
	}
	public SmtpState getRcpState() {
		return rcpState;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
