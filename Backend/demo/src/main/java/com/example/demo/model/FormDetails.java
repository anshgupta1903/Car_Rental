package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "form_details")
public class FormDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Column(name = "full_name", nullable = true, length = 100)
    private String fullName;

    @Email(message = "Please provide a valid email address")
    @Column(nullable = true, length = 150)
    private String email;

    @NotBlank(message = "Pickup location is required")
    @Size(min = 2, max = 200, message = "Pickup location must be between 2 and 200 characters")
    @Column(name = "pickup_location", nullable = false, length = 200)
    private String pickupLocation;

    @Size(max = 200, message = "Dropoff location cannot exceed 200 characters")
    @Column(name = "dropoff_location", nullable = true, length = 200)
    private String dropoffLocation;

    @NotNull(message = "Pickup date and time is required")
    @Column(name = "pickup_datetime", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime pickupDateTime;

    @Column(name = "return_datetime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime returnDateTime;

    @Column(name = "car_type", length = 50)
    private String carType;

    // Link to the specific car being booked
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    // Booking status
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", length = 20, nullable = false)
    private BookingStatus bookingStatus = BookingStatus.PENDING; // PENDING, APPROVED, REJECTED, COMPLETED, CANCELLED
    
    // Admin approval details
    @Column(name = "approved_by_admin_id")
    private Long approvedByAdminId;
    
    @Column(name = "admin_notes", length = 500)
    private String adminNotes;
    
    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    // Total amount for the booking
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Total amount format is invalid")
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // Number of days for the rental
    @Min(value = 1, message = "Rental days must be at least 1")
    @Column(name = "rental_days")
    private Integer rentalDays;

    // Phone number for contact
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    // Additional notes or requirements
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public String getDropoffLocation() { return dropoffLocation; }
    public void setDropoffLocation(String dropoffLocation) { this.dropoffLocation = dropoffLocation; }

    public LocalDateTime getPickupDateTime() { return pickupDateTime; }
    public void setPickupDateTime(LocalDateTime pickupDateTime) { this.pickupDateTime = pickupDateTime; }

    public LocalDateTime getReturnDateTime() { return returnDateTime; }
    public void setReturnDateTime(LocalDateTime returnDateTime) { this.returnDateTime = returnDateTime; }

    public String getCarType() { return carType; }
    public void setCarType(String carType) { this.carType = carType; }


    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }

    public BookingStatus getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(BookingStatus bookingStatus) { this.bookingStatus = bookingStatus; }
    
    public Long getApprovedByAdminId() { return approvedByAdminId; }
    public void setApprovedByAdminId(Long approvedByAdminId) { this.approvedByAdminId = approvedByAdminId; }
    
    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
    
    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public Integer getRentalDays() { return rentalDays; }
    public void setRentalDays(Integer rentalDays) { this.rentalDays = rentalDays; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
