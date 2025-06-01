package model;

/**
 * Kelas representasi Elf, mewarisi dari Kendaraan.
 * Menambahkan properti spesifik seperti kapasitas penumpang.
 */
public class Elf extends Kendaraan {
    private int kapasitasPenumpang; // Properti spesifik untuk Elf

    // Konstruktor default
    public Elf() {
        super();
        this.kapasitasPenumpang = 0;
    }

    // Konstruktor penuh
    public Elf(String id, String merk, String tipe, String platNomor, int tahunProduksi, double hargaPerHari, String keterangan, String statusPeminjaman, int kapasitasPenumpang) {
        super(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, keterangan, statusPeminjaman);
        this.kapasitasPenumpang = kapasitasPenumpang;
    }
    
    // Konstruktor yang sesuai dengan yang diharapkan dari AddEditKendaraanDialog.java:180
    public Elf(String id, String merk, String tipe, String platNomor, int tahunProduksi, double hargaPerHari, int kapasitasPenumpang) {
        super(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, "Kapasitas " + kapasitasPenumpang + " orang", "Tersedia");
        this.kapasitasPenumpang = kapasitasPenumpang;
    }

    public int getKapasitasPenumpang() {
        return kapasitasPenumpang;
    }

    public void setKapasitasPenumpang(int kapasitasPenumpang) {
        this.kapasitasPenumpang = kapasitasPenumpang;
        // Update keterangan berdasarkan kapasitas
        setKeterangan("Kapasitas " + kapasitasPenumpang + " orang");
    }
}