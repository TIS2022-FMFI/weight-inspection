package com.example.model;

import java.sql.Timestamp;

public class Notification {
    private Integer id;
    private Timestamp createdOn;
    private String type = "";
    private String description = "";

    public int getId() {
        return id;
    }

    public void setId(Integer notification_id) {
        this.id = notification_id;
    }

    public Timestamp getCreatedOn() {return createdOn;}

    public void setCreatedOn(Timestamp createdOn) {this.createdOn = createdOn;}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
