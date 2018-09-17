package com.eventisardegna.shardana.eventisardinia;

import java.util.ArrayList;

class HomeCollection {
    public String date="";
    public String titolo="";
    public Double latitude;
    public Double longitude;
    public String luogo="";

    public HomeCollection(){

    }

    public static ArrayList<HomeCollection> date_collection_arr;
    public HomeCollection(String date, String titolo, Double latitude, Double longitude, String luogo){

        this.date=date;
        this.titolo=titolo;
        this.latitude=latitude;
        this.longitude=longitude;
        this.luogo = luogo;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public static ArrayList<HomeCollection> getDate_collection_arr() {
        return date_collection_arr;
    }

    public static void setDate_collection_arr(ArrayList<HomeCollection> date_collection_arr) {
        HomeCollection.date_collection_arr = date_collection_arr;
    }
}
