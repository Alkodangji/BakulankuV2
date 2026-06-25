/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view.keuangan;

import dao.AkunDAO;
import dao.KeuanganDAO;
import helper.DatePickerHelper;
import helper.UiThemeUtil;
import java.awt.CardLayout;
import java.awt.Window;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import javax.swing.DefaultComboBoxModel;
import model.KategoriKeuangan;
import model.KeuanganTransaksi;
import javax.swing.JOptionPane;
import session.Session;
import view.main.MainFrame;
import raven.datetime.DatePicker;

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
    private final KeuanganDAO keuanganDAO = new KeuanganDAO();
    private DatePicker DpTgl;

    public KeuanganPanel() {
            initComponents();
            initDatePicker();
            UiThemeUtil.applyTextFieldClearButton(this);
            UiThemeUtil.styleField(TxtNominal, UiThemeUtil.KEUANGAN_FIELD);
            UiThemeUtil.styleButton(BtnOk, UiThemeUtil.KEUANGAN_BUTTON);
            UiThemeUtil.styleButton(BtnClear, UiThemeUtil.KEUANGAN_BUTTON);
            initPemasukanMinimal();
            refreshRingkasanSaldo();
    }

    private void initDatePicker() {
        DpTgl = DatePickerHelper.install(TxtTgl);
    }

    private void initPemasukanMinimal() {
        CbJenis.setSelectedItem("Pemasukan");
        CbAkunAsal.setModel(new DefaultComboBoxModel<>(new String[] { "Cash", "BRI" }));
        CbAkunTujuan.setModel(new DefaultComboBoxModel<>(new String[] { "Cash", "BRI" }));
        CbJenis.addActionListener(e -> refreshKategoriByJenis());
        refreshKategoriByJenis();
    }

    private void simpanTransaksiKeuangan() {
        try {
            KeuanganTransaksi transaksi = buatTransaksiDariForm();
            keuanganDAO.simpanTransaksi(transaksi);
            JOptionPane.showMessageDialog(this, transaksi.getJenis() + " berhasil disimpan.");
            bersihkanForm();
            refreshSemuaTampilan();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan transaksi: " + e.getMessage());
        }
    }

    private KeuanganTransaksi buatTransaksiDariForm() throws Exception {
        String jenis = String.valueOf(CbJenis.getSelectedItem());
        double nominal = parseNominal(TxtNominal.getText());
        KategoriKeuangan kategori = getKategoriDipilih();

        KeuanganTransaksi transaksi = new KeuanganTransaksi();
        transaksi.setJenis(jenis);
        transaksi.setTanggal(getTanggalInput());
        transaksi.setUserId(Session.idUser > 0 ? Session.idUser : 1);
        transaksi.setNominal(nominal);
        transaksi.setCatatan(TxtNote.getText().trim());

        if ("Pemasukan".equals(jenis)) {
            transaksi.setAkunTujuanId(getIdAkun(String.valueOf(CbAkunTujuan.getSelectedItem())));
            transaksi.setIdKategori(kategori == null ? null : kategori.getIdKategori());
            transaksi.setKategori(kategori == null ? null : kategori.getNamaKategori());
        } else if ("Pengeluaran".equals(jenis)) {
            transaksi.setAkunAsalId(getIdAkun(String.valueOf(CbAkunAsal.getSelectedItem())));
            transaksi.setIdKategori(kategori == null ? null : kategori.getIdKategori());
            transaksi.setKategori(kategori == null ? null : kategori.getNamaKategori());
        } else if ("Transfer".equals(jenis)) {
            transaksi.setAkunAsalId(getIdAkun(String.valueOf(CbAkunAsal.getSelectedItem())));
            transaksi.setAkunTujuanId(getIdAkun(String.valueOf(CbAkunTujuan.getSelectedItem())));
            transaksi.setIdKategori(null);
            transaksi.setKategori("Transfer");
        }
        return transaksi;
    }

    private KategoriKeuangan getKategoriDipilih() {
        Object item = CbKategori.getSelectedItem();
        return item instanceof KategoriKeuangan ? (KategoriKeuangan) item : null;
    }

    public void refreshKategoriByJenis() {
        String jenis = String.valueOf(CbJenis.getSelectedItem());
        DefaultComboBoxModel modelKategori = new DefaultComboBoxModel();
        DefaultComboBoxModel modelKategoriDuplikat = new DefaultComboBoxModel();
        if (!"Transfer".equals(jenis)) {
            try {
                for (KategoriKeuangan kategori : keuanganDAO.getKategoriByJenis(jenis)) {
                    modelKategori.addElement(kategori);
                    modelKategoriDuplikat.addElement(kategori);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal memuat kategori: " + e.getMessage());
            }
        }
        CbKategori.setModel(modelKategori);
        jComboBox6.setModel(modelKategoriDuplikat);
        boolean pakaiKategori = !"Transfer".equals(jenis);
        CbKategori.setEnabled(pakaiKategori);
        jComboBox6.setEnabled(pakaiKategori);
    }

    private LocalDate getTanggalInput() {
        return DatePickerHelper.requireLocalDate(TxtTgl, "Tanggal transaksi");
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

    public void refreshRingkasanSaldo() {
        try (Connection conn = config.Koneksi.getConnection()) {
            if (conn == null) {
                return;
            }
            PengeluaranToday.setText(formatRupiah(akunDAO.getSaldo(conn, 1)));
            TotalAllSaldo.setText(formatRupiah(akunDAO.getSaldo(conn, 2)));
            PemasukanToday.setText(formatRupiah(0));
            TransferToday.setText(formatRupiah(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatRupiah(double nominal) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        return "Rp " + new DecimalFormat("#,##0", symbols).format(nominal);
    }

    private void bersihkanForm() {
        DatePickerHelper.resetToToday(TxtTgl, DpTgl);
        TxtNominal.setText("");
        TxtNote.setText("");
        CbJenis.setSelectedItem("Pemasukan");
        CbAkunAsal.setSelectedItem("Cash");
        CbAkunTujuan.setSelectedItem("Cash");
        refreshKategoriByJenis();
    }

    private void refreshSemuaTampilan() {
        refreshRingkasanSaldo();
        for (Window window : Window.getWindows()) {
            if (window instanceof MainFrame) {
                panggilRefreshSaldoHeader((MainFrame) window);
                break;
            }
        }
        if (MainFrame.RiwayatKeuanganPanel != null) {
            MainFrame.RiwayatKeuanganPanel.loadTable();
        }
    }

    private void panggilRefreshSaldoHeader(MainFrame mainFrame) {
        try {
            MainFrame.class.getMethod("refreshSaldoHeader").invoke(mainFrame);
        } catch (NoSuchMethodException ignored) {
            // Method tersedia di branch terbaru; jika belum ada, refresh header dilewati tanpa mengubah MainFrame.
        } catch (Exception e) {
            e.printStackTrace();
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
        Form = new javax.swing.JPanel();
        TxtTgl = new javax.swing.JFormattedTextField();
        CbAkunAsal = new javax.swing.JComboBox<>();
        CbAkunTujuan = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        TxtNominal = new javax.swing.JTextField();
        CbJenis = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        CbKategori = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TxtNote = new javax.swing.JTextArea();
        BtnOk = new javax.swing.JButton();
        BtnClear = new javax.swing.JButton();
        jComboBox6 = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        Card1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        TotalAllSaldo = new javax.swing.JLabel();
        BtnHist = new javax.swing.JButton();
        Card3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        PengeluaranToday = new javax.swing.JLabel();
        Card2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        PemasukanToday = new javax.swing.JLabel();
        Card4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        TransferToday = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TbTransaksi = new javax.swing.JTable();
        BtnAtur = new javax.swing.JButton();

        jSplitPane1.setResizeWeight(0.7);
        jSplitPane1.setContinuousLayout(false);
        jSplitPane1.setName("jSplitPane1"); // NOI18N
        jSplitPane1.setOneTouchExpandable(true);

        Form.setBackground(new java.awt.Color(255, 255, 255));
        Form.setName("Form"); // NOI18N
        Form.setLayout(new java.awt.GridBagLayout());

        TxtTgl.setName("TxtTgl"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 6);
        Form.add(TxtTgl, gridBagConstraints);

        CbAkunAsal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BRI", "Cash" }));
        CbAkunAsal.setName("CbAkunAsal"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 6);
        Form.add(CbAkunAsal, gridBagConstraints);

        CbAkunTujuan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BRI", "Cash" }));
        CbAkunTujuan.setName("CbAkunTujuan"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 6);
        Form.add(CbAkunTujuan, gridBagConstraints);

        jLabel1.setText("Tanggal");
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 2, 6);
        Form.add(jLabel1, gridBagConstraints);

        jLabel2.setText("Akun Tujuan");
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 2, 6);
        Form.add(jLabel2, gridBagConstraints);

        jLabel3.setText("Akun");
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 2, 6);
        Form.add(jLabel3, gridBagConstraints);

        jLabel4.setText("Nominal");
        jLabel4.setName("jLabel4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 2, 6);
        Form.add(jLabel4, gridBagConstraints);

        TxtNominal.setName("TxtNominal"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 6);
        Form.add(TxtNominal, gridBagConstraints);

        CbJenis.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pemasukan", "Pengeluaran", "Transfer" }));
        CbJenis.setName("CbJenis"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(CbJenis, gridBagConstraints);

        jLabel5.setText("Kategori");
        jLabel5.setName("jLabel5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 2, 6);
        Form.add(jLabel5, gridBagConstraints);

        CbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BRI", "Cash" }));
        CbKategori.setName("CbKategori"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 6);
        Form.add(CbKategori, gridBagConstraints);

        jLabel6.setText("Catatan");
        jLabel6.setName("jLabel6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 2, 6);
        Form.add(jLabel6, gridBagConstraints);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        TxtNote.setColumns(20);
        TxtNote.setRows(5);
        TxtNote.setName("TxtNote"); // NOI18N
        jScrollPane1.setViewportView(TxtNote);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 6);
        Form.add(jScrollPane1, gridBagConstraints);

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

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BRI", "Cash" }));
        jComboBox6.setName("jComboBox6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 6, 6);
        Form.add(jComboBox6, gridBagConstraints);

        jLabel12.setText("Kategori");
        jLabel12.setName("jLabel12"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 2, 6);
        Form.add(jLabel12, gridBagConstraints);

        jSplitPane1.setRightComponent(Form);

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        Card1.setBackground(new java.awt.Color(0, 153, 0));
        Card1.setName("Card1"); // NOI18N
        Card1.setLayout(new java.awt.GridBagLayout());

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
        Card1.add(jLabel7, gridBagConstraints);

        TotalAllSaldo.setFont(new java.awt.Font("Poppins SemiBold", 1, 16)); // NOI18N
        TotalAllSaldo.setForeground(new java.awt.Color(255, 255, 255));
        TotalAllSaldo.setText("Rp 2.000.000");
        TotalAllSaldo.setName("TotalAllSaldo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_TRAILING;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 0);
        Card1.add(TotalAllSaldo, gridBagConstraints);

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
        Card1.add(BtnHist, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        jPanel2.add(Card1, gridBagConstraints);

        Card3.setBackground(new java.awt.Color(255, 255, 255));
        Card3.setName("Card3"); // NOI18N
        Card3.setLayout(new java.awt.GridBagLayout());

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
        Card3.add(jLabel8, gridBagConstraints);

        PengeluaranToday.setFont(new java.awt.Font("Poppins Medium", 1, 16)); // NOI18N
        PengeluaranToday.setText("Rp 1.200.000");
        PengeluaranToday.setName("PengeluaranToday"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        Card3.add(PengeluaranToday, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 0);
        jPanel2.add(Card3, gridBagConstraints);

        Card2.setBackground(new java.awt.Color(255, 255, 255));
        Card2.setName("Card2"); // NOI18N
        Card2.setLayout(new java.awt.GridBagLayout());

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
        Card2.add(jLabel9, gridBagConstraints);

        PemasukanToday.setFont(new java.awt.Font("Poppins Medium", 1, 16)); // NOI18N
        PemasukanToday.setText("Rp 200.000");
        PemasukanToday.setName("PemasukanToday"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        Card2.add(PemasukanToday, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 6);
        jPanel2.add(Card2, gridBagConstraints);

        Card4.setBackground(new java.awt.Color(255, 255, 255));
        Card4.setName("Card4"); // NOI18N
        Card4.setLayout(new java.awt.GridBagLayout());

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
        Card4.add(jLabel10, gridBagConstraints);

        TransferToday.setFont(new java.awt.Font("Poppins Medium", 1, 16)); // NOI18N
        TransferToday.setText("Rp 240.000");
        TransferToday.setName("TransferToday"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        Card4.add(TransferToday, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        jPanel2.add(Card4, gridBagConstraints);

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

        TbTransaksi.setModel(new javax.swing.table.DefaultTableModel(
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
        TbTransaksi.setName("TbTransaksi"); // NOI18N
        jScrollPane2.setViewportView(TbTransaksi);

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

    private void BtnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnOkActionPerformed
        simpanTransaksiKeuangan();
    }//GEN-LAST:event_BtnOkActionPerformed

    private void BtnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnClearActionPerformed
        bersihkanForm();
    }//GEN-LAST:event_BtnClearActionPerformed

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
    private javax.swing.JButton BtnAtur;
    private javax.swing.JButton BtnClear;
    private javax.swing.JButton BtnHist;
    private javax.swing.JButton BtnOk;
    private javax.swing.JPanel Card1;
    private javax.swing.JPanel Card2;
    private javax.swing.JPanel Card3;
    private javax.swing.JPanel Card4;
    private javax.swing.JComboBox<String> CbAkunAsal;
    private javax.swing.JComboBox<String> CbAkunTujuan;
    private javax.swing.JComboBox<String> CbJenis;
    private javax.swing.JComboBox<String> CbKategori;
    private javax.swing.JPanel Form;
    private javax.swing.JLabel PemasukanToday;
    private javax.swing.JLabel PengeluaranToday;
    private javax.swing.JTable TbTransaksi;
    private javax.swing.JLabel TotalAllSaldo;
    private javax.swing.JLabel TransferToday;
    private javax.swing.JTextField TxtNominal;
    private javax.swing.JTextArea TxtNote;
    private javax.swing.JFormattedTextField TxtTgl;
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables
}



