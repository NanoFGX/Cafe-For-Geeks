package DatabaseTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseAdmin {
    public static void main(String[] args) {
        try {
            // Define the database connection URL
            String url = "jdbc:derby:UserDB;create=true";

            // Establish connection to the database
            Connection connection = DriverManager.getConnection(url);

            // SQL statements to create and initialize the Staff table
            String[] sqlStatements = {
                "DROP TABLE Staff", // Attempt to drop the table if it exists
                "CREATE TABLE Staff (StaffID VARCHAR(20) PRIMARY KEY, password VARCHAR(50))", // Create table
                "INSERT INTO Staff VALUES ('S01', 'food')", // Insert sample data
                "INSERT INTO Staff VALUES ('S02', 'tech')" // Insert sample data
            };

            // Execute each SQL statement
            for (String sql : sqlStatements) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute(sql);
                } catch (Exception e) {
                    System.out.println("Skipping: " + sql + " - " + e.getMessage());
                }
            }

            System.out.println("Database and Staff table setup completed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}