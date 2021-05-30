package com.tuTurno.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuTurno.app.LaFuria.ActividadAdmin;
import com.tuTurno.app.LaFuria.ActividadPrincipal;

import java.util.Objects;

import models.cliente;

public class LoginActividad extends AppCompatActivity {

    private EditText txtUsuario;
    private EditText txtContrasena;
    private CheckBox checkadmin;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String gym;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        txtUsuario=findViewById(R.id.txtUsuario);
        txtContrasena=findViewById(R.id.txtContraseña);
        checkadmin = findViewById(R.id.checkBox);
        Button login = findViewById(R.id.botonlogin);
        Button registrar = findViewById(R.id.botonregistrar);
        TextView olvidecontra = findViewById(R.id.olvidecontra);

        iniciarFirebase();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrar();
            }
        });

        olvidecontra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OlvideContrasena();
            }
        });


    }

    private void OlvideContrasena() {
        if(isNetworkConnected()){
            startActivity(new Intent(this, RecuperoActivity.class));
        }else{
            Toast.makeText(this, "Compruebe su conexión a internet e intente de nuevo", Toast.LENGTH_SHORT).show();
        }

    }

    private void registrar() {
        if(isNetworkConnected()){
            startActivity(new Intent(this, RegistroActivity.class));
        }else{
            Toast.makeText(this, "Compruebe su conexión a internet e intente de nuevo", Toast.LENGTH_SHORT).show();
        }

    }

    private void login() {
        if(isNetworkConnected()){
            loginUsuario();
        }else{
            Toast.makeText(this, "Compruebe su conexión a internet e intente de nuevo", Toast.LENGTH_SHORT).show();
        }

    }

    private void loginUsuario(){
        final String usuario = txtUsuario.getText().toString().trim();
        String contrasena = txtContrasena.getText().toString().trim();

        if (!TextUtils.isEmpty(usuario) && !TextUtils.isEmpty(contrasena)){

            auth.signInWithEmailAndPassword(usuario, contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        finish();
                        Accion(usuario);
                    } else {
                        //Log.w("Error",task.exception)
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthInvalidCredentialsException | FirebaseAuthInvalidUserException e) {
                            Toast.makeText(getApplicationContext(), "Email o contraseña incorrecta, intente nuevamente", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

        }else{
            if(TextUtils.isEmpty(usuario)){
                txtUsuario.setError("Ingrese Email");
            }
            if(TextUtils.isEmpty(contrasena)){
                txtContrasena.setError("Ingrese contraseña");
            }
        }
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void Accion(final String user) {
        databaseReference.child("Clientes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot shot : snapshot.getChildren() ){
                    cliente c = shot.getValue(cliente.class);
                    assert c != null;

                    if(user.equals(Objects.requireNonNull(auth.getCurrentUser()).getEmail())){
                        gym = Objects.requireNonNull(shot.child("gym").getValue()).toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


       // if (gym.equals("La Furia Team")) {
            if (checkadmin.isChecked()) {
                if (user.equals("roygenoff@gmail.com") || user.equals("facundoca14@gmail.com") || user.equals("juanmanuelmartinez65.jmm@gmail.com")) {
                    startActivity(new Intent(this, ActividadAdmin.class));
                } else {
                    startActivity(new Intent(this, ActividadPrincipal.class));
                }
            } else {
                startActivity(new Intent(this, ActividadPrincipal.class));
            }
        //}

    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);
    }
}
