package com.example.clinicmanagement3;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class LabResultController implements Initializable {

    @FXML
    private TableView<LabResult> labResultTable;

    @FXML
    private TableColumn<LabResult, String> servicesColumn;

    @FXML
    private TableColumn<LabResult, Void> resultsColumn;

    @FXML
    private Button logoutButton;

    @FXML
    private Button backButton;

    private String currentUsername;

    public void setUsername(String username) {
        this.currentUsername = username;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Bind service column to LabResult.service
        servicesColumn.setCellValueFactory(new PropertyValueFactory<>("service"));

        // Add "View" button to each row in results column
        resultsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewButton = new Button("View");

            {
                viewButton.setOnAction(event -> {
                    LabResult result = getTableView().getItems().get(getIndex());
                    handleView(result);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewButton);
            }
        });

        // Sample data
        labResultTable.getItems().addAll(
                new LabResult("Blood Test"),
                new LabResult("X-Ray"),
                new LabResult("Urinalysis")
        );
    }

    private void handleView(LabResult result) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LabResultView.fxml"));
            Parent root = loader.load();

            // Optional: only call setLabResult if the controller supports it
            Object controllerObj = loader.getController();
            if (controllerObj instanceof LabResultController controller) {
                controller.setLabResult(result); // Safe call
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Lab Result Details");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLabResult(LabResult result) {
        System.out.println("Selected service: " + result.getService());
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PatientDashboard.fxml"));
            Parent root = loader.load();

            PatientDashboardController controller = loader.getController();
            controller.setUsername(currentUsername);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Clinic Management");
            stage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Signup.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Clinic Management");
            stage.show();

            // Close current window
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}