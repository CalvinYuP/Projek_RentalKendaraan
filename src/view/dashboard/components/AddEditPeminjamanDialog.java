package view.dashboard.components;

import controller.KendaraanController;
import controller.PeminjamanController;
import model.Kendaraan;
import model.Peminjaman;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import util.DateUtil; // Pastikan DateUtil sudah ada
import util.DateLabelFormatter; // Custom formatter for JDatePicker

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddEditPeminjamanDialog extends JDialog {
    private static final Logger LOGGER = Logger.getLogger(AddEditPeminjamanDialog.class.getName());

    private PeminjamanController peminjamanController;
    private KendaraanController kendaraanController;
    private Peminjaman currentPeminjaman; // Null for Add, populated for Edit
    private boolean isEditMode;
    private boolean success;

    private JTextField idPeminjamanField;
    private JTextField namaPenyewaField;
    private JTextField kontakPenyewaField;
    private JComboBox<Kendaraan> kendaraanComboBox; // Use Kendaraan object directly
    private JTextField platKendaraanField; // Display only
    private JTextField jenisKendaraanField; // Display only
    private JTextField merkKendaraanField; // Display only
    private JTextField lamaPeminjamanField;
    private JDatePickerImpl tanggalMulaiPicker;
    private JDatePickerImpl tanggalKembaliPicker;
    private JTextField totalBiayaField; // Display only
    private JComboBox<String> statusPeminjamanComboBox;
    private JLabel ktpPathLabel;
    private JButton chooseKtpButton;
    private File selectedKtpFile; // File object for KTP

    private JButton saveButton;
    private JButton cancelButton;

    public AddEditPeminjamanDialog(Frame parent, boolean modal, Peminjaman peminjamanToEdit) {
        super(parent, modal);
        this.peminjamanController = new PeminjamanController();
        this.kendaraanController = new KendaraanController(); // Initialize KendaraanController
        this.currentPeminjaman = peminjamanToEdit;
        this.isEditMode = (peminjamanToEdit != null);
        this.success = false;

        initComponents();
        loadKendaraanForComboBox();
        populateFields();
        calculateTotalBiaya(); // Initial calculation
    }

    private void initComponents() {
        setTitle(isEditMode ? "Update Data Peminjaman" : "Tambah Data Peminjaman");
        setSize(550, 600); // Increased height to accommodate more fields
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

        // ID Peminjaman
        formPanel.add(new JLabel("ID Peminjaman:"), gbc);
        gbc.gridx = 1;
        idPeminjamanField = new JTextField(25);
        idPeminjamanField.setEditable(false);
        formPanel.add(idPeminjamanField, gbc);

        // Nama Penyewa
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Nama Penyewa:"), gbc);
        gbc.gridx = 1;
        namaPenyewaField = new JTextField(25);
        formPanel.add(namaPenyewaField, gbc);

        // Kontak Penyewa
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Kontak Penyewa:"), gbc);
        gbc.gridx = 1;
        kontakPenyewaField = new JTextField(25);
        formPanel.add(kontakPenyewaField, gbc);

        // Kendaraan (ComboBox)
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Pilih Kendaraan:"), gbc);
        gbc.gridx = 1;
        kendaraanComboBox = new JComboBox<>();
        kendaraanComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Kendaraan) {
                    Kendaraan k = (Kendaraan) value;
                    setText(k.getMerk() + " " + k.getTipe() + " (" + k.getPlatNomor() + ") - " + k.getStatusPeminjaman());
                }
                return this;
            }
        });
        kendaraanComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateKendaraanDetails();
                calculateTotalBiaya();
            }
        });
        formPanel.add(kendaraanComboBox, gbc);
        
        // Display fields for selected vehicle
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Plat Kendaraan:"), gbc);
        gbc.gridx = 1;
        platKendaraanField = new JTextField(25);
        platKendaraanField.setEditable(false);
        formPanel.add(platKendaraanField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Jenis Kendaraan:"), gbc);
        gbc.gridx = 1;
        jenisKendaraanField = new JTextField(25);
        jenisKendaraanField.setEditable(false);
        formPanel.add(jenisKendaraanField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Merk Kendaraan:"), gbc);
        gbc.gridx = 1;
        merkKendaraanField = new JTextField(25);
        merkKendaraanField.setEditable(false);
        formPanel.add(merkKendaraanField, gbc);

        // Tanggal Mulai Peminjaman
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Tanggal Mulai:"), gbc);
        gbc.gridx = 1;
        UtilDateModel modelMulai = new UtilDateModel();
        Properties pMulai = new Properties();
        pMulai.put("text.today", "Today");
        pMulai.put("text.month", "Month");
        pMulai.put("text.year", "Year");
        JDatePanelImpl datePanelMulai = new JDatePanelImpl(modelMulai, pMulai);
        tanggalMulaiPicker = new JDatePickerImpl(datePanelMulai, new DateLabelFormatter());
        tanggalMulaiPicker.addActionListener(e -> calculateTotalBiaya()); // Recalculate on date change
        formPanel.add(tanggalMulaiPicker, gbc);

        // Tanggal Kembali Peminjaman
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Tanggal Kembali:"), gbc);
        gbc.gridx = 1;
        UtilDateModel modelKembali = new UtilDateModel();
        Properties pKembali = new Properties();
        pKembali.put("text.today", "Today");
        pKembali.put("text.month", "Month");
        pKembali.put("text.year", "Year");
        JDatePanelImpl datePanelKembali = new JDatePanelImpl(modelKembali, pKembali);
        tanggalKembaliPicker = new JDatePickerImpl(datePanelKembali, new DateLabelFormatter());
        tanggalKembaliPicker.addActionListener(e -> calculateTotalBiaya()); // Recalculate on date change
        formPanel.add(tanggalKembaliPicker, gbc);

        // Lama Peminjaman (calculated)
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Lama Peminjaman (Hari):"), gbc);
        gbc.gridx = 1;
        lamaPeminjamanField = new JTextField(25);
        lamaPeminjamanField.setEditable(false); // Calculated field
        formPanel.add(lamaPeminjamanField, gbc);

        // Total Biaya (calculated)
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Total Biaya:"), gbc);
        gbc.gridx = 1;
        totalBiayaField = new JTextField(25);
        totalBiayaField.setEditable(false); // Calculated field
        formPanel.add(totalBiayaField, gbc);

        // Status Peminjaman
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Status Peminjaman:"), gbc);
        gbc.gridx = 1;
        statusPeminjamanComboBox = new JComboBox<>(new String[]{"Aktif", "Selesai", "Pending", "Dibatalkan"});
        formPanel.add(statusPeminjamanComboBox, gbc);

        // File KTP Path
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("File KTP:"), gbc);
        gbc.gridx = 1;
        JPanel ktpPanel = new JPanel(new BorderLayout());
        ktpPathLabel = new JLabel("Tidak ada file dipilih.");
        ktpPathLabel.setBorder(BorderFactory.createEtchedBorder());
        ktpPanel.add(ktpPathLabel, BorderLayout.CENTER);
        chooseKtpButton = new JButton("Pilih File KTP");
        chooseKtpButton.addActionListener(e -> chooseKtpFile());
        ktpPanel.add(chooseKtpButton, BorderLayout.EAST);
        formPanel.add(ktpPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        saveButton = new JButton(isEditMode ? "Update" : "Tambah");
        cancelButton = new JButton("Batal");

        saveButton.addActionListener(e -> savePeminjaman());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadKendaraanForComboBox() {
        kendaraanComboBox.removeAllItems(); // Clear existing items
        try {
            List<Kendaraan> availableKendaraan = kendaraanController.getAllKendaraan(); // Get all vehicles
            // Filter for 'Tersedia' vehicles if adding new peminjaman,
            // or include the currently rented vehicle if editing existing peminjaman.
            for (Kendaraan k : availableKendaraan) {
                if (k.getStatusPeminjaman().equalsIgnoreCase("Tersedia")) {
                    kendaraanComboBox.addItem(k);
                } else if (isEditMode && currentPeminjaman != null && k.getId().equals(currentPeminjaman.getIdKendaraan())) {
                    // If editing, add the current vehicle even if it's "Dipinjam"
                    kendaraanComboBox.addItem(k);
                }
            }
            if (!isEditMode && kendaraanComboBox.getItemCount() > 0) {
                kendaraanComboBox.setSelectedIndex(0); // Select first available by default
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load vehicle data for combo box: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Gagal memuat data kendaraan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFields() {
        if (isEditMode && currentPeminjaman != null) {
            idPeminjamanField.setText(currentPeminjaman.getIdPeminjaman());
            namaPenyewaField.setText(currentPeminjaman.getNamaPenyewa());
            kontakPenyewaField.setText(currentPeminjaman.getKontakPenyewa());
            lamaPeminjamanField.setText(String.valueOf(currentPeminjaman.getLamaPeminjaman()));
            statusPeminjamanComboBox.setSelectedItem(currentPeminjaman.getStatusPeminjaman());
            
            // Set KTP Path label
            if (currentPeminjaman.getFileKtpPath() != null && !currentPeminjaman.getFileKtpPath().isEmpty()) {
                ktpPathLabel.setText("File KTP: " + new File(currentPeminjaman.getFileKtpPath()).getName());
            } else {
                ktpPathLabel.setText("Tidak ada file dipilih.");
            }

            // Set Dates in JDatePicker
            if (currentPeminjaman.getTanggalMulai() != null) {
                Date dateMulai = Date.from(currentPeminjaman.getTanggalMulai().atStartOfDay(ZoneId.systemDefault()).toInstant());
                ((UtilDateModel) tanggalMulaiPicker.getModel()).setValue(dateMulai);
                ((UtilDateModel) tanggalMulaiPicker.getModel()).setSelected(true);
            }
            if (currentPeminjaman.getTanggalKembali() != null) {
                Date dateKembali = Date.from(currentPeminjaman.getTanggalKembali().atStartOfDay(ZoneId.systemDefault()).toInstant());
                ((UtilDateModel) tanggalKembaliPicker.getModel()).setValue(dateKembali);
                ((UtilDateModel) tanggalKembaliPicker.getModel()).setSelected(true);
            }

            // Select current vehicle in combo box
            try {
                Kendaraan currentVehicle = kendaraanController.getKendaraanById(currentPeminjaman.getIdKendaraan());
                if (currentVehicle != null) {
                    kendaraanComboBox.setSelectedItem(currentVehicle);
                    // Also ensure this vehicle is in the combo box if its status is "Dipinjam"
                    boolean found = false;
                    for (int i = 0; i < kendaraanComboBox.getItemCount(); i++) {
                        if (((Kendaraan)kendaraanComboBox.getItemAt(i)).getId().equals(currentVehicle.getId())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) { // Add it if not already there (e.g., it's currently rented)
                        kendaraanComboBox.addItem(currentVehicle);
                        kendaraanComboBox.setSelectedItem(currentVehicle);
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not load current vehicle for editing: " + currentPeminjaman.getIdKendaraan(), e);
            }
            
            // Disable vehicle selection in edit mode if it's already "Aktif"
            if (currentPeminjaman.getStatusPeminjaman().equalsIgnoreCase("Aktif")) {
                kendaraanComboBox.setEnabled(false);
            }

        } else {
            idPeminjamanField.setText("AUTO-GENERATE"); // Placeholder for new ID
            // Set default dates to today + 1 day for return
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);
            ((UtilDateModel) tanggalMulaiPicker.getModel()).setValue(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            ((UtilDateModel) tanggalMulaiPicker.getModel()).setSelected(true);
            ((UtilDateModel) tanggalKembaliPicker.getModel()).setValue(Date.from(tomorrow.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            ((UtilDateModel) tanggalKembaliPicker.getModel()).setSelected(true);
            statusPeminjamanComboBox.setSelectedItem("Aktif"); // Default status for new peminjaman
        }
        updateKendaraanDetails(); // Update initial vehicle details
    }
    
    private void updateKendaraanDetails() {
        Kendaraan selectedKendaraan = (Kendaraan) kendaraanComboBox.getSelectedItem();
        if (selectedKendaraan != null) {
            platKendaraanField.setText(selectedKendaraan.getPlatNomor());
            jenisKendaraanField.setText(selectedKendaraan.getClass().getSimpleName());
            merkKendaraanField.setText(selectedKendaraan.getMerk());
        } else {
            platKendaraanField.setText("");
            jenisKendaraanField.setText("");
            merkKendaraanField.setText("");
        }
    }

    private void calculateTotalBiaya() {
        Kendaraan selectedKendaraan = (Kendaraan) kendaraanComboBox.getSelectedItem();
        if (selectedKendaraan == null) {
            lamaPeminjamanField.setText("0");
            totalBiayaField.setText("Rp 0,00");
            return;
        }

        Date dateMulai = (Date) tanggalMulaiPicker.getModel().getValue();
        Date dateKembali = (Date) tanggalKembaliPicker.getModel().getValue();

        if (dateMulai == null || dateKembali == null) {
            lamaPeminjamanField.setText("0");
            totalBiayaField.setText("Rp 0,00");
            return;
        }

        LocalDate localDateMulai = dateMulai.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDateKembali = dateKembali.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (localDateKembali.isBefore(localDateMulai)) {
            JOptionPane.showMessageDialog(this, "Tanggal Kembali tidak boleh sebelum Tanggal Mulai.", "Validasi Tanggal", JOptionPane.WARNING_MESSAGE);
            lamaPeminjamanField.setText("0");
            totalBiayaField.setText("Rp 0,00");
            return;
        }

        long days = Period.between(localDateMulai, localDateKembali).getDays();
        if (days == 0) days = 1; // Minimum 1 day if dates are same

        lamaPeminjamanField.setText(String.valueOf(days));
        double hargaPerHari = selectedKendaraan.getHargaPerHari();
        double totalBiaya = hargaPerHari * days;
        totalBiayaField.setText(String.format("Rp %,.2f", totalBiaya));
    }

    private void chooseKtpFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih File KTP");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
        
        int userSelection = fileChooser.showOpenDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            selectedKtpFile = fileChooser.getSelectedFile();
            ktpPathLabel.setText("File KTP: " + selectedKtpFile.getName());
            LOGGER.log(Level.INFO, "KTP file selected: {0}", selectedKtpFile.getAbsolutePath());
        }
    }

    private void savePeminjaman() {
        // Validation
        String namaPenyewa = namaPenyewaField.getText();
        String kontakPenyewa = kontakPenyewaField.getText();
        Kendaraan selectedKendaraan = (Kendaraan) kendaraanComboBox.getSelectedItem();
        
        Date dateMulai = (Date) tanggalMulaiPicker.getModel().getValue();
        Date dateKembali = (Date) tanggalKembaliPicker.getModel().getValue();
        
        if (namaPenyewa.isEmpty() || kontakPenyewa.isEmpty() || selectedKendaraan == null || dateMulai == null || dateKembali == null) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!isEditMode && selectedKendaraan.getStatusPeminjaman().equalsIgnoreCase("Dipinjam")) {
            JOptionPane.showMessageDialog(this, "Kendaraan ini sedang dipinjam. Pilih kendaraan lain.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate localDateMulai = dateMulai.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDateKembali = dateKembali.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (localDateKembali.isBefore(localDateMulai)) {
            JOptionPane.showMessageDialog(this, "Tanggal Kembali tidak boleh sebelum Tanggal Mulai.", "Validasi Tanggal", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Ensure KTP is selected for new entries, or if it was empty and now chosen in edit mode
        if (!isEditMode && selectedKtpFile == null) {
            JOptionPane.showMessageDialog(this, "Anda harus memilih file KTP.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // If in edit mode and the path is currently empty, but a file was chosen, it's fine.
        // If in edit mode and no new file was chosen, but an old path exists, also fine.

        int lamaPeminjaman;
        try {
            lamaPeminjaman = Integer.parseInt(lamaPeminjamanField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Lama Peminjaman tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String statusPeminjaman = (String) statusPeminjamanComboBox.getSelectedItem();
        String idKendaraan = selectedKendaraan.getId();
        String platKendaraan = selectedKendaraan.getPlatNomor();
        String jenisKendaraan = selectedKendaraan.getClass().getSimpleName();
        String merkKendaraan = selectedKendaraan.getMerk();

        Peminjaman peminjaman;
        if (isEditMode) {
            peminjaman = currentPeminjaman; // Use existing object
            peminjaman.setNamaPenyewa(namaPenyewa);
            peminjaman.setKontakPenyewa(kontakPenyewa);
            peminjaman.setIdKendaraan(idKendaraan);
            peminjaman.setPlatKendaraan(platKendaraan);
            peminjaman.setJenisKendaraan(jenisKendaraan);
            peminjaman.setMerkKendaraan(merkKendaraan);
            peminjaman.setLamaPeminjaman(lamaPeminjaman);
            peminjaman.setTanggalMulai(localDateMulai);
            peminjaman.setTanggalKembali(localDateKembali);
            peminjaman.setStatusPeminjaman(statusPeminjaman);
            // KTP path will be updated by controller if selectedKtpFile is not null
        } else {
            peminjaman = new Peminjaman(
                null, // ID will be generated by service
                namaPenyewa, kontakPenyewa, idKendaraan, platKendaraan,
                jenisKendaraan, merkKendaraan, lamaPeminjaman, localDateMulai, localDateKembali,
                statusPeminjaman, null // KTP path will be set by controller
            );
        }

        try {
            boolean operationSuccess;
            if (isEditMode) {
                operationSuccess = peminjamanController.updatePeminjaman(peminjaman, selectedKtpFile);
                LOGGER.log(Level.INFO, "Attempting to update peminjaman: {0}", peminjaman.getIdPeminjaman());
            } else {
                operationSuccess = peminjamanController.addPeminjaman(peminjaman, selectedKtpFile);
                LOGGER.log(Level.INFO, "Attempting to add new peminjaman for vehicle: {0}", peminjaman.getPlatKendaraan());
            }

            if (operationSuccess) {
                success = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data peminjaman.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error saving peminjaman data.", ex);
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return success;
    }
}