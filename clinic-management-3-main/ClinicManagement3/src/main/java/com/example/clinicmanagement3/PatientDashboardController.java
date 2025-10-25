package com.example.clinicmanagement3;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

public class PatientDashboardController {

    @FXML
    private Button appointmentButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button viewAppointmentsButton;

    @FXML
    private Label nameLabel;

    @FXML
    private Label ageLabel;

    @FXML
    private Label genderLabel;

    @FXML
    private Label contactLabel;

    @FXML
    private Label emergencyLabel;

    @FXML
    private Label greetingLabel;

    private String currentUsername;

    public void setUsername(String username) {
        this.currentUsername = username;
        System.out.println("Dashboard received username: " + username);
        loadPatientGreeting(); // Load full name for greeting
        loadPatientInfo();     // Load other details
    }

    @FXML
    public void handleViewAppointments(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewAppointments.fxml"));
            Parent root = loader.load();

            ViewAppointmentsController controller = loader.getController();
            controller.setUsername(currentUsername);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Clinic Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLabResults(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LabResult.fxml"));
            Parent root = loader.load();

            LabResultController controller = loader.getController();
            controller.setUsername(currentUsername);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Clinic Management");
            stage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleTreatmentPlanClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TreatmentPlan.fxml"));
            Parent root = loader.load();

            TreatmentPlanController controller = loader.getController();
            controller.setUsername(currentUsername); //

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Clinic Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleAppointmentClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PatientAppointment.fxml"));
            Parent appointmentRoot = loader.load();

            PatientAppointmentController appointmentController = loader.getController();
            appointmentController.setUsername(currentUsername); // ✅ Pass again

            Stage stage = (Stage) appointmentButton.getScene().getWindow();
            stage.setScene(new Scene(appointmentRoot));
            stage.setTitle("Clinic Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleLogoutClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Signup.fxml"));
            Parent signupRoot = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(signupRoot));
            stage.setTitle("Clinic Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPatientInfo() {
        try (java.sql.Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT full_name, age, gender, contact_number, emergency_contact FROM user WHERE username = ?";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentUsername);
            java.sql.ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nameLabel.setText(rs.getString("full_name"));
                ageLabel.setText(rs.getString("age"));
                genderLabel.setText(rs.getString("gender"));
                contactLabel.setText(rs.getString("contact_number"));
                emergencyLabel.setText(rs.getString("emergency_contact"));
            } else {
                nameLabel.setText("Unknown");
                ageLabel.setText("-");
                genderLabel.setText("-");
                contactLabel.setText("-");
                emergencyLabel.setText("-");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPatientGreeting() {
        try (java.sql.Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT full_name FROM user WHERE username = ?";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentUsername);
            java.sql.ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("full_name");
                greetingLabel.setText("Welcome, " + fullName + "!");
            } else {
                greetingLabel.setText("Welcome!");
            }
        } catch (Exception e) {
            greetingLabel.setText("Welcome!");
            e.printStackTrace();
        }
    }
}