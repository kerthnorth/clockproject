// Import necessary classes for GUI, fonts, dates, etc.
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

// Innitially I define the Clock class
public class Clock {
    // I then declare the main GUI components and a shared variable for time
    private JFrame frame;
    private JLabel clockLabel;
    private volatile String currentTime;

    // This here is my constructor to set up the GUI
    public Clock() {
        // I create a window with my title
        frame = new JFrame("CLOCK PROJECT SIMULATOR");
        // Close application when window is closed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set window size
        frame.setSize(600, 300);
        // Center the window on the screen
        frame.setLocationRelativeTo(null);

        // Load the background image
        ImageIcon backgroundIcon = new ImageIcon("1096112.jpg");
        // Get the actual image from the icon
        Image backgroundImage = backgroundIcon.getImage();

        // Create a custom panel that paints the background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the background image scaled to fit the panel
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Set layout for positioning components
        backgroundPanel.setLayout(new BorderLayout());

        // Create and style the clock label
        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Arial", Font.BOLD, 40)); // Set font and size
        clockLabel.setForeground(Color.WHITE); // Set text color to white
        clockLabel.setHorizontalAlignment(JLabel.CENTER); // Center-align the text

        // Add the clock label to the top of the background panel
        backgroundPanel.add(clockLabel, BorderLayout.BEFORE_FIRST_LINE);

        // Set the custom panel as the content pane of the frame
        frame.setContentPane(backgroundPanel);
        // Make the window visible
        frame.setVisible(true);
    }

    // Start a thread that continuously updates the current time
    private void startUpdatingThread() {
        Thread updater = new Thread(() -> {
            // Define time format
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            while (true) {
                // Get and store the current time
                currentTime = formatter.format(new Date());
                try {
                    // Wait 1 second before updating again
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break; // Exit if interrupted
                }
            }
        });

        // Set thread to run in the background
        updater.setDaemon(true);
        // Start the thread
        updater.start();
    }

    // Start a thread that updates the label with the current time
    private void startDisplayThread() {
        Thread display = new Thread(() -> {
            while (true) {
                // Set the label text if time is available
                if (currentTime != null) {
                    clockLabel.setText(currentTime);
                }
                try {
                    // Wait 1 second before refreshing the label
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break; // Exit if interrupted
                }
            }
        });

        // Start the display thread
        display.start();
    }

    // Start both threads: updating and displaying time
    public void startClock() {
        startUpdatingThread();
        startDisplayThread();
    }

    // Main method to run the application
    public static void main(String[] args) {
        // Ensure GUI is created on the Swing event-dispatching thread
        SwingUtilities.invokeLater(() -> {
            // Create the clock app and start it
            Clock app = new Clock();
            app.startClock();
        });
    }
}
