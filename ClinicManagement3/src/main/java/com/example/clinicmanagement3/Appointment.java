package com.example.clinicmanagement3;

public class Appointment {
    private final String date;
    private final String time;
    private final String service;
    private final String username;   // Used for database linkage
    private final String fullName;   // Used for display

    public Appointment(String date, String time, String service, String username, String fullName) {
        this.date = date;
        this.time = time;
        this.service = service;
        this.username = username;
        this.fullName = fullName;
    }

    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getService() { return service; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
}