package com.example.clinicmanagement3;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private Label warningLabel;

    @FXML
    private ComboBox<String> accountTypeComboBox;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountTypeComboBox.setPromptText("Select Account Type");
        accountTypeComboBox.getItems().addAll("Admin", "Doctor", "Receptionist", "Patient");
    }

    @FXML
    protected void handleLogin() {
        String accountType = accountTypeComboBox.getValue();
        String username = usernameField.getText();
        String password = passwordField.getText();

        warningLabel.setText("");
        accountTypeComboBox.setStyle("");
        usernameField.setStyle("");
        passwordField.setStyle("");

        boolean hasError = false;

        if (accountType == null || accountType.isEmpty()) {
            accountTypeComboBox.setStyle("-fx-border-color: red;");
            hasError = true;
        }
        if (username == null || username.isEmpty()) {
            usernameField.setStyle("-fx-border-color: red;");
            hasError = true;
        }
        if (password == null || password.isEmpty()) {
            passwordField.setStyle("-fx-border-color: red;");
            hasError = true;
        }

        if (hasError) {
            warningLabel.setText("Please fill in all fields before logging in.");
            return;
        }

        try (java.sql.Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM user WHERE username = ? AND password = ? AND account_type = ?";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, accountType);
            java.sql.ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String fxmlFile = switch (accountType) {
                    case "Patient" -> "PatientDashboard.fxml";
                    case "Admin" -> "AdminDashboard.fxml";
                    case "Doctor" -> "DoctorDashboard.fxml";
                    case "Receptionist" -> "ReceptionistDashboard.fxml";
                    default -> null;
                };

                if (fxmlFile != null) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                    Parent dashboardRoot = loader.load();

// ✅ Pass username to PatientDashboardController
                    PatientDashboardController dashboardController = loader.getController();
                    dashboardController.setUsername(username);

                    Stage stage = (Stage) accountTypeComboBox.getScene().getWindow();
                    stage.setScene(new Scene(dashboardRoot));
                    stage.show();
                } else {
                    warningLabel.setText("Unknown account type.");
                }
            } else {
                warningLabel.setStyle("-fx-text-fill: red;");
                warningLabel.setText("Invalid credentials or account type.");
            }
        } catch (Exception e) {
            warningLabel.setText("Database error.");
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Registration.fxml"));
            Parent registerRoot = loader.load();

            Stage stage = (Stage) accountTypeComboBox.getScene().getWindow();
            stage.setScene(new Scene(registerRoot));
            stage.show();
        } catch (IOException e) {
            warningLabel.setText("Unable to load registration screen.");
            e.printStackTrace();
        }
    }
}