package dao;

import config.Koneksi;
import model.PenjualanDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
import helper.RupiahFormat;

public class PenjualanDetailDAO {

    public boolean insert(Connection conn, PenjualanDetail detail) throws Exception {
        String sql = "INSERT INTO tb_penjualan_detail "
                + "(penjualan_id, produk_id, qty, harga, subtotal) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detail.getPenjualanId());
            ps.setInt(2, detail.getProdukId());
            ps.setInt(3, detail.getQty());
            ps.setDouble(4, detail.getHarga());
            ps.setDouble(5, detail.getSubtotal());
            return ps.executeUpdate() > 0;
        }
    }

    public java.util.List<PenjualanDetail> getByPenjualanId(Connection conn, int idPenjualan) throws Exception {
        java.util.List<PenjualanDetail> details = new java.util.ArrayList<>();
        String sql = "SELECT id_detail, penjualan_id, produk_id, qty, harga, subtotal "
                + "FROM tb_penjualan_detail WHERE penjualan_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPenjualan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PenjualanDetail d = new PenjualanDetail();
                    d.setIdDetail(rs.getInt("id_detail"));
                    d.setPenjualanId(rs.getInt("penjualan_id"));
                    d.setProdukId(rs.getInt("produk_id"));
                    d.setQty(rs.getInt("qty"));
                    d.setHarga(rs.getDouble("harga"));
                    d.setSubtotal(rs.getDouble("subtotal"));
                    details.add(d);
                }
            }
        }
        return details;
    }

    public boolean deleteByPenjualanId(Connection conn, int idPenjualan) throws Exception {
        String sql = "DELETE FROM tb_penjualan_detail WHERE penjualan_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPenjualan);
            ps.executeUpdate();
            return true;
        }
    }

    public boolean insert(
            PenjualanDetail detail
    ) {

        try {

            Connection conn =
                    Koneksi.getConnection();

            String sql =
                    "INSERT INTO tb_penjualan_detail "
                  + "(penjualan_id, produk_id, qty, harga, subtotal) "
                  + "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps =
                    conn.prepareStatement(sql);

            ps.setInt(
                    1,
                    detail.getPenjualanId()
            );

            ps.setInt(
                    2,
                    detail.getProdukId()
            );

            ps.setInt(
                    3,
                    detail.getQty()
            );

            ps.setDouble(
                    4,
                    detail.getHarga()
            );

            ps.setDouble(
                    5,
                    detail.getSubtotal()
            );

            return ps.executeUpdate() > 0;

        } catch(Exception e) {

            e.printStackTrace();

        }

        return false;
    }

    public DefaultTableModel getDetailTransaksi(int idPenjualan) {

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addColumn("Produk");
        model.addColumn("Qty");
        model.addColumn("Harga");
        model.addColumn("Subtotal");

        try {

            Connection conn =
                    Koneksi.getConnection();

            String sql =
                    "SELECT p.nama_produk, d.qty, d.harga, d.subtotal "
                  + "FROM tb_penjualan_detail d "
                  + "JOIN tb_produk p ON p.id_produk = d.produk_id "
                  + "WHERE d.penjualan_id = ? "
                  + "ORDER BY d.id_detail";

            PreparedStatement ps =
                    conn.prepareStatement(sql);

            ps.setInt(1, idPenjualan);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("nama_produk"),
                    rs.getInt("qty"),
                    RupiahFormat.format(rs.getDouble("harga")),
                    RupiahFormat.format(rs.getDouble("subtotal"))
                });
            }

        } catch(Exception e) {

            e.printStackTrace();

        }

        return model;
    }

}
