package model;

import java.io.Serializable; // Penting jika Anda berencana menyimpan objek ini

/**
 * Kelas abstrak dasar untuk semua jenis Kendaraan.
 * Mendefinisikan properti umum yang dimiliki oleh semua kendaraan.
 */
public abstract class Kendaraan implements Serializable {
    private String id;
    private String merk;
    private String tipe;
    private String platNomor;
    private int tahunProduksi;
    private double hargaPerHari;
    private String keterangan; // Bisa untuk detail seperti AC, helm, kapasitas, dll.
    private String statusPeminjaman; // "Tersedia", "Dipinjam"

    // Konstruktor default, penting untuk beberapa framework atau deserialisasi
    public Kendaraan() {
        this.statusPeminjaman = "Tersedia"; // Default status
    }

    // Konstruktor dengan parameter untuk inisialisasi
    public Kendaraan(String id, String merk, String tipe, String platNomor, int tahunProduksi, double hargaPerHari, String keterangan, String statusPeminjaman) {
        this.id = id;
        this.merk = merk;
        this.tipe = tipe;
        this.platNomor = platNomor;
        this.tahunProduksi = tahunProduksi;
        this.hargaPerHari = hargaPerHari;
        this.keterangan = keterangan;
        this.statusPeminjaman = statusPeminjaman != null ? statusPeminjaman : "Tersedia"; // Default jika null
    }

    // --- Getters ---
    public String getId() {
        return id;
    }

    public String getMerk() {
        return merk;
    }

    public String getTipe() {
        return tipe;
    }

    public String getPlatNomor() {
        return platNomor;
    }

    public int getTahunProduksi() {
        return tahunProduksi;
    }

    public double getHargaPerHari() {
        return hargaPerHari;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getStatusPeminjaman() {
        return statusPeminjaman;
    }

    // --- Setters ---
    public void setId(String id) {
        this.id = id;
    }

    public void setMerk(String merk) {
        this.merk = merk;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public void setPlatNomor(String platNomor) {
        this.platNomor = platNomor;
    }

    public void setTahunProduksi(int tahunProduksi) {
        this.tahunProduksi = tahunProduksi;
    }

    public void setHargaPerHari(double hargaPerHari) {
        this.hargaPerHari = hargaPerHari;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public void setStatusPeminjaman(String statusPeminjaman) {
        this.statusPeminjaman = statusPeminjaman;
    }

    @Override
    public String toString() {
        return merk + " " + tipe + " (" + platNomor + ")";
    }
}