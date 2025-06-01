package model;

/**
 * Kelas representasi Motor, mewarisi dari Kendaraan.
 * Menambahkan properti spesifik seperti memiliki helm.
 */
public class Motor extends Kendaraan {
    private boolean hasHelm; // Properti spesifik untuk Motor

    // Konstruktor default
    public Motor() {
        super();
        this.hasHelm = false;
    }

    // Konstruktor penuh
    public Motor(String id, String merk, String tipe, String platNomor, int tahunProduksi, double hargaPerHari, String keterangan, String statusPeminjaman, boolean hasHelm) {
        super(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, keterangan, statusPeminjaman);
        this.hasHelm = hasHelm;
    }
    
    // Konstruktor yang sesuai dengan yang diharapkan dari AddEditKendaraanDialog.java:177
    public Motor(String id, String merk, String tipe, String platNomor, int tahunProduksi, double hargaPerHari, boolean hasHelm) {
        super(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, hasHelm ? "Termasuk Helm" : "Tidak Termasuk Helm", "Tersedia");
        this.hasHelm = hasHelm;
    }

    public boolean isHasHelm() {
        return hasHelm;
    }

    public void setHasHelm(boolean hasHelm) {
        this.hasHelm = hasHelm;
        // Update keterangan berdasarkan status Helm
        setKeterangan(hasHelm ? "Termasuk Helm" : "Tidak Termasuk Helm");
    }
}