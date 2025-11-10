package airline_management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;

public class CancelBooking extends JFrame implements ActionListener {
    
    JTextField tfpnr;
    JLabel tfname, cancellationno, lblfcode, lbldateoftravel;
    JButton fetchButton, flight;
    
    public CancelBooking() {
        // Set window properties
        setTitle("Airline Management - Cancel Booking");
        setSize(900, 500);
        setLocationRelativeTo(null);  // Center on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Custom background panel with gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(173, 216, 230), 0, getHeight(), new Color(255, 255, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);
        setContentPane(mainPanel);
        
        Random random = new Random();
        
        // Heading with improved font and color
        JLabel heading = new JLabel("CANCELLATION PORTAL", SwingConstants.CENTER);
        heading.setBounds(250, 20, 400, 40);
        heading.setFont(new Font("Tahoma", Font.BOLD, 30));
        heading.setForeground(new Color(25, 25, 112));  // Dark blue
        add(heading);
        
        // Image with border
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("airline_management/assets/cancel.png"));
        if (i1.getImage() == null) {
            System.err.println("Warning: cancel.PNG not found. Using placeholder.");
            i1 = new ImageIcon();  // Fallback to empty icon
        }
        Image i2 = i1.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(650, 80, 200, 200);
        image.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230), 2));
        add(image);
        
        // Form panel for grouping fields
        JPanel formPanel = new JPanel(null);
        formPanel.setBounds(50, 80, 550, 300);
        formPanel.setBackground(new Color(248, 248, 255));  // Ghost white
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Booking Details", 2, 2, new Font("Tahoma", Font.BOLD, 14)));
        add(formPanel);
        
        // PNR Label and Field
        JLabel lblpnr = new JLabel("PNR Number:");
        lblpnr.setBounds(20, 40, 120, 25);
        lblpnr.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblpnr.setForeground(Color.DARK_GRAY);
        formPanel.add(lblpnr);
        
        tfpnr = new JTextField();
        tfpnr.setBounds(150, 40, 150, 25);
        tfpnr.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tfpnr.setBorder(BorderFactory.createEtchedBorder());
        formPanel.add(tfpnr);
        
        // Fetch Button with improved style
        fetchButton = new JButton("Show Details");
        fetchButton.setBounds(320, 40, 120, 30);
        fetchButton.setBackground(new Color(0, 0, 139));  // Dark blue
        fetchButton.setForeground(Color.WHITE);
        fetchButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        fetchButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLUE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fetchButton.addActionListener(this);
        fetchButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { fetchButton.setBackground(new Color(0, 0, 205)); }
            public void mouseExited(MouseEvent e) { fetchButton.setBackground(new Color(0, 0, 139)); }
        });
        formPanel.add(fetchButton);
        
        // Name Label and Output
        JLabel lblname = new JLabel("Passenger Name:");
        lblname.setBounds(20, 80, 120, 25);
        lblname.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblname.setForeground(Color.DARK_GRAY);
        formPanel.add(lblname);
        
        tfname = new JLabel("Not fetched yet");
        tfname.setBounds(150, 80, 300, 25);
        tfname.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tfname.setForeground(Color.BLUE);
        formPanel.add(tfname);
        
        // Cancellation No
        JLabel lblcancelno = new JLabel("Cancellation No:");
        lblcancelno.setBounds(20, 120, 120, 25);
        lblcancelno.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblcancelno.setForeground(Color.DARK_GRAY);
        formPanel.add(lblcancelno);
        
        cancellationno = new JLabel("CN" + String.format("%06d", random.nextInt(1000000)));
        cancellationno.setBounds(150, 120, 200, 25);
        cancellationno.setFont(new Font("Tahoma", Font.PLAIN, 13));
        cancellationno.setForeground(new Color(139, 0, 0));  // Dark red
        formPanel.add(cancellationno);
        
        // Flight Code
        JLabel lblfcodeLabel = new JLabel("Flight Code:");
        lblfcodeLabel.setBounds(20, 160, 120, 25);
        lblfcodeLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblfcodeLabel.setForeground(Color.DARK_GRAY);
        formPanel.add(lblfcodeLabel);
        
        lblfcode = new JLabel("Not fetched yet");
        lblfcode.setBounds(150, 160, 200, 25);
        lblfcode.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblfcode.setForeground(Color.BLUE);
        formPanel.add(lblfcode);
        
        // Date
        JLabel lbldateLabel = new JLabel("Travel Date:");
        lbldateLabel.setBounds(20, 200, 120, 25);
        lbldateLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        lbldateLabel.setForeground(Color.DARK_GRAY);
        formPanel.add(lbldateLabel);
        
        lbldateoftravel = new JLabel("Not fetched yet");
        lbldateoftravel.setBounds(150, 200, 200, 25);
        lbldateoftravel.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lbldateoftravel.setForeground(Color.BLUE);
        formPanel.add(lbldateoftravel);
        
        // Cancel Button with improved style and hover
        flight = new JButton("Cancel Booking");
        flight.setBounds(200, 250, 150, 35);
        flight.setBackground(new Color(220, 20, 60));  // Crimson
        flight.setForeground(Color.WHITE);
        flight.setFont(new Font("Tahoma", Font.BOLD, 13));
        flight.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.RED, 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        flight.addActionListener(this);
        flight.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { flight.setBackground(new Color(139, 0, 0)); }
            public void mouseExited(MouseEvent e) { flight.setBackground(new Color(220, 20, 60)); }
        });
        formPanel.add(flight);
        
        setVisible(true);
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
                // TODO: Use PreparedStatement for security: PreparedStatement ps = conn.c.prepareStatement("select * from reservation where PNR = ?");
                // ps.setString(1, pnr); ResultSet rs = ps.executeQuery();
                String query = "select * from reservation where PNR = '" + pnr + "'";
                ResultSet rs = conn.s.executeQuery(query);
                
                if (rs.next()) {
                    tfname.setText(rs.getString("name"));
                    lblfcode.setText(rs.getString("flight_code"));
                    lbldateoftravel.setText(rs.getString("travel_date"));
                } else {
                    JOptionPane.showMessageDialog(this, "PNR not found. Please enter a valid PNR.", "Not Found", JOptionPane.ERROR_MESSAGE);
                    clearFields();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else if (ae.getSource() == flight) {
            String name = tfname.getText().trim();
            String pnr = tfpnr.getText().trim();
            String cancelno = cancellationno.getText().trim();
            String fcode = lblfcode.getText().trim();
            String date = lbldateoftravel.getText().trim();
            
            // Validation
            if (pnr.isEmpty() || name.equals("Not fetched yet")) {
                JOptionPane.showMessageDialog(this, "Please fetch details first by entering a valid PNR.", "Incomplete Data", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Confirmation
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Confirm cancellation for PNR: " + pnr + "\nPassenger: " + name + "\nThis will remove the booking from reservations.", 
                "Confirm Cancel", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            
            try {
                Conn conn = new Conn();
                // TODO: Use PreparedStatement for security
                String insertQuery = "insert into cancel values('" + pnr + "', '" + name + "', '" + cancelno + "', '" + fcode + "', '" + date + "')";
                String deleteQuery = "delete from reservation where PNR = '" + pnr + "'";
                
                conn.s.executeUpdate(insertQuery);
                int deletedRows = conn.s.executeUpdate(deleteQuery);
                
                if (deletedRows > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Booking cancelled successfully!\nCancellation No: " + cancelno + 
                        "\nDetails moved to cancel records.\nReservation removed.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                    // Uncomment if you have a Dashboard class: new Dashboard().setVisible(true); dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "No reservation found to cancel.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Cancellation failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    // Helper method to clear fields after fetch failure or cancel
    private void clearFields() {
        tfname.setText("Not fetched yet");
        lblfcode.setText("Not fetched yet");
        lbldateoftravel.setText("Not fetched yet");
        tfpnr.setText("");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CancelBooking());
    }
}