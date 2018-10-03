package com.eventisardegna.shardana.eventisardinia;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener , NavigationView.OnNavigationItemSelectedListener {
    public GregorianCalendar cal_month, cal_month_copy;
    private HwAdapter hwAdapter;
    private TextView tv_month;
    private Button map;
    private Button logout;
    private Button calendario;
    private ArrayList<Dialogpojo> arrayEvento = new ArrayList<Dialogpojo>();
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

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

        map = (Button) findViewById(R.id.map);
        logout = (Button) findViewById(R.id.logout);
        calendario = (Button) findViewById(R.id.calendario);
        final ListView eventi = (ListView) findViewById(R.id.row_add);

        databaseReference.child("Eventi").addValueEventListener(new ValueEventListener() {


            public void onDataChange(DataSnapshot dataSnapshot) {
                // get all of the children at this level.
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // shake hands with each of them.'
                for (DataSnapshot child : children) {
                    HomeCollection homeCollection = child.getValue(HomeCollection.class);

                    Dialogpojo evento = new Dialogpojo();

                    evento.setTitles(homeCollection.date);
                    evento.setDescripts(homeCollection.luogo);
                    evento.setSubjects(homeCollection.titolo);
                    EventAdapter eventAdapter = new EventAdapter(ProfileActivity.this, arrayEvento);
                    eventi.setAdapter(eventAdapter);
                    eventAdapter.add(evento);
                    eventAdapter.notifyDataSetChanged();

                }
            }

            public void onCancelled(DatabaseError databaseError) {

            }
        });

        map.setOnClickListener(this);
        logout.setOnClickListener(this);
        calendario.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == map){
            finish();
            startActivity(new Intent(this, MapsActivity.class));
        }
        if(v == logout){
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        if(v == calendario){

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

}
