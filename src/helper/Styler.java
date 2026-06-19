package helper;

import java.awt.*;
import java.io.InputStream;
import javax.swing.*;

public class Styler {

    // ==================== PALET WARNA GLOBAL ====================
    // Warna utama aplikasi (biru tua) 
    public static final Color PRIMARY = new Color(0x22, 0x62, 0xC6);
    
//    Warna sekunder (putih) 
    public static final Color WHITE = new Color(255, 255, 255);
    
//    Warna latar utama aplikasi (abu sangat muda) 
    public static final Color BACKGROUND = new Color(0xF1, 0xF2, 0xF7);
    
//    Warna latar sidebar (putih sesuai permintaan) 
    public static final Color SIDEBAR_BG = WHITE;
    
//    Warna latar tombol menu aktif 
    public static final Color MENU_ACTIVE_BG = PRIMARY;
    
//    Warna teks tombol menu tidak aktif 
    public static final Color TEXT_INACTIVE = PRIMARY;
    
    // Warna teks tombol menu aktif 
    public static final Color TEXT_ACTIVE = WHITE;
    
    // Warna merah untuk aksi berbahaya (delete, stok minim) 
    public static final Color DANGER = new Color(0xD3, 0x2F, 0x2F);
    
    // Warna teks pada tombol danger 
    public static final Color TEXT_ON_DANGER = WHITE;

    // ==================== TOMBOL SIDEBAR UTAMA ====================
    
    // ==================== KARAKTER IKON (Font Awesome 5 Solid) ====================
    public static final String IC_HOME        = "\uF015"; // fa-home
    public static final String IC_TRANSACTION = "\uF155"; // fa-dollar-sign
    public static final String IC_MASTER_DATA = "\uF1C0"; // fa-database
    public static final String IC_LAPORAN     = "\uF080"; // fa-chart-bar
    public static final String IC_SETTINGS    = "\uF013"; // fa-cog
    public static final String IC_PRODUK      = "\uF07A"; // fa-shopping-cart (contoh)
    public static final String IC_BBM         = "\uF48B"; // fa-gas-pump
    public static final String IC_KATEGORI    = "\uF022"; // fa-list-alt
    
    //
//     * Menerapkan gaya aktif pada tombol menu utama (sidebar).
//     * Latar PRIMARY, teks putih, indikator kiri 3px.
     
    public static void sidebarButtonActive(JButton btn) {
        btn.setOpaque(true);
        btn.setBackground(MENU_ACTIVE_BG);
        btn.setForeground(TEXT_ACTIVE);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, PRIMARY),
            BorderFactory.createEmptyBorder(8, 12, 8, 10)
        ));
    }

    //
//     * Menerapkan gaya tidak aktif pada tombol menu utama.
//     * Latar transparan (putih menyatu dengan sidebar), teks PRIMARY.
     
    public static void sidebarButtonInactive(JButton btn) {
        btn.setOpaque(false);
        btn.setBackground(WHITE);  // tidak terlihat karena opaque false
        btn.setForeground(TEXT_INACTIVE);
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN));
        // Padding kiri 15 (12 + 3) agar posisi teks sama dengan saat aktif
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 10));
    }

    // ==================== TOMBOL SUBMENU ====================
    
    //
//     * Submenu aktif: latar transparan, teks putih sedikit lebih tebal,
//     * dan indikator kecil di kiri agar tidak bentrok dengan indentasi.
     
    public static void submenuButtonActive(JButton btn) {
        btn.setOpaque(true);
        btn.setBackground(new Color(0x1A, 0x50, 0x9A));  // PRIMARY lebih gelap
        btn.setForeground(WHITE);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, PRIMARY),
            BorderFactory.createEmptyBorder(5, 25, 5, 10)
        ));
    }

    //
//     * Submenu tidak aktif: teks PRIMARY, latar putih.
     
    public static void submenuButtonInactive(JButton btn) {
        btn.setOpaque(false);
        btn.setBackground(WHITE);
        btn.setForeground(PRIMARY);
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 28, 5, 10));
    }

    // ==================== WARNA MERAH (DANGER) ====================
    
    //
//     * Tombol dengan aksi berbahaya (Delete, Hapus).
//     * Latar merah, teks putih, tanpa dekorasi khusus.
     
    public static void dangerButton(JButton btn) {
        btn.setBackground(DANGER);
        btn.setForeground(TEXT_ON_DANGER);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD));
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
    }

    //
//     * Label atau teks dengan warna merah (contoh: "Stok Minim").
     
    public static void dangerText(JLabel label) {
        label.setForeground(DANGER);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
    }

    // ==================== UMUM ====================
    
    //
//     * Panel dengan latar putih dan sudut membulat sederhana.
     
    public static void cardPanel(JPanel panel) {
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
    }

    //
//     * Label judul besar.
     
    public static void titleLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(33, 33, 33));
    }

    //
//     * Memuat ikon dari folder /assets/icons/
     
    public static ImageIcon loadIcon(String filename) {
        java.net.URL url = Styler.class.getResource("/assets/icon/" + filename);
        if (url != null) {
            return new ImageIcon(url);
        } else {
            System.err.println("Icon tidak ditemukan: " + filename);
            return new ImageIcon(); // ikon kosong
        }
    }
    
    private static Font awesomeFont = null;

/**
 * Memuat Font Awesome 5 Free Solid dari file OTF.
 * @param size ukuran font (misal 14f)
 * @return Font Awesome yang siap pakai
 */
public static Font getAwesomeFont(float size) {
    if (awesomeFont == null) {
        try (InputStream is = Styler.class.getResourceAsStream("/assets/fonts/Font Awesome 5 Free-Solid-900.otf")) {
            awesomeFont = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Segoe UI", Font.PLAIN, (int) size);
        }
    }
    return awesomeFont.deriveFont(size);
}
}