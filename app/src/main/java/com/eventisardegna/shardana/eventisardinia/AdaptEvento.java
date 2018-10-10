package com.eventisardegna.shardana.eventisardinia;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class AdaptEvento extends RecyclerView.ViewHolder {

    View mView;

    public AdaptEvento(final View itemView) {
        super(itemView);

        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.OnItemClick(v, getAdapterPosition());
            }
        });
        /*itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.OnItemLongClick(v, getAdapterPosition());
                return true;
            }
        });*/
    }

    public void setDetails(final Context ctx, String title, String subject, String image){

        TextView tvSubject = mView.findViewById(R.id.tv_type);
        TextView tvDescription = mView.findViewById(R.id.tv_class);
        ImageView ivImage = mView.findViewById(R.id.immagine);

        tvSubject.setText(title);
        tvDescription.setText(subject);
        Picasso.get().load(image).into(ivImage);
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