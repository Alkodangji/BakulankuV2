package dao;

import config.Koneksi;
import model.PenjualanDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PenjualanDetailDAO {

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
}