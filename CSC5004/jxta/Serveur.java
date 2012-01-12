import java.io.*;
import java.util.*;
import java.net.*;

import net.jxta.discovery.*;
import net.jxta.document.*;
import net.jxta.endpoint.*;
import net.jxta.exception.*;
import net.jxta.peergroup.*;
import net.jxta.pipe.*;
import net.jxta.protocol.*;
import net.jxta.peer.*;
import net.jxta.util.*;
import net.jxta.id.*;



/**
 * Classe d'ecoute des messages dans le groupe
 * Creation d'un pipe d'ecoute
 * Methode de traitement des messages entrant
 */
public class Serveur extends Thread implements PipeMsgListener {


  private PeerGroup peerGroup = null;  //groupe d'appartenance du pair
  private InputPipe inputPipe = null;  //pipe d'entree des messages
  private String monFichier = "";  //nom du fichier de l'annonce
  private DiscoveryService discovery = null;  //service de publication des annonces
  private MesPairs monPair = null;  //pair maitre du serveur


  /**
   * Constructeur de la classe Serveur
   */
  public Serveur(MesPairs pair,String s,PeerGroup p) {
    monFichier = s;
    peerGroup = p;
    monPair = pair;
  }


  /**
   * Chargement de l'annonce et creation du pipe d'ecoute
   */
  public void loadPipeAdv(String fileName) throws FileNotFoundException, IOException {

    //chargement de l'annonce
    FileInputStream file = new FileInputStream(fileName);
    MimeMediaType asMimeType = new MimeMediaType("text/xml");
    PipeAdvertisement pipeAdv = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(asMimeType, file);

    //creation du service de publication des annonces
    discovery = peerGroup.getDiscoveryService();
    discovery.publish(pipeAdv,60000,60000);
    discovery.remotePublish(pipeAdv,(long) 60000);

    //creation du pipe d'ecoute
    PipeService pipeService = peerGroup.getPipeService();
    inputPipe = pipeService.createInputPipe(pipeAdv, this);

  }


  /**
   * Lancement du serveur
   */
  public void run() {
    try {
      loadPipeAdv(monFichier);
    } catch (Exception e) { System.out.println("\nErreur dans Serveur.run() : "); e.printStackTrace();}
  }


  /**
   * Notification d'arrivee des differents messages sur le pipe
   */
  public void pipeMsgEvent(PipeMsgEvent event) {

    //recuperation du message venant d'arriver et de son type
    Message message = event.getMessage();
    String type = message.getMessageElement("Type").toString();

    //il s'agit d'un message texte provenant de la partie "chat"
    if (type.equals("MESSAGE")) {
      monPair.afficheMessage(message.getMessageElement("Auteur").toString() + ": " + message.getMessageElement("Texte").toString());
    }
    //c'est un message de demande de la liste de documents (appui sur bouton "chercher")
    if (type.equals("DEMANDE_LISTE_DOCUMENTS")) {
      monPair.afficheMessage("Demande de liste de doc de :" + message.getMessageElement("Auteur").toString());
      monPair.repondreListe(message.getMessageElement("Auteur").toString());
    }
    //c'est un message contenant la liste des documents d'un pair
    if (type.equals("ENVOI_LISTE")) {
      monPair.afficheMessage("Reception liste de " + message.getMessageElement("Auteur").toString());
      monPair.messageListeRecu(message.getMessageElement("Auteur").toString(),message.getMessageElement("Liste").toString());
      
    }
    //message de demande d'un document particulier (appui sur bouton "afficher")
    if (type.equals("DEMANDE_DOC")) {
       monPair.traiterDemandeDocument(message.getMessageElement("Auteur").toString(),message.getMessageElement("NomDocument").toString());
    }
    //reception d'un message contenant un document suite a une demande du pair
    if (type.equals("ENVOI_DOC")) {
       monPair.receptionDocument(message.getMessageElement("Auteur").toString(),message.getMessageElement("Contenu").toString());
    }


  }


}

