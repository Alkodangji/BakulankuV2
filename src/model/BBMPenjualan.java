package model;

import java.sql.Date;

public class BBMPenjualan {
    private int idPenjualanBbm, userId, bbmId;
    private String nomorTransaksi, metodePembayaran;
    private Date tanggal;
    private double liter, hargaJual, total, diterima, kembalian;

    public int getIdPenjualanBbm() { return idPenjualanBbm; }
    public void setIdPenjualanBbm(int idPenjualanBbm) { this.idPenjualanBbm = idPenjualanBbm; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getBbmId() { return bbmId; }
    public void setBbmId(int bbmId) { this.bbmId = bbmId; }
    public String getNomorTransaksi() { return nomorTransaksi; }
    public void setNomorTransaksi(String nomorTransaksi) { this.nomorTransaksi = nomorTransaksi; }
    public String getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }
    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }
    public double getLiter() { return liter; }
    public void setLiter(double liter) { this.liter = liter; }
    public double getHargaJual() { return hargaJual; }
    public void setHargaJual(double hargaJual) { this.hargaJual = hargaJual; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public double getDiterima() { return diterima; }
    public void setDiterima(double diterima) { this.diterima = diterima; }
    public double getKembalian() { return kembalian; }
    public void setKembalian(double kembalian) { this.kembalian = kembalian; }
}
