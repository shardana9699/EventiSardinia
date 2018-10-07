package com.eventisardegna.shardana.eventisardinia;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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
    private ImageView imageView;

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

    /*@Override
    public View getView(final int position, View convertView, ViewGroup parent){


        /*firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        LayoutInflater inflater = context.getLayoutInflater();
        final View listViewItem = inflater.inflate(R.layout.lista_eventi, null, true);

        TextView tvSubject = (TextView) listViewItem.findViewById(R.id.tv_type);
        final TextView tvDescription = (TextView) listViewItem.findViewById(R.id.tv_class);
        final Button prenota = (Button) listViewItem.findViewById(R.id.prenota);
        imageView = imageView.findViewById(R.id.sfondo);
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        Picasso.with(context)
                .load(alCustom.getImage())
                .fit()
                .centerCrop()
                .into(holder.imageView);


        tvSubject.setText(alCustom.get(position).getSubjects());
        tvDescription.setText(alCustom.get(position).getDescripts());

        return  listViewItem;
    }*/
}


