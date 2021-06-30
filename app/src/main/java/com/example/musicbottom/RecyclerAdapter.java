package com.example.musicbottom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private Context ctx;
    private ArrayList<Songs> songList;
    private onItemListClick onItemClick;


    RecyclerAdapter(Context ctx , ArrayList<Songs> songList ,onItemListClick onItemClick){
        this.ctx = ctx;
        this.songList = songList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.music_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {
        holder.songName.setText(songList.get(position).getTitle());
        holder.cardView.setBackground(holder.cardView.getResources().getDrawable(R.drawable.card_background));
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MaterialTextView songName;
        MaterialCardView cardView;
        View view;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.songName);
            cardView = itemView.findViewById(R.id.songContainer);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
        onItemClick.onClickListener(songList.get(getAdapterPosition()),getAdapterPosition());
        }
    }

    //making interface to get clicked item in main activity
    public interface onItemListClick {
        void onClickListener(Songs song , int position);
    }
}
