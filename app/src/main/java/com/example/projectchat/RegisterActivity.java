package com.example.projectchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etRepeatPassword; //EditText para repetir la contraseña
    private Button buttonRegister;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance(); // Instancia de autenticacion

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmailRegister);
        etPassword = findViewById(R.id.etPasswordRegister);
        etRepeatPassword = findViewById(R.id.etRepeatPassword);
        buttonRegister = findViewById(R.id.buttonRegister);


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Recuperas el nombre,email,contraseña y para repetir contraseña

                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String repeatPassword = etRepeatPassword.getText().toString();

                Boolean validate = validateForm(name, email, password, repeatPassword);

                if (validate) {

                    // este método crea un usuario a partir de un email y un password
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name).build();

                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null)
                                    user.updateProfile(profileUpdates);
                                sendEmail();


                            } else {
                                Toast.makeText(RegisterActivity.this, "Fallo al registrar al usuario", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });

    }

    private boolean validateForm(String name, String email, String password, String repeatPassword) {
        boolean valid = true;

        if (TextUtils.isEmpty(name)) {
            etName.setError("Required.");
            valid = false;
        } else {
            etName.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Required.");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Required.");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        if (TextUtils.isEmpty(repeatPassword)) {
            etRepeatPassword.setError("Required.");
            valid = false;
        } else {
            etRepeatPassword.setError(null);
        }

        // Si contraseña es distinta de repetir contraseña se muestra un error. Si no pues el error pasa a ser nulo

        if (!password.equals(repeatPassword)) {
            etPassword.setError("Contraseña incorrecta");
            etRepeatPassword.setError("Contraseña incorrecta");
            valid = false;
        } else {
            etPassword.setError(null);
            etRepeatPassword.setError(null);

        }

        return valid;

    }

    private void sendEmail() {
        // Enviar el email de verificacion para poder hacer login
        FirebaseUser mAuser = mAuth.getCurrentUser();
        if (mAuser != null) {
            mAuser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "Fallo al enviar el email de verificación", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}