package com.eventisardegna.shardana.eventisardinia;


import android.widget.Button;

public class EventoPrenotabile {
    private String titolo;
    private String luogo;
    private String immagine;

    private String descrizione;
    private Button prenota;

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

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Button getPrenota() {
        return prenota;
    }

    public void setPrenota(Button prenota) {
        this.prenota = prenota;
    }
}