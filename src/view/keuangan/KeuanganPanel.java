/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view.keuangan;

import config.Koneksi;
import dao.AkunDAO;
import java.awt.CardLayout;
import java.awt.Window;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import session.Session;
import view.main.MainFrame;

/**
 *
 * @author ASUS
 */
public
        class KeuanganPanel extends javax.swing.JPanel {

    /**
     * Creates new form KeuanganPanel
     */
    private final AkunDAO akunDAO = new AkunDAO();

    public KeuanganPanel() {
            initComponents();
            initPemasukanMinimal();
            refreshRingkasanSaldo();
    }

    private void initPemasukanMinimal() {
        TxtTgl.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        jComboBox4.setSelectedItem("Pemasukan");
        jComboBox2.setModel(new DefaultComboBoxModel<>(new String[] { "Cash", "BRI" }));
        jComboBox3.setModel(new DefaultComboBoxModel<>(new String[] { "Cash", "BRI" }));
        jComboBox5.setModel(new DefaultComboBoxModel<>(new String[] { "Saldo Awal", "Modal", "Lainnya" }));
        jComboBox6.setModel(new DefaultComboBoxModel<>(new String[] { "Saldo Awal", "Modal", "Lainnya" }));
    }

    private void simpanPemasukan() {
        String jenis = String.valueOf(jComboBox4.getSelectedItem());
        if (!"Pemasukan".equals(jenis)) {
            JOptionPane.showMessageDialog(this, "Untuk sementara hanya Pemasukan yang aktif.");
            return;
        }

        double nominal = parseNominal(jTextField1.getText());
        if (nominal <= 0) {
            JOptionPane.showMessageDialog(this, "Nominal harus lebih dari 0.");
            return;
        }

        int idAkunTujuan = getIdAkun(String.valueOf(jComboBox3.getSelectedItem()));
        String kategori = String.valueOf(jComboBox5.getSelectedItem());
        String catatan = jTextArea1.getText().trim();
        int idUser = Session.idUser > 0 ? Session.idUser : 1;

        Connection conn = null;
        try {
            conn = Koneksi.getConnection();
            if (conn == null) {
                throw new Exception("Koneksi database gagal.");
            }
            conn.setAutoCommit(false);

            String nomorTransaksi = generateNomorTransaksi(conn);
            String sql = "INSERT INTO tb_keuangan "
                    + "(nomor_transaksi, tanggal, user_id, akun_asal_id, akun_tujuan_id, jenis, nominal, kategori, catatan) "
                    + "VALUES (?, ?, ?, NULL, ?, 'Pemasukan', ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nomorTransaksi);
                ps.setDate(2, Date.valueOf(getTanggalInput()));
                ps.setInt(3, idUser);
                ps.setInt(4, idAkunTujuan);
                ps.setDouble(5, nominal);
                ps.setString(6, kategori);
                ps.setString(7, catatan);
                ps.executeUpdate();
            }

            if (!akunDAO.tambahSaldo(conn, idAkunTujuan, nominal)) {
                throw new Exception("Gagal menambah saldo akun tujuan.");
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Pemasukan berhasil disimpan.");
            bersihkanForm();
            refreshRingkasanSaldo();
            refreshSaldoHeaderMainFrame();
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception rollbackError) {
                    rollbackError.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(this, "Gagal simpan pemasukan: " + e.getMessage());
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

    private LocalDate getTanggalInput() {
        String teksTanggal = TxtTgl.getText().trim();
        if (teksTanggal.isEmpty()) {
            return LocalDate.now();
        }
        return LocalDate.parse(teksTanggal, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private double parseNominal(String teksNominal) {
        String bersih = teksNominal.replace("Rp", "").replace(".", "").replace(",", "").trim();
        if (bersih.isEmpty()) {
            return 0;
        }
        return Double.parseDouble(bersih);
    }

    private int getIdAkun(String namaAkun) {
        return "BRI".equalsIgnoreCase(namaAkun) ? 2 : 1;
    }

    private String generateNomorTransaksi(Connection conn) throws Exception {
        String prefix = "KEU" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String sql = "SELECT COUNT(*) FROM tb_keuangan WHERE nomor_transaksi LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                int urut = 1;
                if (rs.next()) {
                    urut = rs.getInt(1) + 1;
                }
                return prefix + String.format("%04d", urut);
            }
        }
    }

    private void refreshRingkasanSaldo() {
        try (Connection conn = Koneksi.getConnection()) {
            if (conn == null) {
                return;
            }
            saldoCash.setText(formatRupiah(akunDAO.getSaldo(conn, 1)));
            saldoCash1.setText(formatRupiah(akunDAO.getSaldo(conn, 2)));
            saldoCash2.setText(formatRupiah(getTotalHariIni(conn, "Pemasukan")));
            saldoCash3.setText(formatRupiah(getTotalHariIni(conn, "Transfer")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double getTotalHariIni(Connection conn, String jenis) throws Exception {
        String sql = "SELECT COALESCE(SUM(nominal), 0) FROM tb_keuangan WHERE tanggal = CURDATE() AND jenis = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, jenis);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    private String formatRupiah(double nominal) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        return "Rp " + new DecimalFormat("#,##0", symbols).format(nominal);
    }

    private void bersihkanForm() {
        TxtTgl.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        jTextField1.setText("");
        jTextArea1.setText("");
        jComboBox4.setSelectedItem("Pemasukan");
        jComboBox3.setSelectedItem("Cash");
        jComboBox5.setSelectedIndex(0);
    }

    private void refreshSaldoHeaderMainFrame() {
        for (Window window : Window.getWindows()) {
            if (window instanceof MainFrame) {
                ((MainFrame) window).refreshSaldoHeader();
                break;
            }
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
        TxtTgl = new javax.swing.JFormattedTextField();
        jComboBox2 = new javax.swing.JComboBox<>();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jComboBox4 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jComboBox5 = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        btn = new javax.swing.JButton();
        btn1 = new javax.swing.JButton();
        jComboBox6 = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        BriCard = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        saldoCash1 = new javax.swing.JLabel();
        BtnHist = new javax.swing.JButton();
        CashCard = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        saldoCash = new javax.swing.JLabel();
        CashCard1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        saldoCash2 = new javax.swing.JLabel();
        CashCard2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        saldoCash3 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        BtnAtur = new javax.swing.JButton();

        jSplitPane1.setResizeWeight(0.7);
        jSplitPane1.setContinuousLayout(false);
        jSplitPane1.setName("jSplitPane1"); // NOI18N
        jSplitPane1.setOneTouchExpandable(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        TxtTgl.setName("TxtTgl"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 6);
        jPanel1.add(TxtTgl, gridBagConstraints);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BRI", "Cash" }));
        jComboBox2.setName("jComboBox2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 6);
        jPanel1.add(jComboBox2, gridBagConstraints);

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BRI", "Cash" }));
        jComboBox3.setName("jComboBox3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 6);
        jPanel1.add(jComboBox3, gridBagConstraints);

        jLabel1.setText("Tanggal");
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 2, 6);
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel2.setText("Akun Tujuan");
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 2, 6);
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel3.setText("Akun");
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 2, 6);
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel4.setText("Nominal");
        jLabel4.setName("jLabel4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 2, 6);
        jPanel1.add(jLabel4, gridBagConstraints);

        jTextField1.setName("jTextField1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 6);
        jPanel1.add(jTextField1, gridBagConstraints);

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pemasukan", "Pengeluaran", "Transfer" }));
        jComboBox4.setName("jComboBox4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(jComboBox4, gridBagConstraints);

        jLabel5.setText("Kategori");
        jLabel5.setName("jLabel5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 2, 6);
        jPanel1.add(jLabel5, gridBagConstraints);

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BRI", "Cash" }));
        jComboBox5.setName("jComboBox5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 6);
        jPanel1.add(jComboBox5, gridBagConstraints);

        jLabel6.setText("Catatan");
        jLabel6.setName("jLabel6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 2, 6);
        jPanel1.add(jLabel6, gridBagConstraints);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 6);
        jPanel1.add(jScrollPane1, gridBagConstraints);

        btn.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        btn.setText("Konfirmasi");
        btn.setName("btn"); // NOI18N
        btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(btn, gridBagConstraints);

        btn1.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        btn1.setText("Bersih");
        btn1.setName("btn1"); // NOI18N
        btn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel1.add(btn1, gridBagConstraints);

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BRI", "Cash" }));
        jComboBox6.setName("jComboBox6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 6);
        jPanel1.add(jComboBox6, gridBagConstraints);

        jLabel12.setText("Kategori");
        jLabel12.setName("jLabel12"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 2, 6);
        jPanel1.add(jLabel12, gridBagConstraints);

        jSplitPane1.setRightComponent(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        BriCard.setBackground(new java.awt.Color(0, 153, 0));
        BriCard.setName("BriCard"); // NOI18N
        BriCard.setLayout(new java.awt.GridBagLayout());

        jLabel7.setFont(new java.awt.Font("Poppins Light", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Total Saldo");
        jLabel7.setName("jLabel7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_TRAILING;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        BriCard.add(jLabel7, gridBagConstraints);

        saldoCash1.setFont(new java.awt.Font("Poppins SemiBold", 1, 16)); // NOI18N
        saldoCash1.setForeground(new java.awt.Color(255, 255, 255));
        saldoCash1.setText("Rp 2.000.000");
        saldoCash1.setName("saldoCash1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_TRAILING;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 0);
        BriCard.add(saldoCash1, gridBagConstraints);

        BtnHist.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        BtnHist.setForeground(new java.awt.Color(0, 153, 0));
        BtnHist.setText("Lihat Transaksi >");
        BtnHist.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BtnHist.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        BtnHist.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        BtnHist.setIconTextGap(0);
        BtnHist.setMargin(new java.awt.Insets(8, 12, 8, 12));
        BtnHist.setName("BtnHist"); // NOI18N
        BtnHist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BtnHistMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BtnHistMouseExited(evt);
            }
        });
        BtnHist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHistActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        BriCard.add(BtnHist, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        jPanel2.add(BriCard, gridBagConstraints);

        CashCard.setBackground(new java.awt.Color(255, 255, 255));
        CashCard.setName("CashCard"); // NOI18N
        CashCard.setLayout(new java.awt.GridBagLayout());

        jLabel8.setFont(new java.awt.Font("Poppins Light", 0, 12)); // NOI18N
        jLabel8.setText("Pengeluaran Hari ini");
        jLabel8.setName("jLabel8"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        CashCard.add(jLabel8, gridBagConstraints);

        saldoCash.setFont(new java.awt.Font("Poppins Medium", 1, 16)); // NOI18N
        saldoCash.setText("Rp 1.200.000");
        saldoCash.setName("saldoCash"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        CashCard.add(saldoCash, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 0);
        jPanel2.add(CashCard, gridBagConstraints);

        CashCard1.setBackground(new java.awt.Color(255, 255, 255));
        CashCard1.setName("CashCard1"); // NOI18N
        CashCard1.setLayout(new java.awt.GridBagLayout());

        jLabel9.setFont(new java.awt.Font("Poppins Light", 0, 12)); // NOI18N
        jLabel9.setText("Pemasukan hari ini");
        jLabel9.setName("jLabel9"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        CashCard1.add(jLabel9, gridBagConstraints);

        saldoCash2.setFont(new java.awt.Font("Poppins Medium", 1, 16)); // NOI18N
        saldoCash2.setText("Rp 200.000");
        saldoCash2.setName("saldoCash2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        CashCard1.add(saldoCash2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 6);
        jPanel2.add(CashCard1, gridBagConstraints);

        CashCard2.setBackground(new java.awt.Color(255, 255, 255));
        CashCard2.setName("CashCard2"); // NOI18N
        CashCard2.setLayout(new java.awt.GridBagLayout());

        jLabel10.setFont(new java.awt.Font("Poppins Light", 0, 12)); // NOI18N
        jLabel10.setText("Transfer Hari ini");
        jLabel10.setName("jLabel10"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        CashCard2.add(jLabel10, gridBagConstraints);

        saldoCash3.setFont(new java.awt.Font("Poppins Medium", 1, 16)); // NOI18N
        saldoCash3.setText("Rp 240.000");
        saldoCash3.setName("saldoCash3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        CashCard2.add(saldoCash3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        jPanel2.add(CashCard2, gridBagConstraints);

        jLabel11.setText("Transaksi Terbaru");
        jLabel11.setName("jLabel11"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 2, 0);
        jPanel2.add(jLabel11, gridBagConstraints);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

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
        jScrollPane2.setViewportView(jTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        jPanel2.add(jScrollPane2, gridBagConstraints);

        BtnAtur.setText("Atur Kategori Keuangan");
        BtnAtur.setName("BtnAtur"); // NOI18N
        BtnAtur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAturActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(BtnAtur, gridBagConstraints);

        jSplitPane1.setLeftComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActionPerformed
        simpanPemasukan();
    }//GEN-LAST:event_btnActionPerformed

    private void btn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn1ActionPerformed
        bersihkanForm();
    }//GEN-LAST:event_btn1ActionPerformed

    private void BtnHistMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtnHistMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnHistMouseEntered

    private void BtnHistMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtnHistMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnHistMouseExited

    private void BtnHistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHistActionPerformed
        CardLayout cl =
        (CardLayout) MainFrame.KeuanganContainer.getLayout();
        cl.show(MainFrame.KeuanganContainer, "RIWAYAT");
        
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnHistActionPerformed

    private void BtnAturActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAturActionPerformed
        CardLayout cl =
        (CardLayout) MainFrame.KeuanganContainer.getLayout();
        cl.show(MainFrame.KeuanganContainer, "KATEGORI");
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnAturActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BriCard;
    private javax.swing.JButton BtnAtur;
    private javax.swing.JButton BtnHist;
    private javax.swing.JPanel CashCard;
    private javax.swing.JPanel CashCard1;
    private javax.swing.JPanel CashCard2;
    private javax.swing.JFormattedTextField TxtTgl;
    private javax.swing.JButton btn;
    private javax.swing.JButton btn1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel saldoCash;
    private javax.swing.JLabel saldoCash1;
    private javax.swing.JLabel saldoCash2;
    private javax.swing.JLabel saldoCash3;
    // End of variables declaration//GEN-END:variables
}



