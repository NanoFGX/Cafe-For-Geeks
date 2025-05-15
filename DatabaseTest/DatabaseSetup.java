package DatabaseTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseSetup {
    public static void main(String[] args) {
        try {
            // Define the database connection URL
            String url = "jdbc:derby:UserDB;create=true";

            // Establish connection to the database
            Connection connection = DriverManager.getConnection(url);
            System.out.println("Connected to the database.");

            // SQL statements to create and initialize the tables
            String[] sqlStatements = {
                "DROP TABLE MatricData", // Attempt to drop table if it exists
                "DROP TABLE FoodItems",  // Attempt to drop table if it exists
                "CREATE TABLE MatricData (matricNumber VARCHAR(20) PRIMARY KEY, password VARCHAR(50))", // Create table
                "INSERT INTO MatricData VALUES ('A001', 'password123')", // Insert sample data
                "INSERT INTO MatricData VALUES ('A002', 'securepass456')",
                "CREATE TABLE FoodItems (id INT PRIMARY KEY, name VARCHAR(100), description VARCHAR(255), price DOUBLE, imagePath VARCHAR(255), quantity INT, quantitySold INT, status VARCHAR(50))", // Create table with quantitySold
                "INSERT INTO FoodItems VALUES (1, 'Burger', 'Delicious beef burger', 9.99, 'file:/C:/Users/DELL/Downloads/burger.jpg', 10, 0, 'Available')", // Insert sample data
                "INSERT INTO FoodItems VALUES (2, 'Pizza', 'Cheesy pepperoni pizza', 12.99, 'file:/C:/Users/DELL/Downloads/pizza.jpg', 5, 0, 'Available')",
                "COMMIT" // Commit changes
            };

            // Execute each SQL statement
            for (String sql : sqlStatements) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute(sql);
                    System.out.println("Executed: " + sql);
                } catch (Exception e) {
                    System.out.println("Skipping: " + sql + " - " + e.getMessage());
                }
            }

            System.out.println("Database and tables setup completed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}