package com.example.frsystem.ui.orderslider.availablejobs;

import com.google.firebase.firestore.GeoPoint;

public class Availablejobsclass {

    String client_name;
    int clientnumber;
    String desc;
    String order_id;
    String date_created;
    String locationName;
    String imageref;
    GeoPoint latlang;

    String email;
    public Availablejobsclass(){

    }

    public Availablejobsclass(String client_name, int clientnumber, String desc, String date_created, String order_id, String email, String locationName, String imageref, GeoPoint latlang)
    {
        this.client_name = client_name;
        this.clientnumber = clientnumber;
        this.desc = desc;
        this.order_id = order_id;
        this.date_created = date_created;
        this.locationName = locationName;
        this.imageref = imageref;
        this.latlang = latlang;
        this.email = email;

    }

    public String getClient_name() {
        return client_name;
    }

    public int getClientnumber() {
        return clientnumber;
    }

    public String getDesc() {
        return desc;
    }

    public String getEmail() {
        return email;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getDate_created() {
        return date_created;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getImageref() {
        return imageref;
    }

    public GeoPoint getLatlang() {
        return latlang;
    }

}
