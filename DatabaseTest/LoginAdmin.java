package DatabaseTest;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginAdmin extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create a GridPane for the login form
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Add components to the GridPane
        Label matricLabel = new Label("Staff ID:");
        matricLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;"); // White and bold text
        TextField matricField = new TextField();
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;"); // White and bold text
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: orange; -fx-text-fill: white; -fx-font-weight: bold;"); // Orange button with white text
        Label statusLabel = new Label();

        gridPane.add(matricLabel, 0, 0);
        gridPane.add(matricField, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(loginButton, 1, 2);
        gridPane.add(statusLabel, 1, 3);

        // Load the background image
        String imagePath = "file:/C:/Users/User/eclipse-workspace/Project/src/DatabaseTest/backgroundadmin.jpg";
        Image backgroundImage = new Image(imagePath);

        // Create a BackgroundImage
        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT, // No repeat
                BackgroundRepeat.NO_REPEAT, // No repeat
                BackgroundPosition.CENTER,  // Center the image
                new BackgroundSize(100, 100, true, true, true, true) // Stretch to fit
        );

        // Set the background to the GridPane
        gridPane.setBackground(new Background(background));

        // Create the scene
        Scene loginScene = new Scene(gridPane, 400, 300);
        primaryStage.setTitle("Admin Login Page");
        primaryStage.setScene(loginScene);
        primaryStage.show();

        // Handle Login Button Click
        loginButton.setOnAction(e -> {
            String matricNumber = matricField.getText().trim();
            String password = passwordField.getText().trim();

            if (matricNumber.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please enter Staff ID and password.");
                return;
            }

            if (validateCredentials(matricNumber, password)) {
                statusLabel.setText("Login successful!");
                // Close the login window
                primaryStage.close();
                // Launch the MenuManagement application
                new MenuManagement().start(new Stage());
            } else {
                statusLabel.setText("Invalid Staff ID or password.");
            }
        });
    }

    private boolean validateCredentials(String matricNumber, String password) {
        boolean isValid = false;
        String url = "jdbc:derby:UserDB;create=true";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM Staff WHERE StaffID = ? AND password = ?")) {

            statement.setString(1, matricNumber);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    isValid = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isValid;
    }

    public static void main(String[] args) {
        launch(args);
    }
}