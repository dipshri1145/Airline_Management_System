package airline_management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.sql.*;
import java.net.URL;
import java.util.Random;

public class BoardingPass extends JFrame implements ActionListener, Printable {
    
    JTextField tfpnr;
    JButton fetchButton, printButton;
    private JPanel ticketPanel;
    private JLabel ticketName, ticketNationality, ticketPnr, ticketSrc, ticketDest, ticketFname, ticketFcode, ticketDate;
    
    public BoardingPass() {
        setTitle("Shri Airlines - Boarding Pass");
        setSize(1200, 700);
        setLocationRelativeTo(null);  // Center on screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(135, 206, 235), 0, getHeight(), new Color(255, 255, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                // Subtle airplane silhouette (generated)
                drawAirplaneSilhouette(g2d);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);
        
        // Top input panel
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        
        // Centered ticket panel
        ticketPanel = createTicketPanel();
        mainPanel.add(ticketPanel, BorderLayout.CENTER);
        
        // Load logo (robust) - FIXED: Add to content pane, not layout
        loadLogo();
        
        setVisible(true);
    }
    
    // Top input panel
    private JPanel createInputPanel() {
        JPanel input = new JPanel(new FlowLayout(FlowLayout.CENTER));
        input.setBackground(new Color(0, 0, 0, 0));  // Transparent
        input.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JLabel lblpnr = new JLabel("Enter PNR: ");
        lblpnr.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblpnr.setForeground(new Color(25, 25, 112));
        
        tfpnr = new JTextField(15);
        tfpnr.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tfpnr.setBorder(BorderFactory.createEtchedBorder());
        
        fetchButton = new JButton("Generate Boarding Pass");
        fetchButton.setBackground(new Color(0, 0, 139));
        fetchButton.setForeground(Color.WHITE);
        fetchButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        fetchButton.addActionListener(this);
        styleButton(fetchButton);
        
        input.add(lblpnr);
        input.add(tfpnr);
        input.add(fetchButton);
        
        return input;
    }
    
    // Realistic ticket panel
    private JPanel createTicketPanel() {
        JPanel ticket = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                // Ticket background
                g2d.setColor(new Color(248, 249, 250));  // Light gray
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                // Dashed border (perforated look)
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{8, 4}, 0));
                g2d.drawRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);
                // Barcode at bottom
                drawBarcode(g2d);
            }
        };
        ticket.setPreferredSize(new Dimension(800, 500));
        ticket.setLayout(null);
        ticket.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        ticket.setOpaque(false);
        
        // Ticket header
        JLabel header = new JLabel("BOARDING PASS", SwingConstants.CENTER);
        header.setBounds(0, 20, 740, 40);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setForeground(new Color(25, 25, 112));
        ticket.add(header);
        
        // Passenger section
        JLabel passHeader = new JLabel("PASSENGER INFORMATION", SwingConstants.LEFT);
        passHeader.setBounds(20, 80, 300, 25);
        passHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        passHeader.setForeground(Color.DARK_GRAY);
        ticket.add(passHeader);
        
        ticketName = new JLabel("Name: Not generated");
        ticketName.setBounds(20, 110, 400, 25);
        ticketName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ticket.add(ticketName);
        
        ticketPnr = new JLabel("PNR: Not generated");
        ticketPnr.setBounds(20, 140, 200, 25);
        ticketPnr.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ticket.add(ticketPnr);
        
        ticketNationality = new JLabel("Nationality: Not generated");
        ticketNationality.setBounds(20, 170, 200, 25);
        ticketNationality.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ticket.add(ticketNationality);
        
        // Flight section
        JLabel flightHeader = new JLabel("FLIGHT INFORMATION", SwingConstants.LEFT);
        flightHeader.setBounds(20, 210, 300, 25);
        flightHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        flightHeader.setForeground(Color.DARK_GRAY);
        ticket.add(flightHeader);
        
        ticketSrc = new JLabel("From: Not generated");
        ticketSrc.setBounds(20, 240, 150, 25);
        ticketSrc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ticket.add(ticketSrc);
        
        ticketDest = new JLabel("To: Not generated");
        ticketDest.setBounds(200, 240, 150, 25);
        ticketDest.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ticket.add(ticketDest);
        
        ticketFname = new JLabel("Flight: Not generated");
        ticketFname.setBounds(20, 270, 200, 25);
        ticketFname.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ticket.add(ticketFname);
        
        ticketFcode = new JLabel("Code: Not generated");
        ticketFcode.setBounds(250, 270, 150, 25);
        ticketFcode.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ticket.add(ticketFcode);
        
        ticketDate = new JLabel("Date: Not generated");
        ticketDate.setBounds(20, 300, 200, 25);
        ticketDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ticket.add(ticketDate);
        
        // Placeholders
        JLabel seat = new JLabel("Seat: 12A | Gate: B5 | Boarding: 30 min before departure");
        seat.setBounds(20, 330, 500, 25);
        seat.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        seat.setForeground(Color.GRAY);
        ticket.add(seat);
        
        // Print button
        printButton = new JButton("Print Ticket");
        printButton.setBounds(300, 400, 140, 35);
        printButton.setBackground(new Color(0, 128, 0));
        printButton.setForeground(Color.WHITE);
        printButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        printButton.addActionListener(this);
        styleButton(printButton);
        ticket.add(printButton);
        
        // Initial fade-in - FIXED: Simple color transition instead of setAlpha
        fadeInComponent(ticket);
        
        return ticket;
    }
    
    // Draw simple airplane silhouette
    private void drawAirplaneSilhouette(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 50));  // Semi-transparent black
        // Simple wing/body shape
        int[] xPoints = {100, 300, 500, 700, 900};
        int[] yPoints = {300, 250, 200, 250, 300};
        g2d.fillPolygon(xPoints, yPoints, 5);
    }
    
    // Draw barcode
    private void drawBarcode(Graphics2D g2d) {
        Random rand = new Random();
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < 50; i++) {
            if (rand.nextBoolean()) {
                g2d.fillRect(100 + i * 12, 450, 8, 30);
            }
        }
        // PNR under barcode (use current tfpnr value)
        String pnrText = tfpnr.getText().trim().isEmpty() ? "||| PNR |||" : "|||" + tfpnr.getText() + "|||";
        g2d.drawString(pnrText, 100, 500);
    }
    
    // Robust logo loading - FIXED: Add to content pane, not layout manager
    private void loadLogo() {
        // Create header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(0, 0, 0, 0));  // Transparent
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        headerPanel.setOpaque(false);
        
        URL logoUrl = ClassLoader.getSystemResource("airlinemanagementsystem/icons/logo2.png");
        ImageIcon logoIcon;
        if (logoUrl != null) {
            logoIcon = new ImageIcon(logoUrl);
            System.out.println("Logo loaded successfully.");
        } else {
            System.err.println("Warning: logo2.png not found. Using generated logo.");
            logoIcon = createLogoPlaceholder();
        }
        Image scaledLogo = logoIcon.getImage().getScaledInstance(120, 60, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        headerPanel.add(logoLabel);
        
        // FIXED: Add to content pane (container), not to layout manager
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        revalidate();  // Refresh layout
    }
    
    // Generated logo placeholder
    private ImageIcon createLogoPlaceholder() {
        BufferedImage img = new BufferedImage(120, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(25, 25, 112));
        g2d.fillRoundRect(0, 0, 120, 60, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2d.drawString("SHRI", 20, 35);
        g2d.drawString("AIR", 20, 55);
        g2d.dispose();
        return new ImageIcon(img);
    }
    
    // Fade-in animation - FIXED: Use background color transition (no setAlpha)
    private void fadeInComponent(JComponent comp) {
        Color startColor = new Color(255, 255, 255, 200);  // Semi-transparent white start
        Color endColor = Color.WHITE;  // Full white end
        Timer timer = new Timer(50, e -> {
            // Simple color interpolation (fade from light to normal)
            int alphaStep = 10;
            Color currentBg = comp.getBackground();
            if (currentBg.getAlpha() > 255) {
                ((Timer) e.getSource()).stop();
                comp.setBackground(endColor);
            } else {
                Color nextColor = new Color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), currentBg.getAlpha() + alphaStep);
                comp.setBackground(nextColor);
                repaint();
            }
        });
        comp.setBackground(startColor);
        timer.start();
    }
    
    // Button styling
    private void styleButton(JButton btn) {
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(btn == fetchButton ? new Color(0, 0, 205) : new Color(0, 139, 0));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(btn == fetchButton ? new Color(0, 0, 139) : new Color(0, 128, 0));
            }
        });
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == fetchButton) {
            String pnr = tfpnr.getText().trim();
            if (pnr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a PNR number.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                Conn conn = new Conn();
                String query = "select * from reservation where PNR = '" + pnr + "'";
                ResultSet rs = conn.s.executeQuery(query);
                
                if (rs.next()) {
                    ticketName.setText("Name: " + rs.getString("name"));
                    ticketNationality.setText("Nationality: " + rs.getString("nationality"));
                    ticketPnr.setText("PNR: " + rs.getString("PNR"));
                    ticketSrc.setText("From: " + rs.getString("src"));
                    ticketDest.setText("To: " + rs.getString("des"));
                    ticketFname.setText("Flight: " + rs.getString("flightname"));
                    ticketFcode.setText("Code: " + rs.getString("flightcode"));
                    ticketDate.setText("Date: " + rs.getString("ddate"));
                    
                    // Refresh barcode with PNR
                    ticketPanel.repaint();
                    JOptionPane.showMessageDialog(this, "Boarding pass generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "PNR not found. Please enter a valid PNR.", "Not Found", JOptionPane.ERROR_MESSAGE);
                    clearTicket();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else if (ae.getSource() == printButton) {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(this);
            if (job.printDialog()) {
                try {
                    job.print();
                    JOptionPane.showMessageDialog(this, "Boarding pass printed!");
                } catch (PrinterException pe) {
                    JOptionPane.showMessageDialog(this, "Print error: " + pe.getMessage());
                }
            }
        }
    }
    
    // Clear ticket fields
    private void clearTicket() {
        ticketName.setText("Name: Not generated");
        ticketNationality.setText("Nationality: Not generated");
        ticketPnr.setText("PNR: Not generated");
        ticketSrc.setText("From: Not generated");
        ticketDest.setText("To: Not generated");
        ticketFname.setText("Flight: Not generated");
        ticketFcode.setText("Code: Not generated");
        ticketDate.setText("Date: Not generated");
        tfpnr.setText("");
        ticketPanel.repaint();
    }
    
    // Printable implementation
    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) {
        if (pageIndex > 0) return NO_SUCH_PAGE;
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        // Draw ticket content
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
        g2d.drawString("BOARDING PASS", 100, 100);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2d.drawString("Name: " + ticketName.getText(), 100, 150);
        g2d.drawString("PNR: " + ticketPnr.getText(), 100, 170);
        // Add more fields...
        return PAGE_EXISTS;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BoardingPass());
    }
}