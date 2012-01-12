import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Vector;


/**
 * Interface graphique de l'application
 * Gere les entree-sorties de l'utilisateur
 */
public class Graphique extends JFrame implements Runnable,ActionListener {

  private JTextArea texte = new JTextArea();  //zone d'affichage du texte des discussions
  private JTextField saisie = new JTextField();  //zone de saisie du texte de l'utilisateur
  private JList liste = new JList();  //liste d'affichage des images trouvees dans le reseau
  private JButton valider = new JButton("OK");  //bouton de validation du texte saisi
  private JButton chercher = new JButton("Chercher");  //bouton de lancement de la recherche des images dans le reseau
  private JButton selectionner = new JButton("Afficher");  //bouton de selection d'une image
  private MesPairs pair = null;  //Pair maitre de l'interface graphique


  /**
   * Constructeur de l'interface graphique
   */
  public Graphique(String nom,MesPairs mp) {
    super(nom); //nom du pair affiche en haut de la fenetre

    //reglages de parametres graphiques
    setSize(600,350);
    setResizable(false);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Container pane = getContentPane();
    pane.setLayout(null);
    texte.setEditable(false);
    JScrollPane scroll_texte = new JScrollPane(texte);
    JScrollPane scroll_liste = new JScrollPane(liste);

    //instanciation du pair
    pair = mp;

    //placement des elements graphiques sur la fenetre
    scroll_texte.setBounds(10,10,350,250);
    scroll_liste.setBounds(400,10,170,200);
    saisie.setBounds(10,280,300,25);
    valider.setBounds(320,280,60,25);
    chercher.setBounds(435,225,100,25);
    selectionner.setBounds(435,260,100,25);

    //ajout d'ecoute d'evenement sur les boutons
    valider.addActionListener(this);
    chercher.addActionListener(this);
    selectionner.addActionListener(this);

    //ajout des composants graphiques a la fenetre
    pane.add(scroll_texte);
    pane.add(scroll_liste);
    pane.add(saisie);
    pane.add(valider);
    pane.add(chercher);
    pane.add(selectionner);

    setContentPane(pane);
  }


  /**
   * Lancement de l'interface Graphique
   */
  public void run() {
    setVisible(true);
  }


  /**
   * Affichage d'un message recu dans la zone de texte
   */
  public void affiche(String chaine) {
    texte.append(chaine+"\n");
  }


  /**
   * Affichage de la liste des documents connus sur le réseau
   */
  public void afficheListe(Vector liste_doc) {
    Vector affiche = new Vector();

    //recuperation des noms des documents pour affichage dans la zone prevue
    for (int i=0;i<liste_doc.size();i++) {
      MesDocuments doc = (MesDocuments) liste_doc.get(i);
      affiche.add(doc.getNom());
    }

    //affichage de la liste de nom des documents
    liste.setListData(affiche);
  }


  /**
   * Gestion des evenements utilisateurs de l'interface graphique
   */
  public void actionPerformed(ActionEvent evt) {

    //activation du bouton "valider"
    if (evt.getSource()==valider) {
      pair.sendMessage(saisie.getText());
      saisie.setText("");
    }

    //activation du bouton "chercher"
    if (evt.getSource()==chercher) {
      pair.chercherDocument();
    }

    //activation du bouton "afficher"
    if (evt.getSource()==selectionner) {
      //recuperation des valeurs selectionnees dans la liste des documents
      String valeur = (String) liste.getSelectedValue();
      int indice = liste.getSelectedIndex();
      if (valeur != null) {
        pair.selectionDocument(indice);
      }
    }
  }

}
