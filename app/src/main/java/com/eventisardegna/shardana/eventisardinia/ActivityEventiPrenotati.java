package com.eventisardegna.shardana.eventisardinia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class ActivityEventiPrenotati extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public GregorianCalendar mese_calendario, mese_calendario_copia;
    private AdaptCalendario adaptCalendario;
    private TextView testo_mese;
    ActionBarDrawerToggle toggle;
    private RecyclerView mRecyclerView;
    ImageView immagineProfilo;
    DrawerLayout drawer;
    DatabaseReference mRef;
    DatabaseReference titoloRef;
    FirebaseAuth firebaseAuth;
    private ImageView nav_profile_image;
    private TextView nav_nome_utente;
    private TextView nav_email_utente;
    boolean doubleTap = false;
    public Uri mImageUri;
    public String image_url;
    private UploadTask mUploadTask;
    private RecyclerView listaEventiView;
    private DatabaseReference databaseReference;
    private ArrayList<DatabaseEvento> eventi= new ArrayList<DatabaseEvento>();
    private AdaptEvento adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventi_prenotati);
        listaEventiView = (RecyclerView) findViewById(R.id.row_adda);
        listaEventiView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navView = navigationView.inflateHeaderView(R.layout.header_tendina_utente);
        nav_profile_image = (ImageView) navView.findViewById(R.id.tendina_immagine_profilo);
        if (user.getPhotoUrl() != null) {
            Picasso.get().load(user.getPhotoUrl()).into(nav_profile_image);
        }
        nav_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(60, 60)
                        .start(ActivityEventiPrenotati.this);
            }
        });
        nav_nome_utente = (TextView) navView.findViewById(R.id.tendina_nome);
        nav_nome_utente.setText(user.getDisplayName());
        nav_email_utente = (TextView) navView.findViewById(R.id.tendina_email);
        nav_email_utente.setText(user.getEmail());

        DatabaseEvento.date_collection_arr = new ArrayList<DatabaseEvento>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Eventi");
        //POPOLAZIONE EVENTI DA DATABASE
        databaseReference.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                loadData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                loadData(dataSnapshot);
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            public void onCancelled(DatabaseError databaseError) { }



        });

        //POPOLAZIONE INTERFACCIA SCORREVOLE DEGLI EVENTI


    }
    public void loadData(DataSnapshot dataSnapshot) {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        // get all of the children at this level.
        if (dataSnapshot.child("prenotazioni").hasChild(user.getDisplayName())) {

            DatabaseEvento databaseEvento = dataSnapshot.getValue(DatabaseEvento.class);
            eventi.add(databaseEvento);

            adapter = new AdaptEvento(ActivityEventiPrenotati.this, eventi);
            listaEventiView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
