/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view.main;


import com.formdev.flatlaf.*;
import helper.FontIcon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import view.bbm.BBMPanel;
import view.brilink.BRILinkPanel;
import view.dashboard.DashboardPanel;
import view.keuangan.KeuanganPanel;
import view.laporan.LaporanPanel;
import view.penjualan.PenjualanPanel;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.CardLayout;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


/**
 *
 * @author ASUS
 */
public class MainFrame extends javax.swing.JFrame {

    /**
     * Creates new form MainFrame
     */
    public static JPanel BBMContainer;
    public static JPanel BRILinkContainer;
    public static JPanel PenjualanContainer;
    public static JPanel KeuanganContainer;
    
    public static view.penjualan.Riwayat RiwayatPenjualanPanel;
    
    
    
    
    public MainFrame() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/shop.png")));
        
        initComponents();
        
        
//        Font Poopins
        FlatLaf.setPreferredFontFamily("Poppins");
        SwingUtilities.updateComponentTreeUI(this);
       
        
//        Saldo Header
        Bri.putClientProperty(
    "FlatLaf.style",
    "arc:12;border:2,2,2,2,#0466c8"
);
        Cash.putClientProperty(
    "FlatLaf.style",
    "arc:12;border:2,2,2,2,#009900"
);
        LblT1.putClientProperty(
    "FlatLaf.style",
    "arc:8"
);
        LblT2.putClientProperty(
    "FlatLaf.style",
    "arc:8"
);
        
        
//        Icon SVG
    int width = 110;
    int height = width * 43 / 232;

FlatSVGIcon logo =
    new FlatSVGIcon(
        "assets/icon/logo.svg",
        width,
        height
    );

LblLogo.setIcon(logo);
        

        BBMContainer = BBM;
        BRILinkContainer = BRILink;
        PenjualanContainer = Penjualan;
        KeuanganContainer= Keuangan;

    //  Load All Panel     
        Home.setLayout(new BorderLayout());
        Home.add(new DashboardPanel(),BorderLayout.CENTER);

        BBM.setLayout(new CardLayout());
        BBM.add(new view.bbm.BBMPanel(), "HOME");
        BBM.add(new view.bbm.Data(), "DATA");
        BBM.add(new view.bbm.Restok(), "RESTOK");
        BBM.add(new view.bbm.Riwayat(), "RIWAYAT");

        Penjualan.setLayout(new CardLayout());

        RiwayatPenjualanPanel = new view.penjualan.Riwayat();

        Penjualan.add(new view.penjualan.PenjualanPanel(), "HOME");
        Penjualan.add(new view.penjualan.Data(), "DATA");
        Penjualan.add(new view.penjualan.Restok(), "RESTOK");
        Penjualan.add(RiwayatPenjualanPanel, "RIWAYAT");

        Keuangan.setLayout(new CardLayout());
        Keuangan.add(new view.keuangan.KeuanganPanel(), "HOME");
        Keuangan.add(new view.keuangan.Kategori(), "KATEGORI");
        Keuangan.add(new view.keuangan.Riwayat(), "RIWAYAT");

        BRILink.setLayout(new CardLayout());
        BRILink.add(new view.brilink.BRILinkPanel(), "HOME");
        BRILink.add(new view.brilink.Riwayat(), "RIWAYAT");
        BRILink.add(new view.brilink.Kategori(), "KATEGORI");
        
        Laporan.setLayout(new BorderLayout());
        Laporan.add(new LaporanPanel(),BorderLayout.CENTER);
        
        // Load Icon
        Font outlinedFont, filledFont;
        try (InputStream isOutlined = getClass().getResourceAsStream("/assets/fonts/MaterialIconsOutlined-Regular.otf");
             InputStream isFilled = getClass().getResourceAsStream("/assets/fonts/MaterialIcons-Regular.ttf")) {
            outlinedFont = Font.createFont(Font.TRUETYPE_FONT, isOutlined);
            filledFont = Font.createFont(Font.TRUETYPE_FONT, isFilled);
        } catch (Exception e) {
            e.printStackTrace();
            outlinedFont = new Font("Segoe UI", Font.PLAIN, 18);
            filledFont = outlinedFont;
        }
        
        final Font finalOutlinedFont = outlinedFont;
        final Font finalFilledFont = filledFont;
        
