package com.eventisardegna.shardana.eventisardinia;

public class DatabaseUtente {


    public String name;
    public String cognome;
    public String mail;
    public String pec;
    public String phone;
    public String data;
    public String luogo;
    public String residenza;

    public DatabaseUtente(String name, String cognome, String mail) {
        this.name = name;
        this.cognome = cognome;
        this.mail = mail;
    }

    public DatabaseUtente(String name, String cognome, String pec, String phone, String data, String luogo, String residenza) {
        this.name = name;
        this.cognome = cognome;
        this.pec = pec;
        this.phone = phone;
        this.data = data;
        this.luogo = luogo;
        this.residenza = residenza;
    }

    public DatabaseUtente(String name, String cognome, String pec, String data, String luogo, String residenza) {
        this.name = name;
        this.cognome = cognome;
        this.pec = pec;
        //this.phone = phone;
        this.data = data;
        this.luogo = luogo;
        this.residenza = residenza;
    }



}
