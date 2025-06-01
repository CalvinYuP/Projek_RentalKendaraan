package service;

import config.DatabaseConnection;
import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

// Import untuk hashing password (jika menggunakan library seperti BCrypt)
// import org.mindrot.jbcrypt.BCrypt; // Contoh, jika Anda menggunakannya

public class AuthService {
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());

    /**
     * Melakukan otentikasi pengguna berdasarkan username dan password.
     * Ini adalah metode yang akan dipanggil oleh controller.
     * @param username Username pengguna.
     * @param password Password plain text yang diinputkan pengguna.
     * @return true jika otentikasi berhasil, false jika tidak.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public boolean login(String username, String password) throws SQLException {
        String sql = "SELECT user_id, username, password, role, is_active FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                int isActive = rs.getInt("is_active");

                // Verifikasi password (gunakan BCrypt.checkpw jika Anda menghash password saat registrasi)
                // Contoh dengan password plain text (TIDAK AMAN untuk produksi!)
                if (password.equals(storedHashedPassword)) { // Perbandingan plain text (hanya untuk contoh)
                // if (BCrypt.checkpw(password, storedHashedPassword)) { // Jika menggunakan BCrypt
                    if (isActive == 1) {
                        // Update last_login timestamp
                        updateLastLogin(username);
                        return true;
                    } else {
                        LOGGER.log(Level.WARNING, "User {0} is inactive.", username);
                        return false;
                    }
                } else {
                    LOGGER.log(Level.WARNING, "Invalid password for user: {0}", username);
                }
            } else {
                LOGGER.log(Level.WARNING, "User not found: {0}", username);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during login for user: " + username, e);
            throw e; // Re-throw the exception to be handled by controller/view
        }
        return false;
    }

    /**
     * Memperbarui timestamp last_login pengguna.
     * @param username Username pengguna.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    private void updateLastLogin(String username) throws SQLException {
        String sql = "UPDATE users SET last_login = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(2, username);
            stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Updated last_login for user: {0}", username);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating last_login for user: " + username, e);
            throw e;
        }
    }

    /**
     * Mendaftarkan pengguna baru.
     * @param user Objek User yang akan didaftarkan.
     * @return true jika berhasil, false jika gagal.
     * @throws SQLException Jika terjadi kesalahan database.
     */
    public boolean register(User user) throws SQLException {
        // Hash password sebelum menyimpan (SANGAT DIREKOMENDASIKAN untuk produksi)
        // String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        // user.setPassword(hashedPassword);

        String sql = "INSERT INTO users (username, password, role, nama_lengkap, email, telepon, is_active, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword()); // Ganti dengan hashedPassword jika di-hash
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getNamaLengkap());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getTelepon());
            stmt.setInt(7, user.getIsActive());
            stmt.setTimestamp(8, Timestamp.valueOf(user.getCreatedAt()));
            stmt.setTimestamp(9, Timestamp.valueOf(user.getUpdatedAt()));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error registering user: " + user.getUsername(), e);
            throw e;
        }
    }
}