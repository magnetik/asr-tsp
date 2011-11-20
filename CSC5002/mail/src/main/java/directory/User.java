package directory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Represent a user
 * @author Julie Garrone
 * @author Baptiste Lafontaine
 *
 */
@Entity
@Table(name="USERTABLE")
public class User implements Serializable {
	private static final long serialVersionUID = -5842550738167269921L;
	
	@Id
	public String username;
	
	@OneToMany(mappedBy="user")
	public Collection<UserRight> listRight;

	public User() {
		listRight = new ArrayList<UserRight>();
	}; // Required by jaxb
	
	public User(String username) {
		this();
		this.username = username;
	}
	
	/**
	 * Return all rights of the user
	 * @return Map of rights
	 */
	public Collection<UserRight> getListOfRights() {
		return listRight;
	}
	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}