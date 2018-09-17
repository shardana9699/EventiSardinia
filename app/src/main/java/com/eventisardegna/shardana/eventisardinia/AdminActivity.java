package com.eventisardegna.shardana.eventisardinia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class AdminActivity extends AppCompatActivity implements View.OnClickListener{


    private DatabaseReference databaseReference;
    private EditText editDate;
    private EditText editTitolo;
    private EditText editLuogo;
    private Button buttonEvent, getPlace;
    public Double latitude;
    public Double longitude;
    private CalendarView mCalendar;
    int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        databaseReference = FirebaseDatabase.getInstance().getReference();
        editDate = (EditText) findViewById(R.id.editDate);
        editTitolo = (EditText) findViewById(R.id.editTitolo);
        editLuogo = (EditText) findViewById(R.id.editLuogo);
        buttonEvent = (Button) findViewById(R.id.buttonEvent);
        getPlace = (Button) findViewById(R.id.getPlace);

        buttonEvent.setOnClickListener(this);
        getPlace.setOnClickListener(this);

    }

    private void admin(){
        String date = editDate.getText().toString().trim();
        String titolo = editTitolo.getText().toString().trim();
        String luogo = editLuogo.getText().toString().trim();

        HomeCollection homeCollection = new HomeCollection(date, titolo, latitude, longitude, luogo);
       // Event event = new Event(date, name, subject, description);

        databaseReference.child("Eventi").push().setValue(homeCollection);

        Toast.makeText(this, "Informazioni Salvate", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View view) {
        if(view == buttonEvent){
            admin();
        }
        if(view == getPlace){
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            try{
                startActivityForResult(builder.build(AdminActivity.this), PLACE_PICKER_REQUEST);
            }
            catch(GooglePlayServicesRepairableException e){
                e.printStackTrace();
            }
            catch (GooglePlayServicesNotAvailableException e ){
                e.printStackTrace();
            }
        }

    }

   /* public void goPlacePicker (View view){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try{
            startActivityForResult(builder.build(AdminActivity.this), PLACE_PICKER_REQUEST);
        }
        catch(GooglePlayServicesRepairableException e){
            e.printStackTrace();
        }
        catch (GooglePlayServicesNotAvailableException e ){
            e.printStackTrace();
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PLACE_PICKER_REQUEST){
            Place place = PlacePicker.getPlace(AdminActivity.this, data);
            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;
        }
    }

}

