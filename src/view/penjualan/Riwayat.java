/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view.penjualan;

import dao.AkunDAO;
import dao.PenjualanDAO;
import dao.PenjualanDetailDAO;
import dao.ProdukDAO;
import java.awt.CardLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import view.main.MainFrame;
import model.Penjualan;
import model.PenjualanDetail;

/**
 *
 * @author ASUS
 */
public
        class Riwayat extends javax.swing.JPanel {

    /**
     * Creates new form Riwayat
     */
    public
            Riwayat() {
        initComponents();
        initDetailButton();
        jButton2.setEnabled(false);
        loadTable();
        
        addComponentListener(new ComponentAdapter() {
        @Override
        public void componentShown(ComponentEvent e) {
            loadTable();
            resetForm();
        }
    });
    }

    private void initDetailButton() {

        BtnDetail = new JButton("Detail");
        BtnDetail.setName("BtnDetail");
        BtnDetail.addActionListener(evt -> tampilkanDetailTransaksi());

        java.awt.GridBagConstraints gridBagConstraints =
                new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(BtnDetail, gridBagConstraints);
    }

    private void resetForm() {

    TxtId.setText("");
    TxtNoTransaksi.setText("");
    TxtTgl.setText("");
    TxtTotal.setText("");
    TxtBayar.setText("");
    TxtKembalian.setText("");

    CbMetode.setSelectedIndex(0);

    TbRiwayat.clearSelection();
}       
            
    public void loadTable() {

    PenjualanDAO dao = new PenjualanDAO();

    TbRiwayat.setModel(
            dao.getRiwayatPenjualan()
    );

    TbRiwayat.getColumnModel()
            .getColumn(0)
            .setMinWidth(0);

    TbRiwayat.getColumnModel()
            .getColumn(0)
            .setMaxWidth(0);

    TbRiwayat.getColumnModel()
            .getColumn(0)
            .setWidth(0);
}
    
    private void pilihDataTabel() {

    int row = TbRiwayat.getSelectedRow();

    if(row == -1){
        return;
    }

    TxtId.setText(
            TbRiwayat.getValueAt(row, 0).toString()
    );

    TxtNoTransaksi.setText(
            TbRiwayat.getValueAt(row, 1).toString()
    );

    TxtTgl.setText(
            TbRiwayat.getValueAt(row, 2).toString()
    );

    TxtTotal.setText(
            TbRiwayat.getValueAt(row, 3).toString()
    );

    CbMetode.setSelectedItem(
            TbRiwayat.getValueAt(row, 4).toString()
    );

    TxtBayar.setText(
            TbRiwayat.getValueAt(row, 5).toString()
    );

    TxtKembalian.setText(
            TbRiwayat.getValueAt(row, 6).toString()
    );
}
    
    private void tampilkanDetailTransaksi() {

        if (TxtId.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Pilih transaksi terlebih dahulu"
            );

            return;
        }

        int idPenjualan = Integer.parseInt(
                TxtId.getText().trim()
        );

        PenjualanDetailDAO dao =
                new PenjualanDetailDAO();

        JTable table =
                new JTable(dao.getDetailTransaksi(idPenjualan));
        table.setEnabled(false);
        table.getTableHeader().setReorderingAllowed(false);

        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(table),
                "Detail Transaksi " + TxtNoTransaksi.getText(),
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void deleteTransaksi() {

    if (TxtId.getText().trim().isEmpty()) {

        JOptionPane.showMessageDialog(
                this,
                "Pilih transaksi terlebih dahulu"
        );

        return;
    }

    int konfirmasi1 = JOptionPane.showConfirmDialog(
            this,
            "Hapus transaksi ini?",
            "Konfirmasi",
            JOptionPane.YES_NO_OPTION
    );

    if (konfirmasi1 != JOptionPane.YES_OPTION) {
        return;
    }

    int konfirmasi2 = JOptionPane.showConfirmDialog(
            this,
            "Penghapusan akan mengembalikan stok produk dan mengurangi saldo akun transaksi. Lanjutkan?",
            "Peringatan",
            JOptionPane.YES_NO_OPTION
    );

    if (konfirmasi2 != JOptionPane.YES_OPTION) {
        return;
    }

    Connection conn = null;

    try {

        int idPenjualan = Integer.parseInt(
                TxtId.getText()
        );

        conn = config.Koneksi.getConnection();
        conn.setAutoCommit(false);

        PenjualanDAO penjualanDAO = new PenjualanDAO();
        PenjualanDetailDAO detailDAO = new PenjualanDetailDAO();
        ProdukDAO produkDAO = new ProdukDAO();
        AkunDAO akunDAO = new AkunDAO();

        Penjualan penjualan = penjualanDAO.findById(conn, idPenjualan);
        if (penjualan == null) {
            throw new Exception("Transaksi tidak ditemukan");
        }

        java.util.List<PenjualanDetail> details = detailDAO.getByPenjualanId(conn, idPenjualan);
        if (details.isEmpty()) {
            throw new Exception("Detail transaksi tidak ditemukan");
        }

        int idAkun = akunDAO.getIdByNama(conn, penjualan.getMetodePembayaran());
        if (idAkun <= 0) {
            throw new Exception("Akun " + penjualan.getMetodePembayaran() + " tidak ditemukan");
        }

        double saldo = akunDAO.getSaldoById(conn, idAkun);
        if (saldo < penjualan.getTotal()) {
            throw new Exception("Saldo " + penjualan.getMetodePembayaran()
                    + " tidak cukup untuk rollback transaksi. Saldo saat ini lebih kecil dari total transaksi.");
        }

        for (PenjualanDetail detail : details) {
            if (!produkDAO.tambahStok(conn, detail.getProdukId(), detail.getQty())) {
                throw new Exception("Gagal mengembalikan stok produk ID " + detail.getProdukId());
            }
        }

        if (!akunDAO.kurangiSaldo(conn, idAkun, penjualan.getTotal())) {
            throw new Exception("Gagal mengurangi saldo " + penjualan.getMetodePembayaran());
        }

        detailDAO.deleteByPenjualanId(conn, idPenjualan);

        if (!penjualanDAO.delete(conn, idPenjualan)) {
            throw new Exception("Gagal menghapus header transaksi");
        }

        conn.commit();

        JOptionPane.showMessageDialog(
                this,
                "Transaksi berhasil dihapus"
        );

        loadTable();
        resetForm();
        refreshSaldoHeaderMainFrame();

    } catch (Exception e) {

        if (conn != null) {
            try {
                conn.rollback();
            } catch (Exception rollbackError) {
                rollbackError.printStackTrace();
            }
        }

        e.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                e.getMessage() == null ? "Terjadi kesalahan saat menghapus transaksi" : e.getMessage()
        );

    } finally {

        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (Exception closeError) {
                closeError.printStackTrace();
            }
        }
    }
}

    private void refreshSaldoHeaderMainFrame() {

    java.awt.Window window = SwingUtilities.getWindowAncestor(this);
    if (window instanceof MainFrame) {
        ((MainFrame) window).refreshSaldoHeader();
    }
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        TxtId = new javax.swing.JTextField();
        TxtNoTransaksi = new javax.swing.JTextField();
        TxtTgl = new javax.swing.JFormattedTextField();
        CbMetode = new javax.swing.JComboBox<>();
        TxtTotal = new javax.swing.JTextField();
        TxtBayar = new javax.swing.JTextField();
        TxtKembalian = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        BtnDelete = new javax.swing.JButton();
        BtnBack = new javax.swing.JButton();
        Btndata = new javax.swing.JButton();
        BtnRestok = new javax.swing.JButton();
        BtnClear = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        TbRiwayat = new javax.swing.JTable();

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.4);
        jSplitPane1.setContinuousLayout(false);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("ID");
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel2.setText("Tanggal");
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel3.setText("Bayar");
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel4.setText("No. Transaksi");
        jLabel4.setName("jLabel4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        jPanel1.add(jLabel4, gridBagConstraints);

        jLabel5.setText("Total");
        jLabel5.setName("jLabel5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        jPanel1.add(jLabel5, gridBagConstraints);

        jLabel6.setText("Metode Bayar");
        jLabel6.setName("jLabel6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        jPanel1.add(jLabel6, gridBagConstraints);

        jLabel7.setText("Kembalian");
        jLabel7.setName("jLabel7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        jPanel1.add(jLabel7, gridBagConstraints);

        TxtId.setName("TxtId"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        jPanel1.add(TxtId, gridBagConstraints);

        TxtNoTransaksi.setName("TxtNoTransaksi"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        jPanel1.add(TxtNoTransaksi, gridBagConstraints);

        TxtTgl.setName("TxtTgl"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel1.add(TxtTgl, gridBagConstraints);

        CbMetode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cash", "BRI" }));
        CbMetode.setName("CbMetode"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel1.add(CbMetode, gridBagConstraints);

        TxtTotal.setName("TxtTotal"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        jPanel1.add(TxtTotal, gridBagConstraints);

        TxtBayar.setName("TxtBayar"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        jPanel1.add(TxtBayar, gridBagConstraints);

        TxtKembalian.setName("TxtKembalian"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        jPanel1.add(TxtKembalian, gridBagConstraints);

        jButton2.setText("Update");
        jButton2.setName("jButton2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(jButton2, gridBagConstraints);

        BtnDelete.setText("Delete");
        BtnDelete.setName("BtnDelete"); // NOI18N
        BtnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDeleteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(BtnDelete, gridBagConstraints);

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
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(BtnBack, gridBagConstraints);

        Btndata.setText("Data Produk");
        Btndata.setName("Btndata"); // NOI18N
        Btndata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtndataActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(Btndata, gridBagConstraints);

        BtnRestok.setText("Restok Produk");
        BtnRestok.setName("BtnRestok"); // NOI18N
        BtnRestok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnRestokActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(BtnRestok, gridBagConstraints);

        BtnClear.setText("Cancel");
        BtnClear.setName("BtnClear"); // NOI18N
        BtnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnClearActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(BtnClear, gridBagConstraints);

        jSplitPane1.setTopComponent(jPanel1);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        TbRiwayat.setModel(new javax.swing.table.DefaultTableModel(
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
        TbRiwayat.setName("TbRiwayat"); // NOI18N
        TbRiwayat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TbRiwayatMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TbRiwayat);

        jSplitPane1.setRightComponent(jScrollPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 769, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 722, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BtnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBackActionPerformed
        CardLayout cl =
        (CardLayout) MainFrame.PenjualanContainer.getLayout();
        cl.show(MainFrame.PenjualanContainer, "HOME");
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnBackActionPerformed

    private void BtnRestokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnRestokActionPerformed
        CardLayout cl =
        (CardLayout) MainFrame.PenjualanContainer.getLayout();
        cl.show(MainFrame.PenjualanContainer, "RESTOK");
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnRestokActionPerformed

    private void BtndataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtndataActionPerformed
        CardLayout cl =
        (CardLayout) MainFrame.PenjualanContainer.getLayout();
        cl.show(MainFrame.PenjualanContainer, "DATA");
        // TODO add your handling code here:
    }//GEN-LAST:event_BtndataActionPerformed

    private void TbRiwayatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TbRiwayatMouseClicked
        pilihDataTabel();
        // TODO add your handling code here:
    }//GEN-LAST:event_TbRiwayatMouseClicked

    private void BtnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnClearActionPerformed
        resetForm();
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnClearActionPerformed

    private void BtnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDeleteActionPerformed
        deleteTransaksi();
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnDeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnBack;
    private javax.swing.JButton BtnClear;
    private javax.swing.JButton BtnDelete;
    private javax.swing.JButton BtnDetail;
    private javax.swing.JButton BtnRestok;
    private javax.swing.JButton Btndata;
    private javax.swing.JComboBox<String> CbMetode;
    private javax.swing.JTable TbRiwayat;
    private javax.swing.JTextField TxtBayar;
    private javax.swing.JTextField TxtId;
    private javax.swing.JTextField TxtKembalian;
    private javax.swing.JTextField TxtNoTransaksi;
    private javax.swing.JFormattedTextField TxtTgl;
    private javax.swing.JTextField TxtTotal;
    private javax.swing.JButton jButton2;
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
    // End of variables declaration//GEN-END:variables
}
