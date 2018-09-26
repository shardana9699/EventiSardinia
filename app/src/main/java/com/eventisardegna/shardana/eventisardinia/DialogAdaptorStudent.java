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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
            TextView tvSubject = (TextView) listViewItem.findViewById(R.id.tv_type);
            // TextView tvDuedate=(TextView)listViewItem.findViewById(R.id.tv_desc);
            final TextView tvDescription = (TextView) listViewItem.findViewById(R.id.tv_class);
            final Button prenota = (Button) listViewItem.findViewById(R.id.prenota);

            final FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child("Eventi").child(alCustom.get(position).getSubjects()).child("prenotazioni").addValueEventListener(new ValueEventListener() {

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
                    if(user.getUid().equals("OjYzaljAaFW2r5wFL84IkMKSPbz1")){
                        prenota.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
            prenota.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    databaseReference.child("Eventi").child(alCustom.get(position).getSubjects()).child("prenotazioni").push().setValue(user.getUid());
                    HomeCollection.date_collection_arr=new ArrayList<HomeCollection>();
                }


            });

            //tvTitle.setText(alCustom.get(position).getTitles());
            tvSubject.setText(alCustom.get(position).getSubjects());
            //tvDuedate.setText("Due Datefrvrrvrv : "+alCustom.get(position).getDuedates());
            tvDescription.setText(alCustom.get(position).getDescripts());

        return  listViewItem;
    }




}

