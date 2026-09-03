package com.sunrise.dental.service.impl;

import com.sunrise.dental.dao.TreatmentChargeDao;
import com.sunrise.dental.model.TreatmentCharge;
import com.sunrise.dental.service.TreatmentChargeService;

import java.util.List;
import java.util.Optional;

public class TreatmentChargeServiceImpl implements TreatmentChargeService {

    private final TreatmentChargeDao treatmentChargeDao;

    public TreatmentChargeServiceImpl(TreatmentChargeDao treatmentChargeDao) {
        this.treatmentChargeDao = treatmentChargeDao;
    }

    @Override
    public List<TreatmentCharge> getActiveCharges() {
        return treatmentChargeDao.findAllActive();
    }

    @Override
    public Optional<TreatmentCharge> getChargeById(Long chargeId) {
        return treatmentChargeDao.findById(chargeId);
    }
}
