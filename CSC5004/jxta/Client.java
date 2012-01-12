import java.io.*;
import java.net.*;


import net.jxta.document.*;
import net.jxta.endpoint.*;
import net.jxta.exception.*;
import net.jxta.peer.*;
import net.jxta.peergroup.*;
import net.jxta.pipe.*;
import net.jxta.util.*;
import net.jxta.protocol.*;
import net.jxta.id.*;
import java.util.HashSet;


/**
 * Classe permettant l'envoi de message sur un pipe (pipe d'emission)
 * La classe cree un pipe suivant un modele d'annonce (advertisement)
 * La classe fournit des fonctions d'envoi des messages
 */
public class Client extends Thread {

  private PeerGroup peerGroup = null;  //groupe d'appartenance du pair
  private OutputPipe outputPipe = null;  //pipe d'emission de message
  private String monFichier = "";  //nom du fichier contenant l'annonce


  /**
   * Constructeur de la classe Client
   */
  public Client(String s,PeerGroup p) {
    peerGroup = p;
    monFichier = s;
  }


  /**
   * Chargement de l'annonce et creation du pipe d'emission principal
   */
  public void loadPipeAdv(String fileName) throws FileNotFoundException, IOException {

      //chargement de l'annonce depuis un fichier
      FileInputStream file = new FileInputStream(fileName);
      MimeMediaType asMimeType = new MimeMediaType("text/xml");

      //il s'agit d'un pipe de diffusion (multicast)
      PipeAdvertisement pipeAdv = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(asMimeType, file);

      //creation du pipe de communication
      PipeService pipeService = peerGroup.getPipeService();
      outputPipe = pipeService.createOutputPipe(pipeAdv,60000);

    }


    /**
     * Lancement de la classe Client
     */
    public void run() {

      try {
        loadPipeAdv(monFichier);
      } catch (Exception e)  { System.err.println("Erreur dans Client.run() : "); e.printStackTrace(); }

    }


    /**
     * Envoi de messages sur le pipe principal (multicast)
     * Le message est de "Type" MESSAGE et contient les champs
     * ["Auteur" (nom de l'emetteur) --> c'est a vous de le faire] et "Texte" (texte du message)
     */
    public void sendMessage(String messageString) {

      //creation du nouveau message et des differents champs
      Message message = new Message();
      MessageUtilities.addString(message,"Type","MESSAGE");
      MessageUtilities.addString(message,"Texte", messageString);
      MessageUtilities.addString(message,"Auteur", peerGroup.getPeerName().toString());

      //si le pipe n'est pas null, j'envoie le message
      if (outputPipe != null) {
        try {
          outputPipe.send(message);  //envoi du message sur le pipe
        } catch (IOException e) {  }
      }
      else {
        System.err.println("Erreur dans Client.sendMessage() : OutputPipe == null");
      }

    }


    /**
     * Pour demander la liste des documents a tous les pairs
     * Le message est de "Type" DEMANDE_LISTE_DOCUMENTS
     * L'unique champ supplementaire est "Auteur" et contient le PeerID de l'expediteur
     */
    public void demandeListeDocuments(PeerID pid) {
      Message message = new Message();
      MessageUtilities.addString(message,"Type","DEMANDE_LISTE_DOCUMENTS");
      MessageUtilities.addString(message,"Auteur", peerGroup.getPeerID().toString());
      //creation du nouveau message et des differents champs

      //on utilise la fonction toString() afin de convertir n'importe quoi en chaine
      if (outputPipe != null) {
        try {
          outputPipe.send(message);  //envoi du message sur le pipe
        } catch (IOException e) {  }
      }
      else {
        System.err.println("Erreur dans Client.sendMessage() : OutputPipe == null");
      }

      //si le pipe n'est pas null, j'envoie le message


    }


