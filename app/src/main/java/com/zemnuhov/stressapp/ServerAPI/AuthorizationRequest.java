package com.zemnuhov.stressapp.ServerAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthorizationRequest{
    @SerializedName("login")
    @Expose
    private String login;

    @SerializedName("pass")
    @Expose
    private String pass;

    public AuthorizationRequest(String login, String password) {
        this.login = login;
        this.pass = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
