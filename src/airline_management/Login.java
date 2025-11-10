package airline_management;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;

public class Login  extends JFrame implements ActionListener {
    JButton Jbr,Jbc,Jbs;
    private JPasswordField tpassword;
    private JTextField tusername;
    public Login(){
        
         setLayout(null);
         
        ImageIcon img=new ImageIcon(ClassLoader.getSystemResource("airline_management/assets/homebg1.png"));
        JLabel jimg=new JLabel(img);
        getContentPane().add(jimg,BorderLayout.CENTER);
        jimg.setBounds(0,0,400,250);
        
       
        JLabel lusername=new JLabel("Username : ");
        lusername.setBounds(60,20,100,20);
        jimg.add(lusername);
        lusername.setForeground(Color.white);
        
        tusername=new JTextField();
        tusername.setBounds(130,20,150,20);
        tusername.setFont(new Font("Tahoma",Font.BOLD,12));
        jimg.add(tusername);
        tusername.setBackground(Color.white);
        tusername.setForeground(Color.black);

        
        
        JLabel lpassword=new JLabel("Password : ");
        lpassword.setBounds(60,60,100,20);
        jimg.add(lpassword);
        lpassword.setForeground(Color.white);
        
        tpassword=new JPasswordField("",15);
        tpassword.setBounds(130,60,150,20);
        jimg.add(tpassword);
        tpassword.setEchoChar('*');
       tpassword.setBackground(Color.white);
        tpassword.setForeground(Color.black);
       
        Color sb=new Color(70,130,180);
        Jbr=new JButton("Reset");
        Jbr.setBounds(40,120,120,20);
        jimg.add(Jbr);
        Jbr.setBackground(sb);

        
        Jbs=new JButton("Submit");
        Jbs.setBounds(190,120,120,20);
        jimg.add(Jbs);
        Jbs.setBackground(sb);

        
        Jbc=new JButton("Close");
        Jbc.setBounds(120,160,120,20);
        jimg.add(Jbc);
        Jbr.addActionListener(this);
        Jbc.setBackground(sb);
        Jbs.addActionListener(this);
        Jbc.addActionListener(this);
        
        
        JLabel text=new JLabel("New to ShriAirlines?",SwingConstants.CENTER);
         text.setBounds(100 ,190,200,30);
        jimg.add(text);
        text.setForeground(Color.white);
        text.setCursor(new Cursor (Cursor.HAND_CURSOR));
        text.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent m){
                    new RegistrationForm();}
        });
        
         
        setTitle("Login");
        setResizable(false);
        setSize(400,250);
        setLocation(500,250);
        setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent AE){
       
    if(AE.getSource()==Jbr)
    {
    tusername.setText("");
    tpassword.setText("");
    }
    else if (AE.getSource() == Jbs) {
    String username = tusername.getText().trim();
    String password = new String(tpassword.getPassword()).trim();

    if (username.isEmpty() || password.isEmpty()) { 
        JOptionPane.showMessageDialog(this, "Please enter username and password!"); 
        return; 
    }

    try {
        Conn conn = new Conn();
        Connection con = conn.getConnection();
        String sql = "SELECT password_hash, role FROM login WHERE username=?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, username);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            String storedHash = rs.getString("password_hash");
            String role = rs.getString("role");
            
            String enteredHash = hashPassword(password); // Hash entered password

            if (enteredHash.equals(storedHash)) {
                JOptionPane.showMessageDialog(this, "Login Successful! Role: " + role);
                new Home(username, role).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Password!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username!");
        }

        rs.close();
        pst.close();
        con.close();

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
    }
}
    else if(AE.getSource()==Jbc){
    setVisible(false);}
    }
    private String hashPassword(String password) {
    try {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

    public static void main(String args[]){
    new Login();
}
}


