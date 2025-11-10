package airline_management;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;

public class AccountPage extends JFrame implements ActionListener {

    JTextField tfFullname, tfNation, tfAge, tfAadhar, tfEmail, tfPhone, tfAddress, tfUsername;
    JPasswordField tfPassword;
    JComboBox<String> cbGender;
    JButton btnUpdate, btnDone;
    JPanel formPanel;
    JScrollPane scrollPane;
    String username; // Logged-in username

    public AccountPage(String username) {
        this.username = username;

        setTitle("Shri Airlines - Account Details");
        setSize(800, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("🛫 My Account", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setBackground(new Color(25, 118, 210));
        header.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(header, BorderLayout.NORTH);

        // Form Panel
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245));
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;
        int row = 0;

        // Personal Info Label
        JLabel lblPersonal = new JLabel("Personal Information");
        lblPersonal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPersonal.setForeground(new Color(25, 118, 210));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        formPanel.add(lblPersonal, gbc);
        row++;
        gbc.gridwidth = 1;

        // Username
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        tfUsername = new JTextField(25);
        tfUsername.setEditable(false);
        formPanel.add(tfUsername, gbc);
        row++;

        // Full Name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        tfFullname = new JTextField(25);
        tfFullname.setEditable(false);
        formPanel.add(tfFullname, gbc);
        row++;

        // Nationality
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Nationality:"), gbc);
        gbc.gridx = 1;
        tfNation = new JTextField(25);
        tfNation.setEditable(false);
        formPanel.add(tfNation, gbc);
        row++;

        // Age
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1;
        tfAge = new JTextField(5);
        tfAge.setEditable(false);
        formPanel.add(tfAge, gbc);
        row++;

        // Contact Info Label
        JLabel lblContact = new JLabel("Contact Information");
        lblContact.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblContact.setForeground(new Color(25, 118, 210));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        formPanel.add(lblContact, gbc);
        row++;
        gbc.gridwidth = 1;

        // Aadhaar
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Aadhaar:"), gbc);
        gbc.gridx = 1;
        tfAadhar = new JTextField(25);
        tfAadhar.setEditable(false);
        formPanel.add(tfAadhar, gbc);
        row++;

        // Email
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        tfEmail = new JTextField(25);
        tfEmail.setEditable(false);
        formPanel.add(tfEmail, gbc);
        row++;

        // Phone
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        tfPhone = new JTextField(25);
        tfPhone.setEditable(false);
        formPanel.add(tfPhone, gbc);
        row++;

        // Address
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        tfAddress = new JTextField(25);
        tfAddress.setEditable(false);
        formPanel.add(tfAddress, gbc);
        row++;

        // Gender
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        cbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        cbGender.setEnabled(false);
        formPanel.add(cbGender, gbc);
        row++;

        // Password
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        tfPassword = new JPasswordField(25);
        tfPassword.setEditable(false);
        formPanel.add(tfPassword, gbc);
        row++;

        // Scroll Pane
        scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(245, 245, 245));
        btnUpdate = new JButton("Update Details");
        btnDone = new JButton("Done");
        btnDone.setEnabled(false);

        btnUpdate.setBackground(new Color(25, 118, 210));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);

        btnDone.setBackground(new Color(76, 175, 80));
        btnDone.setForeground(Color.WHITE);
        btnDone.setFocusPainted(false);

        btnUpdate.addActionListener(this);
        btnDone.addActionListener(this);

        btnPanel.add(btnUpdate);
        btnPanel.add(btnDone);
        add(btnPanel, BorderLayout.SOUTH);

        // Load user info from database
        loadUserDetails();

        setVisible(true);
    }

    private void loadUserDetails() {
        try {
            Conn conn = new Conn();
            PreparedStatement ps = conn.c.prepareStatement("SELECT * FROM login WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                tfUsername.setText(rs.getString("username"));
                tfFullname.setText(rs.getString("full_name"));
                tfNation.setText(rs.getString("nationality"));
                tfAge.setText(String.valueOf(rs.getInt("age")));
                tfAadhar.setText(rs.getString("aadhar_number"));
                tfEmail.setText(rs.getString("email"));
                tfPhone.setText(rs.getString("phone"));
                tfAddress.setText(rs.getString("address"));
                cbGender.setSelectedItem(rs.getString("gender"));
                tfPassword.setText("");
            }

            rs.close();
            ps.close();
            conn.c.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading details: " + e.getMessage());
        }
    }

    private void setFieldsEditable(boolean flag) {
        tfFullname.setEditable(flag);
        tfNation.setEditable(flag);
        tfAge.setEditable(flag);
        tfAadhar.setEditable(flag);
        tfEmail.setEditable(flag);
        tfPhone.setEditable(flag);
        tfAddress.setEditable(flag);
        cbGender.setEnabled(flag);
        tfPassword.setEditable(flag);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnUpdate) {
            setFieldsEditable(true);
            btnUpdate.setEnabled(false);
            btnDone.setEnabled(true);

        } else if (ae.getSource() == btnDone) {
            String pwd = new String(tfPassword.getPassword()).trim();
            if (pwd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter your password to update.");
                return;
            }

            try {
                Conn conn = new Conn();
                String hashedPassword = hashPassword(pwd);

                String sql = "UPDATE login SET full_name=?, nationality=?, age=?, aadhar_number=?, email=?, phone=?, address=?, gender=?, password_hash=? WHERE username=?";
                PreparedStatement ps = conn.c.prepareStatement(sql);
                ps.setString(1, tfFullname.getText().trim());
                ps.setString(2, tfNation.getText().trim());
                ps.setInt(3, Integer.parseInt(tfAge.getText().trim()));
                ps.setString(4, tfAadhar.getText().trim());
                ps.setString(5, tfEmail.getText().trim());
                ps.setString(6, tfPhone.getText().trim());
                ps.setString(7, tfAddress.getText().trim());
                ps.setString(8, (String) cbGender.getSelectedItem());
                ps.setString(9, hashedPassword);
                ps.setString(10, username); // ✅ Fixed here

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Account updated successfully!");
                    setFieldsEditable(false);
                    btnUpdate.setEnabled(true);
                    btnDone.setEnabled(false);
                    tfPassword.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed!");
                }

                ps.close();
                conn.c.close();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Age must be a number!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating: " + e.getMessage());
            }
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AccountPage("john_doe")); // Example username
    }
}
