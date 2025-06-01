package model;

import java.io.Serializable;
import java.time.LocalDateTime; // Menggunakan LocalDateTime untuk tanggal dan waktu

/**
 * Kelas representasi data Transaksi pembayaran.
 * Menyimpan detail tentang pembayaran untuk peminjaman.
 */
public class Transaksi implements Serializable {
    private String idTransaksi;
    private String idPeminjaman; // Mengacu ke Peminjaman yang terkait
    private double totalBiaya;
    private String statusPembayaran; // Contoh: "Belum Lunas", "Lunas"
    private LocalDateTime tanggalTransaksi;

    // Konstruktor default
    public Transaksi() {
    }

    // Konstruktor lengkap
    public Transaksi(String idTransaksi, String idPeminjaman, double totalBiaya, String statusPembayaran, LocalDateTime tanggalTransaksi) {
        this.idTransaksi = idTransaksi;
        this.idPeminjaman = idPeminjaman;
        this.totalBiaya = totalBiaya;
        this.statusPembayaran = statusPembayaran;
        this.tanggalTransaksi = tanggalTransaksi;
    }

    // --- Getters ---
    public String getIdTransaksi() {
        return idTransaksi;
    }

    public String getIdPeminjaman() {
        return idPeminjaman;
    }

    public double getTotalBiaya() {
        return totalBiaya;
    }

    public String getStatusPembayaran() {
        return statusPembayaran;
    }

    public LocalDateTime getTanggalTransaksi() {
        return tanggalTransaksi;
    }

    // --- Setters ---
    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public void setIdPeminjaman(String idPeminjaman) {
        this.idPeminjaman = idPeminjaman;
    }

    public void setTotalBiaya(double totalBiaya) {
        this.totalBiaya = totalBiaya;
    }

    public void setStatusPembayaran(String statusPembayaran) {
        this.statusPembayaran = statusPembayaran;
    }

    public void setTanggalTransaksi(LocalDateTime tanggalTransaksi) {
        this.tanggalTransaksi = tanggalTransaksi;
    }

    @Override
    public String toString() {
        return "Transaksi{" +
               "idTransaksi='" + idTransaksi + '\'' +
               ", idPeminjaman='" + idPeminjaman + '\'' +
               ", totalBiaya=" + totalBiaya +
               ", statusPembayaran='" + statusPembayaran + '\'' +
               '}';
    }
}