package com.sunrise.dental.service;

import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {

    List<Appointment> getAllAppointments();

    Optional<Appointment> getAppointmentById(Long appointmentId);

    List<Appointment> getAppointmentsByDate(LocalDate date);

    List<Appointment> getAppointmentsForDentist(Long dentistId);

    List<User> getAvailableDentists();

    Long createAppointment(Appointment appointment);

    void updateAppointment(Appointment appointment);

    void cancelAppointment(Long appointmentId);

    void completeAppointment(Long appointmentId);
}
