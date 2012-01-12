import net.jxta.discovery.*;
import net.jxta.exception.*;
import net.jxta.peergroup.*;
import net.jxta.peer.*;


/**
 * Classe permettant la decouverte des annonces (advertisements)
 * circulant sur le reseau
 */
public class Decouverte extends Thread implements DiscoveryListener {

  private PeerGroup netPeerGroup = null;  //groupe principal d'appartenance du pair
  private DiscoveryService discovery = null;  //service de decouverte du groupe


  /**
   * Constructeur de la classe Decouverte
   */
  public Decouverte(PeerGroup p) {

    netPeerGroup = p;
    discovery = netPeerGroup.getDiscoveryService();
    discovery.addDiscoveryListener(this);

  }

  /**
   * Lancement de la classe Decouverte
   */
  public void run() {

  }

  /**
   * Recherche des pairs dans le reseau
   */
  public void rechercher() {

    try {
      //decouverte des annonces distantes concernant les pairs
      discovery.getRemoteAdvertisements(null, DiscoveryService.PEER, null, null,50);
    }
    catch (Exception e) {
      System.err.println("\nErreur dans Decouverte.rechercher() : ");
      e.printStackTrace();
    }
  }

  /**
   * Reception des evenements lies a la decouverte de pairs (d'annonces)
   */
  public void discoveryEvent(DiscoveryEvent ev) {

  }

}


