package com.skillswap;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SkillRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderEmail;
    private String receiverEmail;
    private String skillOffered;
    private String skillRequested;
    private String status = "PENDING";

    public SkillRequest() {
    }

    public Long getId() {
        return id;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getSkillOffered() {
        return skillOffered;
    }

    public void setSkillOffered(String skillOffered) {
        this.skillOffered = skillOffered;
    }

    public String getSkillRequested() {
        return skillRequested;
    }

    public void setSkillRequested(String skillRequested) {
        this.skillRequested = skillRequested;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}