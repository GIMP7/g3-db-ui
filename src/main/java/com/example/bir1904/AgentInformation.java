package com.example.bir1904;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "agent_information")
public class AgentInformation {

    @Id
    @Column(name = "agent_tin", nullable = false, length = 15)
    private String agentTin = "";

    @Column(name = "agent_name", nullable = false, length = 70)
    private String agentName = "";

    @Column(name = "agent_rdo", nullable = false, length = 3)
    private String agentRdo = "";

    @Column(name = "agent_address", nullable = false, length = 150)
    private String agentAddress = "";

    @Column(name = "agent_contact", nullable = false, length = 15)
    private String agentContact = "";

    @Column(name = "agent_email", nullable = false, length = 40)
    private String agentEmail = "";

    @Column(name = "registration_id", nullable = false, length = 10)
    private String registrationId = "";

    public AgentInformation() {
    }

    public String getAgentTin() {
        return agentTin;
    }

    public void setAgentTin(String agentTin) {
        this.agentTin = agentTin;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentRdo() {
        return agentRdo;
    }

    public void setAgentRdo(String agentRdo) {
        this.agentRdo = agentRdo;
    }

    public String getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(String agentAddress) {
        this.agentAddress = agentAddress;
    }

    public String getAgentContact() {
        return agentContact;
    }

    public void setAgentContact(String agentContact) {
        this.agentContact = agentContact;
    }

    public String getAgentEmail() {
        return agentEmail;
    }

    public void setAgentEmail(String agentEmail) {
        this.agentEmail = agentEmail;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
}