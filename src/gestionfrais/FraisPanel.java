

package gestionfrais;
// Déclare que cette classe appartient au package 'gestionfrais'.

import java.sql.*;
// Importe toutes les classes et interfaces du package java.sql (pour la gestion de base de données).
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import javax.swing.JOptionPane;
// Importe la classe JOptionPane pour afficher des boîtes de dialogue.
import javax.swing.table.DefaultTableModel;
// Importe la classe DefaultTableModel pour manipuler les données de la JTable.

import com.itextpdf.text.Document;
// Importe la classe Document d'iText pour la création de documents PDF.
import com.itextpdf.text.PageSize;
// Importe la classe PageSize d'iText (pour définir la taille de la page PDF).
import com.itextpdf.text.Paragraph;
// Importe la classe Paragraph d'iText (pour le texte dans le PDF).
import com.itextpdf.text.Element;
// Importe la classe Element d'iText.
import com.itextpdf.text.Font;
// Importe la classe Font d'iText (pour le style de police dans le PDF).
import com.itextpdf.text.BaseColor;
// Importe la classe BaseColor d'iText (pour les couleurs dans le PDF).
import com.itextpdf.text.Phrase;
// Importe la classe Phrase d'iText (pour les fragments de texte).
import com.itextpdf.text.pdf.PdfPCell;
// Importe la classe PdfPCell d'iText (pour les cellules de tableau PDF).
import com.itextpdf.text.pdf.PdfPTable;
// Importe la classe PdfPTable d'iText (pour les tableaux dans le PDF).
import com.itextpdf.text.pdf.PdfWriter;
// Importe la classe PdfWriter d'iText (pour écrire le document PDF).
import gestionelevegason.LoginGestionEleve;
// Importe une classe d'un autre package (probablement la fenêtre de connexion).
import java.io.File;
// Importe la classe File (pour la gestion des fichiers).
import java.io.FileOutputStream;
// Importe la classe FileOutputStream (pour écrire des données dans un fichier).
import java.awt.Desktop;
// Importe la classe Desktop (pour ouvrir des fichiers avec l'application par défaut du système).
import java.awt.Image;
// Importe la classe Image (pour la gestion des images).
import java.text.SimpleDateFormat;
// Importe la classe SimpleDateFormat (pour formater les dates).
import java.util.Date;
// Importe la classe Date (pour obtenir la date et l'heure actuelles).
import javax.swing.ImageIcon;
// Importe la classe ImageIcon (pour les icônes Swing).
import javax.swing.JFileChooser;
// Importe la classe JFileChooser (pour les boîtes de dialogue de sélection de fichiers).
import javax.swing.JFrame;
// Importe la classe JFrame (pour les fenêtres principales).
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
// Importe la classe JOptionPane (importation répétée, redondante mais inoffensive).
import javax.swing.filechooser.FileNameExtensionFilter;
// Importe la classe FileNameExtensionFilter (pour filtrer les types de fichiers dans JFileChooser).
//... vos autres imports

// Déclaration de la classe FraisPanel, qui hérite de javax.swing.JPanel.
public class FraisPanel extends javax.swing.JPanel {
    
    private String roleUtilisateur; // 👈 Stocke le rôle dans le panneau
    
    public FraisPanel(String role) {
        this();
        initComponents();
        this.roleUtilisateur = role;
        configurerFraisPanelSelonRole(); // 👈 Nouvelle méthode de configuration du panneau
        modelHistorique = (DefaultTableModel)tablefrais.getModel();
    }
    
    private void configurerFraisPanelSelonRole() {
    // Si le rôle est nul, on arrête la méthode ou on met un rôle par défaut
    if (this.roleUtilisateur == null) {
        System.out.println("⚠️ Attention : Aucun rôle reçu dans FraisPanel");
        return; 
    }
    // Assurez-vous d'utiliser les noms de variables exacts pour vos boutons
    // ENREGISTRER : enregistrerpayement
    // SUPPRIMER : supprimerPaiement (Hypothèse basée sur votre code transactionnel)

    switch (roleUtilisateur) {
        
        case "Enseignant":
            // 🛑 RÔLE : ENSEIGNANT (Doit consulter, ne doit pas modifier les finances)
            
          
            supprimerPaiement.setVisible(false); 
            imprimerrecu.setVisible(false); 
            pdftb_historique.setVisible(false);
            break;
            
        case "Comptable":
            // 💵 RÔLE : COMPTABLE (Doit enregistrer et supprimer)
            // Laissez les boutons visibles (état par défaut = visible).
            // Si vous avez un bouton spécifique à l'Admin, masquez-le ici.
            supprimerPaiement.setVisible(true);
            recherchefrais.setVisible(true);
            archiverEleves.setVisible(true);
            break;
            
        case "Directeur":
            // RÔLE : DIRECTEUR (Administrateur)
            // Laissez tout visible.
            supprimerPaiement.setVisible(true);
            recherchefrais.setVisible(true);
            archiverEleves.setVisible(true);
            break;
            
        default:
            // Sécurité : masquer les actions en cas de rôle inconnu
            
            supprimerPaiement.setVisible(false);
            break;
    }
}
    // Déclaration d'une variable pour le modèle de données de l'historique de paiement.
    DefaultTableModel modelHistorique;
    
    // Constructeur de la classe FraisPanel.
    public FraisPanel() {
        // Appelle la méthode auto-générée pour initialiser les composants de l'interface graphique.
        initComponents();
        
        // Initialise le modèle 'modelHistorique' en le liant au modèle actuel de la JTable 'tablefrais'.
        modelHistorique = (DefaultTableModel)tablefrais.getModel();
    }
      
    // Déclaration des variables de connexion et de requête SQL (la connexion n'est pas utilisée ici, mais elle est déclarée).
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet Rs = null;
      
