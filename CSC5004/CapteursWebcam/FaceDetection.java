/*
* TP VAP ASR - CSC5004 
* Copyright (C) 2009-2010  Institut TELECOM ; TELECOM Sudparis
* All rights reserved.
* Author: S. LERICHE - sebastien.leriche@it-sudparis.eu
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of the Institut TELECOM, TELECOM SudParis nor the
*       names of its contributors may be used to endorse or promote products
*       derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
* EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import hypermedia.video.OpenCV;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.image.MemoryImageSource;
import java.util.Vector;

import javax.swing.JFrame;

public class FaceDetection extends JFrame implements Runnable {

    private static final long serialVersionUID = 1L;

    // program execution frame rate (millisecond)
    final int FRAME_RATE = 200;

    OpenCV cv = null; // OpenCV Object

    Thread t = null; // the sample thread

    private Image icon;

    // the input video stream image
    Image frame = null;

    // list of all face detected area
    Rectangle[] squares = new Rectangle[0];
    
    private Boolean hasFace;
	private boolean oldFace;

	private Vector<FaceListener> vectFaceListener;
	
    private int iw;

    private int ih;

    private Font font = new Font("Arial", Font.BOLD, 18);

    private BufferStrategy strategy;

    private Graphics2D buffer;



	

    /**
     * Setup Frame and Object(s).
     */
    FaceDetection() {

        super("Détection de visages - Fête de la Science 2009");
        vectFaceListener = new Vector<FaceListener>();
        
        // OpenCV setup
        cv = new OpenCV();
        cv.capture(320, 240);
        cv.cascade(OpenCV.CASCADE_FRONTALFACE_ALT);

        // frame setup
//        this.setBounds(100, 100, cv.width * 2, cv.height * 2);
//        this.setBackground(Color.WHITE);
//        this.setVisible(true);
//        this.addKeyListener(new KeyAdapter() {
//            public void keyReleased(KeyEvent e) {
//                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { // ESC : release
//                    // OpenCV resources
//                    cv.dispose();
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e1) {
//                        // TODO Auto-generated catch block
//                        e1.printStackTrace();
//                    }
//                    System.exit(0);
//                }
//            }
//        });
//
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//
//        Dimension tailleEcran = java.awt.Toolkit.getDefaultToolkit()
//                .getScreenSize();
//        int sizeX = tailleEcran.width * 2 / 3;
//        int sizeY = Math.round(sizeX / (float) 1.33);
//        int PSizeX = getPreferredSize().width;
//        int PSizeY = getPreferredSize().height;
//
//        setSize(Math.min(tailleEcran.width, Math.max(sizeX, PSizeX)), Math.min(
//                tailleEcran.height, Math.max(sizeY, PSizeY)));
//        setLocationRelativeTo(null);
//
//        ImageIcon aux = new ImageIcon("logo_TSP.gif");
//        if (aux != null) {
//            icon = aux.getImage();
//        }
//        if (icon != null) {
//            setIconImage(icon);
//        }
//        iw = icon.getWidth(null);
//        ih = icon.getHeight(null);
//
//        // inhibe la méthode courante d'affichage du composant
//        setIgnoreRepaint(true);
//
//        // on créé 2 buffers dans la VRAM donc c'est du double-buffering
//        createBufferStrategy(2);
//
//        // récupère les buffers graphiques dans la mémoire VRAM
//        strategy = getBufferStrategy();
//        buffer = (Graphics2D) strategy.getDrawGraphics();
//
//        this.setResizable(false);

        // start running program
        t = new Thread(this);
        t.start();
    }

    /**
     * Draw video frame and each detected faces area.
     */
    public void render() {

        RenderingHints r = new RenderingHints(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED);
        buffer.setRenderingHints(r);

        // draw image
        // g2.drawImage( frame, 0, 0, null );
        buffer.drawImage(frame, 0, 0, getWidth(), getHeight(), 0, 0, cv.width,
                cv.height, null);

        float rw = getWidth() / (float) cv.width;
        float rh = getHeight() / (float) cv.height;

        buffer.setColor(Color.BLACK);
        buffer.setFont(font);

        buffer.drawImage(icon, getWidth() - iw - 50, getHeight() - ih - 50, iw,
                ih, null);
        buffer.drawString("Département INFormatique", getWidth() - iw - 350,
                getHeight() - 40);
        buffer.drawString("Conception et réalisation : Sébastien Leriche",
                getWidth() - iw - 350, getHeight() - 20);

        // draw squares
        buffer.setColor(Color.RED);
        for (Rectangle rect : squares)
            buffer.drawRect(Math.round(rect.x * rw), Math.round(rect.y * rh),
                    Math.round(rect.width * rw), Math.round(rect.height * rh));

        strategy.show();
    }

    /**
     * Execute this sample.
     */
    public void run() {
        while (t != null && cv != null) {

            // grab image from video stream
            cv.read();

            // create a new image from cv pixels data
            MemoryImageSource mis = new MemoryImageSource(cv.width, cv.height,
                    cv.pixels(), 0, cv.width);
            frame = createImage(mis);
            
            this.oldFace = getFace();
            
            // detect faces
            squares = cv.detect(1.2f, 2, OpenCV.HAAR_DO_CANNY_PRUNING, 20, 20);
            
            this.hasFace = getFace();
            
            
            if (oldFace != hasFace) {
            	fireFaceEvent();
            }
            // of course, render
            //render();
            try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }
    
    public void fireFaceEvent() {
		if(!vectFaceListener.isEmpty()) {
			FaceEvent fe = new FaceEvent(this, this.hasFace);
			
			for (FaceListener fl : vectFaceListener) {
				fl.FaceAlarm(fe);
			}
		}
		
	}
    
    public void addFaceListener(FaceListener tl) {
		this.vectFaceListener.add(tl);
		
	}
	
	public void removeFaceListener(FaceListener tl) {
		this.vectFaceListener.remove(tl);
		
	}
    
    public boolean getFace() {
    	if (squares.length == 0) {
        	return false;
        }
        else {
        	return true;
        }
    }

    public Boolean hasFace() {
    	return this.hasFace;
    }
    /**
     * Main method.
     * 
     * @param String
     *            [] a list of user's arguments passed from the console to this
     *            program
     */
    public static void main(String[] args) {
        System.out.println("\nOpenCV face detection sample\n");
        new FaceDetection();
    }

}
