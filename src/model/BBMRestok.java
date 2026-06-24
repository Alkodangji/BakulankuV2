package model;

import java.sql.Date;

public class BBMRestok {
    private int idRestokBbm, userId, bbmId, akunId;
    private String nomorTransaksi, catatan;
    private Date tanggal;
    private double liter, hargaBeli, total;

    public int getIdRestokBbm() { return idRestokBbm; }
    public void setIdRestokBbm(int idRestokBbm) { this.idRestokBbm = idRestokBbm; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getBbmId() { return bbmId; }
    public void setBbmId(int bbmId) { this.bbmId = bbmId; }
    public int getAkunId() { return akunId; }
    public void setAkunId(int akunId) { this.akunId = akunId; }
    public String getNomorTransaksi() { return nomorTransaksi; }
    public void setNomorTransaksi(String nomorTransaksi) { this.nomorTransaksi = nomorTransaksi; }
    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }
    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }
    public double getLiter() { return liter; }
    public void setLiter(double liter) { this.liter = liter; }
    public double getHargaBeli() { return hargaBeli; }
    public void setHargaBeli(double hargaBeli) { this.hargaBeli = hargaBeli; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
