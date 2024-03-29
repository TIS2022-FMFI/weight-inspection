package com.example.model;

import com.example.controller.TableController;
import com.example.utils.AHClientHandler;

public class Email {
    private Integer id;
    private String email = "";
    private Boolean sendExports = true;

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

    public Boolean getSendExports() {
        return sendExports;
    }

    public void setSendExports(Boolean send_exports) {
        this.sendExports = send_exports;
    }

    public void post(TableController controller) {
        if (id == null) {
            AHClientHandler.getAHClientHandler().postRequest("/email", this, controller);
            return;
        }
        throw new IllegalStateException("Can't POST email that has ID, probably ment to use PUT");
    }

    public void put(TableController controller) {
        AHClientHandler.getAHClientHandler().putRequest("/email/" + String.valueOf(id), this, controller);
    }

    public void delete(TableController controller) {
        AHClientHandler.getAHClientHandler().deleteRequest("/email/" + String.valueOf(id), controller);
    }
}
