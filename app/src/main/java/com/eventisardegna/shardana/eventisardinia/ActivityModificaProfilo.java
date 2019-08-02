package com.eventisardegna.shardana.eventisardinia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class ActivityModificaProfilo extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int CHOOSE_IMAGE = 101;
    private Button button_modifica_immagine;
    private Button button_salva_profilo;
    private ImageView imageView;
    private Uri mImageUri;
    private EditText nome_utente;
    private EditText email_utente;
    public String image_url;
    private UploadTask mUploadTask;
    private FirebaseStorage mStorage;
    Boolean doubleTap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_profilo);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        button_modifica_immagine = findViewById(R.id.button_modifica_immagine);
        imageView = findViewById(R.id.immagine_profilo);

        nome_utente = (EditText) findViewById(R.id.editNome);
        email_utente = (EditText) findViewById(R.id.editEmail);
        button_salva_profilo = (Button) findViewById(R.id.button_salva_profilo);

        databaseReference.child("UserID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // shake hands with each of them.'
                for (DataSnapshot child : children) {
                    String email = (String) dataSnapshot.getValue().toString().trim();
                    if (email.contains(user.getEmail())) {
                            DatabaseUtente databaseUtente = dataSnapshot.getValue(DatabaseUtente.class);
                            nome_utente.setText(user.getDisplayName());
                            email_utente.setText(user.getEmail());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        button_modifica_immagine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openFileChooser();
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(60,60)
                        .start(ActivityModificaProfilo.this);
                saveInformation();
                /*
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    if(user.isEmailVerified()){
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                    }
                    else{
                        Toast.makeText(ActivityIconVerify.this, "Non hai ancora verificato la Email", Toast.LENGTH_SHORT).show();
                    }
                }*/
            }
        });

        button_salva_profilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nome = nome_utente.getText().toString();
                String[] parts = nome.split(" ");
                String name = parts[0];
                String cognome = parts[1];
                String email = email_utente.getText().toString();
                databaseReference.child("UserID").child("Utenti").child(user.getDisplayName()).removeValue();
                DatabaseUtente databaseUtente = new DatabaseUtente(name, cognome, email);
                if (user != null) {
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name + " " + cognome).build();
                    user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            }
                        }
                    });
                }
                databaseReference.child("UserID").child("Utenti").child(nome).setValue(databaseUtente);
                finish();
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
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

            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
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
