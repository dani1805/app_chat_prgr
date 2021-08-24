package com.example.projectchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class PrefActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);

        // Actividad que sirve de contenedor para implementar el Fragment donde se encuentra cada Preferencia

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settingsPref, new PrefFragmentActivity())
                .commit();

        mAuth = FirebaseAuth.getInstance(); // Instancia auntenticación

        toolbar = findViewById(R.id.tbPref);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ajustes"); // Poner el nombre del usuario en todas las pantallas
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Mostrar el boton de atras
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Habilitar el boton de atras
            }
        });


    }

}

