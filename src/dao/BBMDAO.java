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
import model.BBM;
import model.BBMPenjualan;
import model.BBMRestok;

public class BBMDAO {

    public List<BBM> getAllBBM(String keyword) {
        List<BBM> data = new ArrayList<>();
        String sql = "SELECT id_bbm, kode_bbm, nama_bbm, harga_beli, harga_jual, stok, stok_minimum FROM tb_bbm";
        boolean filter = keyword != null && !keyword.trim().isEmpty();
        if (filter) {
            sql += " WHERE kode_bbm LIKE ? OR nama_bbm LIKE ?";
        }
        sql += " ORDER BY nama_bbm ASC";

        try (Connection conn = Koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            if (filter) {
                String cari = "%" + keyword.trim() + "%";
                ps.setString(1, cari);
                ps.setString(2, cari);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.add(mapBBM(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public BBM getBBMById(Connection conn, int id) throws Exception {
        String sql = "SELECT id_bbm, kode_bbm, nama_bbm, harga_beli, harga_jual, stok, stok_minimum FROM tb_bbm WHERE id_bbm = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapBBM(rs) : null;
            }
        }
    }

    private BBM mapBBM(ResultSet rs) throws Exception {
        return new BBM(
                rs.getInt("id_bbm"),
                rs.getString("kode_bbm"),
                rs.getString("nama_bbm"),
                rs.getDouble("harga_beli"),
                rs.getDouble("harga_jual"),
                rs.getDouble("stok"),
                rs.getDouble("stok_minimum")
        );
    }

    public boolean isKodeDipakai(String kode, int kecualiId) {
        String sql = "SELECT COUNT(*) FROM tb_bbm WHERE kode_bbm = ? AND id_bbm <> ?";
        try (Connection conn = Koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kode);
            ps.setInt(2, kecualiId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public boolean tambahBBM(BBM bbm) {
        String sql = "INSERT INTO tb_bbm(kode_bbm, nama_bbm, harga_beli, harga_jual, stok, stok_minimum) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = Koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            fillBBM(ps, bbm);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBBM(BBM bbm) {
        String sql = "UPDATE tb_bbm SET kode_bbm = ?, nama_bbm = ?, harga_beli = ?, harga_jual = ?, stok = ?, stok_minimum = ? WHERE id_bbm = ?";
        try (Connection conn = Koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            fillBBM(ps, bbm);
            ps.setInt(7, bbm.getIdBbm());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void fillBBM(PreparedStatement ps, BBM bbm) throws Exception {
        ps.setString(1, bbm.getKodeBbm());
        ps.setString(2, bbm.getNamaBbm());
        ps.setDouble(3, bbm.getHargaBeli());
        ps.setDouble(4, bbm.getHargaJual());
        ps.setDouble(5, bbm.getStok());
        ps.setDouble(6, bbm.getStokMinimum());
    }

    public boolean hapusBBM(int id) {
        String sql = "DELETE FROM tb_bbm WHERE id_bbm = ?";
        try (Connection conn = Koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getNamaAkun() {
        List<String> akun = new ArrayList<>();
        String sql = "SELECT nama_akun FROM tb_akun ORDER BY nama_akun ASC";
        try (Connection conn = Koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                akun.add(rs.getString("nama_akun"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return akun;
    }

    public int getAkunIdByNama(String namaAkun) throws Exception {
        try (Connection conn = Koneksi.getConnection()) {
            return getAkunIdByNama(conn, namaAkun);
        }
    }

    private int getAkunIdByNama(Connection conn, String namaAkun) throws Exception {
        String sql = "SELECT id_akun FROM tb_akun WHERE LOWER(nama_akun) = LOWER(?) LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaAkun.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_akun");
                }
            }
        }
        throw new Exception("Akun '" + namaAkun + "' tidak ditemukan di tb_akun.");
    }

    public String generateNomor(Connection conn, String prefix, String table) throws Exception {
        String tanggal = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sql = "SELECT nomor_transaksi FROM " + table + " WHERE nomor_transaksi LIKE ? ORDER BY nomor_transaksi DESC LIMIT 1";
        int urut = 1;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prefix + "-" + tanggal + "-%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    urut = Integer.parseInt(rs.getString("nomor_transaksi").split("-")[2]) + 1;
                }
            }
        }
        return String.format("%s-%s-%03d", prefix, tanggal, urut);
    }

    public void prosesRestok(BBMRestok restok) throws Exception {
        try (Connection conn = Koneksi.getConnection()) {
            conn.setAutoCommit(false);
            try {
                restok.setNomorTransaksi(generateNomor(conn, "BMR", "tb_bbm_restok"));
                BBM bbm = getBBMById(conn, restok.getBbmId());
                if (bbm == null) {
                    throw new Exception("Data BBM tidak ditemukan.");
                }
                double saldo = getSaldoAkun(conn, restok.getAkunId());
                if (saldo < restok.getTotal()) {
                    throw new Exception("Saldo akun tidak mencukupi untuk restok BBM.");
                }
                restok.setHargaBeli(restok.getTotal() / restok.getLiter());
                double stokBaru = bbm.getStok() + restok.getLiter();
                double hargaBeliRataRata = ((bbm.getStok() * bbm.getHargaBeli()) + (restok.getLiter() * restok.getHargaBeli())) / stokBaru;
                insertRestok(conn, restok);
                updateStokDanHargaBeli(conn, restok.getBbmId(), restok.getLiter(), hargaBeliRataRata);
                updateSaldo(conn, restok.getAkunId(), -restok.getTotal());
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private void insertRestok(Connection conn, BBMRestok restok) throws Exception {
        String sql = "INSERT INTO tb_bbm_restok(nomor_transaksi, tanggal, user_id, bbm_id, akun_id, liter, harga_beli, total, catatan) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, restok.getNomorTransaksi());
            ps.setDate(2, restok.getTanggal());
            ps.setInt(3, restok.getUserId());
            ps.setInt(4, restok.getBbmId());
            ps.setInt(5, restok.getAkunId());
            ps.setDouble(6, restok.getLiter());
            ps.setDouble(7, restok.getHargaBeli());
            ps.setDouble(8, restok.getTotal());
            ps.setString(9, restok.getCatatan());
            ps.executeUpdate();
        }
    }

    public void prosesPenjualan(BBMPenjualan penjualan) throws Exception {
        try (Connection conn = Koneksi.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int akunId = getAkunIdByNama(conn, penjualan.getMetodePembayaran());
                penjualan.setNomorTransaksi(generateNomor(conn, "BMP", "tb_bbm_penjualan"));
                BBM bbm = getBBMById(conn, penjualan.getBbmId());
                if (bbm == null) {
                    throw new Exception("Data BBM tidak ditemukan.");
                }
                if (bbm.getStok() < penjualan.getLiter()) {
                    throw new Exception("Stok BBM tidak mencukupi.");
                }
                insertPenjualan(conn, penjualan);
                kurangiStok(conn, penjualan.getBbmId(), penjualan.getLiter());
                updateSaldo(conn, akunId, penjualan.getTotal());
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private void insertPenjualan(Connection conn, BBMPenjualan penjualan) throws Exception {
        String sql = "INSERT INTO tb_bbm_penjualan(nomor_transaksi, tanggal, user_id, bbm_id, liter, harga_jual, total, metode_pembayaran, diterima, kembalian) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, penjualan.getNomorTransaksi());
            ps.setDate(2, penjualan.getTanggal());
            ps.setInt(3, penjualan.getUserId());
            ps.setInt(4, penjualan.getBbmId());
            ps.setDouble(5, penjualan.getLiter());
            ps.setDouble(6, penjualan.getHargaJual());
            ps.setDouble(7, penjualan.getTotal());
            ps.setString(8, penjualan.getMetodePembayaran());
            ps.setDouble(9, penjualan.getDiterima());
            ps.setDouble(10, penjualan.getKembalian());
            ps.executeUpdate();
        }
    }

    public void hapusPenjualanDenganRollback(int idPenjualan) throws Exception {
        try (Connection conn = Koneksi.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PenjualanLama penjualan = getPenjualanLama(conn, idPenjualan);
                int akunId = getAkunIdByNama(conn, penjualan.metodePembayaran);
                double saldo = getSaldoAkun(conn, akunId);
                if (saldo < penjualan.total) {
                    throw new Exception("Saldo akun " + penjualan.metodePembayaran + " tidak cukup untuk rollback penghapusan penjualan.");
                }
                tambahStok(conn, penjualan.bbmId, penjualan.liter);
                updateSaldo(conn, akunId, -penjualan.total);
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_bbm_penjualan WHERE id_penjualan_bbm = ?")) {
                    ps.setInt(1, idPenjualan);
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void hapusRestokDenganRollback(int idRestok) throws Exception {
        try (Connection conn = Koneksi.getConnection()) {
            conn.setAutoCommit(false);
            try {
                RestokLama restok = getRestokLama(conn, idRestok);
                BBM bbm = getBBMById(conn, restok.bbmId);
                if (bbm == null) {
                    throw new Exception("Data BBM tidak ditemukan.");
                }
                if (bbm.getStok() < restok.liter) {
                    throw new Exception("Stok BBM tidak cukup untuk rollback restok. Penghapusan dibatalkan agar stok tidak negatif.");
                }
                kurangiStok(conn, restok.bbmId, restok.liter);
                updateSaldo(conn, restok.akunId, restok.total);
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_bbm_restok WHERE id_restok_bbm = ?")) {
                    ps.setInt(1, idRestok);
                    ps.executeUpdate();
                }
                updateHargaBeliDariSisaRestok(conn, restok.bbmId);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private PenjualanLama getPenjualanLama(Connection conn, int idPenjualan) throws Exception {
        String sql = "SELECT bbm_id, liter, total, metode_pembayaran FROM tb_bbm_penjualan WHERE id_penjualan_bbm = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPenjualan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PenjualanLama(rs.getInt("bbm_id"), rs.getDouble("liter"), rs.getDouble("total"), rs.getString("metode_pembayaran"));
                }
            }
        }
        throw new Exception("Transaksi penjualan BBM tidak ditemukan.");
    }

    private RestokLama getRestokLama(Connection conn, int idRestok) throws Exception {
        String sql = "SELECT bbm_id, akun_id, liter, total FROM tb_bbm_restok WHERE id_restok_bbm = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRestok);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new RestokLama(rs.getInt("bbm_id"), rs.getInt("akun_id"), rs.getDouble("liter"), rs.getDouble("total"));
                }
            }
        }
        throw new Exception("Transaksi restok BBM tidak ditemukan.");
    }

    private double getSaldoAkun(Connection conn, int id) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("SELECT saldo FROM tb_akun WHERE id_akun = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("saldo");
                }
            }
        }
        throw new Exception("Akun tidak ditemukan.");
    }

    private void updateSaldo(Connection conn, int id, double delta) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE tb_akun SET saldo = saldo + ? WHERE id_akun = ?")) {
            ps.setDouble(1, delta);
            ps.setInt(2, id);
            if (ps.executeUpdate() == 0) {
                throw new Exception("Gagal memperbarui saldo akun.");
            }
        }
    }

    private void updateStokDanHargaBeli(Connection conn, int bbmId, double tambahanStok, double hargaBeli) throws Exception {
        String sql = "UPDATE tb_bbm SET stok = stok + ?, harga_beli = ? WHERE id_bbm = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, tambahanStok);
            ps.setDouble(2, hargaBeli);
            ps.setInt(3, bbmId);
            if (ps.executeUpdate() == 0) {
                throw new Exception("Gagal memperbarui stok dan harga beli BBM.");
            }
        }
    }

