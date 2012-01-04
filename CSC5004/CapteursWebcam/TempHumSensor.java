import java.util.Vector;

import oakRH.OakUsbRH;
import oakSensor.OakMemoryMode;
import oakSensor.OakReportMode;


public class TempHumSensor implements Runnable{
	private int time;
	private OakUsbRH rhSensor;
	private float humidity;
	private float temperature;
	private float oldTemperature;
	private Vector<TemperatureListener> vectTempListener;
	private Boolean first;
	
	public TempHumSensor(int t, OakUsbRH rhSensor) {
		this.time = t;
		this.rhSensor = rhSensor;
		this.vectTempListener = new Vector<TemperatureListener>();
		this.first = true;
		try {
			rhSensor.openSensor();
			rhSensor.setReportMode(OakReportMode.REPORT_MODE_FIXED_RATE, OakMemoryMode.RAM);
			rhSensor.setReportRate(500, OakMemoryMode.RAM);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			while (rhSensor.isOpened()) {
				byte[] data = null;
				data = rhSensor.readData();
				
				this.oldTemperature = this.temperature;
				
				setHumidity(Math.round(rhSensor.getHumidity(data)*1e-4*100));
				setTemperature(Math.round(rhSensor.getTemperature(data)*1e-2-273.15));
				
				if (this.first) {
					System.out.println("Première température :" + this.temperature);
					this.first = false;
				}
				
				if ((this.oldTemperature < 30) && (this.temperature >= 30)) {
					fireTemperatureEvent();
				}
				if ((this.oldTemperature > 27) && (this.temperature <= 27)) {
					fireTemperatureEvent();
				}
				
				Thread.sleep(time);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void fireTemperatureEvent() {
		if(!vectTempListener.isEmpty()) {
			TemperatureEvent te = new TemperatureEvent(this, this.temperature);
			
			for (TemperatureListener tl : vectTempListener) {
				tl.temperatureAlarm(te);
			}
		}
		
	}
	
	public void stop() {
		
	}

	public float getHumidity() {
		return humidity;
	}

	private void setHumidity(float humidity) {
		this.humidity = humidity;
	}

	public float getTemperature() {
		return temperature;
	}

	private void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	public void addTemperatureListener(TemperatureListener tl) {
		this.vectTempListener.add(tl);
		
	}
	
	public void removeTemperatureListener(TemperatureListener tl) {
		this.vectTempListener.remove(tl);
		
	}
	
	
	
}
