package airline_management;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import net.proteanit.sql.DbUtils;

/**
 * FlightDetails - Admin & Passenger UI for managing/viewing flights.
 * Uses role-based views. Fully handles Add, Update, Delete, Search.
 */
public class FlightDetails extends JFrame {

    private String role;
    private String username;
    
    private JTable table;
    private JTextField flightNoField, sourceField, destField, departureField, arrivalField, seatsTotalField, 
                      seatsAvailField, fareField, statusField;
    private JButton insertBtn, updateBtn, deleteBtn;
    
    private JComboBox<String> sourceCombo, destCombo;
    private JTextField dateField;
    private JButton searchBtn;
    private JPanel mainContent;
    private JScrollPane jsp;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    // 🎨 Color Scheme
    private final Color APP_BG = Color.decode("#F0F4F8");       // Soft background
    private final Color PANEL_BG = Color.decode("#E3F2FD");      // Panel background
    private final Color ACCENT_COLOR = Color.decode("#1976D2");  // Primary action color
    private final Color BUTTON_BG = Color.WHITE;
    private final Color BUTTON_TEXT = ACCENT_COLOR;
    private final Color SEARCH_PANEL_BG = Color.decode("#BBDEFB");

    /**
     * Constructor - Initializes UI based on user role.
     */
    public FlightDetails(String username, String role) {
        this.role = role;
        this.username = username;
        getContentPane().setBackground(APP_BG);
        setLayout(null);

        mainContent = new JPanel();
        mainContent.setBounds(250, 180, 850, 450);
        mainContent.setBackground(PANEL_BG);
        mainContent.setLayout(null);
        add(mainContent);

        JLabel hh = new JLabel("<html><b>FLIGHT DETAILS</b></html>");
        hh.setBounds(580, 100, 500, 50);
        hh.setFont(new Font("Tahoma", Font.BOLD, 36));
        hh.setForeground(ACCENT_COLOR);
        add(hh);

        if ("Passenger".equals(username)) { // 👈 Fixed condition to match your logic
            setupPassengerView();
        } else {
            setupAdminView();
        }

        setTitle("Flight Management System");
        setSize(1400, 800);
        setLocationRelativeTo(null);
       
       // setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    /**
     * Sets up Passenger View: Search panel + flight results in cards.
     */
    private void setupPassengerView() {
        JPanel searchPanel = new JPanel();
        searchPanel.setBounds(0, 0, 850, 100);
        searchPanel.setBackground(SEARCH_PANEL_BG);
        searchPanel.setLayout(null);
        mainContent.add(searchPanel);

        JLabel sourceLabel = new JLabel("Source:");
        sourceLabel.setBounds(30, 30, 80, 30);
        sourceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchPanel.add(sourceLabel);

        sourceCombo = new JComboBox<>();
        sourceCombo.setBounds(85, 30, 180, 30);
        styleComboBox(sourceCombo);
        searchPanel.add(sourceCombo);

        JLabel destLabel = new JLabel("Destination:");
        destLabel.setBounds(300, 30, 100, 30);
        destLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchPanel.add(destLabel);

        destCombo = new JComboBox<>();
        destCombo.setBounds(385, 30, 180, 30);
        styleComboBox(destCombo);
        searchPanel.add(destCombo);

        JLabel dateLabel = new JLabel("Date(YYYY-MM-DD):");
        dateLabel.setBounds(575, 30, 150, 30);
        dateLabel.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(dateLabel);

        dateField = new JTextField();
        dateField.setBounds(700, 30, 120, 30);
        styleTextField(dateField);
        searchPanel.add(dateField);

        searchBtn = new JButton("Search Flights");
        searchBtn.setBounds(350, 70, 150, 35);
        styleButton(searchBtn);
        searchBtn.addActionListener(e -> displayPassengerFlights());
        searchPanel.add(searchBtn);

        populateComboBoxes();
    }

    /**
     * Sets up Admin View: CRUD buttons + hidden search panel.
     */
    private void setupAdminView() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(0, 0, 850, 120);
        buttonPanel.setBackground(SEARCH_PANEL_BG);
        buttonPanel.setLayout(null);
        mainContent.add(buttonPanel);

        JButton addBtn = new JButton("1. Add Flight Details");
        addBtn.setBounds(50, 30, 180, 40);
        styleButton(addBtn);
        addBtn.addActionListener(e -> addFlightDialog());
        buttonPanel.add(addBtn);

        JButton DeleteBtn = new JButton("2. Delete Flight");
        DeleteBtn.setBounds(250, 30, 180, 40);
        styleButton(DeleteBtn);
        DeleteBtn.addActionListener(e -> deleteFlightDialog());
        buttonPanel.add(DeleteBtn);

        JButton updateBtn = new JButton("3. Update Flight");
        updateBtn.setBounds(450, 30, 180, 40);
        styleButton(updateBtn);
        updateBtn.addActionListener(e -> updateFlightDialog());
        buttonPanel.add(updateBtn);

        JButton showBtn = new JButton("4. Show All Flights");
        showBtn.setBounds(650, 30, 180, 40);
        styleButton(showBtn);
        showBtn.addActionListener(e -> showAdminSearch());
        buttonPanel.add(showBtn);

        setupHiddenSearchPanel();
    }

