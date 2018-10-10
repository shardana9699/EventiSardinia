package com.eventisardegna.shardana.eventisardinia;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ActivityAdmin extends AppCompatActivity implements View.OnClickListener{


    private DatabaseReference databaseReference;
    private EditText editDate;
    private EditText editTitolo;
    private EditText editDescrizione;
    public String luogo;
    public String prenotazioni = "";
    private Button buttonEvent, getPlace, scegliSfondo;
    public Double latitude;
    public Double longitude;
    int PLACE_PICKER_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private UploadTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_evento);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        databaseReference = FirebaseDatabase.getInstance().getReference();
        editDate = (EditText) findViewById(R.id.editDate);
        editTitolo = (EditText) findViewById(R.id.editTitolo);
        editDescrizione = (EditText) findViewById(R.id.editDescrizione);
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
                        DatabaseEvento upload = new DatabaseEvento(date, titolo, latitude, longitude, luogo.toLowerCase(), prenotazioni, downloadUri.toString(), descrizione);
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
            openFileChooser();
        }

    }

    private void openFileChooser(){
        //INTENT PER SCEGLIERE IMMAGINE
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
        //VENGONO ASSEGNATI I VALORI DEL LUOGO
        if(requestCode == PLACE_PICKER_REQUEST){
            Place place = PlacePicker.getPlace(ActivityAdmin.this, data);
            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;
            luogo = (String) place.getName();
        }
        //VIENE ASSEGNATO IL LINK DELL'IMMAGINE
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(380,150)
                    .start(this);
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
            }
        }
    }

}

