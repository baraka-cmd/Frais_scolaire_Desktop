
package gestionfrais;

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;




public class Rapport extends javax.swing.JPanel {
    
    public Rapport(MenuPrincipal parent) {
        
        initComponents();
        this.parent = parent;
    }

    private MenuPrincipal parent;
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        RapportEleve = new javax.swing.JButton();
        RapportFrais = new javax.swing.JButton();
        RapportDette = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        RapportGeneral = new javax.swing.JButton();
        RapportGeneralPDF = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        RapportEleve.setBackground(new java.awt.Color(102, 255, 0));
        RapportEleve.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        RapportEleve.setText("Rapport Elève");
        RapportEleve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RapportEleveActionPerformed(evt);
            }
        });

        RapportFrais.setBackground(new java.awt.Color(0, 255, 255));
        RapportFrais.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        RapportFrais.setText("Rapport Frais");
        RapportFrais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RapportFraisActionPerformed(evt);
            }
        });

        RapportDette.setBackground(new java.awt.Color(255, 153, 0));
        RapportDette.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        RapportDette.setText("Rapport Dette");
        RapportDette.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RapportDetteActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(0, 51, 204));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );

        jLabel1.setIcon(new javax.swing.ImageIcon(new ImageIcon(getClass().getResource("/gestionfrais/date-limite.png")).getImage().getScaledInstance(800, 460, Image.SCALE_SMOOTH)));

        jLabel2.setFont(new java.awt.Font("Times New Roman", 3, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 51, 255));
        jLabel2.setText("Rapport");

        RapportGeneral.setBackground(new java.awt.Color(255, 153, 0));
        RapportGeneral.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        RapportGeneral.setText("Rapport Général vers Excel");
        RapportGeneral.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                RapportGeneralMouseClicked(evt);
            }
        });
        RapportGeneral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RapportGeneralActionPerformed(evt);
            }
        });

        RapportGeneralPDF.setBackground(new java.awt.Color(255, 153, 0));
        RapportGeneralPDF.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        RapportGeneralPDF.setText("Rapport Général vers PDF");
        RapportGeneralPDF.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                RapportGeneralPDFMouseClicked(evt);
            }
        });
        RapportGeneralPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RapportGeneralPDFActionPerformed(evt);
            }
        });

        jLabel6.setIcon(new javax.swing.ImageIcon(new ImageIcon(getClass().getResource("/gestionfrais/pdf.png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

        jLabel5.setIcon(new javax.swing.ImageIcon(new ImageIcon(getClass().getResource("/gestionfrais/xls.png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(RapportEleve, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(123, 123, 123)
                        .addComponent(RapportFrais, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(85, 85, 85)
                        .addComponent(RapportDette, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(RapportGeneral))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(RapportGeneralPDF)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(96, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RapportEleve, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RapportFrais, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RapportDette, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(RapportGeneralPDF, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(RapportGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void RapportEleveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RapportEleveActionPerformed
        // TODO add your handling code here:
        parent.afficherPanelDansContenu(new RapportPanel(parent));
        
    }//GEN-LAST:event_RapportEleveActionPerformed

    private void RapportFraisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RapportFraisActionPerformed
        // TODO add your handling code here:
        parent.afficherPanelDansContenu(new RapportFrais(parent));
    }//GEN-LAST:event_RapportFraisActionPerformed

    private void RapportDetteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RapportDetteActionPerformed
        // TODO add your handling code here:
        parent.afficherPanelDansContenu(new RapportDette(parent));
    }//GEN-LAST:event_RapportDetteActionPerformed

    private void RapportGeneralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RapportGeneralActionPerformed
      RapportGeneralExport exporter = new RapportGeneralExport();
        exporter.executeReportExport();
    
    }//GEN-LAST:event_RapportGeneralActionPerformed

    private void RapportGeneralMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_RapportGeneralMouseClicked
        // TODO add your handling code here:

        
    }//GEN-LAST:event_RapportGeneralMouseClicked

    private void RapportGeneralPDFMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_RapportGeneralPDFMouseClicked
     // Instanciation de la nouvelle classe d'exportation PDF
    RapportGeneralExportPDF exporterPDF = new RapportGeneralExportPDF();
    // Appel de la méthode d'exécution pour le rapport PDF
    exporterPDF.executeReportExport();
   
    }//GEN-LAST:event_RapportGeneralPDFMouseClicked

    private void RapportGeneralPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RapportGeneralPDFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RapportGeneralPDFActionPerformed

 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton RapportDette;
    private javax.swing.JButton RapportEleve;
    private javax.swing.JButton RapportFrais;
    private javax.swing.JButton RapportGeneral;
    private javax.swing.JButton RapportGeneralPDF;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
