package view.dashboard;

import controller.TransaksiController;
import controller.PeminjamanController; // Diperlukan untuk mendapatkan detail peminjaman
import service.PdfService; // Untuk mencetak bukti pembayaran
import model.Transaksi;
import model.Peminjaman; // Diperlukan untuk menampilkan detail peminjaman di tabel/dialog
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

import view.dashboard.components.AddEditTransaksiDialog; // Import dialog

public class TransaksiPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(TransaksiPanel.class.getName());
    private JTable transaksiTable;
    private DefaultTableModel tableModel;
    private TransaksiController transaksiController;
    private PeminjamanController peminjamanController; // Untuk mendapatkan detail peminjaman

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton printButton; // New button for printing proof of payment
    private JTextField searchField;
    private JComboBox<String> statusFilterComboBox;

    public TransaksiPanel() {
        transaksiController = new TransaksiController();
        peminjamanController = new PeminjamanController(); // Initialize PeminjamanController
        initComponents();
        loadTransaksiData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Panel: Title, Search, Filter ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));

        // Title
        JLabel titleLabel = new JLabel("Manajemen Data Transaksi");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel searchFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        
        // Search
        searchField = new JTextField(25);
        searchField.putClientProperty("JTextField.placeholderText", "Cari (ID Transaksi, ID Peminjaman)...");
        JButton searchButton = new JButton("Cari");
        searchButton.addActionListener(e -> searchTransaksi());
        searchFilterPanel.add(searchField);
        searchFilterPanel.add(searchButton);

        // Filter by Status Pembayaran
        statusFilterComboBox = new JComboBox<>(new String[]{"Semua", "Lunas", "Belum Lunas"});
        statusFilterComboBox.addActionListener(e -> filterTransaksi());
        searchFilterPanel.add(new JLabel("Filter Status:"));
        searchFilterPanel.add(statusFilterComboBox);

        topPanel.add(searchFilterPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- Table Panel ---
        String[] columnNames = {"ID Transaksi", "ID Peminjaman", "Nama Penyewa", "Plat Kendaraan", "Total Biaya", "Status Pembayaran", "Tanggal Transaksi"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        transaksiTable = new JTable(tableModel);
        transaksiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transaksiTable.getTableHeader().setReorderingAllowed(false);
        transaksiTable.setAutoCreateRowSorter(true);

        // Double-click to edit
        transaksiTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    editTransaksi();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(transaksiTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel: Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        addButton = new JButton("Tambah Transaksi");
        editButton = new JButton("Update Transaksi");
        deleteButton = new JButton("Hapus Transaksi");
        printButton = new JButton("Cetak Bukti Pembayaran");

        addButton.addActionListener(e -> addTransaksi());
        editButton.addActionListener(e -> editTransaksi());
        deleteButton.addActionListener(e -> deleteTransaksi());
        printButton.addActionListener(e -> printBuktiPembayaran());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(printButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadTransaksiData() {
        tableModel.setRowCount(0); // Clear existing data
        try {
            List<Transaksi> transaksiList = transaksiController.getListTransaksi();
            for (Transaksi t : transaksiList) {
                // Get related Peminjaman data for display
                Peminjaman p = peminjamanController.getPeminjamanById(t.getIdPeminjaman());
                String namaPenyewa = (p != null) ? p.getNamaPenyewa() : "N/A";
                String platKendaraan = (p != null) ? p.getPlatKendaraan() : "N/A";

                Object[] rowData = {
                    t.getIdTransaksi(),
                    t.getIdPeminjaman(),
                    namaPenyewa,
                    platKendaraan,
                    String.format("Rp %,.2f", t.getTotalBiaya()), // Formatted currency
                    t.getStatusPembayaran(),
                    DateUtil.formatDateTime(t.getTanggalTransaksi()) // Formatted datetime
                };
                tableModel.addRow(rowData);
            }
            LOGGER.log(Level.INFO, "Transaksi data loaded successfully. Total: {0}", transaksiList.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load transaksi data: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Gagal memuat data transaksi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchTransaksi() {
        String keyword = searchField.getText();
        tableModel.setRowCount(0); // Clear table
        try {
            List<Transaksi> searchResults = transaksiController.searchTransaksi(keyword);
            for (Transaksi t : searchResults) {
                Peminjaman p = peminjamanController.getPeminjamanById(t.getIdPeminjaman());
                String namaPenyewa = (p != null) ? p.getNamaPenyewa() : "N/A";
                String platKendaraan = (p != null) ? p.getPlatKendaraan() : "N/A";
                Object[] rowData = {
                    t.getIdTransaksi(),
                    t.getIdPeminjaman(),
                    namaPenyewa,
                    platKendaraan,
                    String.format("Rp %,.2f", t.getTotalBiaya()),
                    t.getStatusPembayaran(),
                    DateUtil.formatDateTime(t.getTanggalTransaksi())
                };
                tableModel.addRow(rowData);
            }
            LOGGER.log(Level.INFO, "Search for '{0}' completed. Found {1} results.", new Object[]{keyword, searchResults.size()});
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to search transaksi: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Gagal mencari transaksi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filterTransaksi() {
        String selectedStatus = (String) statusFilterComboBox.getSelectedItem();
        tableModel.setRowCount(0); // Clear table
        try {
            List<Transaksi> filteredList = transaksiController.filterByStatusPembayaran(selectedStatus);
            for (Transaksi t : filteredList) {
                Peminjaman p = peminjamanController.getPeminjamanById(t.getIdPeminjaman());
                String namaPenyewa = (p != null) ? p.getNamaPenyewa() : "N/A";
                String platKendaraan = (p != null) ? p.getPlatKendaraan() : "N/A";
                Object[] rowData = {
                    t.getIdTransaksi(),
                    t.getIdPeminjaman(),
                    namaPenyewa,
                    platKendaraan,
                    String.format("Rp %,.2f", t.getTotalBiaya()),
                    t.getStatusPembayaran(),
                    DateUtil.formatDateTime(t.getTanggalTransaksi())
                };
                tableModel.addRow(rowData);
            }
            LOGGER.log(Level.INFO, "Filtered by status '{0}'. Displaying {1} results.", new Object[]{selectedStatus, filteredList.size()});
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to filter transaksi: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Gagal memfilter transaksi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void addTransaksi() {
        AddEditTransaksiDialog dialog = new AddEditTransaksiDialog(null, true, null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadTransaksiData(); // Refresh table after add
            JOptionPane.showMessageDialog(this, "Data transaksi berhasil ditambahkan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editTransaksi() {
        int selectedRow = transaksiTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diupdate.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = transaksiTable.convertRowIndexToModel(selectedRow);
        String idTransaksi = (String) tableModel.getValueAt(modelRow, 0);

        try {
            Transaksi selectedTransaksi = transaksiController.getTransaksiById(idTransaksi);
            if (selectedTransaksi != null) {
                AddEditTransaksiDialog dialog = new AddEditTransaksiDialog(null, true, selectedTransaksi);
                dialog.setVisible(true);
                if (dialog.isSuccess()) {
                    loadTransaksiData(); // Refresh table after update
                    JOptionPane.showMessageDialog(this, "Data transaksi berhasil diupdate.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Transaksi tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving transaksi for edit: " + idTransaksi, e);
            JOptionPane.showMessageDialog(this, "Gagal mengambil data transaksi untuk update: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTransaksi() {
        int selectedRow = transaksiTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = transaksiTable.convertRowIndexToModel(selectedRow);
            String idTransaksi = (String) tableModel.getValueAt(modelRow, 0);
            try {
                if (transaksiController.deleteTransaksi(idTransaksi)) {
                    JOptionPane.showMessageDialog(this, "Data transaksi berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    loadTransaksiData(); // Refresh table after delete
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error deleting transaksi: " + idTransaksi, e);
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menghapus data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void printBuktiPembayaran() {
        int selectedRow = transaksiTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih transaksi untuk mencetak bukti pembayaran.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = transaksiTable.convertRowIndexToModel(selectedRow);
        String idTransaksi = (String) tableModel.getValueAt(modelRow, 0);

        try {
            Transaksi transaksiToPrint = transaksiController.getTransaksiById(idTransaksi);
            if (transaksiToPrint != null) {
                Peminjaman associatedPeminjaman = peminjamanController.getPeminjamanById(transaksiToPrint.getIdPeminjaman());
                PdfService.cetakBuktiPembayaran(transaksiToPrint, associatedPeminjaman);
                JOptionPane.showMessageDialog(this, "Bukti pembayaran berhasil dicetak ke 'bukti_pembayaran_" + idTransaksi + ".pdf'", "Cetak Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Transaksi tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DocumentException | IOException e) {
            LOGGER.log(Level.SEVERE, "Gagal mencetak bukti pembayaran PDF untuk transaksi: " + idTransaksi, e);
            JOptionPane.showMessageDialog(this, "Gagal mencetak bukti pembayaran: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}