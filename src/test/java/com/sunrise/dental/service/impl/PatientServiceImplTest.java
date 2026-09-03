package com.sunrise.dental.service.impl;

import com.sunrise.dental.dao.PatientDao;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientDao patientDao;

    private PatientServiceImpl patientService;

    @BeforeEach
    void setUp() {
        patientService = new PatientServiceImpl(patientDao);
    }

    @Test
    void createPatientGeneratesPatientNumberAndPersists() {
        when(patientDao.findLatestPatientNumber()).thenReturn(null);
        when(patientDao.insert(any(Patient.class))).thenReturn(100L);

        Patient patient = validPatient();
        Long id = patientService.createPatient(patient);

        assertEquals(100L, id);
        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientDao).insert(captor.capture());
        Patient saved = captor.getValue();
        assertEquals(true, saved.isActive());
        assertEquals(true, saved.getPatientNumber().startsWith("PAT-"));
    }

    @Test
    void createPatientRejectsMissingFirstName() {
        Patient patient = validPatient();
        patient.setFirstName(" ");
        assertThrows(ValidationException.class, () -> patientService.createPatient(patient));
    }

    @Test
    void deactivatePatientRequiresId() {
        assertThrows(ValidationException.class, () -> patientService.deactivatePatient(null));
    }

    private Patient validPatient() {
        Patient patient = new Patient();
        patient.setFirstName("Nimal");
        patient.setLastName("Fernando");
        patient.setDateOfBirth(LocalDate.of(1990, 5, 15));
        patient.setGender("MALE");
        patient.setPhone("0771234567");
        return patient;
    }
}
