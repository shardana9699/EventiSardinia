package com.eventisardegna.shardana.eventisardinia;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eventisardegna.shardana.eventisardinia.Model.MyResponse;
import com.eventisardegna.shardana.eventisardinia.Model.Notification;
import com.eventisardegna.shardana.eventisardinia.Model.Sender;
import com.eventisardegna.shardana.eventisardinia.Remote.APIService;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityAdmin extends AppCompatActivity implements View.OnClickListener{


    private DatabaseReference databaseReference;
    private EditText editDate;
    private EditText editTitolo;
    private EditText editDescrizione;
    public String luogo;
    public HashMap<String, String> prenotazioni = new HashMap<>();
    private Button buttonEvent, getPlace, scegliSfondo;
    public Double latitude;
    public Double longitude;
    int PLACE_PICKER_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private UploadTask mUploadTask;
    private TextView boxData;
    private String data2;
    private static final String TAG = "ActivityAdmin";
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_evento);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Viene ottenuto il token tramite l'id dell'utente loggato
        Common.currentToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("MyTopic");
        mService = Common.getFCMClient();

        databaseReference = FirebaseDatabase.getInstance().getReference(); //carica database
        boxData = (TextView) findViewById(R.id.editDate);
        editTitolo = (EditText) findViewById(R.id.editTitolo);
        editDescrizione = (EditText) findViewById(R.id.editDescrizione);
        buttonEvent = (Button) findViewById(R.id.buttonEvent);
        getPlace = (Button) findViewById(R.id.getPlace);
        scegliSfondo = (Button) findViewById(R.id.sfondo);
        boxData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ActivityAdmin.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                //Log.d(TAG, "onDateSet: dd/mm/yyyy: " + month + "-" + day + "-" + year);
                String date;
                if(month>9 && day > 9) {
                    date = day + "-" + month + "-" + year;
                    data2 = date;
                }else if(month > 9 && day < 10){

                    date = day + "-" + month + "-" + year;
                    data2 = "0"+day + "-" + month + "-" + year;
                }
                else if(month < 10 && day > 9){
                    date = day + "-" + month + "-" + year;
                    data2 = day + "-" + "0"+ month + "-" + year;
                }else{
                    date = day + "-" + month + "-" + year;
                    data2 = "0"+day + "-" + "0"+ month + "-" + year;
                }

                boxData.setText(date);


            }
        };
        mStorageRef = FirebaseStorage.getInstance().getReference(); //caricamento file dal database

        buttonEvent.setOnClickListener(this);
        getPlace.setOnClickListener(this);
        scegliSfondo.setOnClickListener(this);

    }

    private void admin(){

        //viene assegnato il testo inserito dall'admin
        //final String date = editDate.getText().toString().trim();
        final String titolo = editTitolo.getText().toString().trim();
        final String descrizione = editDescrizione.getText().toString().trim();

        //CARICAMENTO EVENTO
        if (mImageUri != null) {

            final StorageReference ref = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = ref.putFile(mImageUri); //VIENE INSERITO IL FILE NELLO STORAGE
            Task<Uri> urlTask = mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult(); //LINK SFONDO
                        //VENGONO CARICATI TUTTI I CAMPI DELL'EVENTO
                        DatabaseEvento upload = new DatabaseEvento(data2, titolo, latitude, longitude, luogo.toLowerCase(), prenotazioni, downloadUri.toString(), descrizione);
                        databaseReference.child("Eventi").child(titolo).setValue(upload);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
        Notification notification = new Notification(titolo, descrizione);
        Sender sender = new Sender("/topics/MyTopic", notification);
        mService.sendNotification(sender)
                .enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                         Log.e("ERROR",t.getMessage());
                    }
                });

        Toast.makeText(this, "Informazioni Salvate", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View view) {

        //CARICA EVENTO
        if(view == buttonEvent){
            admin();
            finish();
            startActivity(new Intent(getApplicationContext(),ProfileAdmin.class));
        }

        //SCEGLI POSIZIONE
        if(view == getPlace){
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            try{
                startActivityForResult(builder.build(ActivityAdmin.this), PLACE_PICKER_REQUEST);
            }
            catch(GooglePlayServicesRepairableException e){
                e.printStackTrace();
            }
            catch (GooglePlayServicesNotAvailableException e ){
                e.printStackTrace();
            }
        }

        //SCEGLI SFONDO
        if(view == scegliSfondo){
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(380,150)
                    .start(this);
        }

    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //VENGONO ASSEGNATI I VALORI DEL LUOGO
        if(requestCode == PLACE_PICKER_REQUEST){
            Place place = PlacePicker.getPlace(ActivityAdmin.this, data);
            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;
            luogo = (String) place.getName();
        }
        //VIENE ASSEGNATO IL LINK DELL'IMMAGINE
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}

