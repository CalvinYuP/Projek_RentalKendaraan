package service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import model.Kendaraan;
import model.Peminjaman;
import model.Transaksi;
import util.DateUtil; // Pastikan DateUtil sudah ada
import controller.PeminjamanController; // Untuk mendapatkan detail Peminjaman dari Transaksi

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service untuk menghasilkan berbagai laporan dalam format PDF.
 */
public class ReportService {
    private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());

    /**
     * Menghasilkan laporan daftar kendaraan.
     * @param kendaraanList Daftar kendaraan yang akan dilaporkan.
     * @param title Judul laporan (e.g., "Laporan Semua Kendaraan", "Laporan Mobil").
     * @throws DocumentException Jika terjadi kesalahan dalam pembuatan dokumen PDF.
     * @throws IOException Jika terjadi kesalahan I/O saat menulis file.
     */
    public static void generateKendaraanReport(List<Kendaraan> kendaraanList, String title) throws DocumentException, IOException {
        String filePath = "laporan_kendaraan_" + title.replace(" ", "_").toLowerCase() + ".pdf";
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            document.add(new Paragraph(title.toUpperCase()));
            document.add(new Paragraph("Tanggal Laporan: " + DateUtil.formatDate(java.time.LocalDate.now())));
            document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(8); // Sesuaikan jumlah kolom
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Add Table Headers
            table.addCell(new PdfPCell(new Phrase("ID")));
            table.addCell(new PdfPCell(new Phrase("Jenis")));
            table.addCell(new PdfPCell(new Phrase("Merk")));
            table.addCell(new PdfPCell(new Phrase("Tipe")));
            table.addCell(new PdfPCell(new Phrase("Plat Nomor")));
            table.addCell(new PdfPCell(new Phrase("Tahun")));
            table.addCell(new PdfPCell(new Phrase("Harga/Hari")));
            table.addCell(new PdfPCell(new Phrase("Status")));

            // Add Table Rows
            for (Kendaraan k : kendaraanList) {
                table.addCell(k.getId());
                table.addCell(k.getClass().getSimpleName()); // Get actual type (Mobil, Motor, etc.)
                table.addCell(k.getMerk());
                table.addCell(k.getTipe());
                table.addCell(k.getPlatNomor());
                table.addCell(String.valueOf(k.getTahunProduksi()));
                table.addCell(String.format("Rp %,.2f", k.getHargaPerHari()));
                table.addCell(k.getStatusPeminjaman());
            }

            document.add(table);

            LOGGER.log(Level.INFO, "Laporan kendaraan PDF created successfully at: {0}", filePath);

        } catch (DocumentException | IOException e) {
            LOGGER.log(Level.SEVERE, "Error creating kendaraan report PDF: " + title, e);
            throw e;
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    /**
     * Menghasilkan laporan daftar peminjaman.
     * @param peminjamanList Daftar peminjaman yang akan dilaporkan.
     * @param title Judul laporan.
     * @throws DocumentException Jika terjadi kesalahan dalam pembuatan dokumen PDF.
     * @throws IOException Jika terjadi kesalahan I/O saat menulis file.
     */
    public static void generatePeminjamanReport(List<Peminjaman> peminjamanList, String title) throws DocumentException, IOException {
        String filePath = "laporan_peminjaman_" + title.replace(" ", "_").toLowerCase() + ".pdf";
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            document.add(new Paragraph(title.toUpperCase()));
            document.add(new Paragraph("Tanggal Laporan: " + DateUtil.formatDate(java.time.LocalDate.now())));
            document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(9); // Sesuaikan jumlah kolom
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Add Table Headers
            table.addCell(new PdfPCell(new Phrase("ID Sewa")));
            table.addCell(new PdfPCell(new Phrase("Penyewa")));
            table.addCell(new PdfPCell(new Phrase("Kontak")));
            table.addCell(new PdfPCell(new Phrase("ID Kendaraan")));
            table.addCell(new PdfPCell(new Phrase("Plat Kendaraan")));
            table.addCell(new PdfPCell(new Phrase("Lama (hari)")));
            table.addCell(new PdfPCell(new Phrase("Tgl Mulai")));
            table.addCell(new PdfPCell(new Phrase("Tgl Kembali")));
            table.addCell(new PdfPCell(new Phrase("Status")));

            // Add Table Rows
            for (Peminjaman p : peminjamanList) {
                table.addCell(p.getIdPeminjaman());
                table.addCell(p.getNamaPenyewa());
                table.addCell(p.getKontakPenyewa());
                table.addCell(p.getIdKendaraan());
                table.addCell(p.getPlatKendaraan());
                table.addCell(String.valueOf(p.getLamaPeminjaman()));
                table.addCell(DateUtil.formatDate(p.getTanggalMulai()));
                table.addCell(DateUtil.formatDate(p.getTanggalKembali()));
                table.addCell(p.getStatusPeminjaman());
            }

            document.add(table);

            LOGGER.log(Level.INFO, "Laporan peminjaman PDF created successfully at: {0}", filePath);

        } catch (DocumentException | IOException e) {
            LOGGER.log(Level.SEVERE, "Error creating peminjaman report PDF: " + title, e);
            throw e;
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    /**
     * Menghasilkan laporan daftar transaksi.
     * @param transaksiList Daftar transaksi yang akan dilaporkan.
     * @param title Judul laporan.
     * @param peminjamanController Controller untuk mendapatkan detail peminjaman terkait.
     * @throws DocumentException Jika terjadi kesalahan dalam pembuatan dokumen PDF.
     * @throws IOException Jika terjadi kesalahan I/O saat menulis file.
     */
    public static void generateTransaksiReport(List<Transaksi> transaksiList, String title, PeminjamanController peminjamanController) throws DocumentException, IOException {
        String filePath = "laporan_transaksi_" + title.replace(" ", "_").toLowerCase() + ".pdf";
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            document.add(new Paragraph(title.toUpperCase()));
            document.add(new Paragraph("Tanggal Laporan: " + DateUtil.formatDate(java.time.LocalDate.now())));
            document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(7); // Sesuaikan jumlah kolom
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Add Table Headers
            table.addCell(new PdfPCell(new Phrase("ID Transaksi")));
            table.addCell(new PdfPCell(new Phrase("ID Peminjaman")));
            table.addCell(new PdfPCell(new Phrase("Nama Penyewa")));
            table.addCell(new PdfPCell(new Phrase("Plat Kendaraan")));
            table.addCell(new PdfPCell(new Phrase("Total Biaya")));
            table.addCell(new PdfPCell(new Phrase("Status Pembayaran")));
            table.addCell(new PdfPCell(new Phrase("Tanggal Transaksi")));

            // Add Table Rows
            for (Transaksi t : transaksiList) {
                Peminjaman p = peminjamanController.getPeminjamanById(t.getIdPeminjaman());
                String namaPenyewa = (p != null) ? p.getNamaPenyewa() : "N/A";
                String platKendaraan = (p != null) ? p.getPlatKendaraan() : "N/A";

                table.addCell(t.getIdTransaksi());
                table.addCell(t.getIdPeminjaman());
                table.addCell(namaPenyewa);
                table.addCell(platKendaraan);
                table.addCell(String.format("Rp %,.2f", t.getTotalBiaya()));
                table.addCell(t.getStatusPembayaran());
                table.addCell(DateUtil.formatDateTime(t.getTanggalTransaksi()));
            }

            document.add(table);

            LOGGER.log(Level.INFO, "Laporan transaksi PDF created successfully at: {0}", filePath);

        } catch (DocumentException | IOException e) {
            LOGGER.log(Level.SEVERE, "Error creating transaksi report PDF: " + title, e);
            throw e;
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }
}