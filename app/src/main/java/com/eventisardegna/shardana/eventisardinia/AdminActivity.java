package com.eventisardegna.shardana.eventisardinia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AdminActivity extends AppCompatActivity implements View.OnClickListener{


    private DatabaseReference databaseReference;
    private EditText editDate;
    private EditText editTitolo;
    private EditText editLuogo;
    private Button buttonEvent;
    private CalendarView mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        editDate = (EditText) findViewById(R.id.editDate);
        editTitolo = (EditText) findViewById(R.id.editTitolo);
        editLuogo = (EditText) findViewById(R.id.editLuogo);
        buttonEvent = (Button) findViewById(R.id.buttonEvent);

        buttonEvent.setOnClickListener(this);

    }

    private void admin(){
        String date = editDate.getText().toString().trim();
        String titolo = editTitolo.getText().toString().trim();
        String luogo = editLuogo.getText().toString().trim();

        HomeCollection homeCollection = new HomeCollection(date, titolo, luogo);
       // Event event = new Event(date, name, subject, description);

        databaseReference.child("Eventi").push().setValue(homeCollection);

        Toast.makeText(this, "Informazioni Salvate", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View view) {
        if(view == buttonEvent){
            admin();
        }

    }

}

