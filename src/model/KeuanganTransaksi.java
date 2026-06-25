package model;

import java.time.LocalDate;

public class KeuanganTransaksi {
    private String jenis;
    private LocalDate tanggal;
    private int userId;
    private Integer akunAsalId;
    private Integer akunTujuanId;
    private Integer idKategori;
    private String kategori;
    private double nominal;
    private String catatan;

    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }
    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public Integer getAkunAsalId() { return akunAsalId; }
    public void setAkunAsalId(Integer akunAsalId) { this.akunAsalId = akunAsalId; }
    public Integer getAkunTujuanId() { return akunTujuanId; }
    public void setAkunTujuanId(Integer akunTujuanId) { this.akunTujuanId = akunTujuanId; }
    public Integer getIdKategori() { return idKategori; }
    public void setIdKategori(Integer idKategori) { this.idKategori = idKategori; }
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public double getNominal() { return nominal; }
    public void setNominal(double nominal) { this.nominal = nominal; }
    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }
}
