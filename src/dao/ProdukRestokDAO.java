package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.ProdukRestok;

public class ProdukRestokDAO {

    public boolean tambahRestok(Connection conn, ProdukRestok restok) throws Exception {
        String sql = "INSERT INTO tb_produk_restok "
                + "(nomor_transaksi, tanggal, user_id, produk_id, akun_id, qty, harga_beli, total, catatan) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, restok.getNomorTransaksi());
            ps.setDate(2, restok.getTanggal());
            ps.setInt(3, restok.getUserId());
            ps.setInt(4, restok.getProdukId());
            ps.setInt(5, restok.getAkunId());
            ps.setInt(6, restok.getQty());
            ps.setDouble(7, restok.getHargaBeli());
            ps.setDouble(8, restok.getTotal());
            ps.setString(9, restok.getCatatan());
            return ps.executeUpdate() > 0;
        }
    }
}
