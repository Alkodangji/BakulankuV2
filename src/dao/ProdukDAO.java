package dao;

import config.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Produk;

public class ProdukDAO {

    public List<Produk> getAllProduk(String keyword) {
        List<Produk> daftarProduk = new ArrayList<>();

        String sql = "SELECT id_produk, kode_produk, nama_produk, harga_beli, harga_jual, stok, stok_minimum "
                + "FROM tb_produk ";

        boolean pakaiKeyword = keyword != null && !keyword.trim().isEmpty();
        if (pakaiKeyword) {
            sql += "WHERE kode_produk LIKE ? OR nama_produk LIKE ? ";
        }
        sql += "ORDER BY nama_produk ASC";

        try (Connection conn = Koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            if (pakaiKeyword) {
                String cari = "%" + keyword.trim() + "%";
                ps.setString(1, cari);
                ps.setString(2, cari);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftarProduk.add(new Produk(
                            rs.getInt("id_produk"),
                            rs.getString("kode_produk"),
                            rs.getString("nama_produk"),
                            rs.getDouble("harga_beli"),
                            rs.getDouble("harga_jual"),
                            rs.getInt("stok"),
                            rs.getInt("stok_minimum")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return daftarProduk;
    }

    public Produk getProdukById(Connection conn, int idProduk) throws Exception {
        String sql = "SELECT id_produk, kode_produk, nama_produk, harga_beli, harga_jual, stok, stok_minimum "
                + "FROM tb_produk WHERE id_produk = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProduk);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Produk(
                            rs.getInt("id_produk"),
                            rs.getString("kode_produk"),
                            rs.getString("nama_produk"),
                            rs.getDouble("harga_beli"),
                            rs.getDouble("harga_jual"),
                            rs.getInt("stok"),
                            rs.getInt("stok_minimum")
                    );
                }
            }
        }

        return null;
    }

    public boolean tambahStokDanUpdateHargaBeli(Connection conn, int idProduk, int qty, double hargaBeliBaru) throws Exception {
        String sql = "UPDATE tb_produk SET stok = stok + ?, harga_beli = ? WHERE id_produk = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setDouble(2, hargaBeliBaru);
            ps.setInt(3, idProduk);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean tambahProduk(Produk produk) {
        String sql = "INSERT INTO tb_produk "
                + "(kode_produk, nama_produk, harga_beli, harga_jual, stok, stok_minimum) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, produk.getKodeProduk());
            ps.setString(2, produk.getNamaProduk());
            ps.setDouble(3, produk.getHargaBeli());
            ps.setDouble(4, produk.getHargaJual());
            ps.setInt(5, produk.getStok());
            ps.setInt(6, produk.getStokMinimum());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateProduk(Produk produk) {
        String sql = "UPDATE tb_produk "
                + "SET kode_produk = ?, nama_produk = ?, harga_beli = ?, harga_jual = ?, stok = ?, stok_minimum = ? "
                + "WHERE id_produk = ?";

        try (Connection conn = Koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, produk.getKodeProduk());
            ps.setString(2, produk.getNamaProduk());
            ps.setDouble(3, produk.getHargaBeli());
            ps.setDouble(4, produk.getHargaJual());
            ps.setInt(5, produk.getStok());
            ps.setInt(6, produk.getStokMinimum());
            ps.setInt(7, produk.getIdProduk());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean hapusProduk(int idProduk) {
        String sql = "DELETE FROM tb_produk WHERE id_produk = ?";

        try (Connection conn = Koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProduk);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public String generateKodeProduk() {
        return "PRD" + String.valueOf(System.currentTimeMillis()).substring(7);
    }

    public boolean kurangiStok(
            int idProduk,
            int qty
    ) {

        try {

            Connection conn =
                    Koneksi.getConnection();

            String sql =
                    "UPDATE tb_produk "
                  + "SET stok = stok - ? "
                  + "WHERE id_produk = ?";

            PreparedStatement ps =
                    conn.prepareStatement(sql);

            ps.setInt(1, qty);
            ps.setInt(2, idProduk);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }
    
    public boolean tambahStok(int idProduk, int qty) {

    try {

        Connection conn = Koneksi.getConnection();

        String sql =
                "UPDATE tb_produk "
              + "SET stok = stok + ? "
              + "WHERE id_produk = ?";

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setInt(1, qty);
        ps.setInt(2, idProduk);

        return ps.executeUpdate() > 0;

    } catch (Exception e) {

        e.printStackTrace();
    }

    return false;
}
}
