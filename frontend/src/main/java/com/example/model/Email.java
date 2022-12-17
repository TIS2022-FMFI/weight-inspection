package com.example.model;

public class Email {
    private Integer id;
    private String email;
    private Boolean send_exports;

    public int getId() {
        return id;
    }

    public void setId(Integer email_id) {
        this.id = email_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getSend_exports() {
        return send_exports;
    }

    public void setSend_exports(Boolean send_exports) {
        this.send_exports = send_exports;
    }
}
