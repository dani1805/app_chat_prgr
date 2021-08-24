package com.example.projectchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectchat.Models.Topic;
import com.example.projectchat.Utils.CustonItemClick;

import java.util.List;

public class AdapterTopics extends RecyclerView.Adapter<AdapterTopics.HolderTopic> { // Adapter para sincronizar los temas del foro

    private List<Topic> topics;
    private final Context context;
    private final CustonItemClick listener;

    public AdapterTopics(List<Topic> topics, Context context, CustonItemClick listener) {
        this.topics = topics;
        this.context = context;
        this.listener = listener;
    }

    static class HolderTopic extends RecyclerView.ViewHolder {

        private final TextView tvTopics;

        public HolderTopic(@NonNull View v) {
            super(v);

            tvTopics = v.findViewById(R.id.tvTopics);

        }
    }

    @NonNull
    @Override
    public AdapterTopics.HolderTopic onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_holder, parent, false);
        return new HolderTopic(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTopics.HolderTopic holder, int position) {

        Topic topic = topics.get(position);

        holder.tvTopics.setText(topic.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }


    public void setList(List<Topic> list) {
        this.topics = list;
        notifyDataSetChanged();
    }

    public void add(Topic item) {
        topics.add(item);
        notifyDataSetChanged();
    }

}


