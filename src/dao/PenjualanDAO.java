package dao;

import config.Koneksi;
import helper.RupiahFormat;
import model.Penjualan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;

public class PenjualanDAO {

    public int insert(Connection conn, Penjualan penjualan) throws Exception {
        String sql = "INSERT INTO tb_penjualan "
                + "(nomor_transaksi, tanggal, user_id, total, metode_pembayaran, diterima, kembalian) "
                + "VALUES (?, NOW(), ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, penjualan.getNomorTransaksi());
            ps.setInt(2, penjualan.getUserId());
            ps.setDouble(3, penjualan.getTotal());
            ps.setString(4, penjualan.getMetodePembayaran());
            ps.setDouble(5, penjualan.getDiterima());
            ps.setDouble(6, penjualan.getKembalian());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public Penjualan findById(Connection conn, int idPenjualan) throws Exception {
        String sql = "SELECT id_penjualan, nomor_transaksi, tanggal, user_id, total, metode_pembayaran, diterima, kembalian "
                + "FROM tb_penjualan WHERE id_penjualan = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPenjualan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Penjualan p = new Penjualan();
                    p.setIdPenjualan(rs.getInt("id_penjualan"));
                    p.setNomorTransaksi(rs.getString("nomor_transaksi"));
                    p.setTanggal(rs.getTimestamp("tanggal"));
                    p.setUserId(rs.getInt("user_id"));
                    p.setTotal(rs.getDouble("total"));
                    p.setMetodePembayaran(rs.getString("metode_pembayaran"));
                    p.setDiterima(rs.getDouble("diterima"));
                    p.setKembalian(rs.getDouble("kembalian"));
                    return p;
                }
            }
        }
        return null;
    }

    public boolean delete(Connection conn, int idPenjualan) throws Exception {
        String sql = "DELETE FROM tb_penjualan WHERE id_penjualan = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPenjualan);
            return ps.executeUpdate() > 0;
        }
    }

    public int insert(Penjualan penjualan) {

        try {

            Connection conn =
                    Koneksi.getConnection();

            String sql =
                    "INSERT INTO tb_penjualan "
                  + "(nomor_transaksi, tanggal, user_id, total, metode_pembayaran, diterima, kembalian) "
                  + "VALUES (?, NOW(), ?, ?, ?, ?, ?)";

            PreparedStatement ps =
                    conn.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            ps.setString(
                    1,
                    penjualan.getNomorTransaksi()
            );

            ps.setInt(
                    2,
                    penjualan.getUserId()
            );

            ps.setDouble(
                    3,
                    penjualan.getTotal()
            );

            ps.setString(
                    4,
                    penjualan.getMetodePembayaran()
            );

            ps.setDouble(
                    5,
                    penjualan.getDiterima()
            );

            ps.setDouble(
                    6,
                    penjualan.getKembalian()
            );

            ps.executeUpdate();

            ResultSet rs =
                    ps.getGeneratedKeys();

            if(rs.next()){

                return rs.getInt(1);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return 0;
    }
    
    public DefaultTableModel getRiwayatPenjualan() {

    DefaultTableModel model = new DefaultTableModel();

    model.addColumn("ID");
    model.addColumn("No Transaksi");
    model.addColumn("Tanggal");
    model.addColumn("Total");
    model.addColumn("Metode");
    model.addColumn("Diterima");
    model.addColumn("Kembalian");

    try {

        Connection conn = Koneksi.getConnection();

        String sql =
            "SELECT id_penjualan, nomor_transaksi, tanggal, total, "
          + "metode_pembayaran, diterima, kembalian "
          + "FROM tb_penjualan "
          + "ORDER BY tanggal DESC";

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            model.addRow(new Object[]{
                rs.getInt("id_penjualan"),
                rs.getString("nomor_transaksi"),
                rs.getString("tanggal"),
                RupiahFormat.format(rs.getDouble("total")),
                rs.getString("metode_pembayaran"),
                RupiahFormat.format(rs.getDouble("diterima")),
                RupiahFormat.format(rs.getDouble("kembalian"))
            });
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return model;
}
    
    public ResultSet getDetailByPenjualanId(int idPenjualan) {

    try {

        Connection conn = Koneksi.getConnection();

        String sql =
                "SELECT produk_id, qty "
              + "FROM tb_penjualan_detail "
              + "WHERE penjualan_id = ?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idPenjualan);

        return ps.executeQuery();

    } catch (Exception e) {

        e.printStackTrace();
    }

    return null;
}
    
    public boolean deleteDetail(int idPenjualan) {

    try {

        Connection conn = Koneksi.getConnection();

        String sql =
                "DELETE FROM tb_penjualan_detail "
              + "WHERE penjualan_id = ?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idPenjualan);

        ps.executeUpdate();
        return true;

    } catch (Exception e) {

        e.printStackTrace();
    }

    return false;
}
    
    public boolean deleteHeader(int idPenjualan) {

    try {

        Connection conn = Koneksi.getConnection();

        String sql =
                "DELETE FROM tb_penjualan "
              + "WHERE id_penjualan = ?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idPenjualan);

        return ps.executeUpdate() > 0;

    } catch (Exception e) {

        e.printStackTrace();
    }

    return false;
}
}