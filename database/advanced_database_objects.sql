USE sunrise_dental_clinic;

DELIMITER //

CREATE FUNCTION IF NOT EXISTS fn_calculate_bill_balance(p_bill_id BIGINT)
RETURNS DECIMAL(10, 2)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_total DECIMAL(10, 2);
    DECLARE v_paid DECIMAL(10, 2);
    SET v_total = (SELECT total_amount FROM bills WHERE bill_id = p_bill_id);
    SET v_paid = (SELECT COALESCE(SUM(amount), 0) FROM payments WHERE bill_id = p_bill_id);
    RETURN GREATEST(v_total - v_paid, 0);
END //

CREATE PROCEDURE IF NOT EXISTS sp_daily_appointment_report(IN p_report_date DATE)
BEGIN
    SELECT a.appointment_number, p.patient_number,
           CONCAT(p.first_name, ' ', p.last_name) AS patient_name,
           u.full_name AS dentist_name, a.appointment_time, a.reason, a.status
    FROM appointments a
    JOIN patients p ON a.patient_id = p.patient_id
    JOIN users u ON a.dentist_id = u.user_id
    WHERE a.appointment_date = p_report_date
    ORDER BY a.appointment_time;
END //

CREATE TRIGGER IF NOT EXISTS trg_payment_update_bill_status
AFTER INSERT ON payments
FOR EACH ROW
BEGIN
    DECLARE v_total DECIMAL(10, 2);
    DECLARE v_paid DECIMAL(10, 2);
    SET v_total = (SELECT total_amount FROM bills WHERE bill_id = NEW.bill_id);
    SET v_paid = (SELECT COALESCE(SUM(amount), 0) FROM payments WHERE bill_id = NEW.bill_id);
    IF v_paid >= v_total THEN
        UPDATE bills SET payment_status = 'PAID' WHERE bill_id = NEW.bill_id;
    ELSEIF v_paid > 0 THEN
        UPDATE bills SET payment_status = 'PARTIALLY_PAID' WHERE bill_id = NEW.bill_id;
    ELSE
        UPDATE bills SET payment_status = 'UNPAID' WHERE bill_id = NEW.bill_id;
    END IF;
END //

DELIMITER ;

CREATE OR REPLACE VIEW vw_appointment_billing_summary AS
SELECT a.appointment_id, a.appointment_number, a.appointment_date, a.appointment_time, a.status,
       p.patient_number, CONCAT(p.first_name, ' ', p.last_name) AS patient_name,
       u.full_name AS dentist_name,
       b.bill_number, b.subtotal, b.discount, b.total_amount, b.payment_status,
       COALESCE((SELECT SUM(pay.amount) FROM payments pay WHERE pay.bill_id = b.bill_id), 0) AS total_paid,
       COALESCE(b.total_amount - (SELECT SUM(pay.amount) FROM payments pay WHERE pay.bill_id = b.bill_id), b.total_amount) AS outstanding
FROM appointments a
JOIN patients p ON a.patient_id = p.patient_id
JOIN users u ON a.dentist_id = u.user_id
LEFT JOIN bills b ON a.appointment_id = b.appointment_id;
