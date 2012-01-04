import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;


public class Audio implements Runnable{
	private Player player;
	
	public Audio() throws FileNotFoundException, JavaLayerException {
		player = new Player(new BufferedInputStream(new FileInputStream("track.mp3")));
	}
	
	public void run() {
		
		try {
			player.play();
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		player.close();
	}
	
}
