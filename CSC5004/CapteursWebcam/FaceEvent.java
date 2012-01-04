import java.util.EventObject;


public class FaceEvent extends EventObject {
	private Boolean hasFace;
	private static final long serialVersionUID = -8471046266604188532L;
	
	public FaceEvent(Object source, Boolean hasFace) {
		super(source);
		this.hasFace = hasFace;
	}
	
	public String toString() {
		if (hasFace) {
			return "Face detected";
		} else {
			return "No Face detected";
		}
		
	}
	
	public Boolean getHasFace() {
		return hasFace;
		
	}

}
