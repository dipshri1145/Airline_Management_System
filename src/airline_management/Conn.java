package airline_management;
import java.sql.*;
public class Conn {
    Connection c;
    Statement s;
    public Conn(){
    try{
        //register the driver
    Class.forName("com.mysql.cj.jdbc.Driver");
    c=DriverManager.getConnection("jdbc:mysql://localhost:3306/airlinemanagementsystem","root","Dipshri810");
    s=c.createStatement();
    
    }
    
    catch(Exception e){
    e.printStackTrace();
}}
    public Connection getConnection() {
        return c;
    }
}


