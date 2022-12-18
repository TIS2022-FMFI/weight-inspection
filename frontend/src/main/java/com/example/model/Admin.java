package com.example.model;

import com.example.utils.AHClientHandler;

import java.sql.Timestamp;

public class Admin {
    private Integer id;
    private Integer email_id;
    private String first_name;
    private String last_name;
    private String nickname;
    private String pass_hash;
    private Timestamp last_login;

    public int getId() {
        return id;
    }

    public void setId(Integer admin_id) {
        this.id = admin_id;
    }

    public int getEmail_id() {
        return email_id;
    }

    public void setEmail_id(Integer email_id) {
        this.email_id = email_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPass_hash() {
        return pass_hash;
    }

    public void setPass_hash(String pass_hash) {
        this.pass_hash = pass_hash;
    }

    public Timestamp getLast_login() {
        return last_login;
    }

    public void setLast_login(Timestamp last_login) {
        this.last_login = last_login;
    }

    public void post() {
        if (id == null) {
            AHClientHandler.getAHClientHandler().postRequest("/admin", this);
            return;
        }
        throw new IllegalStateException("Can't POST admin that has ID, probably ment to use PUT");
    }

    public void put() {
        AHClientHandler.getAHClientHandler().putRequest("/admin/" + String.valueOf(id), this);
    }

    public void delete() {
        AHClientHandler.getAHClientHandler().deleteRequest("/admin/" + String.valueOf(id));
    }
}
