package com.example.clinicmanagement3;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    private String currentUsername;

    public void setUsername(String username) {
        this.currentUsername = username;
    }

    @FXML
    public void handleViewAppointments(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewAppointments.fxml"));
            Parent root = loader.load();

            ViewAppointmentsController controller = loader.getController();
            controller.setUsername(currentUsername); // ✅ Pass username

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("MediCore - View Appointments");
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

            // ✅ Pass username to PatientAppointmentController
            PatientAppointmentController appointmentController = loader.getController();
            appointmentController.setUsername(currentUsername); // Make sure currentUsername is set earlier

            Stage stage = (Stage) appointmentButton.getScene().getWindow();
            stage.setScene(new Scene(appointmentRoot));
            stage.setTitle("MediCore - Book Appointment");
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
            Scene signupScene = new Scene(signupRoot);
            stage.setScene(signupScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}