import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class CSVToDatabase {

    public static void main(String[] args) {
        String csvFile = "C:\\Users\\Agraw\\Desktop"; // Replace with the correct path to your CSV file
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

                // Assuming CSV columns are in order: date, category, amount
                String dateString = data[0]; // Date as a string in "dd-MM-yyyy" format
                String category = data[1];
                double amount = Double.parseDouble(data[2]);

                // Parse the date string and convert it to "yyyy-MM-dd" format
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date utilDate = inputDateFormat.parse(dateString);
                String formattedDate = outputDateFormat.format(utilDate);

                // Convert the formatted date to a java.sql.Date object
                Date date = Date.valueOf(formattedDate);

                // Insert data into the database
                String insertQuery = "INSERT INTO expenditure (date, category, amount) VALUES (?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setDate(1, date);
                    preparedStatement.setString(2, category);
                    preparedStatement.setDouble(3, amount);
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
