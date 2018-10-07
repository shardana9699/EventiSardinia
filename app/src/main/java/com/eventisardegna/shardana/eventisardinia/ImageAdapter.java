package com.eventisardegna.shardana.eventisardinia;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>{

    private Context mContext;
    private List<Dialogpojo> mEventi;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    public ImageAdapter(Context context, List<Dialogpojo> eventi){
        mContext = context;
        mEventi = eventi;
    }
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        View v = LayoutInflater.from(mContext).inflate(R.layout.lista_eventi, parent, false);
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Dialogpojo evento = mEventi.get(position);

        holder.tvSubject.setText(evento.getSubjects());
        holder.tvDescription.setText(evento.getDescripts());
        Picasso.get().load(evento.getImage()).into(holder.imma);

    }

    @Override
    public int getItemCount() {
        return mEventi.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public ImageView imma;
        public TextView tvSubject;
        public TextView tvDescription;

        public ImageViewHolder(View itemView) {
            super(itemView);
            tvSubject = (TextView) itemView.findViewById(R.id.tv_type);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_class);
            imma = (ImageView) itemView.findViewById(R.id.immagine);


        }
    }
}
