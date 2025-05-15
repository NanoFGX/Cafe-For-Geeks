package DatabaseTest;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CafeForGeeks extends Application {

    private Map<String, Integer> orderSummary = new HashMap<>();
    private Connection connection;
    private FlowPane menuItems; 
    private VBox mainSection;   

    @Override
    public void start(Stage primaryStage) {
        connectToDatabase();

        HBox header = new HBox();
        header.setStyle("-fx-background-color: #444444; -fx-padding: 20px;");
        Label welcomeLabel = new Label("Welcome to CafeForGeeks");
        welcomeLabel.setTextFill(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", 28));
        header.getChildren().add(welcomeLabel);
        header.setAlignment(Pos.CENTER);

        // Sidebar Menu
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #333333; -fx-padding: 20px;");

        Button menuButton = new Button("Menu");
        Button paymentButton = new Button("Payment");
        Button socialsButton = new Button("Socials");

        setButtonStyle(menuButton);
        setButtonStyle(paymentButton);
        setButtonStyle(socialsButton);

        // Add a Hyperlink for Admin Login
        Hyperlink adminLink = new Hyperlink("Admin Login");
        adminLink.setStyle("-fx-text-fill: #FFA07A; -fx-font-size: 14px; -fx-underline: true;");
        adminLink.setOnAction(e -> {
            // Launch the LoginAdmin application
            new LoginAdmin().start(new Stage());
        });

        sidebar.getChildren().addAll(menuButton, paymentButton, socialsButton, adminLink);

        // Main Section
        mainSection = new VBox(20);
        mainSection.setPadding(new Insets(20));
        mainSection.setStyle("-fx-background-color: #eeeeee; -fx-padding: 20px;");

        Label todayMenuLabel = new Label("Today's Menu");
        todayMenuLabel.setFont(new Font("Arial", 22));

        // Use FlowPane instead of GridPane
        menuItems = new FlowPane();
        menuItems.setHgap(20);
        menuItems.setVgap(20);
        menuItems.setAlignment(Pos.CENTER);
        menuItems.setPadding(new Insets(10));

        // Wrap the FlowPane in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(menuItems);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #eeeeee; -fx-border-color: #eeeeee;");

        // Fetch menu items from the database
        loadMenuItems();

        // Reload Button
        Button reloadButton = new Button("Reload");
        reloadButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 16px;");
        reloadButton.setOnAction(e -> loadMenuItems());

        // Checkout Button
        Button checkoutButton = new Button("Check-out");
        checkoutButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 16px;");
        checkoutButton.setOnAction(e -> showOrderSummary(primaryStage));

        // Button Layout
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(reloadButton, checkoutButton);
        HBox.setHgrow(reloadButton, Priority.ALWAYS);
        HBox.setHgrow(checkoutButton, Priority.ALWAYS);

        paymentButton.setOnAction(e -> showOrderSummary(primaryStage));
        socialsButton.setOnAction(e -> showSocialsPage(primaryStage));

        mainSection.getChildren().addAll(todayMenuLabel, scrollPane, buttonBox);

        // Layout Setup
        BorderPane layout = new BorderPane();
        layout.setTop(header);
        layout.setLeft(sidebar);
        layout.setCenter(mainSection);

        // Scene Setup
        Scene scene = new Scene(layout, 900, 700);
        primaryStage.setTitle("CafeForGeeks");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setButtonStyle(Button button) {
        button.setPrefWidth(120);
        button.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;");
    }

    private void connectToDatabase() {
        String url = "jdbc:derby:UserDB;create=true"; // Database URL
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to the database.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to connect to the database.");
        }
    }

    private void loadMenuItems() {
        menuItems.getChildren().clear(); // Clear existing items
        String sql = "SELECT id, name, description, price, imagePath, quantity FROM FoodItems"; // Removed quantitySold
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                String imagePath = resultSet.getString("imagePath");
                int quantity = resultSet.getInt("quantity");

                // Create a food item with the fetched data
                VBox foodItem = createFoodItem(id, name, description, "RM" + price, imagePath, quantity);
                menuItems.getChildren().add(foodItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to fetch menu items: " + e.getMessage());
        }
    }

    private VBox createFoodItem(int id, String title, String description, String price, String imagePath, int quantity) {
        VBox foodItem = new VBox(10);
        foodItem.setPadding(new Insets(10));
        foodItem.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1px; -fx-padding: 10px;");

        // Availability Label
        Label availabilityLabel = new Label(quantity > 0 ? "Available" : "Out of Stock");
        availabilityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        availabilityLabel.setTextFill(quantity > 0 ? Color.GREEN : Color.RED);

        // ID Label
        Label idLabel = new Label("ID: " + id);
        idLabel.setFont(new Font("Arial", 12));
        idLabel.setTextFill(Color.GRAY);
        
        // ImageView for the food image
        ImageView foodImage = new ImageView();
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                foodImage.setImage(new Image(imageFile.toURI().toString()));
            } else {
                foodImage.setImage(new Image("file:/C:/Users/User/eclipse-workspace/Project/src/DatabaseTest/images/default.jpg"));
            }
        } else {
            foodImage.setImage(new Image("file:/C:/Users/User/eclipse-workspace/Project/src/DatabaseTest/images/default.jpg"));
        }
        foodImage.setFitHeight(100);
        foodImage.setFitWidth(100);
        foodImage.setPreserveRatio(true);

        // Food title
        Label foodTitle = new Label(title);
        foodTitle.setFont(new Font("Arial", 16));

        // Food description
        Label foodDescription = new Label(description);
        foodDescription.setFont(new Font("Arial", 12));
        foodDescription.setTextFill(Color.GRAY);
        foodDescription.setWrapText(true);
        foodDescription.setMaxWidth(150);
        // Food price
        Label foodPrice = new Label(price);
        foodPrice.setFont(new Font("Arial", 14));
        foodPrice.setTextFill(Color.BLACK);

        // Remaining quantity label
        Label remainingLabel = new Label("Remaining: " + quantity);
        remainingLabel.setFont(new Font("Arial", 12));

        // Controls for adding/subtracting quantity
        HBox controls = new HBox(10);
        Label qtyLabel = new Label("Qty: 0");
        qtyLabel.setFont(new Font("Arial", 12));

        Button addButton = new Button("+");
        Button subtractButton = new Button("-");

        addButton.setOnAction(e -> {
            int currentQuantity = orderSummary.getOrDefault(title, 0);
            if (currentQuantity < quantity) {
                orderSummary.put(title, currentQuantity + 1);
                qtyLabel.setText("Qty: " + orderSummary.get(title));
                remainingLabel.setText("Remaining: " + (quantity - orderSummary.get(title)));
            }
        });

        subtractButton.setOnAction(e -> {
            int currentQuantity = orderSummary.getOrDefault(title, 0);
            if (currentQuantity > 0) {
                orderSummary.put(title, currentQuantity - 1);
                qtyLabel.setText("Qty: " + orderSummary.get(title));
                remainingLabel.setText("Remaining: " + (quantity - orderSummary.get(title)));
            }
        });

        controls.getChildren().addAll(qtyLabel, addButton, subtractButton);

        // Add all elements to the VBox
        foodItem.getChildren().addAll(availabilityLabel, idLabel, foodImage, foodTitle, foodDescription, foodPrice, remainingLabel, controls);

        return foodItem;
    }

    private void showOrderSummary(Stage stage) {
        VBox summaryPage = new VBox(20);
        summaryPage.setAlignment(Pos.CENTER);
        summaryPage.setPadding(new Insets(20));
        summaryPage.setStyle("-fx-background-color: #ffffff; -fx-padding: 20px; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        // Title for the receipt
        Label receiptTitle = new Label("CafeForGeeks");
        receiptTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        receiptTitle.setTextFill(Color.BLACK);

        // Location and Contact Information
        Label locationLabel = new Label("FSKTM UPM, Lebuh Universiti, 43400 Serdang, Selangor");
        locationLabel.setFont(Font.font("Arial", 14));
        locationLabel.setTextFill(Color.BLACK);

        // Current Date and Time
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy   HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());
        Label dateTimeLabel = new Label(currentDateTime);
        dateTimeLabel.setFont(Font.font("Arial", 14));
        dateTimeLabel.setTextFill(Color.BLACK);

        // Order Summary Label
        Label summaryLabel = new Label("Order Summary");
        summaryLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        summaryLabel.setTextFill(Color.BLACK);

        // Order Details Table
        GridPane orderDetails = new GridPane();
        orderDetails.setHgap(40);
        orderDetails.setVgap(10);
        orderDetails.setAlignment(Pos.CENTER);

        // Table Headers
        Label itemHeader = new Label("Item");
        itemHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label qtyHeader = new Label("Qty");
        qtyHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label priceHeader = new Label("Price");
        priceHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label totalHeader = new Label("Total");
        totalHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        orderDetails.add(itemHeader, 0, 0);
        orderDetails.add(qtyHeader, 1, 0);
        orderDetails.add(priceHeader, 2, 0);
        orderDetails.add(totalHeader, 3, 0);

        double totalPrice = 0.0;
        int row = 1;

        for (Map.Entry<String, Integer> entry : orderSummary.entrySet()) {
            if (entry.getValue() > 0) {
                String itemName = entry.getKey();
                int quantity = entry.getValue();
                double itemPrice = getItemPrice(itemName);
                double totalItemPrice = itemPrice * quantity;
                totalPrice += totalItemPrice;

                Label itemLabel = new Label(itemName);
                itemLabel.setFont(Font.font("Arial", 14));
                Label qtyLabel = new Label(String.valueOf(quantity));
                qtyLabel.setFont(Font.font("Arial", 14));
                Label priceLabel = new Label("RM" + String.format("%.2f", itemPrice));
                priceLabel.setFont(Font.font("Arial", 14));
                Label totalLabel = new Label("RM" + String.format("%.2f", totalItemPrice));
                totalLabel.setFont(Font.font("Arial", 14));

                orderDetails.add(itemLabel, 0, row);
                orderDetails.add(qtyLabel, 1, row);
                orderDetails.add(priceLabel, 2, row);
                orderDetails.add(totalLabel, 3, row);

                row++;
            }
        }

        // Add a separator line
        Separator separator = new Separator();
        separator.setPrefWidth(600); // Set the width of the separator
        separator.setStyle("-fx-padding: 10px 0;"); // Add some padding around the separator

        // Total Price Label
        Label totalLabel = new Label("Total Price: RM" + String.format("%.2f", totalPrice));
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        totalLabel.setTextFill(Color.BLACK);

        // Buttons for New Order and Pay
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button addOrderButton = new Button("New Order");
        addOrderButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        addOrderButton.setOnAction(e -> {
            // Clear the order summary and reload the menu items
            orderSummary.clear();
            loadMenuItems();
            start(stage); // Return to the main menu
        });

        Button payButton = new Button("Pay");
        payButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        payButton.setOnAction(e -> showPaymentOptions(stage));

        buttonBox.getChildren().addAll(addOrderButton, payButton);

        // Add all elements to the summary page
        summaryPage.getChildren().addAll(receiptTitle, locationLabel, dateTimeLabel, summaryLabel, orderDetails, separator, totalLabel, buttonBox);

        Scene summaryScene = new Scene(summaryPage, 800, 600);
        stage.setScene(summaryScene);
    }

    private void showPaymentOptions(Stage stage) {
        VBox paymentPage = new VBox(20);
        paymentPage.setAlignment(Pos.CENTER);
        paymentPage.setPadding(new Insets(20));
        paymentPage.setStyle("-fx-background-color: #ffffff; -fx-padding: 20px; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        Label paymentLabel = new Label("Choose Payment Method");
        paymentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        paymentLabel.setTextFill(Color.BLACK);

        Button counterButton = new Button("Pay at Counter");
        counterButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        counterButton.setOnAction(e -> processPayment(stage));

        Button cashButton = new Button("Cash");
        cashButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        cashButton.setOnAction(e -> processPayment(stage));

        Button cardButton = new Button("Card");
        cardButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        cardButton.setOnAction(e -> processPayment(stage));

        Button qrButton = new Button("QR Code");
        qrButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        qrButton.setOnAction(e -> processPayment(stage));

        paymentPage.getChildren().addAll(paymentLabel, counterButton, cashButton, cardButton, qrButton);

        Scene paymentScene = new Scene(paymentPage, 800, 600);
        stage.setScene(paymentScene);
    }

    private void processPayment(Stage stage) {
        // Update the database with the remaining quantity and quantity sold
        String url = "jdbc:derby:UserDB;create=true";
        try (Connection connection = DriverManager.getConnection(url)) {
            for (Map.Entry<String, Integer> entry : orderSummary.entrySet()) {
                String itemName = entry.getKey();
                int quantityOrdered = entry.getValue();

                // Update the remaining quantity and quantity sold
                String updateSql = "UPDATE FoodItems SET quantity = quantity - ?, quantitySold = quantitySold + ? WHERE name = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                    updateStatement.setInt(1, quantityOrdered);
                    updateStatement.setInt(2, quantityOrdered);
                    updateStatement.setString(3, itemName);
                    updateStatement.executeUpdate();
                }
            }

            // Show payment success message
            showAlert("Success", "Payment was successful! Thank you for buying from CafeForGeeks.");
            orderSummary.clear(); // Clear the order summary
            loadMenuItems(); // Reload the menu items to reflect the latest quantities
            start(stage); // Return to the main menu
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to process payment: " + e.getMessage());
        }
    }

    private double getItemPrice(String itemName) {
        String url = "jdbc:derby:UserDB;create=true";
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement("SELECT price FROM FoodItems WHERE name = ?")) {

            statement.setString(1, itemName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("price");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private void showSocialsPage(Stage stage) {
        VBox socialsPage = new VBox(20);
        socialsPage.setAlignment(Pos.CENTER);
        socialsPage.setPadding(new Insets(20));

        Label socialsLabel = new Label("Connect with Us");
        socialsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label instagramLabel = new Label("Instagram: @CafeForGeeks");
        Label facebookLabel = new Label("Facebook: Cafe For Geeks");
        Label whatsappLabel = new Label("WhatsApp: +123456789");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> start(stage));

        socialsPage.getChildren().addAll(socialsLabel, instagramLabel, facebookLabel, whatsappLabel, backButton);

        Scene socialsScene = new Scene(socialsPage, 800, 600);
        stage.setScene(socialsScene);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}