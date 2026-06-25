package model;

public class KategoriKeuangan {
    private int idKategori;
    private String namaKategori;
    private String jenis;

    public KategoriKeuangan(int idKategori, String namaKategori, String jenis) {
        this.idKategori = idKategori;
        this.namaKategori = namaKategori;
        this.jenis = jenis;
    }

    public int getIdKategori() { return idKategori; }
    public String getNamaKategori() { return namaKategori; }
    public String getJenis() { return jenis; }

    @Override
    public String toString() {
        return namaKategori;
    }
}
