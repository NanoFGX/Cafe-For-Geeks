package DatabaseTest;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuManagement extends Application {

    private TextField idField;
    private TextField foodNameField;
    private TextField descriptionField;
    private TextField priceField;
    private TextField quantityField;
    private ImageView imageView;
    private File selectedImageFile;

    private TextField searchField;
    private TableView<Menu> tableView;

    private Connection connection;

    private static final String IMAGE_DIRECTORY = "C:\\Users\\User\\eclipse-workspace\\Project\\src\\DatabaseTest\\images";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Ensure the image directory exists
        createImageDirectory();

        // Establish database connection
        connectToDatabase();

        primaryStage.setTitle("Menu Management System");

        // Input Fields
        idField = new TextField();
        idField.setPromptText("ID");

        foodNameField = new TextField();
        foodNameField.setPromptText("Food Name");

        descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        priceField = new TextField();
        priceField.setPromptText("Price");

        quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        Button chooseImageButton = new Button("Choose Image");
        imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        chooseImageButton.setOnAction(e -> chooseImage(primaryStage));

        // Buttons
        Button insertButton = new Button("Insert");
        insertButton.setOnAction(e -> insertData());

        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> updateData());

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteData());

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> searchData());

        // Search Field
        searchField = new TextField();
        searchField.setPromptText("Search by ID or Food Name");

        // TableView for displaying results
        tableView = new TableView<>();
        TableColumn<Menu, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Menu, String> foodNameColumn = new TableColumn<>("Food Name");
        foodNameColumn.setCellValueFactory(new PropertyValueFactory<>("foodName"));

        TableColumn<Menu, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Menu, String> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Menu, String> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Menu, String> soldColumn = new TableColumn<>("Sold");
        soldColumn.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));

        TableColumn<Menu, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableView.getColumns().addAll(idColumn, foodNameColumn, descriptionColumn, priceColumn, quantityColumn, soldColumn, statusColumn);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Layout
        GridPane formLayout = new GridPane();
        formLayout.setHgap(10);
        formLayout.setVgap(10);
        formLayout.setPadding(new Insets(10));

        formLayout.add(new Label("ID:"), 0, 0);
        formLayout.add(idField, 1, 0);

        formLayout.add(new Label("Food Name:"), 0, 1);
        formLayout.add(foodNameField, 1, 1);

        formLayout.add(new Label("Description:"), 0, 2);
        formLayout.add(descriptionField, 1, 2);

        formLayout.add(new Label("Price:"), 0, 3);
        formLayout.add(priceField, 1, 3);

        formLayout.add(new Label("Quantity:"), 0, 4);
        formLayout.add(quantityField, 1, 4);

        formLayout.add(new Label("Image:"), 0, 5);
        formLayout.add(chooseImageButton, 1, 5);
        formLayout.add(imageView, 2, 5);

        // Button Row Layout
        HBox buttonRow = new HBox(10);
        buttonRow.setPadding(new Insets(10));
        buttonRow.setAlignment(Pos.CENTER);

        // Left side buttons (Delete, Insert, Update)
        HBox leftButtons = new HBox(10, insertButton, updateButton, deleteButton);
        leftButtons.setAlignment(Pos.CENTER_LEFT);

        // Right side (Search field and button)
        HBox searchBox = new HBox(10, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER_RIGHT);

        // Add left and right buttons to the button row
        buttonRow.getChildren().addAll(leftButtons, searchBox);
        HBox.setHgrow(searchBox, Priority.ALWAYS); // Ensures Search stays at the far right.

        // Main Layout (without Back Button)
        VBox mainLayout = new VBox(10, formLayout, buttonRow, tableView);
        mainLayout.setPadding(new Insets(10));

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createImageDirectory() {
        File directory = new File(IMAGE_DIRECTORY);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                showAlert("Error", "Failed to create image directory.");
            }
        }
    }

    private void connectToDatabase() {
        String url = "jdbc:derby:UserDB;create=true"; // Database URL
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to connect to the database.");
        }
    }

    private void chooseImage(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            imageView.setImage(image);
        }
    }

    private String saveImageToDirectory(File imageFile) {
        File directory = new File(IMAGE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate a unique file name to avoid overwriting existing files
        String fileName = System.currentTimeMillis() + "_" + imageFile.getName();
        File destination = new File(directory, fileName);

        try (FileInputStream fis = new FileInputStream(imageFile);
             FileOutputStream fos = new FileOutputStream(destination)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            return destination.getAbsolutePath(); // Return the full path of the saved image
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save the image.");
            return null;
        }
    }

    private void insertData() {
        String sql = "INSERT INTO FoodItems (id, name, description, price, imagePath, quantity, quantitySold) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, Integer.parseInt(idField.getText()));
            preparedStatement.setString(2, foodNameField.getText());
            preparedStatement.setString(3, descriptionField.getText());
            preparedStatement.setDouble(4, Double.parseDouble(priceField.getText()));

            if (selectedImageFile != null) {
                // Save the image path in the database
                String imagePath = saveImageToDirectory(selectedImageFile);
                preparedStatement.setString(5, imagePath);
            } else {
                preparedStatement.setNull(5, Types.VARCHAR); // Use VARCHAR for image path
            }

            preparedStatement.setInt(6, Integer.parseInt(quantityField.getText()));
            preparedStatement.setInt(7, 0); // Initialize quantitySold to 0

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Success", "Menu item inserted successfully.");
                refreshTable();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to insert data: " + e.getMessage());
        }
    }

    private void updateData() {
        String sql = "UPDATE FoodItems SET description = COALESCE(?, description), price = COALESCE(?, price), quantity = COALESCE(?, quantity), imagePath = COALESCE(?, imagePath) WHERE id = ? OR name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Set description if provided
            if (!descriptionField.getText().isEmpty()) {
                preparedStatement.setString(1, descriptionField.getText());
            } else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }

            // Set price if provided
            if (!priceField.getText().isEmpty()) {
                preparedStatement.setDouble(2, Double.parseDouble(priceField.getText()));
            } else {
                preparedStatement.setNull(2, Types.DOUBLE);
            }

            // Set quantity if provided
            if (!quantityField.getText().isEmpty()) {
                preparedStatement.setInt(3, Integer.parseInt(quantityField.getText()));
            } else {
                preparedStatement.setNull(3, Types.INTEGER);
            }

            // Set image path if a new image is selected
            if (selectedImageFile != null) {
                String imagePath = saveImageToDirectory(selectedImageFile);
                preparedStatement.setString(4, imagePath);
            } else {
                preparedStatement.setNull(4, Types.VARCHAR);
            }

            // Set ID or Food Name for the WHERE clause
            preparedStatement.setString(5, idField.getText());
            preparedStatement.setString(6, foodNameField.getText());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                showAlert("Success", "Menu item updated successfully.");
                refreshTable();
            } else {
                showAlert("Error", "No matching record found for update.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update data: " + e.getMessage());
        }
    }

    private void deleteData() {
        String sql = "DELETE FROM FoodItems WHERE id = ? OR name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, idField.getText());
            preparedStatement.setString(2, foodNameField.getText());

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                showAlert("Success", "Menu item deleted successfully.");
                clearFields();
                refreshTable();
            } else {
                showAlert("Error", "No matching record found for deletion.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete data: " + e.getMessage());
        }
    }

    private void searchData() {
        String searchInput = searchField.getText().trim();
        String sql;

        if (searchInput.isEmpty()) {
            // If no input, display all items
            sql = "SELECT * FROM FoodItems";
        } else {
            // Search by ID or Food Name (case-insensitive)
            sql = "SELECT * FROM FoodItems WHERE id = ? OR LOWER(name) = LOWER(?)";
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (!searchInput.isEmpty()) {
                // Set search parameters for ID or Food Name
                try {
                    int id = Integer.parseInt(searchInput); // Try parsing as integer (ID)
                    preparedStatement.setInt(1, id);
                    preparedStatement.setString(2, searchInput); // Also search by name
                } catch (NumberFormatException e) {
                    // If not an integer, search only by name
                    preparedStatement.setInt(1, -1); // Invalid ID to ensure no match
                    preparedStatement.setString(2, searchInput);
                }
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Menu> menuList = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String foodName = resultSet.getString("name");
                String description = resultSet.getString("description");
                String price = String.valueOf(resultSet.getDouble("price"));
                String quantity = String.valueOf(resultSet.getInt("quantity"));
                String quantitySold = String.valueOf(resultSet.getInt("quantitySold"));
                String status = resultSet.getInt("quantity") > 0 ? "Available" : "Out of Stock";

                menuList.add(new Menu(id, foodName, description, price, quantity, quantitySold, status));
            }

            tableView.getItems().setAll(menuList);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to search data: " + e.getMessage());
        }
    }

    private void clearFields() {
        idField.clear();
        foodNameField.clear();
        descriptionField.clear();
        priceField.clear();
        quantityField.clear();
        imageView.setImage(null);
        selectedImageFile = null;
    }

    private void refreshTable() {
        searchData(); // Refreshes the table by performing the search query.
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Menu class for TableView
    public static class Menu {
        private final int id;
        private final String foodName;
        private final String description;
        private final String price;
        private final String quantity;
        private final String quantitySold;
        private final String status;

        public Menu(int id, String foodName, String description, String price, String quantity, String quantitySold, String status) {
            this.id = id;
            this.foodName = foodName;
            this.description = description;
            this.price = price;
            this.quantity = quantity;
            this.quantitySold = quantitySold;
            this.status = status;
        }

        public int getId() {
            return id;
        }

        public String getFoodName() {
            return foodName;
        }

        public String getDescription() {
            return description;
        }

        public String getPrice() {
            return price;
        }

        public String getQuantity() {
            return quantity;
        }

        public String getQuantitySold() {
            return quantitySold;
        }

        public String getStatus() {
            return status;
        }
    }
}