    /**
     * Styles buttons consistently.
     */
    private void styleButton(JButton btn) {
        btn.setBackground(BUTTON_BG);
        btn.setForeground(BUTTON_TEXT);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Styles combo boxes.
     */
    private void styleComboBox(JComboBox<?> combo) {
        combo.setFont(new Font("Arial", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
    }

    /**
     * Styles text fields.
     */
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
    }

    /**
     * Hidden search panel for admin "Show Flights" feature.
     */
    private void setupHiddenSearchPanel() {
        JPanel hiddenSearchPanel = new JPanel();
        hiddenSearchPanel.setBounds(0, 130, 850, 80);
        hiddenSearchPanel.setBackground(SEARCH_PANEL_BG);
        hiddenSearchPanel.setLayout(null);
        hiddenSearchPanel.setVisible(false);
        hiddenSearchPanel.setName("hiddenSearchPanel");
        mainContent.add(hiddenSearchPanel);

        JLabel sourceLabel = new JLabel("Source:");
        sourceLabel.setBounds(30, 20, 80, 30);
        sourceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        hiddenSearchPanel.add(sourceLabel);

        sourceCombo = new JComboBox<>();
        sourceCombo.setBounds(110, 20, 180, 30);
        styleComboBox(sourceCombo);
        hiddenSearchPanel.add(sourceCombo);

        JLabel destLabel = new JLabel("Destination:");
        destLabel.setBounds(320, 20, 100, 30);
        destLabel.setFont(new Font("Arial", Font.BOLD, 14));
        hiddenSearchPanel.add(destLabel);

        destCombo = new JComboBox<>();
        destCombo.setBounds(430, 20, 180, 30);
        styleComboBox(destCombo);
        hiddenSearchPanel.add(destCombo);

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setBounds(640, 20, 60, 30);
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        hiddenSearchPanel.add(dateLabel);

        dateField = new JTextField();
        dateField.setBounds(700, 20, 120, 30);
        styleTextField(dateField);
        hiddenSearchPanel.add(dateField);

        searchBtn = new JButton("Search");
        searchBtn.setBounds(370, 55, 120, 30);
        styleButton(searchBtn);
        searchBtn.addActionListener(e -> displayFlightDetails());
        hiddenSearchPanel.add(searchBtn);

        populateComboBoxes();
    }

    /**
     * Shows hidden search panel for admin.
     */
    private void showAdminSearch() {
        Component[] comps = mainContent.getComponents();
        for (Component c : comps) {
            if ("hiddenSearchPanel".equals(c.getName())) {
                c.setVisible(true);
            }
        }
    }

    /**
     * Displays flights for passenger in card layout.
     */
    private void displayPassengerFlights() {
        String source = (String) sourceCombo.getSelectedItem();
        String dest = (String) destCombo.getSelectedItem();
        String date = dateField.getText();

        if (source == null || source.isEmpty() || dest == null || dest.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select source and destination");
            return;
        }
       

        mainContent.removeAll();
        setupPassengerView(); // Keep search visible

        JPanel resultsPanel = new JPanel();
        resultsPanel.setBounds(0, 110, 850, 340);
        resultsPanel.setBackground(PANEL_BG);
        resultsPanel.setLayout(new BorderLayout());
        mainContent.add(resultsPanel);

        JLabel heading = new JLabel("Flights from " + source + " to " + dest);
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        heading.setForeground(ACCENT_COLOR);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        resultsPanel.add(heading, BorderLayout.NORTH);

        JPanel flightsContainer = new JPanel();
        flightsContainer.setLayout(new BoxLayout(flightsContainer, BoxLayout.Y_AXIS));
        flightsContainer.setBackground(PANEL_BG);
        flightsContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        try {
            Conn conn = new Conn();
            StringBuilder query = new StringBuilder("SELECT * FROM flight WHERE fsource = ? AND destination = ?");
            ArrayList<String> params = new ArrayList<>();
            params.add(source);
            params.add(dest);

            if (!date.trim().isEmpty()) {
                query.append(" AND DATE(departure) = ?");
                params.add(date.trim());
            }

            PreparedStatement ps = conn.c.prepareStatement(query.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            boolean hasFlights = false;

            while (rs.next()) {
                hasFlights = true;
                JPanel flightPanel = createFlightPanel(rs);
                flightsContainer.add(flightPanel);
                flightsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
            }

            if (!hasFlights) {
                JLabel noFlights = new JLabel("No flights available for selected route.");
                noFlights.setFont(new Font("Arial", Font.ITALIC, 16));
                noFlights.setForeground(Color.GRAY);
                noFlights.setHorizontalAlignment(SwingConstants.CENTER);
                flightsContainer.add(noFlights);
            }
JScrollPane scroll = new JScrollPane(flightsContainer);
scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
scroll.setBounds(0, 0, 850, 300);
resultsPanel.add(scroll, BorderLayout.CENTER);
        } catch (SQLException e) {
            e.printStackTrace();
            JLabel error = new JLabel("Error loading flights. Please try again.");
            error.setForeground(Color.RED);
            error.setHorizontalAlignment(SwingConstants.CENTER);
            flightsContainer.add(error);
        }

        mainContent.revalidate();
        mainContent.repaint();
    }

    /**
     * Creates a visually appealing flight info panel for passengers.
     */
  /**
 * Creates a visually appealing flight info panel for passengers.
 */
private JPanel createFlightPanel(ResultSet rs) throws SQLException {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
        BorderFactory.createEmptyBorder(10, 10, 10, 10) // Add inner padding
    ));
    panel.setPreferredSize(new Dimension(800, 130));
    panel.setMaximumSize(new Dimension(800, 130)); // Ensure uniform height

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 10, 5, 10);
    URL imgUrl = ClassLoader.getSystemResource("airline_management/assets/Capture.PNG");
System.out.println("Logo URL: " + imgUrl);

JLabel logo;

if (imgUrl != null) {
    try {
        // Load image synchronously using ImageIO (preferred for PNG)
        BufferedImage originalImage = ImageIO.read(imgUrl);

        if (originalImage != null && originalImage.getWidth() > 0 && originalImage.getHeight() > 0) {
            // Scale smoothly
            Image scaledImg = originalImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            // Make it rounded
            Image roundedImg = makeRoundedImage(scaledImg, 60, 60, 15);
            ImageIcon finalIcon = new ImageIcon(roundedImg);
            logo = new JLabel(finalIcon);
        } else {
            throw new Exception("Image is empty or corrupted");
        }
    } catch (Exception e) {
        System.err.println("❌ Failed to load or process image: " + e.getMessage());
        e.printStackTrace();
        logo = createFallbackLogo();
    }
} else {
    System.err.println("❌ Logo resource not found!");
    logo = createFallbackLogo();
}

gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 3;
gbc.anchor = GridBagConstraints.WEST;
gbc.weightx = 0.1;
panel.add(logo, gbc);
    String flightNo = rs.getString("flight_no");
    JLabel flightName = new JLabel(flightNo);
    flightName.setFont(new Font("Arial", Font.BOLD, 18));
    gbc.gridx = 1; gbc.gridy = 0; gbc.gridheight = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 0.3;
    panel.add(flightName, gbc);

    String depTimeStr = rs.getString("departure");
    String depTime = depTimeStr.substring(11, 16);
    JLabel depTimeLabel = new JLabel(depTime);
    depTimeLabel.setFont(new Font("Arial", Font.BOLD, 14));
    gbc.gridx = 1; gbc.gridy = 1;
    gbc.weightx = 0.15;
    panel.add(depTimeLabel, gbc);

    String src = rs.getString("fsource");
    JLabel sourceLabel = new JLabel(src);
    gbc.gridx = 2; gbc.gridy = 1;
    gbc.weightx = 0.15;
    panel.add(sourceLabel, gbc);

    try {
        java.util.Date dep = sdf.parse(depTimeStr);
        String arrTimeStr = rs.getString("arrival");
        java.util.Date arr = sdf.parse(arrTimeStr);
        long diff = arr.getTime() - dep.getTime();
        long hours = diff / (1000 * 60 * 60);
        long mins = (diff % (1000 * 60 * 60)) / (1000 * 60);
        JLabel travelLabel = new JLabel(hours + "h " + mins + "m");
        gbc.gridx = 3; gbc.gridy = 1;
        gbc.weightx = 0.15;
        panel.add(travelLabel, gbc);

        JLabel arrow = new JLabel(" → ");
        arrow.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 4; gbc.gridy = 1;
        gbc.weightx = 0.05;
        panel.add(arrow, gbc);

        String arrTime = arrTimeStr.substring(11, 16);
        JLabel arrTimeLabel = new JLabel(arrTime);
        arrTimeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 5; gbc.gridy = 1;
        gbc.weightx = 0.15;
        panel.add(arrTimeLabel, gbc);

        String dst = rs.getString("destination");
        JLabel destLabel = new JLabel(dst);
        gbc.gridx = 6; gbc.gridy = 1;
        gbc.weightx = 0.15;
        panel.add(destLabel, gbc);
    } catch (ParseException ex) {
        ex.printStackTrace();
    }

    double fare = rs.getDouble("fare");
    JLabel fareLabel = new JLabel("Fare: ₹" + String.format("%.2f", fare));
    fareLabel.setFont(new Font("Arial", Font.BOLD, 14));
    gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 0.5;
    panel.add(fareLabel, gbc);

    String status = rs.getString("fstatus");
    JLabel statusLabel = new JLabel("Status: " + status);
    statusLabel.setFont(new Font("Arial", Font.ITALIC, 13));
    statusLabel.setForeground(status.equalsIgnoreCase("Active") ? Color.GREEN.darker() : Color.RED.darker());
    gbc.gridx = 4; gbc.gridy = 2; gbc.gridwidth = 3;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.weightx = 0.5;
    panel.add(statusLabel, gbc);

    return panel;
}
private JLabel createFallbackLogo() {
    JLabel fallback = new JLabel("✈️");
    fallback.setFont(new Font("Arial", Font.BOLD, 36));
    fallback.setForeground(new Color(70, 130, 180)); // Steel blue
    fallback.setHorizontalAlignment(SwingConstants.CENTER);
    fallback.setPreferredSize(new Dimension(60, 60));
    fallback.setOpaque(false);
    return fallback;
}

/**
 * Creates a rounded version of an image.
 */
private Image makeRoundedImage(Image image, int width, int height, int cornerRadius) {
    BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = output.createGraphics();
    g2.setComposite(AlphaComposite.Src);
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(Color.WHITE);
    g2.fill(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));
    g2.setComposite(AlphaComposite.SrcAtop);
    g2.drawImage(image, 0, 0, null);
    g2.dispose();
    return output;
}
    /**
     * Displays flight search results for admin in JTable.
     */
    private void displayFlightDetails() {
        try {
            showAllFlightsTable();
            Conn conn = new Conn();
            StringBuilder query = new StringBuilder("SELECT * FROM flight WHERE 1=1");
            ArrayList<String> params = new ArrayList<>();

            String source = (String) sourceCombo.getSelectedItem();
            if (source != null && !source.isEmpty()) {
                query.append(" AND fsource = ?");
                params.add(source);
            }

            String dest = (String) destCombo.getSelectedItem();
            if (dest != null && !dest.isEmpty()) {
                query.append(" AND destination = ?");
                params.add(dest);
            }

            String date = dateField.getText().trim();
            if (!date.isEmpty()) {
                query.append(" AND DATE(departure) = ?");
                params.add(date);
            }

            PreparedStatement ps = conn.c.prepareStatement(query.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            table = new JTable();
            table.setModel(DbUtils.resultSetToTableModel(rs));
            table.setFillsViewportHeight(true);

            jsp = new JScrollPane(table);
            jsp.setBounds(0, 220, 850, 230);
           // mainContent.removeAll();

            setupHiddenSearchPanel();
            Component[] comps = mainContent.getComponents();
            for (Component c : comps) {
                if ("hiddenSearchPanel".equals(c.getName())) {
                    c.setVisible(true);
                }
            }

            mainContent.add(jsp);

            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(this, "No flights found matching criteria.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching flight details.");
        }

        mainContent.revalidate();
        mainContent.repaint();
    }

    private void showAllFlightsTable() {
        try {
            Conn conn = new Conn();
            ResultSet rs = conn.s.executeQuery("SELECT * FROM flight");
            table = new JTable();
            table.setModel(DbUtils.resultSetToTableModel(rs));
            table.setFillsViewportHeight(true);

            jsp = new JScrollPane(table);
            jsp.setBounds(0, 0, 850, 400); // Leave space for button
            mainContent.removeAll();
            mainContent.add(jsp);

            // ADDED: Close Button
            JButton closeBtn = new JButton("Close");
            closeBtn.setBounds(380, 410, 100, 30);
            styleButton(closeBtn);
            closeBtn.addActionListener(e -> {
                mainContent.removeAll();
                mainContent.revalidate();
                mainContent.repaint();
                if ("Admin".equals(role)) setupAdminView();
                else setupPassengerView();
            });
            mainContent.add(closeBtn);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading all flights.");
        }

        mainContent.revalidate();
        mainContent.repaint();
    }

    /**
     * Dialog to add new flight — with input validation.
     */
    private void addFlightDialog() {
        JDialog dialog = new JDialog(this, "Add New Flight", true);
        dialog.setLayout(null);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.WHITE);

        int y = 30;
        int labelWidth = 180;
        int fieldWidth = 250;
        int height = 30;
        int gap = 12;

        JLabel[] labels = {
            new JLabel("Flight No:"),
            new JLabel("Source:"),
            new JLabel("Destination:"),
            new JLabel("Departure (YYYY-MM-DD HH:MM:SS):"),
            new JLabel("Arrival (YYYY-MM-DD HH:MM:SS):"),
            new JLabel("Total Seats:"),
            new JLabel("Available Seats:"),
            new JLabel("Fare (₹):"),
            new JLabel("Status:")
        };

        JTextField[] fields = new JTextField[labels.length];
        
        for (int i = 0; i < labels.length; i++) {
            labels[i].setBounds(30, y, labelWidth, height);
            labels[i].setFont(new Font("Arial", Font.BOLD, 13));
            dialog.add(labels[i]);

            fields[i] = new JTextField();
            fields[i].setBounds(30 + labelWidth + 10, y, fieldWidth, height);
            styleTextField(fields[i]);
            dialog.add(fields[i]);

            y += height + gap;
        }

        JButton saveBtn = new JButton("Add Flight");
        saveBtn.setBounds(150, y + 30, 200, 40);
        styleButton(saveBtn);
        saveBtn.addActionListener(e -> {
            try {
                // Trim and validate
                String[] values = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    values[i] = fields[i].getText().trim();
                    if (values[i].isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "All fields are required.");
                        return;
                    }
                }

                Conn conn = new Conn();
                String query = "INSERT INTO flight (flight_no, fsource, destination, departure, arrival, seats_total, seats_available, fare, fstatus) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.c.prepareStatement(query);

                ps.setString(1, values[0]); // flight_no
                ps.setString(2, values[1]); // fsource
                ps.setString(3, values[2]); // destination
                ps.setString(4, values[3]); // departure
                ps.setString(5, values[4]); // arrival

                // Validate & parse numbers
                ps.setInt(6, Integer.parseInt(values[5]));     // seats_total
                ps.setInt(7, Integer.parseInt(values[6]));     // seats_available
                ps.setDouble(8, Double.parseDouble(values[7])); // fare
                ps.setString(9, values[8]); // fstatus

                ps.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Flight added successfully!");
                dialog.dispose();
                showAllFlightsTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Seats and Fare must be valid numbers.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error adding flight. Check format.");
            }
        });
        dialog.add(saveBtn);

        dialog.setVisible(true);
    }

    /**
     * Dialog to delete flight by flight_no.
     */
    private void deleteFlightDialog() {
        String flightNo = JOptionPane.showInputDialog(this, "Enter Flight Number to Delete:");
        if (flightNo != null && !flightNo.trim().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete flight: " + flightNo + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Conn conn = new Conn();
                    String query = "DELETE FROM flight WHERE flight_no = ?";
                    PreparedStatement ps = conn.c.prepareStatement(query);
                    ps.setString(1, flightNo.trim());
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Flight deleted successfully.");
                        showAllFlightsTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Flight not found.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting flight.");
                }
            }
        }
    }

    /**
     * Dialog to update existing flight — FULLY IMPLEMENTED.
     */
    private void updateFlightDialog() {
        String flightNo = JOptionPane.showInputDialog(this, "Enter Flight Number to Update:");
        if (flightNo != null && !flightNo.trim().isEmpty()) {
            JDialog dialog = new JDialog(this, "Update Flight: " + flightNo, true);
            dialog.setLayout(null);
            dialog.setSize(500, 550);
            dialog.setLocationRelativeTo(this);
            dialog.getContentPane().setBackground(Color.WHITE);

            try {
                Conn conn = new Conn();
                String selectQuery = "SELECT * FROM flight WHERE flight_no = ?";
                PreparedStatement selectPs = conn.c.prepareStatement(selectQuery);
                selectPs.setString(1, flightNo);
                ResultSet rs = selectPs.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(dialog, "Flight not found.");
                    dialog.dispose();
                    return;
                }

                int y = 30;
                int labelWidth = 180;
                int fieldWidth = 250;
                int height = 30;
                int gap = 12;

                String[] labelTexts = {
                    "Source:",
                    "Destination:",
                    "Departure:",
                    "Arrival:",
                    "Total Seats:",
                    "Available Seats:",
                    "Fare (₹):",
                    "Status:"
                };

                JTextField[] fields = new JTextField[labelTexts.length];

                for (int i = 0; i < labelTexts.length; i++) {
                    JLabel label = new JLabel(labelTexts[i]);
                    label.setBounds(30, y, labelWidth, height);
                    label.setFont(new Font("Arial", Font.BOLD, 13));
                    dialog.add(label);

                    fields[i] = new JTextField();
                    fields[i].setBounds(30 + labelWidth + 10, y, fieldWidth, height);
                    styleTextField(fields[i]);
                    dialog.add(fields[i]);

                    y += height + gap;
                }

                // ✅ POPULATE FIELDS WITH EXISTING DATA
                fields[0].setText(rs.getString("fsource"));
                fields[1].setText(rs.getString("destination"));
                fields[2].setText(rs.getString("departure"));
                fields[3].setText(rs.getString("arrival"));
                fields[4].setText(String.valueOf(rs.getInt("seats_total")));
                fields[5].setText(String.valueOf(rs.getInt("seats_available")));
                fields[6].setText(String.valueOf(rs.getDouble("fare")));
                fields[7].setText(rs.getString("fstatus"));

                JButton updateBtn = new JButton("Update Flight");
                updateBtn.setBounds(150, y + 30, 200, 40);
                styleButton(updateBtn);
                updateBtn.addActionListener(ev -> {
                    try {
                        String updateQuery = "UPDATE flight SET fsource=?, destination=?, departure=?, arrival=?, seats_total=?, seats_available=?, fare=?, fstatus=? WHERE flight_no=?";
                        PreparedStatement updatePs = conn.c.prepareStatement(updateQuery);

                        // ✅ SET PARAMETERS SAFELY
                        for (int i = 0; i < 8; i++) {
                            String val = fields[i].getText().trim();
                            if (val.isEmpty()) {
                                JOptionPane.showMessageDialog(dialog, labelTexts[i] + " cannot be empty.");
                                return;
                            }
                            if (i >= 4 && i <= 6) { // numeric fields: index 4,5,6 → seats_total, seats_avail, fare
                                if (i == 6) {
                                    updatePs.setDouble(i + 1, Double.parseDouble(val));
                                } else {
                                    updatePs.setInt(i + 1, Integer.parseInt(val));
                                }
                            } else {
                                updatePs.setString(i + 1, val);
                            }
                        }
                        updatePs.setString(9, flightNo); // WHERE

                        int rows = updatePs.executeUpdate();
                        if (rows > 0) {
                            JOptionPane.showMessageDialog(dialog, "Flight updated successfully.");
                            dialog.dispose();
                            showAllFlightsTable();
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Update failed.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(dialog, "Seats and Fare must be valid numbers.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(dialog, "Error updating flight.");
                    }
                });
                dialog.add(updateBtn);

                dialog.setVisible(true);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading flight data.");
            }
        }
    }

    /**
     * Populates source/destination dropdowns from DB.
     */
    private void populateComboBoxes() {
        try {
            Conn conn = new Conn();
            ResultSet rsSource = conn.s.executeQuery("SELECT DISTINCT fsource FROM flight ORDER BY fsource");
            ArrayList<String> sources = new ArrayList<>();
            while (rsSource.next()) {
                sources.add(rsSource.getString("fsource"));
            }
            sourceCombo.setModel(new DefaultComboBoxModel<>(sources.toArray(new String[0])));

            ResultSet rsDest = conn.s.executeQuery("SELECT DISTINCT destination FROM flight ORDER BY destination");
            ArrayList<String> destinations = new ArrayList<>();
            while (rsDest.next()) {
                destinations.add(rsDest.getString("destination"));
            }
            destCombo.setModel(new DefaultComboBoxModel<>(destinations.toArray(new String[0])));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading location data.");
        }
    }

    public FlightDetails() {
        this("defaultUser", "Passenger");
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            new FlightDetails("defaultUser", "Admin");
        });
    }
}