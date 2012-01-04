import java.util.EventListener;

public interface TemperatureListener extends EventListener {
	
	public void temperatureAlarm( TemperatureEvent evt );
}
