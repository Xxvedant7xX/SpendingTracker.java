import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CSVToDatabase2 {

    public static void main(String[] args) {
        String csvFile = "D:\\Old\\vscodess\\Java\\mysql2.java\\expenses.csv"; // Replace with the correct path to your CSV file
        String dbUrl = "jdbc:mysql://localhost:3306/expenses";
        String dbUser = "root";
        String dbPassword = "root";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {

            // Skip the header row
            String header = reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                // Assuming CSV columns are in order: account_id, date, description, amount
                int account_id = Integer.parseInt(data[0]);
                String date = data[1];
                String description = data[2];
                double amount = Double.parseDouble(data[3]);

                // Insert data into the database
                String insertQuery = "INSERT INTO expenditure (account_id, date, category, amount) VALUES (?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setInt(1, account_id);
                    preparedStatement.setString(2, date);
                    preparedStatement.setString(3, description);
                    preparedStatement.setDouble(4, amount);
                    preparedStatement.executeUpdate();
                }
            }

            System.out.println("Data inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
