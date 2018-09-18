package com.eventisardegna.shardana.eventisardinia;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

class DialogAdaptorStudent extends BaseAdapter {
    Activity activity;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Activity context;
    private ArrayList<Dialogpojo> alCustom;
    private String sturl;


    public DialogAdaptorStudent(Activity context, ArrayList<Dialogpojo> alCustom) {
        this.context = context;
        this.alCustom = alCustom;

    }

    @Override
    public int getCount() {
        return alCustom.size();

    }

    @Override
    public Object getItem(int i) {
        return alCustom.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){


        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        LayoutInflater inflater = context.getLayoutInflater();
        final View listViewItem = inflater.inflate(R.layout.row_addapt, null, true);

        //TextView tvTitle=(TextView)listViewItem.findViewById(R.id.tv_section);
        TextView tvSubject=(TextView)listViewItem.findViewById(R.id.tv_type);
       // TextView tvDuedate=(TextView)listViewItem.findViewById(R.id.tv_desc);
        TextView tvDescription=(TextView)listViewItem.findViewById(R.id.tv_class);
        Button prenota = (Button) listViewItem.findViewById(R.id.prenota);

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        prenota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("prenotazioni").push().setValue(alCustom.get(position).getSubjects());
            }
        });
        //tvTitle.setText(alCustom.get(position).getTitles());
        tvSubject.setText(alCustom.get(position).getSubjects());
        //tvDuedate.setText("Due Datefrvrrvrv : "+alCustom.get(position).getDuedates());
        tvDescription.setText(alCustom.get(position).getDescripts());


        return  listViewItem;
    }


}