    private void tambahStok(Connection conn, int bbmId, double liter) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE tb_bbm SET stok = stok + ? WHERE id_bbm = ?")) {
            ps.setDouble(1, liter);
            ps.setInt(2, bbmId);
            if (ps.executeUpdate() == 0) {
                throw new Exception("Gagal menambah stok BBM.");
            }
        }
    }

    private void kurangiStok(Connection conn, int bbmId, double liter) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE tb_bbm SET stok = stok - ? WHERE id_bbm = ? AND stok >= ?")) {
            ps.setDouble(1, liter);
            ps.setInt(2, bbmId);
            ps.setDouble(3, liter);
            if (ps.executeUpdate() == 0) {
                throw new Exception("Gagal mengurangi stok BBM atau stok tidak mencukupi.");
            }
        }
    }

    private void updateHargaBeliDariSisaRestok(Connection conn, int bbmId) throws Exception {
        String sql = "SELECT SUM(liter * harga_beli) AS nilai_stok, SUM(liter) AS total_liter FROM tb_bbm_restok WHERE bbm_id = ?";
        double hargaBeliBaru = 0;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bbmId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getDouble("total_liter") > 0) {
                    hargaBeliBaru = rs.getDouble("nilai_stok") / rs.getDouble("total_liter");
                }
            }
        }
        try (PreparedStatement ps = conn.prepareStatement("UPDATE tb_bbm SET harga_beli = ? WHERE id_bbm = ?")) {
            ps.setDouble(1, hargaBeliBaru);
            ps.setInt(2, bbmId);
            if (ps.executeUpdate() == 0) {
                throw new Exception("Gagal menghitung ulang harga beli rata-rata BBM.");
            }
        }
    }

    public List<Object[]> getRiwayatPenjualan() {
        String sql = "SELECT p.id_penjualan_bbm, p.nomor_transaksi, p.tanggal, b.nama_bbm, p.liter, p.harga_jual, p.total, p.metode_pembayaran, p.diterima, p.kembalian "
                + "FROM tb_bbm_penjualan p JOIN tb_bbm b ON b.id_bbm = p.bbm_id "
                + "ORDER BY p.tanggal DESC, p.id_penjualan_bbm DESC";
        return queryRows(sql, 10);
    }

    public List<Object[]> getRiwayatRestok() {
        String sql = "SELECT r.id_restok_bbm, r.nomor_transaksi, r.tanggal, b.nama_bbm, r.liter, r.harga_beli, r.total, a.nama_akun, r.catatan "
                + "FROM tb_bbm_restok r JOIN tb_bbm b ON b.id_bbm = r.bbm_id JOIN tb_akun a ON a.id_akun = r.akun_id "
                + "ORDER BY r.tanggal DESC, r.id_restok_bbm DESC";
        return queryRows(sql, 9);
    }

    private List<Object[]> queryRows(String sql, int cols) {
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = Koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] row = new Object[cols];
                for (int i = 0; i < cols; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                rows.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    private static class PenjualanLama {
        int bbmId;
        double liter;
        double total;
        String metodePembayaran;

        PenjualanLama(int bbmId, double liter, double total, String metodePembayaran) {
            this.bbmId = bbmId;
            this.liter = liter;
            this.total = total;
            this.metodePembayaran = metodePembayaran;
        }
    }

    private static class RestokLama {
        int bbmId;
        int akunId;
        double liter;
        double total;

        RestokLama(int bbmId, int akunId, double liter, double total) {
            this.bbmId = bbmId;
            this.akunId = akunId;
            this.liter = liter;
            this.total = total;
        }
    }
}
