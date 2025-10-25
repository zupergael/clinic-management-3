package com.example.clinicmanagement3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PatientAppointmentController implements Initializable {

    @FXML
    private ComboBox<String> timeComboBox;

    @FXML
    private ComboBox<String> serviceComboBox;

    @FXML
    private Button backButton;

    @FXML
    private Button logoutButton;

    @FXML
    private DatePicker appointmentDatePicker;

    @FXML
    private Label warningLabel;

    @FXML
    private Label successLabel;

    @FXML
    private Label rescheduleWarningLabel;

    @FXML
    private TableView<Appointment> appointmentTable;

    @FXML
    private TableColumn<Appointment, String> dateColumn;

    @FXML
    private TableColumn<Appointment, String> timeColumn;

    @FXML
    private TableColumn<Appointment, String> serviceColumn;

    private final ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    private String currentUsername;

    private Appointment appointmentToReschedule = null;

    public void setUsername(String username) {
        this.currentUsername = username;
        System.out.println("Booking screen received username: " + username);
        loadAppointmentsFromDatabase();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timeComboBox.setPromptText("Select Time");
        timeComboBox.getItems().addAll(
                "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
                "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM"
        );

        serviceComboBox.setPromptText("Select Service");
        serviceComboBox.getItems().addAll(
                "General Consultation",
                "Dental Checkup",
                "Pediatric Visit",
                "Laboratory Test",
                "Vaccination",
                "Follow-up Appointment"
        );

        dateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDate()));
        timeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTime()));
        serviceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getService()));

        appointmentTable.setItems(appointmentList);
    }

    private void loadAppointmentsFromDatabase() {
        appointmentList.clear();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
                SELECT a.appointment_date, a.appointment_time, a.service, a.full_name
                FROM appointments a
                JOIN user u ON a.user_id = u.id
                WHERE u.username = ?
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentUsername);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String date = rs.getString("appointment_date");
                String time = rs.getString("appointment_time");
                String service = rs.getString("service");
                String fullName = rs.getString("full_name");

                appointmentList.add(new Appointment(date, time, service, currentUsername, fullName));
            }

            System.out.println("Appointments loaded for: " + currentUsername);
        } catch (SQLException e) {
            e.printStackTrace();
            warningLabel.setStyle("-fx-text-fill: red;");
            warningLabel.setText("Failed to load appointments.");
        }
    }

    @FXML
    protected void handleBookAppointment() {
        String selectedTime = timeComboBox.getValue();
        String selectedService = serviceComboBox.getValue();
        var selectedDate = appointmentDatePicker.getValue();

        timeComboBox.setStyle("");
        serviceComboBox.setStyle("");
        appointmentDatePicker.setStyle("");
        warningLabel.setText("");
        successLabel.setText("");

        boolean hasError = false;

        if (selectedTime == null) {
            timeComboBox.setStyle("-fx-border-color: red;");
            hasError = true;
        }

        if (selectedService == null) {
            serviceComboBox.setStyle("-fx-border-color: red;");
            hasError = true;
        }

        if (selectedDate == null) {
            appointmentDatePicker.setStyle("-fx-border-color: red;");
            hasError = true;
        }

        if (currentUsername == null || currentUsername.isEmpty()) {
            warningLabel.setStyle("-fx-text-fill: red;");
            warningLabel.setText("User session error: username not set.");
            System.out.println("Booking failed: currentUsername is null.");
            return;
        }

        if (hasError) {
            warningLabel.setStyle("-fx-text-fill: red;");
            warningLabel.setText("Please fill in all fields before booking.");
        } else {
            String formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            try (Connection conn = DBConnection.getConnection()) {
                String getUserSql = "SELECT id, full_name FROM user WHERE username = ?";
                PreparedStatement getUserStmt = conn.prepareStatement(getUserSql);
                getUserStmt.setString(1, currentUsername);
                ResultSet rs = getUserStmt.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String fullName = rs.getString("full_name");

                    if (appointmentToReschedule != null) {
                        // Update existing appointment
                        String updateSql = """
                            UPDATE appointments
                            SET appointment_date = ?, appointment_time = ?, service = ?
                            WHERE user_id = ? AND appointment_date = ? AND appointment_time = ?
                            """;
                        PreparedStatement stmt = conn.prepareStatement(updateSql);
                        stmt.setString(1, formattedDate);
                        stmt.setString(2, selectedTime);
                        stmt.setString(3, selectedService);
                        stmt.setInt(4, userId);
                        stmt.setString(5, appointmentToReschedule.getDate());
                        stmt.setString(6, appointmentToReschedule.getTime());
                        stmt.executeUpdate();

                        successLabel.setText("Appointment rescheduled successfully!");
                        System.out.println("Rescheduled appointment for user_id=" + userId);

                        appointmentToReschedule = null; // Clear after update
                        loadAppointmentsFromDatabase(); // Refresh table instead of adding manually
                    } else {
                        // Insert new appointment
                        String insertSql = "INSERT INTO appointments (user_id, full_name, appointment_date, appointment_time, service) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement stmt = conn.prepareStatement(insertSql);
                        stmt.setInt(1, userId);
                        stmt.setString(2, fullName);
                        stmt.setString(3, formattedDate);
                        stmt.setString(4, selectedTime);
                        stmt.setString(5, selectedService);
                        stmt.executeUpdate();

                        System.out.println("New appointment booked for user_id=" + userId);

                        Appointment appointment = new Appointment(formattedDate, selectedTime, selectedService, currentUsername, fullName);
                        loadAppointmentsFromDatabase();
                    }

                    successLabel.setStyle("-fx-text-fill: green;");
                    successLabel.setText("Appointment saved successfully!");

                    appointmentDatePicker.setValue(null);
                    timeComboBox.setValue(null);
                    serviceComboBox.setValue(null);
                } else {
                    warningLabel.setStyle("-fx-text-fill: red;");
                    warningLabel.setText("User not found in database.");
                    System.out.println("Booking failed: user not found for username = " + currentUsername);
                }
            } catch (SQLException e) {
                warningLabel.setStyle("-fx-text-fill: red;");
                warningLabel.setText("Database error: " + e.getMessage());
                e.printStackTrace();
            }
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
            warningLabel.setStyle("-fx-text-fill: red;");
            warningLabel.setText("Failed to load signup screen.");
        }
    }

    @FXML
    protected void handleBackButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PatientDashboard.fxml"));
            Parent dashboardRoot = loader.load();

            PatientDashboardController controller = loader.getController();
            controller.setUsername(currentUsername);

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAppointmentForReschedule(Appointment appointment) {
        this.appointmentToReschedule = appointment;

        appointmentDatePicker.setValue(java.time.LocalDate.parse(appointment.getDate()));
        timeComboBox.setValue(appointment.getTime());
        serviceComboBox.setValue(appointment.getService());

        rescheduleWarningLabel.setStyle("-fx-text-fill: blue;");
        rescheduleWarningLabel.setText("Editing appointment for " + appointment.getDate() + " at " + appointment.getTime());

        successLabel.setText("");
    }
}