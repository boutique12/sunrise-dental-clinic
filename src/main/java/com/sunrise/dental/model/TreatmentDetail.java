package com.sunrise.dental.model;

import java.math.BigDecimal;

public class TreatmentDetail {

    private Long treatmentDetailId;
    private Long treatmentId;
    private Long chargeId;
    private int quantity;
    private BigDecimal unitPrice;
    private String notes;

    private String treatmentName;
    private String treatmentCode;

    public TreatmentDetail() {
    }

    public Long getTreatmentDetailId() {
        return treatmentDetailId;
    }

    public void setTreatmentDetailId(Long treatmentDetailId) {
        this.treatmentDetailId = treatmentDetailId;
    }

    public Long getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(Long treatmentId) {
        this.treatmentId = treatmentId;
    }

    public Long getChargeId() {
        return chargeId;
    }

    public void setChargeId(Long chargeId) {
        this.chargeId = chargeId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public String getTreatmentCode() {
        return treatmentCode;
    }

    public void setTreatmentCode(String treatmentCode) {
        this.treatmentCode = treatmentCode;
    }

    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
