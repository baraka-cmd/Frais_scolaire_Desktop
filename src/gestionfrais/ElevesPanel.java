package gestionfrais;
// Déclare que cette classe fait partie du package nommé 'gestionfrais'.

import java.awt.Font;
// Importe la classe Font pour gérer les polices de caractères.
import java.awt.Image;
// Importe la classe Image (utilisée pour les images, potentiellement pour les photos des élèves).
import java.beans.Statement;
// Importe la classe java.beans.Statement (Attention: ce n'est généralement pas la classe SQL attendue, qui serait java.sql.Statement).
import java.sql.Blob;
// Importe la classe Blob pour gérer les données binaires volumineuses (comme les images) dans la base de données.
import java.sql.Connection;
// Importe l'interface Connection pour établir une connexion à la base de données.
import java.sql.DriverManager;
// Importe la classe DriverManager pour gérer les pilotes de bases de données et obtenir des connexions.
import java.sql.PreparedStatement;
// Importe l'interface PreparedStatement pour exécuter des requêtes SQL précompilées.
import java.sql.ResultSet;
// Importe l'interface ResultSet pour stocker et parcourir les résultats d'une requête.
import java.sql.SQLException;
// Importe la classe SQLException pour gérer les erreurs liées à la base de données.
import javax.swing.ImageIcon;
// Importe la classe ImageIcon pour afficher des images dans les composants Swing.
import javax.swing.JOptionPane;
// Importe la classe JOptionPane pour afficher des boîtes de dialogue standard (messages, erreurs, confirmations).
import javax.swing.table.DefaultTableModel;
// Importe la classe DefaultTableModel pour manipuler les données affichées dans une JTable.

// Déclaration de la classe ElevesPanel, qui hérite probablement de javax.swing.JPanel.
public class ElevesPanel extends javax.swing.JPanel {

    // Constructeur de la classe ElevesPanel.
    public ElevesPanel() {
        // Appelle la méthode générée par l'éditeur de Swing pour initialiser les composants de l'interface graphique.
        initComponents();
        // Appelle la méthode pour charger les données des élèves dans la JTable au démarrage du panneau.
        Selecttb_eleve();
    }
    
    
    // Déclaration d'une variable de connexion à la base de données, initialisée à null.
    Connection con = null;
    // Déclaration d'une variable pour la requête préparée, initialisée à null.
    PreparedStatement ps = null;
    // Déclaration d'une variable pour le jeu de résultats (résultat de la requête SELECT), initialisée à null.
    ResultSet Rs = null;
    
