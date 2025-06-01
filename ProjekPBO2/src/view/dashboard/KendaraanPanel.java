package view.dashboard;

import controller.KendaraanController;
import model.Bus;
import model.Elf;
import model.Kendaraan;
import model.Mobil;
import model.Motor;
import view.dashboard.components.AddEditKendaraanDialog;
import service.ReportService; // Untuk PDF
import com.itextpdf.text.DocumentException; // Untuk PDF
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KendaraanPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(KendaraanPanel.class.getName());
    private JTable kendaraanTable;
    private DefaultTableModel tableModel;
    private KendaraanController kendaraanController;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton downloadButton;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private JComboBox<String> sortComboBox;

    public KendaraanPanel() {
        kendaraanController = new KendaraanController();
        initComponents();
        loadKendaraanData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Panel: Title, Search, Filter, Sort ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));

        // Title
        JLabel titleLabel = new JLabel("Manajemen Data Kendaraan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel searchFilterSortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        
        // Search
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Cari (Merk, Tipe, Plat Nomor)...");
        JButton searchButton = new JButton("Cari");
        searchButton.addActionListener(e -> searchKendaraan());
        searchFilterSortPanel.add(searchField);
        searchFilterSortPanel.add(searchButton);

        // Filter by Jenis Kendaraan
        filterComboBox = new JComboBox<>(new String[]{"Semua", "Mobil", "Motor", "Elf", "Bus"});
        filterComboBox.addActionListener(e -> filterKendaraan());
        searchFilterSortPanel.add(new JLabel("Filter Jenis:"));
        searchFilterSortPanel.add(filterComboBox);

        // Sort by Harga
        sortComboBox = new JComboBox<>(new String[]{"Default", "Harga (Termurah)", "Harga (Termahal)"});
        sortComboBox.addActionListener(e -> sortKendaraan());
        searchFilterSortPanel.add(new JLabel("Urutkan:"));
        searchFilterSortPanel.add(sortComboBox);

        topPanel.add(searchFilterSortPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- Table Panel ---
        String[] columnNames = {"ID Kendaraan", "Jenis", "Merk", "Tipe", "Plat Nomor", "Tahun Produksi", "Harga/Hari", "Keterangan", "Status Peminjaman"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        kendaraanTable = new JTable(tableModel);
        kendaraanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only one row can be selected
        kendaraanTable.getTableHeader().setReorderingAllowed(false); // Disable column reordering
        kendaraanTable.setAutoCreateRowSorter(true); // Enable sorting by clicking column headers

        // Double-click to edit
        kendaraanTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    editKendaraan();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(kendaraanTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel: Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Right alignment

        addButton = new JButton("Tambah Data");
        editButton = new JButton("Update Data");
        deleteButton = new JButton("Hapus Data");
        downloadButton = new JButton("Unduh Laporan PDF");

        addButton.addActionListener(e -> addKendaraan());
        editButton.addActionListener(e -> editKendaraan());
        deleteButton.addActionListener(e -> deleteKendaraan());
        downloadButton.addActionListener(e -> downloadReport());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(downloadButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadKendaraanData() {
        tableModel.setRowCount(0); // Clear existing data
        try {
            List<Kendaraan> kendaraanList = kendaraanController.getAllKendaraan();
            for (Kendaraan k : kendaraanList) {
                Object[] rowData = {
                    k.getId(),
                    k.getClass().getSimpleName(), // Jenis Kendaraan (Mobil, Motor, Elf, Bus)
                    k.getMerk(),
                    k.getTipe(),
                    k.getPlatNomor(),
                    k.getTahunProduksi(),
                    k.getHargaPerHari(),
                    k.getKeterangan(),
                    k.getStatusPeminjaman()
                };
                tableModel.addRow(rowData);
            }
            LOGGER.log(Level.INFO, "Kendaraan data loaded successfully. Total: {0}", kendaraanList.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load kendaraan data: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Gagal memuat data kendaraan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchKendaraan() {
        String keyword = searchField.getText();
        tableModel.setRowCount(0); // Clear table
        try {
            List<Kendaraan> searchResults = kendaraanController.searchKendaraan(keyword);
            for (Kendaraan k : searchResults) {
                 Object[] rowData = {
                    k.getId(),
                    k.getClass().getSimpleName(),
                    k.getMerk(),
                    k.getTipe(),
                    k.getPlatNomor(),
                    k.getTahunProduksi(),
                    k.getHargaPerHari(),
                    k.getKeterangan(),
                    k.getStatusPeminjaman()
                };
                tableModel.addRow(rowData);
            }
            LOGGER.log(Level.INFO, "Search for '{0}' completed. Found {1} results.", new Object[]{keyword, searchResults.size()});
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to search kendaraan: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Gagal mencari kendaraan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterKendaraan() {
        String selectedJenis = (String) filterComboBox.getSelectedItem();
        tableModel.setRowCount(0); // Clear table
        try {
            List<Kendaraan> filteredList = kendaraanController.filterKendaraanByJenis(selectedJenis);
            for (Kendaraan k : filteredList) {
                 Object[] rowData = {
                    k.getId(),
                    k.getClass().getSimpleName(),
                    k.getMerk(),
                    k.getTipe(),
                    k.getPlatNomor(),
                    k.getTahunProduksi(),
                    k.getHargaPerHari(),
                    k.getKeterangan(),
                    k.getStatusPeminjaman()
                };
                tableModel.addRow(rowData);
            }
            LOGGER.log(Level.INFO, "Filtered by '{0}'. Displaying {1} results.", new Object[]{selectedJenis, filteredList.size()});
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to filter kendaraan: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Gagal memfilter kendaraan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sortKendaraan() {
        String selectedSort = (String) sortComboBox.getSelectedItem();
        List<Kendaraan> currentList = getCurrentTableData(); // Get data currently in table
        
        List<Kendaraan> sortedList;
        if (selectedSort.equals("Harga (Termurah)")) {
            sortedList = kendaraanController.sortKendaraanByHarga(currentList, true);
        } else if (selectedSort.equals("Harga (Termahal)")) {
            sortedList = kendaraanController.sortKendaraanByHarga(currentList, false);
        } else {
            // "Default" - reload original data
            loadKendaraanData(); 
            return;
        }

        tableModel.setRowCount(0); // Clear table
        for (Kendaraan k : sortedList) {
             Object[] rowData = {
                k.getId(),
                k.getClass().getSimpleName(),
                k.getMerk(),
                k.getTipe(),
                k.getPlatNomor(),
                k.getTahunProduksi(),
                k.getHargaPerHari(),
                k.getKeterangan(),
                k.getStatusPeminjaman()
            };
            tableModel.addRow(rowData);
        }
        LOGGER.log(Level.INFO, "Kendaraan data sorted by '{0}'.", selectedSort);
    }
    
    // Helper to get current data from table model to avoid re-fetching from DB for sort/filter
    private List<Kendaraan> getCurrentTableData() {
        List<Kendaraan> currentList = new java.util.ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String id = (String) tableModel.getValueAt(i, 0);
            String jenis = (String) tableModel.getValueAt(i, 1);
            String merk = (String) tableModel.getValueAt(i, 2);
            String tipe = (String) tableModel.getValueAt(i, 3);
            String platNomor = (String) tableModel.getValueAt(i, 4);
            int tahunProduksi = (int) tableModel.getValueAt(i, 5);
            double hargaPerHari = (double) tableModel.getValueAt(i, 6);
            String keterangan = (String) tableModel.getValueAt(i, 7);
            String statusPeminjaman = (String) tableModel.getValueAt(i, 8);
            
            // Reconstruct Kendaraan object (simplified, as exact sub-type data might be lost)
            // For full accuracy, you might need to re-fetch from controller/service by ID
            Kendaraan k;
            switch (jenis) {
                case "Mobil": k = new Mobil(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, true); break; // Default true/false for simplicity
                case "Motor": k = new Motor(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, true); break;
                case "Elf": k = new Elf(id, merk, tipe, platNomor, tahunProduksi,hargaPerHari, 0); break;
                case "Bus": k = new Bus(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, (int) tableModel.getValueAt(i, 5)); break;
                default: k = new Kendaraan(id, merk, tipe, platNomor, tahunProduksi, hargaPerHari, keterangan, statusPeminjaman) {}; break; // Anonymous inner class for abstract
            }
            k.setKeterangan(keterangan); // Ensure keterangan is set from table
            k.setStatusPeminjaman(statusPeminjaman); // Ensure status is set from table
            currentList.add(k);
        }
        return currentList;
    }


    private void addKendaraan() {
        AddEditKendaraanDialog dialog = new AddEditKendaraanDialog(null, true, null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadKendaraanData(); // Refresh table after add
            JOptionPane.showMessageDialog(this, "Data kendaraan berhasil ditambahkan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editKendaraan() {
        int selectedRow = kendaraanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diupdate.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Convert table row index to model index in case of sorting/filtering
        int modelRow = kendaraanTable.convertRowIndexToModel(selectedRow);
        String idKendaraan = (String) tableModel.getValueAt(modelRow, 0);

        try {
            Kendaraan selectedKendaraan = kendaraanController.getKendaraanById(idKendaraan);
            if (selectedKendaraan != null) {
                AddEditKendaraanDialog dialog = new AddEditKendaraanDialog(null, true, selectedKendaraan);
                dialog.setVisible(true);
                if (dialog.isSuccess()) {
                    loadKendaraanData(); // Refresh table after update
                    JOptionPane.showMessageDialog(this, "Data kendaraan berhasil diupdate.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Kendaraan tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving vehicle for edit: " + idKendaraan, e);
            JOptionPane.showMessageDialog(this, "Gagal mengambil data kendaraan untuk update: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteKendaraan() {
        int selectedRow = kendaraanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = kendaraanTable.convertRowIndexToModel(selectedRow);
            String idKendaraan = (String) tableModel.getValueAt(modelRow, 0);
            try {
                if (kendaraanController.deleteKendaraan(idKendaraan)) {
                    JOptionPane.showMessageDialog(this, "Data kendaraan berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    loadKendaraanData(); // Refresh table after delete
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data kendaraan.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error deleting vehicle: " + idKendaraan, e);
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menghapus data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void downloadReport() {
        try {
            List<Kendaraan> allKendaraan = kendaraanController.getAllKendaraan();
            ReportService.generateKendaraanReport(allKendaraan, "Laporan Semua Kendaraan");
            JOptionPane.showMessageDialog(this, "Laporan Kendaraan berhasil diunduh ke 'laporan_kendaraan_laporan_semua_kendaraan.pdf'", "Unduh Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (DocumentException | IOException e) {
            LOGGER.log(Level.SEVERE, "Gagal membuat laporan PDF kendaraan.", e);
            JOptionPane.showMessageDialog(this, "Gagal membuat laporan PDF kendaraan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}