package com.eventisardegna.shardana.eventisardinia;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaptEvento extends RecyclerView.Adapter<AdaptEvento.ViewHolder>{
    protected   Context c;
    List<DatabaseEvento> eventi;
    View mView;

    public interface OnItemClickListener{

        void onItemClick(DatabaseEvento evento);

    }

    private List<DatabaseEvento> listaEventi;
    private OnItemClickListener listener;

    public AdaptEvento(Context c, List<DatabaseEvento> listaEventi, OnItemClickListener listener) {
        this.c = c;
        this.listaEventi = listaEventi;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.addapt_evento,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(listaEventi.get(position), listener);

    }

    @Override
    public int getItemCount() {
        return listaEventi.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titolo;
        private TextView descrizione;
        private ImageView immagine;

        public ViewHolder(View itemView) {
            super(itemView);
            titolo = itemView.findViewById(R.id.tv_type);
            descrizione = itemView.findViewById(R.id.tv_class);
            immagine = itemView.findViewById(R.id.immagine);

        }

        public void bind(final DatabaseEvento evento, final OnItemClickListener listener) {

            titolo.setText(evento.getTitolo());
            descrizione.setText(evento.getLuogo());
            Picasso.get().load(evento.getImmagine()).into(immagine);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(evento);
                }
            });
        }
    }


}

