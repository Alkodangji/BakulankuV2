package model;

public class Produk {

    private int idProduk;
    private String kodeProduk;
    private String namaProduk;
    private double hargaBeli;
    private double hargaJual;
    private int stok;
    private int stokMinimum;

    public Produk() {
    }

    public Produk(int idProduk, String kodeProduk, String namaProduk, double hargaBeli, double hargaJual, int stok, int stokMinimum) {
        this.idProduk = idProduk;
        this.kodeProduk = kodeProduk;
        this.namaProduk = namaProduk;
        this.hargaBeli = hargaBeli;
        this.hargaJual = hargaJual;
        this.stok = stok;
        this.stokMinimum = stokMinimum;
    }

    public int getIdProduk() {
        return idProduk;
    }

    public void setIdProduk(int idProduk) {
        this.idProduk = idProduk;
    }

    public String getKodeProduk() {
        return kodeProduk;
    }

    public void setKodeProduk(String kodeProduk) {
        this.kodeProduk = kodeProduk;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public double getHargaBeli() {
        return hargaBeli;
    }

    public void setHargaBeli(double hargaBeli) {
        this.hargaBeli = hargaBeli;
    }

    public double getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(double hargaJual) {
        this.hargaJual = hargaJual;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public int getStokMinimum() {
        return stokMinimum;
    }

    public void setStokMinimum(int stokMinimum) {
        this.stokMinimum = stokMinimum;
    }
}
