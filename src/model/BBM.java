package model;

public class BBM {
    private int idBbm;
    private String kodeBbm;
    private String namaBbm;
    private double hargaBeli;
    private double hargaJual;
    private double stok;
    private double stokMinimum;

    public BBM() {}

    public BBM(int idBbm, String kodeBbm, String namaBbm, double hargaBeli, double hargaJual, double stok, double stokMinimum) {
        this.idBbm = idBbm;
        this.kodeBbm = kodeBbm;
        this.namaBbm = namaBbm;
        this.hargaBeli = hargaBeli;
        this.hargaJual = hargaJual;
        this.stok = stok;
        this.stokMinimum = stokMinimum;
    }

    public int getIdBbm() { return idBbm; }
    public void setIdBbm(int idBbm) { this.idBbm = idBbm; }
    public String getKodeBbm() { return kodeBbm; }
    public void setKodeBbm(String kodeBbm) { this.kodeBbm = kodeBbm; }
    public String getNamaBbm() { return namaBbm; }
    public void setNamaBbm(String namaBbm) { this.namaBbm = namaBbm; }
    public double getHargaBeli() { return hargaBeli; }
    public void setHargaBeli(double hargaBeli) { this.hargaBeli = hargaBeli; }
    public double getHargaJual() { return hargaJual; }
    public void setHargaJual(double hargaJual) { this.hargaJual = hargaJual; }
    public double getStok() { return stok; }
    public void setStok(double stok) { this.stok = stok; }
    public double getStokMinimum() { return stokMinimum; }
    public void setStokMinimum(double stokMinimum) { this.stokMinimum = stokMinimum; }

    @Override
    public String toString() { return kodeBbm + " - " + namaBbm; }
}
