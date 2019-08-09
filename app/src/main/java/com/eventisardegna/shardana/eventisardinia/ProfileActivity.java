package com.eventisardegna.shardana.eventisardinia;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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
    private FirebaseStorage mStorage;
    private UploadTask mUploadTask;
    private View EventiView;
    private RecyclerView listaEventiView;
    private DatabaseReference databaseReference;
    private ArrayList<DatabaseEvento> eventi = new ArrayList<DatabaseEvento>();
    private AdaptEvento adapter;
    private AdaptEvento.OnItemClickListener itemClickListener;
    View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        listaEventiView = (RecyclerView) findViewById(R.id.row_adda);
        listaEventiView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        FirebaseMessaging.getInstance().subscribeToTopic("MyTopic");


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            if (user.isEmailVerified()) {

            } else {
                startActivity(new Intent(getApplicationContext(), ActivityIconVerify.class));
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
        if (user.getPhotoUrl() != null) {
            Picasso.get().load(user.getPhotoUrl()).into(nav_profile_image);
        }
        nav_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(60, 60)
                        .start(ProfileActivity.this);
            }
        });
        nav_nome_utente = (TextView) navView.findViewById(R.id.tendina_nome);
        nav_nome_utente.setText(user.getDisplayName());
        nav_email_utente = (TextView) navView.findViewById(R.id.tendina_email);
        nav_email_utente.setText(user.getEmail());

        eventi = new ArrayList<DatabaseEvento>();


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

            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeData(dataSnapshot);
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            public void onCancelled(DatabaseError databaseError) {
            }


        });

        //POPOLAZIONE INTERFACCIA SCORREVOLE DEGLI EVENTI


        //mUploads = new ArrayList<>();


    }

    public void loadData(DataSnapshot dataSnapshot) {
        // get all of the children at this level.
        int flag = 0;
        DatabaseEvento doc = dataSnapshot.getValue(DatabaseEvento.class);
        DatabaseEvento eve = dataSnapshot.getValue(DatabaseEvento.class);
        for(DatabaseEvento eventi: eventi){
            if(eventi.getTitolo().equals(doc.getTitolo())){
                flag = 1;
            }
        }
        if(flag == 0) {
            DatabaseEvento.date_collection_arr.add(eve);
            eventi.add(doc);
        }

        adapter = new AdaptEvento(ProfileActivity.this, eventi, itemClickListener);
        //listaEventiView.setAdapter(adapter);
        listaEventiView.setAdapter(new AdaptEvento(ProfileActivity.this, eventi, new AdaptEvento.OnItemClickListener() {
            @Override public void onItemClick(DatabaseEvento item) {

                String mTitolo = item.getTitolo();
                String mLuogo = item.getLuogo();
                String mDescrizione = item.getDescrizione();
                String mImage = item.getImmagine();
                Intent intent = new Intent(listaEventiView.getContext(), ActivityDettagliEvento.class);
                intent.putExtra("title", mTitolo);
                intent.putExtra("description", mLuogo);
                intent.putExtra("descrizione", mDescrizione);
                intent.putExtra("image", mImage);
                startActivity(intent);
            }


        }));
    }
    public void removeData(DataSnapshot dataSnapshot) {
        // get all of the children at this level.

        DatabaseEvento doc = dataSnapshot.getValue(DatabaseEvento.class);
        DatabaseEvento eve = dataSnapshot.getValue(DatabaseEvento.class);
        DatabaseEvento.date_collection_arr.remove(eve);
        eventi.remove(doc);

        adapter = new AdaptEvento(ProfileActivity.this, eventi, itemClickListener);
        listaEventiView.setAdapter(adapter);
    }




    //RICERCA EVENTO
    private void firebaseSearch(String searchText) {


        final String query = searchText.toLowerCase();


        mRef = FirebaseDatabase.getInstance().getReference().child("Eventi");

       // Query firebaseSearchQuery = mRef.orderByChild("luogo").startAt(query).endAt(query + "\uf0ff");

        ValueEventListener firebaseSearchQuery = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    eventi.clear();
                    for(DataSnapshot dss : dataSnapshot.getChildren()){
                            String luogo = dss.child("luogo").getValue(String.class);
                            if(luogo.contains(query)) {
                                final DatabaseEvento databaseEvento = dss.getValue(DatabaseEvento.class);
                                eventi.add(databaseEvento);
                            }
                    }
                }
                adapter = new AdaptEvento(ProfileActivity.this, eventi, itemClickListener);
                listaEventiView.setAdapter(new AdaptEvento(ProfileActivity.this, eventi, new AdaptEvento.OnItemClickListener() {
                    @Override public void onItemClick(DatabaseEvento item) {

                        String mTitolo = item.getTitolo();
                        String mLuogo = item.getLuogo();
                        String mDescrizione = item.getDescrizione();
                        String mImage = item.getImmagine();
                        Intent intent = new Intent(listaEventiView.getContext(), ActivityDettagliEvento.class);
                        intent.putExtra("title", mTitolo);
                        intent.putExtra("description", mLuogo);
                        intent.putExtra("descrizione", mDescrizione);
                        intent.putExtra("image", mImage);
                        startActivity(intent);
                    }


                }));
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        };
        mRef.addListenerForSingleValueEvent(firebaseSearchQuery);
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

        if (id == R.id.nav_my_account) {
            finish();
            startActivity(new Intent(this, ActivityModificaProfilo.class));
        }
        if (id == R.id.nav_eventi_prenotati) {
            finish();
            startActivity(new Intent(this, ActivityEventiPrenotati.class));
        }

        if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, ActivityLogin.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        if (id == R.id.calendario) {
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
        if (id == R.id.map) {
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
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
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

            mStorage = FirebaseStorage.getInstance();
            final FirebaseUser user = firebaseAuth.getCurrentUser();
            StorageReference imageRef = mStorage.getReferenceFromUrl(user.getPhotoUrl().toString());
            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });

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

    private void saveInformation() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null && image_url != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(image_url)).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user.getPhotoUrl() != null) {
                            Picasso.get().load(user.getPhotoUrl()).into(nav_profile_image);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleTap) {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        } else {
            Toast.makeText(this, "Premi indietro di nuovo per uscire dall'applicazione!", Toast.LENGTH_SHORT).show();
            doubleTap = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleTap = false;
                }
            }, 1000);
        }
    }


}