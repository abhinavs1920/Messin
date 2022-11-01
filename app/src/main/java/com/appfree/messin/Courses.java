package com.appfree.messin;

public class Courses {
    private String token,usernumber;
    public Courses() {
        // empty constructor
        // required for Firebase.
    }


    public Courses(String token, String usernumber) {
        this.token = token;
        this.usernumber = usernumber;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsernumber() {
        return usernumber;
    }

    // setter method for all variables.
    public void setUsernumber(String usernumber) {
        this.usernumber = usernumber;
    }

}
