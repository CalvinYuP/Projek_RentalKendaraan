package app;

import com.formdev.flatlaf.FlatIntelliJLaf;
import view.auth.LoginForm;

import javax.swing.SwingUtilities;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main entry point for the Rental Kendaraan Application.
 * This class sets up the FlatLaf look and feel and launches the LoginForm
 * on the Event Dispatch Thread (EDT) to ensure proper Swing application startup.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Set FlatLaf look and feel for a modern UI.
        // It's crucial to set the look and feel before creating any Swing components.
        try {
            FlatIntelliJLaf.setup();
            LOGGER.log(Level.INFO, "FlatLaf Look and Feel set up successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to set up FlatLaf Look and Feel: " + e.getMessage(), e);
            // Optionally, fall back to default Swing L&F or exit
        }

        // Ensure that GUI updates are done on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Create and display the LoginForm as the initial application window
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
                LOGGER.log(Level.INFO, "LoginForm launched.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error launching LoginForm: " + e.getMessage(), e);
            }
        });
    }
}