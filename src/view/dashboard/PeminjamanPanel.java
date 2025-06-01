package view.dashboard;

import controller.PeminjamanController;
import service.ReportService; // Untuk PDF
import model.Peminjaman;
import model.Kendaraan; // Perlu Kendaraan untuk dropdown pemilihan kendaraan
import controller.KendaraanController; // Untuk mendapatkan list Kendaraan
import com.itextpdf.text.DocumentException; // Untuk PDF
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.DateUtil; // Untuk pemformatan tanggal

import view.dashboard.components.AddEditPeminjamanDialog; // Import dialog

public class PeminjamanPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(PeminjamanPanel.class.getName());
    private JTable peminjamanTable;
    private DefaultTableModel tableModel;
    private PeminjamanController peminjamanController;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton downloadButton;
    private JTextField searchField;

    public PeminjamanPanel() {
        peminjamanController = new PeminjamanController();
        initComponents();
        loadPeminjamanData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Panel: Title, Search ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));

        // Title
        JLabel titleLabel = new JLabel("Manajemen Data Peminjaman");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchField = new JTextField(25);
        searchField.putClientProperty("JTextField.placeholderText", "Cari (Nama Penyewa, Plat Kendaraan)...");
        JButton searchButton = new JButton("Cari");
        searchButton.addActionListener(e -> searchPeminjaman());
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        topPanel.add(searchPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- Table Panel ---
        String[] columnNames = {"ID Sewa", "Nama Penyewa", "Kontak Penyewa", "ID Kendaraan", "Plat Kendaraan", "Jenis Kendaraan", "Merk Kendaraan", "Lama (Hari)", "Tanggal Mulai", "Tanggal Kembali", "Status Peminjaman", "File KTP"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        peminjamanTable = new JTable(tableModel);
        peminjamanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        peminjamanTable.getTableHeader().setReorderingAllowed(false);
        peminjamanTable.setAutoCreateRowSorter(true);

        // Double-click to edit
        peminjamanTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    editPeminjaman();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(peminjamanTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel: Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        addButton = new JButton("Tambah Peminjaman");
        editButton = new JButton("Update Peminjaman");
        deleteButton = new JButton("Hapus Peminjaman");
        downloadButton = new JButton("Unduh Laporan PDF");

        addButton.addActionListener(e -> addPeminjaman());
        editButton.addActionListener(e -> editPeminjaman());
        deleteButton.addActionListener(e -> deletePeminjaman());
        downloadButton.addActionListener(e -> downloadReport());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(downloadButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadPeminjamanData() {
        tableModel.setRowCount(0); // Clear existing data
        try {
            List<Peminjaman> peminjamanList = peminjamanController.getAllPeminjaman();
            for (Peminjaman p : peminjamanList) {
                Object[] rowData = {
                    p.getIdPeminjaman(),
                    p.getNamaPenyewa(),
                    p.getKontakPenyewa(),
                    p.getIdKendaraan(),
                    p.getPlatKendaraan(),
                    p.getJenisKendaraan(),
                    p.getMerkKendaraan(),
                    p.getLamaPeminjaman(),
                    DateUtil.formatDate(p.getTanggalMulai()), // Formatted date
                    DateUtil.formatDate(p.getTanggalKembali()), // Formatted date
                    p.getStatusPeminjaman(),
                    p.getFileKtpPath() != null && !p.getFileKtpPath().isEmpty() ? "Ada" : "Tidak Ada" // Indicate if KTP exists
                };
                tableModel.addRow(rowData);
            }
            LOGGER.log(Level.INFO, "Peminjaman data loaded successfully. Total: {0}", peminjamanList.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load peminjaman data: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Gagal memuat data peminjaman: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchPeminjaman() {
        String keyword = searchField.getText();
        tableModel.setRowCount(0); // Clear table
        try {
            List<Peminjaman> searchResults = peminjamanController.searchPeminjaman(keyword);
            for (Peminjaman p : searchResults) {
                Object[] rowData = {
                    p.getIdPeminjaman(),
                    p.getNamaPenyewa(),
                    p.getKontakPenyewa(),
                    p.getIdKendaraan(),
                    p.getPlatKendaraan(),
                    p.getJenisKendaraan(),
                    p.getMerkKendaraan(),
                    p.getLamaPeminjaman(),
                    DateUtil.formatDate(p.getTanggalMulai()),
                    DateUtil.formatDate(p.getTanggalKembali()),
                    p.getStatusPeminjaman(),
                    p.getFileKtpPath() != null && !p.getFileKtpPath().isEmpty() ? "Ada" : "Tidak Ada"
                };
                tableModel.addRow(rowData);
            }
            LOGGER.log(Level.INFO, "Search for '{0}' completed. Found {1} results.", new Object[]{keyword, searchResults.size()});
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to search peminjaman: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Gagal mencari peminjaman: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPeminjaman() {
        AddEditPeminjamanDialog dialog = new AddEditPeminjamanDialog(null, true, null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadPeminjamanData(); // Refresh table after add
            JOptionPane.showMessageDialog(this, "Data peminjaman berhasil ditambahkan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editPeminjaman() {
        int selectedRow = peminjamanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diupdate.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = peminjamanTable.convertRowIndexToModel(selectedRow);
        String idPeminjaman = (String) tableModel.getValueAt(modelRow, 0);

        try {
            Peminjaman selectedPeminjaman = peminjamanController.getPeminjamanById(idPeminjaman);
            if (selectedPeminjaman != null) {
                AddEditPeminjamanDialog dialog = new AddEditPeminjamanDialog(null, true, selectedPeminjaman);
                dialog.setVisible(true);
                if (dialog.isSuccess()) {
                    loadPeminjamanData(); // Refresh table after update
                    JOptionPane.showMessageDialog(this, "Data peminjaman berhasil diupdate.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Peminjaman tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving peminjaman for edit: " + idPeminjaman, e);
            JOptionPane.showMessageDialog(this, "Gagal mengambil data peminjaman untuk update: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePeminjaman() {
        int selectedRow = peminjamanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini? Status kendaraan akan diubah kembali menjadi 'Tersedia'.", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = peminjamanTable.convertRowIndexToModel(selectedRow);
            String idPeminjaman = (String) tableModel.getValueAt(modelRow, 0);
            try {
                if (peminjamanController.deletePeminjaman(idPeminjaman)) {
                    JOptionPane.showMessageDialog(this, "Data peminjaman berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    loadPeminjamanData(); // Refresh table after delete
                    // Also refresh KendaraanPanel to reflect status change
                    ((DashboardFrame) SwingUtilities.getWindowAncestor(this)).btnKendaraan.doClick(); // Simulate click on Kendaraan button
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data peminjaman.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error deleting peminjaman: " + idPeminjaman, e);
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menghapus data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void downloadReport() {
        try {
            List<Peminjaman> allPeminjaman = peminjamanController.getAllPeminjaman();
            ReportService.generatePeminjamanReport(allPeminjaman, "Laporan Semua Peminjaman");
            JOptionPane.showMessageDialog(this, "Laporan Peminjaman berhasil diunduh ke 'laporan_peminjaman_laporan_semua_peminjaman.pdf'", "Unduh Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (DocumentException | IOException e) {
            LOGGER.log(Level.SEVERE, "Gagal membuat laporan PDF peminjaman.", e);
            JOptionPane.showMessageDialog(this, "Gagal membuat laporan PDF peminjaman: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}