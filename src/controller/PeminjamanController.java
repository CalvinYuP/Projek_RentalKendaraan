package controller;

import model.Kendaraan;
import model.Peminjaman;
import service.KendaraanService;
import service.PeminjamanService;
import service.FileStorageService; // Jika digunakan untuk upload KTP
import util.DateUtil; // Pastikan DateUtil sudah ada dan berisi method formatDate
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PeminjamanController {
    private static final Logger LOGGER = Logger.getLogger(PeminjamanController.class.getName());
    private PeminjamanService peminjamanService;
    private KendaraanService kendaraanService; // Diperlukan untuk cek ketersediaan kendaraan

    public PeminjamanController() {
        this.peminjamanService = new PeminjamanService();
        this.kendaraanService = new KendaraanService();
    }

    /**
     * Mengambil semua daftar peminjaman.
     * @return List objek Peminjaman.
     */
    public List<Peminjaman> getAllPeminjaman() {
        try {
            return peminjamanService.getAllPeminjaman();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting all peminjaman: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Menambahkan peminjaman baru.
     * Akan memperbarui status kendaraan juga.
     * @param peminjaman Objek Peminjaman yang akan ditambahkan.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean addPeminjaman(Peminjaman peminjaman, File ktpFile) {
        try {
            // Upload KTP file if provided
            if (ktpFile != null && ktpFile.exists()) {
                String newPath = FileStorageService.uploadKtp(ktpFile);
                peminjaman.setFileKtpPath(newPath);
            }

            boolean success = peminjamanService.addPeminjaman(peminjaman);
            if (success) {
                // Update status kendaraan menjadi "Dipinjam"
                Kendaraan kendaraan = kendaraanService.getKendaraanById(peminjaman.getIdKendaraan());
                if (kendaraan != null) {
                    kendaraan.setStatusPeminjaman("Dipinjam");
                    kendaraanService.updateKendaraan(kendaraan); // Perbarui status di database
                    LOGGER.log(Level.INFO, "Vehicle ID {0} status updated to 'Dipinjam' for Peminjaman ID {1}", 
                               new Object[]{peminjaman.getIdKendaraan(), peminjaman.getIdPeminjaman()});
                }
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding peminjaman: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Memperbarui data peminjaman yang sudah ada.
     * @param peminjaman Objek Peminjaman dengan data terbaru.
     * @param ktpFile File KTP baru jika ada perubahan, null jika tidak.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean updatePeminjaman(Peminjaman peminjaman, File ktpFile) {
        try {
            // Jika ada file KTP baru, upload dan perbarui path
            if (ktpFile != null && ktpFile.exists()) {
                String newPath = FileStorageService.uploadKtp(ktpFile);
                peminjaman.setFileKtpPath(newPath);
            }
            
            boolean success = peminjamanService.updatePeminjaman(peminjaman);
            if (success && peminjaman.getStatusPeminjaman().equalsIgnoreCase("Selesai")) {
                 // Update status kendaraan menjadi "Tersedia" jika peminjaman selesai
                Kendaraan kendaraan = kendaraanService.getKendaraanById(peminjaman.getIdKendaraan());
                if (kendaraan != null) {
                    kendaraan.setStatusPeminjaman("Tersedia");
                    kendaraanService.updateKendaraan(kendaraan);
                    LOGGER.log(Level.INFO, "Vehicle ID {0} status updated to 'Tersedia' as Peminjaman ID {1} is 'Selesai'.", 
                               new Object[]{peminjaman.getIdKendaraan(), peminjaman.getIdPeminjaman()});
                }
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating peminjaman: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Menghapus peminjaman berdasarkan ID.
     * @param idPeminjaman ID peminjaman yang akan dihapus.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean deletePeminjaman(String idPeminjaman) {
        try {
            // Ambil data peminjaman sebelum dihapus untuk mengupdate status kendaraan
            Peminjaman peminjaman = peminjamanService.getPeminjamanById(idPeminjaman);
            if (peminjaman != null) {
                // Hapus file KTP jika ada
                FileStorageService.deleteKtp(peminjaman.getFileKtpPath());
            }

            boolean success = peminjamanService.deletePeminjaman(idPeminjaman);
            if (success && peminjaman != null) {
                 // Update status kendaraan menjadi "Tersedia" jika peminjaman ini dihapus
                Kendaraan kendaraan = kendaraanService.getKendaraanById(peminjaman.getIdKendaraan());
                if (kendaraan != null) {
                    kendaraan.setStatusPeminjaman("Tersedia");
                    kendaraanService.updateKendaraan(kendaraan);
                    LOGGER.log(Level.INFO, "Vehicle ID {0} status updated to 'Tersedia' as Peminjaman ID {1} was deleted.", 
                               new Object[]{peminjaman.getIdKendaraan(), peminjaman.getIdPeminjaman()});
                }
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting peminjaman with ID " + idPeminjaman + ": " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Mencari peminjaman berdasarkan kata kunci (nama penyewa, plat kendaraan).
     * @param keyword Kata kunci pencarian.
     * @return List peminjaman yang cocok dengan kata kunci.
     */
    public List<Peminjaman> searchPeminjaman(String keyword) {
        try {
            return peminjamanService.searchPeminjaman(keyword);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching peminjaman with keyword '" + keyword + "': " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Memeriksa ketersediaan kendaraan berdasarkan ID.
     * @param idKendaraan ID kendaraan.
     * @return true jika kendaraan tersedia ("Tersedia"), false jika tidak.
     */
    public boolean isKendaraanAvailable(String idKendaraan) {
        try {
            Kendaraan kendaraan = kendaraanService.getKendaraanById(idKendaraan);
            return kendaraan != null && "Tersedia".equalsIgnoreCase(kendaraan.getStatusPeminjaman());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking vehicle availability for ID " + idKendaraan + ": " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Mengambil peminjaman berdasarkan ID.
     * @param idPeminjaman ID peminjaman.
     * @return Objek Peminjaman jika ditemukan, null jika tidak.
     */
    public Peminjaman getPeminjamanById(String idPeminjaman) {
        try {
            return peminjamanService.getPeminjamanById(idPeminjaman);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting peminjaman by ID: " + idPeminjaman, e);
            return null;
        }
    }
    
    /**
     * Memfilter daftar peminjaman berdasarkan bulan.
     * @param monthIndex Indeks bulan (0-11 untuk Januari-Desember).
     * @return List peminjaman pada bulan tersebut.
     */
    public List<Peminjaman> filterByBulan(int monthIndex) {
        List<Peminjaman> allPeminjaman = getAllPeminjaman();
        if (monthIndex < 0 || monthIndex > 11) { // -1 untuk "Semua Bulan" atau nilai tidak valid
            return allPeminjaman;
        }
        
        return allPeminjaman.stream()
            .filter(p -> p.getTanggalMulai() != null && p.getTanggalMulai().getMonth() == Month.of(monthIndex + 1))
            .collect(Collectors.toList());
    }
    
    /**
     * Mengurutkan daftar peminjaman berdasarkan tanggal mulai.
     * @param peminjamanList Daftar peminjaman.
     * @param ascending true untuk urutan menaik, false untuk menurun.
     * @return List peminjaman yang sudah diurutkan.
     */
    public List<Peminjaman> sortPeminjamanByTanggalMulai(List<Peminjaman> peminjamanList, boolean ascending) {
        List<Peminjaman> sortedList = peminjamanList.stream()
                .sorted(Comparator.comparing(Peminjaman::getTanggalMulai))
                .collect(Collectors.toList());
        if (!ascending) {
            Collections.reverse(sortedList);
        }
        return sortedList;
    }
}