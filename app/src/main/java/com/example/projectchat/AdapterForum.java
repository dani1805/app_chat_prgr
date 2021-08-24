package com.example.projectchat;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectchat.Models.Message;
import com.example.projectchat.Utils.CustonItemClick;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterForum extends RecyclerView.Adapter<AdapterForum.HolderForum> { // Adapter para sincronizar los mensajes del foro

private List<Message> messages;
private final Context context;
private final CustonItemClick listener;

private FirebaseAuth mAuth;


public AdapterForum(List<Message> messages, Context context, CustonItemClick listener, FirebaseAuth mAuth) {
        this.messages = messages;
        this.context = context;
        this.listener = listener;
        this.mAuth = mAuth;
        }

static class HolderForum extends RecyclerView.ViewHolder {

    private final TextView tvMessage;
    private final TextView tvDate;
    private final LinearLayout linearRight;
    private FirebaseAuth mAuth;

    public HolderForum(@NonNull View v) {
        super(v);

        tvMessage = v.findViewById(R.id.tvMessage);
        tvDate = v.findViewById(R.id.tvDate);
        linearRight = v.findViewById(R.id.linearRight);

    }
}

    @NonNull
    @Override
    public AdapterForum.HolderForum onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.holdermessage, parent, false);
        return new HolderForum(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterForum.HolderForum holder, int position) {

        Message message = messages.get(position);

        holder.tvMessage.setText(message.getName().toUpperCase() + ": " + message.getMessage());
        String date = new SimpleDateFormat("yyyy-MM--hh HH:mm").format(new Date()); // Formatear la fecha a mi antojo
        holder.tvDate.setText(date);

        if (mAuth.getCurrentUser().getDisplayName().equals(message.getName())) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1.0f;
            params.gravity = Gravity.LEFT;

            holder.linearRight.setLayoutParams(params);


        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public void setList(List<Message> list) {
        this.messages = list;
        notifyDataSetChanged();
    }

    public void add(Message item) {
        messages.add(item);
        notifyDataSetChanged();
    }


}

