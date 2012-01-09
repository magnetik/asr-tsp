/*
 * ###########################################################################
 * JavAct: A Java(TM) library for distributed and mobile actor-based computing
 * Copyright (C) 2001-2007 I.R.I.T./C.N.R.S.-I.N.P.T.-U.P.S.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 *
 * Initial developer(s): The I.A.M. Team (I.R.I.T.) - SMAC Team (IRIT) since 2007
 * Contributor(s): The I.A.M. Team (I.R.I.T.) - SMAC Team (IRIT) since 2007
 * Contact: javact@irit.fr
 * ###########################################################################
 */

import java.io.IOException;

import hidException.HidNotOpenDeviceException;
import oakLux.OakUsbLux;
import oakSensor.OakMemoryMode;
import oakSensor.OakReportMode;

import org.javact.lang.*;
import org.javact.net.rmi.CreateCt;
import org.javact.net.rmi.SendCt;
import org.javact.util.StandAlone;

import x10.CM11ASerialController;
import x10.Command;
import x10.OperationTimedOutException;

/**
 * Behaviour for the actor Recherche
 */
class RechercheBeh extends RechercheQuasiBehavior implements StandAlone {
	private String placeLumiere;
	private String placeCapteurLumiere;
	private String[] availableMachine;
	private int position;

	public RechercheBeh() {
		placeLumiere = null;
		placeCapteurLumiere = null;

		availableMachine = new String[2];
		//availableMachine = JavActProbe.probe(2007);
		availableMachine[0] = "157.159.110.49:2007";
		availableMachine[1] = "157.159.110.57:2007";

		position = -1;
	}

	public void run() {
		System.out.println("Testing sensor on machine" + myPlace());
		// Cherche le capteur de lum
		OakUsbLux luxSensor = new OakUsbLux();
		try {
			luxSensor.openSensor();
			//luxSensor.setReportMode(OakReportMode.REPORT_MODE_FIXED_RATE, OakMemoryMode.RAM); 
			//luxSensor.setReportRate(500, OakMemoryMode.RAM);
			luxSensor.releaseSensor();

			System.out.println("###########Capteur lumière trouvé:" + myPlace());

			placeCapteurLumiere = myPlace();
		} catch (Exception e) {
			// Sensor is absent !
		}

		CM11ASerialController controller;
		try {
			controller = new CM11ASerialController("/dev/ttyS0");

			System.out.println("###########Lumière trouvée:" + myPlace());

			controller.shutdown(500);

			placeLumiere = myPlace();
		} catch (Exception e) {
			// Pas de lampe ici
		}
		System.out.println(placeLumiere + " " + placeCapteurLumiere);

		if ((placeLumiere != null) && (placeCapteurLumiere != null)) {
			System.out.println("Creation des agents");
			Agent refLumiereAgent = CreateCt.STD.create(placeLumiere, new LumiereBeh());
			CreateCt.STD.create(placeCapteurLumiere, new CapteurLumiereBeh(refLumiereAgent));
		}
		else {
			position++;
			go(availableMachine[position]);
		}



	}

}

/**
 * Behaviour for the actor Lumiere
 */
class LumiereBeh extends LumiereQuasiBehavior {

	public void off() {
		System.out.println("Agent lumière: Message reçu extinction de la lampe");
		CM11ASerialController controller;
		try {
			controller = new CM11ASerialController("/dev/ttyS0");
			controller.addCommand(new Command("A3",Command.OFF));
			controller.shutdown(5000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperationTimedOutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void on() {
		System.out.println("Agent lumière: Message reçu, Allumage de la lampe");
		CM11ASerialController controller;
		try {
			controller = new CM11ASerialController("/dev/ttyS0");
			controller.addCommand(new Command("A3",Command.ON));
			controller.shutdown(5000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperationTimedOutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

/**
 * Behaviour for the actor CapteurLumiere
 */
class CapteurLumiereBeh extends CapteurLumiereQuasiBehavior implements StandAlone {
	private Agent refLumiereAgent;
	private byte[] data;
	public CapteurLumiereBeh(Agent refLumiereAgent) {
		this.refLumiereAgent = refLumiereAgent;
	}

	public void run() {
		System.out.println("Agent capteur lumière créée.");
		OakUsbLux luxSensor = new OakUsbLux();
		try {

			luxSensor.openSensor();
			luxSensor.setReportMode(OakReportMode.REPORT_MODE_FIXED_RATE, OakMemoryMode.RAM); 
			luxSensor.setReportRate(500, OakMemoryMode.RAM);
			data = luxSensor.readData();
			System.out.println("Lecture de la temperature:" + luxSensor.getIlluminance(data));
			if (luxSensor.getIlluminance(data) < 600) {
				send(new JAMon(), refLumiereAgent);
				System.out.println("Faible luminositée... Envoie du message d'allumage");
			}
			else {
				send(new JAMoff(), refLumiereAgent);
				System.out.println("Forte luminositée ... Envoie du message d'extinction");
			}
			
			luxSensor.releaseSensor();
			
			System.out.println("CapteurLumière: suicide. On pourrait rajouter une boucle");
			suicide();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}
}

public class Skeleton1 {

	public static void main(String[] args) {
		CreateCt.STD.create("localhost:2007", new RechercheBeh());
	}

}
