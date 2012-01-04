
public class TestFaceEvent implements FaceListener{
	private TempHumSensor thSensor;
	
	public TestFaceEvent (TempHumSensor thSensor) {
		this.thSensor = thSensor;
	}
	
	public void FaceAlarm(FaceEvent evt) {
		System.out.println(evt);
		
		if (evt.getHasFace()) {
			new FreeTTSHelloWorld(evt.toString() + "Temperature is " + thSensor.getTemperature());
		} else {
			new FreeTTSHelloWorld("Goodbye");
		}
	}

}