    // Déclaration de la méthode privée pour afficher les détails des frais d'un élève spécifique.
    private void afficherFraisEleve(int matricule) {
    
    // Déclaration du modèle de table pour l'historique
    // Réinitialise le nombre de lignes du modèle à zéro, effaçant ainsi le contenu de la table.
    modelHistorique.setRowCount(0);
    

    // Début du bloc try-with-resources: établit une connexion (qui sera fermée automatiquement à la fin du bloc).
    try (Connection con = DatabaseManager.getConnection()) {
        
        // 1. Récupérer les informations de l'élève pour les afficher dans les champs d'aperçu
        // Requête SQL pour obtenir le nom, le post-nom et la classe de l'élève.
        String eleveSql = "SELECT matricule, nom, post_nom, classe FROM tb_eleve WHERE matricule = ?";
        // Prépare la requête pour la recherche de l'élève.
        PreparedStatement psEleve = con.prepareStatement(eleveSql);
        // Définit le premier paramètre (?) par le matricule fourni.
        psEleve.setInt(1, matricule);
        // Exécute la requête et obtient les résultats.
        ResultSet RsEleve = psEleve.executeQuery();

        // Vérifie si un élève correspondant au matricule a été trouvé.
        if (RsEleve.next()) {
            // Affichage des informations de l'élève
            // NOTE : Vous devrez peut-être ajouter des JTextField pour afficher Nom, Post-nom, Classe dans FraisPanel
            // Lignes commentées qui serviraient à afficher les détails de l'élève sur l'interface.
            // nomEleveFrais.setText(RsEleve.getString("nom"));
            // postNomEleveFrais.setText(RsEleve.getString("post_nom"));
        } else {
            // Si aucun élève n'est trouvé, affiche un message d'erreur.
             JOptionPane.showMessageDialog(null, "L'élève avec ce matricule n'existe pas.", "Erreur", JOptionPane.ERROR_MESSAGE);
             // Quitte la méthode.
             return;
        }

        // 2. Récupérer les totaux des frais (Frais Totaux, Montant Payé, Reste à Payer)
        // Requête SQL pour obtenir le récapitulatif des frais de l'élève.
        String fraisSql = "SELECT frais_total, montant_paye, reste_a_payer FROM tb_frais WHERE matricule_eleve = ?";
        // Prépare la requête pour les frais.
        PreparedStatement psFrais = con.prepareStatement(fraisSql);
        // Définit le paramètre par le matricule.
        psFrais.setInt(1, matricule);
        // Exécute la requête et obtient les résultats.
        ResultSet RsFrais = psFrais.executeQuery();
        
        // Vérifie si un enregistrement de frais existe pour cet élève.
        if (RsFrais.next()) {
            // Affiche le montant total des frais, converti en String.
            fraistotaux.setText(String.valueOf(RsFrais.getDouble("frais_total")));
            // Affiche le montant déjà payé.
            montantpaye.setText(String.valueOf(RsFrais.getDouble("montant_paye")));
            // Affiche le montant restant à payer.
            restepaye.setText(String.valueOf(RsFrais.getDouble("reste_a_payer")));
        } else {
             // Si la ligne de frais n'existe pas, on affiche zéro et on prévient l'utilisateur
             // Initialise les champs à "0.00" si le dossier de frais n'est pas trouvé.
             fraistotaux.setText("0.00");
             montantpaye.setText("0.00");
             restepaye.setText("0.00");
             // Affiche un message d'avertissement.
             JOptionPane.showMessageDialog(null, "Aucun dossier de frais initialisé pour cet élève.", "Attention", JOptionPane.WARNING_MESSAGE);
             // Quitte la méthode.
             return; 
        }

        // 3. Remplir la table avec l'historique des versements
        // Requête SQL pour récupérer tous les versements effectués par l'élève.
        String histoSql = "SELECT date_paiement, montant_verse, montant_total_paye FROM tb_historique_paiement WHERE matricule_eleve = ?";
        // Prépare la requête pour l'historique.
        PreparedStatement psHisto = con.prepareStatement(histoSql);
        // Définit le paramètre par le matricule.
        psHisto.setInt(1, matricule);
        // Exécute la requête.
        ResultSet RsHisto = psHisto.executeQuery();
        
        // Boucle pour parcourir chaque ligne du résultat (chaque versement).
        while (RsHisto.next()) {
             // Ajoute une nouvelle ligne au modèle de l'historique avec les données de paiement.
             modelHistorique.addRow(new Object[]{
                 RsHisto.getString("date_paiement"),
                 RsHisto.getDouble("montant_verse"),
                 RsHisto.getDouble("montant_total_paye")
               });
        }
        
    // Capture de l'exception si une erreur SQL se produit.
    } catch (SQLException e) {
        // Affiche la trace complète de l'erreur dans la console.
        e.printStackTrace();
        // Affiche un message d'erreur à l'utilisateur.
        JOptionPane.showMessageDialog(null, "Erreur lors du chargement des données: " + e.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
    }
    
}
// La méthode btnrecherchefraisMouseClicked appelle simplement cette fonction.

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Rechercher = new javax.swing.JPanel();
        recherchefrais = new javax.swing.JTextField();
        btnrechercherfrais = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablefrais = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        lblfraistotaux = new javax.swing.JLabel();
        lblfraistotaux1 = new javax.swing.JLabel();
        lblfraistotaux2 = new javax.swing.JLabel();
        montantpaye = new javax.swing.JLabel();
        restepaye = new javax.swing.JLabel();
        fraistotaux = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        imprimerrecu = new javax.swing.JButton();
        enregistrerpayement = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblfraistotaux3 = new javax.swing.JLabel();
        montantpayement = new javax.swing.JTextField();
        lblfraistotaux4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblfraistotaux5 = new javax.swing.JLabel();
        txtMatricule = new javax.swing.JLabel();
        lblfraistotaux7 = new javax.swing.JLabel();
        lblfraistotaux9 = new javax.swing.JLabel();
        txtNom = new javax.swing.JLabel();
        txtPostNom = new javax.swing.JLabel();
        lblfraistotaux8 = new javax.swing.JLabel();
        txtClasse = new javax.swing.JLabel();
        datepayement = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        pdftb_historique = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        comboAnnee = new javax.swing.JComboBox();
        lblfraistotaux10 = new javax.swing.JLabel();
        archiverEleves = new javax.swing.JButton();
        archiverEleves1 = new javax.swing.JButton();
        supprimerPaiement = new javax.swing.JButton();

        recherchefrais.setFont(new java.awt.Font("Cambria", 1, 14)); // NOI18N
        recherchefrais.setForeground(new java.awt.Color(0, 51, 204));
        recherchefrais.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        btnrechercherfrais.setBackground(new java.awt.Color(52, 152, 219));
        btnrechercherfrais.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        btnrechercherfrais.setText("Rechercher");
        btnrechercherfrais.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnrechercherfraisMouseClicked(evt);
            }
        });

        tablefrais.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date du paiement", "Montant versé"
            }
        ));
        jScrollPane1.setViewportView(tablefrais);
        if (tablefrais.getColumnModel().getColumnCount() > 0) {
            tablefrais.getColumnModel().getColumn(0).setMinWidth(150);
            tablefrais.getColumnModel().getColumn(0).setMaxWidth(150);
        }

        jPanel1.setBackground(new java.awt.Color(102, 0, 102));

        lblfraistotaux.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        lblfraistotaux.setForeground(new java.awt.Color(255, 255, 255));
        lblfraistotaux.setText("Frais totaux");

        lblfraistotaux1.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        lblfraistotaux1.setForeground(new java.awt.Color(255, 255, 255));
        lblfraistotaux1.setText("Montant payé");

        lblfraistotaux2.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        lblfraistotaux2.setForeground(new java.awt.Color(255, 255, 255));
        lblfraistotaux2.setText("Reste à payer");

        montantpaye.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        montantpaye.setForeground(new java.awt.Color(255, 255, 51));

        restepaye.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        restepaye.setForeground(new java.awt.Color(255, 0, 0));

        fraistotaux.setFont(new java.awt.Font("Cambria", 1, 24)); // NOI18N
        fraistotaux.setForeground(new java.awt.Color(51, 255, 51));
        fraistotaux.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblfraistotaux1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblfraistotaux2, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(restepaye, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(montantpaye, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblfraistotaux, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(fraistotaux, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblfraistotaux, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(fraistotaux, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblfraistotaux1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(montantpaye, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblfraistotaux2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(restepaye, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        imprimerrecu.setBackground(new java.awt.Color(128, 128, 128));
        imprimerrecu.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        imprimerrecu.setText("Imprimer reçu");
        imprimerrecu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                imprimerrecuMouseClicked(evt);
            }
        });

        enregistrerpayement.setBackground(new java.awt.Color(46, 204, 113));
        enregistrerpayement.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        enregistrerpayement.setText("Enregistrer le paiement");
        enregistrerpayement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                enregistrerpayementMouseClicked(evt);
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon(new ImageIcon(getClass().getResource("/gestionfrais/printer(1).png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

        jLabel3.setIcon(new javax.swing.ImageIcon(new ImageIcon(getClass().getResource("/gestionfrais/diskette.png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(enregistrerpayement)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(imprimerrecu))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enregistrerpayement)
                    .addComponent(imprimerrecu))
                .addContainerGap())
        );

        lblfraistotaux3.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        lblfraistotaux3.setText("Montant du paiement");

        montantpayement.setFont(new java.awt.Font("Cambria", 1, 24)); // NOI18N
        montantpayement.setForeground(new java.awt.Color(51, 255, 51));
        montantpayement.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        lblfraistotaux4.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        lblfraistotaux4.setText("Date de paiement");

        jPanel3.setBackground(new java.awt.Color(255, 204, 204));

        lblfraistotaux5.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        lblfraistotaux5.setText("Matricule");

        txtMatricule.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        txtMatricule.setForeground(new java.awt.Color(0, 0, 204));

        lblfraistotaux7.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        lblfraistotaux7.setText("Post - nom");

        lblfraistotaux9.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        lblfraistotaux9.setText("Nom");

        txtNom.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        txtNom.setForeground(new java.awt.Color(0, 51, 204));

        txtPostNom.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        txtPostNom.setForeground(new java.awt.Color(0, 51, 204));

        lblfraistotaux8.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        lblfraistotaux8.setText("Classe");

        txtClasse.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        txtClasse.setForeground(new java.awt.Color(0, 51, 204));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblfraistotaux7, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblfraistotaux9, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNom, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPostNom, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lblfraistotaux5, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtMatricule, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lblfraistotaux8, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtClasse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblfraistotaux5)
                    .addComponent(txtMatricule, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblfraistotaux9, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(txtNom, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(txtPostNom, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblfraistotaux7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblfraistotaux8, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClasse, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        datepayement.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        jLabel2.setIcon(new javax.swing.ImageIcon(new ImageIcon(getClass().getResource("/gestionfrais/seach(1).png")).getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH)));

        jLabel5.setFont(new java.awt.Font("Times New Roman", 3, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 51, 255));
        jLabel5.setText("Enregistrement Frais");

        pdftb_historique.setBackground(new java.awt.Color(231, 76, 60));
        pdftb_historique.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        pdftb_historique.setText("PDF");
        pdftb_historique.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pdftb_historiqueMouseClicked(evt);
            }
        });

        jLabel6.setIcon(new javax.swing.ImageIcon(new ImageIcon(getClass().getResource("/gestionfrais/pdf.png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

        comboAnnee.setFont(new java.awt.Font("Cambria", 1, 14)); // NOI18N
        comboAnnee.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2025-2026", "2026-2027", "2027-2028", "2028-2029", "2029-2030", "2030-2031", "2031-2032", "2032-2033", "2033-2034", "2034-2035", "2035-2036", "2036-2037", "2037-2038", "2038-2039", "2039-2040" }));

        lblfraistotaux10.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        lblfraistotaux10.setText("Année scolaire");

        archiverEleves.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        archiverEleves.setText("Archiver");
        archiverEleves.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                archiverElevesActionPerformed(evt);
            }
        });

        archiverEleves1.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        archiverEleves1.setText("Voir archive");
        archiverEleves1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                archiverEleves1ActionPerformed(evt);
            }
        });

        supprimerPaiement.setText("Supprimer");
        supprimerPaiement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supprimerPaiementActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout RechercherLayout = new javax.swing.GroupLayout(Rechercher);
        Rechercher.setLayout(RechercherLayout);
        RechercherLayout.setHorizontalGroup(
            RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RechercherLayout.createSequentialGroup()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(RechercherLayout.createSequentialGroup()
                .addGroup(RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(RechercherLayout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(RechercherLayout.createSequentialGroup()
                                .addGroup(RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblfraistotaux3)
                                    .addComponent(lblfraistotaux4))
                                .addGap(18, 18, 18)
                                .addGroup(RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(datepayement, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(montantpayement, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(RechercherLayout.createSequentialGroup()
                                .addGroup(RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(recherchefrais)
                                    .addComponent(comboAnnee, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(RechercherLayout.createSequentialGroup()
                                        .addComponent(btnrechercherfrais)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(lblfraistotaux10, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(RechercherLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(supprimerPaiement)
                        .addGap(31, 31, 31)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pdftb_historique, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(archiverEleves1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(archiverEleves)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        RechercherLayout.setVerticalGroup(
            RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RechercherLayout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(RechercherLayout.createSequentialGroup()
                        .addGroup(RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(recherchefrais, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnrechercherfrais)))
                        .addGap(28, 28, 28)
                        .addGroup(RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboAnnee, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblfraistotaux10, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(RechercherLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblfraistotaux3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(montantpayement, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblfraistotaux4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(datepayement, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RechercherLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGroup(RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(RechercherLayout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pdftb_historique)
                            .addComponent(supprimerPaiement)))))
            .addGroup(RechercherLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(RechercherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(archiverEleves1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(archiverEleves, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Rechercher, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Rechercher, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnrechercherfraisMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnrechercherfraisMouseClicked
        // TODO add your handling code here:
        // Lignes vides de mise en forme (aucune fonction, ignorées pour le commentaire).
        // Lignes vides de mise en forme (aucune fonction, ignorées pour le commentaire).
        String recherche = recherchefrais.getText().trim();
        // Déclare une variable 'recherche' et stocke le texte du champ 'recherchefrais' après avoir enlevé les espaces blancs.
        String anneeScolaire = (String) comboAnnee.getSelectedItem(); 
        // Déclare 'anneeScolaire' et stocke l'élément sélectionné dans la JComboBox 'comboAnnee'.

        if (recherche.isEmpty()) {
        // Vérifie si le champ de recherche est vide.
            JOptionPane.showMessageDialog(null, "Veuillez entrer le matricule ou le nom à rechercher.", "Champ Manquant", JOptionPane.WARNING_MESSAGE);
            // Affiche un avertissement si le champ est vide.
            return;
            // Quitte la méthode.
        }

        if (anneeScolaire == null || anneeScolaire.isEmpty()) {
        // Vérifie si aucune année scolaire n'a été sélectionnée ou si elle est vide.
            JOptionPane.showMessageDialog(null, "Veuillez sélectionner l'année scolaire.", "Année Manquante", JOptionPane.WARNING_MESSAGE);
            // Affiche un avertissement si l'année est manquante.
            return;
            // Quitte la méthode.
        }

        // Nettoyage initial de l'UI
        // Efface le contenu des champs d'information de l'élève (Matricule, Nom, PostNom, Classe).
        txtMatricule.setText(""); txtNom.setText(""); txtPostNom.setText(""); txtClasse.setText("");
        // Réinitialise les champs de totaux des frais à "0".
        fraistotaux.setText("0"); montantpaye.setText("0"); restepaye.setText("0");
        // Efface toutes les lignes du modèle d'historique de paiement.
        modelHistorique.setRowCount(0);

        // Début du bloc try-with-resources: établit la connexion à la base de données (fermée automatiquement).
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/frais_scolaire", "root", "")) {

            // 1. Rechercher l’élève (dans la table des élèves)
            // Requête SQL pour trouver un élève soit par son matricule, soit par son nom.
            String sqlEleve = "SELECT * FROM tb_eleve WHERE matricule = ? OR nom = ?";
            // Prépare la requête.
            PreparedStatement pstEleve = con.prepareStatement(sqlEleve);
            // Définit le premier paramètre (?) par la chaîne de recherche (pour le matricule).
            pstEleve.setString(1, recherche);
            // Définit le deuxième paramètre (?) par la chaîne de recherche (pour le nom).
            pstEleve.setString(2, recherche);
            // Exécute la requête de recherche de l'élève.
            ResultSet rsEleve = pstEleve.executeQuery();

            // Vérifie si un résultat (un élève) a été trouvé.
            if (rsEleve.next()) {
                String matricule = rsEleve.getString("matricule");
                // Récupère le matricule de l'élève trouvé.

                // Affichage des infos de l'élève
                // Remplit le champ txtMatricule avec le matricule de l'élève.
                txtMatricule.setText(matricule);
                // Remplit le champ txtNom avec le nom de l'élève.
                txtNom.setText(rsEleve.getString("nom"));
                // Remplit le champ txtPostNom avec le post-nom de l'élève.
                txtPostNom.setText(rsEleve.getString("post_nom"));
                // Remplit le champ txtClasse avec la classe de l'élève.
                txtClasse.setText(rsEleve.getString("classe"));

                // 2. Rechercher les frais POUR CETTE ANNÉE (dans tb_frais)
                // Requête SQL pour obtenir le récapitulatif des frais pour l'élève ET l'année spécifiés.
                String sqlFrais = "SELECT frais_total, montant_paye, reste_a_payer FROM tb_frais WHERE matricule_eleve = ? AND annee_scolaire = ?"; 
                // Prépare la requête de recherche des frais.
                PreparedStatement pstFrais = con.prepareStatement(sqlFrais);
                // Définit le matricule de l'élève.
                pstFrais.setString(1, matricule);
                // Définit l'année scolaire sélectionnée.
                pstFrais.setString(2, anneeScolaire); 
                // Exécute la requête de recherche des frais.
                ResultSet rsFrais = pstFrais.executeQuery();

                // Vérifie si un enregistrement de frais a été trouvé pour l'année.
                if (rsFrais.next()) {
                    // Frais trouvés pour l'année
                    // Affiche le frais total formaté avec deux décimales.
                    fraistotaux.setText(String.format("%.2f", rsFrais.getDouble("frais_total")));
                    // Affiche le montant payé formaté.
                    montantpaye.setText(String.format("%.2f", rsFrais.getDouble("montant_paye")));
                    // Affiche le reste à payer formaté.
                    restepaye.setText(String.format("%.2f", rsFrais.getDouble("reste_a_payer")));
                } else {
                    // Aucun frais trouvé pour CETTE ANNÉE, les champs restent à "0"
                    // Affiche un avertissement si le dossier de frais n'existe pas pour l'année sélectionnée.
                    JOptionPane.showMessageDialog(null, "Aucun dossier de frais pour l'année " + anneeScolaire + ". Assurez-vous d'entrer les frais totaux avant le premier paiement.", "Attention", JOptionPane.WARNING_MESSAGE);
                }

                // 3. Remplir l'historique POUR CETTE ANNÉE
                // Requête SQL pour obtenir l'historique de paiement pour l'élève ET l'année, trié par date.
                String sqlHist = "SELECT date_paiement, montant_verse FROM tb_historique_paiement WHERE matricule_eleve = ? AND annee_scolaire = ? ORDER BY date_paiement ASC";
                // Prépare la requête pour l'historique.
                PreparedStatement pstHist = con.prepareStatement(sqlHist);
                // Définit le matricule de l'élève.
                pstHist.setString(1, matricule);
                // Définit l'année scolaire.
                pstHist.setString(2, anneeScolaire); 
                // Exécute la requête d'historique.
                ResultSet rsHist = pstHist.executeQuery();

                // Boucle pour parcourir chaque versement trouvé.
                while (rsHist.next()) {
                    String date = rsHist.getString("date_paiement");
                    // Récupère la date de paiement.
                    double montant = rsHist.getDouble("montant_verse");
                    // Récupère le montant versé.
                    // Ajoute une nouvelle ligne à la table d'historique avec la date et le montant formaté.
                    modelHistorique.addRow(new Object[]{date, String.format("%.2f", montant)}); 
                }

            } else {
                // Si aucun élève n'a été trouvé après la recherche par matricule/nom.
                JOptionPane.showMessageDialog(null, "Élève non trouvé");
                // Affiche un message d'erreur.
            }

        // Capture toute exception (SQLException ou autre) survenue durant l'opération.
        } catch(Exception e) {
            // Affiche la trace de l'erreur dans la console pour le débogage.
            e.printStackTrace();
            // Affiche un message d'erreur général à l'utilisateur.
            JOptionPane.showMessageDialog(null, "Erreur lors de la recherche: " + e.getMessage());
        }

    }//GEN-LAST:event_btnrechercherfraisMouseClicked

    private void enregistrerpayementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_enregistrerpayementMouseClicked
        // TODO add your handling code here:
    
                                                  
        String matricule = txtMatricule.getText().trim();
        // Récupère le texte du champ matricule, supprime les espaces, et le stocke.
        String montantVerseStr = montantpayement.getText().trim();
        // Récupère le texte du champ du montant à verser, le nettoie.
        String fraisTotalStr = fraistotaux.getText().trim();
        // Récupère le texte des frais totaux, le nettoie.
        String anneeScolaire = (String) comboAnnee.getSelectedItem();
        // Récupère l'élément sélectionné dans la liste déroulante de l'année scolaire.
        Date datePaiementObj = datepayement.getDate();
        // Récupère l'objet Date sélectionné dans le composant de date.
        String dateStr = "";
        // Déclare la chaîne pour la date formatée, initialisée à vide.
        double montantVerse = 0.0;
        // Déclare et initialise le montant versé en double.
        double fraisTotal = 0.0;
        // Déclare et initialise les frais totaux en double.
        double nouveauPaye = 0.0;
        // Déclare et initialise le nouveau montant total payé.
        double reste = 0.0;
        // Déclare et initialise le montant restant à payer.
        Date aujourdhui = new Date();
        SimpleDateFormat sidf = new SimpleDateFormat("yyyyMMdd");
        
        if(datePaiementObj.before(aujourdhui)&& !sidf.format(datePaiementObj).equals(sidf.format(aujourdhui))){
            JOptionPane.showMessageDialog(this, "La date est inférieure à aujourd'hui.", "Erreur",JOptionPane.WARNING_MESSAGE);
            // Affiche un avertissement si les champs sont incomplets.
            return;
        }

        // --- 1. Validation et Préparation ---
        if (matricule.isEmpty() || montantVerseStr.isEmpty() || fraisTotalStr.isEmpty() || txtMatricule.getText().equals("")) {
        // Vérifie si des champs critiques (matricule, montant versé, frais totaux) sont vides.
            JOptionPane.showMessageDialog(null, "Veuillez d'abord rechercher un élève, sélectionner l'année, et entrer le montant.", "Champs manquants", JOptionPane.WARNING_MESSAGE);
            // Affiche un avertissement si les champs sont incomplets.
            return;
            // Quitte la méthode.
        }

        if (datePaiementObj == null) {
        // Vérifie si la date de paiement a été sélectionnée.
            JOptionPane.showMessageDialog(null, "Veuillez sélectionner la Date de paiement.", "Champ Date manquant", JOptionPane.WARNING_MESSAGE);
            // Affiche un avertissement si la date est manquante.
            return;
            // Quitte la méthode.
        }

        try {
            // Début du bloc try pour le formatage de la date et la conversion des montants.
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            // Crée un formateur de date au format jour/mois/année.
            dateStr = sdf.format(datePaiementObj);
            // Formate l'objet Date en chaîne de caractères.

            // --- CORRECTION CRUCIALE : GESTION DES SÉPARATEURS DÉCIMAUX (VIRGULE/POINT) ---
            // Remplacer la virgule par le point pour assurer la compatibilité avec Double.parseDouble()
            // Remplace les virgules par des points dans la chaîne du montant versé.
            String sanitizedMontantVerseStr = montantVerseStr.replace(',', '.');
            // Remplace les virgules par des points dans la chaîne des frais totaux.
            String sanitizedFraisTotalStr = fraisTotalStr.replace(',', '.');

            // Convertit les chaînes nettoyées en valeurs doubles.
            montantVerse = Double.parseDouble(sanitizedMontantVerseStr);
            // Convertit les frais totaux en double.
            fraisTotal = Double.parseDouble(sanitizedFraisTotalStr);

            if (montantVerse <= 0) {
            // Vérifie si le montant versé est positif.
                JOptionPane.showMessageDialog(null, "Le montant versé doit être supérieur à zéro.", "Erreur de montant", JOptionPane.ERROR_MESSAGE);
                // Affiche une erreur si le montant est nul ou négatif.
                return;
                // Quitte la méthode.
            }
        } catch (Exception e) {
            // Cette erreur est maintenant ciblée sur un format non numérique réel
            // Capture les exceptions liées au format (par exemple, si le montant n'est pas un nombre).
            JOptionPane.showMessageDialog(null, "Le montant ou la date est invalide. Erreur: " + e.getMessage(), "Erreur de format", JOptionPane.ERROR_MESSAGE);
            // Affiche une erreur de format.
            return;
            // Quitte la méthode.
        }

        // Si les frais totaux sont à zéro et que l'élève n'a pas encore de ligne dans tb_frais (cas initial)
        if (fraisTotal == 0.0) {
        // Vérifie si les frais totaux affichés sont zéro (ce qui empêche tout paiement).
            JOptionPane.showMessageDialog(null, "Veuillez définir le montant des Frais Totaux (Frais Totaux > 0) dans le champ avant le premier enregistrement pour cette année.", "Frais Totaux Manquants", JOptionPane.ERROR_MESSAGE);
            // Affiche une erreur demandant la définition des frais totaux.
            return;
            // Quitte la méthode.
        }

        Connection con = null;
        // Déclare la variable de connexion, initialisée à null.
        try {
            // Début du bloc try pour les opérations de base de données.
            con = DriverManager.getConnection("jdbc:mysql://localhost/frais_scolaire", "root", "");
            // Établit la connexion à la base de données.
            con.setAutoCommit(false); // DÉBUT DE LA TRANSACTION
            // Désactive l'auto-commit pour pouvoir grouper les mises à jour (transaction).

            // --- 2. Vérifie le solde existant pour l'élève ET l'année ---
            // Requête pour vérifier s'il existe déjà un enregistrement de frais pour cet élève et cette année.
            String checkSql = "SELECT montant_paye FROM tb_frais WHERE matricule_eleve = ? AND annee_scolaire = ?";
            PreparedStatement pstCheck = con.prepareStatement(checkSql);
            // Prépare la requête de vérification.
            pstCheck.setString(1, matricule);
            // Paramètre 1: le matricule.
            pstCheck.setString(2, anneeScolaire);
            // Paramètre 2: l'année scolaire.
            ResultSet rs = pstCheck.executeQuery();
            // Exécute la vérification.

            if (rs.next()) {
                // Ligne existante pour l'année → UPDATE
                // S'il y a un enregistrement existant.
                double ancienPaye = rs.getDouble("montant_paye");
                // Récupère le montant déjà payé.
                nouveauPaye = ancienPaye + montantVerse;
                // Calcule le nouveau montant total payé.

                // Validation de dépassement
                if (nouveauPaye > fraisTotal) {
                // Vérifie si le nouveau paiement dépasse le total des frais.
                    JOptionPane.showMessageDialog(null, "Le paiement de " + montantVerseStr + " dépasse le reste à payer de cette année.", "Montant Excédentaire", JOptionPane.WARNING_MESSAGE);
                    // Affiche un avertissement de dépassement.
                    con.rollback();
                    // Annule toutes les modifications de la transaction.
                    return;
                    // Quitte la méthode.
                }
                reste = fraisTotal - nouveauPaye;
                // Calcule le nouveau reste à payer.

                // UPDATE des frais pour CETTE ANNEE
                // Requête pour mettre à jour les colonnes 'montant_paye' et 'reste_a_payer'.
                String updateSql = "UPDATE tb_frais SET montant_paye = ?, reste_a_payer = ? WHERE matricule_eleve = ? AND annee_scolaire = ?";
                PreparedStatement pstUpdate = con.prepareStatement(updateSql);
                // Prépare la requête de mise à jour.
                pstUpdate.setDouble(1, nouveauPaye);
                // Paramètre 1: le nouveau montant payé.
                pstUpdate.setDouble(2, reste);
                // Paramètre 2: le nouveau reste à payer.
                pstUpdate.setString(3, matricule);
                // Paramètre 3: le matricule.
                pstUpdate.setString(4, anneeScolaire);
                // Paramètre 4: l'année scolaire.
                pstUpdate.executeUpdate();
                // Exécute la mise à jour.

            } else {
                // Pas de ligne de frais pour cette année → INSERT (Premier paiement de l'année)
                // S'il n'y a pas d'enregistrement existant (premier paiement de l'année).
                nouveauPaye = montantVerse;
                // Le nouveau montant payé est égal au montant versé.
                if (nouveauPaye > fraisTotal) {
                // Vérifie si ce premier versement dépasse les frais totaux.
                    JOptionPane.showMessageDialog(null, "Le montant versé dépasse les frais totaux.", "Montant Excédentaire", JOptionPane.WARNING_MESSAGE);
                    // Affiche un avertissement de dépassement.
                    con.rollback();
                    // Annule toutes les modifications.
                    return;
                    // Quitte la méthode.
                }
                reste = fraisTotal - nouveauPaye;
                // Calcule le reste à payer.

                // INSERT avec la colonne annee_scolaire
                // Requête d'insertion pour créer le premier enregistrement de frais pour l'année.
                String insertSql = "INSERT INTO tb_frais (matricule_eleve, frais_total, montant_paye, reste_a_payer, annee_scolaire) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstInsert = con.prepareStatement(insertSql);
                // Prépare la requête d'insertion.
                pstInsert.setString(1, matricule);
                // Paramètre 1: le matricule.
                pstInsert.setDouble(2, fraisTotal);
                // Paramètre 2: les frais totaux.
                pstInsert.setDouble(3, nouveauPaye);
                // Paramètre 3: le nouveau montant payé.
                pstInsert.setDouble(4, reste);
                // Paramètre 4: le reste à payer.
                pstInsert.setString(5, anneeScolaire);
                // Paramètre 5: l'année scolaire.
                pstInsert.executeUpdate();
                // Exécute l'insertion.
            }

            // --- 3. Insérer dans tb_historique_paiement (INSERT AVEC ANNEE) ---
            // Requête d'insertion pour enregistrer ce versement dans la table d'historique.
            String insertHist = "INSERT INTO tb_historique_paiement (matricule_eleve, montant_verse, date_paiement, montant_total_paye, annee_scolaire) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstHist = con.prepareStatement(insertHist);
            // Prépare la requête d'insertion dans l'historique.
            pstHist.setString(1, matricule);
            // Paramètre 1: le matricule.
            pstHist.setDouble(2, montantVerse);
            // Paramètre 2: le montant du versement.
            pstHist.setString(3, dateStr);
            // Paramètre 3: la date du paiement.
            pstHist.setDouble(4, nouveauPaye);
            // Paramètre 4: le montant total payé à ce jour.
            pstHist.setString(5, anneeScolaire);
            // Paramètre 5: l'année scolaire.
            pstHist.executeUpdate();
            // Exécute l'insertion dans l'historique.

            con.commit(); // VALIDATION DE LA TRANSACTION
            // Valide toutes les modifications (UPDATE/INSERT) effectuées dans ce bloc.

            // --- 4. Mise à jour de l'interface utilisateur ---
            JOptionPane.showMessageDialog(null, "Paiement enregistré avec succès. Reste à payer : " + String.format("%.2f", reste), "Succès", JOptionPane.INFORMATION_MESSAGE);
            // Affiche un message de succès à l'utilisateur.

            // Mise à jour des champs d'affichage (Note: String.format utilise la locale par défaut)
            montantpaye.setText(String.format("%.2f", nouveauPaye));
            // Met à jour le champ du montant total payé.
            restepaye.setText(String.format("%.2f", reste));
            // Met à jour le champ du reste à payer.

            // Réinitialisation des champs de saisie
            montantpayement.setText("");
            // Efface le champ de saisie du montant versé.
            datepayement.setDate(null);
            // Efface la date sélectionnée.

            // Mise à jour de la table d'historique (simulée)
            if (modelHistorique != null) {
            // Vérifie si le modèle d'historique est initialisé.
                modelHistorique.addRow(new Object[]{dateStr, String.format("%.2f", montantVerse)});
                // Ajoute la nouvelle ligne de paiement à la table d'historique visible.
            }

        } catch (SQLException ex) {
            // Capture les erreurs spécifiques à la base de données.
            try {
                if (con != null) con.rollback();
                // Tente d'annuler les modifications si la connexion existe.
            } catch (SQLException rbex) {
                rbex.printStackTrace();
                // Affiche la trace de l'erreur d'annulation (rollback).
            }
            ex.printStackTrace();
            // Affiche la trace de l'erreur SQL principale.
            JOptionPane.showMessageDialog(null, "Erreur SQL lors de l'enregistrement: " + ex.getMessage(), "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
            // Affiche l'erreur à l'utilisateur.
        } catch (Exception ex) {
            // Capture toute autre exception inattendue.
            ex.printStackTrace();
            // Affiche la trace de l'erreur.
            JOptionPane.showMessageDialog(null, "Une erreur inattendue est survenue: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            // Affiche l'erreur à l'utilisateur.
        } finally {
            // Bloc exécuté quoi qu'il arrive (après try ou catch).
            try {
                if (con != null) con.setAutoCommit(true);
                // Rétablit le mode auto-commit sur true.
                if (con != null) con.close();
                // Ferme la connexion à la base de données.
            } catch (SQLException fex) {
                fex.printStackTrace();
                // Affiche la trace si la fermeture de la connexion échoue.
            }
        }
    }//GEN-LAST:event_enregistrerpayementMouseClicked

    private void imprimerrecuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imprimerrecuMouseClicked
        // TODO add your handling code here:
       
                                                
    
    // 1. Vérification de la sélection et des données de l'élève
    int selectedRow = tablefrais.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Veuillez sélectionner un versement dans le tableau d'historique ci-dessous.", "Sélection requise", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String matricule = txtMatricule.getText().trim();
    String nom = txtNom.getText().trim();
    String postNom = txtPostNom.getText().trim();
    String classe = txtClasse.getText().trim();
    
    // NOUVEAU : Récupération de l'Année Scolaire
    String anneeScolaire = (String) comboAnnee.getSelectedItem(); 
    
    // Vérification rapide que l'élève est chargé et que l'année est sélectionnée
    if (matricule.isEmpty() || nom.isEmpty() || anneeScolaire == null || anneeScolaire.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Veuillez d'abord rechercher et charger les données d'un élève ET sélectionner l'Année Scolaire.", "Erreur", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // 2. Récupérer les données du versement sélectionné depuis le JTable
    // Colonnes : (0: Date du paiement, 1: Montant versé)
    String datePaiement = tablefrais.getValueAt(selectedRow, 0).toString();
    String montantVerseStr = tablefrais.getValueAt(selectedRow, 1).toString();
    
    // Récupérer le solde restant (affiché dans le JLabel 'restepaye')
    String resteAPayerStr = restepaye.getText().trim(); 

    // 3. Génération et Impression du Reçu PDF
    try {
        // Préparation du chemin de sauvegarde temporaire
        String currentTimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        // NOUVEAU : Le nom du fichier inclut l'année
        String fileName = "Recu_" + matricule + "_" + anneeScolaire.replace("/", "-") + "_" + currentTimestamp + ".pdf";
        File tempFile = new File(System.getProperty("java.io.tmpdir"), fileName);
        
        // Initialisation du document iText (format A5 pour un reçu)
        Document document = new Document(PageSize.A5);
        PdfWriter.getInstance(document, new FileOutputStream(tempFile));
        document.open();

        // Définition des polices
        Font fontTitre = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLUE);
        Font fontLabel = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font fontData = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        Font fontMontant = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(0, 153, 0)); // Vert foncé

        // --- EN-TÊTE DU REÇU ---
        Paragraph titre = new Paragraph("REÇU DE PAIEMENT DE FRAIS SCOLAIRES", fontTitre);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingAfter(10);
        document.add(titre);
        
        // NOUVEAU : Ajout de l'Année Scolaire
        document.add(new Paragraph("Année Scolaire : " + anneeScolaire, fontLabel));
        document.add(new Paragraph("Date d'impression : " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), fontData));
        document.add(new Paragraph("----------------------------------"));

        // --- INFORMATIONS DE L'ÉLÈVE ---
        document.add(new Paragraph("Matricule : " + matricule, fontLabel));
        document.add(new Paragraph("Nom Complet : " + nom + " " + postNom, fontLabel));
        document.add(new Paragraph("Classe : " + classe, fontLabel));
        document.add(new Paragraph("\n"));

        // --- DÉTAILS DU PAIEMENT SPÉCIFIQUE ---
        document.add(new Paragraph("Détails du Versement :", fontLabel));
        document.add(new Paragraph("Date du Versement : " + datePaiement, fontData));
        
        Paragraph pMontantVerse = new Paragraph("Montant Versé : " + montantVerseStr + " USD", fontMontant);
        pMontantVerse.setSpacingBefore(5);
        document.add(pMontantVerse);
        document.add(new Paragraph("\n"));

        // --- SOLDE GLOBAL POUR CETTE ANNÉE ---
        document.add(new Paragraph("Solde Actuel (pour " + anneeScolaire + ") :", fontLabel));
        document.add(new Paragraph("Reste à Payer : " + resteAPayerStr + " USD", fontMontant));

        document.add(new Paragraph("\n"));
        
        // --- PIED DE PAGE ---
        Paragraph pNote = new Paragraph("Ceci est un reçu informatisé. Conservez-le comme preuve de paiement.", new Font(Font.FontFamily.HELVETICA, 8));
        pNote.setAlignment(Element.ALIGN_CENTER);
        document.add(pNote);
        
        Paragraph pSignature = new Paragraph("\nSignature du Caissier :\n____________________");
        pSignature.setAlignment(Element.ALIGN_RIGHT); 
        document.add(pSignature);

        document.close();
        
        // 4. Lancement de l'impression
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.OPEN)) {
                // Ouvrir le PDF (ceci lance l'aperçu ou l'impression selon la configuration système)
                desktop.open(tempFile);
            } else {
                JOptionPane.showMessageDialog(this, "Impossible d'ouvrir le fichier PDF. Le reçu est à:\n" + tempFile.getAbsolutePath(), "Attention", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "La fonction Desktop n'est pas supportée. Le reçu est à:\n" + tempFile.getAbsolutePath(), "Attention", JOptionPane.WARNING_MESSAGE);
        }
        
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erreur lors de la génération/lancement du reçu PDF : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    }//GEN-LAST:event_imprimerrecuMouseClicked

    private void pdftb_historiqueMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pdftb_historiqueMouseClicked
        // TODO add your handling code here:
                                                
                                           
    
    // Récupération des informations affichées
    String matricule = txtMatricule.getText().trim();
    String nom = txtNom.getText().trim();
    String postNom = txtPostNom.getText().trim();
    String classe = txtClasse.getText().trim();
    String fraisTotal = fraistotaux.getText().trim();
    String montantPaye = montantpaye.getText().trim();
    String resteAPayer = restepaye.getText().trim();
    String anneeScolaire = (String) comboAnnee.getSelectedItem();

    // Validation des données minimales
    if (matricule.isEmpty() || nom.isEmpty() || anneeScolaire == null || anneeScolaire.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Veuillez d'abord rechercher et charger les données complètes de l'élève (y compris l'Année Scolaire).", "Erreur", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // Récupération du modèle de la JTable
    DefaultTableModel model = (DefaultTableModel) tablefrais.getModel();
    if (model.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Aucun historique de paiement pour l'année " + anneeScolaire + " à exporter/imprimer.", "Attention", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Dialogue pour choisir l'emplacement d'enregistrement
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Enregistrer le Relevé de Frais");
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Documents PDF (*.pdf)", "pdf");
    fileChooser.addChoosableFileFilter(filter);
    
    // Nom de fichier suggéré (avec matricule et année)
    String defaultFileName = "Releve_Frais_" + matricule + "_" + anneeScolaire.replace("/", "-") + ".pdf"; 
    fileChooser.setSelectedFile(new File(defaultFileName));

    int userSelection = fileChooser.showSaveDialog(this); 

    if (userSelection == JFileChooser.APPROVE_OPTION) {

        File outputFile = fileChooser.getSelectedFile();
        String path = outputFile.getAbsolutePath();
        if (!path.toLowerCase().endsWith(".pdf")) {
            outputFile = new File(path + ".pdf");
        }

        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.open();

            // Définition des polices
            Font fontTitre = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLUE);
            Font fontEntete = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
            Font fontNormal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

            // 1. Titre
            Paragraph titre = new Paragraph("RELEVÉ DE PAIEMENT DES FRAIS SCOLAIRES", fontTitre);
            titre.setAlignment(Element.ALIGN_CENTER);
            document.add(titre);
            document.add(new Paragraph("Année Scolaire : " + anneeScolaire, fontEntete));
            document.add(new Paragraph("Date d'édition : " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), fontNormal));
            document.add(new Paragraph("\n"));

            // 2. Informations Élève et Totaux
            PdfPTable infoTable = new PdfPTable(4); // 4 colonnes pour les infos et les totaux
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{25, 25, 25, 25});
            
            // Ligne 1 : Infos Élève
            infoTable.addCell(new Phrase("Matricule:", fontEntete));
            infoTable.addCell(new Phrase(matricule, fontNormal));
            infoTable.addCell(new Phrase("Nom & Post-nom:", fontEntete));
            infoTable.addCell(new Phrase(nom + " " + postNom, fontNormal));
            
            // Ligne 2 : Classe et Frais Totaux
            infoTable.addCell(new Phrase("Classe:", fontEntete));
            infoTable.addCell(new Phrase(classe, fontNormal));
            infoTable.addCell(new Phrase("Frais Totaux:", fontEntete));
            infoTable.addCell(new Phrase(fraisTotal, fontNormal));
            
            // Ligne 3 : Montant Payé et Reste à Payer
            infoTable.addCell(new Phrase("Montant Payé:", fontEntete));
            infoTable.addCell(new Phrase(montantPaye, fontNormal));
            infoTable.addCell(new Phrase("Reste à Payer:", fontEntete));
            infoTable.addCell(new Phrase(resteAPayer, fontNormal));
            
            document.add(infoTable);
            document.add(new Paragraph("\n\n"));


            // 3. Tableau d'Historique des Versements
            document.add(new Paragraph("Historique des Versements pour l'Année " + anneeScolaire + ":", fontEntete));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(60);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.setWidths(new float[]{50, 50});

            // Entêtes de tableau
            PdfPCell header1 = new PdfPCell(new Phrase("Date du paiement", fontEntete));
            header1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header1.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            PdfPCell header2 = new PdfPCell(new Phrase("Montant versé", fontEntete));
            header2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header2.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            table.addCell(header1);
            table.addCell(header2);

            // Données du tableau (issues du JTable)
            for (int i = 0; i < model.getRowCount(); i++) {
                table.addCell(new Phrase(model.getValueAt(i, 0).toString(), fontNormal)); // Date
                table.addCell(new Phrase(model.getValueAt(i, 1).toString(), fontNormal)); // Montant
            }
            
            document.add(table);
            document.add(new Paragraph("\n\n"));
            
            // Signature ou Note
            Paragraph signature = new Paragraph("Signature de l'Administration : ...................................", fontNormal);
            signature.setAlignment(Element.ALIGN_RIGHT);
            document.add(signature);


            document.close();

            JOptionPane.showMessageDialog(this, "Relevé PDF généré et enregistré sous : \n" + outputFile.getAbsolutePath(), "Succès", JOptionPane.INFORMATION_MESSAGE);
            
            // Tenter d'ouvrir le fichier PDF
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(outputFile);
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la génération du PDF. Vérifiez les dépendances iTextPDF : " + ex.getMessage(), "Erreur PDF", JOptionPane.ERROR_MESSAGE);
        }
    

    } 


// NOTE: La méthode imprimerrecuMouseClicked peut simplement appeler pdftb_historiqueMouseClicked(evt);
    }//GEN-LAST:event_pdftb_historiqueMouseClicked

    private void archiverElevesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_archiverElevesActionPerformed
        // TODO add your handling code here:
        archiverDonneesAnnee();
    }//GEN-LAST:event_archiverElevesActionPerformed

    private void archiverEleves1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_archiverEleves1ActionPerformed
        // 1. Création de la nouvelle fenêtre
    // 1. Obtenir la référence de la fenêtre principale actuelle (celle où vous cliquez sur le bouton)
    // Cela utilise l'événement pour trouver la fenêtre qui contient le bouton.
    final JFrame ancienneFrame = (JFrame) SwingUtilities.getWindowAncestor((java.awt.Component) evt.getSource());

    // 2. Définir l'action de retour : Rendre l'ancienne fenêtre VRAIMENT visible
    // C'est cette action qui est passée au ArchiveViewerPanel pour le bouton "Retour".
    Runnable actionRetour = () -> {
        if (ancienneFrame != null) {
            ancienneFrame.setVisible(true); 
            // Note: Nous n'avons pas besoin de faire dispose() ici car c'est ArchiveViewerPanel qui ferme sa propre frame.
        }
    };
    
    // 3. Fermer/Masquer la fenêtre principale (pour ne voir que l'archive)
    // Nous la masquons au lieu de la disposer, c'est plus sûr pour la réafficher.
    if (ancienneFrame != null) {
        ancienneFrame.setVisible(false); // Masque la fenêtre principale
    }

    // 4. Création de la nouvelle fenêtre d'archives (JFrame)
    JFrame archiveFrame = new JFrame("Historique et Visualisation des Archives Scolaires");
    
    // 5. Instanciation du panneau AVEC les paramètres de retour !
    // NOUS UTILISONS MAINTENANT le constructeur qui accepte l'action de retour et la frame.
    ArchiveViewerPanel archivePanel = new ArchiveViewerPanel(archiveFrame, actionRetour);
    
    // 6. Configuration de la nouvelle fenêtre
    archiveFrame.setContentPane(archivePanel);
    archiveFrame.setUndecorated(true); // Supprime la barre de titre Windows/Système
    archiveFrame.setResizable(false);  // Empêche le redimensionnement manuel
    
    // 7. Affichage et dimensionnement
    archiveFrame.setSize(1300, 600); // <--- TRÈS IMPORTANT : Utiliser .pack() pour prendre la taille préférée du JPanel (1300x750)
    archiveFrame.setLocationRelativeTo(null); // Centrer la fenêtre
    archiveFrame.setVisible(true);
    
    archiveFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
    }//GEN-LAST:event_archiverEleves1ActionPerformed

    private void supprimerPaiementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supprimerPaiementActionPerformed
        // TODO add your handling code here:
        
    
    // 1. VÉRIFICATION DE LA SÉLECTION
    // Assurez-vous d'utiliser la référence à votre JTable d'historique. 
    // Si votre table d'historique s'appelle tablefrais, remplacez 'tableHistorique'.
    int selectedRow = tablefrais.getSelectedRow(); 
    
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Veuillez sélectionner une ligne dans l'historique de paiement à supprimer.", "Erreur de Sélection", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // 2. CONFIRMATION DE L'UTILISATEUR
    int confirmation = JOptionPane.showConfirmDialog(this, 
        "Êtes-vous sûr de vouloir supprimer ce paiement? Les soldes seront ajustés.", 
        "Confirmation de Suppression", JOptionPane.YES_NO_OPTION);

    if (confirmation != JOptionPane.YES_OPTION) {
        return;
    }

    // 3. RÉCUPÉRATION DES DONNÉES CLÉS
    
    // Récupère le matricule de l'élève actuellement affiché.
    String matriculeEleve = txtMatricule.getText(); 
    
    // Récupère la date du paiement (colonne 0 dans le modèle de l'historique).
    // NOTE : Si la date n'est pas la clé primaire, cela peut causer des problèmes si deux paiements ont la même date.
    String datePaiementASupprimer = modelHistorique.getValueAt(selectedRow, 0).toString(); 
    
    // Récupère le montant versé à déduire (colonne 1 dans le modèle de l'historique).
    double montantVerseASupprimer;
    try {
        // CORRECTION : Nettoyer la chaîne et la convertir en double.
        String montantStr = modelHistorique.getValueAt(selectedRow, 1).toString().replace(",", "."); 
        montantVerseASupprimer = Double.parseDouble(montantStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Erreur de format de montant dans la table.", "Erreur", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // Lancement de la méthode de mise à jour et suppression
    supprimerPaiementEtMettreAJourSoldes(matriculeEleve, datePaiementASupprimer, montantVerseASupprimer);

    }//GEN-LAST:event_supprimerPaiementActionPerformed

/**
 * Gère la transaction SQL pour annuler le paiement et mettre à jour les tables tb_frais et tb_historique_paiement.
 */
    private Connection getConnection() throws SQLException {
    try {
        String url = "jdbc:mysql://localhost/frais_scolaire";
        String user = "root";
        String password = ""; // Ou votre mot de passe réel
        
        return DriverManager.getConnection(url, user, password);
    } catch (SQLException ex) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Erreur de connexion à la BD", ex);
        throw ex; 
    }
}
private void supprimerPaiementEtMettreAJourSoldes(String matriculeEleve, String datePaiement, double montantASupprimer) {
    Connection con = null;
    try {
        // --- DÉBUT TRANSACTION ---
        con = getConnection(); // Utilise la méthode sécurisée
        con.setAutoCommit(false); 

        // --- ÉTAPE 1 : RÉCUPÉRER LES SOLDES ACTUELS ---
        String sqlSelect = "SELECT montant_paye, reste_a_payer FROM tb_frais WHERE matricule_eleve = ?";
        PreparedStatement psSelect = con.prepareStatement(sqlSelect);
        psSelect.setString(1, matriculeEleve);
        ResultSet Rs = psSelect.executeQuery();

        if (Rs.next()) {
            double montantPayeActuel = Rs.getDouble("montant_paye");
            double resteAPayerActuel = Rs.getDouble("reste_a_payer");

            // --- ÉTAPE 2 : CALCULER LES NOUVEAUX SOLDES ---
            double nouveauMontantPaye = montantPayeActuel - montantASupprimer;
            double nouveauResteAPayer = resteAPayerActuel + montantASupprimer;

            // --- ÉTAPE 3 : METTRE À JOUR LA TABLE tb_frais ---
            String sqlUpdateFrais = "UPDATE tb_frais SET montant_paye = ?, reste_a_payer = ? WHERE matricule_eleve = ?";
            PreparedStatement psUpdateFrais = con.prepareStatement(sqlUpdateFrais);
            psUpdateFrais.setDouble(1, nouveauMontantPaye);
            psUpdateFrais.setDouble(2, nouveauResteAPayer);
            psUpdateFrais.setString(3, matriculeEleve);
            psUpdateFrais.executeUpdate();

            // --- ÉTAPE 4 : SUPPRIMER L'ENREGISTREMENT DANS tb_historique_paiement ---
            String sqlDeleteHistorique = "DELETE FROM tb_historique_paiement WHERE matricule_eleve = ? AND date_paiement = ?";
            PreparedStatement psDeleteHistorique = con.prepareStatement(sqlDeleteHistorique);
            psDeleteHistorique.setString(1, matriculeEleve);
            psDeleteHistorique.setString(2, datePaiement); 
            
            int rowsDeleted = psDeleteHistorique.executeUpdate();
            
            if (rowsDeleted == 0) {
                 con.rollback(); // Annuler tout si l'enregistrement d'historique n'est pas trouvé
                 JOptionPane.showMessageDialog(this, "Erreur: Paiement introuvable dans l'historique.", "Annulation", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            // --- ÉTAPE 5 : FINALISER LA TRANSACTION ---
            con.commit(); // ✅ VALIDATION
            JOptionPane.showMessageDialog(this, "Paiement supprimé avec succès. Les soldes ont été mis à jour.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            
            // --- ÉTAPE 6 : RECHARGER L'INTERFACE (SANS con = getConnection()) ---
            
            // 1. Mise à jour des tables
            chargerFrais(); 
            afficherHistoriquePaiement(); 
            
            // 2. Mise à jour des champs d'affichage
            montantpaye.setText(String.format("%.2f", nouveauMontantPaye)); // CORRIGÉ
            restepaye.setText(String.format("%.2f", nouveauResteAPayer));  // CORRIGÉ

        } else {
            con.rollback();
            JOptionPane.showMessageDialog(this, "Erreur critique : Fiche frais de l'élève introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }

    } catch (SQLException ex) {
        // --- Gestion des erreurs et Rollback ---
        try {
            if (con != null) con.rollback(); 
        } catch (SQLException rollbackEx) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Rollback failed", rollbackEx);
        }
        JOptionPane.showMessageDialog(this, "Erreur de base de données lors de la suppression: " + ex.getMessage(), "Erreur BD", JOptionPane.ERROR_MESSAGE);
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Suppression failed", ex);
    } finally {
        // --- Fermeture de la connexion ---
        try {
            if (con != null) {
                con.setAutoCommit(true); // Rétablir l'auto-commit par défaut
                con.close();
            }
        } catch (SQLException closeEx) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Connection close failed", closeEx);
        }
    }
}

public void afficherHistoriquePaiement() {
    
    String matricule = txtMatricule.getText(); 
    
    // Assurez-vous que tableHistorique est le nom de votre JTable d'historique. 
    // Si vous utilisez tablefrais, remplacez-le ici.
    DefaultTableModel model = (DefaultTableModel) tablefrais.getModel(); 
    model.setRowCount(0);

    if (matricule.isEmpty()) {
        return; 
    }
    
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet Rs = null;
    
    // Requête pour l'historique : 3 colonnes de l'interface graphique
    String sql = "SELECT date_paiement, montant_verse, montant_total_paye " +
                 "FROM tb_historique_paiement WHERE matricule_eleve = ?";

    try {
        con = getConnection();
        ps = con.prepareStatement(sql);
        ps.setString(1, matricule); 
        Rs = ps.executeQuery();

        while (Rs.next()) {
            model.addRow(new Object[]{
                Rs.getString("date_paiement"), 
                String.format("%.2f", Rs.getDouble("montant_verse")), // Montant versé
                String.format("%.2f", Rs.getDouble("montant_total_paye")) // Solde après ce paiement
            });
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Erreur lors du chargement de l'historique (BD): " + ex.getMessage(), "Erreur BD", JOptionPane.ERROR_MESSAGE);
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Chargement historique échoué", ex);
    } finally {
        try {
            if (Rs != null) Rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException closeEx) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Échec de fermeture de connexion", closeEx);
        }
    }
}
public void chargerFrais() { 
    
    // Si tablefrais est le JTable principal qui affiche les élèves
    DefaultTableModel model = (DefaultTableModel) tablefrais.getModel(); 
    model.setRowCount(0); 
    
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet Rs = null;
    
    // Requête corrigée avec JOINTURE et ALIAS
    String sql = "SELECT " +
                 "f.matricule_eleve, e.nom, e.post_nom, e.classe, " +
                 "f.frais_total, f.montant_paye, f.reste_a_payer " +
                 "FROM tb_frais f " +
                 "INNER JOIN tb_eleve e ON f.matricule_eleve = e.matricule"; // Jointure selon vos structures (matricule dans tb_eleve)
    
    try {
        con = getConnection();
        ps = con.prepareStatement(sql);
        Rs = ps.executeQuery();
        
        while (Rs.next()) {
            model.addRow(new Object[]{
                Rs.getString("matricule_eleve"),
                Rs.getString("nom"),
                Rs.getString("post_nom"),
                Rs.getString("classe"),
                String.format("%.2f", Rs.getDouble("frais_total")),
                String.format("%.2f", Rs.getDouble("montant_paye")),
                String.format("%.2f", Rs.getDouble("reste_a_payer"))
            });
        }
        
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Erreur lors du chargement des frais (BD): " + ex.getMessage(), "Erreur de Base de Données", JOptionPane.ERROR_MESSAGE);
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Chargement des frais échoué", ex);
    } finally {
        try {
            if (Rs != null) Rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException closeEx) {
             Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Échec de fermeture de connexion", closeEx);
        }
    }
}
    public void afficherTable() {
        // Récupère le modèle de la JTable, qui gère les données affichées
        DefaultTableModel model = (DefaultTableModel) tablefrais.getModel();

        // Vide le contenu actuel de la table pour repartir à zéro
        model.setRowCount(0);

        try {
            // Connexion à la base de données MySQL 'gestion_des_eleve' avec utilisateur 'root' sans mot de passe
            con = DriverManager.getConnection("jdbc:mysql://localhost/frais_scolaire", "root", "");

            // Requête SQL simple pour récupérer toutes les données de la table 'gestion_eleve'
            String sql = "SELECT * FROM tb_historique_paiement";

            // Préparation de la requête (permet aussi de sécuriser les paramètres)
            ps = con.prepareStatement(sql);

            // Exécution de la requête et récupération du résultat
            Rs = ps.executeQuery();

            // Parcours de toutes les lignes récupérées
            while (Rs.next()) {
                // Création d'un tableau d'objets représentant une ligne de la table
                Object[] row = {
                    Rs.getString("date_paiement"),          // Code élève
                    Rs.getString("montant_verse"),           // Nom élève
                   
                };
                // Ajoute la ligne dans le modèle de la JTable (ajoute donc une ligne visible)
                model.addRow(row);
            }

        } catch (SQLException ex) {
            // En cas d'erreur SQL, affiche la trace dans la console pour le débogage
            ex.printStackTrace();
        }
}





// ... (votre classe FraisPanel)

private void archiverDonneesAnnee() {
    String anneeAArchiver = (String) comboAnnee.getSelectedItem();
    
    if (anneeAArchiver == null || anneeAArchiver.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Veuillez sélectionner l'Année Scolaire à archiver.", "Erreur", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // --- Confirmation de l'opération ---
    int confirmation = JOptionPane.showConfirmDialog(this, 
        "ATTENTION: Cette opération va archiver TOUS les élèves (y compris les numéros de téléphone) et vider complètement les tables tb_eleve, tb_frais et tb_historique_paiement pour l'année " + anneeAArchiver + ".\n" +
        "Voulez-vous vraiment continuer?", 
        "Confirmation d'Archivage TOTAL", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

    if (confirmation != JOptionPane.YES_OPTION) {
        return;
    }

    Connection con = null;
    int rowsArchivedEleves = 0;
    int rowsArchivedHist = 0;
    int rowsDeletedEleves = 0;

    try {
        con = DriverManager.getConnection("jdbc:mysql://localhost/frais_scolaire", "root", "");
        con.setAutoCommit(false); // DÉBUT DE LA TRANSACTION

        // --- 1. ARCHIVAGE DES DONNÉES ÉLÈVES (INCLUANT LE TÉLÉPHONE) ---
        String sqlArchiveEleves = 
            // AJOUT de la colonne telephone ici
            "INSERT IGNORE INTO tb_archive_eleves (matricule_eleve, annee_scolaire, nom, postNom, telephone, classe_annee_archive, sexe, frais_total_annuel, montant_paye_final, reste_a_payer_final, date_archivage) " +
            "SELECT " +
            "  e.matricule, " +
            "  ?, " + 
            "  e.nom, e.post_nom, e.telephone, e.classe, e.sexe, " + // AJOUT de e.telephone
            "  COALESCE(f.frais_total, 0.00), " + 
            "  COALESCE(f.montant_paye, 0.00), " +
            "  COALESCE(f.reste_a_payer, 0.00), " +
            "  CURDATE() " +
            "FROM tb_eleve e " +
            "LEFT JOIN tb_frais f ON e.matricule = f.matricule_eleve AND f.annee_scolaire = ?"; 

        try (PreparedStatement psArchiveEleves = con.prepareStatement(sqlArchiveEleves)) {
            psArchiveEleves.setString(1, anneeAArchiver); 
            psArchiveEleves.setString(2, anneeAArchiver); 
            rowsArchivedEleves = psArchiveEleves.executeUpdate();
        }

        // --- 2. ARCHIVAGE de l'Historique (tb_historique_paiement -> tb_archive_historique) ---
        String sqlArchiveHist = "INSERT IGNORE INTO tb_archive_historique (matricule_eleve, annee_scolaire, montant_verse, date_paiement, montant_total_paye) " +
                                "SELECT matricule_eleve, annee_scolaire, montant_verse, date_paiement, montant_total_paye " +
                                "FROM tb_historique_paiement WHERE annee_scolaire = ?";
        try (PreparedStatement psArchiveHist = con.prepareStatement(sqlArchiveHist)) {
            psArchiveHist.setString(1, anneeAArchiver);
            rowsArchivedHist = psArchiveHist.executeUpdate();
        }
        
        // --- 3. SUPPRESSION TOTALE des données de travail (Nettoyage) ---
        // Suppression de tb_frais pour l'année archivée
        String sqlDeleteFrais = "DELETE FROM tb_frais WHERE annee_scolaire = ?";
        try (PreparedStatement psDeleteFrais = con.prepareStatement(sqlDeleteFrais)) {
            psDeleteFrais.setString(1, anneeAArchiver);
            psDeleteFrais.executeUpdate();
        }
        
        // Suppression de tb_historique_paiement pour l'année archivée
        String sqlDeleteHist = "DELETE FROM tb_historique_paiement WHERE annee_scolaire = ?";
        try (PreparedStatement psDeleteHist = con.prepareStatement(sqlDeleteHist)) {
            psDeleteHist.setString(1, anneeAArchiver);
            psDeleteHist.executeUpdate();
        }
        
        // Suppression de TOUS les élèves de tb_eleve
        String sqlDeleteAllEleves = "DELETE FROM tb_eleve";
        try (PreparedStatement psDeleteAllEleves = con.prepareStatement(sqlDeleteAllEleves)) {
            rowsDeletedEleves = psDeleteAllEleves.executeUpdate();
        }

        con.commit(); // VALIDATION DE TOUTES LES TRANSACTIONS

        JOptionPane.showMessageDialog(this, 
            "Archivage de l'année " + anneeAArchiver + " terminé. ✅\n" +
            "Total élèves archivés: " + rowsArchivedEleves + "\n" +
            "Tables de travail vidées pour la rentrée.",
            "Archivage Annuel Terminé", JOptionPane.INFORMATION_MESSAGE);
        
        // Nettoyage de l'interface
        // Assurez-vous d'avoir une méthode reinitialiserChamps()
        // reinitialiserChamps(); 

    } catch (SQLException ex) {
        try {
            if (con != null) con.rollback(); 
        } catch (SQLException rbex) {
            rbex.printStackTrace();
        }
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, 
            "Erreur SQL critique lors de l'archivage: " + ex.getMessage(), 
            "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (con != null) con.setAutoCommit(true);
            if (con != null) con.close();
        } catch (SQLException fex) {
            fex.printStackTrace();
        }
    }
}

/**
 * Réinitialise les champs de saisie et d'affichage dans le panneau Frais.
 */
private void reinitialiserChamps() {
    // Champs de saisie
    montantpayement.setText("");
    datepayement.setDate(null);
    
    // Champs d'affichage du solde (si vous voulez effacer l'élève courant)
    // NOTE: Si vous voulez garder l'élève chargé, n'effacez pas ces champs.
    // Si vous voulez effacer l'élève, décommentez les lignes suivantes:
    
    txtMatricule.setText("");
    txtNom.setText("");
    txtPostNom.setText("");
    txtClasse.setText("");
    fraistotaux.setText("");
    montantpaye.setText("");
    restepaye.setText("");
    
    // Vider la table d'historique
    DefaultTableModel model = (DefaultTableModel) tablefrais.getModel();
    model.setRowCount(0);
   
    
    // Dans le contexte de l'archivage, vous pourriez vouloir tout effacer:
    montantpaye.setText("0.00");
    restepaye.setText("0.00");
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Rechercher;
    private javax.swing.JButton archiverEleves;
    private javax.swing.JButton archiverEleves1;
    private javax.swing.JButton btnrechercherfrais;
    private javax.swing.JComboBox comboAnnee;
    private com.toedter.calendar.JDateChooser datepayement;
    private javax.swing.JButton enregistrerpayement;
    private javax.swing.JTextField fraistotaux;
    private javax.swing.JButton imprimerrecu;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblfraistotaux;
    private javax.swing.JLabel lblfraistotaux1;
    private javax.swing.JLabel lblfraistotaux10;
    private javax.swing.JLabel lblfraistotaux2;
    private javax.swing.JLabel lblfraistotaux3;
    private javax.swing.JLabel lblfraistotaux4;
    private javax.swing.JLabel lblfraistotaux5;
    private javax.swing.JLabel lblfraistotaux7;
    private javax.swing.JLabel lblfraistotaux8;
    private javax.swing.JLabel lblfraistotaux9;
    private javax.swing.JLabel montantpaye;
    private javax.swing.JTextField montantpayement;
    private javax.swing.JButton pdftb_historique;
    private javax.swing.JTextField recherchefrais;
    private javax.swing.JLabel restepaye;
    private javax.swing.JButton supprimerPaiement;
    private javax.swing.JTable tablefrais;
    private javax.swing.JLabel txtClasse;
    private javax.swing.JLabel txtMatricule;
    private javax.swing.JLabel txtNom;
    private javax.swing.JLabel txtPostNom;
    // End of variables declaration//GEN-END:variables
}
