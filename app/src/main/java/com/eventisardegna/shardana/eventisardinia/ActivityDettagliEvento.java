package com.eventisardegna.shardana.eventisardinia;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ActivityDettagliEvento extends AppCompatActivity {


    TextView titolo, luogo, descrizione;
    ImageView foto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli_evento);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        titolo = findViewById(R.id.titolo_dettagli_evento);
        luogo = findViewById(R.id.luogo_dettagli_evento);
        descrizione = findViewById(R.id.descrizione_dettagli_evento);
        foto = findViewById(R.id.foto_dettagli_evento);

        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String descrizion = getIntent().getStringExtra("descrizione");
        String image = getIntent().getStringExtra("image");

        titolo.setText(title);
        luogo.setText(description);
        descrizione.setText(descrizion);
        Picasso.get().load(image).into(foto);
    }

}
