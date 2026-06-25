/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view.brilink;

import com.formdev.flatlaf.FlatClientProperties;
import dao.BrilinkDAO;
import dao.KategoriTopupDAO;
import helper.AppIcon;
import helper.RupiahFormat;
import java.awt.CardLayout;

import java.awt.Color;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import model.Brilink;
import model.KategoriTopup;
import session.Session;
import raven.datetime.DatePicker;
import raven.datetime.TimePicker;
import view.main.MainFrame;
/**
 *
 * @author ASUS
 */
public
        class BRILinkPanel extends javax.swing.JPanel {

     private Color PRIMARY = new Color(9,87,195);
     
     private DatePicker datePicker;
     private final BrilinkDAO brilinkDAO = new BrilinkDAO();
     private final KategoriTopupDAO kategoriDAO = new KategoriTopupDAO();
     private Runnable saldoRefreshCallback;
     

     
    

    public
            BRILinkPanel() {
        this(null);
    }

    public
            BRILinkPanel(Runnable saldoRefreshCallback) {
        this.saldoRefreshCallback = saldoRefreshCallback;
        initComponents();
//        Load UI
//        Update Form
        updateFormByJenis();
//        date time setup
        datePicker = new DatePicker();
        datePicker.setEditor(TxtTgl);
        datePicker.setColor(Color.BLACK);
        datePicker.putClientProperty(
            FlatClientProperties.STYLE,
            "background:@briColor;" 
        );
        datePicker.setSelectionArc(20);
        datePicker.setEditorValidation(true);
        datePicker.setValidationOnNull(true);
        datePicker.now();
        loadKategoriCombo();
        loadSaldo();
        loadRiwayatRingkas();
        
//        Custom Component
        BriCard.putClientProperty(
                "FlatLaf.style",
              "arc:20" );
        CashCard.putClientProperty(
                "FlatLaf.style",
              "arc:20" );
   
        BtnAtur.setIcon(
            AppIcon.CATEGORY.create(Color.decode("#0957C3"))
            );
        BtnAtur.putClientProperty(
                "FlatLaf.style",
                   "arc:12;"
                 + "borderWidth:2;"
                 + "borderColor:@briColor;"
                 + "background:#ffffff"
            );
    
        CbJenis.putClientProperty(
            "FlatLaf.style",
            "borderColor:#0957C3;"
          + "focusedBorderColor:@briColor"
        );
        
        TxtNominal.putClientProperty(
            "FlatLaf.style",
          "showClearButton:true");
        
        
        RupiahFormat.formatTextField(TxtTotal);
        RupiahFormat.formatTextField(TxtKembali);
        
        Form.putClientProperty(
            "FlatLaf.styleClass",
          "basePanel"
        );
                
        line.putClientProperty(
             "FlatLaf.style", 
           "arc:12;"
         + "background:@briColor");
        
        
    } //End Constructor
            
//            Method zone
//            Update Jenis pada Form
    private void updateFormByJenis() {

    String jenis = CbJenis.getSelectedItem().toString();

    if (jenis.equals("Tarik Tunai")) {

        CbKat.setEnabled(false);
        CbFee.setEnabled(true);

        LblInfoTitle.setText("Total Diterima Pelanggan");
        TxtBayar.setText("-");
        TxtBayar.setEnabled(false);
        TxtKembali.setText("-");

    } else if (jenis.equals("Setor Tunai") || jenis.equals("Setor / Transfer")) {

        CbKat.setEnabled(false);
        CbFee.setEnabled(false);
        TxtBayar.setText("");
        TxtBayar.setEnabled(true);

        LblInfoTitle.setText("Total Dibayar Pelanggan");

    } else if (jenis.equals("Top Up")) {

        CbKat.setEnabled(true);
        CbFee.setEnabled(false);
        TxtBayar.setText("");
        TxtBayar.setEnabled(true);

        LblInfoTitle.setText("Total Dibayar Pelanggan");

    }

}
private void hitungTransaksi() {
    
    long nominal = (long) RupiahFormat.parse(TxtNominal.getText());
    long fee = (long) RupiahFormat.parse(TxtFee.getText());
    
    String jenis = CbJenis.getSelectedItem().toString();

    if (jenis.equals("Tarik Tunai")) {

        String metodeFee = CbFee.getSelectedItem().toString();

        long total;

        if (metodeFee.equals("Terpisah")) {
            total = nominal;
        } else {
            total = nominal - fee;
        }
        TxtTotal.setText(RupiahFormat.format(total));
        TxtBayar.setText("-");
        TxtKembali.setText("-");

    } else if (jenis.equals("Setor Tunai") || jenis.equals("Setor / Transfer") || jenis.equals("Top Up")) {

        long total = nominal + fee;
    long bayar = (long) RupiahFormat.parse(TxtBayar.getText());

    long kembali = bayar - total;

    if (kembali < 0) {
    kembali = 0;
        }

        TxtTotal.setText(RupiahFormat.format(total));
        TxtKembali.setText(RupiahFormat.format(kembali));
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
        TxtNominal = new javax.swing.JTextField();
        TxtTgl = new javax.swing.JFormattedTextField();
        btn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        TxtFee = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        CbFee = new javax.swing.JComboBox<>();
        CbKat = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        BtnAtur = new javax.swing.JButton();
        CbJenis = new javax.swing.JComboBox<>();
        TxtTotal = new javax.swing.JTextField();
        line = new javax.swing.JPanel();
        LblInfoTitle = new javax.swing.JLabel();
        TxtBayar = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        TxtKembali = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel11 = new javax.swing.JLabel();
        btn1 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        CashCard = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        saldoCash = new javax.swing.JLabel();
        BriCard = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        saldoCash1 = new javax.swing.JLabel();
        BtnHist = new javax.swing.JButton();
        CashCard1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        saldoCash2 = new javax.swing.JLabel();
        CashCard3 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        saldoCash4 = new javax.swing.JLabel();
        CashCard4 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        saldoCash5 = new javax.swing.JLabel();
        CashCard2 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        saldoCash3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();

        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jSplitPane1.setResizeWeight(0.9);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        Form.setBackground(new java.awt.Color(255, 255, 255));
        Form.setName("Form"); // NOI18N
        Form.setLayout(new java.awt.GridBagLayout());

        TxtNominal.setName("TxtNominal"); // NOI18N
        TxtNominal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtNominalActionPerformed(evt);
            }
        });
        TxtNominal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TxtNominalKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(TxtNominal, gridBagConstraints);

        TxtTgl.setName("TxtTgl"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(TxtTgl, gridBagConstraints);

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
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(btn, gridBagConstraints);

        jLabel2.setText("Nominal");
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        Form.add(jLabel2, gridBagConstraints);

        jLabel5.setText("Fee");
        jLabel5.setName("jLabel5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        Form.add(jLabel5, gridBagConstraints);

        TxtFee.setName("TxtFee"); // NOI18N
        TxtFee.setPreferredSize(new java.awt.Dimension(68, 30));
        TxtFee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtFeeActionPerformed(evt);
            }
        });
        TxtFee.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TxtFeeKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(TxtFee, gridBagConstraints);

        jLabel6.setText("Metode Fee");
        jLabel6.setName("jLabel6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        Form.add(jLabel6, gridBagConstraints);

        CbFee.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Terpisah", "Terpotong" }));
        CbFee.setName("CbFee"); // NOI18N
        CbFee.setPreferredSize(new java.awt.Dimension(68, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(CbFee, gridBagConstraints);

        CbKat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Terpisah", "Terpotong" }));
        CbKat.setName("CbKat"); // NOI18N
        CbKat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CbKatActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(CbKat, gridBagConstraints);

        jLabel7.setText("Kategori");
        jLabel7.setName("jLabel7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        Form.add(jLabel7, gridBagConstraints);

        BtnAtur.setBackground(new java.awt.Color(242, 242, 242));
        BtnAtur.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        BtnAtur.setForeground(new java.awt.Color(9, 87, 195));
        BtnAtur.setText("Atur");
        BtnAtur.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BtnAtur.setIconTextGap(0);
        BtnAtur.setMargin(new java.awt.Insets(2, 5, 3, 8));
        BtnAtur.setName("BtnAtur"); // NOI18N
        BtnAtur.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BtnAturMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BtnAturMouseExited(evt);
            }
        });
        BtnAtur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAturActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(BtnAtur, gridBagConstraints);

        CbJenis.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        CbJenis.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tarik Tunai", "Setor Tunai", "Top Up" }));
        CbJenis.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        CbJenis.setName("CbJenis"); // NOI18N
        CbJenis.setPreferredSize(new java.awt.Dimension(126, 30));
        CbJenis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CbJenisActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(CbJenis, gridBagConstraints);

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
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(TxtTotal, gridBagConstraints);

        line.setName("line"); // NOI18N

        javax.swing.GroupLayout lineLayout = new javax.swing.GroupLayout(line);
        line.setLayout(lineLayout);
        lineLayout.setHorizontalGroup(
            lineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        lineLayout.setVerticalGroup(
            lineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(line, gridBagConstraints);

        LblInfoTitle.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        LblInfoTitle.setText("Total");
        LblInfoTitle.setName("LblInfoTitle"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        Form.add(LblInfoTitle, gridBagConstraints);

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
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(TxtBayar, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel9.setText("Bayar");
        jLabel9.setName("jLabel9"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(jLabel9, gridBagConstraints);

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
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(TxtKembali, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel10.setText("Kembali");
        jLabel10.setName("jLabel10"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(jLabel10, gridBagConstraints);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(jScrollPane1, gridBagConstraints);

        jLabel11.setText("Catatan");
        jLabel11.setName("jLabel11"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        Form.add(jLabel11, gridBagConstraints);

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
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(btn1, gridBagConstraints);

        jLabel12.setText("Tanggal");
        jLabel12.setName("jLabel12"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        Form.add(jLabel12, gridBagConstraints);

        jCheckBox1.setText("Cetak Struk Sekaligus?");
        jCheckBox1.setContentAreaFilled(false);
        jCheckBox1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jCheckBox1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jCheckBox1.setName("jCheckBox1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        Form.add(jCheckBox1, gridBagConstraints);

        jSplitPane1.setRightComponent(Form);

        jScrollPane3.setBorder(null);
        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        CashCard.setBackground(new java.awt.Color(255, 255, 255));
        CashCard.setName("CashCard"); // NOI18N

        jLabel3.setFont(new java.awt.Font("Poppins Light", 0, 12)); // NOI18N
        jLabel3.setText("Transaksi Hari ini");
        jLabel3.setName("jLabel3"); // NOI18N

        saldoCash.setFont(new java.awt.Font("Poppins Medium", 1, 16)); // NOI18N
        saldoCash.setText("29");
        saldoCash.setName("saldoCash"); // NOI18N

        javax.swing.GroupLayout CashCardLayout = new javax.swing.GroupLayout(CashCard);
        CashCard.setLayout(CashCardLayout);
        CashCardLayout.setHorizontalGroup(
            CashCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CashCardLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CashCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(saldoCash, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(92, Short.MAX_VALUE))
        );
        CashCardLayout.setVerticalGroup(
            CashCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CashCardLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(2, 2, 2)
                .addComponent(saldoCash)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 12, 12);
        jPanel2.add(CashCard, gridBagConstraints);

        BriCard.setBackground(new java.awt.Color(0, 102, 255));
        BriCard.setName("BriCard"); // NOI18N

        jLabel4.setFont(new java.awt.Font("Poppins Light", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Pendapatan Hari ini");
        jLabel4.setName("jLabel4"); // NOI18N

        saldoCash1.setFont(new java.awt.Font("Poppins SemiBold", 1, 16)); // NOI18N
        saldoCash1.setForeground(new java.awt.Color(255, 255, 255));
        saldoCash1.setText("Rp 2.300.000");
        saldoCash1.setName("saldoCash1"); // NOI18N

        BtnHist.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        BtnHist.setForeground(new java.awt.Color(9, 87, 195));
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

        javax.swing.GroupLayout BriCardLayout = new javax.swing.GroupLayout(BriCard);
        BriCard.setLayout(BriCardLayout);
        BriCardLayout.setHorizontalGroup(
            BriCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BriCardLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(BriCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(saldoCash1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(BriCardLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(50, 50, 50)))
                .addGap(135, 135, 135)
                .addComponent(BtnHist)
                .addGap(14, 14, 14))
        );
        BriCardLayout.setVerticalGroup(
            BriCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BriCardLayout.createSequentialGroup()
                .addGroup(BriCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(BriCardLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)
                        .addGap(0, 0, 0)
                        .addComponent(saldoCash1))
                    .addGroup(BriCardLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(BtnHist, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(BriCard, gridBagConstraints);

        CashCard1.setBackground(new java.awt.Color(255, 255, 255));
        CashCard1.setName("CashCard1"); // NOI18N

        jLabel8.setFont(new java.awt.Font("Poppins Light", 0, 12)); // NOI18N
        jLabel8.setText("Fee Hari ini");
        jLabel8.setName("jLabel8"); // NOI18N

        saldoCash2.setFont(new java.awt.Font("Poppins Medium", 1, 16)); // NOI18N
        saldoCash2.setText("Rp 300.000");
        saldoCash2.setName("saldoCash2"); // NOI18N

        javax.swing.GroupLayout CashCard1Layout = new javax.swing.GroupLayout(CashCard1);
        CashCard1.setLayout(CashCard1Layout);
        CashCard1Layout.setHorizontalGroup(
            CashCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CashCard1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CashCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(saldoCash2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(62, 62, 62))
        );
        CashCard1Layout.setVerticalGroup(
            CashCard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CashCard1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(0, 0, 0)
                .addComponent(saldoCash2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 0);
        jPanel2.add(CashCard1, gridBagConstraints);

        CashCard3.setBackground(new java.awt.Color(255, 255, 255));
        CashCard3.setName("CashCard3"); // NOI18N

        jLabel14.setFont(new java.awt.Font("Poppins Light", 0, 10)); // NOI18N
        jLabel14.setText("Top Up");
        jLabel14.setName("jLabel14"); // NOI18N

        saldoCash4.setFont(new java.awt.Font("Poppins Medium", 1, 12)); // NOI18N
        saldoCash4.setText("12");
        saldoCash4.setName("saldoCash4"); // NOI18N

        javax.swing.GroupLayout CashCard3Layout = new javax.swing.GroupLayout(CashCard3);
        CashCard3.setLayout(CashCard3Layout);
        CashCard3Layout.setHorizontalGroup(
            CashCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CashCard3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CashCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(saldoCash4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(27, 27, 27))
        );
        CashCard3Layout.setVerticalGroup(
            CashCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CashCard3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addGap(2, 2, 2)
                .addComponent(saldoCash4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 0);
        jPanel2.add(CashCard3, gridBagConstraints);

        CashCard4.setBackground(new java.awt.Color(255, 255, 255));
        CashCard4.setName("CashCard4"); // NOI18N

        jLabel15.setFont(new java.awt.Font("Poppins Light", 0, 10)); // NOI18N
        jLabel15.setText("tarik Tunai");
        jLabel15.setName("jLabel15"); // NOI18N

        saldoCash5.setFont(new java.awt.Font("Poppins Medium", 1, 12)); // NOI18N
        saldoCash5.setText("4");
        saldoCash5.setName("saldoCash5"); // NOI18N

        javax.swing.GroupLayout CashCard4Layout = new javax.swing.GroupLayout(CashCard4);
        CashCard4.setLayout(CashCard4Layout);
        CashCard4Layout.setHorizontalGroup(
            CashCard4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CashCard4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CashCard4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saldoCash5, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );
        CashCard4Layout.setVerticalGroup(
            CashCard4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CashCard4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addGap(2, 2, 2)
                .addComponent(saldoCash5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 12, 12);
        jPanel2.add(CashCard4, gridBagConstraints);

        CashCard2.setBackground(new java.awt.Color(255, 255, 255));
        CashCard2.setName("CashCard2"); // NOI18N

        jLabel13.setFont(new java.awt.Font("Poppins Light", 0, 10)); // NOI18N
        jLabel13.setText("Setor / Transfer Tunai");
        jLabel13.setName("jLabel13"); // NOI18N

        saldoCash3.setFont(new java.awt.Font("Poppins Medium", 1, 12)); // NOI18N
        saldoCash3.setText("12");
        saldoCash3.setName("saldoCash3"); // NOI18N

        javax.swing.GroupLayout CashCard2Layout = new javax.swing.GroupLayout(CashCard2);
        CashCard2.setLayout(CashCard2Layout);
        CashCard2Layout.setHorizontalGroup(
            CashCard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CashCard2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CashCard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(saldoCash3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(27, 27, 27))
        );
        CashCard2Layout.setVerticalGroup(
            CashCard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CashCard2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addGap(2, 2, 2)
                .addComponent(saldoCash3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        jPanel2.add(CashCard2, gridBagConstraints);

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
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel2.add(jScrollPane2, gridBagConstraints);

        jLabel1.setText("Transaksi Terbaru");
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        jPanel2.add(jLabel1, gridBagConstraints);

        jPanel3.setName("jPanel3"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.7;
        jPanel2.add(jPanel3, gridBagConstraints);

        jScrollPane3.setViewportView(jPanel2);

        jSplitPane1.setLeftComponent(jScrollPane3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 741, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 963, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BtnAturMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtnAturMouseEntered
        BtnAtur.setForeground(Color.decode("#000000"));
        BtnAtur.setIcon(
            AppIcon.CATEGORY.create(Color.decode("#000000"))
            );
        BtnAtur.putClientProperty(
                "FlatLaf.style",
                   "arc:12;"
                 + "borderWidth:2;"
                 + "borderColor:#000000;"
                 + "background:#ffffff"
            );
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnAturMouseEntered

    private void BtnAturMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtnAturMouseExited
        BtnAtur.setForeground(Color.decode("#0957C3"));
        BtnAtur.setIcon(
            AppIcon.CATEGORY.create(Color.decode("#0957C3"))
            );
        BtnAtur.putClientProperty(
                "FlatLaf.style",
                   "arc:12;"
                 + "borderWidth:2;"
                 + "borderColor:#0957C3;"
                 + "background:#ffffff"
            );
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnAturMouseExited

    private void TxtNominalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtNominalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtNominalActionPerformed

    private void btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActionPerformed
        simpanTransaksi();
    }//GEN-LAST:event_btnActionPerformed

    private void TxtNominalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtNominalKeyReleased
        RupiahFormat.formatTextField(TxtNominal);
        hitungTransaksi();
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtNominalKeyReleased

    private void TxtFeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtFeeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtFeeActionPerformed

    private void TxtFeeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtFeeKeyReleased
        RupiahFormat.formatTextField(TxtFee);
        hitungTransaksi();
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtFeeKeyReleased

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
        RupiahFormat.formatTextField(TxtBayar);
        hitungTransaksi();
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtBayarKeyReleased

    private void TxtKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtKembaliActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtKembaliActionPerformed

    private void TxtKembaliKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtKembaliKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtKembaliKeyReleased

    private void CbKatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CbKatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CbKatActionPerformed

    private void btn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn1ActionPerformed

    private void CbJenisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CbJenisActionPerformed
        updateFormByJenis();
        hitungTransaksi();
        // TODO add your handling code here:
    }//GEN-LAST:event_CbJenisActionPerformed

    private void BtnHistMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtnHistMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnHistMouseEntered

    private void BtnHistMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtnHistMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnHistMouseExited

    private void BtnHistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHistActionPerformed
        CardLayout cl =
        (CardLayout) MainFrame.BRILinkContainer.getLayout();
        cl.show(MainFrame.BRILinkContainer, "RIWAYAT");
// TODO add your handling code here:
    }//GEN-LAST:event_BtnHistActionPerformed

    private void loadKategoriCombo() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement(new KategoriTopup(0, "-- Pilih Kategori --"));
        for (KategoriTopup k : kategoriDAO.getAll()) model.addElement(k);
        CbKat.setModel(model);
    }

    private void loadSaldo() {
        try {
            saldoCash.setText(RupiahFormat.format(brilinkDAO.getSaldoByNamaAkun("Cash").longValue()));
            saldoCash1.setText(RupiahFormat.format(brilinkDAO.getSaldoByNamaAkun("BRI").longValue()));
        } catch (RuntimeException e) {
            saldoCash.setText("Rp 0"); saldoCash1.setText("Rp 0");
        }
    }

    private void loadRiwayatRingkas() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"No", "Tanggal", "Jenis", "Kategori", "Nominal", "Fee"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Brilink b : brilinkDAO.getAll()) model.addRow(new Object[]{b.getNomorTransaksi(), b.getTanggal(), b.getJenis(), b.getKategoriNama() == null ? "-" : b.getKategoriNama(), RupiahFormat.format(b.getNominal().longValue()), RupiahFormat.format(b.getFee().longValue())});
        jTable1.setModel(model);
    }

    private void simpanTransaksi() {
        try {
            String jenis = CbJenis.getSelectedItem().toString();
            BigDecimal nominal = parseMoney(TxtNominal.getText());
            BigDecimal fee = parseMoney(TxtFee.getText());
            if (nominal.compareTo(BigDecimal.ZERO) <= 0) { JOptionPane.showMessageDialog(this, "Nominal wajib diisi dan harus lebih dari 0."); return; }
            if (fee.compareTo(BigDecimal.ZERO) < 0) { JOptionPane.showMessageDialog(this, "Fee wajib diisi dan tidak boleh negatif."); return; }
            Date tanggal = parseTanggal();
            if (tanggal == null) {
                return;
            }
            if ("Tarik Tunai".equals(jenis)) {
                String metodeFee = CbFee.getSelectedItem() == null ? null : CbFee.getSelectedItem().toString();
                if (metodeFee == null || metodeFee.trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Metode fee wajib dipilih untuk Tarik Tunai."); return; }
                if ("Terpotong".equals(metodeFee) && nominal.compareTo(fee) < 0) { JOptionPane.showMessageDialog(this, "Nominal Tarik Tunai Terpotong wajib lebih besar atau sama dengan fee."); return; }
            } else if ("Setor Tunai".equals(jenis) || "Setor / Transfer".equals(jenis) || "Top Up".equals(jenis)) {
                BigDecimal totalBayar = nominal.add(fee);
                BigDecimal bayar = parseMoney(TxtBayar.getText());
                if (bayar.compareTo(totalBayar) < 0) { JOptionPane.showMessageDialog(this, "Bayar wajib lebih besar atau sama dengan total nominal + fee."); return; }
            }
            Brilink b = new Brilink();
            b.setTanggal(tanggal);
            b.setUserId(Session.idUser);
            b.setJenis(jenis);
            b.setNominal(nominal);
            b.setFee(fee);
            b.setCatatan(jTextArea1.getText());
            if ("Tarik Tunai".equals(jenis)) {
                b.setMetodeFee(CbFee.getSelectedItem().toString());
            } else {
                b.setMetodeFee(null);
            }
            if ("Top Up".equals(jenis)) {
                KategoriTopup kategori = (KategoriTopup) CbKat.getSelectedItem();
                if (kategori == null || kategori.getIdKategori() == 0) { JOptionPane.showMessageDialog(this, "Kategori wajib dipilih untuk Top Up."); return; }
                b.setKategoriId(kategori.getIdKategori());
            }
            brilinkDAO.insert(b);
            JOptionPane.showMessageDialog(this, "Transaksi BRILink berhasil disimpan.");
            clearTransaksi(); loadKategoriCombo(); loadSaldo(); loadRiwayatRingkas(); refreshSaldoHeader();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi: " + rootMessage(e));
        }
    }

    private Date parseTanggal() {
        if (datePicker == null || !datePicker.isDateSelected() || datePicker.getSelectedDate() == null) {
            JOptionPane.showMessageDialog(this, "Tanggal transaksi wajib diisi.");
            return null;
        }

        String editorText = TxtTgl.getText() == null ? "" : TxtTgl.getText().trim();
        String selectedText = datePicker.getSelectedDateAsString();
        if (editorText.isEmpty() || (selectedText != null && !editorText.equals(selectedText))) {
            JOptionPane.showMessageDialog(this, "Tanggal transaksi wajib diisi.");
            return null;
        }

        LocalDate selectedDate = datePicker.getSelectedDate();
        return Date.valueOf(selectedDate);
    }

    private BigDecimal parseMoney(String text) { return BigDecimal.valueOf((long) RupiahFormat.parse(text)); }

    private void refreshSaldoHeader() {
        if (saldoRefreshCallback != null) {
            saldoRefreshCallback.run();
            return;
        }
        java.awt.Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof MainFrame) {
            ((MainFrame) window).refreshSaldoHeader();
        }
    }
    private String rootMessage(Throwable e) { Throwable t=e; while(t.getCause()!=null) t=t.getCause(); return t.getMessage(); }
    private void clearTransaksi() { TxtNominal.setText(""); TxtFee.setText(""); TxtBayar.setText(""); TxtTotal.setText(""); TxtKembali.setText(""); jTextArea1.setText(""); }

    private void BtnAturActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAturActionPerformed
        CardLayout cl =
        (CardLayout) MainFrame.BRILinkContainer.getLayout();
        cl.show(MainFrame.BRILinkContainer, "KATEGORI");
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnAturActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BriCard;
    private javax.swing.JButton BtnAtur;
    private javax.swing.JButton BtnHist;
    private javax.swing.JPanel CashCard;
    private javax.swing.JPanel CashCard1;
    private javax.swing.JPanel CashCard2;
    private javax.swing.JPanel CashCard3;
    private javax.swing.JPanel CashCard4;
    private javax.swing.JComboBox<String> CbFee;
    private javax.swing.JComboBox<String> CbJenis;
    private javax.swing.JComboBox<String> CbKat;
    private javax.swing.JPanel Form;
    private javax.swing.JLabel LblInfoTitle;
    private javax.swing.JTextField TxtBayar;
    private javax.swing.JTextField TxtFee;
    private javax.swing.JTextField TxtKembali;
    private javax.swing.JTextField TxtNominal;
    private javax.swing.JFormattedTextField TxtTgl;
    private javax.swing.JTextField TxtTotal;
    private javax.swing.JButton btn;
    private javax.swing.JButton btn1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPanel line;
    private javax.swing.JLabel saldoCash;
    private javax.swing.JLabel saldoCash1;
    private javax.swing.JLabel saldoCash2;
    private javax.swing.JLabel saldoCash3;
    private javax.swing.JLabel saldoCash4;
    private javax.swing.JLabel saldoCash5;
    // End of variables declaration//GEN-END:variables
}
