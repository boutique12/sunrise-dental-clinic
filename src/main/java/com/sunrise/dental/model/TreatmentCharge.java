package com.sunrise.dental.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TreatmentCharge {

    private Long chargeId;
    private String treatmentCode;
    private String treatmentName;
    private String description;
    private BigDecimal standardCharge;
    private boolean active;
    private LocalDateTime createdAt;

    public TreatmentCharge() {
    }

    public Long getChargeId() {
        return chargeId;
    }

    public void setChargeId(Long chargeId) {
        this.chargeId = chargeId;
    }

    public String getTreatmentCode() {
        return treatmentCode;
    }

    public void setTreatmentCode(String treatmentCode) {
        this.treatmentCode = treatmentCode;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getStandardCharge() {
        return standardCharge;
    }

    public void setStandardCharge(BigDecimal standardCharge) {
        this.standardCharge = standardCharge;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
