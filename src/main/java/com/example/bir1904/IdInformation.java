package com.example.bir1904;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "id_information")
public class IdInformation {

    @Id
    @Column(name = "id_number", nullable = false, length = 20)
    private String idNumber = "";

    @Column(name = "id_type", nullable = false, length = 30)
    private String idType = "";

    @Column(name = "id_effective", nullable = false)
    private LocalDate idEffective = LocalDate.now();

    @Column(name = "id_expiry")
    private LocalDate idExpiry;

    @Column(name = "registration_id", nullable = false, length = 10)
    private String registrationId = "";

    public IdInformation() {
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public LocalDate getIdEffective() {
        return idEffective;
    }

    public void setIdEffective(LocalDate idEffective) {
        this.idEffective = idEffective;
    }

    public LocalDate getIdExpiry() {
        return idExpiry;
    }

    public void setIdExpiry(LocalDate idExpiry) {
        this.idExpiry = idExpiry;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
}