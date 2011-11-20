package box.message;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import box.Box;

/**
 * Represents a message
 * Can be serialized in XML via JAXB
 * @author Baptiste Lafontaine
 * @author Julie Garrone
 *
 */
@XmlRootElement
@Entity
public class Message implements Serializable {
	private static final long serialVersionUID = 4035485820029813303L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int id;
	
	public String senderName;
	
	@ManyToOne
	public Box receiverName;
	
	@Temporal(TemporalType.DATE)
	public Date sendingDate;
	public String subject;
	public String body;
	
	@Enumerated(EnumType.STRING)
	public MessageStatus status;
	
	public Message() {} ; // Needed by jaxb
	
	public Message(String senderName, Box receiverName, String subject, String body) {
		this.senderName = senderName;
		this.receiverName = receiverName;
		this.sendingDate = new Date();
		this.subject = subject;
		this.body = body;
		this.status = MessageStatus.New;
	}
	
	public void setRead() {
		this.status = MessageStatus.Read;
	}
	
	public MessageStatus getStatus() {
		return this.status;
	}
	
}