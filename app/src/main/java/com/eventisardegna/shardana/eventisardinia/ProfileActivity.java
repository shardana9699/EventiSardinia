package com.eventisardegna.shardana.eventisardinia;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener , NavigationView.OnNavigationItemSelectedListener {
    public GregorianCalendar mese_calendario, mese_calendario_copia;
    private AdaptCalendario adaptCalendario;
    private TextView testo_mese;
    ActionBarDrawerToggle toggle;
    private List<EventoPrenotabile> mUploads;
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
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            if(user.isEmailVerified()) {

            }else{
                startActivity(new Intent(getApplicationContext(),ActivityIconVerify.class));
            }
        }
        //GESTIONE ACTION BAR
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                        .start(ProfileActivity.this);
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

        mUploads = new ArrayList<>();
        FirebaseRecyclerAdapter<EventoCliccabile, AdaptEvento> FirebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<EventoCliccabile, AdaptEvento>(
                        EventoCliccabile.class, R.layout.addapt_evento, AdaptEvento.class, databaseReference.child("Eventi")
                ) {
                    @Override
                    protected void populateViewHolder(AdaptEvento viewHolder, EventoCliccabile model, int position) {

                        viewHolder.setDetails(getApplicationContext(), model.getTitolo(), model.getLuogo(), model.getmImageUrl());
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
                                String mImage = getItem(position).getmImageUrl();
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

    @Override
    public void onClick(View v) {

    }

    //RICERCA EVENTO
    private void firebaseSearch(String searchText){

        String query = searchText.toLowerCase();

        mRef = FirebaseDatabase.getInstance().getReference().child("Eventi");
        Query firebaseSearchQuery = mRef.orderByChild("luogo").startAt(query).endAt(query + "\uf0ff");

        FirebaseRecyclerAdapter<EventoCliccabile, AdaptEvento> FirebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<EventoCliccabile, AdaptEvento>(
                        EventoCliccabile.class, R.layout.addapt_evento, AdaptEvento.class,firebaseSearchQuery
                ) {
                    @Override
                    protected void populateViewHolder(AdaptEvento viewHolder, EventoCliccabile model, int position) {

                        viewHolder.setDetails(getApplicationContext(), model.getTitolo(), model.getLuogo(), model.getmImageUrl());
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
                                String mImage = getItem(position).getmImageUrl();
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



    protected void setNextMonth() {
        if (mese_calendario.get(GregorianCalendar.MONTH) == mese_calendario.getActualMaximum(GregorianCalendar.MONTH)) {
            mese_calendario.set((mese_calendario.get(GregorianCalendar.YEAR) + 1), mese_calendario.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            mese_calendario.set(GregorianCalendar.MONTH,
                    mese_calendario.get(GregorianCalendar.MONTH) + 1);
        }
    }

    protected void setPreviousMonth() {
        if (mese_calendario.get(GregorianCalendar.MONTH) == mese_calendario.getActualMinimum(GregorianCalendar.MONTH)) {
            mese_calendario.set((mese_calendario.get(GregorianCalendar.YEAR) - 1), mese_calendario.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            mese_calendario.set(GregorianCalendar.MONTH, mese_calendario.get(GregorianCalendar.MONTH) - 1);
        }
    }

    public void refreshCalendar() {
        adaptCalendario.refreshDays();
        adaptCalendario.notifyDataSetChanged();
        testo_mese.setText(android.text.format.DateFormat.format("MMMM yyyy", mese_calendario));
    }

   /* @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
*/

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_logout){
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, ActivityLogin.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if(doubleTap){
            super.onBackPressed();
        }else{
            Toast.makeText(this,"Premi indietro di nuovo per uscire dall'applicazione!",Toast.LENGTH_SHORT).show();
            doubleTap = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleTap = false;
                }
            },2000);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.calendario){
            final Dialog dialogs = new Dialog(this);
            dialogs.setContentView(R.layout.dialog_calendario);

            mese_calendario = (GregorianCalendar) GregorianCalendar.getInstance();
            //mese_calendario_copia = (GregorianCalendar) mese_calendario.clone();
            adaptCalendario = new AdaptCalendario(this, mese_calendario, DatabaseEvento.date_collection_arr);
            testo_mese = (TextView) dialogs.findViewById(R.id.tv_month);
            testo_mese.setText(android.text.format.DateFormat.format("MMMM yyyy", mese_calendario));

            ImageButton previous = (ImageButton) dialogs.findViewById(R.id.ib_prev);
            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPreviousMonth();
                    refreshCalendar();
                }
            });
            ImageButton next = (ImageButton) dialogs.findViewById(R.id.Ib_next);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setNextMonth();
                    refreshCalendar();
                }
            });
            GridView gridview = (GridView) dialogs.findViewById(R.id.gv_calendar);
            gridview.setAdapter(adaptCalendario);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    String selectedGridDate = AdaptCalendario.day_string.get(position);
                    ((AdaptCalendario) parent.getAdapter()).getPositionList(selectedGridDate, ProfileActivity.this);
                }

            });

            setNextMonth();

            setPreviousMonth();

            refreshCalendar();

            dialogs.show();

        }
        if(id == R.id.map){
            startActivity(new Intent(this, ActivityMaps.class));
        }
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //VENGONO ASSEGNATI I VALORI DEL LUOGO
        //VIENE ASSEGNATO IL LINK DELL'IMMAGINE
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                uploadImageToFirebaseStorage();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private void uploadImageToFirebaseStorage() {
        //StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("immaginiprofilo/"+System.currentTimeMillis()+ ".jpg");

        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("immaginiprofilo/" + System.currentTimeMillis() + ".jpg");

        //CARICAMENTO EVENTO
        if (mImageUri != null) {

            mUploadTask = profileImageRef.putFile(mImageUri); //VIENE INSERITO IL FILE NELLO STORAGE
            Task<Uri> urlTask = mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return profileImageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult(); //LINK SFONDO
                        image_url = downloadUri.toString();
                        saveInformation();
                        //VENGONO CARICATI TUTTI I CAMPI DELL'EVENTO
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

        }
    }
    private void saveInformation(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null && image_url != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(image_url)).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if(user.getPhotoUrl() != null){
                            Picasso.get().load(user.getPhotoUrl()).into(nav_profile_image);
                        }
                    }
                }
            });
        }
    }


}
