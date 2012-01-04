import java.util.EventObject;


public class TemperatureEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3480008984876990915L;
	float temperature;
	
	public TemperatureEvent(Object source, float temp) {
		super(source);
		temperature = temp;
	}
	
	public String toString() {
		return Float.toString(temperature);
	}

}
