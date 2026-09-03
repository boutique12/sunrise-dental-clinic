package com.sunrise.dental.service.impl;

import com.sunrise.dental.dao.AppointmentDao;
import com.sunrise.dental.dao.TreatmentDao;
import com.sunrise.dental.dao.TreatmentDetailDao;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.model.Treatment;
import com.sunrise.dental.model.TreatmentDetail;
import com.sunrise.dental.service.TreatmentService;
import com.sunrise.dental.util.AppConstants;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class TreatmentServiceImpl implements TreatmentService {

    private final TreatmentDao treatmentDao;
    private final TreatmentDetailDao treatmentDetailDao;
    private final AppointmentDao appointmentDao;

    public TreatmentServiceImpl(TreatmentDao treatmentDao, TreatmentDetailDao treatmentDetailDao,
                                AppointmentDao appointmentDao) {
        this.treatmentDao = treatmentDao;
        this.treatmentDetailDao = treatmentDetailDao;
        this.appointmentDao = appointmentDao;
    }

    @Override
    public List<Treatment> getAllTreatments() {
        List<Treatment> treatments = treatmentDao.findAll();
        treatments.forEach(this::loadDetails);
        return treatments;
    }

    @Override
    public Optional<Treatment> getTreatmentById(Long treatmentId) {
        Optional<Treatment> treatment = treatmentDao.findById(treatmentId);
        treatment.ifPresent(this::loadDetails);
        return treatment;
    }

    @Override
    public List<Treatment> getTreatmentsForDentist(Long dentistId) {
        List<Treatment> treatments = treatmentDao.findByDentistId(dentistId);
        treatments.forEach(this::loadDetails);
        return treatments;
    }

    @Override
    public Long createTreatment(Treatment treatment) {
        validateTreatment(treatment);
        if (treatmentDao.findByAppointmentId(treatment.getAppointmentId()).isPresent()) {
            throw new ValidationException("Treatment already exists for this appointment");
        }
        Long treatmentId = treatmentDao.insert(treatment);
        treatment.setTreatmentId(treatmentId);
        saveDetails(treatment);
        appointmentDao.updateStatus(treatment.getAppointmentId(), AppConstants.STATUS_COMPLETED);
        return treatmentId;
    }

    @Override
    public void updateTreatment(Treatment treatment) {
        validateTreatment(treatment);
        if (treatment.getTreatmentId() == null) {
            throw new ValidationException("Treatment id is required for update");
        }
        treatmentDao.update(treatment);
        treatmentDetailDao.deleteByTreatmentId(treatment.getTreatmentId());
        saveDetails(treatment);
    }

    @Override
    public void deleteTreatment(Long treatmentId) {
        if (treatmentId == null) {
            throw new ValidationException("Treatment id is required");
        }
        treatmentDetailDao.deleteByTreatmentId(treatmentId);
        treatmentDao.delete(treatmentId);
    }

    private void loadDetails(Treatment treatment) {
        treatment.setDetails(treatmentDetailDao.findByTreatmentId(treatment.getTreatmentId()));
    }

    private void saveDetails(Treatment treatment) {
        if (treatment.getDetails() == null || treatment.getDetails().isEmpty()) {
            throw new ValidationException("At least one treatment detail is required");
        }
        for (TreatmentDetail detail : treatment.getDetails()) {
            detail.setTreatmentId(treatment.getTreatmentId());
            if (detail.getChargeId() == null) {
                throw new ValidationException("Treatment charge is required for each detail");
            }
            if (detail.getQuantity() <= 0) {
                throw new ValidationException("Quantity must be greater than zero");
            }
            if (detail.getUnitPrice() == null || detail.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Unit price must be zero or greater");
            }
            treatmentDetailDao.insert(detail);
        }
    }

    private void validateTreatment(Treatment treatment) {
        if (treatment == null) {
            throw new ValidationException("Treatment data is required");
        }
        if (treatment.getAppointmentId() == null) {
            throw new ValidationException("Appointment is required");
        }
        if (treatment.getDentistId() == null) {
            throw new ValidationException("Dentist is required");
        }
        if (treatment.getTreatmentDate() == null) {
            throw new ValidationException("Treatment date is required");
        }
        if (treatment.getTreatmentNotes() == null || treatment.getTreatmentNotes().isBlank()) {
            throw new ValidationException("Treatment notes are required");
        }
    }
}
