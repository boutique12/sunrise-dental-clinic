package com.sunrise.dental.service;

import com.sunrise.dental.model.TreatmentCharge;

import java.util.List;
import java.util.Optional;

public interface TreatmentChargeService {

    List<TreatmentCharge> getActiveCharges();

    Optional<TreatmentCharge> getChargeById(Long chargeId);
}
