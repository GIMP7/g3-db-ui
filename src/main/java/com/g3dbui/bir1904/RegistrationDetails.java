package com.g3dbui.bir1904;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "registration_details")
public class RegistrationDetails {

    @Id
    @Column(name = "registration_id", nullable = false, length = 10)
    private String registrationId = "";

    @Column(name = "agent_tin", length = 15)
    private String agentTin;

    @Column(name = "reg_date", nullable = false)
    private LocalDate regDate = LocalDate.now();

    @Column(name = "taxpayer_type", nullable = false, length = 40)
    private String taxpayerType = "";

    @Column(name = "purpose", nullable = false, length = 20)
    private String purpose = "";

    public RegistrationDetails() {
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getAgentTin() {
        return agentTin;
    }

    public void setAgentTin(String agentTin) {
        this.agentTin = agentTin;
    }

    public LocalDate getRegDate() {
        return regDate;
    }

    public void setRegDate(LocalDate regDate) {
        this.regDate = regDate;
    }

    public String getTaxpayerType() {
        return taxpayerType;
    }

    public void setTaxpayerType(String taxpayerType) {
        this.taxpayerType = taxpayerType;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}