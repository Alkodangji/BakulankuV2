package model;

public class TransaksiKeuangan {
    public int idKategoriKeuangan;
    public String kategoriNama; // pemasukan/pengeluaran (opsional)
    public String metodePembayaran; // "Cash" atau "BRI"
    public double nominal;
    public String keterangan;
    public int idUser;
}
