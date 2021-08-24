package com.example.projectchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.example.projectchat.Adapters.AdapterForum;
import com.example.projectchat.Models.Message;
import com.example.projectchat.Models.MyNotification;
import com.example.projectchat.Models.NotificationData;
import com.example.projectchat.Models.Token;
import com.example.projectchat.WebService.WebServiceClient;
import com.example.projectchat.Utils.CustonItemClick;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForumActivity extends AppCompatActivity {

    private EditText etMessage;
    private Button btnSend;
    private Toolbar toolbar;
    private RecyclerView rvForum;
    private List<Message> messages = new ArrayList<>();
    private AdapterForum adapterForum;

    private String topic;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference topicRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        mAuth = FirebaseAuth.getInstance(); // Instancia auntenticación
        db = FirebaseFirestore.getInstance(); // Instancia bbdd

        topicRef = db.collection("topics"); // Hacer referencia a la colección "topics"

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        rvForum = findViewById(R.id.rvForum);
        toolbar = findViewById(R.id.tbForum);

        topic = getIntent().getStringExtra("topic"); // Recoges los temas

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar el boton de atras
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Habilitar el boton de atras
            }
        });
        setUpView();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = etMessage.getText().toString(); // Recuperar el mensaje
                String name = mAuth.getCurrentUser().getDisplayName(); // Recuperar y mostrar en cada pantalla el nombre del usuario
                Date date = new Date();

                Message messageDb = new Message(name,date,message); // Objeto de FirebaseFirestore
                // Añadir un nuevo mensaje a la coleccion topics
                topicRef.document(topic).collection("messages").add(messageDb).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        adapterForum.add(messageDb); //Añadir el mensaje y que se muestre de manera visual
                        etMessage.setText(""); // Vaciar el editText para cuando envies el mensaje

                        SharedPreferences preferences = ForumActivity.this.getSharedPreferences("myPreferences", Context.MODE_PRIVATE);

                        String token = preferences.getString("token", "");
                        if (!token.isEmpty()) {
                            Token tokenDb = new Token(token);
                            topicRef.document(topic).collection("tokens").add(tokenDb); // Añadir el token y llamar al metodo sendNotification
                            sendNotification(name, message);

                        }
                    }
                });
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menusearch, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapterForum.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterForum.getFilter().filter(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void setUpView() { // Metodo para limpiar codigo y simplificarlo que realiza la funcionalidad del adapter, el diseño de este, hace referencia al user
        // y sirve para añadir los temas al recyclerView.

        rvForum.setHasFixedSize(true);
        messages = new ArrayList<>();

        LinearLayoutManager llm = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvForum.setLayoutManager(llm);

        topicRef.document(topic).collection("messages").orderBy("date").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Message message = documentSnapshot.toObject(Message.class);
                        messages.add(message); // Añadir el mensaje y ordenarlo segun la fecha
                        Log.i("Tag", message.getName());
                    }

                    adapterForum.setList(messages); // Metodo para actualizar los mensajes
                }
            }
        });

        adapterForum = new AdapterForum(messages, this, new CustonItemClick() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onLongItemClick(int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ForumActivity.this);
                builder.setMessage("¿Deseas borrar algún mensaje?");
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String message = messages.get(position).getMessage();
                        String name = messages.get(position).getName();
                        Date date = messages.get(position).getDate(); // Recupero el mensaje,nombre y fecha para poder borrar los mensajes.

                        messages.remove(position);
                        adapterForum.notifyDataSetChanged();

                        // He igualado tanto como en nombre, mensaje o en fecha para que pueda filtrar por cada uno de ellos puesto que lo mismo en un chat
                        // puede haber una cantidad determinada de una palabra por ejemplo
                        topicRef.document(topic).collection("messages").whereEqualTo("message", message).whereEqualTo("name", name). whereEqualTo("date", date).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        topicRef.document(topic).collection("messages").document(documentSnapshot.getId()).delete();
                                        // Borra en la base de datos el elemento(el mensaje) añadido
                                    }
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }, mAuth);

        rvForum.setAdapter(adapterForum);
    }

    private void sendNotification(String title, String message) {

        topicRef.document(topic).collection("tokens").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> tokens = new ArrayList<>();

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    Map<String, Object> map = documentSnapshot.getData();
                    tokens.add((String) map.get("token"));

                }
                sendNotificationFinal(title, message, tokens);
            }
        });
    }

    private void sendNotificationFinal(String title, String message, List<String> tokens) {

        String date = new SimpleDateFormat("yyyy-MM--hh HH:mm").format(new Date()); // Formatear la fecha a mi antojo

        NotificationData notificationData = new NotificationData(message, title, date);
        MyNotification myNotification = new MyNotification(tokens, notificationData);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebServiceClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WebServiceClient webServiceClient = retrofit.create(WebServiceClient.class);
        Call<Object> call = webServiceClient.sendNotification(myNotification);
        call.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Log.i("notif", "Notificacion enviada");
                    Log.i("text", response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.i("notif", t.getMessage());
            }
        });


    }

}