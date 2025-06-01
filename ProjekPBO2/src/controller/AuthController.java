package controller;

import model.User;
import service.AuthService;
import service.FileStorageService; // Jika diperlukan untuk menyimpan data pengguna, tapi untuk Auth hanya perlu Service
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthController {
    private static final Logger LOGGER = Logger.getLogger(AuthController.class.getName());
    private AuthService authService;

    public AuthController() {
        this.authService = new AuthService();
    }

    /**
     * Melakukan otentikasi pengguna.
     * @param username Username pengguna.
     * @param password Password pengguna (plain text, akan di-hash oleh service).
     * @return true jika otentikasi berhasil, false jika tidak.
     */
    public boolean login(String username, String password) {
        LOGGER.log(Level.INFO, "Attempting login for user: {0}", username);
        try {
            // AuthService akan menangani hashing password dan verifikasi
            boolean success = authService.login(username, password);
            if (success) {
                LOGGER.log(Level.INFO, "Login successful for user: {0}", username);
                // Di sini Anda bisa menambahkan logika untuk menyimpan sesi user
            } else {
                LOGGER.log(Level.WARNING, "Login failed for user: {0}. Invalid credentials.", username);
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during login for user: " + username, e);
            // Handle exceptions (e.g., database connection error)
            return false;
        }
    }

    // Anda bisa menambahkan method lain seperti register, logout, dll.
    public boolean register(User newUser) {
        LOGGER.log(Level.INFO, "Attempting to register new user: {0}", newUser.getUsername());
        try {
            return authService.register(newUser);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during user registration: " + newUser.getUsername(), e);
            return false;
        }
    }
}