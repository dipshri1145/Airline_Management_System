package airline_management;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Journy_Details extends JFrame implements ActionListener {
    private static final String[] HEADERS = {"ID", "PNR", "Ticket No", "Aadhar", "Name", "Nationality", 
                                             "Flight Code", "Source", "Destination", "Travel Date", 
                                             "Seat", "Booked At", "Arrival Time", "Departure Time"};
    
    JTable table;
    JTextField pnr;
    JButton show, close;
    
    public Journy_Details() {
        setTitle("Journey Details");
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(Color.WHITE);
        
        JLabel lblpnr = new JLabel("PNR Number:");
        lblpnr.setFont(new Font("Tahoma", Font.PLAIN, 16));
        inputPanel.add(lblpnr);
        
        pnr = new JTextField(15);
        inputPanel.add(pnr);
        
        show = new JButton("Show Details");
        show.setBackground(Color.BLACK);
        show.setForeground(Color.WHITE);
        show.addActionListener(this);
        inputPanel.add(show);
        
        close = new JButton("Close");
        close.setBackground(Color.BLACK);
        close.setForeground(Color.WHITE);
        close.addActionListener(this);
        inputPanel.add(close);
        
        add(inputPanel, BorderLayout.NORTH);
        
        table = new JTable();
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setModel(new DefaultTableModel(new Object[][]{}, HEADERS));
        
        // Simple renderer for dates only
        table.setDefaultRenderer(Object.class, new TableCellRenderer() {
            private final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                String columnName = table.getColumnName(column).toLowerCase();
                if (value instanceof Date && (columnName.contains("date") || columnName.contains("booked"))) {
                    value = dateFormat.format((Date) value);
                } else if (value == null) {
                    value = "";
                }
                return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });
        
        JScrollPane jsp = new JScrollPane(table);
        jsp.setBackground(Color.WHITE);
        add(jsp, BorderLayout.CENTER);
        
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == close) {
            dispose();
            return;
        }
        
        String pnrText = pnr.getText().trim();
        if (pnrText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter PNR Number");
            return;
        }
        
        try {
            Conn conn = new Conn();
            PreparedStatement ps = conn.c.prepareStatement("SELECT * FROM reservation WHERE PNR = ?");
            ps.setString(1, pnrText);
            ResultSet rs = ps.executeQuery();
            
            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "No Information Found");
                table.setModel(new DefaultTableModel(new Object[][]{}, HEADERS));
                rs.close();
                ps.close();
                return;
            }
            
            DefaultTableModel model = new DefaultTableModel(new Object[][]{}, HEADERS);
            SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            while (rs.next()) {
                Object[] row = new Object[14];
                for (int i = 1; i <= 12; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                
                Date travelDate = rs.getDate(10);
                Calendar cal = Calendar.getInstance();
                if (travelDate != null) {
                    cal.setTime(travelDate);
                } else {
                    cal.setTime(new Date());
                }
                
                // Departure: 09:00:00
                cal.set(Calendar.HOUR_OF_DAY, 9);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                String departure = dtFormat.format(cal.getTime());
                
                // Arrival: +2 hours
                cal.add(Calendar.HOUR, 2);
                String arrival = dtFormat.format(cal.getTime());
                
                row[12] = arrival;
                row[13] = departure;
                model.addRow(row);
            }
            
            table.setModel(model);
            rs.close();
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            table.setModel(new DefaultTableModel(new Object[][]{}, HEADERS));
        }
    }

    public static void main(String[] args) {
        new Journy_Details();
    }
}