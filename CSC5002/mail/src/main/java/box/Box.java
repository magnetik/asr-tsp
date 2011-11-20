package box;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import box.message.Message;

/**
 * Represent a mail box
 * @author Julie Garrone
 * @author Baptiste Lafontaine
 *
 */

@Entity
public class Box implements Serializable {
	private static final long serialVersionUID = 4989225737648878907L;
	
	@Id
	public String name;
	
	@Enumerated(EnumType.STRING)
	public BoxType type;
	
	@OneToMany(mappedBy="receiverName")
	public Collection<Message> msgList;
	
	public Box() {
		msgList = new ArrayList<Message>();
	}; // Required by jaxb
	
	public Box (String name, BoxType type) {
		this();
		this.name = name;
		this.type = type;
	}

	public Collection<Message> getMsgList() {
		return msgList;
	}

	public String getName() {
		return name;
	}
	public BoxType getType() {
		return type;
	}
}