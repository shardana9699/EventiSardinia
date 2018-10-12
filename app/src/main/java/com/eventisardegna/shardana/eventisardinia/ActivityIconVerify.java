package com.eventisardegna.shardana.eventisardinia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class ActivityIconVerify extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int CHOOSE_IMAGE = 101;
    private Button button_inserisci_profilo, button_inizia, button_verifica;
    private ImageView imageView;
    private Uri mImageUri;
    public String image_url;
    private UploadTask mUploadTask;
    Boolean doubleTap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_verify);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null) {
            if (firebaseAuth.getCurrentUser() != null && user.isEmailVerified()) {
                //Attivita del profilo
                finish();
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();

        button_inserisci_profilo = findViewById(R.id.button_carica_profilo);
        button_inizia = findViewById(R.id.button_iscrizione_completa);
        button_verifica = findViewById(R.id.button_verifica_account);
        imageView = findViewById(R.id.icona_profilo);

        button_inserisci_profilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        button_verifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ActivityIconVerify.this, "Email di Verifica Inviata", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        button_inizia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInformation();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    if(user.isEmailVerified()){
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                    }
                    else{
                        Toast.makeText(ActivityIconVerify.this, "Non hai ancora verificato la Email", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
    private void openFileChooser(){
        //INTENT PER SCEGLIERE IMMAGINE
        Intent intent = new Intent ();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleziona immagine profilo"), CHOOSE_IMAGE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //VIENE ASSEGNATO IL LINK DELL'IMMAGINE
        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
            }
        }


        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
            imageView.setImageBitmap(bitmap);
            uploadImageToFirebaseStorage();

        }catch (IOException e){
            e.printStackTrace();
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

                    }
                }
            });
        }
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
}
