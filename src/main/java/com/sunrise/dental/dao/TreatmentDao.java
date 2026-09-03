package com.sunrise.dental.dao;

import com.sunrise.dental.model.Treatment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TreatmentDao {

    List<Treatment> findAll();

    Optional<Treatment> findById(Long treatmentId);

    Optional<Treatment> findByAppointmentId(Long appointmentId);

    List<Treatment> findByDentistId(Long dentistId);

    Long insert(Treatment treatment);

    void update(Treatment treatment);

    void delete(Long treatmentId);

    int countByDate(LocalDate date);
}
