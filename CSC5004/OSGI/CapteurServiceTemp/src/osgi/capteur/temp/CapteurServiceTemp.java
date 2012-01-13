package osgi.capteur.temp;

import hidException.HidNotOpenDeviceException;
import oakRH.OakUsbRH;
import osgi.capteur.CapteurService;

public class CapteurServiceTemp implements CapteurService {
	private OakUsbRH rhSensor;
	
	public CapteurServiceTemp () {
		rhSensor = new OakUsbRH();
		try {
			rhSensor.openSensor();
		} catch (HidNotOpenDeviceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getData() {
		try {
			if (rhSensor.isOpened()) {
				byte[] data = rhSensor.readData();
				return (int)Math.round(rhSensor.getTemperature(data)-273.15);
			}
			else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} 

	}

}
