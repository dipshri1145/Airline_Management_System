package airline_management;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationForm extends JFrame implements ActionListener {

    private JTextField txtUsername, txtFullName, txtEmail, txtContact;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnRegister, btnCancel;

    public RegistrationForm() {
        setTitle("Shri Airlines - Registration");
        setSize(550, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel header = new JPanel();
        header.setBackground(new Color(70, 130, 180));
        header.setPreferredSize(new Dimension(0, 80));
        JLabel lblHeader = new JLabel("Registration Form");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblHeader.setForeground(Color.WHITE);
        header.add(lblHeader);
        add(header, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Full Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        txtFullName = new JTextField(20);
        formPanel.add(txtFullName, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Username *:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(20);
        formPanel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Password *:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(20);
        formPanel.add(txtPassword, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        formPanel.add(txtEmail, gbc);

        // Contact
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Contact No:"), gbc);
        gbc.gridx = 1;
        txtContact = new JTextField(20);
        formPanel.add(txtContact, gbc);

        // Role Dropdown
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        String[] roles = {"User", "Admin", "Admin Staff"};
        cmbRole = new JComboBox<>(roles);
        formPanel.add(cmbRole, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegister.setBackground(new Color(46, 139, 87));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.addActionListener(this);

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 14));
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnRegister);
        buttonPanel.add(btnCancel);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

  @Override
public void actionPerformed(ActionEvent e) {
    String fullName = txtFullName.getText().trim();
    String username = txtUsername.getText().trim();
    String password = new String(txtPassword.getPassword()).trim();
    String email = txtEmail.getText().trim();
    String contact = txtContact.getText().trim();
    String role = cmbRole.getSelectedItem().toString();

    // ===== VALIDATION =====
    if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Full Name, Username and Password are required!");
        return;
    }

    if (username.length() < 4) {
        JOptionPane.showMessageDialog(this, "Username must be at least 4 characters long!");
        return;
    }

    if (!isValidPassword(password)) {
        JOptionPane.showMessageDialog(this,
                "Password must be at least 6 chars, contain one number and one special character!");
        return;
    }

    if (!email.isEmpty() && !isValidEmail(email)) {
        JOptionPane.showMessageDialog(this, "Please enter a valid email address!");
        return;
    }

    if (!contact.isEmpty() && !contact.matches("\\d{10}")) {
        JOptionPane.showMessageDialog(this, "Contact number must be 10 digits!");
        return;
    }

    // ===== HASH PASSWORD =====
    String hashedPassword = hashPassword(password);

    try {
        Conn con = new Conn();
        Connection conn = con.getConnection();
        String sql = "INSERT INTO login (username, password, role, fullname, email, contact) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, username);
        pst.setString(2, hashedPassword);
        pst.setString(3, role);
        pst.setString(4, fullName);
        pst.setString(5, email);
        pst.setString(6, contact);

        int rows = pst.executeUpdate();
        if (rows > 0) {
            JOptionPane.showMessageDialog(this, "Registration Successful! Welcome Shri Airlines ✈️");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration Failed.");
        }

        pst.close();
        conn.close();
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
    }
}

// Utility method for hashing
private String hashPassword(String password) {
    try {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
    }
}

private boolean isValidPassword(String password) {
    return password.length() >= 6 &&
           password.matches(".*\\d.*") &&
           password.matches(".*[!@#$%^&*].*");
}

private boolean isValidEmail(String email) {
    return email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$");
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistrationForm());
    }
}

