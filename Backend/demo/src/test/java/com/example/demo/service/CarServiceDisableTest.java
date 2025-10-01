package com.example.demo.service;

import com.example.demo.model.Car;
import com.example.demo.model.User;
import com.example.demo.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarServiceDisableTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarService carService;

    private Car testCar;
    private User testOwner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testOwner = new User();
        testOwner.setId(1L);
        testOwner.setUsername("testowner");
        testOwner.setEmail("owner@test.com");
        
        testCar = new Car();
        testCar.setId(1L);
        testCar.setMake("Toyota");
        testCar.setModel("Camry");
        testCar.setYear(2023);
        testCar.setCarType("Sedan");
        testCar.setTransmission("Automatic");
        testCar.setFuelType("Petrol");
        testCar.setSeatingCapacity(5);
        testCar.setPricePerDay(new BigDecimal("50.00"));
        testCar.setColor("White");
        testCar.setLicensePlate("ABC123");
        testCar.setMileage(15);
        testCar.setAvailable(true);
        testCar.setDisabled(false);
        testCar.setOwner(testOwner);
    }

    @Test
    void testDisableCar() {
        // Given
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        // When
        boolean result = carService.disableCar(1L);

        // Then
        assertTrue(result);
        assertTrue(testCar.getDisabled());
        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).save(testCar);
    }

    @Test
    void testEnableCar() {
        // Given
        testCar.setDisabled(true);
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        // When
        boolean result = carService.enableCar(1L);

        // Then
        assertTrue(result);
        assertFalse(testCar.getDisabled());
        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).save(testCar);
    }

    @Test
    void testDisableCarNotFound() {
        // Given
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        boolean result = carService.disableCar(1L);

        // Then
        assertFalse(result);
        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, never()).save(any());
    }

    @Test
    void testIsCarAvailableConsidersDisabledStatus() {
        // Test case 1: Car is available but disabled
        testCar.setAvailable(true);
        testCar.setDisabled(true);
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        
        assertFalse(carService.isCarAvailable(1L));

        // Test case 2: Car is available and enabled
        testCar.setAvailable(true);
        testCar.setDisabled(false);
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        
        assertTrue(carService.isCarAvailable(1L));

        // Test case 3: Car is not available but enabled
        testCar.setAvailable(false);
        testCar.setDisabled(false);
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        
        assertFalse(carService.isCarAvailable(1L));
    }

    @Test
    void testCanUserDisableCar() {
        // Given
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        // Test case 1: User is the owner
        assertTrue(carService.canUserDisableCar(1L, 1L));

        // Test case 2: User is not the owner
        assertFalse(carService.canUserDisableCar(1L, 2L));

        // Test case 3: Car not found
        when(carRepository.findById(999L)).thenReturn(Optional.empty());
        assertFalse(carService.canUserDisableCar(999L, 1L));
    }

    @Test
    void testGetAllAvailableCarsShouldExcludeDisabled() {
        // Given
        Car enabledCar = new Car();
        enabledCar.setId(1L);
        enabledCar.setAvailable(true);
        enabledCar.setDisabled(false);

        when(carRepository.findByAvailableTrueAndDisabledFalse()).thenReturn(List.of(enabledCar));

        // When
        List<Car> availableCars = carService.getAllAvailableCars();

        // Then
        assertEquals(1, availableCars.size());
        assertFalse(availableCars.get(0).getDisabled());
        verify(carRepository, times(1)).findByAvailableTrueAndDisabledFalse();
    }
}