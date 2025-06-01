package model;

/**
 * Kelas representasi Bus, mewarisi dari Kendaraan.
 * Menambahkan properti spesifik seperti kapasitas penumpang.
 */
public class Bus extends Kendaraan {
    private int kapasitasPenumpang; // Properti spesifik untuk Bus

    // Konstruktor default
    public Bus() {
        super();
        this.kapasitasPenumpang = 0;
    }

    // Konstruktor penuh
    public Bus(String id, String merk, String tipe, String platNomor, int tahunProduksi, double hargaPerHari, String keterangan, String statusPeminjaman, int kapasitasPenumpang) {
        super(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, keterangan, statusPeminjaman);
        this.kapasitasPenumpang = kapasitasPenumpang;
    }
    
    // Konstruktor yang sesuai dengan yang diharapkan dari AddEditKendaraanDialog.java:183
    public Bus(String id, String merk, String tipe, String platNomor, int tahunProduksi, double hargaPerHari, int kapasitasPenumpang) {
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