package com.example.projectchat.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectchat.Models.Message;
import com.example.projectchat.R;
import com.example.projectchat.Utils.CustonItemClick;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterForum extends RecyclerView.Adapter<AdapterForum.HolderForum> implements Filterable { // Adapter para sincronizar los mensajes del foro, la hago filterable para poder implementar el buscador

private List<Message> messages;
private List<Message> messagesCopy; // Elemento que sirve luego para igualar el List
private final Context context;
private final CustonItemClick listener;

private FirebaseAuth mAuth;


public AdapterForum(List<Message> messages, Context context, CustonItemClick listener, FirebaseAuth mAuth) {
        this.messages = messages;
        this.context = context;
        this.listener = listener;
        this.mAuth = mAuth;
}

    @Override // Filtro para implementar el Searchview
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String text = constraint.toString();

                if (text.isEmpty()) {
                    messages = messagesCopy;
                } else {
                    List<Message> listAux = new ArrayList<>();

                    for (Message message : messagesCopy) {

                        String filterMessage = message.getMessage().toLowerCase();
                        String textSearchMinus = text.toLowerCase();

                        if (filterMessage.contains(textSearchMinus)) {
                            listAux.add(message);
                        }
                    }
                    messages = listAux;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = messages;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                messages = (List<Message>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
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

    @SuppressLint("ResourceType")
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
            holder.linearRight.setBackgroundResource(R.color.sendEmail);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongItemClick(position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public void setList(List<Message> list) {
        this.messagesCopy = list;
        this.messages = list;
        notifyDataSetChanged();
    }

    public void add(Message item) {
        messages.add(item);
        notifyDataSetChanged();
    }


}

