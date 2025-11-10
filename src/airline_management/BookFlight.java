package airline_management;

import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.barcodes.BarcodeQRCode;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Desktop;

public class BookFlight extends JFrame implements ActionListener {

    // Form Fields
    private JTextField tfaadhar, cardNumberField, cvvField;
    private JLabel tfname, tfnationality, tfaddress, labelgender, labelfcode;
    private JButton bookflight, fetchButton, flight, scanQrButton;
    private JDateChooser dcdate;
    private JComboBox<String> sourceComboBox, destinationComboBox;
    private JRadioButton cardPaymentRadio, qrPaymentRadio;
    private ButtonGroup paymentGroup;

    // Seat Selection
    private JPanel seatPanel;
    private JButton[][] seatButtons;
    private String selectedSeat = null;
    private Set<String> occupiedSeats = new HashSet<>();

    // Payment Fields
    private JComboBox<String> expiryMonth, expiryYear;
    private JLabel razorpayQRLabel;
    private String qrData;

    // Data Lists
    private List<String> sourceList = new ArrayList<>();
    private List<String> destinationList = new ArrayList<>();

    // Main Panel with Scroll
    private GradientPanel mainContent;

    // Splash Screen
    private JPanel splashPanel;

    // Constants
    private static final int LEFT_MARGIN = 50;
    private static final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private static final Color ACCENT_COLOR = new Color(255, 140, 0);
    private static final Color SUCCESS_COLOR = new Color(0, 180, 0);
    private static final Color OCCUPIED_COLOR = new Color(220, 50, 50);
    private static final Color WINDOW_COLOR = new Color(173, 216, 230);
    private static final Color AISLE_COLOR = new Color(255, 255, 224);

