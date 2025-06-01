package controller;

import model.Transaksi;
import service.TransaksiService;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TransaksiController {
    private static final Logger LOGGER = Logger.getLogger(TransaksiController.class.getName());
    private TransaksiService transaksiService;

    public TransaksiController() {
        this.transaksiService = new TransaksiService();
    }

    /**
     * Mengambil semua daftar transaksi.
     * @return List objek Transaksi.
     */
    public List<Transaksi> getListTransaksi() { // Diubah dari getAllTransaksi sesuai error message
        try {
            return transaksiService.getAllTransaksi();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting all transactions: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Mengambil transaksi berdasarkan ID.
     * @param idTransaksi ID transaksi.
     * @return Objek Transaksi jika ditemukan, null jika tidak.
     */
    public Transaksi getTransaksiById(String idTransaksi) {
        try {
            return transaksiService.getTransaksiById(idTransaksi);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting transaction by ID: " + idTransaksi, e);
            return null;
        }
    }

    /**
     * Menambahkan transaksi baru.
     * @param transaksi Objek Transaksi yang akan ditambahkan.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean addTransaksi(Transaksi transaksi) {
        try {
            return transaksiService.addTransaksi(transaksi);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding transaction: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Memperbarui data transaksi yang sudah ada.
     * @param transaksi Objek Transaksi dengan data terbaru.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean updateTransaksi(Transaksi transaksi) {
        try {
            return transaksiService.updateTransaksi(transaksi);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating transaction: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Menghapus transaksi berdasarkan ID.
     * @param idTransaksi ID transaksi yang akan dihapus.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean deleteTransaksi(String idTransaksi) {
        try {
            return transaksiService.deleteTransaksi(idTransaksi);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting transaction with ID " + idTransaksi + ": " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Memperbarui status pembayaran transaksi.
     * @param idTransaksi ID transaksi.
     * @param newStatus Status pembayaran baru (e.g., "Lunas", "Belum Lunas").
     * @return true jika berhasil, false jika gagal.
     */
    public boolean updateStatusPembayaran(String idTransaksi, String newStatus) {
        try {
            return transaksiService.updateStatusPembayaran(idTransaksi, newStatus);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating payment status for transaction ID " + idTransaksi + ": " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Memfilter daftar transaksi berdasarkan status pembayaran.
     * @param status Status pembayaran ("Lunas", "Belum Lunas", atau null/kosong untuk semua).
     * @return List transaksi yang sesuai dengan status.
     */
    public List<Transaksi> filterByStatusPembayaran(String status) {
        List<Transaksi> allTransaksi = getListTransaksi();
        if (status == null || status.isEmpty() || status.equalsIgnoreCase("Semua")) {
            return allTransaksi;
        }
        return allTransaksi.stream()
                .filter(t -> t.getStatusPembayaran() != null && t.getStatusPembayaran().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    /**
     * Mencari transaksi berdasarkan kata kunci (ID Peminjaman, ID Transaksi).
     * @param keyword Kata kunci pencarian.
     * @return List transaksi yang cocok dengan kata kunci.
     */
    public List<Transaksi> searchTransaksi(String keyword) {
        try {
            return transaksiService.searchTransaksi(keyword);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching transactions with keyword '" + keyword + "': " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}