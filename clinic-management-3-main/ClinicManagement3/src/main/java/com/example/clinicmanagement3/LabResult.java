package com.example.clinicmanagement3;

import javafx.beans.property.SimpleStringProperty;

public class LabResult {
    private final SimpleStringProperty service;

    public LabResult(String service) {
        this.service = new SimpleStringProperty(service);
    }

    public String getService() {
        return service.get();
    }

    public SimpleStringProperty serviceProperty() {
        return service;
    }
}