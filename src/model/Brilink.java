package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Brilink {
    private int idBrilink;
    private String nomorTransaksi;
    private Date tanggal;
    private int userId;
    private Integer kategoriId;
    private String kategoriNama;
    private String jenis;
    private BigDecimal nominal;
    private BigDecimal fee;
    private String metodeFee;
    private String catatan;

    public int getIdBrilink() { return idBrilink; }
    public void setIdBrilink(int idBrilink) { this.idBrilink = idBrilink; }
    public String getNomorTransaksi() { return nomorTransaksi; }
    public void setNomorTransaksi(String nomorTransaksi) { this.nomorTransaksi = nomorTransaksi; }
    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public Integer getKategoriId() { return kategoriId; }
    public void setKategoriId(Integer kategoriId) { this.kategoriId = kategoriId; }
    public String getKategoriNama() { return kategoriNama; }
    public void setKategoriNama(String kategoriNama) { this.kategoriNama = kategoriNama; }
    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }
    public BigDecimal getNominal() { return nominal; }
    public void setNominal(BigDecimal nominal) { this.nominal = nominal; }
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
    public String getMetodeFee() { return metodeFee; }
    public void setMetodeFee(String metodeFee) { this.metodeFee = metodeFee; }
    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }
}
