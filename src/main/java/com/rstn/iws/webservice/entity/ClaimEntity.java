package com.rstn.iws.webservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "Claim")
@Data
public class ClaimEntity implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String applicationNumber;
    private String policyNumber;
    private String timeCreate;
    private String timeUpdate;
    private boolean isDelete;

    public ClaimEntity(){}

    public ClaimEntity(String applicationNumber, String policyNumber, boolean isDelete) {
        this.applicationNumber = applicationNumber;
        this.policyNumber = policyNumber;
        this.isDelete = isDelete;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(String timeCreate) {
        this.timeCreate = timeCreate;
    }

    public String getTimeUpdate() {
        return timeUpdate;
    }

    public void setTimeUpdate() {
        this.timeUpdate = formatDate(new Date());
    }

    public void setTimeUpdate1() {
        this.timeUpdate = "";
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        this.isDelete = delete;
    }

    public String formatDate(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String currentTime = dateFormat.format(date);
        return currentTime;
    }

    @Override
    public String toString() {
        return "ClaimEntity{" +
                "id=" + id +
                ", applicationNumber='" + applicationNumber + '\'' + "\n" +
                ", policyNumber='" + policyNumber + '\'' + "\n" +
                ", timeCreate='" + timeCreate + '\'' + "\n" +
                ", timeUpdate='" + timeUpdate + '\'' + "\n" +
                ", isDelete=" + isDelete + "\n" +
                '}';
    }
}
