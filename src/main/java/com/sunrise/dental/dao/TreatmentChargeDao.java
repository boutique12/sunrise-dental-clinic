package com.sunrise.dental.dao;

import com.sunrise.dental.model.TreatmentCharge;

import java.util.List;
import java.util.Optional;

public interface TreatmentChargeDao {

    List<TreatmentCharge> findAllActive();

    Optional<TreatmentCharge> findById(Long chargeId);

    Optional<TreatmentCharge> findByCode(String treatmentCode);
}
