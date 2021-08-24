package com.example.projectchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.projectchat.Adapters.AdapterTopics;
import com.example.projectchat.Models.Topic;
import com.example.projectchat.Utils.CustonItemClick;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton faAdd;
    private Toolbar toolbar;
    private RecyclerView rvTopics;
    private List<Topic> topics = new ArrayList<>();
    private AdapterTopics adapterTopics;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference topicRef;

    private static final String CHANNEL_ID = "3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance(); // Instancia auntenticación
        db = FirebaseFirestore.getInstance(); // Instancia bbdd

        topicRef = db.collection("topics"); // Hacer referencia a la colección "topics"

        faAdd = findViewById(R.id.faAdd);
        rvTopics = findViewById(R.id.rvTopics);
        toolbar = findViewById(R.id.tbMenu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Welcome " + mAuth.getCurrentUser().getDisplayName());
        toolbar.setTitleTextColor(Color.WHITE);

        setUpView();
        createNotificationChannel();

        faAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomAlertDialog(); //Mostrar el AlertDialog propio
            }
        });

    }

    public void setUpView()  { // Metodo para limpiar codigo y simplificarlo que realiza la funcionalidad del adapter, el diseño de este, hace referencia al user
        // y sirve para añadir los temas al recyclerView.

        rvTopics.setHasFixedSize(true);
        topics = new ArrayList<>();

        LinearLayoutManager llm = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvTopics.setLayoutManager(llm);

        topicRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Topic topic = documentSnapshot.toObject(Topic.class);
                        topics.add(topic); // Añades el tema
                        Log.i("Tag", topic.getName());
                    }

                    adapterTopics.setList(topics); // Actualizas los temas
                }
            }
        });

        adapterTopics = new AdapterTopics(topics, MainActivity.this, new CustonItemClick() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, ForumActivity.class);
                intent.putExtra("topic", topics.get(position).getName());
                startActivity(intent); // Pasas los temas a la actividad del chat

            }

            @Override
            public void onLongItemClick(int position) {

            }
        });

        rvTopics.setAdapter(adapterTopics);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);


        return super.onCreateOptionsMenu(menu);
    }

    public void showCustomAlertDialog() { //AlertDialog creado donde le paso el titulo del tema y si el usuario acepta se añade a la base de datos el tema en cuestion
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.customalertdialog, null);

        EditText etTitle = v.findViewById(R.id.etTitle);

        builder.setView(v);
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            // Cuando el usuario acepta el AlertDialog
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String title = etTitle.getText().toString(); // Recuperas el titulo o nombre del tema

                Topic topicDb = new Topic(title); // Instancias un objeto de FirebaseFirestore
                
                topicRef.document(title).set(topicDb);

                adapterTopics.add(topicDb); // El metodo add lo he utilizado porque no he sabido implementar el snapsshots.... y he creado un metodo para añadir directamente en este caso el tema

            }
        });

        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channel_name = "Notificaciones de prueba";//nombre canal
            String channel_description = "Probando notificaciones en una App de ejemplo";//descripción
            int importance = NotificationManager.IMPORTANCE_DEFAULT;//Prioridad o importancia de la notificación
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channel_name, importance); //construye el canal
            channel.setDescription(channel_description); // añade una descripción
            NotificationManager notificationManager = getSystemService(NotificationManager.class); //Se obtiene el gestor de notificaciones
            notificationManager.createNotificationChannel(channel); //se crea el canal de notificaciones
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                Log.i("Tag","Cerrar sesion");
                mAuth.signOut(); // El sign out sirve para cerrar sesion

                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser == null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                return true;

            case R.id.settings:

                Intent intent = new Intent(MainActivity.this, PrefActivity.class);
                startActivity(intent);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}