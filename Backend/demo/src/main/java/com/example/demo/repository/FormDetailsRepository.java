package com.example.demo.repository;

import com.example.demo.model.BookingStatus;
import com.example.demo.model.FormDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormDetailsRepository extends JpaRepository<FormDetails, Long> {
    @Query("SELECT f FROM FormDetails f LEFT JOIN FETCH f.car WHERE f.email = :email ORDER BY f.createdAt DESC")
    List<FormDetails> findByEmailOrderByCreatedAtDesc(@Param("email") String email);
    
    // Admin query methods
    @Query("SELECT f FROM FormDetails f LEFT JOIN FETCH f.car WHERE f.bookingStatus = :bookingStatus ORDER BY f.createdAt DESC")
    List<FormDetails> findByBookingStatusOrderByCreatedAtDesc(@Param("bookingStatus") BookingStatus bookingStatus);
    
    long countByBookingStatus(BookingStatus bookingStatus);
    
    @Query("SELECT f FROM FormDetails f LEFT JOIN FETCH f.car ORDER BY f.createdAt DESC")
    List<FormDetails> findAllWithCar();
    
    // Find bookings for cars owned by a specific user
    @Query("SELECT f FROM FormDetails f LEFT JOIN FETCH f.car WHERE f.car.owner.id = :ownerId ORDER BY f.createdAt DESC")
    List<FormDetails> findByCarOwnerIdOrderByCreatedAtDesc(@Param("ownerId") Long ownerId);
}
