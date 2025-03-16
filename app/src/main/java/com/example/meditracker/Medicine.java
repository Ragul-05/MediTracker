package com.example.meditracker;

public class Medicine {
    private String name;
    private String schedule;
    private String startDate;
    private String endDate;
    private String medicineId;

    public Medicine() {
        // Default constructor required for Firebase
    }

    public Medicine(String name, String schedule, String startDate, String endDate, String medicineId) {
        this.name = name;
        this.schedule = schedule;
        this.startDate = startDate;
        this.endDate = endDate;
        this.medicineId = medicineId;
    }

    public String getName() {
        return name;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getMedicineId() {
        return medicineId;
    }
}
