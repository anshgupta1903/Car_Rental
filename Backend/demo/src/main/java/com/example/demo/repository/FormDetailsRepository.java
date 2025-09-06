package com.example.demo.repository;

import com.example.demo.model.FormDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormDetailsRepository extends JpaRepository<FormDetails, Long> {
}
