package model;

import java.io.Serializable;
import java.time.LocalDate; // Menggunakan LocalDate untuk tanggal

/**
 * Kelas representasi data Peminjaman.
 * Menyimpan detail tentang transaksi peminjaman kendaraan.
 */
public class Peminjaman implements Serializable {
    private String idPeminjaman;
    private String namaPenyewa;
    private String kontakPenyewa;
    private String idKendaraan; // ID Kendaraan yang dipinjam
    private String platKendaraan; // Plat nomor kendaraan yang dipinjam (untuk kemudahan akses)
    private String jenisKendaraan; // Tambahan: untuk laporan dan transaksi (Mobil, Motor, Elf, Bus)
    private String merkKendaraan; // Tambahan: untuk laporan dan transaksi
    private int lamaPeminjaman; // Dalam hari
    private LocalDate tanggalMulai;
    private LocalDate tanggalKembali;
    private String statusPeminjaman; // Contoh: "Pending", "Aktif", "Selesai", "Dibatalkan"
    private String fileKtpPath; // Path ke file KTP penyewa

    // Konstruktor default
    public Peminjaman() {
    }

    // Konstruktor lengkap
    public Peminjaman(String idPeminjaman, String namaPenyewa, String kontakPenyewa, String idKendaraan, 
                       String platKendaraan, String jenisKendaraan, String merkKendaraan, int lamaPeminjaman, 
                       LocalDate tanggalMulai, LocalDate tanggalKembali, String statusPeminjaman, String fileKtpPath) {
        this.idPeminjaman = idPeminjaman;
        this.namaPenyewa = namaPenyewa;
        this.kontakPenyewa = kontakPenyewa;
        this.idKendaraan = idKendaraan;
        this.platKendaraan = platKendaraan;
        this.jenisKendaraan = jenisKendaraan;
        this.merkKendaraan = merkKendaraan;
        this.lamaPeminjaman = lamaPeminjaman;
        this.tanggalMulai = tanggalMulai;
        this.tanggalKembali = tanggalKembali;
        this.statusPeminjaman = statusPeminjaman;
        this.fileKtpPath = fileKtpPath;
    }

    // --- Getters ---
    public String getIdPeminjaman() {
        return idPeminjaman;
    }

    public String getNamaPenyewa() {
        return namaPenyewa;
    }

    public String getKontakPenyewa() {
        return kontakPenyewa;
    }

    public String getIdKendaraan() {
        return idKendaraan;
    }

    public String getPlatKendaraan() {
        return platKendaraan;
    }

    public String getJenisKendaraan() {
        return jenisKendaraan;
    }

    public String getMerkKendaraan() {
        return merkKendaraan;
    }

    public int getLamaPeminjaman() {
        return lamaPeminjaman;
    }

    public LocalDate getTanggalMulai() {
        return tanggalMulai;
    }

    public LocalDate getTanggalKembali() {
        return tanggalKembali;
    }

    public String getStatusPeminjaman() {
        return statusPeminjaman;
    }

    public String getFileKtpPath() {
        return fileKtpPath;
    }

    // --- Setters ---
    public void setIdPeminjaman(String idPeminjaman) {
        this.idPeminjaman = idPeminjaman;
    }

    public void setNamaPenyewa(String namaPenyewa) {
        this.namaPenyewa = namaPenyewa;
    }

    public void setKontakPenyewa(String kontakPenyewa) {
        this.kontakPenyewa = kontakPenyewa;
    }

    public void setIdKendaraan(String idKendaraan) {
        this.idKendaraan = idKendaraan;
    }

    public void setPlatKendaraan(String platKendaraan) {
        this.platKendaraan = platKendaraan;
    }

    public void setJenisKendaraan(String jenisKendaraan) {
        this.jenisKendaraan = jenisKendaraan;
    }

    public void setMerkKendaraan(String merkKendaraan) {
        this.merkKendaraan = merkKendaraan;
    }

    public void setLamaPeminjaman(int lamaPeminjaman) {
        this.lamaPeminjaman = lamaPeminjaman;
    }

    public void setTanggalMulai(LocalDate tanggalMulai) {
        this.tanggalMulai = tanggalMulai;
    }

    public void setTanggalKembali(LocalDate tanggalKembali) {
        this.tanggalKembali = tanggalKembali;
    }

    public void setStatusPeminjaman(String statusPeminjaman) {
        this.statusPeminjaman = statusPeminjaman;
    }

    public void setFileKtpPath(String fileKtpPath) {
        this.fileKtpPath = fileKtpPath;
    }

    @Override
    public String toString() {
        return "Peminjaman{" +
               "idPeminjaman='" + idPeminjaman + '\'' +
               ", namaPenyewa='" + namaPenyewa + '\'' +
               ", idKendaraan='" + idKendaraan + '\'' +
               ", platKendaraan='" + platKendaraan + '\'' +
               '}';
    }
}