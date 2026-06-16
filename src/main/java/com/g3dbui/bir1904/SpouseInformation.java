package com.g3dbui.bir1904;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "spouse_information")
public class SpouseInformation {

    @Id
    @Column(name = "spouse_id", nullable = false, length = 10)
    private String spouseId = "";

    @Column(name = "spouse_employment", nullable = false, length = 40)
    private String spouseEmployment = "";

    @Column(name = "spouse_name", nullable = false, length = 70)
    private String spouseName = "";

    @Column(name = "spouse_tin", length = 15)
    private String spouseTin;

    @Column(name = "spouse_employer_name", length = 70)
    private String spouseEmployerName;

    @Column(name = "spouse_employer_tin", length = 15)
    private String spouseEmployerTin;

    @Column(name = "registration_id", nullable = false, length = 10)
    private String registrationId = "";

    public SpouseInformation() {
    }

    public String getSpouseId() {
        return spouseId;
    }

    public void setSpouseId(String spouseId) {
        this.spouseId = spouseId;
    }

    public String getSpouseEmployment() {
        return spouseEmployment;
    }

    public void setSpouseEmployment(String spouseEmployment) {
        this.spouseEmployment = spouseEmployment;
    }

    public String getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    public String getSpouseTin() {
        return spouseTin;
    }

    public void setSpouseTin(String spouseTin) {
        this.spouseTin = spouseTin;
    }

    public String getSpouseEmployerName() {
        return spouseEmployerName;
    }

    public void setSpouseEmployerName(String spouseEmployerName) {
        this.spouseEmployerName = spouseEmployerName;
    }

    public String getSpouseEmployerTin() {
        return spouseEmployerTin;
    }

    public void setSpouseEmployerTin(String spouseEmployerTin) {
        this.spouseEmployerTin = spouseEmployerTin;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
}