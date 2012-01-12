import java.io.*;


/**
 * Classe permettant la manipulation des fichiers locaux a partager
 */
public class ExplorationLocale {

  private File repertoire = null;  //repertoire contenant les documents

  /**
   * Constructeur de la classe ExplorationLocale
   */
  public ExplorationLocale(String nom) {
    repertoire = new File(nom);
  }

  /**
   * Retourne la liste des documents du repertoire sous forme de chaine
   */
  public String lister() {
    String liste = "";
    String[] table = repertoire.list();

    for (int i=0;i<table.length;i++) {
      //creation de la chaine a retourner
      liste += table[i]+";";
    }

    return liste;
  }


  /**
   * Accede au fichier dont le nom est donne en parametre
   * Retourne un tableau de byte contenant le fichier
   */
  public byte[] recupererFichier(String nom_fichier) {

    byte[] table = null;
    RandomAccessFile lire = null;  //flux de lecture sur le fichier

    try {
      lire = new RandomAccessFile("documents/"+nom_fichier, "r");
      int taille = (int) lire.length();
      table = new byte[taille];  //creation du tableau a la bonne taille
    } catch (IOException ioe) {System.err.println("Erreur dans ExplorationLocale.recupererFichier()_1"); ioe.printStackTrace();}

    try {
      lire.readFully(table);  //stockage du fichier sous forme de bytes dans le tableau
    } catch (IOException ioe) {System.err.println("Erreur dans ExplorationLocale.recupererFichier()_2"); ioe.printStackTrace();}

    return table;

  }


}
