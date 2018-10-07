package com.eventisardegna.shardana.eventisardinia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener{


    private DatabaseReference databaseReference;
    private EditText editDate;
    private EditText editTitolo;
    private EditText editLuogo;
    private EditText editDescrizione;
    public String luogo;
    public String prenotazioni = "";
    private Button buttonEvent, getPlace, scegliSfondo;
    public Double latitude;
    public Double longitude;
    private CalendarView mCalendar;
    int PLACE_PICKER_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Uri mImageUri;
    private ImageView ima;

    private StorageReference mStorageRef;
    private UploadTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        databaseReference = FirebaseDatabase.getInstance().getReference();
        editDate = (EditText) findViewById(R.id.editDate);
        editTitolo = (EditText) findViewById(R.id.editTitolo);
        editDescrizione = (EditText) findViewById(R.id.editDescrizione);
        //editLuogo = (EditText) findViewById(R.id.editLuogo);
        buttonEvent = (Button) findViewById(R.id.buttonEvent);
        getPlace = (Button) findViewById(R.id.getPlace);
        scegliSfondo = (Button) findViewById(R.id.sfondo);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        buttonEvent.setOnClickListener(this);
        getPlace.setOnClickListener(this);
        scegliSfondo.setOnClickListener(this);

    }

    private void admin(){
        final String date = editDate.getText().toString().trim();
        final String titolo = editTitolo.getText().toString().trim();
        final String descrizione = editDescrizione.getText().toString().trim();
        //String luogo = editLuogo.getText().toString().trim();
        if (mImageUri != null) {
            final StorageReference ref = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = ref.putFile(mImageUri);
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
                        Uri downloadUri = task.getResult();
                        HomeCollection upload = new HomeCollection(date, titolo, latitude, longitude, luogo, prenotazioni, downloadUri.toString(), descrizione);
                        databaseReference.child("Eventi").child(titolo).setValue(upload);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }

        Toast.makeText(this, "Informazioni Salvate", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View view) {
        if(view == buttonEvent){
            admin();
            finish();
            startActivity(new Intent(getApplicationContext(),AdminProfile.class));
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
        if(view == scegliSfondo){
            openFileChooser();
        }

    }

    private void openFileChooser(){
        Intent intent = new Intent ();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PLACE_PICKER_REQUEST){
            Place place = PlacePicker.getPlace(AdminActivity.this, data);
            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;
            luogo = (String) place.getName();
        }
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
        }
    }

}

