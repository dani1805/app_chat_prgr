package com.example.projectchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnAccept;
    private Button btnRegister;

    private FirebaseAuth mAuth;
    private FirebaseMessaging messaging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnAccept = findViewById(R.id.btnAccept);
        btnRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance(); // Instancia auntenticación
        messaging = FirebaseMessaging.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recuperas el email y la contraseña
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                Boolean validate = validateForm(email, password);

                if (validate) {

                    //este método realiza el login con el usuario y contraseña enviados
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (user.isEmailVerified()) {

                                    messaging.getToken().addOnCompleteListener(new OnCompleteListener<String>() { //Recuperar el token
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            if (task.isSuccessful()) {
                                                String token = task.getResult();

                                                SharedPreferences preferences = LoginActivity.this.getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("token", token);
                                                editor.apply(); // Guardar el token cuando el usuario hace login. Así se guarda a nivel global en toda la aplicacion
                                                Log.i("token", token);
                                            }

                                        }
                                    });


                                   Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                   startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Email no verificado. No puedes acceder", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(LoginActivity.this, "Login incorrecto. Prueba de nuevo", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null && currentUser.isEmailVerified()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent); //Cuando estas logueado y el usuario es distinto de nulo y el email esta verficado accedes a la aplicacion directamente tras un solo login inicial
        }
    }

    private boolean validateForm (String email, String password) { // Validacion si los campos del formularios estan vacios
        boolean valid = true;

        if (TextUtils.isEmpty(password)) {
            etEmail.setError("Required.");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            etPassword.setError("Required.");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        return valid;

    }

}