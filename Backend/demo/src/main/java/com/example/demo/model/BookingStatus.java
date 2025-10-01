package com.example.demo.model;

public enum BookingStatus {
    PENDING("Pending Admin Approval"),
    APPROVED("Approved by Admin"),
    REJECTED("Rejected by Admin"),
    COMPLETED("Booking Completed"),
    CANCELLED("Booking Cancelled");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}