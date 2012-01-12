/**
 * Classe permettant la gestion des documents connus dans le reseau
 * Un document comporte un nom et un proprietaire
 */
public class MesDocuments {

  private String nom = null;  //nom du document
  private String proprietaire = null;  //identifiant du pair possedant le document


  /**
   * Constructeur de la classe MesDocuments
   */
  public MesDocuments(String n,String pid) {
    nom = n;
    proprietaire = pid;
  }


  /**
   * Retourne le nom du document
   */
  public String getNom() {
    return nom;
  }


  /**
   * Retourne l'identifiant du proprietaire du document
   */
  public String getProprietaire() {
    return proprietaire;
  }


}
