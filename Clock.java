import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class Clock {
    private JFrame frame;
    private JLabel clockLabel;
    private volatile LocalTime alarmTime;
    private boolean alarmSet = false;

    private Timer clockTimer;
    private Timer stopwatchTimer;
    private Timer countdownTimer;

    private int stopwatchSeconds = 0;
    private JLabel stopwatchLabel;
    private boolean stopwatchRunning = false;

    private int timerSeconds = 0;
    private JLabel timerLabel;
    private boolean timerRunning = false;

    public Clock() {
        frame = new JFrame("Multi-Feature Clock App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Load the background image
        ImageIcon backgroundIcon = new ImageIcon("1096112.jpg");
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

        backgroundPanel.setLayout(new BorderLayout());

        // Create and add the clock label on top of the background
        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Arial", Font.BOLD, 36));
        clockLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(clockLabel, BorderLayout.NORTH);

        // Tabbed Pane for Alarm, Stopwatch, and Timer
        JTabbedPane tabs = new JTabbedPane();
        tabs.setOpaque(false); // Keep it transparent to show background

        // Alarm Tab
        tabs.add("Alarm", buildAlarmPanel());

        // Stopwatch Tab
        tabs.add("Stopwatch", buildStopwatchPanel());

        // Timer Tab
        tabs.add("Timer", buildTimerPanel());

        backgroundPanel.add(tabs, BorderLayout.CENTER);

        // Set the background panel as the content pane
        frame.setContentPane(backgroundPanel);
        frame.setVisible(true);

        // Start the clock
        startClock();
    }

    // Alarm Panel setup
    private JPanel buildAlarmPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setOpaque(false);  // Transparent panel

        JLabel label = new JLabel("Set Alarm (HH:mm:ss):");
        JTextField alarmField = new JTextField(8);
        JButton setAlarm = new JButton("Set Alarm");

        setAlarm.addActionListener(e -> {
            try {
                alarmTime = LocalTime.parse(alarmField.getText(), DateTimeFormatter.ofPattern("HH:mm:ss"));
                alarmSet = true;
                JOptionPane.showMessageDialog(frame, "Alarm set for " + alarmTime);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid time format. Use HH:mm:ss");
            }
        });

        panel.add(label);
        panel.add(alarmField);
        panel.add(setAlarm);
        return panel;
    }

    // Stopwatch Panel setup
    private JPanel buildStopwatchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);  // Transparent panel

        stopwatchLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        stopwatchLabel.setFont(new Font("Arial", Font.BOLD, 32));
        panel.add(stopwatchLabel, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton start = new JButton("Start");
        JButton stop = new JButton("Stop");
        JButton reset = new JButton("Reset");

        start.addActionListener(e -> {
            if (!stopwatchRunning) {
                stopwatchRunning = true;
                stopwatchTimer = new Timer(1000, ev -> updateStopwatch());
                stopwatchTimer.start();
            }
        });

        stop.addActionListener(e -> {
            stopwatchRunning = false;
            if (stopwatchTimer != null) stopwatchTimer.stop();
        });

        reset.addActionListener(e -> {
            stopwatchRunning = false;
            stopwatchSeconds = 0;
            if (stopwatchTimer != null) stopwatchTimer.stop();
            stopwatchLabel.setText("00:00:00");
        });

        buttons.add(start);
        buttons.add(stop);
        buttons.add(reset);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    // Timer Panel setup
    private JPanel buildTimerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);  // Transparent panel

        timerLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 32));
        panel.add(timerLabel, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        JTextField input = new JTextField(5);
        JButton startBtn = new JButton("Start Timer");
        JButton stopBtn = new JButton("Stop");

        startBtn.addActionListener(e -> {
            try {
                timerSeconds = Integer.parseInt(input.getText());
                timerRunning = true;
                countdownTimer = new Timer(1000, ev -> updateCountdown());
                countdownTimer.start();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Enter seconds as a number.");
            }
        });

        stopBtn.addActionListener(e -> {
            timerRunning = false;
            if (countdownTimer != null) countdownTimer.stop();
        });

        inputPanel.add(new JLabel("Seconds:"));
        inputPanel.add(input);
        inputPanel.add(startBtn);
        inputPanel.add(stopBtn);
        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Start the clock and update the time label
    private void startClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        clockTimer = new Timer(1000, e -> {
            String currentTime = LocalTime.now().format(formatter);
            clockLabel.setText("Current Time: " + currentTime);
            if (alarmSet && alarmTime != null && LocalTime.now().withNano(0).equals(alarmTime)) {
                alarmSet = false;
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(frame, "⏰ Alarm! Time is " + alarmTime);
            }
        });
        clockTimer.start();
    }

    // Update Stopwatch
    private void updateStopwatch() {
        stopwatchSeconds++;
        stopwatchLabel.setText(formatSeconds(stopwatchSeconds));
    }

    // Update Timer countdown
    private void updateCountdown() {
        if (timerSeconds > 0) {
            timerSeconds--;
            timerLabel.setText(formatSeconds(timerSeconds));
        } else {
            countdownTimer.stop();
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(frame, "⏰ Timer Done!");
        }
    }

    // Format time (hours, minutes, seconds)
    private String formatSeconds(int totalSecs) {
        int hrs = totalSecs / 3600;
        int mins = (totalSecs % 3600) / 60;
        int secs = totalSecs % 60;
        return String.format("%02d:%02d:%02d", hrs, mins, secs);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Clock::new);
    }
}
