package com.example.frsystem.home;

import com.google.firebase.Timestamp;

public class Jobscounterclass {

    private int activejobs, completedjobs, number;
    private String username, uId, email, profilepicuri;
    private Timestamp datecreated, datemodified;

    public Jobscounterclass(int activejobs, int completedjobs, String username, String uId, String profilepicuri, String email, Timestamp datecreated, Timestamp datemodified, int number){
        this.activejobs = activejobs;
        this.completedjobs = completedjobs;
        this.datecreated = datecreated;
        this.datemodified = datemodified;
        this.username = username;
        this.uId = uId;
        this.email = email;
        this.profilepicuri = profilepicuri;
        this.number = number;
    }

    public Jobscounterclass(){

    }

    public int getActivejobs() {
        return activejobs;
    }

    public int getCompletedjobs() {
        return completedjobs;
    }

    public int getNumber() {
        return number;
    }

    public String getUsername() {
        return username;
    }

    public String getuId() {
        return uId;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilepicuri() {
        return profilepicuri;
    }

    public Timestamp getDatecreated() {
        return datecreated;
    }

    public Timestamp getDatemodified() {
        return datemodified;
    }
}
