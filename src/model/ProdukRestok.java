package model;

import java.sql.Date;

public class ProdukRestok {

    private int idRestok;
    private String nomorTransaksi;
    private Date tanggal;
    private int userId;
    private int produkId;
    private int akunId;
    private int qty;
    private double hargaBeli;
    private double total;
    private String catatan;

    public int getIdRestok() { return idRestok; }
    public void setIdRestok(int idRestok) { this.idRestok = idRestok; }
    public String getNomorTransaksi() { return nomorTransaksi; }
    public void setNomorTransaksi(String nomorTransaksi) { this.nomorTransaksi = nomorTransaksi; }
    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getProdukId() { return produkId; }
    public void setProdukId(int produkId) { this.produkId = produkId; }
    public int getAkunId() { return akunId; }
    public void setAkunId(int akunId) { this.akunId = akunId; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
    public double getHargaBeli() { return hargaBeli; }
    public void setHargaBeli(double hargaBeli) { this.hargaBeli = hargaBeli; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }
}
