package model;

import java.util.Date;

public class Penjualan {

    private int idPenjualan;
    private String nomorTransaksi;
    private Date tanggal;
    private int userId;

    private double total;
    private String metodePembayaran;
    private double diterima;
    private double kembalian;

    public Penjualan() {
    }

    public int getIdPenjualan() {
        return idPenjualan;
    }

    public void setIdPenjualan(int idPenjualan) {
        this.idPenjualan = idPenjualan;
    }

    public String getNomorTransaksi() {
        return nomorTransaksi;
    }

    public void setNomorTransaksi(String nomorTransaksi) {
        this.nomorTransaksi = nomorTransaksi;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getMetodePembayaran() {
        return metodePembayaran;
    }

    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }

    public double getDiterima() {
        return diterima;
    }

    public void setDiterima(double diterima) {
        this.diterima = diterima;
    }

    public double getKembalian() {
        return kembalian;
    }

    public void setKembalian(double kembalian) {
        this.kembalian = kembalian;
    }
}