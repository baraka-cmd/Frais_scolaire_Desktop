package gestionfrais; // Définit le package du projet

import java.awt.Color; // Import pour la gestion des couleurs (graphiques)
import java.awt.image.BufferedImage; // Import pour manipuler l'image du graphique en mémoire
import java.io.ByteArrayOutputStream; // Import pour convertir l'image en flux de données
import java.io.File; // Import pour manipuler les fichiers physiques
import java.io.FileOutputStream; // Import pour écrire les données dans le fichier Excel
import java.sql.*; // Import pour la connexion et les requêtes SQL
import java.util.*; // Import pour les listes et les maps (stockage de données)
import javax.imageio.ImageIO; // Import pour encoder l'image au format PNG
import javax.swing.JFileChooser; // Import pour ouvrir la fenêtre de sauvegarde
import javax.swing.JOptionPane; // Import pour afficher des messages à l'utilisateur
import javax.swing.filechooser.FileNameExtensionFilter; // Import pour filtrer les fichiers .xlsx

// Apache POI (Excel)
import org.apache.poi.ss.usermodel.*; // Import des classes de base d'Apache POI
import org.apache.poi.xssf.usermodel.XSSFWorkbook; // Import pour créer des fichiers Excel format .xlsx
import org.apache.poi.ss.util.CellRangeAddress; // Import pour fusionner des cellules
import org.apache.poi.xssf.usermodel.XSSFSheet; // Import pour les fonctions spécifiques aux feuilles XLSX

// JFreeChart (Graphiques)
import org.jfree.chart.ChartFactory; // Import pour créer les types de graphiques
import org.jfree.chart.JFreeChart; // Import de la classe principale de graphique
import org.jfree.chart.plot.PiePlot; // Import pour personnaliser les camemberts
import org.jfree.data.general.DefaultPieDataset; // Import pour fournir les données au graphique

public class RapportGeneralExport { // Déclaration de la classe principale

    private static final String DB_URL = "jdbc:mysql://localhost/frais_scolaire"; // Adresse de la base de données
    private static final String DB_USER = "root"; // Nom d'utilisateur de la base de données
    private static final String DB_PASSWORD = ""; // Mot de passe de la base de données
    private int currentRow = 0; // Compteur pour suivre la ligne actuelle dans Excel

    // Couleurs Institutionnelles
    private static final Color COLOR_PAID = new Color(39, 174, 96);  // Couleur verte pour les paiements
    private static final Color COLOR_DEBT = new Color(192, 57, 43);  // Couleur rouge pour les dettes
    private static final Color COLOR_BOY = new Color(52, 152, 219);   // Couleur bleue pour les garçons
    private static final Color COLOR_GIRL = new Color(231, 76, 60);   // Couleur rose/rouge pour les filles

    private static class ReportData { // Classe interne pour regrouper les données récupérées
        int totalMale, totalFemale, totalStudents, totalClasses; // Variables pour les effectifs
        double expectedFees, paidFees, totalDebt; // Variables pour les montants financiers
        List<Map<String, Object>> classesData = new ArrayList<>(); // Liste pour stocker les détails par classe
    } // Fin de la classe interne

