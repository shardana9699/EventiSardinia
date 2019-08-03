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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    FirebaseAuth firebaseAuth;
    private ImageView nav_profile_image;
    private TextView nav_nome_utente;
    private TextView nav_email_utente;
    boolean doubleTap = false;
    public Uri mImageUri;
    public String image_url;
    private UploadTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventi_prenotati);


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navView = navigationView.inflateHeaderView(R.layout.header_tendina_utente);
        nav_profile_image = (ImageView) navView.findViewById(R.id.tendina_immagine_profilo);
        if(user.getPhotoUrl() != null){
            Picasso.get().load(user.getPhotoUrl()).into(nav_profile_image);
        }
        nav_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(60,60)
                        .start(ActivityEventiPrenotati.this);
            }
        });
        nav_nome_utente = (TextView) navView.findViewById(R.id.tendina_nome);
        nav_nome_utente.setText(user.getDisplayName());
        nav_email_utente = (TextView) navView.findViewById(R.id.tendina_email);
        nav_email_utente.setText(user.getEmail());

        DatabaseEvento.date_collection_arr = new ArrayList<DatabaseEvento>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();


        //POPOLAZIONE EVENTI DA DATABASE
        databaseReference.child("Eventi").addValueEventListener(new ValueEventListener() {


            public void onDataChange(DataSnapshot dataSnapshot) {
                // get all of the children at this level.
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // shake hands with each of them.'
                for (DataSnapshot child : children) {
                    DatabaseEvento databaseEvento = child.getValue(DatabaseEvento.class);
                    DatabaseEvento.date_collection_arr.add(databaseEvento);
                }
            }

            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //POPOLAZIONE INTERFACCIA SCORREVOLE DEGLI EVENTI
        mRecyclerView = findViewById(R.id.row_adda);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        String query = user.getEmail();

        mRef = FirebaseDatabase.getInstance().getReference().child("Eventi");
        Query firebaseSearchQuery = mRef.orderByChild("prenotazioni").equalTo(query);
        if(firebaseSearchQuery != null) {
            //mUploads = new ArrayList<>();
            FirebaseRecyclerAdapter<EventoPrenotabile, AdaptEvento> FirebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<EventoPrenotabile, AdaptEvento>(
                            EventoPrenotabile.class, R.layout.addapt_evento, AdaptEvento.class, firebaseSearchQuery
                    ) {
                        @Override
                        protected void populateViewHolder(AdaptEvento viewHolder, EventoPrenotabile model, int position) {

                            viewHolder.setDetails(getApplicationContext(), model.getTitolo(), model.getLuogo(), model.getImmagine());
                        }

                        @Override
                        public AdaptEvento onCreateViewHolder(ViewGroup parent, int viewType) {

                            AdaptEvento adaptEvento = super.onCreateViewHolder(parent, viewType);

                            adaptEvento.setOnClickListener(new AdaptEvento.ClickListener() {
                                @Override
                                public void OnItemClick(View view, int position) {

                                    String mTitolo = getItem(position).getTitolo();
                                    String mLuogo = getItem(position).getLuogo();
                                    String mDescrizione = getItem(position).getDescrizione();
                                    String mImage = getItem(position).getImmagine();
                                    Intent intent = new Intent(view.getContext(), ActivityDettagliEvento.class);
                                    intent.putExtra("title", mTitolo);
                                    intent.putExtra("description", mLuogo);
                                    intent.putExtra("descrizione", mDescrizione);
                                    intent.putExtra("image", mImage);
                                    startActivity(intent);
                                }

                                @Override
                                public void OnItemLongClick(View view, int position) {

                                }
                            });

                            return adaptEvento;
                        }
                    };


            mRecyclerView.setAdapter(FirebaseRecyclerAdapter);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
