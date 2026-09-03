package com.sunrise.dental.service.impl;

import com.sunrise.dental.dao.AppointmentDao;
import com.sunrise.dental.dao.UserDao;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.User;
import com.sunrise.dental.service.AppointmentService;
import com.sunrise.dental.util.AppConstants;
import com.sunrise.dental.util.ClinicScheduleUtil;
import com.sunrise.dental.util.NumberGenerator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentDao appointmentDao;
    private final UserDao userDao;

    public AppointmentServiceImpl(AppointmentDao appointmentDao, UserDao userDao) {
        this.appointmentDao = appointmentDao;
        this.userDao = userDao;
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentDao.findAll();
    }

    @Override
    public Optional<Appointment> getAppointmentById(Long appointmentId) {
        return appointmentDao.findById(appointmentId);
    }

    @Override
    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return appointmentDao.findByDate(date);
    }

    @Override
    public List<Appointment> getAppointmentsForDentist(Long dentistId) {
        return appointmentDao.findByDentistId(dentistId);
    }

    @Override
    public List<User> getAvailableDentists() {
        return userDao.findByRole(AppConstants.ROLE_DENTIST);
    }

    @Override
    public Long createAppointment(Appointment appointment) {
        validateAppointment(appointment, null);
        if (appointment.getStatus() == null || appointment.getStatus().isBlank()) {
            appointment.setStatus(AppConstants.STATUS_SCHEDULED);
        }
        String latest = appointmentDao.findLatestAppointmentNumber();
        int sequence = NumberGenerator.extractSequence(latest, "APT");
        appointment.setAppointmentNumber(NumberGenerator.generateAppointmentNumber(sequence));
        return appointmentDao.insert(appointment);
    }

    @Override
    public void updateAppointment(Appointment appointment) {
        validateAppointment(appointment, appointment.getAppointmentId());
        appointmentDao.update(appointment);
    }

    @Override
    public void cancelAppointment(Long appointmentId) {
        if (appointmentId == null) {
            throw new ValidationException("Appointment id is required");
        }
        appointmentDao.updateStatus(appointmentId, AppConstants.STATUS_CANCELLED);
    }

    @Override
    public void completeAppointment(Long appointmentId) {
        if (appointmentId == null) {
            throw new ValidationException("Appointment id is required");
        }
        appointmentDao.updateStatus(appointmentId, AppConstants.STATUS_COMPLETED);
    }

    private void validateAppointment(Appointment appointment, Long excludeId) {
        if (appointment == null) {
            throw new ValidationException("Appointment data is required");
        }
        if (appointment.getPatientId() == null) {
            throw new ValidationException("Patient is required");
        }
        if (appointment.getDentistId() == null) {
            throw new ValidationException("Dentist is required");
        }
        if (appointment.getAppointmentDate() == null) {
            throw new ValidationException("Appointment date is required");
        }
        if (appointment.getAppointmentTime() == null) {
            throw new ValidationException("Appointment time is required");
        }
        ClinicScheduleUtil.validateAppointmentTime(appointment.getAppointmentTime());
        if (appointment.getReason() == null || appointment.getReason().isBlank()) {
            throw new ValidationException("Reason is required");
        }
        if (excludeId == null && appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Appointment date cannot be in the past");
        }
        if (appointmentDao.existsForDentistAtTime(
                appointment.getDentistId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                excludeId)) {
            throw new ValidationException("Dentist already has an appointment at this time");
        }
    }
}