    public void executeReportExport() { // Méthode principale de lancement de l'export
        ReportData data = fetchData(); // Récupération des données depuis la base de données
        if (data == null) return; // Arrêt si les données sont vides ou si erreur SQL

        JFileChooser fileChooser = new JFileChooser(); // Création de l'explorateur de fichiers
        fileChooser.setFileFilter(new FileNameExtensionFilter("Classeur Excel (*.xlsx)", "xlsx")); // Filtre XLSX
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) { // Si l'utilisateur valide l'enregistrement
            File file = fileChooser.getSelectedFile(); // Récupération du fichier sélectionné
            if (!file.getName().endsWith(".xlsx")) file = new File(file + ".xlsx"); // Ajout de l'extension si manquante

            try (Workbook workbook = new XSSFWorkbook()) { // Création du classeur Excel en mémoire
                generateFullReport(workbook, data); // Appel de la génération du contenu
                try (FileOutputStream out = new FileOutputStream(file)) { // Ouverture du flux d'écriture
                    workbook.write(out); // Écriture des données dans le fichier
                    JOptionPane.showMessageDialog(null, "Rapport Dashboard généré avec succès !"); // Notification de succès
                } // Fermeture automatique du flux d'écriture
            } catch (Exception e) { // Gestion des erreurs potentielles
                e.printStackTrace(); // Affichage de l'erreur en console
                JOptionPane.showMessageDialog(null, "Erreur d'exportation : " + e.getMessage()); // Notification d'erreur
            } // Fin du bloc try-with-resources
        } // Fin du test de sélection de fichier
    } // Fin de la méthode executeReportExport

    private void generateFullReport(Workbook wb, ReportData data) { // Méthode de création du contenu Excel
        Sheet sheet = wb.createSheet("Dashboard Général"); // Création de l'onglet "Dashboard Général"
        sheet.setDisplayGridlines(false); // Désactivation de l'affichage de la grille par défaut
        currentRow = 0; // Réinitialisation de l'index de ligne

        // Styles
        CellStyle styleMainTitle = createStyle(wb, true, 22, IndexedColors.DARK_BLUE.getIndex(), false); // Style titre principal
        CellStyle styleSecHeader = createStyle(wb, true, 12, IndexedColors.WHITE.getIndex(), true); // Style en-tête de section
        CellStyle styleLabel = createStyle(wb, true, 11, IndexedColors.BLACK.getIndex(), false); // Style pour les libellés
        CellStyle styleInt = wb.createCellStyle(); styleInt.setDataFormat(wb.createDataFormat().getFormat("0")); // Style nombre entier
        CellStyle styleMoney = wb.createCellStyle(); styleMoney.setDataFormat(wb.createDataFormat().getFormat("#,##0")); // Style monétaire
        CellStyle stylePercent = wb.createCellStyle(); stylePercent.setDataFormat(wb.createDataFormat().getFormat("0.0%")); // Style pourcentage

        // --- TITRE PRINCIPAL ---
        Row rTitle = sheet.createRow(currentRow++); // Création de la première ligne
        Cell cTitle = rTitle.createCell(1); // Création de la cellule dans la colonne B
        cTitle.setCellValue("RAPPORT ANNUEL DE GESTION SCOLAIRE 2025-2026"); // Texte du titre
        cTitle.setCellStyle(styleMainTitle); // Application du style de titre
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 7)); // Fusion des cellules de B1 à H1
        currentRow += 2; // Saut de deux lignes

        // --- PARTIE 1 : STATISTIQUES DES EFFECTIFS ---
        addSectionTitle(sheet, "I. STATISTIQUES DES EFFECTIFS", styleSecHeader); // Ajout du titre de section I
        addInfoRow(sheet, "Effectif Total :", data.totalStudents, styleInt); // Ligne total élèves
        addInfoRow(sheet, "Total Garçons :", data.totalMale, styleInt); // Ligne total garçons
        addInfoRow(sheet, "Total Filles :", data.totalFemale, styleInt); // Ligne total filles
        addInfoRow(sheet, "Nombre de Classes :", data.totalClasses, styleInt); // Ligne nombre de classes
        
        DefaultPieDataset dsGenre = new DefaultPieDataset(); // Création du dataset pour le graphique genre
        dsGenre.setValue("Garçons", data.totalMale); // Valeur pour les garçons
        dsGenre.setValue("Filles", data.totalFemale); // Valeur pour les filles
        insertChart(wb, sheet, dsGenre, "Sexe", 5, currentRow - 4, 3, 4); // Insertion du graphique à droite des stats
        currentRow += 1; // Espacement

        // --- PARTIE 2 : RÉSUMÉ FINANCIER GLOBAL ---
        addSectionTitle(sheet, "II. RÉSUMÉ FINANCIER GLOBAL", styleSecHeader); // Ajout du titre de section II
        addInfoRow(sheet, "Total Frais Attendus :", data.expectedFees, styleMoney); // Ligne frais attendus
        addInfoRow(sheet, "Total Frais Payés :", data.paidFees, styleMoney); // Ligne frais payés
        addInfoRow(sheet, "Reste à Recouvrer :", data.totalDebt, styleMoney); // Ligne dette totale
        
        DefaultPieDataset dsGlob = new DefaultPieDataset(); // Création du dataset pour le graphique financier
        dsGlob.setValue("Payé", data.paidFees); // Valeur des paiements
        dsGlob.setValue("Dette", data.totalDebt); // Valeur des dettes
        insertChart(wb, sheet, dsGlob, "Finance", 5, currentRow - 3, 3, 3); // Insertion du graphique à droite
        currentRow += 1; // Espacement

        // --- PARTIE 3 : RÉPARTITION DÉTAILLÉE PAR CLASSE ---
        addSectionTitle(sheet, "III. ANALYSE DÉTAILLÉE PAR CLASSE", styleSecHeader); // Ajout du titre de section III
        
        // Légende des couleurs
        Row rLeg = sheet.createRow(currentRow++); // Création de la ligne pour la légende
        Cell cLeg = rLeg.createCell(1); // Cellule de la légende
        cLeg.setCellValue("LÉGENDE : VERT = PAYÉ | ROUGE = DETTE"); // Texte informatif de la légende
        styleLabel.setAlignment(HorizontalAlignment.LEFT); // Alignement à gauche
        cLeg.setCellStyle(styleLabel); // Application du style
        sheet.addMergedRegion(new CellRangeAddress(currentRow-1, currentRow-1, 1, 7)); // Fusion de la ligne légende

        String[] headers = {"CLASSE", "EFFECTIF", "ATTENDU", "PAYÉ", "DETTE", "TAUX %", "RECOUVREMENT"}; // Liste des en-têtes du tableau
        Row hr = sheet.createRow(currentRow++); // Création de la ligne d'en-tête
        hr.setHeightInPoints(25); // Définition de la hauteur de la ligne d'en-tête
        for (int i = 0; i < headers.length; i++) { // Boucle pour créer chaque cellule d'en-tête
            Cell c = hr.createCell(i + 1); // Création de la cellule
            c.setCellValue(headers[i]); // Injection du texte
            c.setCellStyle(styleSecHeader); // Application du style bleu/blanc
        } // Fin de boucle en-tête

        for (Map<String, Object> cData : data.classesData) { // Boucle sur chaque classe récupérée en BDD
            Row row = sheet.createRow(currentRow); // Création de la ligne pour la classe
            row.setHeightInPoints(60); // Hauteur augmentée pour loger le mini-graphique

            row.createCell(1).setCellValue((String) cData.get("nom")); // Nom de la classe
            Cell c2 = row.createCell(2); c2.setCellValue((int) cData.get("eff")); c2.setCellStyle(styleInt); // Effectif classe
            Cell c3 = row.createCell(3); c3.setCellValue((double) cData.get("att")); c3.setCellStyle(styleMoney); // Attendu financier
            Cell c4 = row.createCell(4); c4.setCellValue((double) cData.get("pay")); c4.setCellStyle(styleMoney); // Payé financier
            Cell c5 = row.createCell(5); c5.setCellValue((double) cData.get("det")); c5.setCellStyle(styleMoney); // Dette financière
            
            double tx = ((double) cData.get("att") > 0) ? (double) cData.get("pay") / (double) cData.get("att") : 0; // Calcul du taux
            Cell c6 = row.createCell(6); c6.setCellValue(tx); c6.setCellStyle(stylePercent); // Injection du taux formaté

            // Mini graphique par classe
            DefaultPieDataset dsClass = new DefaultPieDataset(); // Dataset pour le graphique de la classe
            dsClass.setValue("Payé", (double) cData.get("pay")); // Part payée
            dsClass.setValue("Dette", (double) cData.get("det")); // Part dette
            insertChart(wb, sheet, dsClass, "Finance", 7, currentRow, 1, 1); // Insertion du mini-camembert en colonne H

            currentRow++; // Passage à la ligne suivante
        } // Fin de boucle des classes
        
        // Protection de la feuille
        sheet.protectSheet("1234"); // Verrouillage de la feuille avec le mot de passe "1234"
        
        if (sheet instanceof XSSFSheet) { // Vérification du type de feuille pour accès aux options avancées
            XSSFSheet xssfsheet = (XSSFSheet) sheet; // Cast en XSSFSheet
            xssfsheet.lockDeleteColumns(true); // Interdiction de supprimer des colonnes
            xssfsheet.lockDeleteRows(true); // Interdiction de supprimer des lignes
            xssfsheet.lockInsertColumns(true); // Interdiction d'insérer des colonnes
            xssfsheet.lockInsertRows(true); // Interdiction d'insérer des lignes
            xssfsheet.getCTWorksheet().getSheetProtection().setSelectLockedCells(false); // Empêche de sélectionner les cellules
        } // Fin de configuration de la protection

        for (int i = 1; i <= 7; i++) sheet.autoSizeColumn(i); // Ajustement automatique de la largeur des colonnes
    } // Fin de la méthode generateFullReport

    private void insertChart(Workbook wb, Sheet sh, DefaultPieDataset ds, String type, int col, int row, int w, int h) { // Méthode d'insertion de graphique
        try { // Bloc de sécurité pour la manipulation d'images
            JFreeChart chart = ChartFactory.createPieChart(null, ds, false, false, false); // Création du graphique sans légende ni titre
            chart.setBackgroundPaint(new Color(255, 255, 255, 0)); // Fond transparent
            PiePlot plot = (PiePlot) chart.getPlot(); // Récupération de la zone de dessin
            plot.setBackgroundPaint(null); // Pas de fond pour la zone de dessin
            plot.setOutlineVisible(false); // Pas de bordure autour du graphique
            plot.setLabelGenerator(null); // Pas d'étiquettes de texte sur les parts

            if (type.equals("Sexe")) { // Choix des couleurs pour le graphique des effectifs
                plot.setSectionPaint("Garçons", COLOR_BOY); // Bleu pour garçons
                plot.setSectionPaint("Filles", COLOR_GIRL); // Rose pour filles
            } else { // Choix des couleurs pour le financier
                plot.setSectionPaint("Payé", COLOR_PAID); // Vert pour payé
                plot.setSectionPaint("Dette", COLOR_DEBT); // Rouge pour dette
            } // Fin du test de type

            BufferedImage img = chart.createBufferedImage(200, 150); // Génération de l'image en mémoire
            ByteArrayOutputStream bos = new ByteArrayOutputStream(); // Préparation du flux mémoire
            ImageIO.write(img, "png", bos); // Conversion de l'image en PNG
            int idx = wb.addPicture(bos.toByteArray(), Workbook.PICTURE_TYPE_PNG); // Ajout de l'image dans les ressources Excel
            
            Drawing<?> drawing = sh.createDrawingPatriarch(); // Création du gestionnaire d'objets graphiques
            ClientAnchor anchor = wb.getCreationHelper().createClientAnchor(); // Création de l'ancrage
            anchor.setCol1(col); anchor.setRow1(row); // Définition du coin haut-gauche
            anchor.setCol2(col + w); anchor.setRow2(row + h); // Définition du coin bas-droit
            drawing.createPicture(anchor, idx); // Affichage effectif de l'image sur la feuille
        } catch (Exception e) {} // Ignorer les erreurs d'image
    } // Fin de la méthode insertChart

    private void addSectionTitle(Sheet sh, String title, CellStyle st) { // Méthode pour ajouter un titre de bloc
        Row r = sh.createRow(currentRow++); // Création de la ligne
        Cell c = r.createCell(1); // Création de la cellule
        c.setCellValue(title); // Texte du titre de section
        c.setCellStyle(st); // Style d'en-tête (bleu foncé)
        sh.addMergedRegion(new CellRangeAddress(currentRow - 1, currentRow - 1, 1, 3)); // Fusion sur 3 colonnes
        currentRow++; // Ligne vide après le titre
    } // Fin de la méthode addSectionTitle

    private void addInfoRow(Sheet sh, String label, Object val, CellStyle stVal) { // Méthode pour ajouter une ligne de données simple
        Row r = sh.createRow(currentRow++); // Création de la ligne
        r.createCell(1).setCellValue(label); // Libellé à gauche
        Cell c = r.createCell(2); // Cellule pour la valeur
        if (val instanceof Number) c.setCellValue(((Number) val).doubleValue()); // Injection si c'est un nombre
        else c.setCellValue(val.toString()); // Injection si c'est du texte
        c.setCellStyle(stVal); // Application du style (entier ou monnaie)
    } // Fin de la méthode addInfoRow

    private CellStyle createStyle(Workbook wb, boolean bold, int size, short color, boolean bg) { // Méthode de création de style réutilisable
        CellStyle st = wb.createCellStyle(); // Création de l'objet style
        org.apache.poi.ss.usermodel.Font f = wb.createFont(); // Création d'une police
        f.setBold(bold); f.setFontHeightInPoints((short) size); f.setColor(color); // Configuration police
        st.setFont(f); // Association police au style
        if (bg) { // Si un fond est demandé
            st.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex()); // Fond bleu foncé
            st.setFillPattern(FillPatternType.SOLID_FOREGROUND); // Motif de remplissage plein
            st.setAlignment(HorizontalAlignment.CENTER); // Centrage horizontal
        } // Fin du test fond
        st.setVerticalAlignment(VerticalAlignment.CENTER); // Centrage vertical par défaut
        return st; // Renvoi du style configuré
    } // Fin de la méthode createStyle

    private ReportData fetchData() { // Méthode de récupération des données SQL
        ReportData d = new ReportData(); // Instanciation du conteneur de données
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) { // Connexion JDBC
            Statement s = con.createStatement(); // Création du statement
            ResultSet rs = s.executeQuery("SELECT COUNT(*) as t, SUM(CASE WHEN sexe='Masculin' THEN 1 ELSE 0 END) as m, COUNT(DISTINCT classe) as c FROM tb_eleve"); // SQL effectifs
            if (rs.next()) { // Si résultat trouvé
                d.totalStudents = rs.getInt("t"); d.totalMale = rs.getInt("m"); // Lecture total et garçons
                d.totalFemale = d.totalStudents - d.totalMale; d.totalClasses = rs.getInt("c"); // Déduction filles et classes
            } // Fin lecture effectifs
            rs = s.executeQuery("SELECT SUM(frais_total), SUM(montant_paye) FROM tb_frais"); // SQL finances globales
            if (rs.next()) { d.expectedFees = rs.getDouble(1); d.paidFees = rs.getDouble(2); d.totalDebt = d.expectedFees - d.paidFees; } // Lecture montants
            
            rs = s.executeQuery("SELECT tbe.classe, COUNT(*), SUM(frais_total), SUM(montant_paye), SUM(reste_a_payer) FROM tb_frais tbf JOIN tb_eleve tbe ON tbf.matricule_eleve = tbe.matricule GROUP BY tbe.classe"); // SQL par classe
            while (rs.next()) { // Boucle sur chaque ligne de classe
                Map<String, Object> m = new HashMap<>(); // Création d'une map pour la ligne
                m.put("nom", rs.getString(1)); m.put("eff", rs.getInt(2)); // Stockage nom et effectif
                m.put("att", rs.getDouble(3)); m.put("pay", rs.getDouble(4)); m.put("det", rs.getDouble(5)); // Stockage montants
                d.classesData.add(m); // Ajout de la map à la liste globale
            } // Fin de boucle
            return d; // Renvoi des données complètes
        } catch (Exception e) { return null; } // Renvoi null en cas d'erreur de connexion
    } // Fin de la méthode fetchData
} // Fin de la classe RapportGeneralExport