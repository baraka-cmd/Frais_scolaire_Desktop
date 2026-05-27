package gestionfrais;



// --- Imports de la création de PDF (iText) ---
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

// --- Imports de l'Interface Graphique et Fenêtrage (Swing/AWT) ---
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities; // Nécessaire si vous utilisez getWindowAncestor
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font; // <--- C'EST L'IMPORT java.awt.Font qui est utilisé pour vos boutons/labels
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

// --- Imports de Java Standard (utilitaires, fichiers, BD) ---
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


// --- Imports de la création de PDF (iText) ---
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

// --- Imports de l'Interface Graphique et Fenêtrage (Swing/AWT) ---
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities; 
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font; // <-- SEULEMENT java.awt.Font
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

// --- Imports de Java Standard (utilitaires, fichiers, BD) ---
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.filechooser.FileNameExtensionFilter;


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities; 
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font; // SEULEMENT java.awt.Font
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArchiveViewerPanel extends JPanel {

    private final JFrame parentFrame; 
    private final Runnable returnAction; 
    
    // Composants d'interface
    private JComboBox<String> comboAnneeFiltre;
    private JComboBox<String> comboClasseFiltre;
    private JComboBox<String> comboSexeFiltre;
    private JTable tableArchives;
    private JTable tableHistorique;
    private JTable tableStats;
    private DefaultTableModel modelArchives;
    private DefaultTableModel modelHistorique;
    private DefaultTableModel modelStats;

    public ArchiveViewerPanel(JFrame parentFrame, Runnable returnAction) {
        this.parentFrame = parentFrame;
        this.returnAction = returnAction;
        
        initComponents();
        chargerFiltresAnnees();
        chargerFiltresClassesEtSexe(); 
        chargerArchives(null, null, null); 
    }
    
    public ArchiveViewerPanel() {
        this(null, () -> {
            JOptionPane.showMessageDialog(null, "Action de retour non définie.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }); 
    }

    private void initComponents() {
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        // MODIFICATION: Augmentation de la largeur à 1300 pour accommoder la table
        this.setPreferredSize(new Dimension(1300, 750)); 
        this.setLayout(new BorderLayout(10, 10));
        
        // --- Panneau de Contrôle et Filtres (Nord) ---
        JPanel controlPanel = new JPanel(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.anchor = GridBagConstraints.WEST;
        
        // Initialisation des composants
        comboAnneeFiltre = new JComboBox<>();
        comboClasseFiltre = new JComboBox<>();
        comboSexeFiltre = new JComboBox<>(new String[]{"Tous", "Masculin", "Feminin"});
        
        JButton btnFiltrer = createStyledButton("Filtrer", new Color(0, 123, 255), Color.WHITE);
        JButton btnExportPDF = createStyledButton("Exporter PDF", new Color(220, 53, 69), Color.WHITE);
        JButton btnExportExcel = createStyledButton("Exporter Excel", new Color(40, 167, 69), Color.WHITE);
        JButton btnRetour = createStyledButton("Retour", new Color(108, 117, 125), Color.WHITE); 

        int col = 0;
        
        // Polices pour les labels
        Font labelFont = new Font("Times new roman", Font.BOLD, 16);
        
        // 1. Filtres
        gbc.gridx = col++; gbc.gridy = 0; JLabel anneeLabel = new JLabel("Année:"); anneeLabel.setFont(labelFont); controlPanel.add(anneeLabel, gbc);
        gbc.gridx = col++; gbc.gridy = 0; controlPanel.add(comboAnneeFiltre, gbc);
        gbc.gridx = col++; gbc.gridy = 0; JLabel classeLabel = new JLabel("Classe:"); classeLabel.setFont(labelFont); controlPanel.add(classeLabel, gbc);
        gbc.gridx = col++; gbc.gridy = 0; controlPanel.add(comboClasseFiltre, gbc);
        gbc.gridx = col++; gbc.gridy = 0; JLabel sexeLabel = new JLabel("Sexe:"); sexeLabel.setFont(labelFont); controlPanel.add(sexeLabel, gbc);
        gbc.gridx = col++; gbc.gridy = 0; controlPanel.add(comboSexeFiltre, gbc);

        // 2. Boutons d'Action
        gbc.gridx = col++; gbc.gridy = 0; controlPanel.add(btnFiltrer, gbc);
        gbc.gridx = col++; gbc.gridy = 0; controlPanel.add(btnExportPDF, gbc);
        gbc.gridx = col++; gbc.gridy = 0; controlPanel.add(btnExportExcel, gbc);
        
        // 3. Bouton Retour 
        gbc.gridx = col++; gbc.gridy = 0; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.EAST; 
        controlPanel.add(btnRetour, gbc);
        
        this.add(controlPanel, BorderLayout.NORTH);
        
        // --- Panneau Central (Tables et Historique) ---
        
        modelArchives = new DefaultTableModel(new Object[]{
            "Matricule", "Nom", "Post-nom", "Téléphone", "Sexe", "Classe", "Année Scolaire", "Frais Total", "Montant Payé", "Reste à Payer", "Date Archivage"
        }, 0);
        tableArchives = new JTable(modelArchives);
        tableArchives.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); 
        
        TableColumnModel tcm = tableArchives.getColumnModel();
        // Ajustement des largeurs de colonnes pour la nouvelle largeur de fenêtre (1300)
        tcm.getColumn(0).setPreferredWidth(100);  // Matricule
        tcm.getColumn(1).setPreferredWidth(140);  // Nom
        tcm.getColumn(2).setPreferredWidth(140);  // Post-nom
        tcm.getColumn(3).setPreferredWidth(100);  // Téléphone
        tcm.getColumn(6).setPreferredWidth(120);  // Année Scolaire
        tcm.getColumn(7).setPreferredWidth(110);  // Frais Total
        tcm.getColumn(8).setPreferredWidth(110);  // Montant Payé
        tcm.getColumn(9).setPreferredWidth(110);  // Reste à Payer
        tcm.getColumn(10).setPreferredWidth(120); // Date Archivage (Nouvelle largeur)


        modelHistorique = new DefaultTableModel(new Object[]{"Date", "Montant Versé", "Solde après"}, 0);
        tableHistorique = new JTable(modelHistorique);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.75); 
        splitPane.setDividerSize(5); 
        splitPane.setTopComponent(new JScrollPane(tableArchives));
        splitPane.setBottomComponent(new JScrollPane(tableHistorique));
        
        this.add(splitPane, BorderLayout.CENTER);
        
        // --- Panneau Sud (Statistiques en JTable Détaillée) ---
        
        modelStats = new DefaultTableModel(new Object[]{"Indicateur", "Valeur"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableStats = new JTable(modelStats);
        tableStats.setEnabled(false); 
        
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JScrollPane statsScrollPane = new JScrollPane(tableStats);
        
        statsScrollPane.setPreferredSize(new Dimension(600, 150)); 
        statsScrollPane.setBorder(BorderFactory.createTitledBorder("Statistiques Détaillées du Filtre"));
        
        statsPanel.add(statsScrollPane);
        this.add(statsPanel, BorderLayout.SOUTH); 
        
        // --- Listeners ---
        btnFiltrer.addActionListener(e -> filtrerArchives());
        btnExportPDF.addActionListener(e -> exportToPDF());
        btnExportExcel.addActionListener(e -> exportToExcel());
        btnRetour.addActionListener(e -> handleReturnAction()); 
        
        tableArchives.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                afficherHistoriqueEleve();
            }
        });
    }
    
    private JButton createStyledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false); 
        button.setFont(new Font("Times new roman", Font.BOLD, 16)); 
        button.setPreferredSize(new Dimension(140, 35)); 
        return button;
    }
    
    private void handleReturnAction() {
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        if (currentFrame != null) {
            currentFrame.dispose(); 
        } else if (parentFrame != null) {
            parentFrame.dispose();
        }
        
        if (returnAction != null) {
            returnAction.run(); 
        } else {
            System.err.println("Erreur: Action de retour non définie ou fenêtre parente introuvable.");
        }
    }


    // MÉTHODES DE BASE DE DONNÉES 
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://localhost/frais_scolaire",
            "root",
            ""
        );
    }
    
    private void chargerArchives(String annee, String classe, String sexe) {
        
        modelArchives.setRowCount(0);
        modelHistorique.setRowCount(0);
        modelStats.setRowCount(0); 
        
        StringBuilder sql = new StringBuilder(
            "SELECT matricule_eleve, nom, postNom, telephone, sexe, classe_annee_archive, annee_scolaire, " +
            "frais_total_annuel, montant_paye_final, reste_a_payer_final, date_archivage " +
            "FROM tb_archive_eleves WHERE 1=1" 
        );
        Vector<Object> params = new Vector<>();
        
        if (annee != null && !annee.equals("Tous")) {
            sql.append(" AND annee_scolaire = ?"); 
            params.add(annee); 
        }
        if (classe != null && !classe.equals("Toutes")) {
            sql.append(" AND classe_annee_archive = ?");
            params.add(classe);
        }
        if (sexe != null && !sexe.equals("Tous")) {
            sql.append(" AND sexe = ?");
            params.add(sexe);
        }

        double totalFraisAttendu = 0.0;
        double totalPayeFiltre = 0.0;
        double totalDette = 0.0;      
        int rows = 0;
        int totalMale = 0;            
        int totalFemale = 0;          
        
        try (Connection con = getConnection();
              PreparedStatement ps = con.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet Rs = ps.executeQuery()) {
                while (Rs.next()) {
                    rows++;
                    
                    totalFraisAttendu += Rs.getDouble("frais_total_annuel");
                    totalPayeFiltre += Rs.getDouble("montant_paye_final");
                    totalDette += Rs.getDouble("reste_a_payer_final");
                    
                    String studentSexe = Rs.getString("sexe");
                    if ("Masculin".equalsIgnoreCase(studentSexe)) {
                        totalMale++;
                    } else if ("Feminin".equalsIgnoreCase(studentSexe)) {
                        totalFemale++;
                    }
                    
                    modelArchives.addRow(new Object[]{
                        Rs.getString("matricule_eleve"),
                        Rs.getString("nom"),
                        Rs.getString("postNom"),
                        Rs.getString("telephone"), 
                        studentSexe,
                        Rs.getString("classe_annee_archive"),
                        Rs.getString("annee_scolaire"), 
                        String.format("%.2f", Rs.getDouble("frais_total_annuel")),
                        String.format("%.2f", Rs.getDouble("montant_paye_final")),
                        String.format("%.2f", Rs.getDouble("reste_a_payer_final")),
                        Rs.getDate("date_archivage")
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement des archives: " + ex.getMessage(), "Erreur BD", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(ArchiveViewerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            double tauxRecouvrement = (totalFraisAttendu > 0) ? (totalPayeFiltre / totalFraisAttendu) : 0.0;

            modelStats.addRow(new Object[]{"Total Élèves (Tous)", rows});
            modelStats.addRow(new Object[]{"Total Garçons", totalMale});
            modelStats.addRow(new Object[]{"Total Filles", totalFemale});
            modelStats.addRow(new Object[]{"-------------------", "-------------------"});
            modelStats.addRow(new Object[]{"Frais Total Attendu", String.format("%,.2f USD", totalFraisAttendu)});
            modelStats.addRow(new Object[]{"Montant Total Payé", String.format("%,.2f USD", totalPayeFiltre)});
            modelStats.addRow(new Object[]{"Reste à Payer (Dette)", String.format("%,.2f USD", totalDette)});
            modelStats.addRow(new Object[]{"Taux de Recouvrement", String.format("%,.1f%%", tauxRecouvrement * 100)});
        }
    }
    
    private void afficherHistoriqueEleve() {
        int selectedRow = tableArchives.getSelectedRow();
        modelHistorique.setRowCount(0);
        
        if (selectedRow == -1) {
            return;
        }

        String matricule = modelArchives.getValueAt(selectedRow, 0).toString(); 
        String annee = modelArchives.getValueAt(selectedRow, 6).toString();   
        
        String sql = "SELECT date_paiement, montant_verse, montant_total_paye FROM tb_archive_historique WHERE matricule_eleve = ? AND annee_scolaire = ? ORDER BY date_paiement ASC";
        
        try (Connection con = getConnection();
              PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, matricule); 
            ps.setString(2, annee);     
            
            try (ResultSet Rs = ps.executeQuery()) {
                while (Rs.next()) {
                    modelHistorique.addRow(new Object[]{
                        Rs.getString("date_paiement"),
                        String.format("%.2f", Rs.getDouble("montant_verse")), 
                        String.format("%.2f", Rs.getDouble("montant_total_paye"))
                    });
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ArchiveViewerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void filtrerArchives() {
        String annee = (String) comboAnneeFiltre.getSelectedItem();
        String classe = (String) comboClasseFiltre.getSelectedItem();
        String sexe = (String) comboSexeFiltre.getSelectedItem();
        chargerArchives(annee, classe, sexe);
    }
    
    private void chargerFiltresAnnees() {
        comboAnneeFiltre.addItem("Tous");
        String sql = "SELECT DISTINCT annee_scolaire FROM tb_archive_eleves ORDER BY annee_scolaire DESC";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet Rs = ps.executeQuery()) {
            while (Rs.next()) {
                comboAnneeFiltre.addItem(Rs.getString("annee_scolaire"));
            }
        } catch (SQLException ex) {
            System.err.println("Erreur de chargement des années d'archive: " + ex.getMessage());
        }
    }
    
    private void chargerFiltresClassesEtSexe() {
        comboClasseFiltre.addItem("Toutes");
        String sql = "SELECT DISTINCT classe_annee_archive FROM tb_archive_eleves ORDER BY classe_annee_archive ASC";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet Rs = ps.executeQuery()) {
            while (Rs.next()) {
                comboClasseFiltre.addItem(Rs.getString("classe_annee_archive"));
            }
        } catch (SQLException ex) {
            System.err.println("Erreur de chargement des classes d'archive: " + ex.getMessage());
        }
    }

    private void exportToPDF() {
    
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer le Relevé d'Archives PDF");
        fileChooser.setSelectedFile(new File("Archives_Frais_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".pdf"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Documents (*.pdf)", "pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            if (!outputFile.getName().toLowerCase().endsWith(".pdf")) {
                outputFile = new File(outputFile.getAbsolutePath() + ".pdf");
            }
            
            try {
                Document document = new Document(PageSize.A4.rotate()); 
                PdfWriter.getInstance(document, new FileOutputStream(outputFile));
                document.open();

                // Utilisation du nom complet 'com.itextpdf.text.Font' pour éviter le conflit avec java.awt.Font
                com.itextpdf.text.Font fontTitre = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 16, 
                    com.itextpdf.text.Font.BOLD, BaseColor.BLUE
                );
                com.itextpdf.text.Font fontEntete = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 8, 
                    com.itextpdf.text.Font.BOLD
                );
                com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 7, 
                    com.itextpdf.text.Font.NORMAL
                );
                
                Paragraph titre = new Paragraph("Rapport d'Archive des Frais Scolaires - Année: " + comboAnneeFiltre.getSelectedItem(), fontTitre);
                titre.setAlignment(Element.ALIGN_CENTER);
                document.add(titre);
                document.add(new Paragraph("Date d'édition : " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), fontNormal));
                document.add(new Paragraph("\n"));

                PdfPTable table = new PdfPTable(modelArchives.getColumnCount());
                table.setWidthPercentage(100);
                
                for (int i = 0; i < modelArchives.getColumnCount(); i++) {
                    PdfPCell header = new PdfPCell(new Phrase(modelArchives.getColumnName(i), fontEntete));
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    table.addCell(header);
                }

                for (int i = 0; i < modelArchives.getRowCount(); i++) {
                    for (int j = 0; j < modelArchives.getColumnCount(); j++) {
                        table.addCell(new Phrase(modelArchives.getValueAt(i, j).toString(), fontNormal));
                    }
                }
                
                document.add(table);
                
                document.add(new Paragraph("\nStatistiques Détaillées du Filtre:", fontEntete));
                PdfPTable statsPdfTable = new PdfPTable(2);
                statsPdfTable.setWidths(new float[]{0.5f, 0.5f});
                statsPdfTable.setWidthPercentage(40); 
                statsPdfTable.setHorizontalAlignment(Element.ALIGN_LEFT);
                
                for (int i = 0; i < modelStats.getRowCount(); i++) {
                     statsPdfTable.addCell(new PdfPCell(new Phrase(modelStats.getValueAt(i, 0).toString(), fontEntete)));
                     statsPdfTable.addCell(new PdfPCell(new Phrase(modelStats.getValueAt(i, 1).toString(), fontNormal)));
                }

                document.add(statsPdfTable);

                document.close();
                JOptionPane.showMessageDialog(this, "Export PDF réussi sous : \n" + outputFile.getAbsolutePath(), "Succès", JOptionPane.INFORMATION_MESSAGE);
                Desktop.getDesktop().open(outputFile);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'export PDF: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer les Archives en CSV (compatible Excel)");
        fileChooser.setSelectedFile(new File("Archives_Frais_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers CSV (*.csv)", "csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            if (!outputFile.getName().toLowerCase().endsWith(".csv")) {
                outputFile = new File(outputFile.getAbsolutePath() + ".csv");
            }
            
            try (PrintWriter writer = new PrintWriter(outputFile)) {
                
                for (int i = 0; i < modelArchives.getColumnCount(); i++) {
                    writer.print(modelArchives.getColumnName(i) + (i == modelArchives.getColumnCount() - 1 ? "" : ";"));
                }
                writer.println();
                
                for (int i = 0; i < modelArchives.getRowCount(); i++) {
                    for (int j = 0; j < modelArchives.getColumnCount(); j++) {
                        writer.print(modelArchives.getValueAt(i, j).toString().replace(";", ",") + (j == modelArchives.getColumnCount() - 1 ? "" : ";")); 
                    }
                    writer.println();
                }
                
                writer.println();
                writer.println("Statistiques Détaillées du Filtre");
                for (int i = 0; i < modelStats.getRowCount(); i++) {
                    writer.println(modelStats.getValueAt(i, 0) + ";" + modelStats.getValueAt(i, 1));
                }

                JOptionPane.showMessageDialog(this, "Export CSV (Excel) réussi sous : \n" + outputFile.getAbsolutePath(), "Succès", JOptionPane.INFORMATION_MESSAGE);
                Desktop.getDesktop().open(outputFile);
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'export Excel/CSV: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}