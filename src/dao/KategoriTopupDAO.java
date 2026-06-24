package dao;

import config.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.KategoriTopup;

public class KategoriTopupDAO {
    public boolean insert(KategoriTopup kategori) {
        String sql = "INSERT INTO tb_kategori_topup (nama_kategori) VALUES (?)";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kategori.getNamaKategori());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    public boolean update(KategoriTopup kategori) {
        String sql = "UPDATE tb_kategori_topup SET nama_kategori=? WHERE id_kategori=?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kategori.getNamaKategori()); ps.setInt(2, kategori.getIdKategori());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    public boolean delete(int idKategori) {
        String sql = "DELETE FROM tb_kategori_topup WHERE id_kategori=?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKategori); return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    public List<KategoriTopup> getAll() {
        List<KategoriTopup> list = new ArrayList<>();
        String sql = "SELECT id_kategori, nama_kategori FROM tb_kategori_topup ORDER BY nama_kategori";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new KategoriTopup(rs.getInt("id_kategori"), rs.getString("nama_kategori")));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
    public boolean isDuplicate(String namaKategori) {
        String sql = "SELECT COUNT(*) FROM tb_kategori_topup WHERE LOWER(nama_kategori)=LOWER(?)";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaKategori.trim());
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    public KategoriTopup findById(int idKategori) {
        String sql = "SELECT id_kategori, nama_kategori FROM tb_kategori_topup WHERE id_kategori=?";
        try (Connection conn = Koneksi.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKategori);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return new KategoriTopup(rs.getInt(1), rs.getString(2)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }
}
