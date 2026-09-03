package com.sunrise.dental.dao;

import com.sunrise.dental.model.TreatmentDetail;

import java.util.List;

public interface TreatmentDetailDao {

    List<TreatmentDetail> findByTreatmentId(Long treatmentId);

    void insert(TreatmentDetail detail);

    void deleteByTreatmentId(Long treatmentId);
}