        FontIcon[] icons = {
            new FontIcon("\uE88A", finalOutlinedFont.deriveFont(18f), Color.BLACK, 24, 24), 
            new FontIcon("\uE8CC", finalOutlinedFont.deriveFont(18f), Color.BLACK, 24, 24), 
            new FontIcon("\uE546", finalOutlinedFont.deriveFont(18f), Color.BLACK, 24, 24), 
            new FontIcon("\uE84F", finalOutlinedFont.deriveFont(18f), Color.BLACK, 24, 24), 
            new FontIcon("\uE227", finalOutlinedFont.deriveFont(18f), Color.BLACK, 24, 24), 
            new FontIcon("\uE915", finalOutlinedFont.deriveFont(18f), Color.BLACK, 24, 24)  
        };

        for (int i = 0; i < icons.length; i++) {
            sidebarTabs.setIconAt(i, icons[i]);
        }

        sidebarTabs.addChangeListener(e -> {
            int selectedIndex = sidebarTabs.getSelectedIndex();

            // Perbarui ikon: tab aktif → Filled, lainnya → Outlined
            for (int i = 0; i < icons.length; i++) {
                FontIcon icon = icons[i];
                if (i == selectedIndex) {
                    icon.setFont(finalFilledFont.deriveFont(18f));
                } else {
                    icon.setFont(finalOutlinedFont.deriveFont(18f));
                }
            }sidebarTabs.repaint();
        }
        );
        
