/**
 * Copyright 2003 Sun Microsystems, Inc.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 */
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

/**
 * Simple program to demonstrate the use of the FreeTTS speech
 * synthesizer.  This simple program shows how to use FreeTTS
 * without requiring the Java Speech API (JSAPI).
 */
public class FreeTTSHelloWorld implements Runnable {
	
	private String phrase;
	Thread t = null;
	
	public FreeTTSHelloWorld(String phrase) {
		this.phrase = phrase;
		t = new Thread(this);
        t.start();
	}
	
    /**
     * Example of how to list all the known voices.
     */
    public static void listAllVoices() {
        System.out.println();
        System.out.println("All voices available:");        
        VoiceManager voiceManager = VoiceManager.getInstance();
        Voice[] voices = voiceManager.getVoices();
        for (int i = 0; i < voices.length; i++) {
            System.out.println("    " + voices[i].getName()
                               + " (" + voices[i].getDomain() + " domain)");
        }
    }

    public void run() {
        String voiceName = "kevin16";
        
        /* The VoiceManager manages all the voices for FreeTTS.
         */
        VoiceManager voiceManager = VoiceManager.getInstance();
        Voice helloVoice = voiceManager.getVoice(voiceName);

        if (helloVoice == null) {
            System.err.println(
                "Cannot find a voice named "
                + voiceName + ".  Please specify a different voice.");
            System.exit(1);
        }
        
        /* Allocates the resources for the voice.
         */
        helloVoice.allocate();

        /* Synthesize speech.
         */
        helloVoice.speak(this.phrase);

        /* Clean up and leave.
         */
        helloVoice.deallocate();
    }

}
