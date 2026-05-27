package gestionfrais; // Définit le package du projet

import java.awt.Color; // Import pour les couleurs AWT (utilisées par JFreeChart)
import java.awt.image.BufferedImage; // Import pour gérer l'image du graphique en mémoire
import java.io.ByteArrayOutputStream; // Import pour convertir l'image en flux d'octets
import java.io.File; // Import pour manipuler les fichiers
import java.io.FileOutputStream; // Import pour écrire le fichier PDF sur le disque
import java.io.IOException; // Import pour la gestion des erreurs d'entrée/sortie
import java.sql.*; // Import pour la connexion et les requêtes à la base de données
import java.util.*; // Import pour les structures de données (List, Map, HashMap)
import javax.imageio.ImageIO; // Import pour transformer le graphique en format image (PNG)
import javax.swing.JFileChooser; // Import pour la fenêtre de sélection de dossier/fichier
import javax.swing.JOptionPane; // Import pour afficher des boîtes de dialogue
import javax.swing.filechooser.FileNameExtensionFilter; // Import pour filtrer l'extension .pdf

// ITEXT PDF IMPORTS
import com.itextpdf.text.*; // Import des classes de base d'iText (Document, Paragraph, etc.)
import com.itextpdf.text.Font; // Import spécifique pour la gestion des polices iText
import com.itextpdf.text.Image; // Import spécifique pour l'insertion d'images dans le PDF
import com.itextpdf.text.pdf.*; // Import pour les fonctionnalités PDF avancées (PdfWriter, PdfPTable)

// JFREECHART IMPORTS (Pour les graphiques)
import org.jfree.chart.ChartFactory; // Import pour créer les graphiques (camemberts)
import org.jfree.chart.JFreeChart; // Classe principale pour représenter un graphique
import org.jfree.chart.plot.PiePlot; // Import pour personnaliser l'apparence des camemberts
import org.jfree.data.general.DefaultPieDataset; // Import pour définir les données du graphique

import java.util.List; // Import de l'interface List
import java.util.ArrayList; // Import de la classe ArrayList
import java.util.Map; // Import de l'interface Map
import java.util.HashMap; // Import de la classe HashMap

public class RapportGeneralExportPDF { // Déclaration de la classe principale

    private static final String DB_URL = "jdbc:mysql://localhost/frais_scolaire"; // URL de connexion à MySQL
    private static final String DB_USER = "root"; // Nom d'utilisateur MySQL
    private static final String DB_PASSWORD = ""; // Mot de passe MySQL

    // Couleurs Institutionnelles (AWT pour JFreeChart)
    private static final Color COLOR_PAID = new Color(39, 174, 96); // Vert pour les montants payés
    private static final Color COLOR_DEBT = new Color(192, 57, 43); // Rouge pour les dettes
    private static final Color COLOR_BOY = new Color(52, 152, 219); // Bleu pour les garçons
    private static final Color COLOR_GIRL = new Color(231, 76, 60); // Rose/Rouge pour les filles
    private static final BaseColor DARK_BLUE = new BaseColor(0, 51, 102); // Bleu foncé iText pour les titres

    private static class ReportData { // Classe interne pour structurer les données du rapport
        int totalStudents, totalClasses, totalMale, totalFemale; // Variables pour les effectifs
        double expectedFees, paidFees, totalDebt; // Variables pour les finances
        String currency = "XAF"; // Devise utilisée
        List<Map<String, Object>> classesData = new ArrayList<>(); // Liste stockant les données par classe
    } // Fin de la classe interne

