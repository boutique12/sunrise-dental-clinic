package com.sunrise.dental.dao;

import com.sunrise.dental.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientDao {

    List<Patient> findAll();

    List<Patient> findAllActive();

    Optional<Patient> findById(Long patientId);

    Optional<Patient> findByPatientNumber(String patientNumber);

    List<Patient> searchByNameOrPhone(String keyword);

    String findLatestPatientNumber();

    Long insert(Patient patient);

    void update(Patient patient);

    void deactivate(Long patientId);

    int countCreatedOnDate(java.time.LocalDate date);
}
