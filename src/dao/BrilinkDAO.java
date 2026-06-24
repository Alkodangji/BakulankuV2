package dao;

import config.Koneksi;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import model.Brilink;

public class BrilinkDAO {
    public boolean insert(Brilink b) {
        Connection conn = Koneksi.getConnection();
        try {
            conn.setAutoCommit(false);
            b.setNomorTransaksi(generateNomorTransaksi(b.getJenis(), b.getTanggal()));
            insertData(conn, b); applySaldoEffect(conn, b); conn.commit(); return true;
        } catch (Exception e) { rollback(conn); throw new RuntimeException(e); }
        finally { restoreClose(conn); }
    }
    public boolean update(Brilink b) {
        Connection conn = Koneksi.getConnection();
        try { conn.setAutoCommit(false); Brilink old = findById(conn, b.getIdBrilink()); if (old == null) throw new SQLException("Transaksi tidak ditemukan"); reverseSaldoEffect(conn, old); updateData(conn, b); applySaldoEffect(conn, b); conn.commit(); return true; }
        catch (Exception e) { rollback(conn); throw new RuntimeException(e); } finally { restoreClose(conn); }
    }
    public boolean delete(int idBrilink) {
        Connection conn = Koneksi.getConnection();
        try { conn.setAutoCommit(false); Brilink old = findById(conn, idBrilink); if (old == null) throw new SQLException("Transaksi tidak ditemukan"); reverseSaldoEffect(conn, old); try (PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_brilink WHERE id_brilink=?")) { ps.setInt(1, idBrilink); ps.executeUpdate(); } conn.commit(); return true; }
        catch (Exception e) { rollback(conn); throw new RuntimeException(e); } finally { restoreClose(conn); }
    }
    public Brilink findById(int idBrilink) { try (Connection c = Koneksi.getConnection()) { return findById(c, idBrilink); } catch (SQLException e) { throw new RuntimeException(e); } }
    public List<Brilink> getAll() {
        List<Brilink> list = new ArrayList<>();
        String sql = "SELECT b.*, k.nama_kategori FROM tb_brilink b LEFT JOIN tb_kategori_topup k ON b.kategori_id=k.id_kategori ORDER BY b.tanggal DESC, b.id_brilink DESC";
        try (Connection c = Koneksi.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
    public String generateNomorTransaksi(String jenis, Date tanggal) {
        String code = jenis.equals("Tarik Tunai") ? "BTT" : jenis.equals("Setor Tunai") ? "BST" : "BTU";
        String date = new SimpleDateFormat("yyyyMMdd").format(tanggal);
        String prefix = code + "-" + date + "-";
        String sql = "SELECT nomor_transaksi FROM tb_brilink WHERE nomor_transaksi LIKE ? ORDER BY nomor_transaksi DESC LIMIT 1";
        try (Connection c = Koneksi.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return prefix + String.format("%03d", Integer.parseInt(rs.getString(1).substring(prefix.length())) + 1); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return prefix + "001";
    }
    public BigDecimal getSaldoByNamaAkun(String namaAkun) { try (Connection c = Koneksi.getConnection()) { return getSaldoByNamaAkun(c, namaAkun); } catch (SQLException e) { throw new RuntimeException(e); } }
    public void updateSaldo(Connection conn, String namaAkun, BigDecimal delta) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE tb_akun SET saldo=saldo+? WHERE nama_akun=?")) { ps.setBigDecimal(1, delta); ps.setString(2, namaAkun); if (ps.executeUpdate() == 0) throw new SQLException("Akun " + namaAkun + " tidak ditemukan"); }
    }
    public void applySaldoEffect(Connection conn, Brilink b) throws SQLException { effect(conn, b, true); }
    public void reverseSaldoEffect(Connection conn, Brilink b) throws SQLException { effect(conn, b, false); }
    private void effect(Connection conn, Brilink b, boolean apply) throws SQLException {
        BigDecimal cash; BigDecimal bri;
        if ("Tarik Tunai".equals(b.getJenis())) { BigDecimal keluar = "Terpotong".equals(b.getMetodeFee()) ? b.getNominal().subtract(b.getFee()) : b.getNominal(); cash = keluar.negate(); bri = b.getNominal(); }
        else { cash = b.getNominal().add(b.getFee()); bri = b.getNominal().negate(); }
        if (!apply) { cash = cash.negate(); bri = bri.negate(); }
        ensureEnough(conn, "Cash", cash); ensureEnough(conn, "BRI", bri); updateSaldo(conn, "Cash", cash); updateSaldo(conn, "BRI", bri);
    }
    private void ensureEnough(Connection conn, String akun, BigDecimal delta) throws SQLException { if (delta.signum() < 0 && getSaldoByNamaAkun(conn, akun).add(delta).signum() < 0) throw new SQLException("Saldo " + akun + " tidak mencukupi"); }
    private BigDecimal getSaldoByNamaAkun(Connection c, String akun) throws SQLException { try (PreparedStatement ps = c.prepareStatement("SELECT saldo FROM tb_akun WHERE nama_akun=?")) { ps.setString(1, akun); try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getBigDecimal(1); throw new SQLException("Akun " + akun + " tidak ditemukan"); } } }
    private void insertData(Connection c, Brilink b) throws SQLException { String s="INSERT INTO tb_brilink (nomor_transaksi,tanggal,user_id,kategori_id,jenis,nominal,fee,metode_fee,catatan) VALUES (?,?,?,?,?,?,?,?,?)"; try(PreparedStatement ps=c.prepareStatement(s)){ fill(ps,b); ps.executeUpdate(); } }
    private void updateData(Connection c, Brilink b) throws SQLException { String s="UPDATE tb_brilink SET nomor_transaksi=?,tanggal=?,user_id=?,kategori_id=?,jenis=?,nominal=?,fee=?,metode_fee=?,catatan=? WHERE id_brilink=?"; try(PreparedStatement ps=c.prepareStatement(s)){ fill(ps,b); ps.setInt(10,b.getIdBrilink()); ps.executeUpdate(); } }
    private void fill(PreparedStatement ps, Brilink b) throws SQLException { ps.setString(1,b.getNomorTransaksi()); ps.setDate(2,b.getTanggal()); ps.setInt(3,b.getUserId()); if(b.getKategoriId()==null) ps.setNull(4, java.sql.Types.INTEGER); else ps.setInt(4,b.getKategoriId()); ps.setString(5,b.getJenis()); ps.setBigDecimal(6,b.getNominal()); ps.setBigDecimal(7,b.getFee()); ps.setString(8,b.getMetodeFee()); ps.setString(9,b.getCatatan()); }
    private Brilink findById(Connection c, int id) throws SQLException { String s="SELECT b.*, k.nama_kategori FROM tb_brilink b LEFT JOIN tb_kategori_topup k ON b.kategori_id=k.id_kategori WHERE b.id_brilink=?"; try(PreparedStatement ps=c.prepareStatement(s)){ ps.setInt(1,id); try(ResultSet rs=ps.executeQuery()){ return rs.next()?map(rs):null; } } }
    private Brilink map(ResultSet rs) throws SQLException { Brilink b=new Brilink(); b.setIdBrilink(rs.getInt("id_brilink")); b.setNomorTransaksi(rs.getString("nomor_transaksi")); b.setTanggal(rs.getDate("tanggal")); b.setUserId(rs.getInt("user_id")); int kid=rs.getInt("kategori_id"); b.setKategoriId(rs.wasNull()?null:kid); b.setKategoriNama(rs.getString("nama_kategori")); b.setJenis(rs.getString("jenis")); b.setNominal(rs.getBigDecimal("nominal")); b.setFee(rs.getBigDecimal("fee")); b.setMetodeFee(rs.getString("metode_fee")); b.setCatatan(rs.getString("catatan")); return b; }
    private void rollback(Connection c){ try{ if(c!=null)c.rollback(); }catch(SQLException e){} }
    private void restoreClose(Connection c){ try{ if(c!=null){ c.setAutoCommit(true); c.close(); }}catch(SQLException e){} }
}
