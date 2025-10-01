package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Make is required")
    @Size(min = 2, max = 50, message = "Make must be between 2 and 50 characters")
    @Column(nullable = false, length = 50)
    private String make;

    @NotBlank(message = "Model is required")
    @Size(min = 2, max = 50, message = "Model must be between 2 and 50 characters")
    @Column(nullable = false, length = 50)
    private String model;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2050, message = "Year must be 2050 or earlier")
    @Column(name = "car_year", nullable = false)
    private Integer year;

    @NotBlank(message = "Car type is required")
    @Column(name = "car_type", nullable = false, length = 50)
    private String carType; // SUV, Sedan, Hatchback, etc.

    @NotBlank(message = "Transmission is required")
    @Column(nullable = false, length = 20)
    private String transmission; // Manual, Automatic

    @NotBlank(message = "Fuel type is required")
    @Column(name = "fuel_type", nullable = false, length = 20)
    private String fuelType; // Petrol, Diesel, Electric, Hybrid

    @NotNull(message = "Seating capacity is required")
    @Min(value = 1, message = "Seating capacity must be at least 1")
    @Max(value = 15, message = "Seating capacity cannot exceed 15")
    @Column(nullable = false)
    private Integer seatingCapacity;

    @NotNull(message = "Price per day is required")
    @DecimalMin(value = "0.01", message = "Price per day must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
    @Column(name = "price_per_day", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    @Column(nullable = false)
    private Boolean available = true;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(length = 1000)
    private String description;

    @NotBlank(message = "Color is required")
    @Column(nullable = false, length = 20)
    private String color;

    @NotBlank(message = "License plate is required")
    @Column(name = "license_plate", unique = true, length = 20)
    private String licensePlate;

    @NotNull(message = "Mileage is required")
    @Min(value = 1, message = "Mileage must be at least 1 km/l")
    @Column(nullable = false)
    private Integer mileage; // km per liter

    @Column(name = "air_conditioning")
    private Boolean airConditioning = true;

    @Column(name = "gps_navigation")
    private Boolean gpsNavigation = false;

    @Column(name = "bluetooth_connectivity")
    private Boolean bluetoothConnectivity = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Car owner (admin/user who listed the car)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    // Whether the car is disabled by admin (separate from availability)
    @Column(name = "is_disabled", nullable = false)
    private Boolean disabled = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Car() {}

    public Car(String make, String model, Integer year, String carType, String transmission,
               String fuelType, Integer seatingCapacity, BigDecimal pricePerDay, String color,
               String licensePlate, Integer mileage, String imageUrl, String description) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.carType = carType;
        this.transmission = transmission;
        this.fuelType = fuelType;
        this.seatingCapacity = seatingCapacity;
        this.pricePerDay = pricePerDay;
        this.color = color;
        this.licensePlate = licensePlate;
        this.mileage = mileage;
        this.imageUrl = imageUrl;
        this.description = description;
        this.available = true;
        this.airConditioning = true;
        this.gpsNavigation = false;
        this.bluetoothConnectivity = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getCarType() { return carType; }
    public void setCarType(String carType) { this.carType = carType; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public Integer getSeatingCapacity() { return seatingCapacity; }
    public void setSeatingCapacity(Integer seatingCapacity) { this.seatingCapacity = seatingCapacity; }

    public BigDecimal getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(BigDecimal pricePerDay) { this.pricePerDay = pricePerDay; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }

    public Boolean getAirConditioning() { return airConditioning; }
    public void setAirConditioning(Boolean airConditioning) { this.airConditioning = airConditioning; }

    public Boolean getGpsNavigation() { return gpsNavigation; }
    public void setGpsNavigation(Boolean gpsNavigation) { this.gpsNavigation = gpsNavigation; }

    public Boolean getBluetoothConnectivity() { return bluetoothConnectivity; }
    public void setBluetoothConnectivity(Boolean bluetoothConnectivity) { this.bluetoothConnectivity = bluetoothConnectivity; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public Boolean getDisabled() { return disabled; }
    public void setDisabled(Boolean disabled) { this.disabled = disabled; }
}
