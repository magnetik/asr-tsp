package directory;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class UserRight implements Serializable {
	private static final long serialVersionUID = 1676040353049464760L;

	@Id
	@Column(name="IdUserRight")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int id;
	
	public String boxName;
	
	@Enumerated(EnumType.ORDINAL)
	@Column(name="TheRight")
	public UserRightEnum right;
	
	@ManyToOne
	public User user;

	public UserRight() {};

	public UserRight(User user, String boxName, UserRightEnum renum) {
		this.user = user;
		this.boxName = boxName;
		this.right = renum;
	}

	public UserRightEnum getRight() {
		return right;
	}

	public void setRight(UserRightEnum right) {
		this.right = right;
	}

	public String getBoxName() {
		return boxName;
	}
	
	public void setBoxName(String boxName) {
		this.boxName= boxName;
	}
	
}
