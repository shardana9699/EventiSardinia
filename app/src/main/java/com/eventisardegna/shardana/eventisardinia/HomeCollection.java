package com.eventisardegna.shardana.eventisardinia;

import java.util.ArrayList;

class HomeCollection {
    public String date="";
    public String titolo="";
    public String luogo="";

    public HomeCollection(){

    }

    public static ArrayList<HomeCollection> date_collection_arr;
    public HomeCollection(String date, String titolo, String luogo){

        this.date=date;
        this.titolo=titolo;
        this.luogo=luogo;

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

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public static ArrayList<HomeCollection> getDate_collection_arr() {
        return date_collection_arr;
    }

    public static void setDate_collection_arr(ArrayList<HomeCollection> date_collection_arr) {
        HomeCollection.date_collection_arr = date_collection_arr;
    }
}
