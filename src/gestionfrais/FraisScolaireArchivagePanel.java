package gestionfrais;

import gestionfrais.FraisScolaireArchivagePanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panneau gérant l'archivage COMPLET des données de la base 'frais_scolaire'.
 * Archive et vide les tables : tb_eleve, tb_frais, tb_historique_paiement.
 */
public class FraisScolaireArchivagePanel extends JPanel {

    // Connexion à la base de données 'frais_scolaire'
    private static final String DB_URL = "jdbc:mysql://localhost/frais_scolaire"; 
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final Logger LOGGER = Logger.getLogger(FraisScolaireArchivagePanel.class.getName());

    public FraisScolaireArchivagePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240)); 
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        buttonPanel.setBackground(new Color(240, 240, 240));

        // Boutons
        JButton btnArchiver = createStyledButton("ARCHIVER TOUTES LES DONNÉES", new Color(255, 153, 51));
        JButton btnVueArchive = createStyledButton("VOIR TOUTES LES ARCHIVES", new Color(153, 204, 255));
        
        buttonPanel.add(btnArchiver);
        buttonPanel.add(btnVueArchive);
        
        this.add(buttonPanel, BorderLayout.NORTH);

        btnArchiver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int initialConfirm = JOptionPane.showConfirmDialog(null,
                        "ATTENTION : Cette opération va archiver et vider les tables tb_eleve, tb_frais et tb_historique_paiement.\nÊtes-vous sûr de vouloir commencer l'archivage annuel ?",
                        "Confirmation d'Archivage Complet",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (initialConfirm == JOptionPane.YES_OPTION) {
                    archiverToutesLesDonnees();
                }
            }
        });
        
        btnVueArchive.addActionListener(e -> {
            new ArchiveViewerPanel().setVisible(true);
        });
    }
    
    private JButton createStyledButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(350, 45));
        button.setBackground(background);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Cambria", Font.BOLD, 16));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        return button;
    }

    /**
     * Exécute l'archivage de masse des trois tables : eleves, frais, historique paiement.
     */
    private void archiverToutesLesDonnees() {
        int totalCount = 0;
        Connection con = null;

        try {
            // 1. Connexion et début de la transaction
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            con.setAutoCommit(false); // Démarrage de la transaction

            // 2. Archivage des tables (dans l'ordre pour respecter les dépendances s'il y a lieu, bien que la suppression soit à la fin)
            // tb_eleve -> tb_eleve_archive (matricule, nom, post_nom, prenom, date_naissance, lieu_naissance, sexe, telephone_tuteur, adresse, promotion)
            totalCount += archiverTable(con, 
                "tb_eleve", 
                "tb_eleve_archive", 
                "matricule, nom, post_nom, prenom, date_naissance, lieu_naissance, sexe, telephone_tuteur, adresse, promotion");

            // tb_frais -> tb_frais_archive (id_frais, matricule_eleve, frais_total, montant_paye, reste_a_payer, annee_scolaire)
            totalCount += archiverTable(con, 
                "tb_frais", 
                "tb_frais_archive", 
                "id_frais, matricule_eleve, frais_total, montant_paye, reste_a_payer, annee_scolaire");
                
            // tb_historique_paiement -> tb_historique_paiement_archive (id_paiement, id_frais, montant, date_paiement)
            totalCount += archiverTable(con, 
                "tb_historique_paiement", 
                "tb_historique_paiement_archive", 
                "id_paiement, id_frais, montant, date_paiement");
            
            
            // 3. Validation de la transaction (si aucune erreur n'a été levée)
            if (totalCount > 0) {
                con.commit(); // Valide TOUTES les insertions et suppressions
                JOptionPane.showMessageDialog(null, 
                    "Archivage complet réussi ! " + totalCount + " enregistrement(s) déplacés et tables principales vidées pour la nouvelle année.", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Si aucune donnée n'a été trouvée dans les tables
                con.rollback(); // Annule toute modification (bien qu'il n'y en ait pas eu)
                JOptionPane.showMessageDialog(null, "Aucune donnée à archiver dans les tables principales.", "Information", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de l'archivage complet. Tentative de ROLLBACK.", ex);
            try {
                if (con != null) {
                    con.rollback(); // Annule TOUTE l'opération en cas d'erreur
                    LOGGER.info("ROLLBACK effectué. Les données sont intactes.");
                }
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Erreur lors du rollback.", rollbackEx);
            }
            JOptionPane.showMessageDialog(null, "Erreur grave lors de l'archivage. Aucune modification n'a été appliquée (ROLLBACK).", "Erreur Transactionnelle", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Rétablir et fermer la connexion
            try {
                if (con != null) con.setAutoCommit(true);
                if (con != null) con.close();
            } catch (SQLException closeEx) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la fermeture de la connexion.", closeEx);
            }
        }
    }
    
    /**
     * Méthode utilitaire pour archiver et vider une seule table.
     * @param con La connexion SQL active (en mode transactionnel).
     * @param sourceTable Nom de la table source (ex: tb_eleve).
     * @param archiveTable Nom de la table d'archive (ex: tb_eleve_archive).
     * @param columnList Liste des colonnes séparées par des virgules (ex: col1, col2, col3).
     * @return Le nombre d'enregistrements archivés.
     */
    private int archiverTable(Connection con, String sourceTable, String archiveTable, String columnList) throws SQLException {
        int rowCount = 0;
        
        // 1. Création des requêtes dynamiques
        String sqlSelect = "SELECT " + columnList + " FROM " + sourceTable;
        
        // Créer les placeholders pour les valeurs + un pour NOW()
        String[] columns = columnList.split(", ");
        String placeholders = "?, ".repeat(columns.length).substring(0, columns.length * 3 - 2); // Ex: ?, ?, ?
        
        String sqlInsert = "INSERT INTO " + archiveTable + 
                           " (" + columnList + ", date_archivage) " +
                           "VALUES (" + placeholders + ", NOW())"; // Ajout de date_archivage

        // 2. Sélectionner les données
        try (PreparedStatement pstSelect = con.prepareStatement(sqlSelect);
             ResultSet rs = pstSelect.executeQuery()) {
            
            // 3. Insérer les données dans l'archive
            try (PreparedStatement pstInsert = con.prepareStatement(sqlInsert)) {
                while (rs.next()) {
                    for (int i = 1; i <= columns.length; i++) {
                        // Utilise setObject pour gérer différents types de données (String, Int, Double, Date...)
                        pstInsert.setObject(i, rs.getObject(i)); 
                    }
                    pstInsert.executeUpdate();
                    rowCount++;
                }
            }
        } // Les PreparedStatement et ResultSet se ferment ici
        
        // 4. Suppression des données de la table source SI archivage réussi
        if (rowCount > 0) {
            try (PreparedStatement pstDelete = con.prepareStatement("DELETE FROM " + sourceTable)) {
                pstDelete.executeUpdate();
                LOGGER.info(rowCount + " enregistrements déplacés de " + sourceTable + " vers " + archiveTable);
            }
        }
        
        return rowCount;
    }
}