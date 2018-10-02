package com.eventisardegna.shardana.eventisardinia;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<Dialogpojo> {

   /* public EventAdapter(Context context, ArrayList<Dialogpojo>evento){
        super(context, 0, evento);
    }
*/
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Activity context;
    private ArrayList<Dialogpojo> alCustom;
    private String sturl;

    public EventAdapter(Activity context, ArrayList<Dialogpojo> alCustom) {
        super(context, 0, alCustom);
        this.context = context;
        this.alCustom = alCustom;

    }

    @Override
    public int getCount() {
        return alCustom.size();

    }

    @Override
    public Dialogpojo getItem(int i) {
        return alCustom.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){


        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        LayoutInflater inflater = context.getLayoutInflater();
        final View listViewItem = inflater.inflate(R.layout.row_addapt, null, true);

        TextView tvSubject = (TextView) listViewItem.findViewById(R.id.tv_type);
        final TextView tvDescription = (TextView) listViewItem.findViewById(R.id.tv_class);
        final Button prenota = (Button) listViewItem.findViewById(R.id.prenota);

        final FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child("Eventi").child(alCustom.get(position).getSubjects()).child("prenotazioni").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get all of the children at this level.
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // shake hands with each of them.'
                for (DataSnapshot child : children) {
                    String name = (String) dataSnapshot.getValue().toString().trim();
                    if(name.contains(user.getUid())){
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

        tvSubject.setText(alCustom.get(position).getSubjects());
        tvDescription.setText(alCustom.get(position).getDescripts());

        return  listViewItem;
    }
}


