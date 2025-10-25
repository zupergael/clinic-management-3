module com.example.clinicmanagement3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    requires java.sql;
    requires java.desktop;
    requires javafx.graphics; // ✅ Add this line

    opens com.example.clinicmanagement3 to javafx.fxml;
    exports com.example.clinicmanagement3;
}