package view.dashboard.components;

import controller.KendaraanController;
import model.Bus;
import model.Elf;
import model.Kendaraan;
import model.Mobil;
import model.Motor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddEditKendaraanDialog extends JDialog {
    private static final Logger LOGGER = Logger.getLogger(AddEditKendaraanDialog.class.getName());

    private KendaraanController kendaraanController;
    private Kendaraan currentKendaraan; // Null for Add, populated for Edit
    private boolean isEditMode;
    private boolean success;

    private JTextField idField;
    private JComboBox<String> jenisComboBox;
    private JTextField merkField;
    private JTextField tipeField;
    private JTextField platNomorField;
    private JTextField tahunProduksiField;
    private JTextField hargaPerHariField;
    private JCheckBox detailSpecificCheckBox; // For Mobil (AC) or Motor (Helm)
    private JTextField kapasitasField; // For Elf, Bus
    private JLabel detailSpecificLabel;
    private JLabel kapasitasLabel;
    private JButton saveButton;
    private JButton cancelButton;

    public AddEditKendaraanDialog(Frame parent, boolean modal, Kendaraan kendaraanToEdit) {
        super(parent, modal);
        this.kendaraanController = new KendaraanController();
        this.currentKendaraan = kendaraanToEdit;
        this.isEditMode = (kendaraanToEdit != null);
        this.success = false;

        initComponents();
        populateFields();
        updateFormVisibility(); // Update visibility based on initial type or existing vehicle
    }

    private void initComponents() {
        setTitle(isEditMode ? "Update Data Kendaraan" : "Tambah Data Kendaraan");
        setSize(450, 450);
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

        // ID Kendaraan
        formPanel.add(new JLabel("ID Kendaraan:"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(20);
        idField.setEditable(false); // ID should generally not be editable, especially for existing
        formPanel.add(idField, gbc);

        // Jenis Kendaraan
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Jenis Kendaraan:"), gbc);
        gbc.gridx = 1;
        jenisComboBox = new JComboBox<>(new String[]{"Mobil", "Motor", "Elf", "Bus"});
        jenisComboBox.addActionListener(e -> updateFormVisibility());
        jenisComboBox.setEnabled(!isEditMode); // Cannot change type in edit mode
        formPanel.add(jenisComboBox, gbc);

        // Merk
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Merk:"), gbc);
        gbc.gridx = 1;
        merkField = new JTextField(20);
        merkField.setEditable(!isEditMode);
        formPanel.add(merkField, gbc);

        // Tipe
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Tipe:"), gbc);
        gbc.gridx = 1;
        tipeField = new JTextField(20);
        tipeField.setEditable(!isEditMode);
        formPanel.add(tipeField, gbc);

        // Plat Nomor
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Plat Nomor:"), gbc);
        gbc.gridx = 1;
        platNomorField = new JTextField(20);
        platNomorField.setEditable(!isEditMode);
        formPanel.add(platNomorField, gbc);

        // Tahun Produksi
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Tahun Produksi:"), gbc);
        gbc.gridx = 1;
        tahunProduksiField = new JTextField(20);
        tahunProduksiField.setEditable(!isEditMode);
        formPanel.add(tahunProduksiField, gbc);

        // Harga per Hari
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Harga per Hari:"), gbc);
        gbc.gridx = 1;
        hargaPerHariField = new JTextField(20);
        formPanel.add(hargaPerHariField, gbc);

        // Specific Detail (AC/Helm) - CheckBox
        gbc.gridx = 0;
        gbc.gridy++;
        detailSpecificLabel = new JLabel("Memiliki AC?"); // Default for Mobil
        formPanel.add(detailSpecificLabel, gbc);
        gbc.gridx = 1;
        detailSpecificCheckBox = new JCheckBox();
        formPanel.add(detailSpecificCheckBox, gbc);

        // Kapasitas Penumpang - TextField
        gbc.gridx = 0;
        gbc.gridy++;
        kapasitasLabel = new JLabel("Kapasitas Penumpang:");
        formPanel.add(kapasitasLabel, gbc);
        gbc.gridx = 1;
        kapasitasField = new JTextField(20);
        formPanel.add(kapasitasField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        saveButton = new JButton(isEditMode ? "Update" : "Tambah");
        cancelButton = new JButton("Batal");

        saveButton.addActionListener(e -> saveKendaraan());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateFields() {
        if (isEditMode && currentKendaraan != null) {
            idField.setText(currentKendaraan.getId());
            merkField.setText(currentKendaraan.getMerk());
            tipeField.setText(currentKendaraan.getTipe());
            platNomorField.setText(currentKendaraan.getPlatNomor());
            tahunProduksiField.setText(String.valueOf(currentKendaraan.getTahunProduksi()));
            hargaPerHariField.setText(String.valueOf(currentKendaraan.getHargaPerHari()));
            
            String jenis = currentKendaraan.getClass().getSimpleName();
            jenisComboBox.setSelectedItem(jenis);

            // Populate specific fields based on type
            if (currentKendaraan instanceof Mobil) {
                detailSpecificCheckBox.setSelected(((Mobil) currentKendaraan).isHasAC());
            } else if (currentKendaraan instanceof Motor) {
                detailSpecificCheckBox.setSelected(((Motor) currentKendaraan).isHasHelm());
            } else if (currentKendaraan instanceof Elf) {
                kapasitasField.setText(String.valueOf(((Elf) currentKendaraan).getKapasitasPenumpang()));
            } else if (currentKendaraan instanceof Bus) {
                kapasitasField.setText(String.valueOf(((Bus) currentKendaraan).getKapasitasPenumpang()));
            }
        } else {
             // For add mode, set default ID if needed (or let service generate)
             idField.setText("AUTO-GENERATE"); // Placeholder
        }
    }

    private void updateFormVisibility() {
        String selectedJenis = (String) jenisComboBox.getSelectedItem();

        // Hide all specific fields first
        detailSpecificLabel.setVisible(false);
        detailSpecificCheckBox.setVisible(false);
        kapasitasLabel.setVisible(false);
        kapasitasField.setVisible(false);

        switch (selectedJenis) {
            case "Mobil":
                detailSpecificLabel.setText("Memiliki AC?");
                detailSpecificLabel.setVisible(true);
                detailSpecificCheckBox.setVisible(true);
                break;
            case "Motor":
                detailSpecificLabel.setText("Termasuk Helm?");
                detailSpecificLabel.setVisible(true);
                detailSpecificCheckBox.setVisible(true);
                break;
            case "Elf":
            case "Bus":
                kapasitasLabel.setVisible(true);
                kapasitasField.setVisible(true);
                break;
        }
        
        // Revalidate and repaint to ensure changes are rendered
        revalidate();
        repaint();
    }

    private void saveKendaraan() {
        String id = idField.getText(); // Will be auto-generated for new, or kept for edit
        String jenis = (String) jenisComboBox.getSelectedItem();
        String merk = merkField.getText();
        String tipe = tipeField.getText();
        String platNomor = platNomorField.getText();
        int tahunProduksi;
        double hargaPerHari;

        // Validation
        if (merk.isEmpty() || tipe.isEmpty() || platNomor.isEmpty() || hargaPerHariField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi (kecuali ID jika mode tambah).", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            tahunProduksi = Integer.parseInt(tahunProduksiField.getText());
            hargaPerHari = Double.parseDouble(hargaPerHariField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tahun Produksi dan Harga per Hari harus angka.", "Validasi", JOptionPane.WARNING_MESSAGE);
            LOGGER.log(Level.WARNING, "Invalid number format in AddEditKendaraanDialog.", e);
            return;
        }
        
        if (tahunProduksi <= 1900 || tahunProduksi > java.time.Year.now().getValue() + 5) { // Simple validation
            JOptionPane.showMessageDialog(this, "Tahun Produksi tidak valid.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (hargaPerHari <= 0) {
            JOptionPane.showMessageDialog(this, "Harga per Hari harus lebih dari 0.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Kendaraan kendaraan;
        String keterangan = ""; // Keterangan akan diset di konstruktor subkelas atau setter

        try {
            switch (jenis) {
                case "Mobil":
                    boolean hasAC = detailSpecificCheckBox.isSelected();
                    keterangan = hasAC ? "Ada AC" : "Tidak Ada AC";
                    if (isEditMode && currentKendaraan instanceof Mobil) {
                        ((Mobil) currentKendaraan).setHargaPerHari(hargaPerHari);
                        ((Mobil) currentKendaraan).setHasAC(hasAC);
                        kendaraan = currentKendaraan;
                    } else {
                        // For Add mode, or changing type (though type change is disabled)
                        kendaraan = new Mobil(null, merk, tipe, platNomor, tahunProduksi, hargaPerHari, hasAC);
                    }
                    break;
                case "Motor":
                    boolean hasHelm = detailSpecificCheckBox.isSelected();
                    keterangan = hasHelm ? "Termasuk Helm" : "Tidak Termasuk Helm";
                    if (isEditMode && currentKendaraan instanceof Motor) {
                        ((Motor) currentKendaraan).setHargaPerHari(hargaPerHari);
                        ((Motor) currentKendaraan).setHasHelm(hasHelm);
                        kendaraan = currentKendaraan;
                    } else {
                        kendaraan = new Motor(null, merk, tipe, platNomor, tahunProduksi, hargaPerHari, hasHelm);
                    }
                    break;
                case "Elf":
                    int kapasitasElf;
                    try {
                        kapasitasElf = Integer.parseInt(kapasitasField.getText());
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Kapasitas penumpang Elf harus angka.", "Validasi", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    keterangan = "Kapasitas " + kapasitasElf + " orang";
                    if (isEditMode && currentKendaraan instanceof Elf) {
                        ((Elf) currentKendaraan).setHargaPerHari(hargaPerHari);
                        ((Elf) currentKendaraan).setKapasitasPenumpang(kapasitasElf);
                        kendaraan = currentKendaraan;
                    } else {
                        kendaraan = new Elf(null, merk, tipe, platNomor, tahunProduksi, hargaPerHari, kapasitasElf);
                    }
                    break;
                case "Bus":
                    int kapasitasBus;
                    try {
                        kapasitasBus = Integer.parseInt(kapasitasField.getText());
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Kapasitas penumpang Bus harus angka.", "Validasi", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    keterangan = "Kapasitas " + kapasitasBus + " orang";
                    if (isEditMode && currentKendaraan instanceof Bus) {
                        ((Bus) currentKendaraan).setHargaPerHari(hargaPerHari);
                        ((Bus) currentKendaraan).setKapasitasPenumpang(kapasitasBus);
                        kendaraan = currentKendaraan;
                    } else {
                        kendaraan = new Bus(null, merk, tipe, platNomor, tahunProduksi, hargaPerHari, kapasitasBus);
                    }
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Jenis kendaraan tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
            }

            // Set common properties not handled by specific constructors
            kendaraan.setKeterangan(keterangan);
            kendaraan.setStatusPeminjaman(currentKendaraan != null ? currentKendaraan.getStatusPeminjaman() : "Tersedia"); // Keep status for edit, default for add

            boolean operationSuccess;
            if (isEditMode) {
                // Ensure ID is passed for update operation
                kendaraan.setId(id);
                operationSuccess = kendaraanController.updateKendaraan(kendaraan);
                LOGGER.log(Level.INFO, "Attempting to update vehicle: {0}", kendaraan.getId());
            } else {
                // ID will be generated by service if null or empty
                kendaraan.setId(null); 
                operationSuccess = kendaraanController.addKendaraan(kendaraan);
                LOGGER.log(Level.INFO, "Attempting to add new vehicle: {0}", kendaraan.getPlatNomor());
            }

            if (operationSuccess) {
                success = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data kendaraan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error saving vehicle data.", ex);
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return success;
    }
}