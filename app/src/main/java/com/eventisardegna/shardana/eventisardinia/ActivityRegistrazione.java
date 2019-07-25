package com.eventisardegna.shardana.eventisardinia;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityRegistrazione extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPec;
    private EditText editTextData;
    private EditText editTextLuogo;
    private EditText editTextResidenza;
    private EditText editTextPassword;
    private TextView textViewSignIn;
    private EditText editTextName,editTextCognome, editTextPhone;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String tipoUtente="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //Attivita del profilo
            finish();
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextCognome = (EditText) findViewById(R.id.editTextCognome);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPec = (EditText) findViewById(R.id.pec);
        editTextData = (EditText) findViewById(R.id.editTextData);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextLuogo = (EditText) findViewById(R.id.editTextLuogo);
        editTextResidenza = (EditText) findViewById(R.id.editTextResidenza);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSignIn = (TextView) findViewById(R.id.textViewSignIn);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_nuovoUtente, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        progressDialog = new ProgressDialog(this);

        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
    }

    private void registerUser(){
        String email;
        if(tipoUtente == "Utente"){
            email = editTextEmail.getText().toString().trim();
        }
        else{
            email = editTextPec.getText().toString().trim();
        }
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email) && TextUtils.isEmpty(email)){
            Toast.makeText(this,"Inserisci l'email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Inserisci la password",Toast.LENGTH_SHORT).show();
            return;

        }

        progressDialog.setMessage("Registrazione in corso...");
        progressDialog.show();

        //creazione nuovo utente

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Attivita del profilo
                        saveUserInformation();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ActivityRegistrazione.this, "Email di Verifica Inviata", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        finish();
                        startActivity(new Intent(getApplicationContext(), ActivityIconVerify.class));
                    } else {
                        Toast.makeText(ActivityRegistrazione.this, "Errore nella registrazione,Riprova", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            });


    }

    private void saveUserInformation() {
        if (tipoUtente.equals( "Utente")) {
            String name = editTextName.getText().toString().trim();
            String cognome = editTextCognome.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            DatabaseUtente databaseUtente = new DatabaseUtente(name, cognome, email);

            FirebaseUser user = firebaseAuth.getCurrentUser();
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
            databaseReference.child("UserID").child("Utenti").child(name + " " + cognome).setValue(databaseUtente);
            Toast.makeText(this, "Informazioni Salvate", Toast.LENGTH_LONG).show();
        }
        if (tipoUtente.equals( "Espositore")) {
            String name = editTextName.getText().toString().trim();
            String cognome = editTextCognome.getText().toString().trim();
            String pec = editTextPec.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String data = editTextData.getText().toString().trim();
            String luogo = editTextLuogo.getText().toString().trim();
            String residenza = editTextResidenza.getText().toString().trim();
            DatabaseUtente databaseUtente = new DatabaseUtente(name, cognome, pec, phone, data, luogo, residenza);

            FirebaseUser user = firebaseAuth.getCurrentUser();
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
            databaseReference.child("UserID").child("Espositori").child(name + " " + cognome).setValue(databaseUtente);
            Toast.makeText(this, "Informazioni Salvate", Toast.LENGTH_LONG).show();
        }
        if (tipoUtente.equals( "Organizzatore")) {
            String name = editTextName.getText().toString().trim();
            String cognome = editTextCognome.getText().toString().trim();
            String pec = editTextPec.getText().toString().trim();
            //String phone = editTextPhone.getText().toString().trim();
            String data = editTextData.getText().toString().trim();
            String luogo = editTextLuogo.getText().toString().trim();
            String residenza = editTextResidenza.getText().toString().trim();
            DatabaseUtente databaseUtente = new DatabaseUtente(name, cognome, pec, data, luogo, residenza);

            FirebaseUser user = firebaseAuth.getCurrentUser();
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
            databaseReference.child("UserID").child("Organizzatori").child(name + " " + cognome).setValue(databaseUtente);
            Toast.makeText(this, "Informazioni Salvate", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View view) {
        if(view == buttonRegister){
            registerUser();

        }

        if(view == textViewSignIn){
            startActivity(new Intent(this,ActivityLogin.class));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();

        switch (text) {
            case "Utente":
                editTextEmail.setVisibility(View.VISIBLE);
                editTextPassword.setVisibility(View.VISIBLE);
                editTextPec.setVisibility(View.GONE);
                editTextPhone.setVisibility(View.GONE);
                editTextData.setVisibility(View.GONE);
                editTextLuogo.setVisibility(View.GONE);
                editTextResidenza.setVisibility(View.GONE);
                tipoUtente = "Utente";
                break;
            case "Espositore":
                editTextEmail.setVisibility(View.GONE);
                editTextPec.setVisibility(View.VISIBLE);
                editTextPassword.setVisibility(View.VISIBLE);
                editTextPhone.setVisibility(View.VISIBLE);
                editTextData.setVisibility(View.VISIBLE);
                editTextLuogo.setVisibility(View.VISIBLE);
                editTextResidenza.setVisibility(View.VISIBLE);
                tipoUtente = "Espositore";
                break;
            case "Organizzatore":
                editTextEmail.setVisibility(View.GONE);
                editTextPec.setVisibility(View.VISIBLE);
                editTextPassword.setVisibility(View.VISIBLE);
                editTextPhone.setVisibility(View.VISIBLE);
                editTextData.setVisibility(View.VISIBLE);
                editTextLuogo.setVisibility(View.VISIBLE);
                editTextResidenza.setVisibility(View.VISIBLE);
                tipoUtente = "Organizzatore";
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

