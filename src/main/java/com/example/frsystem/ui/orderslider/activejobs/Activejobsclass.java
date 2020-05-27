package com.example.frsystem.ui.orderslider.activejobs;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Activejobsclass implements Comparable<Activejobsclass>{

    String clientusername;
    int clientnumber, priority;
    GeoPoint clientlatlang;
    String client_description;
    String orderid;
    Timestamp datecreated;
    Timestamp datemodified;
    String clientprofilepicuri;
    String clientemail;
    String status;
    String id;

    public Activejobsclass(){

    }

    public Activejobsclass(String clientusername, int clientnumber, String clientemail, String client_description, String orderid, Timestamp datecreated, Timestamp datemodified, GeoPoint clientlatlang, String clientprofilepicuri, String status, String id)
    {
        this.clientusername = clientusername;
        this.clientnumber = clientnumber;
        this.client_description = client_description;
        this.orderid = orderid;
        this.clientemail = clientemail;
        this.clientlatlang = clientlatlang;
        this.datecreated = datecreated;
        this.clientprofilepicuri = clientprofilepicuri;
        this.status = status;
        this.datemodified = datemodified;
        this.id = id;
        this.priority = priority;

    }

    public String getClientusername() {
        return clientusername;
    }

    public int getClientnumber() {
        return clientnumber;
    }

    public GeoPoint getClientlatlang() {
        return clientlatlang;
    }

    public String getClient_description() {
        return client_description;
    }

    public String getOrderid() {
        return orderid;
    }

    public Timestamp getDatecreated() {
        return datecreated;
    }

    public Timestamp getDatemodified() {
        return datemodified;
    }

    public String getClientprofilepicuri() {
        return clientprofilepicuri;
    }

    public String getClientemail() {
        return clientemail;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int compareTo(Activejobsclass job) {
        if(priority < job.priority)
            return -1;
        else if(priority > job.priority)
            return 1;
        return 0;
    }
}
