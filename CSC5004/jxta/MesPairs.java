import java.util.*;
import java.lang.*;

import net.jxta.exception.*;
import net.jxta.peergroup.*;
import net.jxta.peer.*;
import net.jxta.id.*;
import java.net.URI;


/**
 * Classe principale de l'application
 * Elle cree un pair JXTA accompagne de differents services
 */
public class MesPairs extends Thread {

  private PeerGroup groupe = null;  //groupe de pairs d'appartenance
  private Client client = null;  //envoie des messages
  private Serveur serveur = null;  //reception des messages
  private Decouverte decouverte = null;  //recherche des pairs
  private Graphique graphe = null;  //interface graphique du pair
  private String monNom = null;  //nom du pair dans le groupe
  private FenetreSecondaire secondaire = null;  //fenetre d'affichage des documents
  private Vector documentsConnus = new Vector();  //liste des documents connus sur le reseau
  private ExplorationLocale exploration = null;  //exploration des fichiers locaux


  /**
   * Constructeur de la classe MesPairs
   */
  public MesPairs() {

    try {
      groupe = PeerGroupFactory.newNetPeerGroup();  //cree un nouveau groupe general
    } catch (Exception e) {System.err.println("MesPairs() : Erreur de creation du groupe");}

    //instanciation des services de decouverte et de communications
    decouverte = new Decouverte(groupe);
    client = new Client("PropagatePipeAdv.xml",groupe);
    serveur = new Serveur(this,"PropagatePipeAdv.xml",groupe);

    //recuperation du nom du pair dans le groupe general
    monNom = groupe.getPeerName().toString();

    //instanciation de l'interface graphique
    graphe = new Graphique(monNom,this);

    //creation du module d'exploration des documents locaux
    exploration = new ExplorationLocale("documents");

    //creation de la fenetre secondaire pour les documents
    secondaire = new FenetreSecondaire();

  }


  /**
   * Methode de lancement de la classe MesPairs
   * lance le client, le serveur, la decouverte, l'interface graphique et la fenetre secondaire
   */
  public void run() {
    decouverte.start();
    client.start();
    serveur.start();
    graphe.run();
    secondaire.run();
    travail();
  }


  /**
   * Oblige le pair à effectuer une recherche de pairs
   * dans le réseau toutes les 5 secondes
   */
  public void travail() {
    while (true) {
      decouverte.rechercher();  //recherche des pairs dans le reseau
      try {
        Thread.sleep(5000);  //endormissement du thread
      } catch (Exception e) {System.err.println("MesPairs.travail() : Impossible de dormir");}
    }
  }


  /**
   * Envoi d'un message par l'utilisateur
   */
  public void sendMessage(String message) {
    //envoi d'un message a l'ensemble des pairs
    client.sendMessage(message);
  }


  /**
   * Affichage d'un message recu
   */
  public void afficheMessage(String message) {
    graphe.affiche(message);
  }


  /**
   * Recherche des documents disponibles dans le reseau de pairs
   */
  public void chercherDocument() {
    documentsConnus.clear();

    //demande des documents disponibles a l'ensemble des pairs
    client.demandeListeDocuments(groupe.getPeerID());

  }


  /**
   * Transformation d'une chaine de caracteres contenant un PeerID
   * en un objet du type PeerID
   */
  public PeerID chaineVersPeerID(String pid_str) {
    PeerID dest_pid = null;

    try {
      dest_pid = (PeerID) ID.create(new URI(pid_str));
    } catch (Exception e) { System.err.println("Erreur dans MesPairs.chaineVersPeerID()");  }

    return dest_pid;
  }


  /**
   * Recuperation aupres du proprietaire du document selectionne
   */
  public void selectionDocument(int place) {

    MesDocuments doc = (MesDocuments) documentsConnus.get(place);
    String le_nom = doc.getNom();
    String proprio = doc.getProprietaire();

    //recuperation du PeerID du destinataire
    PeerID dest_pid = chaineVersPeerID(proprio);

    //demande d'un document a un pair
    client.demanderDocument(dest_pid,le_nom);

  }


  /**
   * Prepare l'envoi d'un document à la demande d'un pair
   */
  public void traiterDemandeDocument(String sender_pid,String nom_doc) {
    String le_fichier = new String(exploration.recupererFichier(nom_doc));

    //recuperation du PeerID du destinataire
    PeerID dest_pid = chaineVersPeerID(sender_pid);

    //envoi du fichier au demandeur
    client.envoyerFichier(nom_doc,le_fichier,dest_pid);

  }


  /**
   * Preparation d'une reponse a une demande de lsite de documents
   */
  public void repondreListe(String pid) {
    String liste_doc = exploration.lister();

    //recuperation du PeerID du destinataire
    PeerID sender_pid = chaineVersPeerID(pid);

    //envoi de la liste des documents au demandeur
    client.envoiListeDocuments(sender_pid,liste_doc);
  }


  /**
   * Reception et traitement d'une liste de documents venant d'un pair
   */
  public void messageListeRecu(String proprio,String la_liste) {
    String temp = "";
    int index = 0;

    //parcourt de la liste des documents
    while (index<la_liste.length()) {

      while (la_liste.charAt(index) != ';') {
        temp += la_liste.charAt(index);
        index++;
      }

      //memorisation des documents de la liste
      MesDocuments doc = new MesDocuments(temp,proprio);
      if (!documentsConnus.contains(doc)) {
        documentsConnus.add(doc);
      }

      temp = "";
      index++;

    }

    //affichage du resultat de la requete de documents
    graphe.afficheListe(documentsConnus);

  }


  /**
   * Reception du document distant demande par l'utilisateur
   */
  public void receptionDocument(String nom_doc,String contenu_doc) {

    //mise en forme puis affichage du document recu
    secondaire.afficher("   ...    "+nom_doc+"    ...\n\n"+contenu_doc);
    secondaire.montrer();

 }



}
