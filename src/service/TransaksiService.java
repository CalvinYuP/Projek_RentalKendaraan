package service;

import config.DatabaseConnection;
import model.Transaksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp; // Untuk LocalDateTime ke java.sql.Timestamp
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransaksiService {
    private static final Logger LOGGER = Logger.getLogger(TransaksiService.class.getName());

    /**
     * Mengambil semua data transaksi dari database.
     * @return List objek Transaksi.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public List<Transaksi> getAllTransaksi() throws SQLException {
        List<Transaksi> transaksiList = new ArrayList<>();
        String sql = "SELECT * FROM transaksi";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String idTransaksi = rs.getString("id_transaksi");
                String idPeminjaman = rs.getString("id_peminjaman");
                double totalBiaya = rs.getDouble("total_biaya");
                String statusPembayaran = rs.getString("status_pembayaran");
                LocalDateTime tanggalTransaksi = rs.getTimestamp("tanggal_transaksi").toLocalDateTime();

                Transaksi transaksi = new Transaksi(
                    idTransaksi, idPeminjaman, totalBiaya, statusPembayaran, tanggalTransaksi
                );
                transaksiList.add(transaksi);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all transactions from database: " + e.getMessage(), e);
            throw e;
        }
        return transaksiList;
    }

    /**
     * Mengambil satu data transaksi berdasarkan ID.
     * @param idTransaksi ID transaksi.
     * @return Objek Transaksi jika ditemukan, null jika tidak.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public Transaksi getTransaksiById(String idTransaksi) throws SQLException {
        String sql = "SELECT * FROM transaksi WHERE id_transaksi = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idTransaksi);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String idPeminjaman = rs.getString("id_peminjaman");
                double totalBiaya = rs.getDouble("total_biaya");
                String statusPembayaran = rs.getString("status_pembayaran");
                LocalDateTime tanggalTransaksi = rs.getTimestamp("tanggal_transaksi").toLocalDateTime();

                return new Transaksi(
                    idTransaksi, idPeminjaman, totalBiaya, statusPembayaran, tanggalTransaksi
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching transaction with ID " + idTransaksi + ": " + e.getMessage(), e);
            throw e;
        }
        return null;
    }

    /**
     * Menambahkan transaksi baru ke database.
     * @param transaksi Objek Transaksi yang akan ditambahkan. ID akan di-generate jika null.
     * @return true jika berhasil, false jika gagal.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public boolean addTransaksi(Transaksi transaksi) throws SQLException {
        if (transaksi.getIdTransaksi() == null || transaksi.getIdTransaksi().isEmpty()) {
            transaksi.setIdTransaksi("TRN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (transaksi.getTanggalTransaksi() == null) {
            transaksi.setTanggalTransaksi(LocalDateTime.now());
        }

        String sql = "INSERT INTO transaksi (id_transaksi, id_peminjaman, total_biaya, status_pembayaran, tanggal_transaksi) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transaksi.getIdTransaksi());
            stmt.setString(2, transaksi.getIdPeminjaman());
            stmt.setDouble(3, transaksi.getTotalBiaya());
            stmt.setString(4, transaksi.getStatusPembayaran());
            stmt.setTimestamp(5, Timestamp.valueOf(transaksi.getTanggalTransaksi()));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding transaction: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Memperbarui data transaksi di database.
     * @param transaksi Objek Transaksi dengan data terbaru.
     * @return true jika berhasil, false jika gagal.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public boolean updateTransaksi(Transaksi transaksi) throws SQLException {
        String sql = "UPDATE transaksi SET id_peminjaman = ?, total_biaya = ?, status_pembayaran = ?, tanggal_transaksi = ? WHERE id_transaksi = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transaksi.getIdPeminjaman());
            stmt.setDouble(2, transaksi.getTotalBiaya());
            stmt.setString(3, transaksi.getStatusPembayaran());
            stmt.setTimestamp(4, Timestamp.valueOf(transaksi.getTanggalTransaksi()));
            stmt.setString(5, transaksi.getIdTransaksi());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating transaction with ID " + transaksi.getIdTransaksi() + ": " + e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Memperbarui hanya status pembayaran transaksi.
     * @param idTransaksi ID transaksi.
     * @param newStatus Status pembayaran baru (e.g., "Lunas", "Belum Lunas").
     * @return true jika berhasil, false jika gagal.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public boolean updateStatusPembayaran(String idTransaksi, String newStatus) throws SQLException {
        String sql = "UPDATE transaksi SET status_pembayaran = ? WHERE id_transaksi = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setString(2, idTransaksi);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating payment status for transaction ID " + idTransaksi + ": " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Menghapus transaksi dari database.
     * @param idTransaksi ID transaksi yang akan dihapus.
     * @return true jika berhasil, false jika gagal.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public boolean deleteTransaksi(String idTransaksi) throws SQLException {
        String sql = "DELETE FROM transaksi WHERE id_transaksi = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idTransaksi);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting transaction with ID " + idTransaksi + ": " + e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Mencari transaksi berdasarkan kata kunci (ID Peminjaman atau ID Transaksi).
     * @param keyword Kata kunci pencarian.
     * @return List transaksi yang cocok dengan kata kunci.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public List<Transaksi> searchTransaksi(String keyword) throws SQLException {
        List<Transaksi> searchResults = new ArrayList<>();
        String sql = "SELECT * FROM transaksi WHERE id_transaksi LIKE ? OR id_peminjaman LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String idTransaksi = rs.getString("id_transaksi");
                String idPeminjaman = rs.getString("id_peminjaman");
                double totalBiaya = rs.getDouble("total_biaya");
                String statusPembayaran = rs.getString("status_pembayaran");
                LocalDateTime tanggalTransaksi = rs.getTimestamp("tanggal_transaksi").toLocalDateTime();

                Transaksi transaksi = new Transaksi(
                    idTransaksi, idPeminjaman, totalBiaya, statusPembayaran, tanggalTransaksi
                );
                searchResults.add(transaksi);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching transactions: " + e.getMessage(), e);
            throw e;
        }
        return searchResults;
    }
}