package com.eventisardegna.shardana.eventisardinia;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ActivityNavAccount extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* setContentView(R.layout.header_tendina_utente);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        ImageView imageView = findViewById(R.id.tendina_immagine_profilo);
        TextView nome = findViewById(R.id.tendina_nome);
        nome.setText(user.getDisplayName());
        if(user.getPhotoUrl() != null) {
            Picasso.get().load(user.getPhotoUrl()).into(imageView);
        }*/
    }
}
