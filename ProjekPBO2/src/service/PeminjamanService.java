package service;

import config.DatabaseConnection;
import model.Peminjaman;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date; // Untuk LocalDate ke java.sql.Date
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PeminjamanService {
    private static final Logger LOGGER = Logger.getLogger(PeminjamanService.class.getName());

    /**
     * Mengambil semua data peminjaman dari database.
     * @return List objek Peminjaman.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public List<Peminjaman> getAllPeminjaman() throws SQLException {
        List<Peminjaman> peminjamanList = new ArrayList<>();
        String sql = "SELECT * FROM peminjaman";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String idPeminjaman = rs.getString("id_peminjaman");
                String namaPenyewa = rs.getString("nama_penyewa");
                String kontakPenyewa = rs.getString("kontak_penyewa");
                String idKendaraan = rs.getString("id_kendaraan");
                String platKendaraan = rs.getString("plat_kendaraan");
                String jenisKendaraan = rs.getString("jenis_kendaraan"); // Pastikan kolom ini ada
                String merkKendaraan = rs.getString("merk_kendaraan");   // Pastikan kolom ini ada
                int lamaPeminjaman = rs.getInt("lama_peminjaman");
                LocalDate tanggalMulai = rs.getDate("tanggal_mulai").toLocalDate();
                LocalDate tanggalKembali = rs.getDate("tanggal_kembali").toLocalDate();
                String statusPeminjaman = rs.getString("status_peminjaman");
                String fileKtpPath = rs.getString("file_ktp_path"); // Pastikan kolom ini ada

                Peminjaman peminjaman = new Peminjaman(
                    idPeminjaman, namaPenyewa, kontakPenyewa, idKendaraan, platKendaraan,
                    jenisKendaraan, merkKendaraan, lamaPeminjaman, tanggalMulai, tanggalKembali,
                    statusPeminjaman, fileKtpPath
                );
                peminjamanList.add(peminjaman);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all peminjaman from database: " + e.getMessage(), e);
            throw e;
        }
        return peminjamanList;
    }

    /**
     * Mengambil satu data peminjaman berdasarkan ID.
     * @param idPeminjaman ID peminjaman.
     * @return Objek Peminjaman jika ditemukan, null jika tidak.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public Peminjaman getPeminjamanById(String idPeminjaman) throws SQLException {
        String sql = "SELECT * FROM peminjaman WHERE id_peminjaman = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idPeminjaman);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String namaPenyewa = rs.getString("nama_penyewa");
                String kontakPenyewa = rs.getString("kontak_penyewa");
                String idKendaraan = rs.getString("id_kendaraan");
                String platKendaraan = rs.getString("plat_kendaraan");
                String jenisKendaraan = rs.getString("jenis_kendaraan");
                String merkKendaraan = rs.getString("merk_kendaraan");
                int lamaPeminjaman = rs.getInt("lama_peminjaman");
                LocalDate tanggalMulai = rs.getDate("tanggal_mulai").toLocalDate();
                LocalDate tanggalKembali = rs.getDate("tanggal_kembali").toLocalDate();
                String statusPeminjaman = rs.getString("status_peminjaman");
                String fileKtpPath = rs.getString("file_ktp_path");

                return new Peminjaman(
                    idPeminjaman, namaPenyewa, kontakPenyewa, idKendaraan, platKendaraan,
                    jenisKendaraan, merkKendaraan, lamaPeminjaman, tanggalMulai, tanggalKembali,
                    statusPeminjaman, fileKtpPath
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching peminjaman with ID " + idPeminjaman + ": " + e.getMessage(), e);
            throw e;
        }
        return null;
    }

    /**
     * Menambahkan peminjaman baru ke database.
     * @param peminjaman Objek Peminjaman yang akan ditambahkan. ID akan di-generate jika null.
     * @return true jika berhasil, false jika gagal.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public boolean addPeminjaman(Peminjaman peminjaman) throws SQLException {
        if (peminjaman.getIdPeminjaman() == null || peminjaman.getIdPeminjaman().isEmpty()) {
            peminjaman.setIdPeminjaman("PMJ" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        String sql = "INSERT INTO peminjaman (id_peminjaman, nama_penyewa, kontak_penyewa, id_kendaraan, plat_kendaraan, " +
                     "jenis_kendaraan, merk_kendaraan, lama_peminjaman, tanggal_mulai, tanggal_kembali, status_peminjaman, file_ktp_path) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, peminjaman.getIdPeminjaman());
            stmt.setString(2, peminjaman.getNamaPenyewa());
            stmt.setString(3, peminjaman.getKontakPenyewa());
            stmt.setString(4, peminjaman.getIdKendaraan());
            stmt.setString(5, peminjaman.getPlatKendaraan());
            stmt.setString(6, peminjaman.getJenisKendaraan());
            stmt.setString(7, peminjaman.getMerkKendaraan());
            stmt.setInt(8, peminjaman.getLamaPeminjaman());
            stmt.setDate(9, Date.valueOf(peminjaman.getTanggalMulai()));
            stmt.setDate(10, Date.valueOf(peminjaman.getTanggalKembali()));
            stmt.setString(11, peminjaman.getStatusPeminjaman());
            stmt.setString(12, peminjaman.getFileKtpPath());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding peminjaman: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Memperbarui data peminjaman di database.
     * @param peminjaman Objek Peminjaman dengan data terbaru.
     * @return true jika berhasil, false jika gagal.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public boolean updatePeminjaman(Peminjaman peminjaman) throws SQLException {
        String sql = "UPDATE peminjaman SET nama_penyewa = ?, kontak_penyewa = ?, id_kendaraan = ?, plat_kendaraan = ?, " +
                     "jenis_kendaraan = ?, merk_kendaraan = ?, lama_peminjaman = ?, tanggal_mulai = ?, tanggal_kembali = ?, " +
                     "status_peminjaman = ?, file_ktp_path = ? WHERE id_peminjaman = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, peminjaman.getNamaPenyewa());
            stmt.setString(2, peminjaman.getKontakPenyewa());
            stmt.setString(3, peminjaman.getIdKendaraan());
            stmt.setString(4, peminjaman.getPlatKendaraan());
            stmt.setString(5, peminjaman.getJenisKendaraan());
            stmt.setString(6, peminjaman.getMerkKendaraan());
            stmt.setInt(7, peminjaman.getLamaPeminjaman());
            stmt.setDate(8, Date.valueOf(peminjaman.getTanggalMulai()));
            stmt.setDate(9, Date.valueOf(peminjaman.getTanggalKembali()));
            stmt.setString(10, peminjaman.getStatusPeminjaman());
            stmt.setString(11, peminjaman.getFileKtpPath());
            stmt.setString(12, peminjaman.getIdPeminjaman());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating peminjaman with ID " + peminjaman.getIdPeminjaman() + ": " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Menghapus peminjaman dari database.
     * @param idPeminjaman ID peminjaman yang akan dihapus.
     * @return true jika berhasil, false jika gagal.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public boolean deletePeminjaman(String idPeminjaman) throws SQLException {
        String sql = "DELETE FROM peminjaman WHERE id_peminjaman = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idPeminjaman);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting peminjaman with ID " + idPeminjaman + ": " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Mencari peminjaman berdasarkan kata kunci (nama_penyewa, plat_kendaraan).
     * @param keyword Kata kunci pencarian.
     * @return List peminjaman yang cocok dengan kata kunci.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public List<Peminjaman> searchPeminjaman(String keyword) throws SQLException {
        List<Peminjaman> searchResults = new ArrayList<>();
        String sql = "SELECT * FROM peminjaman WHERE nama_penyewa LIKE ? OR plat_kendaraan LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String idPeminjaman = rs.getString("id_peminjaman");
                String namaPenyewa = rs.getString("nama_penyewa");
                String kontakPenyewa = rs.getString("kontak_penyewa");
                String idKendaraan = rs.getString("id_kendaraan");
                String platKendaraan = rs.getString("plat_kendaraan");
                String jenisKendaraan = rs.getString("jenis_kendaraan");
                String merkKendaraan = rs.getString("merk_kendaraan");
                int lamaPeminjaman = rs.getInt("lama_peminjaman");
                LocalDate tanggalMulai = rs.getDate("tanggal_mulai").toLocalDate();
                LocalDate tanggalKembali = rs.getDate("tanggal_kembali").toLocalDate();
                String statusPeminjaman = rs.getString("status_peminjaman");
                String fileKtpPath = rs.getString("file_ktp_path");

                Peminjaman peminjaman = new Peminjaman(
                    idPeminjaman, namaPenyewa, kontakPenyewa, idKendaraan, platKendaraan,
                    jenisKendaraan, merkKendaraan, lamaPeminjaman, tanggalMulai, tanggalKembali,
                    statusPeminjaman, fileKtpPath
                );
                searchResults.add(peminjaman);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching peminjaman: " + e.getMessage(), e);
            throw e;
        }
        return searchResults;
    }
}