    public void executeReportExport() { // Méthode de lancement de l'exportation
        ReportData data = fetchData(); // Récupération des données depuis la base de données
        if (data == null) return; // Si aucune donnée ou erreur, on arrête

        JFileChooser fileChooser = new JFileChooser(); // Création de la fenêtre de sauvegarde
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichier PDF (*.pdf)", "pdf")); // Filtre PDF
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) { // Si l'utilisateur valide
            File file = fileChooser.getSelectedFile(); // On récupère le fichier choisi
            if (!file.getName().endsWith(".pdf")) file = new File(file + ".pdf"); // On s'assure que l'extension est .pdf

            try { // Bloc de tentative de génération
                generatePDF(data, file); // Appel de la méthode de création du PDF
                JOptionPane.showMessageDialog(null, "Rapport PDF avec graphiques généré !"); // Message de succès
            } catch (Exception e) { // Gestion des erreurs
                e.printStackTrace(); // Affiche l'erreur dans la console
                JOptionPane.showMessageDialog(null, "Erreur : " + e.getMessage()); // Message d'erreur
            } // Fin du bloc try-catch
        } // Fin de la sélection de fichier
    } // Fin de la méthode executeReportExport

    private void generatePDF(ReportData data, File file) throws DocumentException, IOException { // Méthode de création du document
        Document document = new Document(PageSize.A4); // Création du document au format A4
        PdfWriter.getInstance(document, new FileOutputStream(file)); // Liaison entre le document et le fichier de sortie
        document.open(); // Ouverture du document pour écriture

        // Polices
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, DARK_BLUE); // Police du titre principal
        Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK); // Police des sous-titres
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE); // Police des en-têtes de table
        Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 10); // Police du texte normal

        // --- TITRE ---
        Paragraph title = new Paragraph("RAPPORT DE GESTION SCOLAIRE 2025-2026", titleFont); // Création du paragraphe titre
        title.setAlignment(Element.ALIGN_CENTER); // Centrage du titre
        document.add(title); // Ajout du titre au document
        document.add(new Paragraph(" ")); // Ajout d'une ligne vide pour l'espacement

        // --- SECTION 1 : EFFECTIFS + GRAPHIQUE ---
        document.add(new Paragraph("1. STATISTIQUES DES EFFECTIFS", subTitleFont)); // Sous-titre section 1
        document.add(new Paragraph(" ")); // Espacement

        PdfPTable tableEff = new PdfPTable(2); // Table principale à 2 colonnes pour séparer texte et graphique
        tableEff.setWidthPercentage(100); // Utilise toute la largeur de la page
        
        // Sous-table pour les chiffres
        PdfPTable subStats = new PdfPTable(2); // Table imbriquée pour les indicateurs
        addHeaderCell(subStats, "Indicateur", headerFont, DARK_BLUE); // En-tête indicateur
        addHeaderCell(subStats, "Valeur", headerFont, DARK_BLUE); // En-tête valeur
        addContentCell(subStats, "Total Élèves", String.valueOf(data.totalStudents), textFont); // Donnée total élèves
        addContentCell(subStats, "Garçons", String.valueOf(data.totalMale), textFont); // Donnée garçons
        addContentCell(subStats, "Filles", String.valueOf(data.totalFemale), textFont); // Donnée filles

        PdfPCell cellStats = new PdfPCell(subStats); // Cellule contenant la sous-table
        cellStats.setBorder(Rectangle.NO_BORDER); // On enlève la bordure
        tableEff.addCell(cellStats); // On ajoute les stats à la table principale

        // Graphique Genre
        DefaultPieDataset dsGenre = new DefaultPieDataset(); // Dataset pour le graphique de genre
        dsGenre.setValue("Garçons", data.totalMale); // Ajout des garçons
        dsGenre.setValue("Filles", data.totalFemale); // Ajout des filles
        Image imgGenre = getChartImage(dsGenre, "Sexe"); // Génération de l'image du graphique
        PdfPCell cellImgGenre = new PdfPCell(imgGenre, true); // Cellule contenant l'image
        cellImgGenre.setBorder(Rectangle.NO_BORDER); // Pas de bordure
        tableEff.addCell(cellImgGenre); // Ajout de l'image à la table principale

        document.add(tableEff); // Ajout de la table d'effectifs au document

        // --- SECTION 2 : FINANCES + GRAPHIQUE ---
        document.add(new Paragraph("2. RÉSUMÉ FINANCIER GLOBAL", subTitleFont)); // Sous-titre section 2
        document.add(new Paragraph(" ")); // Espacement

        PdfPTable tableFin = new PdfPTable(2); // Table principale à 2 colonnes (texte / graphique)
        tableFin.setWidthPercentage(100); // Largeur 100%

        PdfPTable subFin = new PdfPTable(2); // Sous-table pour les détails financiers
        addHeaderCell(subFin, "Description", headerFont, DARK_BLUE); // En-tête description
        addHeaderCell(subFin, "Montant", headerFont, DARK_BLUE); // En-tête montant
        addContentCell(subFin, "Total Attendu", formatAmount(data.expectedFees), textFont); // Donnée attendu
        addContentCell(subFin, "Total Payé", formatAmount(data.paidFees), textFont); // Donnée payé
        addContentCell(subFin, "Reste à Recouvrer", formatAmount(data.totalDebt), textFont); // Donnée dette

        PdfPCell cellFin = new PdfPCell(subFin); // Cellule contenant les finances
        cellFin.setBorder(Rectangle.NO_BORDER); // Sans bordure
        tableFin.addCell(cellFin); // Ajout à la table principale

        // Graphique Financier
        DefaultPieDataset dsFin = new DefaultPieDataset(); // Dataset pour le graphique financier
        dsFin.setValue("Payé", data.paidFees); // Part payée
        dsFin.setValue("Dette", data.totalDebt); // Part dette
        Image imgFin = getChartImage(dsFin, "Finance"); // Création de l'image camembert financier
        PdfPCell cellImgFin = new PdfPCell(imgFin, true); // Cellule de l'image
        cellImgFin.setBorder(Rectangle.NO_BORDER); // Sans bordure
        tableFin.addCell(cellImgFin); // Ajout à la table

        document.add(tableFin); // Ajout de la section financière au document

        // --- SECTION 3 : DÉTAILS PAR CLASSE ---
        document.add(new Paragraph("3. RÉPARTITION DÉTAILLÉE PAR CLASSE", subTitleFont)); // Titre section 3
        document.add(new Paragraph("LÉGENDE : VERT = PAYÉ | ROUGE = DETTE", textFont)); // Information légende
        document.add(new Paragraph(" ")); // Espacement

        PdfPTable tableClass = new PdfPTable(7); // Table des classes à 7 colonnes
        tableClass.setWidthPercentage(100); // Largeur complète
        float[] w = {2f, 1f, 1.5f, 1.5f, 1.5f, 1.2f, 1.3f}; // Définition des largeurs relatives de colonnes
        tableClass.setWidths(w); // Application des largeurs

        String[] h = {"CLASSE", "EFF.", "ATTENDU", "PAYÉ", "DETTE", "%", "VISUEL"}; // En-têtes du tableau
        for(String s : h) addHeaderCell(tableClass, s, headerFont, DARK_BLUE); // Création des cellules d'en-tête

        for(Map<String, Object> c : data.classesData) { // Boucle pour chaque classe de la liste
            tableClass.addCell(new Phrase(c.get("nom").toString(), textFont)); // Nom de la classe
            tableClass.addCell(new Phrase(c.get("eff").toString(), textFont)); // Effectif
            tableClass.addCell(new Phrase(formatAmount((double)c.get("att")), textFont)); // Montant attendu
            tableClass.addCell(new Phrase(formatAmount((double)c.get("pay")), textFont)); // Montant payé
            tableClass.addCell(new Phrase(formatAmount((double)c.get("det")), textFont)); // Montant dette
            
            double tx = ((double)c.get("att") > 0) ? (double)c.get("pay")/(double)c.get("att") : 0; // Calcul taux recouvrement
            tableClass.addCell(new Phrase(String.format("%.1f%%", tx*100), textFont)); // Affichage du taux en %

            // Mini graphique pour la classe
            DefaultPieDataset dsC = new DefaultPieDataset(); // Dataset pour le mini-camembert
            dsC.setValue("P", (double)c.get("pay")); // Part P (Payé)
            dsC.setValue("D", (double)c.get("det")); // Part D (Dette)
            Image mini = getChartImage(dsC, "Finance"); // Génération du mini graphique
            mini.scaleToFit(40, 40); // Réduction de la taille pour tenir dans une cellule
            PdfPCell cMini = new PdfPCell(mini, false); // Création de la cellule pour l'image
            cMini.setHorizontalAlignment(Element.ALIGN_CENTER); // Centrage dans la cellule
            tableClass.addCell(cMini); // Ajout à la table
        } // Fin de boucle par classe

        document.add(tableClass); // Ajout du tableau détaillé au document
        document.close(); // Fermeture finale du document
    } // Fin de la méthode generatePDF

    // --- Utilitaires Graphiques ---
    private Image getChartImage(DefaultPieDataset ds, String type) throws IOException, BadElementException { // Génère une image iText à partir de données
        JFreeChart chart = ChartFactory.createPieChart(null, ds, true, false, false); // Création du graphique camembert
        chart.setBackgroundPaint(Color.WHITE); // Fond blanc pour le graphique
        PiePlot plot = (PiePlot) chart.getPlot(); // Accès aux réglages du dessin
        plot.setBackgroundPaint(Color.WHITE); // Fond blanc de la zone de dessin
        plot.setOutlineVisible(false); // Pas de bordure de contour
        plot.setLabelGenerator(null); // Pas d'étiquettes de texte sur les parts

        if(type.equals("Sexe")) { // Attribution des couleurs selon le type de données
            plot.setSectionPaint("Garçons", COLOR_BOY); // Bleu pour Garçons
            plot.setSectionPaint("Filles", COLOR_GIRL); // Rose pour Filles
        } else { // Si type Finance
            plot.setSectionPaint("Payé", COLOR_PAID); // Vert pour Payé
            plot.setSectionPaint("Dette", COLOR_DEBT); // Rouge pour Dette
            plot.setSectionPaint("P", COLOR_PAID); // Support abréviation pour mini-graphiques
            plot.setSectionPaint("D", COLOR_DEBT); // Support abréviation pour mini-graphiques
        } // Fin conditions couleurs

        BufferedImage objRes = chart.createBufferedImage(300, 200); // Rendu du graphique en image tampon
        ByteArrayOutputStream bas = new ByteArrayOutputStream(); // Flux de sortie en mémoire
        ImageIO.write(objRes, "png", bas); // Encodage de l'image en PNG vers le flux
        return Image.getInstance(bas.toByteArray()); // Retourne l'image au format compatible iText
    } // Fin méthode graphique

    private void addHeaderCell(PdfPTable t, String txt, Font f, BaseColor bg) { // Utilitaire pour les cellules d'en-tête
        PdfPCell c = new PdfPCell(new Phrase(txt, f)); // Création de la cellule avec texte et police
        c.setBackgroundColor(bg); // Couleur de fond (bleu foncé)
        c.setHorizontalAlignment(Element.ALIGN_CENTER); // Alignement centré horizontal
        c.setPadding(5); // Espace intérieur
        t.addCell(c); // Ajout à la table
    } // Fin méthode addHeaderCell

    private void addContentCell(PdfPTable t, String lbl, String val, Font f) { // Utilitaire pour les lignes de contenu simple
        t.addCell(new Phrase(lbl, f)); // Ajout du libellé
        PdfPCell c = new PdfPCell(new Phrase(val, f)); // Création cellule pour la valeur
        c.setHorizontalAlignment(Element.ALIGN_RIGHT); // Alignement à droite pour les chiffres
        t.addCell(c); // Ajout à la table
    } // Fin méthode addContentCell

    private String formatAmount(double a) { // Utilitaire de formatage monétaire
        return String.format("%,.0f", a); // Format avec séparateurs de milliers
    } // Fin méthode formatAmount

    private ReportData fetchData() { // Récupération des données SQL
        ReportData d = new ReportData(); // Instanciation du conteneur de données
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) { // Connexion JDBC
            Statement s = con.createStatement(); // Création de l'objet de requête
            ResultSet rs = s.executeQuery("SELECT COUNT(*), SUM(CASE WHEN sexe='Masculin' THEN 1 ELSE 0 END) FROM tb_eleve"); // SQL effectifs globaux
            if(rs.next()) { d.totalStudents = rs.getInt(1); d.totalMale = rs.getInt(2); d.totalFemale = d.totalStudents - d.totalMale; } // Stockage des comptes
            
            rs = s.executeQuery("SELECT SUM(frais_total), SUM(montant_paye) FROM tb_frais"); // SQL finances globales
            if(rs.next()) { d.expectedFees = rs.getDouble(1); d.paidFees = rs.getDouble(2); d.totalDebt = d.expectedFees - d.paidFees; } // Stockage des montants

            rs = s.executeQuery("SELECT tbe.classe, COUNT(*), SUM(frais_total), SUM(montant_paye), SUM(reste_a_payer) FROM tb_frais tbf JOIN tb_eleve tbe ON tbf.matricule_eleve = tbe.matricule GROUP BY tbe.classe"); // SQL détaillé par classe
            while(rs.next()) { // Parcours de chaque ligne de résultat
                Map<String, Object> m = new HashMap<>(); // Création d'une Map pour la classe
                m.put("nom", rs.getString(1)); m.put("eff", rs.getInt(2)); // Nom et effectif
                m.put("att", rs.getDouble(3)); m.put("pay", rs.getDouble(4)); m.put("det", rs.getDouble(5)); // Chiffres financiers
                d.classesData.add(m); // Ajout de la Map à la liste
            } // Fin boucle classes
            return d; // Retourne l'objet de données rempli
        } catch (Exception e) { return null; } // Retourne null si la connexion ou la requête échoue
    } // Fin méthode fetchData
} // Fin de la classe RapportGeneralExportPDF