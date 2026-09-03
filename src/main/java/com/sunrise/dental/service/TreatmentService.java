package com.sunrise.dental.service;

import com.sunrise.dental.model.Treatment;

import java.util.List;
import java.util.Optional;

public interface TreatmentService {

    List<Treatment> getAllTreatments();

    Optional<Treatment> getTreatmentById(Long treatmentId);

    List<Treatment> getTreatmentsForDentist(Long dentistId);

    Long createTreatment(Treatment treatment);

    void updateTreatment(Treatment treatment);

    void deleteTreatment(Long treatmentId);
}
