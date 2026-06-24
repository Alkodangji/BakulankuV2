package model;

public class Akun {

    private int idAkun;
    private String namaAkun;
    private double saldo;

    public Akun() {
    }

    public Akun(int idAkun, String namaAkun, double saldo) {
        this.idAkun = idAkun;
        this.namaAkun = namaAkun;
        this.saldo = saldo;
    }

    public int getIdAkun() {
        return idAkun;
    }

    public void setIdAkun(int idAkun) {
        this.idAkun = idAkun;
    }

    public String getNamaAkun() {
        return namaAkun;
    }

    public void setNamaAkun(String namaAkun) {
        this.namaAkun = namaAkun;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}
