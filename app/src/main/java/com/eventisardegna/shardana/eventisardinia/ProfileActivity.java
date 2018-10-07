package com.eventisardegna.shardana.eventisardinia;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener , NavigationView.OnNavigationItemSelectedListener {
    public GregorianCalendar cal_month, cal_month_copy;
    private HwAdapter hwAdapter;
    private TextView tv_month;
    private Button map;
    private Button calendario;
    private ArrayList<Dialogpojo> arrayEvento = new ArrayList<Dialogpojo>();
    ActionBarDrawerToggle toggle;
    private ImageAdapter mAdapter;
    private List<Dialogpojo> mUploads;
    private RecyclerView mRecyclerView;
    private ImageView immma;
    public String message;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        HomeCollection.date_collection_arr = new ArrayList<HomeCollection>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();

        databaseReference.child("Eventi").addValueEventListener(new ValueEventListener() {


            public void onDataChange(DataSnapshot dataSnapshot) {
                // get all of the children at this level.
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // shake hands with each of them.'
                for (DataSnapshot child : children) {
                    HomeCollection homeCollection = child.getValue(HomeCollection.class);
                    HomeCollection.date_collection_arr.add(homeCollection);
                }
            }

            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRecyclerView = findViewById(R.id.row_adda);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>();
        FirebaseRecyclerAdapter<Model, ViewHolder> FirebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Model, ViewHolder>(
                        Model.class, R.layout.lista_eventi, ViewHolder.class, databaseReference.child("Eventi")
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Model model, int position) {

                        viewHolder.setDetails(getApplicationContext(), model.getTitolo(), model.getLuogo(), model.getmImageUrl());
                    }

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);

                        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void OnItemClick(View view, int position) {

                                String mTitolo = getItem(position).getTitolo();
                                String mLuogo = getItem(position).getLuogo();
                                String mDescrizione = getItem(position).getDescrizione();
                                String mImage = getItem(position).getmImageUrl();
                                Intent intent = new Intent(view.getContext(), DettagliEvento.class);
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

                        return viewHolder;
                    }
                };



        mRecyclerView.setAdapter(FirebaseRecyclerAdapter);


        /*databaseReference.child("Eventi").addValueEventListener(new ValueEventListener() {


            public void onDataChange(DataSnapshot dataSnapshot) {
                // get all of the children at this level.
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // shake hands with each of them.'
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    HomeCollection homeCollection = postSnapshot.getValue(HomeCollection.class);

                    Dialogpojo evento = new Dialogpojo();

                    evento.setTitles(homeCollection.date);
                    evento.setDescripts(homeCollection.luogo);
                    evento.setSubjects(homeCollection.titolo);
                    evento.setImage(homeCollection.mImageUrl);
                    mUploads.add(evento);
                }
                ImageAdapter imageAdapter = new ImageAdapter(ProfileActivity.this, mUploads);
                mRecyclerView.setAdapter(imageAdapter);
                    //eventi.add(imageAdapter);
                    //eventi.notifyDataSetChanged();
            }

            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    @Override
    public void onClick(View v) {

    }


    protected void setNextMonth() {
        if (cal_month.get(GregorianCalendar.MONTH) == cal_month.getActualMaximum(GregorianCalendar.MONTH)) {
            cal_month.set((cal_month.get(GregorianCalendar.YEAR) + 1), cal_month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            cal_month.set(GregorianCalendar.MONTH,
                    cal_month.get(GregorianCalendar.MONTH) + 1);
        }
    }

    protected void setPreviousMonth() {
        if (cal_month.get(GregorianCalendar.MONTH) == cal_month.getActualMinimum(GregorianCalendar.MONTH)) {
            cal_month.set((cal_month.get(GregorianCalendar.YEAR) - 1), cal_month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            cal_month.set(GregorianCalendar.MONTH, cal_month.get(GregorianCalendar.MONTH) - 1);
        }
    }

    public void refreshCalendar() {
        hwAdapter.refreshDays();
        hwAdapter.notifyDataSetChanged();
        tv_month.setText(android.text.format.DateFormat.format("MMMM yyyy", cal_month));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_logout){
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.calendario){
            final Dialog dialogs = new Dialog(this);
            dialogs.setContentView(R.layout.activity_cal);

            cal_month = (GregorianCalendar) GregorianCalendar.getInstance();
            cal_month_copy = (GregorianCalendar) cal_month.clone();
            hwAdapter = new HwAdapter(this, cal_month,HomeCollection.date_collection_arr);
            tv_month = (TextView) dialogs.findViewById(R.id.tv_month);
            tv_month.setText(android.text.format.DateFormat.format("MMMM yyyy", cal_month));

            ImageButton previous = (ImageButton) dialogs.findViewById(R.id.ib_prev);
            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cal_month.get(GregorianCalendar.MONTH) == 4&&cal_month.get(GregorianCalendar.YEAR)==2018) {
                        //cal_month.set((cal_month.get(GregorianCalendar.YEAR) - 1), cal_month.getActualMaximum(GregorianCalendar.MONTH), 1);
                        //Toast.makeText(ProfileActivity.this, "Event Detail is available for current session only.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        setPreviousMonth();
                        refreshCalendar();
                    }

                }
            });
            ImageButton next = (ImageButton) dialogs.findViewById(R.id.Ib_next);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cal_month.get(GregorianCalendar.MONTH) == 5&&cal_month.get(GregorianCalendar.YEAR)==2020) {
                        //cal_month.set((cal_month.get(GregorianCalendar.YEAR) + 1), cal_month.getActualMinimum(GregorianCalendar.MONTH), 1);
                        //Toast.makeText(ProfileActivity.this, ".", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        setNextMonth();
                        refreshCalendar();
                    }
                }
            });
            GridView gridview = (GridView) dialogs.findViewById(R.id.gv_calendar);
            gridview.setAdapter(hwAdapter);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    String selectedGridDate = HwAdapter.day_string.get(position);
                    ((HwAdapter) parent.getAdapter()).getPositionList(selectedGridDate, ProfileActivity.this);
                }

            });

            setNextMonth();

            setPreviousMonth();

            refreshCalendar();

            dialogs.show();

        }
        if(id == R.id.map){
            startActivity(new Intent(this, MapsActivity.class));
        }
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
