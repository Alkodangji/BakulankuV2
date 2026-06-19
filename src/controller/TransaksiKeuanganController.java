package controller;

import config.Koneksi;
import helper.SaldoHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransaksiKeuanganController {

    private static String getJenisKategori(Connection conn, int idKategoriKeuangan) throws SQLException {
        String sql = "SELECT jenis FROM kategori_keuangan WHERE id_kategori_keuangan = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKategoriKeuangan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("jenis"); // "pemasukan" / "pengeluaran"
            }
        }
        throw new RuntimeException("kategori_keuangan tidak ditemukan. id=" + idKategoriKeuangan);
    }

    public static void simpanTransaksiKeuangan(
            int idKategoriKeuangan,
            String metodePembayaran, // "Cash" atau "BRI"
            double nominal,
            int idUser,
            String keterangan
    ) {
        if (nominal <= 0) throw new IllegalArgumentException("nominal harus > 0");
        if (metodePembayaran == null || metodePembayaran.isBlank())
            throw new IllegalArgumentException("metode_pembayaran wajib diisi");

        String insertSql = "INSERT INTO transaksi_keuangan " +
                "(tanggal, nominal, id_kategori_keuangan, metode_pembayaran, id_user, keterangan) " +
                "VALUES (NOW(), ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = Koneksi.getConnection();
            conn.setAutoCommit(false);

            String jenis = getJenisKategori(conn, idKategoriKeuangan); // pemasukan/pengeluaran

            // 1) insert transaksi
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setDouble(1, nominal);
                ps.setInt(2, idKategoriKeuangan);
                ps.setString(3, metodePembayaran);
                ps.setInt(4, idUser);
                ps.setString(5, keterangan);
                ps.executeUpdate();
            }

            // 2) update saldo global (Java)
            if ("pemasukan".equalsIgnoreCase(jenis)) {
                SaldoHelper.tambahSaldo(metodePembayaran, nominal);
            } else if ("pengeluaran".equalsIgnoreCase(jenis)) {
                SaldoHelper.kurangiSaldo(metodePembayaran, nominal);
            } else {
                throw new RuntimeException("jenis kategori tidak valid: " + jenis);
            }

            conn.commit();
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                // ignore rollback error
            }
            throw new RuntimeException("Gagal simpan transaksi_keuangan: " + e.getMessage(), e);
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException ignored) {}
        }
    }
}
