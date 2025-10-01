package com.example.demo.config;

import com.example.demo.model.Car;
import com.example.demo.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CarRepository carRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if cars already exist
        long carCount = carRepository.count();
        System.out.println("Current car count in database: " + carCount);
        
        if (carCount == 0) {
            System.out.println("No cars found. Initializing sample cars...");
            initializeCars();
        } else {
            System.out.println("Cars already exist in database. Skipping initialization.");
            // List ALL cars for debugging
            System.out.println("=== ALL CARS IN DATABASE ===");
            carRepository.findAll().forEach(car -> 
                System.out.println("Car ID=" + car.getId() + ", Make=" + car.getMake() + 
                                 ", Model=" + car.getModel() + ", Available=" + car.getAvailable() + 
                                 ", Price=" + car.getPricePerDay()));
            System.out.println("=== END OF CAR LIST ===");
        }
    }

    private void initializeCars() {
        List<Car> cars = Arrays.asList(
            new Car("Toyota", "Camry", 2023, "Sedan", "Automatic", "Petrol", 5, 
                    new BigDecimal("2500.00"), "White", "TN01AB1234", 15, 
                    "https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80", 
                    "Comfortable and reliable sedan perfect for city and highway driving."),
                    
            new Car("Honda", "CR-V", 2023, "SUV", "Automatic", "Petrol", 7, 
                    new BigDecimal("3500.00"), "Silver", "TN02CD5678", 12, 
                    "https://images.unsplash.com/photo-1605559424843-9e4c228bf1c2?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80", 
                    "Spacious SUV ideal for family trips and off-road adventures."),
                    
            new Car("Maruti", "Swift", 2022, "Hatchback", "Manual", "Petrol", 5, 
                    new BigDecimal("1800.00"), "Red", "TN03EF9012", 18, 
                    "https://images.unsplash.com/photo-1503736334956-4c8f8e92946d?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80", 
                    "Compact and fuel-efficient hatchback perfect for city commuting."),
                    
            new Car("Hyundai", "Creta", 2023, "SUV", "Automatic", "Diesel", 5, 
                    new BigDecimal("3200.00"), "Blue", "TN04GH3456", 14, 
                    "https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80", 
                    "Modern SUV with advanced features and excellent fuel economy."),
                    
            new Car("BMW", "X3", 2023, "SUV", "Automatic", "Petrol", 5, 
                    new BigDecimal("5500.00"), "Black", "TN05IJ7890", 10, 
                    "https://images.unsplash.com/photo-1555215695-3004980ad54e?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80", 
                    "Luxury SUV with premium features and powerful performance."),
                    
            new Car("Mercedes", "C-Class", 2023, "Sedan", "Automatic", "Petrol", 5, 
                    new BigDecimal("6000.00"), "White", "TN06KL1234", 9, 
                    "https://images.unsplash.com/photo-1617814076367-b759c7d7e738?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80", 
                    "Elegant luxury sedan with cutting-edge technology and comfort."),
                    
            new Car("Tata", "Nexon", 2022, "SUV", "Manual", "Electric", 5, 
                    new BigDecimal("2800.00"), "Green", "TN07MN5678", 25, 
                    "https://images.unsplash.com/photo-1593941707874-ef25b8b4a92b?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80", 
                    "Eco-friendly electric SUV with zero emissions and modern design."),
                    
            new Car("Ford", "EcoSport", 2022, "SUV", "Automatic", "Petrol", 5, 
                    new BigDecimal("2700.00"), "Orange", "TN08OP9012", 13, 
                    "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80", 
                    "Compact SUV with sporty design and great handling capabilities."),
                    
            new Car("Volkswagen", "Polo", 2023, "Hatchback", "Automatic", "Petrol", 5, 
                    new BigDecimal("2200.00"), "Gray", "TN09QR3456", 16, 
                    "https://images.unsplash.com/photo-1502877338535-766e1452684a?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80", 
                    "Premium hatchback with European engineering and style."),
                    
            new Car("Mahindra", "Scorpio", 2022, "SUV", "Manual", "Diesel", 7, 
                    new BigDecimal("3800.00"), "Brown", "TN10ST7890", 11, 
                    "https://images.unsplash.com/photo-1544636331-e26879cd4d9b?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80", 
                    "Rugged SUV built for tough terrains and long journeys."),
                    
            new Car("Kia", "Seltos", 2023, "SUV", "Automatic", "Petrol", 5, 
                    new BigDecimal("3300.00"), "Silver", "TN11UV1234", 13, 
                    "https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80", 
                    "Stylish SUV with advanced safety features and connectivity."),
                    
            new Car("Nissan", "Magnite", 2022, "SUV", "Manual", "Petrol", 5, 
                    new BigDecimal("2400.00"), "Yellow", "TN12WX5678", 15, 
                    "https://images.unsplash.com/photo-1600712242805-5f78671b24da?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80", 
                    "Compact SUV with bold design and efficient performance.")
        );

        // Set additional features for some cars
        cars.forEach(car -> {
            if (car.getPricePerDay().compareTo(new BigDecimal("4000")) > 0) {
                car.setGpsNavigation(true);
                car.setBluetoothConnectivity(true);
            } else if (car.getPricePerDay().compareTo(new BigDecimal("2500")) > 0) {
                car.setBluetoothConnectivity(true);
            }
        });

        carRepository.saveAll(cars);
        System.out.println("Initialized " + cars.size() + " cars in the database");
    }
}