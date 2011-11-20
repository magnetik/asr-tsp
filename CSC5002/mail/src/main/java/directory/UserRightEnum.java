package directory;

/**
 * Represent the permission of a user to a box
 * <br>0 : None
 * <br>1 : Read
 * <br>2 : Write
 * 
 * @author Baptiste Lafontaine
 * @author Julie Garrone
 * 
 * @see User
 *
 */
public enum UserRightEnum {
	None(0),
	Read(1),
	Write(2);
	
	/** L'attribut qui contient la valeur associ� � l'enum */
	private final int value;
 
	/** Le constructeur qui associe une valeur � l'enum */
	private UserRightEnum(int value) {
		this.value = value;
	}
 
	/** La m�thode accesseur qui renvoit la valeur de l'enum */
	public int getValue() {
		return this.value;
	}
	
	public static UserRightEnum fromStringUserRightToUserRight(String right) {
		if (right.equals("Read")) {
			return UserRightEnum.Read;
		}
		else if (right.equals("Write")) {
			return UserRightEnum.Write;
		}
		else if (right.equals("None")) {
			return UserRightEnum.None;
		}
		else return null;
	}
}