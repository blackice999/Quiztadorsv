package com.course.quiztador_sv;

/**
 * Created by Adam on 13.04.2018.
 */
public class Credentials {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
