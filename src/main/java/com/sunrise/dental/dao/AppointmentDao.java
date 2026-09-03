package com.sunrise.dental.dao;

import com.sunrise.dental.model.Appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentDao {

    List<Appointment> findAll();

    Optional<Appointment> findById(Long appointmentId);

    List<Appointment> findByDate(LocalDate date);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDentistId(Long dentistId);

    List<Appointment> findByStatus(String status);

    String findLatestAppointmentNumber();

    Long insert(Appointment appointment);

    void update(Appointment appointment);

    void updateStatus(Long appointmentId, String status);

    boolean existsForDentistAtTime(Long dentistId, LocalDate date, java.time.LocalTime time, Long excludeId);

    int countByDate(LocalDate date);

    int countByDateAndStatus(LocalDate date, String status);
}
