package com.sunrise.dental.service;

import com.sunrise.dental.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientService {

    List<Patient> getAllPatients();

    List<Patient> getActivePatients();

    Optional<Patient> getPatientById(Long patientId);

    List<Patient> searchPatients(String keyword);

    Long createPatient(Patient patient);

    void updatePatient(Patient patient);

    void deactivatePatient(Long patientId);
}