        sidebarTabs.setSelectedIndex(0);
        icons[0].setFont(finalFilledFont.deriveFont(18f));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
            }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        Header = new javax.swing.JPanel();
        Bri = new javax.swing.JPanel();
        LblBri = new javax.swing.JLabel();
        lblMenu1 = new javax.swing.JLabel();
        LblT1 = new javax.swing.JLabel();
        Cash = new javax.swing.JPanel();
        LblCash = new javax.swing.JLabel();
        lblMenu3 = new javax.swing.JLabel();
        LblT2 = new javax.swing.JLabel();
        BtnOut = new javax.swing.JButton();
        LblJudul = new javax.swing.JLabel();
        LblLogo = new javax.swing.JLabel();
        sidebarTabs = new javax.swing.JTabbedPane();
        Home = new javax.swing.JPanel();
        BRILink = new javax.swing.JPanel();
        BBM = new javax.swing.JPanel();
        Penjualan = new javax.swing.JPanel();
        Keuangan = new javax.swing.JPanel();
        Laporan = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(java.awt.Color.lightGray);

        jPanel4.setBackground(new java.awt.Color(230, 230, 230));
        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.BorderLayout());

        Header.setBackground(new java.awt.Color(255, 255, 255));
        Header.setName("Header"); // NOI18N
        Header.setPreferredSize(new java.awt.Dimension(10, 40));

        Bri.setBackground(new java.awt.Color(255, 255, 255));
        Bri.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(9, 87, 195), 2));
        Bri.setName("Bri"); // NOI18N
        Bri.setOpaque(false);

        LblBri.setFont(new java.awt.Font("Poppins Medium", 1, 10)); // NOI18N
        LblBri.setForeground(new java.awt.Color(4, 102, 200));
        LblBri.setText("1.200.000");
        LblBri.setName("LblBri"); // NOI18N

        lblMenu1.setFont(new java.awt.Font("Poppins Medium", 1, 10)); // NOI18N
        lblMenu1.setForeground(new java.awt.Color(4, 102, 200));
        lblMenu1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMenu1.setText("Rp");
        lblMenu1.setName("lblMenu1"); // NOI18N

        LblT1.setBackground(new java.awt.Color(4, 102, 200));
        LblT1.setFont(new java.awt.Font("Poppins Medium", 1, 14)); // NOI18N
        LblT1.setForeground(new java.awt.Color(255, 255, 255));
        LblT1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LblT1.setText("BRI");
        LblT1.setDoubleBuffered(true);
        LblT1.setName("LblT1"); // NOI18N
        LblT1.setOpaque(true);

        javax.swing.GroupLayout BriLayout = new javax.swing.GroupLayout(Bri);
        Bri.setLayout(BriLayout);
        BriLayout.setHorizontalGroup(
            BriLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BriLayout.createSequentialGroup()
                .addComponent(LblT1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(lblMenu1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LblBri, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        BriLayout.setVerticalGroup(
            BriLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BriLayout.createSequentialGroup()
                .addComponent(LblT1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(BriLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(LblBri, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(lblMenu1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Cash.setBackground(new java.awt.Color(255, 255, 255));
        Cash.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 0), 2));
        Cash.setName("Cash"); // NOI18N
        Cash.setPreferredSize(new java.awt.Dimension(138, 25));

        LblCash.setFont(new java.awt.Font("Poppins Medium", 1, 10)); // NOI18N
        LblCash.setForeground(new java.awt.Color(0, 153, 0));
        LblCash.setText("1.100.000");
        LblCash.setName("LblCash"); // NOI18N

        lblMenu3.setFont(new java.awt.Font("Poppins Medium", 1, 10)); // NOI18N
        lblMenu3.setForeground(new java.awt.Color(0, 153, 0));
        lblMenu3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMenu3.setText("Rp");
        lblMenu3.setName("lblMenu3"); // NOI18N

        LblT2.setBackground(new java.awt.Color(0, 153, 0));
        LblT2.setFont(new java.awt.Font("Poppins Medium", 1, 14)); // NOI18N
        LblT2.setForeground(new java.awt.Color(255, 255, 255));
        LblT2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LblT2.setText("CASH");
        LblT2.setName("LblT2"); // NOI18N
        LblT2.setOpaque(true);

        javax.swing.GroupLayout CashLayout = new javax.swing.GroupLayout(Cash);
        Cash.setLayout(CashLayout);
        CashLayout.setHorizontalGroup(
            CashLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CashLayout.createSequentialGroup()
                .addComponent(LblT2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(lblMenu3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(LblCash, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );
        CashLayout.setVerticalGroup(
            CashLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LblCash, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblMenu3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(LblT2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        BtnOut.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        BtnOut.setForeground(new java.awt.Color(255, 0, 0));
        BtnOut.setText("Logout");
        BtnOut.setBorder(null);
        BtnOut.setBorderPainted(false);
        BtnOut.setContentAreaFilled(false);
        BtnOut.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BtnOut.setName("BtnOut"); // NOI18N
        BtnOut.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BtnOutMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BtnOutMouseExited(evt);
            }
        });
        BtnOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnOutActionPerformed(evt);
            }
        });

        LblJudul.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        LblJudul.setText("Home");
        LblJudul.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        LblJudul.setName("LblJudul"); // NOI18N

        LblLogo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LblLogo.setText(" ");
        LblLogo.setName("LblLogo"); // NOI18N

        javax.swing.GroupLayout HeaderLayout = new javax.swing.GroupLayout(Header);
        Header.setLayout(HeaderLayout);
        HeaderLayout.setHorizontalGroup(
            HeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HeaderLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(LblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(LblJudul, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 422, Short.MAX_VALUE)
                .addComponent(Bri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Cash, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BtnOut, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        HeaderLayout.setVerticalGroup(
            HeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HeaderLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(HeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LblJudul, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HeaderLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(HeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Cash, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Bri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnOut, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8))
        );

        jPanel4.add(Header, java.awt.BorderLayout.NORTH);

        sidebarTabs.setBackground(new java.awt.Color(255, 255, 255));
        sidebarTabs.setForeground(new java.awt.Color(102, 102, 102));
        sidebarTabs.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        sidebarTabs.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        sidebarTabs.setFont(new java.awt.Font("Poppins Medium", 0, 18)); // NOI18N
        sidebarTabs.setMinimumSize(new java.awt.Dimension(10, 36));
        sidebarTabs.setName("sidebarTabs"); // NOI18N
        sidebarTabs.setOpaque(true);
        sidebarTabs.setPreferredSize(new java.awt.Dimension(140, 2000));
        sidebarTabs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sidebarTabsStateChanged(evt);
            }
        });

        Home.setForeground(new java.awt.Color(255, 0, 0));
        Home.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Home.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Home.setName("Home"); // NOI18N
        Home.setPreferredSize(new java.awt.Dimension(8, 372));

        javax.swing.GroupLayout HomeLayout = new javax.swing.GroupLayout(Home);
        Home.setLayout(HomeLayout);
        HomeLayout.setHorizontalGroup(
            HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 938, Short.MAX_VALUE)
        );
        HomeLayout.setVerticalGroup(
            HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2000, Short.MAX_VALUE)
        );

        sidebarTabs.addTab("Home", Home);

        BRILink.setForeground(new java.awt.Color(255, 0, 0));
        BRILink.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        BRILink.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        BRILink.setName("BRILink"); // NOI18N
        BRILink.setPreferredSize(new java.awt.Dimension(8, 372));

        javax.swing.GroupLayout BRILinkLayout = new javax.swing.GroupLayout(BRILink);
        BRILink.setLayout(BRILinkLayout);
        BRILinkLayout.setHorizontalGroup(
            BRILinkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 938, Short.MAX_VALUE)
        );
        BRILinkLayout.setVerticalGroup(
            BRILinkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2000, Short.MAX_VALUE)
        );

        sidebarTabs.addTab("BRILink", BRILink);

        BBM.setForeground(new java.awt.Color(255, 0, 0));
        BBM.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        BBM.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        BBM.setName("BBM"); // NOI18N
        BBM.setPreferredSize(new java.awt.Dimension(8, 372));

        javax.swing.GroupLayout BBMLayout = new javax.swing.GroupLayout(BBM);
        BBM.setLayout(BBMLayout);
        BBMLayout.setHorizontalGroup(
            BBMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        BBMLayout.setVerticalGroup(
            BBMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2000, Short.MAX_VALUE)
        );

        sidebarTabs.addTab("BBM", BBM);

        Penjualan.setForeground(new java.awt.Color(255, 0, 0));
        Penjualan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Penjualan.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Penjualan.setName("Penjualan"); // NOI18N
        Penjualan.setPreferredSize(new java.awt.Dimension(8, 372));

        javax.swing.GroupLayout PenjualanLayout = new javax.swing.GroupLayout(Penjualan);
        Penjualan.setLayout(PenjualanLayout);
        PenjualanLayout.setHorizontalGroup(
            PenjualanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        PenjualanLayout.setVerticalGroup(
            PenjualanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2000, Short.MAX_VALUE)
        );

        sidebarTabs.addTab("Penjualan", Penjualan);

        Keuangan.setForeground(new java.awt.Color(255, 0, 0));
        Keuangan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Keuangan.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Keuangan.setName("Keuangan"); // NOI18N
        Keuangan.setPreferredSize(new java.awt.Dimension(8, 372));

        javax.swing.GroupLayout KeuanganLayout = new javax.swing.GroupLayout(Keuangan);
        Keuangan.setLayout(KeuanganLayout);
        KeuanganLayout.setHorizontalGroup(
            KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        KeuanganLayout.setVerticalGroup(
            KeuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2000, Short.MAX_VALUE)
        );

        sidebarTabs.addTab("Keuangan", Keuangan);

        Laporan.setForeground(new java.awt.Color(255, 0, 0));
        Laporan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Laporan.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Laporan.setName("Laporan"); // NOI18N
        Laporan.setPreferredSize(new java.awt.Dimension(8, 372));

        javax.swing.GroupLayout LaporanLayout = new javax.swing.GroupLayout(Laporan);
        Laporan.setLayout(LaporanLayout);
        LaporanLayout.setHorizontalGroup(
            LaporanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 938, Short.MAX_VALUE)
        );
        LaporanLayout.setVerticalGroup(
            LaporanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2000, Short.MAX_VALUE)
        );

        sidebarTabs.addTab("Laporan", Laporan);

        jPanel4.add(sidebarTabs, java.awt.BorderLayout.CENTER);
        sidebarTabs.getAccessibleContext().setAccessibleName("Home");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 1058, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnOutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnOutActionPerformed

    private void sidebarTabsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sidebarTabsStateChanged
        LblJudul.setText(
        sidebarTabs.getTitleAt(
            sidebarTabs.getSelectedIndex()
        )
    );        // TODO add your handling code here:
    }//GEN-LAST:event_sidebarTabsStateChanged

    private void BtnOutMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtnOutMouseEntered
        BtnOut.setForeground(Color.BLACK);
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnOutMouseEntered

    private void BtnOutMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtnOutMouseExited
        BtnOut.setForeground(Color.RED);
    // TODO add your handling code here:
    }//GEN-LAST:event_BtnOutMouseExited

 

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
    FlatLaf.registerCustomDefaultsSource("theme");

    FlatLightLaf.setup();
        
    java.awt.EventQueue.invokeLater(() -> {
    new MainFrame().setVisible(true);
    }); 
}

   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BBM;
    private javax.swing.JPanel BRILink;
    private javax.swing.JPanel Bri;
    private javax.swing.JButton BtnOut;
    private javax.swing.JPanel Cash;
    private javax.swing.JPanel Header;
    private javax.swing.JPanel Home;
    private javax.swing.JPanel Keuangan;
    private javax.swing.JPanel Laporan;
    private javax.swing.JLabel LblBri;
    private javax.swing.JLabel LblCash;
    private javax.swing.JLabel LblJudul;
    private javax.swing.JLabel LblLogo;
    private javax.swing.JLabel LblT1;
    private javax.swing.JLabel LblT2;
    private javax.swing.JPanel Penjualan;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lblMenu1;
    private javax.swing.JLabel lblMenu3;
    private javax.swing.JTabbedPane sidebarTabs;
    // End of variables declaration//GEN-END:variables
}
