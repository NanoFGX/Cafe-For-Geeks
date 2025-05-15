package DatabaseTest;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Background Image
        Image backgroundImage = new Image("file:/C:/Users/User/eclipse-workspace/Project/src/DatabaseTest/background.jpg");
        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT, // No repeat
                BackgroundRepeat.NO_REPEAT, // No repeat
                BackgroundPosition.CENTER,  // Center the image
                new BackgroundSize(100, 100, true, true, true, true) // Expand to fit the window
        );

        // Title Label
        Label titleLabel = new Label("Welcome To CafeForGeeks");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30)); // Bold, size 30
        titleLabel.setTextFill(Color.WHITE); // Black text color

        // Login Form
        GridPane loginGrid = new GridPane();
        loginGrid.setAlignment(Pos.CENTER);
        loginGrid.setPadding(new Insets(10));
        loginGrid.setHgap(10);
        loginGrid.setVgap(10);

        Label matricLabel = new Label("Matric Number:");
        matricLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        matricLabel.setStyle("-fx-text-fill: white;");
        TextField matricField = new TextField();

        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        passwordLabel.setStyle("-fx-text-fill: white;");
        PasswordField passwordField = new PasswordField();

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #FFA07A; -fx-text-fill: white; -fx-background-radius: 15;");
        Button signUpButton = new Button("Sign Up");
        signUpButton.setStyle("-fx-background-color: #FF7F50; -fx-text-fill: white; -fx-background-radius: 15;");
        buttonBox.getChildren().addAll(loginButton, signUpButton);

        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14)); // Bold, size 14

        loginGrid.add(matricLabel, 0, 0);
        loginGrid.add(matricField, 1, 0);
        loginGrid.add(passwordLabel, 0, 1);
        loginGrid.add(passwordField, 1, 1);
        loginGrid.add(buttonBox, 1, 2);
        loginGrid.add(statusLabel, 1, 3);

        // Main Layout
        VBox mainLayout = new VBox(20); // Add spacing between title and login form
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(titleLabel, loginGrid);

        // Set the background
        mainLayout.setBackground(new Background(background));

        Scene loginScene = new Scene(mainLayout, 600, 400);

        primaryStage.setTitle("CafeForGeeks");
        primaryStage.setScene(loginScene);
        primaryStage.show();

        // Handle Login Button Click
        loginButton.setOnAction(e -> {
            String matricNumber = matricField.getText();
            String password = passwordField.getText();

            if (validateCredentials(matricNumber, password)) {
                statusLabel.setText("Login successful!");
                statusLabel.setTextFill(Color.GREEN); // Green text color for success
                // Close the login window and open the CafeForGeeks main menu
                primaryStage.close(); // Close the login window
                new CafeForGeeks().start(new Stage()); // Launch the CafeForGeeks application
            } else {
                statusLabel.setText("Invalid matric number or password.");
                statusLabel.setTextFill(Color.RED); // Red text color for failure
            }
        });

        // Handle Sign Up Button Click
        signUpButton.setOnAction(e -> showSignUpStage(primaryStage, loginScene));
    }

    private void showSignUpStage(Stage primaryStage, Scene loginScene) {
        // Background Image
        Image backgroundImage = new Image("file:/C:/Users/User/eclipse-workspace/Project/src/DatabaseTest/background.jpg");
        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT, // No repeat
                BackgroundRepeat.NO_REPEAT, // No repeat
                BackgroundPosition.CENTER,  // Center the image
                new BackgroundSize(100, 100, true, true, true, true) // Expand to fit the window
        );

        // Registration Form
        GridPane signUpGrid = new GridPane();
        signUpGrid.setAlignment(Pos.CENTER);
        signUpGrid.setPadding(new Insets(10));
        signUpGrid.setHgap(10);
        signUpGrid.setVgap(10);

        Label matricLabel = new Label("Matric Number:");
        matricLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        matricLabel.setStyle("-fx-text-fill: white;");
        TextField matricField = new TextField();

        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        passwordLabel.setStyle("-fx-text-fill: white;");
        PasswordField passwordField = new PasswordField();

        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #FFA07A; -fx-text-fill: white; -fx-background-radius: 15;");
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #FF7F50; -fx-text-fill: white; -fx-background-radius: 15;");
        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14)); // Bold, size 14

        signUpGrid.add(matricLabel, 0, 0);
        signUpGrid.add(matricField, 1, 0);
        signUpGrid.add(passwordLabel, 0, 1);
        signUpGrid.add(passwordField, 1, 1);
        signUpGrid.add(registerButton, 1, 2);
        signUpGrid.add(backButton, 1, 3);
        signUpGrid.add(statusLabel, 1, 4);

        // Main Layout
        VBox mainLayout = new VBox(20); // Add spacing between title and login form
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(signUpGrid);

        // Set the background
        mainLayout.setBackground(new Background(background));

        Scene signUpScene = new Scene(mainLayout, 600, 400);

        // Handle Register Button Click
        registerButton.setOnAction(e -> {
            String matricNumber = matricField.getText();
            String password = passwordField.getText();

            if (registerUser(matricNumber, password)) {
                statusLabel.setText("Sign-up successful! You can now log in.");
                statusLabel.setTextFill(Color.GREEN); // Green text color for success
            } else {
                statusLabel.setText("Sign-up failed. Try again.");
                statusLabel.setTextFill(Color.RED); // Red text color for failure
            }
        });

        // Handle Back Button Click
        backButton.setOnAction(e -> primaryStage.setScene(loginScene));

        primaryStage.setScene(signUpScene);
    }

    private boolean validateCredentials(String matricNumber, String password) {
        boolean isValid = false;
        String url = "jdbc:derby:UserDB;create=true";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM MatricData WHERE matricNumber = ? AND password = ?")) {

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

    private boolean registerUser(String matricNumber, String password) {
        boolean isRegistered = false;
        String url = "jdbc:derby:UserDB;create=true";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO MatricData (matricNumber, password) VALUES (?, ?)")) {

            statement.setString(1, matricNumber);
            statement.setString(2, password);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                isRegistered = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isRegistered;
    }

    public static void main(String[] args) {
        launch(args);
    }
}