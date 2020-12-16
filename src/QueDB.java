import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class QueDB {
    String user;
    String pass;
    String dbname;
    String url;
    Connection conn;
    ResultSet res;
    Statement stat;
    

    QueDB(String newUser, String newPass, String newDBname) {
        user = newUser;
        pass = newPass;
        dbname = newDBname;
    }

    public void connectDB() {
        url = (
                "jdbc:mysql://localhost:3306/" + dbname +
                "?useJDBCCompliantTimezoneShift=true&" +
                "useLegacyDatetimeCode=false&serverTimezone=UTC"
        );
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.print(e);
        }
        
        try {
            conn = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            this.createDBTable();
        }
    }
    
    public void createDBTable() {
       String localURL = (
               "jdbc:mysql://localhost:3306/" +
               "?useJDBCCompliantTimezoneShift=true&" +
               "useLegacyDatetimeCode=false&serverTimezone=UTC"
       );
       
       try {
           conn = DriverManager.getConnection(localURL, user, pass);
           stat = conn.createStatement();
           
           stat.executeUpdate(
                   "CREATE DATABASE " + dbname
           );
           
           stat.execute(
                   "USE " + dbname
           );
            
           System.out.println("database created.");
           
           stat.executeUpdate(
                   "CREATE TABLE customers (" +
                   "customer_id INT NOT NULL AUTO_INCREMENT," +
                   "customer_name VARCHAR(90) NOT NULL UNIQUE," +
                   "window INT(11)," +
                   "PRIMARY KEY (customer_id))"
           );
           
           
           System.out.println("table created.");
           
           conn.close();
           
       } catch (SQLException e) {
           System.out.print(e);
       }
       
    }
    
    public boolean insertDATA(String custName, int window) {
       try {
           conn = DriverManager.getConnection(url, user, pass);
           stat = conn.createStatement();
           stat.executeUpdate(
                   "INSERT INTO customers (" +
                   "customer_name," +
                   "window)" +
                   "VALUES ('" + custName + "'" + ", " +
                   "'" + window + "')"
           );
           conn.close();
           return true;           
       } catch (SQLException e) {
           return false;
       }        
    }
    
    public void deleteEntries() {
        try {
            conn = DriverManager.getConnection(url, user, pass);
            stat = conn.createStatement();
            stat.executeUpdate(
                    "DELETE FROM customers"
            );
            
            stat.execute(
                   "ALTER TABLE customers AUTO_INCREMENT = 1;"
           );

            System.out.println("deleted all entries");
            conn.close();
            
        } catch (SQLException e) {
            System.out.print(e);
        }
    }
    
    public int countEntries() {

        try {
            conn = DriverManager.getConnection(url, user, pass);
            stat = conn.createStatement();
            res = stat.executeQuery(
                    "SELECT COUNT(*) AS rowCount FROM customers"
            );
            res.next();
            return res.getInt("rowCount");
            
        } catch (SQLException e) {
            System.out.print(e);
            return 0;
        }
    }
    
    public String[][] getEntries() {
        try {
            conn = DriverManager.getConnection(url, user, pass);
            stat = conn.createStatement();
            int totalEntries = this.countEntries();
            int tempCount = 0;
            
            String[][] allData = new String[totalEntries][3];
            
            res = stat.executeQuery(
                    "SELECT * FROM customers"
            );
            
            while(res.next()) {
                int custID = res.getInt("customer_id");
                String strCustID = String.valueOf(custID);
                String custName = res.getString("customer_name");
                String custTrans = res.getString("window");
                
                allData[tempCount][0] = strCustID;
                allData[tempCount][1] = custName;
                allData[tempCount][2] = custTrans;
                
                tempCount++;
            }
            
            conn.close();
            
            return allData;
        } catch (SQLException e) {
            System.out.print(e);
            return null;
        }
        
    }
    
    public String[] getLastEntry() {
        String [] lastEntry = new String[3];
        try {
            conn = DriverManager.getConnection(url, user, pass);
            stat = conn.createStatement();
            res = stat.executeQuery(
                    "SELECT * FROM customers ORDER BY customer_id DESC LIMIT 1"
            );
            res.next();
            
            int custID = res.getInt("customer_id");
            String strCustID = String.valueOf(custID);
            String custName = res.getString("customer_name");
            String custTrans = res.getString("window");
            
            lastEntry[0] = strCustID;
            lastEntry[1] = custName;
            lastEntry[2] = custTrans;
            
            conn.close();
            return lastEntry;
            
        } catch (SQLException e) {
            return null;
        }
    }
}
