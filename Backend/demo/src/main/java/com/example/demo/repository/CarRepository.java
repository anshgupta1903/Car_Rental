package com.example.demo.repository;

import com.example.demo.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    // Find all available cars
    List<Car> findByAvailableTrue();
    
    // Find all available and enabled cars (not disabled)
    List<Car> findByAvailableTrueAndDisabledFalse();
    
    // Find all enabled cars (not disabled, regardless of availability)
    List<Car> findByDisabledFalse();
    
    // Find cars by owner
    List<Car> findByOwnerId(Long ownerId);

    // Find cars by type
    List<Car> findByCarTypeAndAvailableTrue(String carType);
    
    // Find available and enabled cars by type
    List<Car> findByCarTypeAndAvailableTrueAndDisabledFalse(String carType);

    // Find cars by price range
    @Query("SELECT c FROM Car c WHERE c.available = true AND c.pricePerDay BETWEEN :minPrice AND :maxPrice")
    List<Car> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    // Find cars by seating capacity
    List<Car> findBySeatingCapacityAndAvailableTrue(Integer seatingCapacity);

    // Find cars by transmission type
    List<Car> findByTransmissionAndAvailableTrue(String transmission);

    // Find cars by fuel type
    List<Car> findByFuelTypeAndAvailableTrue(String fuelType);

    // Find cars by make
    List<Car> findByMakeAndAvailableTrue(String make);

    // Search cars by make and model (case insensitive)
    @Query("SELECT c FROM Car c WHERE c.available = true AND c.disabled = false AND " +
           "(LOWER(c.make) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.model) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Car> findByMakeOrModelContainingIgnoreCase(@Param("keyword") String keyword);

    // Complex search with multiple filters
    @Query("SELECT c FROM Car c WHERE c.available = true AND c.disabled = false " +
           "AND (:carType IS NULL OR c.carType = :carType) " +
           "AND (:transmission IS NULL OR c.transmission = :transmission) " +
           "AND (:fuelType IS NULL OR c.fuelType = :fuelType) " +
           "AND (:minPrice IS NULL OR c.pricePerDay >= :minPrice) " +
           "AND (:maxPrice IS NULL OR c.pricePerDay <= :maxPrice) " +
           "AND (:seatingCapacity IS NULL OR c.seatingCapacity >= :seatingCapacity)")
    List<Car> findCarsWithFilters(@Param("carType") String carType,
                                  @Param("transmission") String transmission,
                                  @Param("fuelType") String fuelType,
                                  @Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice,
                                  @Param("seatingCapacity") Integer seatingCapacity);

    // Find car by license plate
    Optional<Car> findByLicensePlate(String licensePlate);

    // Get all distinct car types
    @Query("SELECT DISTINCT c.carType FROM Car c WHERE c.available = true AND c.disabled = false")
    List<String> findDistinctCarTypes();

    // Get all distinct makes
    @Query("SELECT DISTINCT c.make FROM Car c WHERE c.available = true AND c.disabled = false")
    List<String> findDistinctMakes();

    // Get all distinct fuel types
    @Query("SELECT DISTINCT c.fuelType FROM Car c WHERE c.available = true AND c.disabled = false")
    List<String> findDistinctFuelTypes();
}