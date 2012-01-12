import java.awt.*;
import javax.swing.*;


/**
 * Classe permettant l'affichage d'une fentre supplementaire pour les documents
 * Le document sera un cours texte dans notre application
 */
public class FenetreSecondaire extends JFrame implements Runnable {

  private JTextArea texte = new JTextArea();  //zone d'affichage des documents


    /**
     * Constructeur de la classe FenetreSecondaire
     * Regle les differents parametres d'affichage graphique
     */
    public FenetreSecondaire() {
      super("Affichage");
      setSize(250,220);
      setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
      setResizable(false);

      Container pane = getContentPane();
      pane.setLayout(null);

      texte.setEditable(false);
      JScrollPane scroll = new JScrollPane(texte);
      scroll.setBounds(5,5,235,170);

      pane.add(scroll);
      setContentPane(pane);
    }


    /**
     * Lancement de la classe FenetreSecondaire
     */
    public void run() {

    }


    /**
     * Methode pour faire apparaitre la fenetre secondaire de l'application
     */
    public void montrer() {
      setVisible(true);
    }


    /**
     * Affiche le texte dans la zone d'affichage des documents
     */
    public void afficher(String str) {
      texte.setText(str);
    }

  }
