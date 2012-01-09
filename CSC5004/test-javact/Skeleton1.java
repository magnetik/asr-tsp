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

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.javact.lang.*;
import org.javact.net.rmi.CreateCt;
import org.javact.net.rmi.SendCt;
import org.javact.util.StandAlone;

/**
 * Behaviour for the actor Recherche
 */
class RechercheBeh extends RechercheQuasiBehavior implements StandAlone {
	private static final long serialVersionUID = 1L;

	private int position;

	private String[] availableMachine;

	HashMap<String, Double> hm; 
	
	String origin;

	public RechercheBeh () {
		availableMachine = JavActProbe.probe(2007);
		hm = new HashMap<String, Double>();
		position = -1;
		String origin = null;
	}

	@Override
	public void run() {
		// Get identifier
		Double load;
		String me = myPlace();
		
		// Saving the origin to display the result on origin
		// at the end
		if (origin == null) { origin = me; }

		// Get load
		OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
		if (bean == null) {
			throw new NullPointerException("Unable to collect operating system metrics, jmx bean is null");
		}
		load = new Double(bean.getSystemLoadAverage());

		// Put my load in the hashmap
		hm.put(me, load);

		if (hm.size() == availableMachine.length) {
			// All machine has been probed,
			// Get the less loaded
			// And move to it
			String HMmachine = null;
			Double min = new Double(100);
			Double HMload;
			String minMachine = new String();
			// Getting the minimum of the HashMap
			// Minimum by value
			for(Entry<String, Double> entry : hm.entrySet()) {
				HMmachine = entry.getKey();
				HMload = entry.getValue();
				System.out.println("Load of " + HMmachine + " is " + HMload);
				if (HMload < min) {
					minMachine = HMmachine;
					min = HMload;
				}
			}
			System.out.println("----> The less loaded machine is " + minMachine);
			// Go to the less loaded machine
			become(new SupervisorBeh(origin));
			go(minMachine);
		}
		else {
			position++;
			go(availableMachine[position]);
		}
	}
}

/**
 * Behaviour for the actor Supervisor
 */
class SupervisorBeh extends SupervisorQuasiBehavior implements StandAlone {
	String origin;
	
	public SupervisorBeh(String origin) {
		this.origin = origin;
	}
	
	@Override
	public void run() {
		String me = myPlace();

		System.out.println("Hello, i'm " + me + "; the less loaded machine");
		System.out.println("Calculating PI...");
		
		Pi pi = new Pi(1000);
		BigDecimal result = pi.call();
		
		System.out.println("Calcul OK. Moving to origin: ");

		become(new AfficheurBeh(result));
		go(origin);
	}

}

/**
 * Behaviour for the actor Afficheur
 */
class AfficheurBeh extends AfficheurQuasiBehavior implements StandAlone {
	BigDecimal result;
	
	public AfficheurBeh (BigDecimal result) {
		this.result = result;
	}
	@Override
	public void run() {
		System.out.println("Hello, i'm " + myPlace() + "Result is :" + result);
		suicide();
	}

}

public class Skeleton1 {

	public static void main(String[] args) {
		CreateCt.STD.create("localhost:2007", new RechercheBeh());

	}

}