     // Déclaration de la méthode pour sélectionner et afficher les données des élèves.
     public void Selecttb_eleve()
     {
         // Récupère le modèle de données actuel de la JTable nommée 'tb_eleve'.
         DefaultTableModel model = (DefaultTableModel) tb_eleve.getModel();

         // Supprime toutes les lignes existantes dans le modèle, vidant ainsi la table.
         model.setRowCount(0);

         // Début du bloc try-catch pour la gestion des erreurs de connexion et de requête SQL.
         try {
             // Tente d'établir une connexion à la base de données MySQL.
             con = DriverManager.getConnection("jdbc:mysql://localhost/frais_scolaire", "root", "");

             // Chaîne de caractères contenant la requête SQL pour récupérer toutes les colonnes de la table 'tb_eleve'.
             String sql = "SELECT * FROM tb_eleve";

             // Crée un objet PreparedStatement à partir de la connexion et de la requête SQL.
             PreparedStatement ps = con.prepareStatement(sql);

             // Exécute la requête SELECT et stocke le résultat dans l'objet ResultSet.
             Rs = ps.executeQuery();

             // Boucle pour parcourir chaque ligne retournée par le ResultSet.
             while (Rs.next()) {
                 // Création d'un tableau d'objets pour stocker les valeurs d'une ligne de l'élève.
                 Object[] row = {
                     Rs.getString("matricule"),             // Récupère la valeur de la colonne 'matricule'.
                     Rs.getString("nom"),           // Récupère la valeur de la colonne 'nom'.
                     Rs.getString("post_nom"),      // Récupère la valeur de la colonne 'post_nom'.
                     Rs.getString("classe"),        // Récupère la valeur de la colonne 'classe'.
                     Rs.getString("sexe"),          // Récupère la valeur de la colonne 'sexe'.
                     Rs.getString("telephone"),     // Récupère la valeur de la colonne 'telephone'.
                 };
                 // Ajoute le tableau (la ligne d'élève) au modèle de la JTable.
                 model.addRow(row);
             }

         // Capture de l'exception si une erreur SQL se produit (connexion, requête, etc.).
         } catch (SQLException ex) {
             // Affiche la trace de l'erreur dans la console pour le débogage.
             ex.printStackTrace();
             
     }
         
         // Ajout d'un écouteur d'événements à la JTable 'tb_eleve' pour détecter les clics de souris.
         tb_eleve.addMouseListener(new java.awt.event.MouseAdapter() {
             // Redéfinition de la méthode appelée lors du relâchement du bouton de la souris.
             @Override
             public void mouseReleased(java.awt.event.MouseEvent evt) {
                 // Récupère l'index de la ligne actuellement sélectionnée dans le tableau.
                 int selectionner = tb_eleve.getSelectedRow();  

                 // Vérifie si une ligne a bien été sélectionnée (index >= 0).
                 if (selectionner >= 0) {
                     // Récupère à nouveau le modèle de données du tableau.
                     DefaultTableModel model = (DefaultTableModel) tb_eleve.getModel();

                     // Remplissage des champs de texte et des combobox avec les données de la ligne sélectionnée:
                     // Remplissage du champ ideleve avec la valeur de la colonne 0 (matricule).
                     ideleve.setText(getSafeValue(model, selectionner, 0));
                     // Remplissage du champ nomeleve avec la valeur de la colonne 1 (nom).
                     nomeleve.setText(getSafeValue(model, selectionner, 1));
                     // Remplissage du champ post_nomeleve avec la valeur de la colonne 2 (post_nom).
                     post_nomeleve.setText(getSafeValue(model, selectionner, 2));
                     // Remplissage de la combobox comboeleve avec la valeur de la colonne 3 (classe).
                     comboeleve.setSelectedItem(getSafeValue(model, selectionner, 3));
                     // Remplissage de la combobox sexeeleve avec la valeur de la colonne 4 (sexe).
                     sexeeleve.setSelectedItem(getSafeValue(model, selectionner, 4));
                     // Remplissage du champ teleeleve avec la valeur de la colonne 5 (telephone).
                     teleeleve.setText(getSafeValue(model, selectionner, 5));
                     
                 }
             }

             
             // Déclaration de la méthode utilitaire privée pour récupérer une valeur sans risque d'erreur NullPointerException.
             private String getSafeValue(DefaultTableModel model, int row, int col) {
                 // Récupère l'objet à la position spécifiée (ligne, colonne).
                 Object value = model.getValueAt(row, col);
                 // Vérifie si l'objet n'est pas null; si oui, le convertit en String, sinon retourne une chaîne vide.
                 return value != null ? value.toString() : "";  
             }
             
             
         });
         // Création d'un objet Font pour le style de l'entête du tableau.
         Font headerFont = new Font("Times new roman", Font.BOLD, 14);
         // Applique cette nouvelle police de caractères à l'entête de la JTable.
         tb_eleve.getTableHeader().setFont(headerFont);
     }
     
     // Note: La méthode 'initComponents()' et les déclarations de variables des composants graphiques (tb_eleve, ideleve, nomeleve, etc.) 
     // ne sont pas visibles dans cet extrait, mais elles sont nécessaires au fonctionnement.

            

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnrechercher = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        ideleve = new javax.swing.JTextField();
        lblfraistotaux = new javax.swing.JLabel();
        lblfraistotaux1 = new javax.swing.JLabel();
        lblfraistotaux3 = new javax.swing.JLabel();
        lblfraistotaux4 = new javax.swing.JLabel();
        lblfraistotaux5 = new javax.swing.JLabel();
        lblfraistotaux2 = new javax.swing.JLabel();
        comboeleve = new javax.swing.JComboBox();
        nomeleve = new javax.swing.JTextField();
        post_nomeleve = new javax.swing.JTextField();
        teleeleve = new javax.swing.JTextField();
        sexeeleve = new javax.swing.JComboBox();
        btnenregistrer = new javax.swing.JButton();
        btnmodifier = new javax.swing.JButton();
        btneleve6 = new javax.swing.JButton();
        btneleve7 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tb_eleve = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        champRecherche = new javax.swing.JTextField();

