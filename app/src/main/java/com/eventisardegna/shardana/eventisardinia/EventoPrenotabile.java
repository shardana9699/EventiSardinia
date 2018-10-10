package com.eventisardegna.shardana.eventisardinia;


import android.widget.Button;

public class EventoPrenotabile {
    private String titles;
    private String subjects;
    private String image;
    private String descripts;
    private Button prenota;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitles(String titles) {
        this.titles = titles;
    }

    public void setSubjects(String subjects) {
        this.subjects = subjects;
    }

    public void setDescripts(String descripts) {
        this.descripts = descripts;
    }

    public String getTitles() {
        return titles;
    }

    public String getSubjects() {
        return subjects;
    }

    public String getDescripts() {
        return descripts;
    }

    public Button getPrenota() {
        return prenota;
    }

    public void setPrenota(Button prenota) {
        this.prenota = prenota;
    }
}