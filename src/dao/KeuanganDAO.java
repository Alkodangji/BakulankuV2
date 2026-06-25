package dao;

import config.Koneksi;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import model.KategoriKeuangan;
import model.KeuanganTransaksi;

public class KeuanganDAO {
    private final AkunDAO akunDAO = new AkunDAO();

    public List<KategoriKeuangan> getKategoriByJenis(String jenis) throws Exception {
        List<KategoriKeuangan> list = new ArrayList<>();
        String sql = "SELECT id_kategori, nama_kategori, jenis FROM tb_kategori_keuangan WHERE jenis = ? ORDER BY nama_kategori";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, jenis);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new KategoriKeuangan(rs.getInt("id_kategori"), rs.getString("nama_kategori"), rs.getString("jenis")));
                }
            }
        }
        return list;
    }

    public DefaultTableModel getKategoriTableModel() throws Exception {
        DefaultTableModel model = new DefaultTableModel(new Object[] {"ID", "Nama Kategori", "Jenis"}, 0);
        String sql = "SELECT id_kategori, nama_kategori, jenis FROM tb_kategori_keuangan ORDER BY jenis, nama_kategori";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[] {rs.getInt("id_kategori"), rs.getString("nama_kategori"), rs.getString("jenis")});
            }
        }
        return model;
    }

    public void tambahKategori(String namaKategori, String jenis) throws Exception {
        String sql = "INSERT INTO tb_kategori_keuangan (nama_kategori, jenis) VALUES (?, ?)";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaKategori);
            ps.setString(2, jenis);
            ps.executeUpdate();
        }
    }

    public void simpanTransaksi(KeuanganTransaksi transaksi) throws Exception {
        Connection conn = null;
        try {
            conn = Koneksi.getConnection();
            if (conn == null) {
                throw new Exception("Koneksi database gagal.");
            }
            conn.setAutoCommit(false);
            validasiTransaksi(conn, transaksi);
            insertTransaksi(conn, transaksi, generateNomorTransaksi(conn, transaksi.getJenis()));
            updateSaldo(conn, transaksi);
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception rollbackError) { rollbackError.printStackTrace(); }
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (Exception closeError) { closeError.printStackTrace(); }
            }
        }
    }

    private void validasiTransaksi(Connection conn, KeuanganTransaksi transaksi) throws Exception {
        if (transaksi.getNominal() <= 0) {
            throw new Exception("Nominal harus lebih dari 0.");
        }
        if ("Pemasukan".equals(transaksi.getJenis())) {
            if (transaksi.getAkunTujuanId() == null || transaksi.getIdKategori() == null) throw new Exception("Akun tujuan dan kategori pemasukan wajib diisi.");
            return;
        }
        if ("Pengeluaran".equals(transaksi.getJenis())) {
            if (transaksi.getAkunAsalId() == null || transaksi.getIdKategori() == null) throw new Exception("Akun asal dan kategori pengeluaran wajib diisi.");
            cekSaldoCukup(conn, transaksi.getAkunAsalId(), transaksi.getNominal());
            return;
        }
        if ("Transfer".equals(transaksi.getJenis())) {
            if (transaksi.getAkunAsalId() == null || transaksi.getAkunTujuanId() == null) throw new Exception("Akun asal dan tujuan transfer wajib diisi.");
            if (transaksi.getAkunAsalId().equals(transaksi.getAkunTujuanId())) throw new Exception("Akun asal dan tujuan transfer tidak boleh sama.");
            cekSaldoCukup(conn, transaksi.getAkunAsalId(), transaksi.getNominal());
            return;
        }
        throw new Exception("Jenis transaksi tidak valid.");
    }

    private void cekSaldoCukup(Connection conn, int idAkun, double nominal) throws Exception {
        if (akunDAO.getSaldo(conn, idAkun) < nominal) {
            throw new Exception("Saldo akun asal tidak cukup.");
        }
    }

    private void insertTransaksi(Connection conn, KeuanganTransaksi transaksi, String nomorTransaksi) throws Exception {
        String sql = "INSERT INTO tb_keuangan (nomor_transaksi, tanggal, user_id, akun_asal_id, akun_tujuan_id, jenis, nominal, kategori, catatan, id_kategori) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomorTransaksi);
            ps.setDate(2, Date.valueOf(transaksi.getTanggal()));
            ps.setInt(3, transaksi.getUserId());
            setNullableInt(ps, 4, transaksi.getAkunAsalId());
            setNullableInt(ps, 5, transaksi.getAkunTujuanId());
            ps.setString(6, transaksi.getJenis());
            ps.setDouble(7, transaksi.getNominal());
            ps.setString(8, transaksi.getKategori());
            ps.setString(9, transaksi.getCatatan());
            setNullableInt(ps, 10, transaksi.getIdKategori());
            ps.executeUpdate();
        }
    }

    private void updateSaldo(Connection conn, KeuanganTransaksi transaksi) throws Exception {
        if ("Pemasukan".equals(transaksi.getJenis())) {
            if (!akunDAO.tambahSaldo(conn, transaksi.getAkunTujuanId(), transaksi.getNominal())) throw new Exception("Gagal menambah saldo akun tujuan.");
        } else if ("Pengeluaran".equals(transaksi.getJenis())) {
            if (!akunDAO.kurangiSaldo(conn, transaksi.getAkunAsalId(), transaksi.getNominal())) throw new Exception("Gagal mengurangi saldo akun asal.");
        } else if ("Transfer".equals(transaksi.getJenis())) {
            if (!akunDAO.kurangiSaldo(conn, transaksi.getAkunAsalId(), transaksi.getNominal())) throw new Exception("Gagal mengurangi saldo akun asal.");
            if (!akunDAO.tambahSaldo(conn, transaksi.getAkunTujuanId(), transaksi.getNominal())) throw new Exception("Gagal menambah saldo akun tujuan.");
        }
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws Exception {
        if (value == null) ps.setNull(index, java.sql.Types.INTEGER); else ps.setInt(index, value);
    }

    private String generateNomorTransaksi(Connection conn, String jenis) throws Exception {
        String kode = "Pemasukan".equals(jenis) ? "KMS" : "Pengeluaran".equals(jenis) ? "KKR" : "KTF";
        String tanggal = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String prefix = kode + "-" + tanggal + "-";
        String sql = "SELECT COUNT(*) FROM tb_keuangan WHERE nomor_transaksi LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                int urut = rs.next() ? rs.getInt(1) + 1 : 1;
                return prefix + String.format("%03d", urut);
            }
        }
    }

    public DefaultTableModel getRiwayatTableModel() throws Exception {
        DefaultTableModel model = new DefaultTableModel(new Object[] {"No Transaksi", "Tanggal", "Jenis", "Kategori", "Nominal", "Akun Asal", "Akun Tujuan", "Catatan"}, 0);
        String sql = "SELECT k.nomor_transaksi, k.tanggal, k.jenis, COALESCE(kk.nama_kategori, k.kategori, '-') kategori, k.nominal, "
                + "COALESCE(asal.nama_akun, '-') akun_asal, COALESCE(tujuan.nama_akun, '-') akun_tujuan, COALESCE(k.catatan, '') catatan "
                + "FROM tb_keuangan k "
                + "LEFT JOIN tb_kategori_keuangan kk ON kk.id_kategori = k.id_kategori "
                + "LEFT JOIN tb_akun asal ON asal.id_akun = k.akun_asal_id "
                + "LEFT JOIN tb_akun tujuan ON tujuan.id_akun = k.akun_tujuan_id "
                + "ORDER BY k.tanggal DESC, k.id_keuangan DESC";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[] {rs.getString("nomor_transaksi"), rs.getDate("tanggal"), rs.getString("jenis"), rs.getString("kategori"), rs.getDouble("nominal"), rs.getString("akun_asal"), rs.getString("akun_tujuan"), rs.getString("catatan")});
            }
        }
        return model;
    }
}
