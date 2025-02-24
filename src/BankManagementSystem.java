import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class BankManagementSystem extends JFrame {
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    BankManagementSystem window = new BankManagementSystem();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public BankManagementSystem() {
        initializeDatabase();
        login();
        frame.setVisible(true);
    }

    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/bank";
            String user = "root";
            String password = "pratyush@10";
            
            connection = DriverManager.getConnection(url, user, password);
            
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS admin ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(255) NOT NULL,"
                    + "password VARCHAR(255) NOT NULL,"
                    + "UNIQUE INDEX (username)"
                    + ");");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS details ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(255) NOT NULL,"
                    + "FOREIGN KEY (username) REFERENCES admin(username),"
                    + "Customer_Name VARCHAR(255) NOT NULL,"
                    + "Branch VARCHAR(255) NOT NULL,"
                    + "City VARCHAR(255) NOT NULL,"
                    + "Phone VARCHAR(10) NOT NULL"
                    + ");");            
            statement.close();
        } catch (Exception e) {
            System.out.println("Failed to create tables: " + e);
        }
    }


    private boolean login(String username, String password) {
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM admin WHERE username = ? AND password = ?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                String storedUsername = resultSet.getString("username");
                return username.equals(storedUsername)&&password.equals(storedPassword);                
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Login failed: " + e);
            return false;
        }
    }

    public void login() {
        frame = new JFrame();
        frame.setTitle("Bank Management System");
        frame.setBounds(100,100,300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 300, 150);
        frame.getContentPane().add(panel);
        panel.setLayout(null);

        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setBounds(10,5,80,25);
        panel.add(usernameLabel);
        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setBounds(10,42,80,25);
        panel.add(passwordLabel);

        JTextField usernameField = new JTextField(15);
        usernameField.setBounds(100,7,100,25);
        panel.add(usernameField);
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setBounds(100,45,100,25);
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (login(username, password)) {
                    // If login is successful, open the bank page
                    openBankPage(username);
                } else {
                    JOptionPane.showMessageDialog(null, "Login Failed. Username and password do not match.");
                }
            }
        });
        loginButton.setBounds(110,82,70,25);
        panel.add(loginButton);
    }

    private void openBankPage(String loggedInUsername) {
        JFrame bankPage = new JFrame();
        bankPage.setTitle("Bank Page");
        bankPage.setBounds(100, 100, 1000, 800);
        bankPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        // Create a JPanel to hold user details
        JPanel topPanel = new JPanel(new FlowLayout()); // Use GridLayout to organize labels

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM details WHERE username = ?");
            preparedStatement.setString(1, loggedInUsername);
            resultSet = preparedStatement.executeQuery();
    
            if (resultSet.next()) {
                String customerName = resultSet.getString("Customer_Name");
                String branch = resultSet.getString("Branch");
                String city = resultSet.getString("City");
                String phone = resultSet.getString("Phone");
    
                JLabel nameLabel = new JLabel("Customer Name: " + customerName);
                JLabel branchLabel = new JLabel("Branch: " + branch);
                JLabel cityLabel = new JLabel("City: " + city);
                JLabel phoneLabel = new JLabel("Phone: " + phone);
    
                topPanel.add(nameLabel);
                topPanel.add(branchLabel);
                topPanel.add(cityLabel);
                topPanel.add(phoneLabel);
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve user details: " + e);
        }
    
        // Add the user details panel to the bankPage frame
        bankPage.add(topPanel);
    
        bankPage.setVisible(true);
        frame.setVisible(false);
    }
}