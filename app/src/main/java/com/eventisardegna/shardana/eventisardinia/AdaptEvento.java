package com.eventisardegna.shardana.eventisardinia;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaptEvento extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    protected   Context c;
    List<DatabaseEvento> eventi;
    View mView;

    private List<DatabaseEvento> itemList = null;

    public AdaptEvento(Context c, List<DatabaseEvento> eventi) {
        this.c = c;
        this.eventi = eventi;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mView= LayoutInflater.from(parent.getContext()).inflate(R.layout.addapt_evento,parent,false);
        RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(mView) {
            @Override
            public String toString() {
                return super.toString();
            }
        };
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //final  DatabaseEvento feedItems = itemList.get(position);
        TextView tvSubject = mView.findViewById(R.id.tv_type);
        TextView tvDescription = mView.findViewById(R.id.tv_class);
        ImageView ivImage = mView.findViewById(R.id.immagine);
        tvSubject.setText(eventi.get(position).getTitolo());
        tvDescription.setText(eventi.get(position).getLuogo());
        Picasso.get().load(eventi.get(position).getImmagine()).into(ivImage);
    }

    @Override
    public int getItemCount() {
        return eventi.size();
    }

    private AdaptEvento.ClickListener mClickListener;



    public interface ClickListener{

        void OnItemClick(View view, int position);

        void OnItemLongClick(View view, int position);

    }



    public void setOnClickListener(AdaptEvento.ClickListener clickListener){

        mClickListener = clickListener;

    }
}

