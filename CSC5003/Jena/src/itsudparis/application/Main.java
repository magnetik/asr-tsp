/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package itsudparis.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.hp.hpl.jena.rdf.model.Model;
import itsudparis.tools.JenaEngine;

/**
 * @author DO.ITSUDPARIS
 */
public class Main {
	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		String NS = "";
		// lire le model a partir d'une ontologie
		Model model = JenaEngine.readModel("data/family.owl");
		if (model != null) {
			//lire le Namespace de l’ontologie
			NS = model.getNsPrefixURI("");
//			// modifier le model
//			// Ajouter une nouvelle femme dans le modele: Nora, 50, estFilleDe Peter
//			JenaEngine.createInstanceOfClass(model, NS, "Femme", "Nora");
//			JenaEngine.updateValueOfDataTypeProperty(model, NS, "Nora", "age", 50);
//			JenaEngine.updateValueOfObjectProperty(model, NS, "Nora", "estFilleDe", "Peter");
//
//			// Ajouter un nouvel homme dans le modele: Rob, 51, seMarierAvec Nora
//			JenaEngine.createInstanceOfClass(model, NS, "Homme", "Rob");
//			JenaEngine.updateValueOfDataTypeProperty(model, NS, "Rob", "age", 51);
//			JenaEngine.updateValueOfDataTypeProperty(model, NS, "Rob", "nom", "Rob Yeung");
//			JenaEngine.updateValueOfObjectProperty(model, NS, "Rob", "seMarierAvec", "Nora");
//			
//			//
//			JenaEngine.createInstanceOfClass(model, NS, "Homme", "Bill");
//			JenaEngine.updateValueOfDataTypeProperty(model, NS, "Bill", "age", 75);
//			JenaEngine.updateValueOfDataTypeProperty(model, NS, "Bill", "nom", "Bill Gates");
//			JenaEngine.updateValueOfObjectProperty(model, NS, "Bill", "seMarierAvec", "Nora");
//			
			//apply owl rules on the model
			Model owlInferencedModel = JenaEngine.readInferencedModelFromRuleFile(model, "/mci/ei0912/garrone/workspace/jena/src/data/owlrules.txt");
			// apply our rules on the owlInferencedModel
			Model inferedModel = JenaEngine.readInferencedModelFromRuleFile(owlInferencedModel, "/mci/ei0912/garrone/workspace/jena/src/data/rules.txt");
			
			//Aplli
			
			InputStreamReader lecteur=new InputStreamReader(System.in);
			BufferedReader entree=new BufferedReader(lecteur);
			String nom;
			while (true) {
				try {
					nom=entree.readLine();
					// Récupération des parents
					String parents = JenaEngine.executeQueryFileWithParameter(inferedModel, "/mci/ei0912/garrone/workspace/jena/src/data/queryParents.txt", nom);
					System.out.println("Parents de " + nom);
					System.out.println(parents);
					
					// Récupération des freres et soeurs
					String fraterie = JenaEngine.executeQueryFileWithParameter(inferedModel, "/mci/ei0912/garrone/workspace/jena/src/data/queryFraterie.txt", nom);
					System.out.println("Fraterie de " + nom);
					System.out.println(fraterie);
					
					// Récupération du conjoint
					String conjoint = JenaEngine.executeQueryFileWithParameter(inferedModel, "/mci/ei0912/garrone/workspace/jena/src/data/queryConjoint.txt", nom);
					System.out.println("Conjoint de " + nom);
					System.out.println(conjoint);
					
					// Récupération des prétendants
					String pretendants = JenaEngine.executeQueryFileWithParameter(inferedModel, "/mci/ei0912/garrone/workspace/jena/src/data/queryPretendants.txt", nom);
					System.out.println("Pretendants de " + nom);
					System.out.println(pretendants);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		} else {
			System.out.println("Error when reading model from ontology");
		}
	}
}