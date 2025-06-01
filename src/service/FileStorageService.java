package service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service untuk menangani penyimpanan dan penghapusan file (misalnya KTP).
 */
public class FileStorageService {
    private static final Logger LOGGER = Logger.getLogger(FileStorageService.class.getName());
    private static final String UPLOAD_DIR = "uploads" + File.separator + "ktp"; // Folder untuk menyimpan KTP

    static {
        // Pastikan direktori upload ada
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
                LOGGER.log(Level.INFO, "Created upload directory: {0}", uploadPath.toAbsolutePath());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Could not create upload directory: " + uploadPath.toAbsolutePath(), e);
            }
        }
    }

    /**
     * Mengupload file KTP ke direktori penyimpanan.
     * @param sourceFile File KTP yang akan diupload.
     * @return Path relatif file yang disimpan, atau null jika gagal.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    public static String uploadKtp(File sourceFile) throws IOException {
        if (sourceFile == null || !sourceFile.exists()) {
            return null;
        }

        String fileName = sourceFile.getName();
        String fileExtension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            fileExtension = fileName.substring(dotIndex);
        }

        String newFileName = UUID.randomUUID().toString() + fileExtension;
        Path targetPath = Paths.get(UPLOAD_DIR, newFileName);

        try {
            Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.log(Level.INFO, "KTP uploaded successfully: {0} to {1}", new Object[]{sourceFile.getName(), targetPath.toAbsolutePath()});
            return UPLOAD_DIR + File.separator + newFileName; // Return relative path
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error uploading KTP file: " + sourceFile.getName(), e);
            throw e;
        }
    }

    /**
     * Menghapus file KTP dari penyimpanan.
     * @param filePath Path relatif file KTP yang akan dihapus.
     * @return true jika berhasil dihapus, false jika gagal atau file tidak ada.
     */
    public static boolean deleteKtp(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        Path fileToDelete = Paths.get(filePath);
        try {
            if (Files.exists(fileToDelete)) {
                Files.delete(fileToDelete);
                LOGGER.log(Level.INFO, "KTP file deleted successfully: {0}", fileToDelete.toAbsolutePath());
                return true;
            } else {
                LOGGER.log(Level.WARNING, "KTP file not found for deletion: {0}", fileToDelete.toAbsolutePath());
                return false;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error deleting KTP file: " + fileToDelete.toAbsolutePath(), e);
            return false;
        }
    }
}