    public BookFlight() {
        initializeUI();
        loadFlightLocations();
        updateComboBoxes();
        animateFadeIn();
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("✈️ Airline Management System - Book Flight");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Background Image Setup
        ImageIcon backgroundImage = null;
        try {
            java.net.URL imgURL = ClassLoader.getSystemResource("airline_management/assets/Capture3.PNG");
            if (imgURL != null) {
                backgroundImage = new ImageIcon(imgURL);
                if (backgroundImage.getIconWidth() <= 0) {
                    System.err.println("Background image is invalid");
                    backgroundImage = null;
                }
            } else {
                System.err.println("Background image not found");
            }
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
        }

        final ImageIcon finalBackgroundImage = backgroundImage;
        JLabel backgroundLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (finalBackgroundImage != null && finalBackgroundImage.getImage() != null) {
                    Image img = finalBackgroundImage.getImage();
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(240, 240, 240));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        backgroundLabel.setLayout(new BorderLayout());

        // SPLASH SCREEN
        splashPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(PRIMARY_COLOR);
                g2.setFont(new Font("Roboto", Font.BOLD, 30));
                g2.drawString("Airline Management System", (getWidth() - 350) / 2, getHeight() / 2);
                g2.setFont(new Font("Roboto", Font.PLAIN, 20));
                g2.drawString("Loading...", (getWidth() - 100) / 2, getHeight() / 2 + 50);
                g2.dispose();
            }
        };
        splashPanel.setOpaque(false);
        backgroundLabel.add(splashPanel, BorderLayout.CENTER);

        // MAIN CONTENT PANEL
        mainContent = new GradientPanel();
        mainContent.setLayout(new GridBagLayout());
        mainContent.setPreferredSize(new Dimension(1200, 1600));
        mainContent.setVisible(false);

        // Add Components
        addComponentsToMainPanel();

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        backgroundLabel.add(scrollPane, BorderLayout.CENTER);

        add(backgroundLabel, BorderLayout.CENTER);
        setSize(1400, 900);
        setLocationRelativeTo(null);
    }

    private void addComponentsToMainPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Heading
        JLabel heading = new JLabel("✈️ Book Your Flight");
        heading.setFont(new Font("Roboto", Font.BOLD, 38));
        heading.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        mainContent.add(heading, gbc);

        // Passenger Details Panel
        JPanel passengerPanel = createSectionPanel("Passenger Details");
        gbc.gridy = 1;
        mainContent.add(passengerPanel, gbc);

        GridBagConstraints pgbc = new GridBagConstraints();
        pgbc.insets = new Insets(5, 5, 5, 5);
        pgbc.fill = GridBagConstraints.HORIZONTAL;

        pgbc.gridx = 0;
        pgbc.gridy = 0;
        passengerPanel.add(addLabel("Aadhar Number:"), pgbc);
        pgbc.gridx = 1;
        tfaadhar = createTextField();
        passengerPanel.add(tfaadhar, pgbc);
        pgbc.gridx = 2;
        fetchButton = createStyledButton("🔍 Fetch User", PRIMARY_COLOR);
        fetchButton.addActionListener(this);
        passengerPanel.add(fetchButton, pgbc);

        pgbc.gridx = 0;
        pgbc.gridy = 1;
        passengerPanel.add(addLabel("Name:"), pgbc);
        pgbc.gridx = 1;
        tfname = createInfoLabel();
        passengerPanel.add(tfname, pgbc);

        pgbc.gridx = 0;
        pgbc.gridy = 2;
        passengerPanel.add(addLabel("Nationality:"), pgbc);
        pgbc.gridx = 1;
        tfnationality = createInfoLabel();
        passengerPanel.add(tfnationality, pgbc);

        pgbc.gridx = 0;
        pgbc.gridy = 3;
        passengerPanel.add(addLabel("Address:"), pgbc);
        pgbc.gridx = 1;
        tfaddress = createInfoLabel();
        passengerPanel.add(tfaddress, pgbc);

        pgbc.gridx = 0;
        pgbc.gridy = 4;
        passengerPanel.add(addLabel("Gender:"), pgbc);
        pgbc.gridx = 1;
        labelgender = createInfoLabel();
        passengerPanel.add(labelgender, pgbc);

        // Flight Details Panel
        JPanel flightPanel = createSectionPanel("Flight Details");
        gbc.gridy = 2;
        mainContent.add(flightPanel, gbc);

        pgbc.gridx = 0;
        pgbc.gridy = 0;
        flightPanel.add(addLabel("Source:"), pgbc);
        pgbc.gridx = 1;
        sourceComboBox = new JComboBox<>();
        styleComboBox(sourceComboBox);
        flightPanel.add(sourceComboBox, pgbc);

        pgbc.gridx = 0;
        pgbc.gridy = 1;
        flightPanel.add(addLabel("Destination:"), pgbc);
        pgbc.gridx = 1;
        destinationComboBox = new JComboBox<>();
        styleComboBox(destinationComboBox);
        flightPanel.add(destinationComboBox, pgbc);

        pgbc.gridx = 0;
        pgbc.gridy = 2;
        flightPanel.add(addLabel("Travel Date:"), pgbc);
        pgbc.gridx = 1;
        dcdate = new JDateChooser();
        dcdate.setDateFormatString("yyyy-MM-dd");
        dcdate.setFont(new Font("Roboto", Font.PLAIN, 16));
        dcdate.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1, true));
        dcdate.setPreferredSize(new Dimension(200, 30));
        flightPanel.add(dcdate, pgbc);

        pgbc.gridx = 0;
        pgbc.gridy = 3;
        flightPanel.add(addLabel("Flight Code:"), pgbc);
        pgbc.gridx = 1;
        labelfcode = createInfoLabel();
        flightPanel.add(labelfcode, pgbc);

        pgbc.gridx = 1;
        pgbc.gridy = 4;
        flight = createStyledButton("🛫 Fetch Flights", ACCENT_COLOR);
        flight.addActionListener(this);
        flightPanel.add(flight, pgbc);

        // Seat Selection Panel
        JPanel seatSelectionPanel = createSectionPanel("Seat Selection");
        gbc.gridy = 3;
        mainContent.add(seatSelectionPanel, gbc);

        JLabel seatLayoutLabel = new JLabel("Layout: Window | Aisle | [Aisle] | Aisle | Window");
        seatLayoutLabel.setFont(new Font("Roboto", Font.ITALIC, 12));
        seatLayoutLabel.setForeground(Color.GRAY);
        pgbc.gridx = 0;
        pgbc.gridy = 0;
        pgbc.gridwidth = 2;
        seatSelectionPanel.add(seatLayoutLabel, pgbc);

        seatPanel = new JPanel(new GridLayout(4, 5, 10, 10));
        seatPanel.setBorder(BorderFactory.createTitledBorder("💺 Airplane Seat Map"));
        seatPanel.setOpaque(false);
        createSeatGrid();
        pgbc.gridy = 1;
        seatSelectionPanel.add(seatPanel, pgbc);

        // Payment Options Panel
        JPanel paymentPanel = createSectionPanel("Payment Options");
        gbc.gridy = 4;
        mainContent.add(paymentPanel, gbc);

        pgbc.gridx = 0;
        pgbc.gridy = 0;
        pgbc.gridwidth = 2;
        cardPaymentRadio = new JRadioButton("Card Payment", true);
        qrPaymentRadio = new JRadioButton("QR Scanner");
        cardPaymentRadio.setFont(new Font("Roboto", Font.PLAIN, 16));
        qrPaymentRadio.setFont(new Font("Roboto", Font.PLAIN, 16));
        cardPaymentRadio.setOpaque(false);
        qrPaymentRadio.setOpaque(false);
        paymentGroup = new ButtonGroup();
        paymentGroup.add(cardPaymentRadio);
        paymentGroup.add(qrPaymentRadio);
        cardPaymentRadio.addActionListener(e -> togglePaymentFields());
        qrPaymentRadio.addActionListener(e -> togglePaymentFields());
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.setOpaque(false);
        radioPanel.add(cardPaymentRadio);
        radioPanel.add(qrPaymentRadio);
        paymentPanel.add(radioPanel, pgbc);

        pgbc.gridwidth = 1;
        pgbc.gridx = 0;
        pgbc.gridy = 1;
        paymentPanel.add(addLabel("Card No:"), pgbc);
        pgbc.gridx = 1;
        cardNumberField = createTextField();
        cardNumberField.setColumns(16);
        paymentPanel.add(cardNumberField, pgbc);

        pgbc.gridx = 0;
        pgbc.gridy = 2;
        paymentPanel.add(addLabel("Expiry:"), pgbc);
        pgbc.gridx = 1;
        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        expiryMonth = new JComboBox<>(months);
        styleComboBox(expiryMonth);
        expiryMonth.setPreferredSize(new Dimension(80, 30));
        JPanel expiryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        expiryPanel.setOpaque(false);
        expiryPanel.add(expiryMonth);
        String[] years = {"24", "25", "26", "27", "28", "29", "30"};
        expiryYear = new JComboBox<>(years);
        styleComboBox(expiryYear);
        expiryYear.setPreferredSize(new Dimension(60, 30));
        expiryPanel.add(expiryYear);
        paymentPanel.add(expiryPanel, pgbc);

        pgbc.gridx = 0;
        pgbc.gridy = 3;
        paymentPanel.add(addLabel("CVV:"), pgbc);
        pgbc.gridx = 1;
        cvvField = createTextField();
        cvvField.setColumns(4);
        paymentPanel.add(cvvField, pgbc);

        pgbc.gridx = 0;
        pgbc.gridy = 4;
        JLabel qrHeader = new JLabel("📲 Scan to Pay");
        qrHeader.setFont(new Font("Roboto", Font.BOLD, 18));
        qrHeader.setForeground(PRIMARY_COLOR);
        paymentPanel.add(qrHeader, pgbc);

        pgbc.gridx = 0;
        pgbc.gridy = 5;
        pgbc.gridwidth = 2;
        razorpayQRLabel = new JLabel();
        razorpayQRLabel.setPreferredSize(new Dimension(200, 200));
        razorpayQRLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        razorpayQRLabel.setHorizontalAlignment(SwingConstants.CENTER);
        razorpayQRLabel.setVerticalAlignment(SwingConstants.CENTER);
        razorpayQRLabel.setText("QR Code");
        paymentPanel.add(razorpayQRLabel, pgbc);

        pgbc.gridy = 6;
        scanQrButton = createStyledButton("📷 Scan QR", PRIMARY_COLOR);
        scanQrButton.addActionListener(this);
        paymentPanel.add(scanQrButton, pgbc);

        // Book Button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        bookflight = createStyledButton("✅ Pay & Book Flight", SUCCESS_COLOR);
        bookflight.setFont(new Font("Roboto", Font.BOLD, 18));
        bookflight.setPreferredSize(new Dimension(300, 50));
        bookflight.addActionListener(this);
        mainContent.add(bookflight, gbc);

        togglePaymentFields();
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
            title,
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Roboto", Font.BOLD, 16),
            PRIMARY_COLOR
        ));
        return panel;
    }

    private JLabel addLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Roboto", Font.PLAIN, 16));
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Roboto", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBackground(new Color(245, 245, 245));
        field.setPreferredSize(new Dimension(200, 30));
        return field;
    }

    private JLabel createInfoLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Roboto", Font.PLAIN, 16));
        label.setForeground(new Color(50, 50, 50));
        label.setOpaque(true);
        label.setBackground(new Color(230, 230, 230));
        label.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        label.setPreferredSize(new Dimension(200, 30));
        return label;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? getBackground() : getBackground().darker());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 35));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Roboto", Font.PLAIN, 16));
        combo.setBackground(new Color(245, 245, 245));
        combo.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1, true));
        combo.setPreferredSize(new Dimension(180, 30));
    }

    private void createSeatGrid() {
        seatButtons = new JButton[4][4];
        seatPanel.setLayout(new GridLayout(4, 5, 10, 10));
        String[] rows = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            String seatIdLeftWindow = rows[i] + "1";
            JButton btnLeftWindow = createSeatButton(seatIdLeftWindow, "Window", "🌅 A" + (i + 1));
            seatButtons[i][0] = btnLeftWindow;
            seatPanel.add(btnLeftWindow);

            String seatIdLeftAisle = rows[i] + "2";
            JButton btnLeftAisle = createSeatButton(seatIdLeftAisle, "Aisle", "🚶 B" + (i + 1));
            seatButtons[i][1] = btnLeftAisle;
            seatPanel.add(btnLeftAisle);

            JPanel aisleGap = new JPanel();
            aisleGap.setOpaque(false);
            seatPanel.add(aisleGap);

            String seatIdRightAisle = rows[i] + "4";
            JButton btnRightAisle = createSeatButton(seatIdRightAisle, "Aisle", "🚶 E" + (i + 1));
            seatButtons[i][2] = btnRightAisle;
            seatPanel.add(btnRightAisle);

            String seatIdRightWindow = rows[i] + "5";
            JButton btnRightWindow = createSeatButton(seatIdRightWindow, "Window", "🌅 F" + (i + 1));
            seatButtons[i][3] = btnRightWindow;
            seatPanel.add(btnRightWindow);
        }
    }

    private JButton createSeatButton(String seatId, String type, String displayText) {
        JButton btn = new JButton(displayText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setActionCommand(seatId);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Roboto", Font.BOLD, 12));
        btn.setContentAreaFilled(false);
        btn.setToolTipText(type + " Seat - " + seatId);
        btn.setBackground(type.equals("Window") ? WINDOW_COLOR : AISLE_COLOR);
        btn.setEnabled(true);
        btn.addActionListener(e -> selectSeat(btn, seatId));
        return btn;
    }

    private void selectSeat(JButton clickedBtn, String seatId) {
        if (occupiedSeats.contains(seatId)) {
            JOptionPane.showMessageDialog(this, "This seat is already occupied.", "Seat Unavailable", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedSeat != null) {
            for (JButton[] row : seatButtons) {
                for (JButton b : row) {
                    if (b != null && b.getActionCommand().equals(selectedSeat)) {
                        String type = b.getToolTipText().startsWith("Window") ? "Window" : "Aisle";
                        b.setBackground(type.equals("Window") ? WINDOW_COLOR : AISLE_COLOR);
                        b.setText(type.equals("Window") ? "🌅 " + selectedSeat : "🚶 " + selectedSeat);
                    }
                }
            }
        }
        selectedSeat = seatId;
        clickedBtn.setBackground(SUCCESS_COLOR);
        clickedBtn.setText("✅ " + seatId);
    }

    private void loadOccupiedSeats(String flightCode, String travelDate) {
        occupiedSeats.clear();
        try (Connection conn = new Conn().c;
             PreparedStatement ps = conn.prepareStatement("SELECT seat FROM reservation WHERE flight_code = ? AND travel_date = ?")) {
            ps.setString(1, flightCode);
            ps.setString(2, travelDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                occupiedSeats.add(rs.getString("seat"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load occupied seats: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        resetSeatGrid();
    }

    private void resetSeatGrid() {
        for (JButton[] row : seatButtons) {
            for (JButton btn : row) {
                if (btn != null) {
                    String seatId = btn.getActionCommand();
                    if (occupiedSeats.contains(seatId)) {
                        btn.setBackground(OCCUPIED_COLOR);
                        btn.setText("❌ " + seatId);
                        btn.setEnabled(false);
                    } else {
                        String type = btn.getToolTipText().startsWith("Window") ? "Window" : "Aisle";
                        btn.setBackground(type.equals("Window") ? WINDOW_COLOR : AISLE_COLOR);
                        btn.setText(type.equals("Window") ? "🌅 " + seatId : "🚶 " + seatId);
                        btn.setEnabled(true);
                    }
                    if (seatId.equals(selectedSeat)) {
                        btn.setBackground(SUCCESS_COLOR);
                        btn.setText("✅ " + seatId);
                    }
                }
            }
        }
    }

    private void togglePaymentFields() {
        boolean isCardPayment = cardPaymentRadio.isSelected();
        cardNumberField.setEnabled(isCardPayment);
        expiryMonth.setEnabled(isCardPayment);
        expiryYear.setEnabled(isCardPayment);
        cvvField.setEnabled(isCardPayment);
        razorpayQRLabel.setVisible(!isCardPayment);
        scanQrButton.setVisible(!isCardPayment);
    }

    private void loadFlightLocations() {
        try (Connection conn = new Conn().c;
             PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT fsource, destination FROM flight")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String src = rs.getString("fsource");
                String dest = rs.getString("destination");
                if (src != null && !sourceList.contains(src)) sourceList.add(src);
                if (dest != null && !destinationList.contains(dest)) destinationList.add(dest);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load locations: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateComboBoxes() {
        sourceComboBox.removeAllItems();
        destinationComboBox.removeAllItems();
        sourceList.forEach(sourceComboBox::addItem);
        destinationList.forEach(destinationComboBox::addItem);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == fetchButton) {
            fetchPassengerDetails();
        } else if (ae.getSource() == flight) {
            fetchFlightDetails();
        } else if (ae.getSource() == bookflight) {
            if (qrPaymentRadio.isSelected()) {
                simulateQrScanAndBook();
            } else {
                processBooking();
            }
        } else if (ae.getSource() == scanQrButton) {
            simulateQrScanAndBook();
        }
    }

    private void fetchPassengerDetails() {
        String aadhar = tfaadhar.getText().trim();
        if (aadhar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Aadhar number.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (Connection conn = new Conn().c;
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM passenger WHERE aadhar = ?")) {
            ps.setString(1, aadhar);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tfname.setText(rs.getString("name"));
                tfnationality.setText(rs.getString("nation"));
                tfaddress.setText(rs.getString("address"));
                labelgender.setText(rs.getString("gender"));
            } else {
                JOptionPane.showMessageDialog(this, "No passenger found with this Aadhar.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
                clearPassengerFields();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchFlightDetails() {
        String src = (String) sourceComboBox.getSelectedItem();
        String dest = (String) destinationComboBox.getSelectedItem();
        String ddate = "";
        if (dcdate.getDate() != null) {
            ddate = new SimpleDateFormat("yyyy-MM-dd").format(dcdate.getDate());
        }
        if (src == null || dest == null || ddate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select source, destination, and travel date.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (Connection conn = new Conn().c;
             PreparedStatement ps = conn.prepareStatement("SELECT flight_no FROM flight WHERE fsource = ? AND destination = ? LIMIT 1")) {
            ps.setString(1, src);
            ps.setString(2, dest);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String flightCode = rs.getString("flight_no");
                labelfcode.setText(flightCode);
                loadOccupiedSeats(flightCode, ddate);
            } else {
                JOptionPane.showMessageDialog(this, "No flights available for this route.", "No Flights", JOptionPane.INFORMATION_MESSAGE);
                labelfcode.setText("");
                occupiedSeats.clear();
                resetSeatGrid();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processBooking() {
        String aadhar = tfaadhar.getText().trim();
        String flightCode = labelfcode.getText().trim();
        String src = (String) sourceComboBox.getSelectedItem();
        String dest = (String) destinationComboBox.getSelectedItem();
        String date = (dcdate.getDate() != null) 
            ? new SimpleDateFormat("yyyy-MM-dd").format(dcdate.getDate()) 
            : "";
        String seat = (selectedSeat != null) ? selectedSeat : "";
        String name = tfname.getText().trim();
        String nationality = tfnationality.getText().trim();

        if (aadhar.isEmpty() || flightCode.isEmpty() || src == null || dest == null || 
            date.isEmpty() || seat.isEmpty() || name.isEmpty() || nationality.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please complete all fields and select a seat.", 
                "Input Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String pnr = "PNR-" + (100000 + new Random().nextInt(900000));
        String ticket = "TICKET-" + (1000 + new Random().nextInt(9000));

        try (Connection conn = new Conn().c;
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO reservation (pnr, ticket_no, aadhar, name, nationality, " +
                 "flight_code, source, destination, travel_date, seat) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            
            ps.setString(1, pnr);
            ps.setString(2, ticket);
            ps.setString(3, aadhar);
            ps.setString(4, name);
            ps.setString(5, nationality);
            ps.setString(6, flightCode);
            ps.setString(7, src);
            ps.setString(8, dest);
            ps.setString(9, date);
            ps.setString(10, seat);
            
            ps.executeUpdate();

            generatePDF(pnr, ticket, name, flightCode, src, dest, date, seat);

            JOptionPane.showMessageDialog(this, 
                "✅ Booking Confirmed!\nPNR: " + pnr + "\nTicket: " + ticket, 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            resetForm();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "❌ Booking failed: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generatePDF(String pnr, String ticket, String name, String flightCode, String src, String dest, String date, String seat) {
        String fare = "N/A";
        try (Connection conn = new Conn().c;
             PreparedStatement ps = conn.prepareStatement("SELECT fare FROM flight WHERE flight_no = ?")) {
            ps.setString(1, flightCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                fare = rs.getString("fare");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save PDF Ticket");
            fileChooser.setSelectedFile(new File("ticket_" + pnr + ".pdf"));
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection != JFileChooser.APPROVE_OPTION) return;

            File fileToSave = fileChooser.getSelectedFile();
            PdfWriter writer = new PdfWriter(fileToSave);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("✈️ Shri Airlines")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(24)
                .setBold()
                .setMarginBottom(20));

            document.add(new Paragraph("E-Ticket & Boarding Pass")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(16)
                .setMarginBottom(30));

            Table table = new Table(2);
            table.setWidth(500);
            table.setMarginLeft(50);
            table.setMarginRight(50);

            addRow(table, "PNR", pnr);
            addRow(table, "Ticket Number", ticket);
            addRow(table, "Passenger Name", name);
            addRow(table, "Flight Code", flightCode);
            addRow(table, "From", src);
            addRow(table, "To", dest);
            addRow(table, "Travel Date", date);
            addRow(table, "Seat", seat);
            addRow(table, "Fare", "₹" + fare);
            addRow(table, "Status", "CONFIRMED");

            document.add(table);

            document.add(new Paragraph("\nThank you for flying with Shri Airlines!\nHave a safe and pleasant journey.")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12)
                .setItalic()
                .setMarginTop(30));

            document.close();

            JOptionPane.showMessageDialog(this, 
                "📄 Ticket saved to:\n" + fileToSave.getAbsolutePath(), 
                "PDF Generated", 
                JOptionPane.INFORMATION_MESSAGE);
            Desktop.getDesktop().open(fileToSave);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Failed to generate PDF: " + e.getMessage(), 
                "PDF Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addRow(Table table, String label, String value) {
        table.addCell(new Paragraph(label).setBold().setFontSize(12));
        table.addCell(new Paragraph(value).setFontSize(12));
    }

    private void simulateQrScanAndBook() {
        String flightCode = labelfcode.getText().trim();
        if (flightCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fetch flight details first.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fare = "0";
        try (Connection conn = new Conn().c;
             PreparedStatement ps = conn.prepareStatement("SELECT fare FROM flight WHERE flight_no = ?")) {
            ps.setString(1, flightCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                fare = rs.getString("fare");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch fare: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String pnr = "PNR-" + (100000 + new Random().nextInt(900000));
        ImageIcon qrIcon = generateQRCode(pnr, flightCode, fare);
        if (qrIcon != null) {
            razorpayQRLabel.setIcon(qrIcon);
        }

        JDialog scanDialog = new JDialog(this, "Simulating QR Scan", true);
        scanDialog.setSize(320, 180);
        scanDialog.setLocationRelativeTo(this);
        scanDialog.setLayout(new BorderLayout());

        JLabel statusLabel = new JLabel("Scanning QR Code...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        scanDialog.add(statusLabel, BorderLayout.NORTH);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        scanDialog.add(progressBar, BorderLayout.CENTER);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> scanDialog.dispose());
        scanDialog.add(cancelButton, BorderLayout.SOUTH);

        scanDialog.setVisible(true);

        javax.swing.Timer scanTimer = new javax.swing.Timer(80, new ActionListener() {
            int progress = 0;
            public void actionPerformed(ActionEvent e) {
                progress += 3;
                progressBar.setValue(progress);
                if (progress >= 100) {
                    ((javax.swing.Timer) e.getSource()).stop();
                    statusLabel.setText("✅ Payment Successful!");
                    javax.swing.Timer closeTimer = new javax.swing.Timer(1200, ev -> {
                        scanDialog.dispose();
                        processBooking();
                    });
                    closeTimer.setRepeats(false);
                    closeTimer.start();
                }
            }
        });
        scanTimer.start();
    }

    private ImageIcon generateQRCode(String pnr, String flightCode, String amount) {
        try {
            qrData = String.format("PNR:%s|Flight:%s|Amount:%s|Date:%s",
                pnr, flightCode, amount, new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
            BarcodeQRCode qrCode = new BarcodeQRCode(qrData);
            java.awt.Image awtImage = qrCode.createAwtImage(Color.BLACK, Color.WHITE);
            int width = awtImage.getWidth(null);
            int height = awtImage.getHeight(null);
            if (width <= 0 || height <= 0) {
                throw new IllegalStateException("Invalid QR code image dimensions");
            }
            BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = qrImage.createGraphics();
            g2d.drawImage(awtImage, 0, 0,100,100, null);
           
            g2d.dispose();
            ByteArrayOutputStream imgBaos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", imgBaos);
            ImageIcon qrIcon = new ImageIcon(imgBaos.toByteArray());
            return qrIcon;
        } catch (Exception e) {
            System.err.println("Error generating QR code: " + e.getMessage());
            return null;
        }
    }

    private void clearPassengerFields() {
        tfname.setText("");
        tfnationality.setText("");
        tfaddress.setText("");
        labelgender.setText("");
    }

    private void resetForm() {
        tfaadhar.setText("");
        clearPassengerFields();
        labelfcode.setText("");
        dcdate.setDate(null);
        cardNumberField.setText("");
        cvvField.setText("");
        expiryMonth.setSelectedIndex(0);
        expiryYear.setSelectedIndex(0);
        selectedSeat = null;
        resetSeatGrid();
        cardPaymentRadio.setSelected(true);
        togglePaymentFields();
    }

    private void animateFadeIn() {
        Timer splashTimer = new Timer(2000, e -> {
            splashPanel.setVisible(false);
            mainContent.setVisible(true);
            mainContent.setAlpha(0.0f);
            Timer fadeInTimer = new Timer(30, ev -> {
                float alpha = mainContent.getAlpha() + 0.03f;
                if (alpha >= 0.95f) {
                    ((Timer) ev.getSource()).stop();
                    alpha = 0.95f;
                }
                mainContent.setAlpha(alpha);
            });
            fadeInTimer.setRepeats(true);
            fadeInTimer.start();
        });
        splashTimer.setRepeats(false);
        splashTimer.start();
    }

    private static class GradientPanel extends JPanel {
        private float alpha = 0.95f;

        public GradientPanel() {
            setOpaque(false);
        }

        public void setAlpha(float alpha) {
            this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
            repaint();
        }

        public float getAlpha() {
            return alpha;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(255, 255, 255, 230),
                0, getHeight(), new Color(200, 220, 255, 230)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g2d);
            g2d.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Failed to set system look and feel: " + e.getMessage());
            }
            new BookFlight();
        });
    }
}