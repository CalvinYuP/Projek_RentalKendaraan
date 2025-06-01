package view.auth;

import controller.AuthController;
import view.dashboard.DashboardFrame;
import com.formdev.flatlaf.FlatIntelliJLaf; // Pastikan FlatLaf sudah diatur di Main.java

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginForm extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(LoginForm.class.getName());
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private AuthController authController;
    private JCheckBox agreeCheckBox; // From the provided image

    public LoginForm() {
        authController = new AuthController();
        initComponents();
    }

    private void initComponents() {
        setTitle("Login Aplikasi Rental Kendaraan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350); // Increased size to accommodate layout better
        setLocationRelativeTo(null); // Center the window

        // Set layout to BorderLayout
        setLayout(new BorderLayout());

        // --- Header Panel ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20)); // Center alignment, vertical gap
        JLabel titleLabel = new JLabel("Login Form");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- Input Panel ---
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout()); // Using GridBagLayout for flexible alignment
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // Padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0); // Padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Allow components to expand horizontally

        JLabel usernameLabel = new JLabel("Username:");
        inputPanel.add(usernameLabel, gbc);

        gbc.gridy++;
        usernameField = new JTextField(20);
        inputPanel.add(usernameField, gbc);

        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        inputPanel.add(passwordLabel, gbc);

        gbc.gridy++;
        passwordField = new JPasswordField(20);
        inputPanel.add(passwordField, gbc);
        
        gbc.gridy++;
        agreeCheckBox = new JCheckBox("I agree to the Terms & Conditions");
        inputPanel.add(agreeCheckBox, gbc);

        add(inputPanel, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // FlowLayout for button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(120, 35)); // Set preferred size for the button
        
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        buttonPanel.add(loginButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set default values for testing (remove in production)
        usernameField.setText("admin"); // Default admin username
        passwordField.setText("admin123"); // Default admin password
        agreeCheckBox.setSelected(true); // Auto-check for testing
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!agreeCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(this, "You must agree to the Terms & Conditions to log in.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean isAuthenticated = authController.login(username, password);
            if (isAuthenticated) {
                LOGGER.log(Level.INFO, "Login successful. Opening Dashboard.");
                JOptionPane.showMessageDialog(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                DashboardFrame dashboard = new DashboardFrame();
                dashboard.setVisible(true);
                this.dispose(); // Close login form
            } else {
                LOGGER.log(Level.WARNING, "Login failed for user: {0}", username);
                JOptionPane.showMessageDialog(this, "Invalid Username or Password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred during login.", ex);
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Set FlatLaf look and feel
        FlatIntelliJLaf.setup();
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}