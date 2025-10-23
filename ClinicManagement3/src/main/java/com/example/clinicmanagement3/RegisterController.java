package com.example.clinicmanagement3;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField ageField;

    @FXML
    private DatePicker birthdayPicker;

    @FXML
    private ComboBox<String> genderComboBox;

    @FXML
    private TextField contactNumberField;

    @FXML
    private TextField emergencyContactField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Label warningLabel;

    @FXML
    private Button registerButton;

    @FXML
    private ComboBox<String> accountTypeComboBox;

    @FXML
    public void initialize() {
        genderComboBox.setPromptText("Select Gender");
        genderComboBox.getItems().addAll("Male", "Female", "Other");

        accountTypeComboBox.setPromptText("Select Account Type");
        accountTypeComboBox.getItems().addAll("Patient", "Doctor", "Receptionist", "Admin");
    }

    @FXML
    protected void handleRegisterClick() {
        String fullName = fullNameField.getText();
        String age = ageField.getText();
        var birthday = birthdayPicker.getValue();
        String gender = genderComboBox.getValue();
        String contact = contactNumberField.getText();
        String emergencyContact = emergencyContactField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String accountType = accountTypeComboBox.getValue();

        // Reset styles and messages
        fullNameField.setStyle("");
        ageField.setStyle("");
        birthdayPicker.setStyle("");
        genderComboBox.setStyle("");
        contactNumberField.setStyle("");
        emergencyContactField.setStyle("");
        usernameField.setStyle("");
        passwordField.setStyle("");
        accountTypeComboBox.setStyle("");
        warningLabel.setText("");

        boolean hasError = false;

        if (fullName == null || fullName.isEmpty()) {
            fullNameField.setStyle("-fx-border-color: red;");
            hasError = true;
        }

        if (age == null || age.isEmpty()) {
            ageField.setStyle("-fx-border-color: red;");
            hasError = true;
        }

        if (birthday == null) {
            birthdayPicker.setStyle("-fx-border-color: red;");
            hasError = true;
        }

        if (gender == null || gender.isEmpty()) {
            genderComboBox.setStyle("-fx-border-color: red;");
            hasError = true;
        }

        if (contact == null || contact.isEmpty()) {
            contactNumberField.setStyle("-fx-border-color: red;");
            hasError = true;
        }

        if (emergencyContact == null || emergencyContact.isEmpty()) {
            emergencyContactField.setStyle("-fx-border-color: red;");
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

        if (accountType == null || accountType.isEmpty()) {
            accountTypeComboBox.setStyle("-fx-border-color: red;");
            hasError = true;
        }

        if (hasError) {
            warningLabel.setStyle("-fx-text-fill: red;");
            warningLabel.setText("Please fill in all fields before registering.");
        } else {
            warningLabel.setText(""); // Clear warning

            // ✅ Save to SQL
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO user (full_name, age, birthday, gender, contact_number, emergency_contact, username, password, account_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, fullName);
                stmt.setString(2, age);
                stmt.setString(3, birthday.toString());
                stmt.setString(4, gender);
                stmt.setString(5, contact);
                stmt.setString(6, emergencyContact);
                stmt.setString(7, username);
                stmt.setString(8, password);
                stmt.setString(9, accountType);

                stmt.executeUpdate();

                // ✅ Redirect to SignUp.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
                Parent signupRoot = loader.load();
                Stage stage = (Stage) registerButton.getScene().getWindow();
                stage.setScene(new Scene(signupRoot));
                stage.setTitle("MediCore - Sign Up");
                stage.show();

            } catch (SQLException | IOException e) {
                warningLabel.setStyle("-fx-text-fill: red;");
                warningLabel.setText("Registration failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleBackToLogin(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("MediCore - Login");
            stage.show();
        } catch (IOException e) {
            warningLabel.setText("Unable to return to login.");
            e.printStackTrace();
        }
    }
}