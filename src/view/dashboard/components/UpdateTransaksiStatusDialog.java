package view.dashboard.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class UpdateTransaksiStatusDialog extends JDialog {
    private JComboBox<String> statusComboBox;
    private String newStatus;
    private boolean succeeded = false;

    public UpdateTransaksiStatusDialog(Frame owner, String title, boolean modal, String currentStatus) {
        super(owner, title, modal);
        setResizable(false);
        initComponents(currentStatus);
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents(String currentStatus) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Status Pembayaran:"), gbc);

        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(new String[]{"NOT PAID", "PAID"});
        statusComboBox.setSelectedItem(currentStatus);
        panel.add(statusComboBox, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Batal");

        updateButton.addActionListener(this::updateAction);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateAction(ActionEvent e) {
        newStatus = (String) statusComboBox.getSelectedItem();
        succeeded = true;
        dispose();
    }

    public String getNewStatus() {
        return newStatus;
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}