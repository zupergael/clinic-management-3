package com.example.clinicmanagement3;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AccountTypeController implements Initializable {

    @FXML
    private ComboBox<String> accountTypeComboBox;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private Label warningLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountTypeComboBox.getItems().addAll("Admin", "Doctor", "Receptionist", "Patient");
        accountTypeComboBox.setPromptText("Select Account Type");
    }

    private String getSelectedAccountType() {
        String selectedType = accountTypeComboBox.getValue();
        if (selectedType == null || selectedType.isEmpty()) {
            accountTypeComboBox.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            warningLabel.setText("Please select an account type.");
            warningLabel.setStyle("-fx-text-fill: red;");
            return null;
        }
        return selectedType;
    }

    @FXML
    protected void handleLogin() {
        String selectedType = getSelectedAccountType();
        if (selectedType == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml")); // Login screen
            Parent loginRoot = loader.load();

            HelloController loginController = loader.getController();
            loginController.setAccountType(selectedType);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Clinic Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            warningLabel.setText("Failed to load login screen.");
        }
    }

    @FXML
    protected void handleRegister() {
        String selectedType = getSelectedAccountType();
        if (selectedType == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Registration.fxml"));
            Parent registerRoot = loader.load();

            RegisterController registerController = loader.getController();
            registerController.setAccountType(selectedType);

            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(new Scene(registerRoot));
            stage.setTitle("Clinic Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            warningLabel.setText("Failed to load registration screen.");
        }
    }
}