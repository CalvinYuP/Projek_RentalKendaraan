package view.dashboard;

import controller.KendaraanController;
import controller.PeminjamanController;
import controller.TransaksiController;
import service.ReportService; // Untuk menghasilkan PDF
import model.Kendaraan;
import model.Peminjaman;
import model.Transaksi;
import com.itextpdf.text.DocumentException; // Untuk penanganan PDF
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LaporanPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(LaporanPanel.class.getName());

    private KendaraanController kendaraanController;
    private PeminjamanController peminjamanController;
    private TransaksiController transaksiController;

    // Components for Kendaraan Report
    private JButton btnGenerateKendaraanReport;
    private JComboBox<String> filterKendaraanType;

    // Components for Peminjaman Report
    private JButton btnGeneratePeminjamanReport;
    private JComboBox<String> filterPeminjamanMonth;

    // Components for Transaksi Report
    private JButton btnGenerateTransaksiReport;
    private JComboBox<String> filterTransaksiStatus;

    public LaporanPanel() {
        kendaraanController = new KendaraanController();
        peminjamanController = new PeminjamanController();
        transaksiController = new TransaksiController();
        initComponents();
        loadDataForReports(); // Initial load for combo boxes
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Laporan Aplikasi Rental Kendaraan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(3, 1, 10, 10)); // 3 rows, 1 column, with gaps

        // --- Kendaraan Report Section ---
        JPanel kendaraanReportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        kendaraanReportPanel.setBorder(BorderFactory.createTitledBorder("Laporan Kendaraan"));
        
        kendaraanReportPanel.add(new JLabel("Filter Jenis:"));
        filterKendaraanType = new JComboBox<>(new String[]{"Semua", "Mobil", "Motor", "Elf", "Bus"});
        kendaraanReportPanel.add(filterKendaraanType);

        btnGenerateKendaraanReport = new JButton("Generate Laporan Kendaraan");
        btnGenerateKendaraanReport.addActionListener(e -> generateKendaraanReport());
        kendaraanReportPanel.add(btnGenerateKendaraanReport);
        contentPanel.add(kendaraanReportPanel);

        // --- Peminjaman Report Section ---
        JPanel peminjamanReportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        peminjamanReportPanel.setBorder(BorderFactory.createTitledBorder("Laporan Peminjaman"));
        
        peminjamanReportPanel.add(new JLabel("Filter Bulan:"));
        String[] months = {"Semua Bulan", "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        filterPeminjamanMonth = new JComboBox<>(months);
        peminjamanReportPanel.add(filterPeminjamanMonth);

        btnGeneratePeminjamanReport = new JButton("Generate Laporan Peminjaman");
        btnGeneratePeminjamanReport.addActionListener(e -> generatePeminjamanReport());
        peminjamanReportPanel.add(btnGeneratePeminjamanReport);
        contentPanel.add(peminjamanReportPanel);

        // --- Transaksi Report Section ---
        JPanel transaksiReportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        transaksiReportPanel.setBorder(BorderFactory.createTitledBorder("Laporan Transaksi"));

        transaksiReportPanel.add(new JLabel("Filter Status:"));
        filterTransaksiStatus = new JComboBox<>(new String[]{"Semua", "Lunas", "Belum Lunas"});
        transaksiReportPanel.add(filterTransaksiStatus);

        btnGenerateTransaksiReport = new JButton("Generate Laporan Transaksi");
        btnGenerateTransaksiReport.addActionListener(e -> generateTransaksiReport());
        transaksiReportPanel.add(btnGenerateTransaksiReport);
        contentPanel.add(transaksiReportPanel);

        add(contentPanel, BorderLayout.CENTER);
    }
    
    public void loadDataForReports() {
        // No data needs to be pre-loaded into tables, just ensure controllers are ready.
        // This method is called when the panel is shown to refresh states if needed.
        LOGGER.log(Level.INFO, "LaporanPanel initialized. Ready to generate reports.");
    }

    private void generateKendaraanReport() {
        String selectedType = (String) filterKendaraanType.getSelectedItem();
        List<Kendaraan> data;
        try {
            if (selectedType.equalsIgnoreCase("Semua")) {
                data = kendaraanController.getAllKendaraan();
            } else {
                data = kendaraanController.filterKendaraanByJenis(selectedType);
            }
            
            String reportTitle = "Laporan Kendaraan " + selectedType;
            ReportService.generateKendaraanReport(data, reportTitle);
            JOptionPane.showMessageDialog(this, "Laporan Kendaraan '" + selectedType + "' berhasil diunduh.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            LOGGER.log(Level.INFO, "Generated Kendaraan Report for type: {0}", selectedType);
        } catch (DocumentException | IOException e) {
            LOGGER.log(Level.SEVERE, "Gagal membuat laporan PDF kendaraan.", e);
            JOptionPane.showMessageDialog(this, "Gagal membuat laporan PDF kendaraan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching kendaraan data for report.", e);
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memuat data kendaraan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generatePeminjamanReport() {
        int selectedMonthIndex = filterPeminjamanMonth.getSelectedIndex() - 1; // 0 for Jan, -1 for "Semua Bulan"
        List<Peminjaman> data;
        try {
            data = peminjamanController.filterByBulan(selectedMonthIndex);
            
            String monthName = (selectedMonthIndex == -1) ? "Semua Bulan" : Month.of(selectedMonthIndex + 1).name();
            String reportTitle = "Laporan Peminjaman Bulan " + monthName;
            ReportService.generatePeminjamanReport(data, reportTitle);
            JOptionPane.showMessageDialog(this, "Laporan Peminjaman berhasil diunduh.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            LOGGER.log(Level.INFO, "Generated Peminjaman Report for month index: {0}", selectedMonthIndex);
        } catch (DocumentException | IOException e) {
            LOGGER.log(Level.SEVERE, "Gagal membuat laporan PDF peminjaman.", e);
            JOptionPane.showMessageDialog(this, "Gagal membuat laporan PDF peminjaman: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching peminjaman data for report.", e);
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memuat data peminjaman: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateTransaksiReport() {
        String selectedStatus = (String) filterTransaksiStatus.getSelectedItem();
        List<Transaksi> data;
        try {
            data = transaksiController.filterByStatusPembayaran(selectedStatus);

            String reportTitle = "Laporan Transaksi Status " + selectedStatus;
            ReportService.generateTransaksiReport(data, reportTitle, peminjamanController); // Pass PeminjamanController
            JOptionPane.showMessageDialog(this, "Laporan Transaksi berhasil diunduh.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            LOGGER.log(Level.INFO, "Generated Transaksi Report for status: {0}", selectedStatus);
        } catch (DocumentException | IOException e) {
            LOGGER.log(Level.SEVERE, "Gagal membuat laporan PDF transaksi.", e);
            JOptionPane.showMessageDialog(this, "Gagal membuat laporan PDF transaksi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching transaksi data for report.", e);
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memuat data transaksi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}