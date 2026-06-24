package dao;

import config.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ProdukDAO {

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