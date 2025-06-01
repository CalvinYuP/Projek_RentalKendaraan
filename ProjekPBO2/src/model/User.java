package model;

import java.io.Serializable;
import java.time.LocalDateTime; // Untuk created_at dan updated_at

/**
 * Kelas representasi data pengguna (user) aplikasi.
 */
public class User implements Serializable {
    private int userId;
    private String username;
    private String password; // Pastikan ini di-hash di aplikasi nyata!
    private String role; // Contoh: "admin", "pegawai", "user"
    private String namaLengkap;
    private String email;
    private String telepon;
    private int isActive; // 0 for false, 1 for true
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Konstruktor default
    public User() {
    }

    // Konstruktor lengkap
    public User(int userId, String username, String password, String role, String namaLengkap, String email, String telepon, int isActive, LocalDateTime lastLogin, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.telepon = telepon;
        this.isActive = isActive;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Konstruktor untuk register/login (tanpa semua metadata)
    public User(String username, String password, String role, String namaLengkap) {
        this.username = username;
        this.password = password; // Di aplikasi nyata, ini harus di-hash
        this.role = role;
        this.namaLengkap = namaLengkap;
        this.isActive = 1; // Default aktif
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }


    // --- Getters ---
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public String getEmail() {
        return email;
    }

    public String getTelepon() {
        return telepon;
    }

    public int getIsActive() {
        return isActive;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // --- Setters ---
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "User{" +
               "userId=" + userId +
               ", username='" + username + '\'' +
               ", role='" + role + '\'' +
               '}';
    }
}