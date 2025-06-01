package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Kelas utilitas untuk membantu dalam pemformatan tanggal dan waktu.
 */
public class DateUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    /**
     * Memformat objek LocalDate menjadi String dengan format "dd-MM-yyyy".
     * @param date Objek LocalDate.
     * @return String representasi tanggal, atau null jika input null.
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * Memformat objek LocalDateTime menjadi String dengan format "dd-MM-yyyy HH:mm:ss".
     * @param dateTime Objek LocalDateTime.
     * @return String representasi tanggal dan waktu, atau null jika input null.
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATETIME_FORMATTER);
    }

    // Anda bisa menambahkan method lain seperti:
    // public static LocalDate parseDate(String dateString) { ... }
    // public static LocalDateTime parseDateTime(String dateTimeString) { ... }
}