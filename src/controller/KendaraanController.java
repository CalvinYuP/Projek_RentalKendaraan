package controller;

import model.Kendaraan;
import service.KendaraanService;
import java.util.Collections; // Untuk sorting
import java.util.Comparator; // Untuk sorting
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class KendaraanController {
    private static final Logger LOGGER = Logger.getLogger(KendaraanController.class.getName());
    private KendaraanService kendaraanService;

    public KendaraanController() {
        this.kendaraanService = new KendaraanService();
    }

    /**
     * Mengambil semua daftar kendaraan.
     * @return List objek Kendaraan.
     */
    public List<Kendaraan> getAllKendaraan() {
        try {
            return kendaraanService.getAllKendaraan();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting all vehicles: " + e.getMessage(), e);
            return Collections.emptyList(); // Mengembalikan list kosong jika ada error
        }
    }

    /**
     * Menambahkan kendaraan baru.
     * @param kendaraan Objek Kendaraan yang akan ditambahkan.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean addKendaraan(Kendaraan kendaraan) {
        try {
            return kendaraanService.addKendaraan(kendaraan);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding vehicle: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Memperbarui data kendaraan yang sudah ada.
     * @param kendaraan Objek Kendaraan dengan data terbaru.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean updateKendaraan(Kendaraan kendaraan) {
        try {
            return kendaraanService.updateKendaraan(kendaraan);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating vehicle: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Menghapus kendaraan berdasarkan ID.
     * @param id ID kendaraan yang akan dihapus.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean deleteKendaraan(String id) {
        try {
            return kendaraanService.deleteKendaraan(id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting vehicle with ID " + id + ": " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Mencari kendaraan berdasarkan kata kunci (merk, tipe, plat nomor).
     * @param keyword Kata kunci pencarian.
     * @return List kendaraan yang cocok dengan kata kunci.
     */
    public List<Kendaraan> searchKendaraan(String keyword) {
        try {
            return kendaraanService.searchKendaraan(keyword);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching vehicles with keyword '" + keyword + "': " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Mengurutkan daftar kendaraan berdasarkan harga per hari.
     * @param kendaraanList Daftar kendaraan yang akan diurutkan.
     * @param ascending true untuk urutan menaik (termurah ke termahal), false untuk urutan menurun.
     * @return List kendaraan yang sudah diurutkan.
     */
    public List<Kendaraan> sortKendaraanByHarga(List<Kendaraan> kendaraanList, boolean ascending) {
        List<Kendaraan> sortedList = kendaraanList.stream()
                .sorted(Comparator.comparingDouble(Kendaraan::getHargaPerHari))
                .collect(Collectors.toList());
        if (!ascending) {
            Collections.reverse(sortedList);
        }
        return sortedList;
    }
    
    /**
     * Memfilter kendaraan berdasarkan jenis.
     * @param jenis Jenis kendaraan (e.g., "Mobil", "Motor", "Bus", "Elf").
     * @return List kendaraan yang sesuai jenis.
     */
    public List<Kendaraan> filterKendaraanByJenis(String jenis) {
        try {
            List<Kendaraan> allKendaraan = kendaraanService.getAllKendaraan(); // Atau bisa juga dari cache jika ada
            if (jenis == null || jenis.isEmpty() || jenis.equalsIgnoreCase("Semua")) {
                return allKendaraan;
            }
            return allKendaraan.stream()
                .filter(k -> {
                    String className = k.getClass().getSimpleName();
                    return className.equalsIgnoreCase(jenis);
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error filtering vehicles by type '" + jenis + "': " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Mengambil kendaraan berdasarkan ID.
     * @param id ID kendaraan.
     * @return Objek Kendaraan jika ditemukan, null jika tidak.
     */
    public Kendaraan getKendaraanById(String id) {
        try {
            return kendaraanService.getKendaraanById(id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting vehicle by ID: " + id, e);
            return null;
        }
    }
}