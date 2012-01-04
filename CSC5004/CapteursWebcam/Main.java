import oakRH.OakUsbRH;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		OakUsbRH rhSensor = new OakUsbRH();
		int time = 1000;
		
		TempHumSensor thSensor = new TempHumSensor(time, rhSensor);
		(new Thread(thSensor)).start();
		FaceDetection fd = new FaceDetection();
		
		TestFaceEvent tfe = new TestFaceEvent(thSensor);
		fd.addFaceListener(tfe);
		
		TestTemperatureEvent tte = new TestTemperatureEvent();
		thSensor.addTemperatureListener( tte ) ;
		
		try {
			Audio audio = new Audio();
			(new Thread(audio)).start();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		
		

	}

}
