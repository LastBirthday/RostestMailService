package main.java.ru.home.spring;

import java.io.Serializable;

/**
 * Created by Dds on 20.11.2016.
 */


public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private String userName;
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
