package com.eventisardegna.shardana.eventisardinia;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public ViewHolder(final View itemView) {
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
        ImageView imageViewTv = mView.findViewById(R.id.immagine);

        tvSubject.setText(title);
        tvDescription.setText(subject);
        Picasso.get().load(image).into(imageViewTv);
    }

    private ViewHolder.ClickListener mClickListener;

    public interface ClickListener{
        void OnItemClick(View view, int position);
        void OnItemLongClick(View view, int position);
    }

    public void setOnClickListener(ViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }
}