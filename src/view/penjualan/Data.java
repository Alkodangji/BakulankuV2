/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view.penjualan;

import dao.ProdukDAO;
import helper.RupiahFormat;
import java.awt.CardLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Produk;
import view.main.MainFrame;

/**
 *
 * @author ASUS
 */
public
        class Data extends javax.swing.JPanel {

    private final ProdukDAO produkDAO = new ProdukDAO();
    private DefaultTableModel produkTableModel;

    /**
     * Creates new form Data
     */
    public
            Data() {
        initComponents();
        initProdukCrud();
    }

    private void initProdukCrud() {
        produkTableModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Kode Produk", "Nama Produk", "Harga Beli", "Harga Jual", "Stok", "Stok Minimum"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jTable1.setModel(produkTableModel);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sembunyikanKolomId();

        jTextField1.setEditable(false);

        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                isiFormDariTabel();
            }
        });

        jButton4.addActionListener(evt -> tambahProduk());
        jButton5.addActionListener(evt -> clearForm());
        jButton6.addActionListener(evt -> editProduk());
        jButton7.addActionListener(evt -> hapusProduk());

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadTableProduk();
            }
        });

        loadTableProduk();
        clearForm();
    }

    private void loadTableProduk() {
        loadTableProduk(null);
    }

    private void loadTableProduk(String keyword) {
        produkTableModel.setRowCount(0);
        List<Produk> daftarProduk = produkDAO.getAllProduk(keyword);

        for (Produk produk : daftarProduk) {
            produkTableModel.addRow(new Object[]{
                produk.getIdProduk(),
                produk.getKodeProduk(),
                produk.getNamaProduk(),
                RupiahFormat.format(produk.getHargaBeli()),
                RupiahFormat.format(produk.getHargaJual()),
                produk.getStok(),
                produk.getStokMinimum()
            });
        }

        sembunyikanKolomId();
    }

    private void sembunyikanKolomId() {
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setMinWidth(0);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
        }
    }

    private void isiFormDariTabel() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        int modelRow = jTable1.convertRowIndexToModel(selectedRow);
        jTextField1.setText(String.valueOf(produkTableModel.getValueAt(modelRow, 0)));
        jTextField2.setText(String.valueOf(produkTableModel.getValueAt(modelRow, 1)));
        jTextField3.setText(String.valueOf(produkTableModel.getValueAt(modelRow, 2)));
        jTextField4.setText(String.valueOf(RupiahFormat.parse(String.valueOf(produkTableModel.getValueAt(modelRow, 3)))));
        jTextField5.setText(String.valueOf(RupiahFormat.parse(String.valueOf(produkTableModel.getValueAt(modelRow, 4)))));
        jTextField6.setText(String.valueOf(produkTableModel.getValueAt(modelRow, 5)));
        jTextField7.setText(String.valueOf(produkTableModel.getValueAt(modelRow, 6)));
    }

    private void tambahProduk() {
        Produk produk = bacaForm(false);
        if (produk == null) {
            return;
        }

        if (produkDAO.tambahProduk(produk)) {
            JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan.");
            loadTableProduk();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Produk gagal ditambahkan.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editProduk() {
        Produk produk = bacaForm(true);
        if (produk == null) {
            return;
        }

        if (produkDAO.updateProduk(produk)) {
            JOptionPane.showMessageDialog(this, "Produk berhasil diupdate.");
            loadTableProduk();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Produk gagal diupdate.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusProduk() {
        int idProduk = getIdProdukTerpilih();
        if (idProduk <= 0) {
            JOptionPane.showMessageDialog(this, "Pilih produk yang akan dihapus.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Hapus produk ini?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (produkDAO.hapusProduk(idProduk)) {
            JOptionPane.showMessageDialog(this, "Produk berhasil dihapus.");
            loadTableProduk();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Produk gagal dihapus.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Produk bacaForm(boolean wajibAdaId) {
        int idProduk = 0;
        if (wajibAdaId) {
            idProduk = getIdProdukTerpilih();
            if (idProduk <= 0) {
                JOptionPane.showMessageDialog(this, "Pilih produk yang akan diupdate.");
                return null;
            }
        }

        String kodeProduk = jTextField2.getText().trim();
        String namaProduk = jTextField3.getText().trim();

        if (namaProduk.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama produk wajib diisi.");
            return null;
        }

        double hargaBeli = parseAngka(jTextField4.getText());
        double hargaJual = parseAngka(jTextField5.getText());
        int stok = parseInteger(jTextField6.getText());
        int stokMinimum = parseInteger(jTextField7.getText());

        if (hargaBeli < 0) {
            JOptionPane.showMessageDialog(this, "Harga beli tidak boleh kurang dari 0.");
            return null;
        }
        if (hargaJual <= 0) {
            JOptionPane.showMessageDialog(this, "Harga jual harus lebih dari 0.");
            return null;
        }
        if (stok < 0) {
            JOptionPane.showMessageDialog(this, "Stok tidak boleh kurang dari 0.");
            return null;
        }
        if (stokMinimum < 0) {
            JOptionPane.showMessageDialog(this, "Stok minimum tidak boleh kurang dari 0.");
            return null;
        }
        if (kodeProduk.isEmpty()) {
            kodeProduk = produkDAO.generateKodeProduk();
        }

        return new Produk(idProduk, kodeProduk, namaProduk, hargaBeli, hargaJual, stok, stokMinimum);
    }

    private int getIdProdukTerpilih() {
        try {
            if (!jTextField1.getText().trim().isEmpty()) {
                return Integer.parseInt(jTextField1.getText().trim());
            }

            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = jTable1.convertRowIndexToModel(selectedRow);
                return Integer.parseInt(String.valueOf(produkTableModel.getValueAt(modelRow, 0)));
            }
        } catch (NumberFormatException e) {
            return 0;
        }
        return 0;
    }

    private double parseAngka(String nilai) {
        if (nilai == null || nilai.trim().isEmpty()) {
            return 0;
        }

        try {
            return Double.parseDouble(nilai.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return RupiahFormat.parse(nilai);
        }
    }

    private int parseInteger(String nilai) {
        if (nilai == null || nilai.trim().isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(nilai.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void clearForm() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setText("");
        jTextField7.setText("");
        jTable1.clearSelection();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        BtnBack = new javax.swing.JButton();
        BtnHist = new javax.swing.JButton();
        BtnRestok = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.1);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel7.setText("Stok minimum");
        jLabel7.setName("jLabel7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        jPanel1.add(jLabel7, gridBagConstraints);

        jLabel2.setText("Kode Barang");
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel3.setText("Nama Barang");
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel5.setText("Harga Jual");
        jLabel5.setName("jLabel5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        jPanel1.add(jLabel5, gridBagConstraints);

        jLabel6.setText("Stok saat ini");
        jLabel6.setName("jLabel6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        jPanel1.add(jLabel6, gridBagConstraints);

        jLabel4.setText("Harga Beli");
        jLabel4.setName("jLabel4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        jPanel1.add(jLabel4, gridBagConstraints);

        jLabel1.setText("ID");
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        jPanel1.add(jLabel1, gridBagConstraints);

        jTextField1.setName("jTextField1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        jPanel1.add(jTextField1, gridBagConstraints);

        jTextField2.setName("jTextField2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        jPanel1.add(jTextField2, gridBagConstraints);

        jTextField3.setName("jTextField3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        jPanel1.add(jTextField3, gridBagConstraints);

        jTextField4.setName("jTextField4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        jPanel1.add(jTextField4, gridBagConstraints);

        jTextField5.setName("jTextField5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        jPanel1.add(jTextField5, gridBagConstraints);

        jTextField6.setName("jTextField6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        jPanel1.add(jTextField6, gridBagConstraints);

        jTextField7.setName("jTextField7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        jPanel1.add(jTextField7, gridBagConstraints);

        BtnBack.setText("Kembali");
        BtnBack.setName("BtnBack"); // NOI18N
        BtnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBackActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(BtnBack, gridBagConstraints);

        BtnHist.setText("Riwayat");
        BtnHist.setName("BtnHist"); // NOI18N
        BtnHist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHistActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(BtnHist, gridBagConstraints);

        BtnRestok.setText("Restok");
        BtnRestok.setName("BtnRestok"); // NOI18N
        BtnRestok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnRestokActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(BtnRestok, gridBagConstraints);

        jButton4.setText("Tambah");
        jButton4.setName("jButton4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(jButton4, gridBagConstraints);

        jButton5.setText("Cancel");
        jButton5.setName("jButton5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(jButton5, gridBagConstraints);

        jButton6.setText("Update");
        jButton6.setName("jButton6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(jButton6, gridBagConstraints);

        jButton7.setText("Delete");
        jButton7.setName("jButton7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(jButton7, gridBagConstraints);

        jSplitPane1.setTopComponent(jPanel1);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);

        jSplitPane1.setRightComponent(jScrollPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BtnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBackActionPerformed
        CardLayout cl =
        (CardLayout) MainFrame.PenjualanContainer.getLayout();
        cl.show(MainFrame.PenjualanContainer, "HOME");
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnBackActionPerformed

    private void BtnHistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHistActionPerformed
        CardLayout cl =
        (CardLayout) MainFrame.PenjualanContainer.getLayout();
        cl.show(MainFrame.PenjualanContainer, "RIWAYAT");
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnHistActionPerformed

    private void BtnRestokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnRestokActionPerformed
        CardLayout cl =
        (CardLayout) MainFrame.PenjualanContainer.getLayout();
        cl.show(MainFrame.PenjualanContainer, "RESTOK");
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnRestokActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnBack;
    private javax.swing.JButton BtnHist;
    private javax.swing.JButton BtnRestok;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    // End of variables declaration//GEN-END:variables
}
