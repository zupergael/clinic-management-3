package com.example.clinicmanagement3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ViewAppointmentsController implements Initializable {

    @FXML
    private TableView<Appointment> appointmentTable;

    @FXML
    private TableColumn<Appointment, String> dateColumn;

    @FXML
    private TableColumn<Appointment, String> timeColumn;

    @FXML
    private TableColumn<Appointment, String> serviceColumn;

    @FXML
    private TableColumn<Appointment, Void> actionColumn;

    @FXML
    private Button logoutButton;

    @FXML
    private Button backButton;

    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    private String currentUsername;

    public void setUsername(String username) {
        this.currentUsername = username;
        System.out.println("Appointments received username: " + username);
        loadAppointments();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));

        addActionButtonsToTable();
    }

    private void loadAppointments() {
        appointments.clear();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
            SELECT a.appointment_date, a.appointment_time, a.service, u.username, u.full_name
            FROM appointments a
            JOIN user u ON a.user_id = u.id
            WHERE u.username = ?
            ORDER BY a.appointment_date ASC, a.appointment_time ASC
        """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentUsername);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                appointments.add(new Appointment(
                        rs.getString("appointment_date"),
                        rs.getString("appointment_time"),
                        rs.getString("service"),
                        rs.getString("username"),
                        rs.getString("full_name")
                ));
            }

            appointmentTable.setItems(appointments);

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addActionButtonsToTable() {
        Callback<TableColumn<Appointment, Void>, TableCell<Appointment, Void>> cellFactory = param -> new TableCell<>() {
            private final Button cancelBtn = new Button("Cancel");
            private final Button rescheduleBtn = new Button("Reschedule");
            private final HBox buttonBox = new HBox(10, cancelBtn, rescheduleBtn);

            {
                cancelBtn.setOnAction(event -> {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    handleCancel(appointment);
                });

                rescheduleBtn.setOnAction(event -> {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    handleReschedule(appointment);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonBox);
            }
        };

        actionColumn.setCellFactory(cellFactory);
    }

    private void handleCancel(Appointment appointment) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
            DELETE FROM appointments
            WHERE appointment_date = ? AND appointment_time = ? AND user_id = (
            SELECT id FROM user WHERE username = ?
                )
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, appointment.getDate());
            stmt.setString(2, appointment.getTime());
            stmt.setString(3, appointment.getUsername());
            stmt.executeUpdate();
            appointments.remove(appointment);
            showAlert("Success", "Appointment cancelled.");
        } catch (SQLException e) {
            showAlert("Error", "Failed to cancel appointment.");
            e.printStackTrace();
        }
    }

    private void handleReschedule(Appointment appointment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PatientAppointment.fxml"));
            Parent root = loader.load();

            PatientAppointmentController controller = loader.getController();
            controller.setUsername(currentUsername);
            controller.loadAppointmentForReschedule(appointment);

            Stage stage = (Stage) appointmentTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Clinic Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load reschedule screen.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
}