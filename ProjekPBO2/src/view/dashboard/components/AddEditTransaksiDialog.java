package view.dashboard.components;

import controller.PeminjamanController;
import controller.TransaksiController;
import model.Peminjaman;
import model.Transaksi;
import model.Kendaraan;
import controller.KendaraanController;
import util.DateUtil; // Untuk pemformatan tanggal
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import util.DateLabelFormatter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddEditTransaksiDialog extends JDialog {
    private static final Logger LOGGER = Logger.getLogger(AddEditTransaksiDialog.class.getName());

    private TransaksiController transaksiController;
    private PeminjamanController peminjamanController;
    private KendaraanController kendaraanController;// Untuk mengambil data peminjaman
    private Transaksi currentTransaksi; // Null for Add, populated for Edit
    private boolean isEditMode;
    private boolean success;

    private JTextField idTransaksiField;
    private JComboBox<Peminjaman> idPeminjamanComboBox;
    private JTextField totalBiayaField;
    private JComboBox<String> statusPembayaranComboBox;
    private JDatePickerImpl tanggalTransaksiPicker; // Use JDatePicker for date

    private JButton saveButton;
    private JButton cancelButton;

    public AddEditTransaksiDialog(Frame parent, boolean modal, Transaksi transaksiToEdit) {
        super(parent, modal);
        this.transaksiController = new TransaksiController();
        this.peminjamanController = new PeminjamanController();
        this.kendaraanController = new KendaraanController();
        this.currentTransaksi = transaksiToEdit;
        this.isEditMode = (transaksiToEdit != null);
        this.success = false;

        initComponents();
        loadPeminjamanForComboBox();
        populateFields();
        calculateTotalBiayaFromPeminjaman(); // Initial calculation if peminjaman is selected
    }

    private void initComponents() {
        setTitle(isEditMode ? "Update Data Transaksi" : "Tambah Data Transaksi");
        setSize(450, 400);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        // ID Transaksi
        formPanel.add(new JLabel("ID Transaksi:"), gbc);
        gbc.gridx = 1;
        idTransaksiField = new JTextField(25);
        idTransaksiField.setEditable(false);
        formPanel.add(idTransaksiField, gbc);

        // ID Peminjaman (ComboBox)
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("ID Peminjaman:"), gbc);
        gbc.gridx = 1;
        idPeminjamanComboBox = new JComboBox<>();
        idPeminjamanComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Peminjaman) {
                    Peminjaman p = (Peminjaman) value;
                    setText(p.getIdPeminjaman() + " - " + p.getNamaPenyewa() + " (" + p.getPlatKendaraan() + ")");
                }
                return this;
            }
        });
        idPeminjamanComboBox.addActionListener(e -> calculateTotalBiayaFromPeminjaman());
        formPanel.add(idPeminjamanComboBox, gbc);

        // Total Biaya (Auto-calculated/Editable in edit mode?)
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Total Biaya:"), gbc);
        gbc.gridx = 1;
        totalBiayaField = new JTextField(25);
        totalBiayaField.setEditable(false); // Make it read-only, calculated from Peminjaman
        formPanel.add(totalBiayaField, gbc);

        // Status Pembayaran
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Status Pembayaran:"), gbc);
        gbc.gridx = 1;
        statusPembayaranComboBox = new JComboBox<>(new String[]{"Belum Lunas", "Lunas"});
        formPanel.add(statusPembayaranComboBox, gbc);

        // Tanggal Transaksi
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Tanggal Transaksi:"), gbc);
        gbc.gridx = 1;
        UtilDateModel modelTglTransaksi = new UtilDateModel();
        Properties pTglTransaksi = new Properties();
        pTglTransaksi.put("text.today", "Today");
        pTglTransaksi.put("text.month", "Month");
        pTglTransaksi.put("text.year", "Year");
        JDatePanelImpl datePanelTglTransaksi = new JDatePanelImpl(modelTglTransaksi, pTglTransaksi);
        tanggalTransaksiPicker = new JDatePickerImpl(datePanelTglTransaksi, new DateLabelFormatter());
        formPanel.add(tanggalTransaksiPicker, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        saveButton = new JButton(isEditMode ? "Update" : "Tambah");
        cancelButton = new JButton("Batal");

        saveButton.addActionListener(e -> saveTransaksi());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadPeminjamanForComboBox() {
        idPeminjamanComboBox.removeAllItems();
        try {
            List<Peminjaman> peminjamanList = peminjamanController.getAllPeminjaman();
            for (Peminjaman p : peminjamanList) {
                // Only add peminjaman that are "Aktif" or "Pending" for new transactions,
                // or the one associated with the current transaction if in edit mode.
                if (p.getStatusPeminjaman().equalsIgnoreCase("Aktif") || p.getStatusPeminjaman().equalsIgnoreCase("Pending")) {
                     idPeminjamanComboBox.addItem(p);
                } else if (isEditMode && currentTransaksi != null && p.getIdPeminjaman().equals(currentTransaksi.getIdPeminjaman())) {
                    idPeminjamanComboBox.addItem(p);
                }
            }
            if (!isEditMode && idPeminjamanComboBox.getItemCount() > 0) {
                idPeminjamanComboBox.setSelectedIndex(0); // Select first by default
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load peminjaman data for combo box: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Gagal memuat data peminjaman: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFields() {
        if (isEditMode && currentTransaksi != null) {
            idTransaksiField.setText(currentTransaksi.getIdTransaksi());
            statusPembayaranComboBox.setSelectedItem(currentTransaksi.getStatusPembayaran());
            totalBiayaField.setText(String.format("Rp %,.2f", currentTransaksi.getTotalBiaya()));

            // Set Peminjaman in ComboBox
            try {
                Peminjaman associatedPeminjaman = peminjamanController.getPeminjamanById(currentTransaksi.getIdPeminjaman());
                if (associatedPeminjaman != null) {
                    idPeminjamanComboBox.setSelectedItem(associatedPeminjaman);
                    // Ensure the associated peminjaman is visible in the combobox even if its status changes
                    boolean found = false;
                    for (int i = 0; i < idPeminjamanComboBox.getItemCount(); i++) {
                        if (((Peminjaman)idPeminjamanComboBox.getItemAt(i)).getIdPeminjaman().equals(associatedPeminjaman.getIdPeminjaman())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        idPeminjamanComboBox.addItem(associatedPeminjaman);
                        idPeminjamanComboBox.setSelectedItem(associatedPeminjaman);
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not load associated peminjaman for editing: " + currentTransaksi.getIdPeminjaman(), e);
            }
            
            // Set Tanggal Transaksi
            if (currentTransaksi.getTanggalTransaksi() != null) {
                Date dateTransaksi = Date.from(currentTransaksi.getTanggalTransaksi().atZone(ZoneId.systemDefault()).toInstant());
                ((UtilDateModel) tanggalTransaksiPicker.getModel()).setValue(dateTransaksi);
                ((UtilDateModel) tanggalTransaksiPicker.getModel()).setSelected(true);
            }
            
            // Disable peminjaman selection in edit mode
            idPeminjamanComboBox.setEnabled(false);

        } else {
            idTransaksiField.setText("AUTO-GENERATE"); // Placeholder
            statusPembayaranComboBox.setSelectedItem("Belum Lunas"); // Default status for new
            // Set current date for Tanggal Transaksi
            ((UtilDateModel) tanggalTransaksiPicker.getModel()).setValue(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            ((UtilDateModel) tanggalTransaksiPicker.getModel()).setSelected(true);
        }
    }

    private void calculateTotalBiayaFromPeminjaman() {
        Peminjaman selectedPeminjaman = (Peminjaman) idPeminjamanComboBox.getSelectedItem();
        if (selectedPeminjaman != null) {
            double hargaPerHari = 0;
            try {
                // Get vehicle's harga per hari
                Kendaraan kendaraan = kendaraanController.getKendaraanById(selectedPeminjaman.getIdKendaraan());
                if (kendaraan != null) {
                    hargaPerHari = kendaraan.getHargaPerHari();
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error getting vehicle for peminjaman: " + selectedPeminjaman.getIdKendaraan(), e);
            }
            
            int lamaPeminjaman = selectedPeminjaman.getLamaPeminjaman();
            double calculatedTotal = hargaPerHari * lamaPeminjaman;
            totalBiayaField.setText(String.format("Rp %,.2f", calculatedTotal));
        } else {
            totalBiayaField.setText("Rp 0,00");
        }
    }

    private void saveTransaksi() {
        Peminjaman selectedPeminjaman = (Peminjaman) idPeminjamanComboBox.getSelectedItem();
        String statusPembayaran = (String) statusPembayaranComboBox.getSelectedItem();
        Date dateTransaksi = (Date) tanggalTransaksiPicker.getModel().getValue();

        // Validation
        if (selectedPeminjaman == null || statusPembayaran.isEmpty() || dateTransaksi == null) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double totalBiaya;
        try {
            // Parse from formatted string, remove currency symbols and thousands separators
            String totalBiayaText = totalBiayaField.getText().replace("Rp ", "").replace(".", "").replace(",", ".");
            totalBiaya = Double.parseDouble(totalBiayaText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format Total Biaya tidak valid.", "Validasi", JOptionPane.WARNING_MESSAGE);
            LOGGER.log(Level.WARNING, "Invalid total biaya format.", e);
            return;
        }
        
        LocalDateTime localDateTimeTransaksi = dateTransaksi.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        Transaksi transaksi;
        if (isEditMode) {
            transaksi = currentTransaksi; // Use existing object
            transaksi.setIdPeminjaman(selectedPeminjaman.getIdPeminjaman());
            transaksi.setTotalBiaya(totalBiaya);
            transaksi.setStatusPembayaran(statusPembayaran);
            transaksi.setTanggalTransaksi(localDateTimeTransaksi);
        } else {
            transaksi = new Transaksi(
                null, // ID will be generated by service
                selectedPeminjaman.getIdPeminjaman(),
                totalBiaya,
                statusPembayaran,
                localDateTimeTransaksi
            );
        }

        try {
            boolean operationSuccess;
            if (isEditMode) {
                operationSuccess = transaksiController.updateTransaksi(transaksi);
                LOGGER.log(Level.INFO, "Attempting to update transaction: {0}", transaksi.getIdTransaksi());
            } else {
                operationSuccess = transaksiController.addTransaksi(transaksi);
                LOGGER.log(Level.INFO, "Attempting to add new transaction for peminjaman: {0}", transaksi.getIdPeminjaman());
            }

            if (operationSuccess) {
                success = true;
                // If payment status changes to "Lunas", update associated peminjaman status
                if (statusPembayaran.equalsIgnoreCase("Lunas")) {
                    Peminjaman associatedPeminjaman = peminjamanController.getPeminjamanById(selectedPeminjaman.getIdPeminjaman());
                    if (associatedPeminjaman != null && !associatedPeminjaman.getStatusPeminjaman().equalsIgnoreCase("Selesai")) {
                        associatedPeminjaman.setStatusPeminjaman("Selesai");
                        peminjamanController.updatePeminjaman(associatedPeminjaman, null); // Don't re-upload KTP
                        LOGGER.log(Level.INFO, "Peminjaman ID {0} status updated to 'Selesai' due to Lunas payment.", associatedPeminjaman.getIdPeminjaman());
                    }
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error saving transaction data.", ex);
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return success;
    }
}