package view.dashboard;

import model.User; // Asumsi Anda ingin menampilkan detail user yang login
import view.auth.LoginForm; // Untuk logout
import com.formdev.flatlaf.FlatClientProperties; // Untuk FlatLaf properties
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DashboardFrame extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(DashboardFrame.class.getName());
    private JPanel mainPanel;
    private JPanel sidebar;
    private CardLayout cardLayout;

    // Menu buttons
    public JButton btnKendaraan;
    private JButton btnPeminjaman;
    private JButton btnTransaksi;
    private JButton btnLaporan;
    private JButton btnLogout;

    public DashboardFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Aplikasi Rental Kendaraan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Start maximized
        // setSize(1200, 700); // Default size if not maximized
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // --- Sidebar Panel ---
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(50, 60, 70)); // Darker background for sidebar
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        // Company Logo/Name
        JLabel logoLabel = new JLabel("RENTAL KUY");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align in BoxLayout
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0)); // Bottom padding
        sidebar.add(logoLabel);

        // Spacer
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // Menu Buttons
        btnKendaraan = createSidebarButton("Data Kendaraan");
        btnPeminjaman = createSidebarButton("Peminjaman");
        btnTransaksi = createSidebarButton("Transaksi");
        btnLaporan = createSidebarButton("Laporan");
        btnLogout = createSidebarButton("Logout");

        sidebar.add(btnKendaraan);
        sidebar.add(btnPeminjaman);
        sidebar.add(btnTransaksi);
        sidebar.add(btnLaporan);
        
        // Spacer to push logout to bottom
        sidebar.add(Box.createVerticalGlue()); 
        sidebar.add(btnLogout);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20))); // Bottom padding

        add(sidebar, BorderLayout.WEST);

        // --- Main Content Panel ---
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        // Initialize and add panels
        KendaraanPanel kendaraanPanel = new KendaraanPanel();
        PeminjamanPanel peminjamanPanel = new PeminjamanPanel();
        TransaksiPanel transaksiPanel = new TransaksiPanel();
        LaporanPanel laporanPanel = new LaporanPanel();

        mainPanel.add(kendaraanPanel, "KendaraanPanel");
        mainPanel.add(peminjamanPanel, "PeminjamanPanel");
        mainPanel.add(transaksiPanel, "TransaksiPanel");
        mainPanel.add(laporanPanel, "LaporanPanel");

        add(mainPanel, BorderLayout.CENTER);

        // --- Action Listeners for Sidebar Buttons ---
        btnKendaraan.addActionListener(e -> {
            cardLayout.show(mainPanel, "KendaraanPanel");
            kendaraanPanel.loadKendaraanData(); // Refresh data when panel is shown
        });
        btnPeminjaman.addActionListener(e -> {
            cardLayout.show(mainPanel, "PeminjamanPanel");
            peminjamanPanel.loadPeminjamanData(); // Refresh data
        });
        btnTransaksi.addActionListener(e -> {
            cardLayout.show(mainPanel, "TransaksiPanel");
            transaksiPanel.loadTransaksiData(); // Refresh data
        });
        btnLaporan.addActionListener(e -> {
            cardLayout.show(mainPanel, "LaporanPanel");
            laporanPanel.loadDataForReports(); // Refresh data
        });
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                LOGGER.log(Level.INFO, "User initiated logout. Returning to Login Form.");
                new LoginForm().setVisible(true);
                dispose(); // Close dashboard
            }
        });

        // Show KendaraanPanel by default on startup
        cardLayout.show(mainPanel, "KendaraanPanel");
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align in BoxLayout
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); // Make button fill width
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 80, 90)); // Slightly lighter background for buttons
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        button.setFocusPainted(false); // Remove focus border
        button.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT); // FlatLaf styling

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(90, 100, 110));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 80, 90));
            }
        });
        return button;
    }
    
    // Main method for testing dashboard directly (optional, usually started from LoginForm)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DashboardFrame().setVisible(true);
        });
    }
}