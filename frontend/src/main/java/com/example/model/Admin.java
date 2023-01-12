package com.example.model;

import com.example.controller.TableController;
import com.example.utils.AHClientHandler;

import java.sql.Timestamp;

public class Admin {
    private Integer id;
    private Integer emailId;
    private String firstName = "";
    private String lastName = "";
    private String username = "";
    private String password = "";
    private Timestamp lastLogin;

    public int getId() {
        return id;
    }

    public void setId(Integer admin_id) {
        this.id = admin_id;
    }

    public String getEmailId()
    {
        if (emailId == null)
        {
            return "";
        }
        return emailId.toString();
    }

    public void setEmailId(Integer emailId) {this.emailId = emailId;}

    public String getFirstName() {return firstName;}

    public void setFirstName(String firstName) {this.firstName = firstName;}

    public String getLastName() {return lastName;}

    public void setLastName(String lastName) {this.lastName = lastName;}

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public Timestamp getLastLogin() {return lastLogin;}

    public void setLastLogin(Timestamp lastLogin) {this.lastLogin = lastLogin;}

    public void post(TableController controller) {
        if (id == null) {
            AHClientHandler.getAHClientHandler().postRequest("/admin", this, controller);
            return;
        }
        throw new IllegalStateException("Can't POST admin that has ID, probably ment to use PUT");
    }

    public void put(TableController controller) {
        AHClientHandler.getAHClientHandler().putRequest("/admin/" + String.valueOf(id), this, controller);
    }

    public void delete(TableController controller) {
        AHClientHandler.getAHClientHandler().deleteRequest("/admin/" + String.valueOf(id), controller);
    }
}
