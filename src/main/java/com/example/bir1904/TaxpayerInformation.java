package com.example.bir1904;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "taxpayer_information")
public class TaxpayerInformation {

    @Id
    @Column(name = "registration_id", nullable = false, length = 10)
    private String registrationId = "";

    @Column(name = "philsys_number", unique = true, length = 19)
    private String philsysNumber;

    @Column(name = "foreign_tin", length = 20)
    private String foreignTin;

    @Column(name = "residence", length = 150)
    private String residence;

    @Column(name = "taxpayer_name", nullable = false, length = 70)
    private String taxpayerName = "";

    @Column(name = "name_category", nullable = false, length = 15)
    private String nameCategory = "";

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate = LocalDate.now();

    @Column(name = "birth_place", nullable = false, length = 150)
    private String birthPlace = "";

    @Column(name = "local_address", nullable = false, length = 150)
    private String localAddress = "";

    @Column(name = "foreign_address", length = 150)
    private String foreignAddress;

    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "gender", nullable = false, length = 1)
    private String gender = "";

    @Column(name = "civil_status", nullable = false, length = 1)
    private String civilStatus = "";

    @Column(name = "contact_no", nullable = false, length = 15)
    private String contactNo = "";

    @Column(name = "email", nullable = false, length = 40)
    private String email = "";

    @Column(name = "mother_name", nullable = false, length = 70)
    private String motherName = "";

    @Column(name = "father_name", nullable = false, length = 70)
    private String fatherName = "";

    public TaxpayerInformation() {
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getPhilsysNumber() {
        return philsysNumber;
    }

    public void setPhilsysNumber(String philsysNumber) {
        this.philsysNumber = philsysNumber;
    }

    public String getForeignTin() {
        return foreignTin;
    }

    public void setForeignTin(String foreignTin) {
        this.foreignTin = foreignTin;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getTaxpayerName() {
        return taxpayerName;
    }

    public void setTaxpayerName(String taxpayerName) {
        this.taxpayerName = taxpayerName;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public String getForeignAddress() {
        return foreignAddress;
    }

    public void setForeignAddress(String foreignAddress) {
        this.foreignAddress = foreignAddress;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCivilStatus() {
        return civilStatus;
    }

    public void setCivilStatus(String civilStatus) {
        this.civilStatus = civilStatus;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }
}