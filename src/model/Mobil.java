package model;

/**
 * Kelas representasi Mobil, mewarisi dari Kendaraan.
 * Menambahkan properti spesifik seperti memiliki AC.
 */
public class Mobil extends Kendaraan {
    private boolean hasAC; // Properti spesifik untuk Mobil

    // Konstruktor default
    public Mobil() {
        super();
        this.hasAC = false;
    }

    // Konstruktor penuh yang cocok dengan yang diminta di AddEditKendaraanDialog
    public Mobil(String id, String merk, String tipe, String platNomor, int tahunProduksi, double hargaPerHari, String keterangan, String statusPeminjaman, boolean hasAC) {
        super(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, keterangan, statusPeminjaman);
        this.hasAC = hasAC;
    }

    // Konstruktor dengan parameter minimal, jika diperlukan (bisa disesuaikan)
    public Mobil(String merk, String tipe, String platNomor, int tahunProduksi, double hargaPerHari, boolean hasAC) {
        super(null, merk, tipe, platNomor, tahunProduksi, hargaPerHari, hasAC ? "AC" : "Non-AC", "Tersedia");
        this.hasAC = hasAC;
    }
    
    // Konstruktor yang sesuai dengan yang diharapkan dari AddEditKendaraanDialog.java:174
    // Ini adalah yang paling penting untuk memperbaiki error konstruktor.
    public Mobil(String id, String merk, String tipe, String platNomor, int tahunProduksi, double hargaPerHari, boolean hasAC) {
        super(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, hasAC ? "Ada AC" : "Tidak Ada AC", "Tersedia");
        this.hasAC = hasAC;
    }


    public boolean isHasAC() {
        return hasAC;
    }

    public void setHasAC(boolean hasAC) {
        this.hasAC = hasAC;
        // Update keterangan berdasarkan status AC
        setKeterangan(hasAC ? "Ada AC" : "Tidak Ada AC");
    }
}