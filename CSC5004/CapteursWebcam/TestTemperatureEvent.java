

public class TestTemperatureEvent implements TemperatureListener{

	public TestTemperatureEvent() {
		
	}

	public void temperatureAlarm( TemperatureEvent evt ) {
		System.out.println("Temp:" + evt.toString());
		new FreeTTSHelloWorld( "Alarm : Temperature is " + evt.toString() + "degree" ) ;
	}

}
