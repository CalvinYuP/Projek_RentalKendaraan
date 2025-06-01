package service;

import config.DatabaseConnection;
import model.Bus;
import model.Elf;
import model.Kendaraan;
import model.Mobil;
import model.Motor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // Untuk generate ID unik
import java.util.logging.Level;
import java.util.logging.Logger;

public class KendaraanService {
    private static final Logger LOGGER = Logger.getLogger(KendaraanService.class.getName());

    /**
     * Mengambil semua data kendaraan dari database.
     * @return List objek Kendaraan.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public List<Kendaraan> getAllKendaraan() throws SQLException {
        List<Kendaraan> kendaraanList = new ArrayList<>();
        String sql = "SELECT * FROM kendaraan";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id_kendaraan");
                String jenis = rs.getString("jenis"); // Kolom 'jenis' dari database
                String merk = rs.getString("merk");
                String tipe = rs.getString("tipe");
                String platNomor = rs.getString("plat_nomor");
                int tahunProduksi = rs.getInt("tahun_produksi");
                double hargaPerHari = rs.getDouble("harga_per_hari");
                String keterangan = rs.getString("keterangan");
                String statusPeminjaman = rs.getString("status_peminjaman");

                Kendaraan kendaraan = null;
                switch (jenis.toLowerCase()) {
                    case "mobil":
                        // Asumsi kolom 'keterangan' bisa parse boolean untuk hasAC, atau punya kolom sendiri
                        boolean hasAC = keterangan != null && keterangan.toLowerCase().contains("ac");
                        kendaraan = new Mobil(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, keterangan, statusPeminjaman, hasAC);
                        break;
                    case "motor":
                        // Asumsi kolom 'keterangan' bisa parse boolean untuk hasHelm
                        boolean hasHelm = keterangan != null && keterangan.toLowerCase().contains("helm");
                        kendaraan = new Motor(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, keterangan, statusPeminjaman, hasHelm);
                        break;
                    case "elf":
                    case "bus":
                        // Asumsi kolom 'keterangan' bisa parse int untuk kapasitas
                        int kapasitas = 0;
                        try {
                            String capStr = keterangan.replaceAll("[^0-9]", "").trim();
                            if (!capStr.isEmpty()) {
                                kapasitas = Integer.parseInt(capStr);
                            }
                        } catch (NumberFormatException e) {
                            LOGGER.log(Level.WARNING, "Could not parse capacity from keterangan: " + keterangan, e);
                        }
                        if (jenis.equalsIgnoreCase("elf")) {
                            kendaraan = new Elf(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, keterangan, statusPeminjaman, kapasitas);
                        } else { // bus
                            kendaraan = new Bus(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, keterangan, statusPeminjaman, kapasitas);
                        }
                        break;
                    default:
                        LOGGER.log(Level.WARNING, "Unknown vehicle type: {0} for ID: {1}", new Object[]{jenis, id});
                        break;
                }
                if (kendaraan != null) {
                    kendaraanList.add(kendaraan);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all vehicles from database: " + e.getMessage(), e);
            throw e;
        }
        return kendaraanList;
    }

    /**
     * Mengambil satu data kendaraan berdasarkan ID.
     * @param id ID kendaraan.
     * @return Objek Kendaraan jika ditemukan, null jika tidak.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public Kendaraan getKendaraanById(String id) throws SQLException {
        String sql = "SELECT * FROM kendaraan WHERE id_kendaraan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String jenis = rs.getString("jenis");
                String merk = rs.getString("merk");
                String tipe = rs.getString("tipe");
                String platNomor = rs.getString("plat_nomor");
                int tahunProduksi = rs.getInt("tahun_produksi");
                double hargaPerHari = rs.getDouble("harga_per_hari");
                String keterangan = rs.getString("keterangan");
                String statusPeminjaman = rs.getString("status_peminjaman");

                switch (jenis.toLowerCase()) {
                    case "mobil":
                        boolean hasAC = keterangan != null && keterangan.toLowerCase().contains("ac");
                        return new Mobil(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, keterangan, statusPeminjaman, hasAC);
                    case "motor":
                        boolean hasHelm = keterangan != null && keterangan.toLowerCase().contains("helm");
                        return new Motor(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, keterangan, statusPeminjaman, hasHelm);
                    case "elf":
                        int kapasitasElf = 0;
                        try {
                            String capStr = keterangan.replaceAll("[^0-9]", "").trim();
                            if (!capStr.isEmpty()) kapasitasElf = Integer.parseInt(capStr);
                        } catch (NumberFormatException e) {}
                        return new Elf(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, keterangan, statusPeminjaman, kapasitasElf);
                    case "bus":
                        int kapasitasBus = 0;
                        try {
                            String capStr = keterangan.replaceAll("[^0-9]", "").trim();
                            if (!capStr.isEmpty()) kapasitasBus = Integer.parseInt(capStr);
                        } catch (NumberFormatException e) {}
                        return new Bus(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, keterangan, statusPeminjaman, kapasitasBus);
                    default:
                        LOGGER.log(Level.WARNING, "Unknown vehicle type: {0} for ID: {1}", new Object[]{jenis, id});
                        return null;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching vehicle with ID " + id + ": " + e.getMessage(), e);
            throw e;
        }
        return null;
    }

    /**
     * Menambahkan kendaraan baru ke database.
     * @param kendaraan Objek Kendaraan yang akan ditambahkan. ID akan di-generate jika null.
     * @return true jika berhasil, false jika gagal.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public boolean addKendaraan(Kendaraan kendaraan) throws SQLException {
        if (kendaraan.getId() == null || kendaraan.getId().isEmpty()) {
            kendaraan.setId("KND" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        String jenis = kendaraan.getClass().getSimpleName(); // "Mobil", "Motor", "Elf", "Bus"

        String sql = "INSERT INTO kendaraan (id_kendaraan, jenis, merk, tipe, plat_nomor, tahun_produksi, harga_per_hari, keterangan, status_peminjaman) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, kendaraan.getId());
            stmt.setString(2, jenis);
            stmt.setString(3, kendaraan.getMerk());
            stmt.setString(4, kendaraan.getTipe());
            stmt.setString(5, kendaraan.getPlatNomor());
            stmt.setInt(6, kendaraan.getTahunProduksi());
            stmt.setDouble(7, kendaraan.getHargaPerHari());
            stmt.setString(8, kendaraan.getKeterangan());
            stmt.setString(9, kendaraan.getStatusPeminjaman());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding vehicle: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Memperbarui data kendaraan di database.
     * Hanya harga_per_hari dan keterangan yang bisa diubah sesuai PDF.
     * @param kendaraan Objek Kendaraan dengan data terbaru.
     * @return true jika berhasil, false jika gagal.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public boolean updateKendaraan(Kendaraan kendaraan) throws SQLException {
        String sql = "UPDATE kendaraan SET harga_per_hari = ?, keterangan = ?, status_peminjaman = ? WHERE id_kendaraan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, kendaraan.getHargaPerHari());
            stmt.setString(2, kendaraan.getKeterangan());
            stmt.setString(3, kendaraan.getStatusPeminjaman()); // Juga update status peminjaman
            stmt.setString(4, kendaraan.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating vehicle with ID " + kendaraan.getId() + ": " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Menghapus kendaraan dari database.
     * @param id ID kendaraan yang akan dihapus.
     * @return true jika berhasil, false jika gagal.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public boolean deleteKendaraan(String id) throws SQLException {
        String sql = "DELETE FROM kendaraan WHERE id_kendaraan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting vehicle with ID " + id + ": " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Mencari kendaraan berdasarkan kata kunci (merk, tipe, plat_nomor).
     * @param keyword Kata kunci pencarian.
     * @return List kendaraan yang cocok dengan kata kunci.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public List<Kendaraan> searchKendaraan(String keyword) throws SQLException {
        List<Kendaraan> searchResults = new ArrayList<>();
        String sql = "SELECT * FROM kendaraan WHERE merk LIKE ? OR tipe LIKE ? OR plat_nomor LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id_kendaraan");
                String jenis = rs.getString("jenis");
                String merk = rs.getString("merk");
                String tipe = rs.getString("tipe");
                String platNomor = rs.getString("plat_nomor");
                int tahunProduksi = rs.getInt("tahun_produksi");
                double hargaPerHari = rs.getDouble("harga_per_hari");
                String keterangan = rs.getString("keterangan");
                String statusPeminjaman = rs.getString("status_peminjaman");

                Kendaraan kendaraan = null;
                switch (jenis.toLowerCase()) {
                    case "mobil":
                        boolean hasAC = keterangan != null && keterangan.toLowerCase().contains("ac");
                        kendaraan = new Mobil(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, keterangan, statusPeminjaman, hasAC);
                        break;
                    case "motor":
                        boolean hasHelm = keterangan != null && keterangan.toLowerCase().contains("helm");
                        kendaraan = new Motor(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, keterangan, statusPeminjaman, hasHelm);
                        break;
                    case "elf":
                    case "bus":
                        int kapasitas = 0;
                        try {
                            String capStr = keterangan.replaceAll("[^0-9]", "").trim();
                            if (!capStr.isEmpty()) {
                                kapasitas = Integer.parseInt(capStr);
                            }
                        } catch (NumberFormatException e) {}
                        if (jenis.equalsIgnoreCase("elf")) {
                            kendaraan = new Elf(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, keterangan, statusPeminjaman, kapasitas);
                        } else { // bus
                            kendaraan = new Bus(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, keterangan, statusPeminjaman, kapasitas);
                        }
                        break;
                }
                if (kendaraan != null) {
                    searchResults.add(kendaraan);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching vehicles: " + e.getMessage(), e);
            throw e;
        }
        return searchResults;
    }
}