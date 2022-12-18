package com.example.model;

import com.example.utils.AHClientHandler;

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

    public void post()
    {
        if (id == null)
        {
            AHClientHandler.getAHClientHandler().postRequest("/email", this);
            return;
        }
        throw new IllegalStateException("Can't POST email that has ID, probably ment to use PUT");
    }

    public void put()
    {
        AHClientHandler.getAHClientHandler().putRequest("/email/" + String.valueOf(id), this);
    }

    public void delete()
    {
        AHClientHandler.getAHClientHandler().deleteRequest("/email/" + String.valueOf(id));
    }
}
