package airline_management;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.Border;


public class AddCustomer extends JFrame implements ActionListener {
   JButton save,clear;
   JRadioButton mrb,mrf,other;
   private static int counter=1;
   JTextField tname,tage,taadhar,tnation,tphone,tmail,taddress;
    public static String generateID(){
        return "cust-"+String.format("%04d",counter++);}
    public AddCustomer(){
        //setLayout(null);
        JPanel cp=new JPanel();
        Color sb=new Color(70,130,180);
        cp.setLayout(null);
        cp.setBounds(100, 100, 600, 100);
        cp.setBackground(sb);
        this.getContentPane().add(cp);
        Border bl = BorderFactory.createLineBorder(Color.black,2,true);

        
        JLabel heading =new JLabel("<html><u >ADD PASSENGER</u></html>");
        heading.setBounds(150,10,500,35);
        heading.setFont(new Font("Tahoma",Font.PLAIN,30));
        heading.setForeground(Color.white);
        cp.add(heading);
        
        //fit inpute param
        JLabel name=new JLabel("Name          :");
        name.setBounds(40,70,150,20);
        name.setForeground(Color.white);
        name.setFont(new Font("Tahoma",Font.PLAIN,20));
        cp.add(name);
        
        tname=new JTextField("",30);
        tname.setBounds(180,70,170,25);
        tname.setToolTipText("Enter Full Name");
        tname.setFont(new Font("Tahoma",Font.BOLD,12));
        tname.setBackground(Color.LIGHT_GRAY);
        cp.add(tname);
        tname.setBorder(bl);
        
        JLabel nationality=new JLabel("Nationality   : ");
        nationality.setBounds(40,110,150,20);
        nationality.setFont(new Font("Tahoma",Font.PLAIN,20));
        nationality.setForeground(Color.white);
        cp.add(nationality);
        
        tnation=new JTextField("",25);
        tnation.setBounds(180,110,170,25);
        tnation.setFont(new Font("Tahoma",Font.BOLD,12));
        tnation.setToolTipText("Country Name");
        tnation.setBackground(Color.LIGHT_GRAY);
        tnation.setBorder(bl);
        cp.add(tnation);
        
        JLabel age=new JLabel("Age             : ");
        age.setBounds(40,150,150,25);
        age.setForeground(Color.white);
        age.setFont(new Font("Tahoma",Font.PLAIN,20));
        cp.add(age);
        
        tage=new JTextField("",20);
        tage.setToolTipText("Enter Age");
        tage.setBackground(Color.LIGHT_GRAY);
        tage.setFont(new Font("Tahoma",Font.BOLD,12));
        tage.setBounds(180,150,170,25);
        tage.setBorder(bl);
        cp.add(tage);
        
         JLabel laadhar=new JLabel("Aadhar        :");
        laadhar.setBounds(40,190,150,25);
        laadhar.setForeground(Color.white);
        laadhar.setFont(new Font("Tahoma",Font.PLAIN,20));
        cp.add(laadhar);
        
        taadhar=new JTextField("",20);
        taadhar.setToolTipText("Aadhar Number");
        taadhar.setFont(new Font("Tahoma",Font.BOLD,12));
        taadhar.setBounds(180,190,170,25);
        taadhar.setBackground(Color.LIGHT_GRAY);
        taadhar.setBorder(bl);
        cp.add(taadhar);
        
        JLabel address=new JLabel("Address       : ");
        address.setBounds(40,230,150,25);
        address.setForeground(Color.white);
        address.setFont(new Font("Tahoma",Font.PLAIN,20));
        cp.add(address);
        
        taddress=new JTextField("",20);
        taddress.setToolTipText("Enter Address");
        taddress.setFont(new Font("Tahoma",Font.BOLD,12));
        taddress.setBounds(180,230,170,25);
        taddress.setBackground(Color.LIGHT_GRAY);
        taddress.setBorder(bl);
        cp.add(taddress);
        
        JLabel phone=new JLabel("Phone         : ");
        phone.setBounds(40,270,150,25);
        phone.setForeground(Color.white);
        phone.setFont(new Font("Tahoma",Font.PLAIN,20));
        cp.add(phone);
        
        tphone=new JTextField("",10);
        tphone.setToolTipText("Enter phone Number");
        tphone.setFont(new Font("Tahoma",Font.BOLD,12));
        tphone.setBounds(180,270,170,25);
        tphone.setBackground(Color.LIGHT_GRAY);
        tphone.setBorder(bl);
        cp.add(tphone);
        
        JLabel mail=new JLabel("Email          : ");
        mail.setBounds(40,310,150,25);
        mail.setForeground(Color.white);
        mail.setFont(new Font("Tahoma",Font.PLAIN,20));
        cp.add(mail);
        
        tmail=new JTextField("",30);
        tmail.setToolTipText("Enter Email-Address");
        tmail.setFont(new Font("Tahoma",Font.BOLD,12));
        tmail.setBounds(180,310,170,25);
        tmail.setBackground(Color.LIGHT_GRAY);
        tmail.setBorder(bl);
        cp.add(tmail);
        
        JLabel lgen=new JLabel("Gender        : ");
        lgen.setBounds(40,350,150,25);
        lgen.setForeground(Color.white);
        lgen.setFont(new Font("Tahoma",Font.PLAIN,20));
        cp.add(lgen);
        
        ButtonGroup gp=new ButtonGroup();
        mrb=new JRadioButton("Male");
        mrb.setFont(new Font("Tahoma",Font.PLAIN,17));       
        mrb.setBounds(180,350,60,25);
        mrb.setForeground(Color.white);
        mrb.setBackground(sb);
        cp.add(mrb);
        
        mrf=new JRadioButton("Female");
        mrf.setFont(new Font("Tahoma",Font.PLAIN,17)); 
        mrf.setBackground(sb);
        mrf.setForeground(Color.white);
        mrf.setBounds(235,350,80,25);
        cp.add(mrf);
        
        other=new JRadioButton("Other");
        other.setFont(new Font("Tahoma",Font.PLAIN,17)); 
        other.setBackground(sb);
        other.setForeground(Color.white);
        other.setBounds(315,350,70,25);
        other.doClick();
        cp.add(other);

        gp.add(mrb);
        gp.add(mrf);
        gp.add(other);
        
        JPanel bp=new JPanel();
        bp.setLayout(null);
        bp.setBounds(40,395,400,40);
        bp.setBackground(sb);
        
        save=new JButton("SAVE");
        save.setBorder(bl);
        save.setBounds(210,5,100,30);
        
        clear=new JButton("CLEAR");
        clear.setBorder(bl);
        clear.setBounds(0,5,100,30);
        
        bp.add(save);
        bp.add(clear);
         cp.add(bp);
        ImageIcon ig=new ImageIcon(ClassLoader.getSystemResource("airline_management/assets/emp.png"));
        JLabel jig=new JLabel(ig);
        jig.setBounds(410,50,200,400);
        cp.add(jig);
        this.getContentPane().add(cp);
        cp.setBorder(bl);
        save.addActionListener(this);
        clear.addActionListener(this);
        
        //add scrollbar
        cp.setPreferredSize(new Dimension(600,450));
        JScrollPane scroll=new JScrollPane(cp);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.getContentPane().add(scroll);
        
        
        
        pack();
        setSize(640,500);
        setResizable(false);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        
    }
    @Override
    public void actionPerformed(ActionEvent ae){
       
        String name=tname.getText();
        String age=tage.getText();
        String aadhar=taadhar.getText();
        String nation=tnation.getText();
        String phone=tphone.getText();
        String email=tmail.getText();
        String address=taddress.getText();
        String gender=null;
        String cid=AddCustomer.generateID();
        if(mrb.isSelected())
            gender="Male";
        else if(mrf.isSelected())
            gender="Female";
        else
            gender="Other";
        
  
        if(ae.getSource()==clear){
           tname.setText("");
           tage.setText("");
           taadhar.setText("");
           tnation.setText("");
           tphone.setText("");
           tmail.setText("");
           taddress.setText("");
           other.doClick();
   
       }
       else if(ae.getSource()==save){
           if(name.trim().isEmpty()){
               JOptionPane.showMessageDialog(this, "Name Feild cannot be empty!");
               return;}
           if(nation.trim().isEmpty()){
               JOptionPane.showMessageDialog(this, "Nationality Feild cannot be empty!");
               return;}
           if(age.trim().isEmpty()){
               JOptionPane.showMessageDialog(this, "Age Feild cannot be empty!");
               return;}
           if(Integer.parseInt(age.trim())>=100){
               JOptionPane.showMessageDialog(this, "Re-Enter Age Feild!");
               return;}
            if(aadhar.trim().isEmpty()){
               JOptionPane.showMessageDialog(this, "Aadhar Number Feild cannot be empty !");
               return;}
           if(aadhar.trim().length()!=12){ 
               JOptionPane.showMessageDialog(this, "Aadhar Number must be 12 digits!");
               return;}
              if(address.trim().isEmpty()){
               JOptionPane.showMessageDialog(this, "Address Feild cannot be empty!");
               return;}
            if(phone.trim().isEmpty()){
               JOptionPane.showMessageDialog(this, "Phone Feild cannot be empty!");
               return;}
             if(!phone.trim().matches("\\d{10}")){ 
               JOptionPane.showMessageDialog(this, "Phone Number must be 10 digits!");
               return;}
             if(email.trim().isEmpty()){
               JOptionPane.showMessageDialog(this, "Email Feild cannot be empty!");
               return;}
              if(!email.trim().matches("^(.+)@gmail(.com+)$")){ 
               JOptionPane.showMessageDialog(this, "Invalid Email!");
               return;}

            
           
           
       try{
           Conn con=new Conn();
           String query="insert into passenger values('"+cid+"','"+name.trim()+"','"+nation.trim()+"','"+age.trim()+"','"+aadhar.trim()+"','"+address.trim()+"','"+phone.trim()+"','"+email.trim()+"','"+gender+"')";
           con.s.executeUpdate(query);
       }
       catch(Exception e){
           e.printStackTrace();
           
       }
     JOptionPane.showMessageDialog(null, "Customer Details saved successfully!");
     JOptionPane.showMessageDialog(null, "Note your Customer ID : "+cid);

       }
    
    }
    public static void main(String args[]){
     AddCustomer a =new AddCustomer();
     a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     a.setVisible(false);
    }

    
}
