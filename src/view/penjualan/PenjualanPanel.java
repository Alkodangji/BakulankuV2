/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view.penjualan;

import static com.formdev.flatlaf.extras.components.FlatTabbedPane.TabType.card;
import config.Koneksi;
import dao.AkunDAO;
import dao.PenjualanDAO;
import dao.PenjualanDetailDAO;
import dao.ProdukDAO;
import helper.DatePickerHelper;
import helper.KodeTransaksi;
import helper.NomorTransaksi;
import helper.RupiahFormat;
import helper.WrapLayout;
import helper.UiThemeUtil;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import view.component.MenuCard;
import view.main.MainFrame;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import raven.datetime.DatePicker;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import model.Penjualan;
import model.PenjualanDetail;
import session.Session;
import view.component.CartItem;

/**
 *
 * @author ASUS
 */
public
        class PenjualanPanel extends javax.swing.JPanel {

    private DatePicker DpTgl;

    /**
     * Creates new form PenjualanPanel
     */
    public
            PenjualanPanel() {
        initComponents();
        DpTgl = DatePickerHelper.install(
            TxtTgl,
            LocalDate.now(),
            Color.WHITE
        );
        UiThemeUtil.applyTextFieldClearButton(this);
        UiThemeUtil.styleField(TxtCari, UiThemeUtil.PENJUALAN_FIELD);
        
        resetForm();
        
        addComponentListener(new ComponentAdapter() {
    @Override
    public void componentShown(ComponentEvent e) {
        loadProduk(TxtCari.getText().trim());
    }
});
        
        PnlCart.setLayout(
    new BoxLayout(
        PnlCart,
        BoxLayout.Y_AXIS
        )
    );
        
        PnlMenu.setLayout(
        new WrapLayout(
            FlowLayout.LEFT,
            38,
            38
        )
    );

        TxtCari.putClientProperty(
            "JTextField.placeholderText",
            "Cari produk..."
    );

        TxtCari.putClientProperty(
            "JTextField.showClearButton",
            true
    );
        
    loadProduk("");
    }
    
    private void resetForm() {

    TxtNoTransaksi.setText(
        NomorTransaksi.generate(
            KodeTransaksi.PENJUALAN,
            "tb_penjualan"
        )
    );

}    
            
            
 private void loadProduk(String keyword) {

    PnlMenu.removeAll();

    try {

        Connection conn =
                Koneksi.getConnection();

        String sql =
            "SELECT * FROM tb_produk "
          + "WHERE nama_produk LIKE ? "
          + "ORDER BY nama_produk";

        PreparedStatement ps =
                conn.prepareStatement(sql);

        ps.setString(
                1,
                "%" + keyword + "%"
        );

        ResultSet rs =
                ps.executeQuery();

        while(rs.next()) {

            MenuCard card =
                    new MenuCard(
                        rs.getInt("id_produk"),
                        rs.getString("nama_produk"),
                        rs.getString("harga_jual"),
                        rs.getString("stok")
                    );
                        if(cartItems.containsKey(
                    card.getIdProduk()
            )){
                card.setSelected(true);
            }

            card.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            MouseEvent e) {

                        tambahKeKeranjang(card);

                    }
                }
            );

            PnlMenu.add(card);
        }

        PnlMenu.revalidate();
        PnlMenu.repaint();

    } catch(Exception e) {
        e.printStackTrace();
    }
}
 
 private HashMap<Integer, CartItem> cartItems =
        new HashMap<>();
    
    

    private void tambahKeKeranjang(MenuCard card) {

    int idProduk = card.getIdProduk();

    if (cartItems.containsKey(idProduk)) {

        CartItem item =
                cartItems.get(idProduk);

        item.tambahQty();
        updateTotal();
        hitungKembalian();

    } else {

        CartItem item = new CartItem(
                card.getIdProduk(),
                card.getNamaProduk(),
                card.getHarga(),
                card.getStok()
        );
        cartItems.put(
                idProduk,
                item
        );

        PnlCart.add(item);
        card.setSelected(true);
        updateTotal();
        hitungKembalian();
        
        item.setOnDelete(() -> {
        cartItems.remove(idProduk);
        card.setSelected(false);
        PnlCart.remove(item);
        updateTotal();
        hitungKembalian();
        }
        );
        
        item.setOnQtyChanged(() -> {
        updateTotal();
        hitungKembalian();
        });
    }
    PnlCart.revalidate();
    PnlCart.repaint();
    }
    
    private void updateTotal() {

    double total = 0;

    for (CartItem item : cartItems.values()) {

        total += item.getSubtotal();

    }

    TxtTotal.setText(
            RupiahFormat.format(total)
    );
}
    
    private void hitungKembalian() {

    double total =
            RupiahFormat.parse(
                    TxtTotal.getText()
            );

    double bayar =
            RupiahFormat.parse(
                    TxtBayar.getText()
            );

    if (bayar < total) {

        TxtKembali.setText("Kurang");

        return;
    }

    double kembali = bayar - total;

    TxtKembali.setText(
            RupiahFormat.format(kembali)
    );
}
    private ArrayList<MenuCard> menuCards =
        new ArrayList<>();
    
    private void bersihkanTransaksi() {

    cartItems.clear();

    PnlCart.removeAll();

    loadProduk(
            TxtCari.getText()
    );

    TxtTotal.setText("");
    TxtBayar.setText("");
    TxtKembali.setText("");

    PnlCart.revalidate();
    PnlCart.repaint();
}
    
    private Penjualan getDataPenjualan() {

    Penjualan penjualan =
            new Penjualan();

    penjualan.setNomorTransaksi(
            TxtNoTransaksi.getText()
    );

    penjualan.setUserId(
            Session.idUser
    );

    penjualan.setTotal(
            RupiahFormat.parse(
                    TxtTotal.getText()
            )
    );

    penjualan.setMetodePembayaran(
            CbAkun.getSelectedItem()
                    .toString()
    );

    penjualan.setDiterima(
            RupiahFormat.parse(
                    TxtBayar.getText()
            )
    );

    penjualan.setKembalian(
            RupiahFormat.parse(
                    TxtKembali.getText()
            )
    );

    return penjualan;
}
    
    private boolean validasiTransaksi() {

    if (!isTanggalValid()) {
        JOptionPane.showMessageDialog(
                this,
                "Tanggal transaksi wajib diisi dan harus valid"
        );

        return false;
    }

    if(cartItems.isEmpty()){

        JOptionPane.showMessageDialog(
                this,
                "Item masih kosong"
        );

        return false;
    }

    double total =
            RupiahFormat.parse(
                    TxtTotal.getText()
            );

    double bayar =
            RupiahFormat.parse(
                    TxtBayar.getText()
            );

    if(bayar < total){

        JOptionPane.showMessageDialog(
                this,
                "Nominal Pembayaran kurang"
        );

        return false;
    }

    return true;
}
    
    private boolean isTanggalValid() {
        return DatePickerHelper.validateRequired(TxtTgl, "Tanggal transaksi");
    }

    private void simpanTransaksi() {

    Connection conn = null;

    try {

        conn = Koneksi.getConnection();
        if (conn == null) {
            throw new Exception("Koneksi database tidak tersedia");
        }
        conn.setAutoCommit(false);

        if(!validasiTransaksi()){
            throw new Exception("Validasi transaksi gagal");
        }

        Penjualan penjualan = getDataPenjualan();
        PenjualanDAO penjualanDAO = new PenjualanDAO();
        PenjualanDetailDAO detailDAO = new PenjualanDetailDAO();
        ProdukDAO produkDAO = new ProdukDAO();
        AkunDAO akunDAO = new AkunDAO();

        for (CartItem item : cartItems.values()) {
            if (item.getQty() <= 0) {
                throw new Exception("Qty produk harus lebih dari 0");
            }

            int stokTerbaru = produkDAO.getStokById(conn, item.getIdProduk());
            if (stokTerbaru < 0) {
                throw new Exception("Produk tidak ditemukan");
            }
            if (stokTerbaru < item.getQty()) {
                throw new Exception("Stok tidak cukup untuk produk ID "
                        + item.getIdProduk() + ". Stok tersedia: " + stokTerbaru);
            }
        }

        int idPenjualan = penjualanDAO.insert(conn, penjualan);
        if (idPenjualan <= 0) {
            throw new Exception("Gagal menyimpan header penjualan");
        }

        for (CartItem item : cartItems.values()) {
            PenjualanDetail detail = new PenjualanDetail();
            detail.setPenjualanId(idPenjualan);
            detail.setProdukId(item.getIdProduk());
            detail.setQty(item.getQty());
            detail.setHarga(item.getHarga());
            detail.setSubtotal(item.getSubtotal());

            if (!detailDAO.insert(conn, detail)) {
                throw new Exception("Gagal menyimpan detail penjualan");
            }

            if (!produkDAO.kurangiStokAman(conn, item.getIdProduk(), item.getQty())) {
                throw new Exception("Stok tidak cukup saat update produk ID " + item.getIdProduk());
            }
        }

        int idAkun = akunDAO.getIdByNama(conn, penjualan.getMetodePembayaran());
        if (idAkun <= 0) {
            throw new Exception("Akun " + penjualan.getMetodePembayaran() + " tidak ditemukan");
        }
        if (!akunDAO.tambahSaldo(conn, idAkun, penjualan.getTotal())) {
            throw new Exception("Gagal menambah saldo " + penjualan.getMetodePembayaran());
        }

        conn.commit();

        if (MainFrame.RiwayatPenjualanPanel != null) {
            MainFrame.RiwayatPenjualanPanel.loadTable();
        }
        refreshSaldoHeaderMainFrame();

        JOptionPane.showMessageDialog(
                this,
                "Transaksi berhasil"
        );

        bersihkanTransaksi();
        resetForm();

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
                e.getMessage() == null ? "Transaksi gagal" : e.getMessage()
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

        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        Form = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        PnlCart = new javax.swing.JPanel();
        LblInfoTitle = new javax.swing.JLabel();
        TxtTotal = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        TxtBayar = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        TxtKembali = new javax.swing.JTextField();
        CStruk = new javax.swing.JCheckBox();
        BtnOk = new javax.swing.JButton();
        BtnClear = new javax.swing.JButton();
        TxtNoTransaksi = new javax.swing.JFormattedTextField();
        TxtTgl = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        CbAkun = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        PnlMenu = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        TxtCari = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        BtnData = new javax.swing.JButton();
        BtnHist = new javax.swing.JButton();

        jPanel1.setName("jPanel1"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jSplitPane1.setResizeWeight(0.7);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        Form.setBackground(new java.awt.Color(255, 255, 255));
        Form.setForeground(new java.awt.Color(231, 75, 44));
        Form.setMinimumSize(new java.awt.Dimension(210, 376));
        Form.setName("Form"); // NOI18N
        Form.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel1.setText("Detail Transaksi");
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(jLabel1, gridBagConstraints);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        PnlCart.setBackground(new java.awt.Color(255, 255, 255));
        PnlCart.setName("PnlCart"); // NOI18N
        PnlCart.setLayout(new javax.swing.BoxLayout(PnlCart, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane2.setViewportView(PnlCart);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(jScrollPane2, gridBagConstraints);

        LblInfoTitle.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        LblInfoTitle.setText("Total");
        LblInfoTitle.setName("LblInfoTitle"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        Form.add(LblInfoTitle, gridBagConstraints);

        TxtTotal.setEnabled(false);
        TxtTotal.setName("TxtTotal"); // NOI18N
        TxtTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtTotalActionPerformed(evt);
            }
        });
        TxtTotal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TxtTotalKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        Form.add(TxtTotal, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel9.setText("Bayar");
        jLabel9.setName("jLabel9"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(jLabel9, gridBagConstraints);

        TxtBayar.setName("TxtBayar"); // NOI18N
        TxtBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtBayarActionPerformed(evt);
            }
        });
        TxtBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TxtBayarKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(TxtBayar, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel10.setText("Kembali");
        jLabel10.setName("jLabel10"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(jLabel10, gridBagConstraints);

        TxtKembali.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        TxtKembali.setEnabled(false);
        TxtKembali.setName("TxtKembali"); // NOI18N
        TxtKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtKembaliActionPerformed(evt);
            }
        });
        TxtKembali.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TxtKembaliKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(TxtKembali, gridBagConstraints);

        CStruk.setText("Cetak Struk Sekaligus?");
        CStruk.setContentAreaFilled(false);
        CStruk.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        CStruk.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        CStruk.setName("CStruk"); // NOI18N
        CStruk.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        CStruk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CStrukActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(CStruk, gridBagConstraints);

        BtnOk.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        BtnOk.setText("Konfirmasi");
        BtnOk.setName("BtnOk"); // NOI18N
        BtnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(BtnOk, gridBagConstraints);

        BtnClear.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        BtnClear.setText("Bersih");
        BtnClear.setName("BtnClear"); // NOI18N
        BtnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnClearActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(BtnClear, gridBagConstraints);

        TxtNoTransaksi.setEditable(false);
        TxtNoTransaksi.setEnabled(false);
        TxtNoTransaksi.setFocusable(false);
        TxtNoTransaksi.setName("TxtNoTransaksi"); // NOI18N
        TxtNoTransaksi.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(TxtNoTransaksi, gridBagConstraints);

        TxtTgl.setName("TxtTgl"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        Form.add(TxtTgl, gridBagConstraints);

        jLabel3.setText("Tanggal");
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        Form.add(jLabel3, gridBagConstraints);

        jLabel4.setText("Metode Bayar");
        jLabel4.setName("jLabel4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(jLabel4, gridBagConstraints);

        CbAkun.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BRI", "Cash" }));
        CbAkun.setName("CbAkun"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(CbAkun, gridBagConstraints);

        jSplitPane1.setRightComponent(Form);

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setViewportView(PnlMenu);

        PnlMenu.setBackground(new java.awt.Color(255, 255, 255));
        PnlMenu.setMinimumSize(new java.awt.Dimension(12, 12));
        PnlMenu.setName("PnlMenu"); // NOI18N

        javax.swing.GroupLayout PnlMenuLayout = new javax.swing.GroupLayout(PnlMenu);
        PnlMenu.setLayout(PnlMenuLayout);
        PnlMenuLayout.setHorizontalGroup(
            PnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 608, Short.MAX_VALUE)
        );
        PnlMenuLayout.setVerticalGroup(
            PnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 516, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(PnlMenu);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel3.add(jScrollPane1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Daftar Produk");
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        jPanel3.add(jLabel2, gridBagConstraints);

        TxtCari.setName("TxtCari"); // NOI18N
        TxtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TxtCariKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 6, 7, 6);
        jPanel3.add(TxtCari, gridBagConstraints);

        jPanel4.setBackground(new java.awt.Color(0, 0, 0));
        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.GridBagLayout());

        BtnData.setText("Data Produk");
        BtnData.setName("BtnData"); // NOI18N
        BtnData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDataActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel4.add(BtnData, gridBagConstraints);

        BtnHist.setText("Riwayat");
        BtnHist.setName("BtnHist"); // NOI18N
        BtnHist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHistActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        jPanel4.add(BtnHist, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        jPanel3.add(jPanel4, gridBagConstraints);

        jSplitPane1.setLeftComponent(jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 852, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void TxtTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtTotalActionPerformed

    private void TxtTotalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtTotalKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtTotalKeyReleased

    private void TxtBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtBayarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtBayarActionPerformed

    private void TxtBayarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtBayarKeyReleased
        RupiahFormat.formatTextField(
            TxtBayar
        );

        hitungKembalian();
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtBayarKeyReleased

    private void TxtKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtKembaliActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtKembaliActionPerformed

    private void TxtKembaliKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtKembaliKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtKembaliKeyReleased

    private void BtnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnOkActionPerformed
        simpanTransaksi();
// TODO add your handling code here:
    }//GEN-LAST:event_BtnOkActionPerformed

    private void BtnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnClearActionPerformed
         bersihkanTransaksi();
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnClearActionPerformed

    private void BtnDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDataActionPerformed
        CardLayout cl =
        (CardLayout) MainFrame.PenjualanContainer.getLayout();
        cl.show(MainFrame.PenjualanContainer, "DATA");
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnDataActionPerformed

    private void BtnHistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHistActionPerformed
        CardLayout cl =
        (CardLayout) MainFrame.PenjualanContainer.getLayout();
        cl.show(MainFrame.PenjualanContainer, "RIWAYAT");
        
        
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnHistActionPerformed

    private void TxtCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtCariKeyReleased
        loadProduk(TxtCari.getText());
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtCariKeyReleased

    private void CStrukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CStrukActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CStrukActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnClear;
    private javax.swing.JButton BtnData;
    private javax.swing.JButton BtnHist;
    private javax.swing.JButton BtnOk;
    private javax.swing.JCheckBox CStruk;
    private javax.swing.JComboBox<String> CbAkun;
    private javax.swing.JPanel Form;
    private javax.swing.JLabel LblInfoTitle;
    private javax.swing.JPanel PnlCart;
    private javax.swing.JPanel PnlMenu;
    private javax.swing.JTextField TxtBayar;
    private javax.swing.JTextField TxtCari;
    private javax.swing.JTextField TxtKembali;
    private javax.swing.JFormattedTextField TxtNoTransaksi;
    private javax.swing.JFormattedTextField TxtTgl;
    private javax.swing.JTextField TxtTotal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables
}
