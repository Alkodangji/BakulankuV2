package helper;

import config.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SaldoHelper {

    // Tidak perlu lock object lagi, karena kita akan amankan di level database

    /**
     * Mengambil saldo terbaru dari akun.
     */
    public static double getSaldo(String namaAkun) {
        String sql = "SELECT saldo FROM tabel_saldo WHERE nama_akun = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaAkun);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("saldo");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal ambil saldo: " + e.getMessage(), e);
        }
        // Jika akun tidak ada, lempar exception agar tidak silent fail
        throw new RuntimeException("Akun tidak ditemukan: " + namaAkun);
    }

    /**
     * Menambah saldo akun (aman, tanpa cek negatif).
     */
    public static void tambahSaldo(String namaAkun, double nominal) {
        if (nominal <= 0) throw new IllegalArgumentException("Nominal harus positif");
        updateSaldo(namaAkun, nominal, true);
    }

    /**
     * Mengurangi saldo akun. Memastikan saldo cukup dalam SATU transaksi atomik.
     */
    public static void kurangiSaldo(String namaAkun, double nominal) {
        if (nominal <= 0) throw new IllegalArgumentException("Nominal harus positif");
        updateSaldo(namaAkun, nominal, false);
    }

    /**
     * Operasi atomik: dalam satu koneksi, SELECT saldo, lalu UPDATE jika valid.
     */
    private static void updateSaldo(String namaAkun, double nominal, boolean isTambah) {
        String selectSql = "SELECT saldo FROM tabel_saldo WHERE nama_akun = ? FOR UPDATE";
        String updateSql = "UPDATE tabel_saldo SET saldo = saldo " +
                           (isTambah ? "+ ?" : "- ?") +
                           " WHERE nama_akun = ?";

        try (Connection conn = Koneksi.getConnection()) {
            conn.setAutoCommit(false); // mulai transaksi
            try {
                // 1. Kunci dan baca saldo
                double saldoSekarang;
                try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
                    psSelect.setString(1, namaAkun);
                    try (ResultSet rs = psSelect.executeQuery()) {
                        if (!rs.next()) {
                            throw new RuntimeException("Akun tidak ditemukan: " + namaAkun);
                        }
                        saldoSekarang = rs.getDouble("saldo");
                    }
                }

                // 2. Validasi jika pengurangan
                if (!isTambah && saldoSekarang < nominal) {
                    throw new RuntimeException("Saldo tidak cukup untuk akun: " + namaAkun +
                                               " (Saldo: " + saldoSekarang + ", Butuh: " + nominal + ")");
                }

                // 3. Update saldo
                try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                    psUpdate.setDouble(1, nominal);
                    psUpdate.setString(2, namaAkun);
                    psUpdate.executeUpdate();
                }

                conn.commit(); // sukses
            } catch (Exception e) {
                conn.rollback(); // batalkan jika ada error
                throw new RuntimeException("Gagal update saldo: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true); // kembalikan ke default
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal koneksi/transaksi: " + e.getMessage(), e);
        }
    }
}