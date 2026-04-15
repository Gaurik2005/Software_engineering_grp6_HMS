CREATE TABLE IF NOT EXISTS payments (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    reference_type VARCHAR(50) NOT NULL,
    reference_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    description VARCHAR(255),
    paid_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS medical_records (
    medical_record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    diagnosis TEXT,
    prescription TEXT,
    notes TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_medical_record_appointment
        FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id)
);

CREATE TABLE IF NOT EXISTS nurse_duties (
    nurse_duty_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nurse_id BIGINT NOT NULL,
    assigned_by_nurse_id BIGINT,
    duty_date DATE NOT NULL,
    shift_start TIME NOT NULL,
    shift_end TIME NOT NULL,
    ward_or_room VARCHAR(120),
    notes VARCHAR(255),
    CONSTRAINT fk_nurse_duties_nurse
        FOREIGN KEY (nurse_id) REFERENCES nurses(nurse_id)
);

CREATE TABLE IF NOT EXISTS nurse_payslips (
    nurse_payslip_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nurse_id BIGINT NOT NULL,
    month INT NOT NULL,
    year INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    generated_at DATETIME NOT NULL,
    CONSTRAINT fk_nurse_payslips_nurse
        FOREIGN KEY (nurse_id) REFERENCES nurses(nurse_id)
);
