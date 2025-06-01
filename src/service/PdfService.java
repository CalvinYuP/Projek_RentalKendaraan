package service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import model.Peminjaman;
import model.Transaksi;
import util.DateUtil; // Pastikan DateUtil sudah ada
import controller.PeminjamanController; // Untuk mendapatkan detail Peminjaman dari Transaksi

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service untuk menghasilkan file PDF (misalnya bukti pembayaran).
 */
public class PdfService {
    private static final Logger LOGGER = Logger.getLogger(PdfService.class.getName());

    /**
     * Mencetak bukti pembayaran ke file PDF.
     * @param transaksi Objek Transaksi yang akan dicetak.
     * @param peminjaman Objek Peminjaman yang terkait dengan transaksi.
     * @throws DocumentException Jika terjadi kesalahan dalam pembuatan dokumen PDF.
     * @throws IOException Jika terjadi kesalahan I/O saat menulis file.
     */
    public static void cetakBuktiPembayaran(Transaksi transaksi, Peminjaman peminjaman) throws DocumentException, IOException {
        String filePath = "bukti_pembayaran_" + transaksi.getIdTransaksi() + ".pdf";
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Judul
            document.add(new Paragraph("BUKTI PEMBAYARAN RENTAL KENDARAAN"));
            document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------"));
            document.add(new Paragraph(" ")); // Spasi

            // Detail Transaksi
            document.add(new Paragraph("ID Transaksi: " + transaksi.getIdTransaksi()));
            document.add(new Paragraph("Tanggal Transaksi: " + DateUtil.formatDateTime(transaksi.getTanggalTransaksi())));
            document.add(new Paragraph("Total Biaya: " + String.format("Rp %,.2f", transaksi.getTotalBiaya())));
            document.add(new Paragraph("Status Pembayaran: " + transaksi.getStatusPembayaran()));
            document.add(new Paragraph(" "));

            // Detail Peminjaman (jika ada)
            if (peminjaman != null) {
                document.add(new Paragraph("Detail Peminjaman:"));
                document.add(new Paragraph("ID Peminjaman: " + peminjaman.getIdPeminjaman()));
                document.add(new Paragraph("Nama Penyewa: " + peminjaman.getNamaPenyewa()));
                document.add(new Paragraph("Kontak Penyewa: " + peminjaman.getKontakPenyewa()));
                document.add(new Paragraph("Kendaraan: " + peminjaman.getMerkKendaraan() + " " + peminjaman.getJenisKendaraan() + " (" + peminjaman.getPlatKendaraan() + ")"));
                document.add(new Paragraph("Lama Peminjaman: " + peminjaman.getLamaPeminjaman() + " hari"));
                document.add(new Paragraph("Tanggal Mulai: " + DateUtil.formatDate(peminjaman.getTanggalMulai())));
                document.add(new Paragraph("Tanggal Kembali: " + DateUtil.formatDate(peminjaman.getTanggalKembali())));
                document.add(new Paragraph("Status Peminjaman: " + peminjaman.getStatusPeminjaman()));
                document.add(new Paragraph(" "));
            }

            document.add(new Paragraph("Terima kasih telah menggunakan jasa rental kendaraan kami!"));

            LOGGER.log(Level.INFO, "Bukti pembayaran PDF created successfully at: {0}", filePath);

        } catch (DocumentException | IOException e) {
            LOGGER.log(Level.SEVERE, "Error creating PDF for transaction ID " + transaksi.getIdTransaksi(), e);
            throw e;
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }
}