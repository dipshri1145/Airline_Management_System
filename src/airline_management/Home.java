package airline_management;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

public class Home extends JFrame implements ActionListener {

    JMenuBar menubar;
    private String role;
    private String username;
    private JLabel mh; // Animated heading reference

    public Home(String role, String username) {
        this.role = role;
        this.username = username;

        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Shri Airlines - Dashboard");

        // Background image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("airline_management/assets/hm.png"));
        JLabel image = new JLabel(i1);
        image.setLayout(null);
        setContentPane(image); // Set as content pane

        // Welcome Heading with Animation
        mh = new JLabel("");
        mh.setBounds(50, 50, 1200, 60);
        mh.setForeground(Color.WHITE);
        mh.setFont(new Font("Segoe UI", Font.BOLD, 42));
        mh.setHorizontalAlignment(SwingConstants.LEFT);

        // Add text shadow effect
        mh.setBorder(new EmptyBorder(5, 5, 5, 5));
        image.add(mh);

        // Start animation after frame is visible
        SwingUtilities.invokeLater(() -> animateWelcomeText());

        // Menu bar
        menubar = new JMenuBar();
        setJMenuBar(menubar);

        // --- Details Menu ---
        JMenu MENU = new JMenu("Details");
        MENU.setFont(new Font("Arial", Font.BOLD, 16));
        menubar.add(MENU);

        JMenuItem flightDetails = new JMenuItem("Flight Details");
        flightDetails.addActionListener(this);
        styleMenuItem(flightDetails);
        MENU.add(flightDetails);

        JMenuItem customerDetails = new JMenuItem("Add Customer Details");
        customerDetails.addActionListener(this);
        styleMenuItem(customerDetails);
        MENU.add(customerDetails);

        JMenuItem bookFlight = new JMenuItem("Book Flight");
        bookFlight.addActionListener(this);
        styleMenuItem(bookFlight);
        MENU.add(bookFlight);

        JMenuItem journeyDetails = new JMenuItem("Journey Details");
        journeyDetails.addActionListener(this);
        styleMenuItem(journeyDetails);
        MENU.add(journeyDetails);

        JMenuItem ticketCancellation = new JMenuItem("Cancel Ticket");
        ticketCancellation.addActionListener(this);
        styleMenuItem(ticketCancellation);
        MENU.add(ticketCancellation);

        // --- Ticket Menu ---
        JMenu ticket = new JMenu("Ticket");
        ticket.setFont(new Font("Arial", Font.BOLD, 16));
        menubar.add(ticket);

        JMenuItem boardingpass = new JMenuItem("Boarding Pass");
        boardingpass.addActionListener(this);
        styleMenuItem(boardingpass);
        ticket.add(boardingpass);

        // --- Account Menu (NEW) ---
        JMenu account = new JMenu("Account");
        account.setFont(new Font("Arial", Font.BOLD, 16));
        menubar.add(account);

       
        // Frame settings
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);

        // Resize background to fit
        image.setBounds(0, 0, getWidth(), getHeight());
    }

    // Smooth fade-in + slide-in animation for welcome text
    private void animateWelcomeText() {
        String fullText = "✈️ Shri Airlines WELCOMES YOU, " + username + "!";
        StringBuilder currentText = new StringBuilder();

        Timer timer = new Timer(80, null); // 80ms per character
        timer.addActionListener(e -> {
            if (currentText.length() < fullText.length()) {
                currentText.append(fullText.charAt(currentText.length()));
                mh.setText(currentText.toString());
            } else {
                ((Timer)e.getSource()).stop();
                // Optional: add glow effect after animation
                addGlowEffect();
            }
        });
        timer.start();
    }

    // Adds subtle outer glow using multiple labels (simulated)
    private void addGlowEffect() {
        mh.setForeground(new Color(255, 255, 255));
        // For true glow, consider using compound borders or custom painting
    }

    // Style menu items with hover effect
    private void styleMenuItem(JMenuItem item) {
        item.setFont(new Font("Tahoma", Font.PLAIN, 14));
        item.setOpaque(true);
        item.setBackground(Color.decode("#f0f0f0"));
        item.setBorderPainted(false);

        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                item.setBackground(Color.decode("#cce7ff"));
            }
            public void mouseExited(MouseEvent e) {
                item.setBackground(Color.decode("#f0f0f0"));
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String text = ae.getActionCommand();
        if (text == null) return;

        switch (text) {
            case "Add Customer Details":
                new AddCustomer();
                break;
            case "Flight Details":
                new FlightDetails(username, role);
                break;
            case "Book Flight":
                new BookFlight();
                break;
            case "Cancel Ticket":
                new CancelBooking();
                break;
            case "Boarding Pass":
                new BoardingPass();
                break;
            case "Account":
                new AccountPage(username);
                break;
            case "Journey Details":
                new Journy_Details();
                break;
            default:
                JOptionPane.showMessageDialog(this, "Feature coming soon: " + text);
                break;
        }
    }

    public Home() {
        this("Passenger", "Guest");
    }

    public static void main(String args[]) {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        SwingUtilities.invokeLater(() -> {
            Home h = new Home("Admin", "defaultUser");
            System.out.println("Window Size: " + h.getSize());
        });
    }
}