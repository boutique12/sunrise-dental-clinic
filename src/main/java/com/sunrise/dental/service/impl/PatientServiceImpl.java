package com.sunrise.dental.service.impl;

import com.sunrise.dental.dao.PatientDao;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.model.Patient;
import com.sunrise.dental.service.PatientService;
import com.sunrise.dental.util.NumberGenerator;

import java.util.List;
import java.util.Optional;

public class PatientServiceImpl implements PatientService {

    private final PatientDao patientDao;

    public PatientServiceImpl(PatientDao patientDao) {
        this.patientDao = patientDao;
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientDao.findAll();
    }

    @Override
    public List<Patient> getActivePatients() {
        return patientDao.findAllActive();
    }

    @Override
    public Optional<Patient> getPatientById(Long patientId) {
        return patientDao.findById(patientId);
    }

    @Override
    public List<Patient> searchPatients(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getActivePatients();
        }
        return patientDao.searchByNameOrPhone(keyword.trim());
    }

    @Override
    public Long createPatient(Patient patient) {
        validatePatient(patient, false);
        String latest = patientDao.findLatestPatientNumber();
        int sequence = NumberGenerator.extractSequence(latest, "PAT");
        patient.setPatientNumber(NumberGenerator.generatePatientNumber(sequence));
        patient.setActive(true);
        return patientDao.insert(patient);
    }

    @Override
    public void updatePatient(Patient patient) {
        validatePatient(patient, true);
        if (patient.getPatientId() == null) {
            throw new ValidationException("Patient id is required for update");
        }
        patientDao.update(patient);
    }

    @Override
    public void deactivatePatient(Long patientId) {
        if (patientId == null) {
            throw new ValidationException("Patient id is required");
        }
        patientDao.deactivate(patientId);
    }

    private void validatePatient(Patient patient, boolean updating) {
        if (patient == null) {
            throw new ValidationException("Patient data is required");
        }
        if (patient.getFirstName() == null || patient.getFirstName().isBlank()) {
            throw new ValidationException("First name is required");
        }
        if (patient.getLastName() == null || patient.getLastName().isBlank()) {
            throw new ValidationException("Last name is required");
        }
        if (patient.getDateOfBirth() == null) {
            throw new ValidationException("Date of birth is required");
        }
        if (patient.getGender() == null || patient.getGender().isBlank()) {
            throw new ValidationException("Gender is required");
        }
        if (patient.getPhone() == null || patient.getPhone().isBlank()) {
            throw new ValidationException("Phone number is required");
        }
        if (updating && patient.getPatientId() == null) {
            throw new ValidationException("Patient id is required");
        }
    }
}