        jPanel1.setToolTipText("");

        btnrechercher.setBackground(new java.awt.Color(52, 152, 219));
        btnrechercher.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        btnrechercher.setText("Rechercher");
        btnrechercher.setToolTipText("");
        btnrechercher.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnrechercherMouseClicked(evt);
            }
        });

        ideleve.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        ideleve.setForeground(new java.awt.Color(51, 102, 255));
        ideleve.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        lblfraistotaux.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        lblfraistotaux.setText("Matricule");

        lblfraistotaux1.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        lblfraistotaux1.setText("Nom");

        lblfraistotaux3.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        lblfraistotaux3.setText("Classe");

        lblfraistotaux4.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        lblfraistotaux4.setText("Sexe");

        lblfraistotaux5.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        lblfraistotaux5.setText("Tél Parent");

        lblfraistotaux2.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        lblfraistotaux2.setText("Post - nom");

        comboeleve.setFont(new java.awt.Font("Cambria", 0, 18)); // NOI18N
        comboeleve.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1 année", "2 année", "3 année", "4 année", "5 année", "6 année" }));

        nomeleve.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        nomeleve.setForeground(new java.awt.Color(51, 102, 255));
        nomeleve.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        post_nomeleve.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        post_nomeleve.setForeground(new java.awt.Color(51, 102, 255));
        post_nomeleve.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        teleeleve.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        teleeleve.setForeground(new java.awt.Color(51, 102, 255));
        teleeleve.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));

        sexeeleve.setFont(new java.awt.Font("Cambria", 0, 18)); // NOI18N
        sexeeleve.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Masculin", "Feminin" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblfraistotaux1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblfraistotaux, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                    .addComponent(lblfraistotaux2, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ideleve, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                    .addComponent(nomeleve)
                    .addComponent(post_nomeleve))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblfraistotaux3, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblfraistotaux4, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(37, 37, 37))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(lblfraistotaux5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(comboeleve, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sexeeleve, 0, 174, Short.MAX_VALUE)
                    .addComponent(teleeleve)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboeleve, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblfraistotaux3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sexeeleve, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblfraistotaux4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblfraistotaux5)
                            .addComponent(teleeleve, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblfraistotaux, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(ideleve, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblfraistotaux1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nomeleve, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblfraistotaux2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(post_nomeleve, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)))))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        btnenregistrer.setBackground(new java.awt.Color(46, 204, 113));
        btnenregistrer.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        btnenregistrer.setText("Enregistrer");
        btnenregistrer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnenregistrerMouseClicked(evt);
            }
        });
        btnenregistrer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnenregistrerActionPerformed(evt);
            }
        });

        btnmodifier.setBackground(new java.awt.Color(52, 152, 219));
        btnmodifier.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        btnmodifier.setText("Modifier");
        btnmodifier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnmodifierActionPerformed(evt);
            }
        });

        btneleve6.setBackground(new java.awt.Color(231, 76, 60));
        btneleve6.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        btneleve6.setText("Supprimer");
        btneleve6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btneleve6MouseClicked(evt);
            }
        });

        btneleve7.setBackground(new java.awt.Color(255, 255, 255));
        btneleve7.setFont(new java.awt.Font("Cambria", 1, 18)); // NOI18N
        btneleve7.setText("Nouveau");
        btneleve7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btneleve7MouseClicked(evt);
            }
        });
        btneleve7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btneleve7ActionPerformed(evt);
            }
        });

        tb_eleve.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Matricule", "Nom", "Post - nom", "Classe", "Sexe", "N° Téléphone parent"
            }
        ));
        jScrollPane1.setViewportView(tb_eleve);
        if (tb_eleve.getColumnModel().getColumnCount() > 0) {
            tb_eleve.getColumnModel().getColumn(0).setMinWidth(90);
            tb_eleve.getColumnModel().getColumn(0).setMaxWidth(90);
            tb_eleve.getColumnModel().getColumn(3).setMinWidth(90);
            tb_eleve.getColumnModel().getColumn(3).setMaxWidth(90);
            tb_eleve.getColumnModel().getColumn(4).setMinWidth(90);
            tb_eleve.getColumnModel().getColumn(4).setMaxWidth(90);
        }

        jLabel2.setIcon(new javax.swing.ImageIcon(new ImageIcon(getClass().getResource("/gestionfrais/seach(1).png")).getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH)));

        jLabel3.setIcon(new javax.swing.ImageIcon(new ImageIcon(getClass().getResource("/gestionfrais/diskette.png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

        jLabel4.setIcon(new javax.swing.ImageIcon(new ImageIcon(getClass().getResource("/gestionfrais/edit(2).png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

        jLabel5.setIcon(new javax.swing.ImageIcon(new ImageIcon(getClass().getResource("/gestionfrais/delete.png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

        jLabel1.setFont(new java.awt.Font("Times New Roman", 3, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 255));
        jLabel1.setText("Enregistrement Elève");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(champRecherche, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(btnrechercher, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnenregistrer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnmodifier)
                        .addGap(42, 42, 42)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btneleve6)
                        .addGap(80, 80, 80)
                        .addComponent(btneleve7)
                        .addGap(32, 32, 32))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnrechercher)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btneleve7)
                                .addComponent(btneleve6)
                                .addComponent(btnmodifier))
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnenregistrer)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(champRecherche, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnenregistrerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnenregistrerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnenregistrerActionPerformed

    private void btnenregistrerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnenregistrerMouseClicked
        // TODO add your handling code here:
                                                   
    // Début de la méthode de gestion de l'événement de clic de souris sur le bouton "enregistrer".
    
    // Le commentaire généré automatiquement, indiquant où insérer le code de gestion.
    // TODO add your handling code here:
    
    // Début d'un bloc try pour gérer les opérations potentiellement génératrices d'erreurs (SQL).
    try{
        // Établit la connexion à la base de données MySQL 'frais_scolaire' en tant qu'utilisateur 'root'.
        con = DriverManager.getConnection("jdbc:mysql://localhost/frais_scolaire", "root", "");
        // Prépare la requête SQL d'insertion dans la table 'tb_eleve' avec des emplacements réservés (?) pour les valeurs.
        PreparedStatement add = con.prepareStatement("insert into tb_eleve(matricule, nom, post_nom, classe, sexe, telephone) values (?,?,?,?,?,?)");
        
        // Définit la valeur du premier paramètre (matricule) : conversion du texte du champ 'ideleve' en entier.
        add.setInt(1, Integer.valueOf(ideleve.getText()));
        // Définit la valeur du deuxième paramètre (nom) : utilise le texte du champ 'nomeleve'.
        add.setString(2, nomeleve.getText());
        // Définit la valeur du troisième paramètre (post_nom) : utilise le texte du champ 'post_nomeleve'.
        add.setString(3, post_nomeleve.getText());
        // Définit la valeur du quatrième paramètre (classe) : utilise l'élément sélectionné dans la combobox 'comboeleve' converti en String.
        add.setString(4, comboeleve.getSelectedItem().toString());
        // Définit la valeur du cinquième paramètre (sexe) : utilise l'élément sélectionné dans la combobox 'sexeeleve' converti en String.
        add.setString(5, sexeeleve.getSelectedItem().toString()); 
        // Définit la valeur du sixième paramètre (telephone) : utilise le texte du champ 'teleeleve'.
        add.setString(6, teleeleve.getText());
        
        // Exécute la requête d'insertion (un élève est ajouté à la base de données).
        add.executeUpdate();

        // Ferme explicitement la connexion à la base de données pour libérer les ressources.
        con.close();

        // Affiche un message de confirmation à l'utilisateur.
        JOptionPane.showMessageDialog(null, "Elève enregistrer avec succès", null, JOptionPane.INFORMATION_MESSAGE);
        // Appelle une méthode (non montrée ici) pour rafraîchir l'affichage du tableau.
        afficherTable();
    // Capture toute exception qui pourrait se produire pendant le bloc try.
    }catch (Exception e) {
        // En cas d'erreur (par exemple, un matricule déjà existant ou une erreur de connexion).
        // Affiche la trace complète de l'erreur dans la console pour le débogage.
        e.printStackTrace();
        // Affiche un message d'erreur spécifique à l'utilisateur, incluant le message de l'exception.
        JOptionPane.showMessageDialog(null, "Erreur, matricule existe déjà " + e.getMessage(), null, JOptionPane.ERROR_MESSAGE);
    }
    
    

        
    }//GEN-LAST:event_btnenregistrerMouseClicked

    private void btnmodifierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmodifierActionPerformed
        // TODO add your handling code here:
        //Début de la méthode de gestion de l'événement de clic sur le bouton "modifier".
    
    // Le commentaire généré automatiquement, indiquant où insérer le code de gestion.
    // TODO add your handling code here:
    
    // Début d'un bloc try pour gérer les opérations de mise à jour SQL.
    try {
        // Établit la connexion à la base de données.
        con = DriverManager.getConnection("jdbc:mysql://localhost/frais_scolaire", "root", "");

        // Requête SQL pour mettre à jour les colonnes de l'élève en fonction de son matricule.
        String sql = "UPDATE tb_eleve SET nom = ?, post_nom = ?, classe = ?, sexe = ?, telephone = ? WHERE matricule = ?";
        // Prépare la requête de mise à jour.
        ps = con.prepareStatement(sql);

        // Définition des paramètres pour la requête UPDATE:
        // Paramètre 1: nom (texte du champ nomeleve).
        ps.setString(1, nomeleve.getText());
        // Paramètre 2: post_nom (texte du champ post_nomeleve).
        ps.setString(2, post_nomeleve.getText());
        // Paramètre 3: classe (élément sélectionné de comboeleve).
        ps.setString(3, comboeleve.getSelectedItem().toString());
        // Paramètre 4: sexe (élément sélectionné de sexeeleve).
        ps.setString(4, sexeeleve.getSelectedItem().toString());
        // Paramètre 5: telephone (texte du champ teleeleve).
        ps.setString(5, teleeleve.getText());
        // Paramètre 6: matricule (critère de la clause WHERE) (texte du champ ideleve).
        ps.setString(6, ideleve.getText());
        
        // Exécute la requête de mise à jour et stocke le nombre de lignes modifiées.
        int rowsUpdated = ps.executeUpdate();  

        // Vérifie si au moins une ligne a été modifiée (rowsUpdated > 0).
        if (rowsUpdated > 0) {
            // Affiche un message de succès si la mise à jour a eu lieu.
            JOptionPane.showMessageDialog(null, "Données modifiées avec succès !");
            // Recharge les données dans le tableau après la modification.
            afficherTable(); 
            // Désélectionne toute ligne actuellement sélectionnée dans le tableau.
            tb_eleve.clearSelection(); 
        } else {
            // Affiche un message d'erreur si aucune ligne n'a été modifiée (matricule non trouvé).
            JOptionPane.showMessageDialog(null, "Matricule inexistant", null, JOptionPane.ERROR_MESSAGE);
        }

    // Capture toute exception SQL.
    } catch (SQLException ex) {
        // Affiche la trace de l'erreur dans la console.
        ex.printStackTrace();
        // Affiche un message d'erreur générique à l'utilisateur.
        JOptionPane.showMessageDialog(null, "Erreur lors de la modification", null, JOptionPane.ERROR_MESSAGE);
    }
    
    
    
    
    }//GEN-LAST:event_btnmodifierActionPerformed

    private void btneleve6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btneleve6MouseClicked
        // TODO add your handling code here:
        String num = ideleve.getText();
        // Déclare une variable String 'num' et y stocke le texte (le matricule) entré dans le champ 'ideleve'.

        // Demander confirmation à l'utilisateur avant la suppression
        // Début de la section du code qui demande une confirmation à l'utilisateur.
        int confirmation = JOptionPane.showConfirmDialog(
        // Affiche une boîte de dialogue de confirmation (Yes/No).
            null,
            // Le composant parent (null pour centrer la boîte sur l'écran).
            "Voulez-vous vraiment supprimer cet élève ?",
            // Le message affiché dans la boîte de dialogue.
            "Confirmation de suppression",
            // Le titre de la boîte de dialogue.
            JOptionPane.YES_NO_OPTION,
            // Type d'options affichées (Oui et Non).
            JOptionPane.WARNING_MESSAGE
            // Type d'icône utilisée (une icône d'avertissement).
        );

        // Si l'utilisateur confirme la suppression
        // Vérifie si la réponse de l'utilisateur correspond à l'option "Oui".
        if (confirmation == JOptionPane.YES_OPTION) {
            // Début du bloc try-with-resources pour gérer automatiquement la fermeture de la connexion et de la déclaration SQL.
            try (
                // Connexion à la base de données
                // Établit la connexion à la base de données 'frais_scolaire'.
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost/frais_scolaire", "root", "");
                // Crée un objet Statement pour exécuter des requêtes SQL.
                java.sql.Statement stmt = con.createStatement()
            ) {
                // Exécution de la requête de suppression
                // Exécute la commande DELETE SQL sur la table 'tb_eleve' en filtrant par le matricule stocké dans 'num'.
                stmt.executeUpdate("DELETE FROM tb_eleve WHERE matricule = '" + num + "'");

                // Afficher un message de confirmation
                // Affiche un message informant l'utilisateur que l'opération a réussi.
                JOptionPane.showMessageDialog(null, "Élève supprimé avec succès.", "Suppression", JOptionPane.INFORMATION_MESSAGE);

                // Appelle la méthode (présumée existante) pour rafraîchir le contenu du tableau des élèves.
                afficherTable();
                // Commentaire indiquant l'intention de fermer la fenêtre, mais la ligne de code est manquante/commentée.
                // Fermer la fenêtre actuelle et retourner à l'écran principal

                // Fermeture automatique de 'con' et 'stmt' grâce au bloc try-with-resources.
            } catch (SQLException ex) {
                // En cas d'erreur SQL
                // Capture l'exception SQLException.
                // Affiche un message d'erreur à l'utilisateur, incluant la description de l'erreur SQL.
                JOptionPane.showMessageDialog(null, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            // Si l'utilisateur annule la suppression
            // Exécuté si la confirmation n'est pas JOptionPane.YES_OPTION (c'est-à-dire si l'utilisateur a choisi Non ou fermé la boîte).
            // Affiche un message dans la console Java indiquant que la suppression a été annulée.
            System.out.println("Suppression annulée.");
        }


    }//GEN-LAST:event_btneleve6MouseClicked

    private void btneleve7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btneleve7MouseClicked
        // TODO add your handling code here:
        ideleve.setText("");
        // Définit le texte du champ 'ideleve' (probablement le matricule de l'élève) comme une chaîne vide, effaçant son contenu.
        nomeleve.setText("");
        // Définit le texte du champ 'nomeleve' (le nom de l'élève) comme une chaîne vide, l'effaçant.
        post_nomeleve.setText("");
        // Définit le texte du champ 'post_nomeleve' (le post-nom de l'élève) comme une chaîne vide, l'effaçant.
        comboeleve.setSelectedIndex(0);
        // Réinitialise la liste déroulante 'comboeleve' (probablement la classe) en sélectionnant le premier élément (index 0).
        sexeeleve.setSelectedIndex(0);
        // Réinitialise la liste déroulante 'sexeeleve' (le sexe) en sélectionnant le premier élément (index 0).
        teleeleve.setText("");
        // Définit le texte du champ 'teleeleve' (le numéro de téléphone) comme une chaîne vide, l'effaçant.
    }//GEN-LAST:event_btneleve7MouseClicked

    private void btnrechercherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnrechercherMouseClicked
        // TODO add your handling code here:
       
 
   
        // Déclare une variable entière 'matricule' et l'initialise à 0.

        String matriculeText = champRecherche.getText().trim(); 
        // Récupère le texte du champ de saisie 'ideleve' (le matricule potentiel) et supprime les espaces blancs au début et à la fin.

        if (matriculeText.isEmpty()) {
        // Vérifie si la chaîne de caractères du matricule est vide (après suppression des espaces).
            JOptionPane.showMessageDialog(null, "Veuillez entrer un matricule pour la recherche.", "Erreur de saisie", JOptionPane.WARNING_MESSAGE);
            // Affiche une boîte de dialogue d'avertissement demandant à l'utilisateur de saisir une valeur.
            return; 
            // Quitte la méthode si le champ de matricule est vide.
        }

        

        try (Connection con = DatabaseManager.getConnection()) {
        // Début du bloc try-with-resources: établit une connexion à la base de données via 'DatabaseManager.getConnection()' (connexion sera fermée automatiquement).

            // Requête SELECT
            // Définit la requête SQL pour sélectionner toutes les colonnes de la table 'tb_eleve' en filtrant par le matricule.
            String sql = "SELECT * FROM tb_eleve WHERE matricule = ? OR nom LIKE ?";
            // Prépare l'objet PreparedStatement pour exécuter la requête avec un paramètre sécurisé.
            PreparedStatement ps = con.prepareStatement(sql);
            // Définit la valeur du premier paramètre de la requête (?) comme étant la valeur entière de 'matricule'.
            ps.setString(1, matriculeText);
            // Définit le deuxième paramètre (?) par la chaîne de recherche (pour le nom).
            ps.setString(2, matriculeText + "%");

            // Exécute la requête SELECT et stocke le résultat dans l'objet ResultSet.
            ResultSet Rs = ps.executeQuery();

            // Vérifie si le ResultSet contient au moins une ligne (c'est-à-dire si un élève a été trouvé).
            if (Rs.next()) {
                // Remplissage des champs du formulaire
                // Remplit le champ 'ideleve' avec le matricule trouvé (conversion en String, même si déjà saisi).
                ideleve.setText(Rs.getString("matricule"));
                // Remplit le champ 'nomeleve' avec le nom de l'élève trouvé.
                nomeleve.setText(Rs.getString("nom"));
                // Remplit le champ 'post_nomeleve' avec le post-nom de l'élève trouvé.
                post_nomeleve.setText(Rs.getString("post_nom"));
                // Commentaire pour rappeler la vérification des colonnes et du nom du composant JComboBox.
                // Assurez-vous que ces colonnes existent et que le JComboBox est correctement nommé
                // Sélectionne l'élément correspondant à la classe trouvée dans la JComboBox 'comboeleve'.
                comboeleve.setSelectedItem(Rs.getString("classe")); 
                // Sélectionne l'élément correspondant au sexe trouvé dans la JComboBox 'sexeeleve'.
                sexeeleve.setSelectedItem(Rs.getString("sexe"));
                // Remplit le champ 'teleeleve' avec le numéro de téléphone trouvé.
                teleeleve.setText(Rs.getString("telephone"));
            } else {
                // Si aucune ligne n'est trouvée (l'élève n'existe pas).
                JOptionPane.showMessageDialog(null, "Matricule n'existe pas dans la base de données.", null, JOptionPane.ERROR_MESSAGE);
                // Affiche un message d'erreur indiquant que le matricule est inconnu.
                // Vider les champs si l'élève n'est pas trouvé
                // Efface le contenu du champ 'nomeleve'.
                nomeleve.setText("");
                // Efface le contenu du champ 'post_nomeleve'.
                post_nomeleve.setText("");
                // Tente de sélectionner un élément vide dans la JComboBox 'sexeeleve' (potentiellement une erreur si l'élément "" n'existe pas).
                sexeeleve.setSelectedItem("");
                // Efface le contenu du champ 'teleeleve'.
                teleeleve.setText("");
            }

        } catch (SQLException e) {
        // Capture l'exception si une erreur SQL se produit (connexion, requête, etc.).
            e.printStackTrace(); 
            // Affiche la trace complète de l'erreur dans la console pour le débogage.
            JOptionPane.showMessageDialog(null, "Erreur lors de la recherche DB: " + e.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
            // Affiche un message d'erreur à l'utilisateur.
        }

    }//GEN-LAST:event_btnrechercherMouseClicked

    private void btneleve7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btneleve7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btneleve7ActionPerformed

public void afficherTable() {
    // Déclaration de la méthode publique 'afficherTable' qui met à jour les données du tableau des élèves.
    
    // Récupère le modèle de la JTable, qui gère les données affichées
    // Récupère l'objet DefaultTableModel associé à la JTable 'tb_eleve'.
    DefaultTableModel model = (DefaultTableModel) tb_eleve.getModel();

    // Vide le contenu actuel de la table pour repartir à zéro
    // Supprime toutes les lignes existantes dans le modèle, ce qui vide la JTable à l'écran.
    model.setRowCount(0);

    // Début d'un bloc try pour gérer les opérations de base de données (connexion, requête).
    try {
        // Connexion à la base de données MySQL 'gestion_des_eleve' avec utilisateur 'root' sans mot de passe
        // Établit la connexion à la base de données 'frais_scolaire'.
        con = DriverManager.getConnection("jdbc:mysql://localhost/frais_scolaire", "root", "");

        // Requête SQL simple pour récupérer toutes les données de la table 'gestion_eleve'
        // Définit la requête SQL pour sélectionner toutes les colonnes de la table 'tb_eleve'.
        String sql = "SELECT * FROM tb_eleve";

        // Préparation de la requête (permet aussi de sécuriser les paramètres)
        // Crée un PreparedStatement pour la requête SQL.
        ps = con.prepareStatement(sql);

        // Exécution de la requête et récupération du résultat
        // Exécute la requête SELECT et stocke les résultats dans l'objet ResultSet 'Rs'.
        Rs = ps.executeQuery();

        // Parcours de toutes les lignes récupérées
        // Boucle pour itérer sur chaque ligne (chaque élève) retournée par la base de données.
        while (Rs.next()) {
            // Création d'un tableau d'objets représentant une ligne de la table
            // Crée un tableau pour stocker les valeurs d'une ligne d'élève.
            Object[] row = {
                Rs.getString("matricule"),        // Récupère le matricule de l'élève.
                Rs.getString("nom"),             // Récupère le nom de l'élève.
                Rs.getString("post_nom"),         // Récupère le post-nom de l'élève.
                Rs.getString("classe"),           // Récupère la classe de l'élève.
                Rs.getString("sexe"),             // Récupère le sexe de l'élève.
                Rs.getString("telephone"),        // Récupère le numéro de téléphone de l'élève.
                
            };
            // Ajoute la ligne dans le modèle de la JTable (ajoute donc une ligne visible)
            // Ajoute le tableau d'objets (la nouvelle ligne) au modèle de la JTable.
            model.addRow(row);
        }

    // Capture de l'exception si une erreur SQL se produit.
    } catch (SQLException ex) {
        // En cas d'erreur SQL, affiche la trace dans la console pour le débogage
        // Affiche la trace complète de l'erreur dans la console.
        ex.printStackTrace();
    }
    // Note: Il serait bon d'ajouter ici un bloc 'finally' pour fermer explicitement 'con', 'ps', et 'Rs'.

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btneleve6;
    private javax.swing.JButton btneleve7;
    private javax.swing.JButton btnenregistrer;
    private javax.swing.JButton btnmodifier;
    private javax.swing.JButton btnrechercher;
    private javax.swing.JTextField champRecherche;
    private javax.swing.JComboBox comboeleve;
    private javax.swing.JTextField ideleve;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblfraistotaux;
    private javax.swing.JLabel lblfraistotaux1;
    private javax.swing.JLabel lblfraistotaux2;
    private javax.swing.JLabel lblfraistotaux3;
    private javax.swing.JLabel lblfraistotaux4;
    private javax.swing.JLabel lblfraistotaux5;
    private javax.swing.JTextField nomeleve;
    private javax.swing.JTextField post_nomeleve;
    private javax.swing.JComboBox sexeeleve;
    private javax.swing.JTable tb_eleve;
    private javax.swing.JTextField teleeleve;
    // End of variables declaration//GEN-END:variables
}


