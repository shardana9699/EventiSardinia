package com.eventisardegna.shardana.eventisardinia;


import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
        import android.os.Bundle;
import android.view.View;
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.GridView;
        import android.widget.ImageButton;
        import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
        import java.util.GregorianCalendar;

public class ProfileAdmin extends AppCompatActivity implements View.OnClickListener {
    public GregorianCalendar mese_calendario;
    private AdaptCalendario adaptCalendario;
    private TextView testo_mese;
    private Button button_map;
    private Button button_logout;
    private Button button_aggiungi_evento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //INIZIALIZZAZIONE EVENTO
        DatabaseEvento.date_collection_arr=new ArrayList<DatabaseEvento>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();

        //POPOLAZIONE ARRAY EVENTI
        databaseReference.child("Eventi").addValueEventListener(new ValueEventListener() {

            /**
             * This method will be invoked any time the data on the database changes.
             * Additionally, it will be invoked as soon as we connect the listener, so that we can get an initial snapshot of the data on the database.
             * @param dataSnapshot
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get all of the children at this level.
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                // shake hands with each of them.'
                for (DataSnapshot child : children) {
                    DatabaseEvento databaseEvento = child.getValue(DatabaseEvento.class);
                    DatabaseEvento.date_collection_arr.add(databaseEvento);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        button_map = (Button) findViewById(R.id.map);
        button_logout = (Button) findViewById(R.id.logout);
        button_aggiungi_evento = (Button)findViewById(R.id.addEvents);

        mese_calendario = (GregorianCalendar) GregorianCalendar.getInstance();
        adaptCalendario = new AdaptCalendario(this, mese_calendario, DatabaseEvento.date_collection_arr);

        testo_mese = (TextView) findViewById(R.id.tv_month);
        testo_mese.setText(android.text.format.DateFormat.format("MMMM yyyy", mese_calendario));


        ImageButton previous = (ImageButton) findViewById(R.id.ib_prev);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreviousMonth();
                refreshCalendar();
            }
        });
        ImageButton next = (ImageButton) findViewById(R.id.Ib_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextMonth();
                refreshCalendar();
            }
        });
        GridView gridview = (GridView) findViewById(R.id.gv_calendar);
        gridview.setAdapter(adaptCalendario);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String selectedGridDate = AdaptCalendario.day_string.get(position);
                ((AdaptCalendario) parent.getAdapter()).getPositionList(selectedGridDate, ProfileAdmin.this);
            }

        });

        button_map.setOnClickListener(this);
        button_logout.setOnClickListener(this);
        button_aggiungi_evento.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if(v == button_map){
            startActivity(new Intent(this, ActivityMaps.class));
        }
        if(v == button_logout){
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, ActivityLogin.class));
        }
        if(v == button_aggiungi_evento){
            startActivity(new Intent(this, ActivityAdmin.class));

        }
    }
}