    /**
     * Creation d'un pipe d'emission dirige vers un unique pair
     */
    public OutputPipe creationPipe(PeerID pid) {

      //pipe d'emission
      OutputPipe myPipe = null;

      try {

        FileInputStream file = new FileInputStream("PropagatePipeAdv.xml");
        MimeMediaType asMimeType = new MimeMediaType("text/xml");
        PipeAdvertisement pipeAdv = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(asMimeType, file);

        PipeService pipeService = peerGroup.getPipeService();

        //creation de l'ensemble (a un seul element dans notre cas) des pairs pouvant recevoir les messages
        HashSet resolve = new HashSet();
        resolve.add(pid);

        //creation du pipe de communication
        myPipe = pipeService.createOutputPipe(pipeAdv, resolve, 60000);

      } catch (Exception e) {System.err.println("Erreur dans Client.creationPipe()");}

      return myPipe;

    }


    /**
     * Envoi de la liste des documents locaux suite a une demande
     * L'envoi se fait sur un pipe temporaire dirige vers un seul pair destinataire
     * Le message est de "Type" ENVOI_LISTE
     * Les champs sont "Auteur" (PeerID) et "Liste" (liste des documents locaux)
     */
    public void envoiListeDocuments(PeerID pid,String listeDoc) {

        //creation d'un pipe d'emission temporaire
	OutputPipe tempPipe = creationPipe(pid);

        //creation du message et des differents champs
   	Message message = new Message();
	MessageUtilities.addString(message,"Type","ENVOI_LISTE");
	MessageUtilities.addString(message,"Auteur", peerGroup.getPeerID().toString());
	MessageUtilities.addString(message,"Liste", listeDoc);

        //si le pipe n'est pas null, j'envoie le message
	if (outputPipe != null) {
		try {
			tempPipe.send(message);  //envoi du message sur le pipe
		} catch (IOException e) { e.printStackTrace();  }
	}
	else {
        	System.err.println("Erreur dans Client.sendMessage() : OutputPipe == null");
	}
        //fermeture du pipe temporaire avec la methode close()
	tempPipe.close();

      }


      /**
       * Message de demande d'un document a un pair defini
       * Le message est de "Type" DEMANDE_DOC
       * Les autres champs sont "Auteur" (PeerID) et "NomDocument" (nom du document demande)
       */
      public void demanderDocument(PeerID pid,String nom_doc) {

        //creation d'un pipe d'emission temporaire
	OutputPipe tempPipe = creationPipe(pid);

        //creation du message et des differents champs
   	Message message = new Message();
	MessageUtilities.addString(message,"Type","DEMANDE_DOC");
	MessageUtilities.addString(message,"Auteur", peerGroup.getPeerID().toString());
	MessageUtilities.addString(message,"NomDocument", nom_doc);

        //si le pipe n'est pas null, j'envoie le message
	if (outputPipe != null) {
		try {
			tempPipe.send(message);  //envoi du message sur le pipe
		} catch (IOException e) { e.printStackTrace();  }
	}
	else {
        	System.err.println("Erreur dans Client.sendMessage() : OutputPipe == null");
	}
        //fermeture du pipe temporaire avec la methode close()
	tempPipe.close();


        }


        /**
         * Envoi du fichier sous forme d'une chaine de caracteres dans un message
         * Le "Type" du message est ENVOI_DOC
         * Les autres champs sont "Auteur" (PeerID) "NomDocument" (nom du fichier) "Contenu" (contenu du fichier)
         */
        public void envoyerFichier(String nom_doc,String le_fichier,PeerID pid) {
        	//creation d'un pipe d'emission temporaire
		OutputPipe tempPipe = creationPipe(pid);

		//creation du message et des differents champs
	   	Message message = new Message();
		MessageUtilities.addString(message,"Type","ENVOI_DOC");
		MessageUtilities.addString(message,"Auteur", peerGroup.getPeerID().toString());
		MessageUtilities.addString(message,"NomDocument", nom_doc);
		MessageUtilities.addString(message,"Contenu", le_fichier);

		//si le pipe n'est pas null, j'envoie le message
		if (outputPipe != null) {
			try {
				tempPipe.send(message);  //envoi du message sur le pipe
			} catch (IOException e) { e.printStackTrace();  }
		}
		else {
			System.err.println("Erreur dans Client.sendMessage() : OutputPipe == null");
		}
		//fermeture du pipe temporaire avec la methode close()
		tempPipe.close();




          }


        }
