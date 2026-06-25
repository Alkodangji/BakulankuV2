package dao;

import config.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Akun;

public class AkunDAO {

    public List<Akun> getAllAkun() {
        List<Akun> daftarAkun = new ArrayList<>();
        String sql = "SELECT id_akun, nama_akun, saldo FROM tb_akun ORDER BY nama_akun ASC";

        try (Connection conn = Koneksi.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                daftarAkun.add(new Akun(
                        rs.getInt("id_akun"),
                        rs.getString("nama_akun"),
                        rs.getDouble("saldo")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return daftarAkun;
    }

    public double getSaldo(int idAkun) {
        try (Connection conn = Koneksi.getConnection()) {
            return getSaldo(conn, idAkun);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getSaldo(Connection conn, int idAkun) throws Exception {
        String sql = "SELECT saldo FROM tb_akun WHERE id_akun = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAkun);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("saldo");
                }
            }
        }
        return 0;
    }

    public boolean tambahSaldo(Connection conn, int idAkun, double nominal) throws Exception {
        String sql = "UPDATE tb_akun SET saldo = saldo + ? WHERE id_akun = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, nominal);
            ps.setInt(2, idAkun);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean kurangiSaldo(int idAkun, double nominal) {
        try (Connection conn = Koneksi.getConnection()) {
            return kurangiSaldo(conn, idAkun, nominal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean kurangiSaldo(Connection conn, int idAkun, double nominal) throws Exception {
        String sql = "UPDATE tb_akun SET saldo = saldo - ? WHERE id_akun = ? AND saldo >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, nominal);
            ps.setInt(2, idAkun);
            ps.setDouble(3, nominal);
            return ps.executeUpdate() > 0;
        }
    }
}
