-- Sunrise Dental Clinic - CIS6003 Advanced Programming
CREATE DATABASE IF NOT EXISTS sunrise_dental_clinic
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE sunrise_dental_clinic;

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_role CHECK (role IN ('RECEPTIONIST', 'DENTIST'))
);

CREATE TABLE IF NOT EXISTS patients (
    patient_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_number VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10) NOT NULL,
    nic_number VARCHAR(20) UNIQUE,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(255),
    medical_notes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_patient_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    INDEX idx_patient_name (first_name, last_name),
    INDEX idx_patient_phone (phone)
);

CREATE TABLE IF NOT EXISTS appointments (
    appointment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    appointment_number VARCHAR(30) NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    dentist_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    CONSTRAINT fk_appointment_dentist FOREIGN KEY (dentist_id) REFERENCES users(user_id),
    CONSTRAINT chk_appointment_status CHECK (status IN ('SCHEDULED', 'COMPLETED', 'CANCELLED', 'NO_SHOW')),
    CONSTRAINT uq_dentist_schedule UNIQUE (dentist_id, appointment_date, appointment_time),
    INDEX idx_appointment_date (appointment_date),
    INDEX idx_appointment_patient (patient_id)
);

CREATE TABLE IF NOT EXISTS treatment_charges (
    charge_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    treatment_code VARCHAR(20) NOT NULL UNIQUE,
    treatment_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    standard_charge DECIMAL(10, 2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_standard_charge CHECK (standard_charge >= 0)
);

CREATE TABLE IF NOT EXISTS treatments (
    treatment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    appointment_id BIGINT NOT NULL,
    dentist_id BIGINT NOT NULL,
    treatment_date DATE NOT NULL,
    diagnosis VARCHAR(500),
    treatment_notes TEXT NOT NULL,
    prescription TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_treatment_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id),
    CONSTRAINT fk_treatment_dentist FOREIGN KEY (dentist_id) REFERENCES users(user_id),
    INDEX idx_treatment_appointment (appointment_id),
    INDEX idx_treatment_date (treatment_date)
);

CREATE TABLE IF NOT EXISTS treatment_details (
    treatment_detail_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    treatment_id BIGINT NOT NULL,
    charge_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    notes VARCHAR(255),
    CONSTRAINT fk_detail_treatment FOREIGN KEY (treatment_id) REFERENCES treatments(treatment_id),
    CONSTRAINT fk_detail_charge FOREIGN KEY (charge_id) REFERENCES treatment_charges(charge_id),
    CONSTRAINT chk_treatment_quantity CHECK (quantity > 0),
    CONSTRAINT chk_treatment_unit_price CHECK (unit_price >= 0),
    INDEX idx_detail_treatment (treatment_id)
);

CREATE TABLE IF NOT EXISTS bills (
    bill_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bill_number VARCHAR(30) NOT NULL UNIQUE,
    appointment_id BIGINT NOT NULL UNIQUE,
    subtotal DECIMAL(10, 2) NOT NULL,
    discount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bill_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id),
    CONSTRAINT chk_bill_subtotal CHECK (subtotal >= 0),
    CONSTRAINT chk_bill_discount CHECK (discount >= 0),
    CONSTRAINT chk_bill_total CHECK (total_amount >= 0),
    CONSTRAINT chk_payment_status CHECK (payment_status IN ('UNPAID', 'PARTIALLY_PAID', 'PAID'))
);

CREATE TABLE IF NOT EXISTS payments (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_number VARCHAR(30) NOT NULL UNIQUE,
    bill_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    received_by BIGINT NOT NULL,
    reference_number VARCHAR(100),
    notes VARCHAR(255),
    CONSTRAINT fk_payment_bill FOREIGN KEY (bill_id) REFERENCES bills(bill_id),
    CONSTRAINT fk_payment_received_by FOREIGN KEY (received_by) REFERENCES users(user_id),
    CONSTRAINT chk_payment_amount CHECK (amount > 0),
    CONSTRAINT chk_payment_method CHECK (payment_method IN ('CASH', 'CARD', 'BANK_TRANSFER')),
    INDEX idx_payment_bill (bill_id),
    INDEX idx_payment_date (payment_date)
);

-- Demo staff (PBKDF2-HMAC-SHA256 hashes)
INSERT IGNORE INTO users (username, password_hash, full_name, role, email, phone, active) VALUES
('reception01', '120000:pNGrV8CMHzsm1NBc5ndLIw==:6sM4KS7Ejs7IiXfyGhAQh7d4NV3n30mMZfnjvDXZcr0=', 'Sunrise Receptionist', 'RECEPTIONIST', 'reception@sunrisedental.lk', '0112345678', TRUE),
('dentist.amara', '120000:okAJM+aAeVNJy+wFmlxHDw==:0eDIUUDPcVDUitEVkctRdDJhg+27liZaN/2x6Qvj6m4=', 'Dr. Amara Silva', 'DENTIST', 'amara@sunrisedental.lk', '0771234567', TRUE),
('dentist.kasun', '120000:Gsw8QAYnnhDUwsXqZ8G4zQ==:iEFyMsSJZDamCoihMNztzxEN3OqvJlRKfcqOfhGuVs4=', 'Dr. Kasun Perera', 'DENTIST', 'kasun@sunrisedental.lk', '0777654321', TRUE);

INSERT IGNORE INTO treatment_charges (treatment_code, treatment_name, description, standard_charge) VALUES
('TR001', 'Dental Consultation', 'General dental consultation and examination', 2000.00),
('TR002', 'Dental Cleaning', 'Professional teeth cleaning', 3500.00),
('TR003', 'Tooth Filling', 'Standard tooth filling treatment', 5000.00),
('TR004', 'Tooth Extraction', 'Standard tooth extraction', 6000.00),
('TR005', 'Root Canal Treatment', 'Root canal treatment procedure', 25000.00),
('TR006', 'Dental X-Ray', 'Dental X-ray examination', 3000.00);
