package com.example.model;

import com.example.controller.TableController;
import com.example.utils.AHClientHandler;

import java.sql.Timestamp;

public class Admin {
    private Integer id;
    private Integer email_id = 0; //Ak tu nie je číslo, tak umrie aplikácia...
    //private Integer email_id; //Ak tu nie je číslo, tak umrie aplikácia...
    private String firstName = "";
    private String lastName = "";
    private String username = "";
    private String password = "";
    private Timestamp last_login;

    public int getId() {
        return id;
    }

    public void setId(Integer admin_id) {
        this.id = admin_id;
    }

    public String getEmail_id() {return email_id.toString();}
    //public int getEmail_id() {return email_id;}

    public void setEmail_id(Integer email_id) {
        this.email_id = email_id;
    }

    public String getFirstName() {return firstName;}

    public void setFirstName(String firstName) {this.firstName = firstName;}

    public String getLastName() {return lastName;}

    public void setLastName(String lastName) {this.lastName = lastName;}

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public Timestamp getLast_login() {
        return last_login;
    }

    public void setLast_login(Timestamp last_login) {
        this.last_login = last_login;
    }

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
